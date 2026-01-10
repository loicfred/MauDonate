package mau.donate.config;

import mau.donate.objects.User;
import mau.donate.service.OAuth2Service;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;
import java.util.Collections;

@Configuration
public class SecurityConfig {

    private final DataSource dataSource;
    private final OAuth2Service oAuth2UserService;

    public SecurityConfig(DataSource dataSource, OAuth2Service oAuth2UserService) {
        this.dataSource = dataSource;
        this.oAuth2UserService = oAuth2UserService;
    }

    // ----------------------
    // Password Encoder
    // ----------------------
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ----------------------
    // UserDetailsService
    // ----------------------
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = User.getByEmail(username);
            if (user == null) throw new UsernameNotFoundException("User not found");
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .disabled(!user.isEnabled())
                    .authorities(Collections.emptyList())
                    .build();
        };
    }

    // ----------------------
    // Authentication Handlers
    // ----------------------
    @Bean
    public AuthenticationSuccessHandler formLoginSuccessHandler() {
        return (request, response, authentication) -> {
            System.out.println(authentication.getName() + " logged in (form)!");
            response.sendRedirect("/home");
        };
    }

    @Bean
    public AuthenticationSuccessHandler oauth2LoginSuccessHandler(RememberMeServices rememberMeServices) {
        return (request, response, authentication) -> {
            System.out.println(authentication.getName() + " logged in (OAuth2)!");
            // Trigger remember-me if present
            rememberMeServices.loginSuccess(request, response, authentication);
            response.sendRedirect("/home");
        };
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            if (authentication != null) {
                System.out.println(authentication.getName() + " logged out!");
            }
            response.sendRedirect("/accounts/login?logout");
        };
    }

    // ----------------------
    // Persistent Token Repository (Remember-Me)
    // ----------------------
    @Bean
    public RememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
        PersistentTokenBasedRememberMeServices services =
                new PersistentTokenBasedRememberMeServices(
                        "my-remember-me-key",  // key used to identify cookies
                        userDetailsService,
                        persistentTokenRepository()
                );
        services.setAlwaysRemember(false); // optional, can respect checkbox
        services.setCookieName("remember-me");
        services.setTokenValiditySeconds(1209600); // 14 days
        return services;
    }
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        // repo.setCreateTableOnStartup(true); // Uncomment first time to create table
        return repo;
    }

    // ----------------------
    // Security Filter Chain
    // ----------------------
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, RememberMeServices rememberMeServices) {
        String[] publicPaths = {
                "/", "/home", "/accounts/**", "/error",
                "/service-worker.js", "/manifest.json", "/offline",
                "/css/**", "/js/**", "/img/**"
        };

        return http
                // ----------------------
                // Authorization
                // ----------------------
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(publicPaths).permitAll()
                        .anyRequest().authenticated()
                )

                // ----------------------
                // CSRF
                // ----------------------
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/paypal/**")
                )

                // ----------------------
                // Form Login
                // ----------------------
                .formLogin(form -> form
                        .loginPage("/accounts/login")
                        .loginProcessingUrl("/accounts/login")
                        .successHandler(formLoginSuccessHandler())
                        .failureUrl("/accounts/login?error")
                        .permitAll()
                )

                // ----------------------
                // Remember-Me
                // ----------------------
                .rememberMe(rememberMe -> rememberMe
                        .tokenRepository(persistentTokenRepository())
                        .tokenValiditySeconds(1209600) // 14 days
                        .userDetailsService(userDetailsService())
                )

                // ----------------------
                // Logout
                // ----------------------
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler())
                        .permitAll()
                )

                // ----------------------
                // OAuth2 Login
                // ----------------------
                .oauth2Login(oauth -> oauth
                        .loginPage("/accounts/login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                        .successHandler(oauth2LoginSuccessHandler(rememberMeServices))
                )

                .build();
    }
}

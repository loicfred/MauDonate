package mau.donate.config;

import mau.donate.objects.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;
import java.util.Collections;

@Configuration
public class SecurityConfig {

    private final DataSource dataSource;

    public SecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public UserDetailsService userDetailsService() throws UsernameNotFoundException {
        return username -> {
            User user = User.getByUsername(username);
            if (user == null) user = User.getByEmail(username);
            if (user == null) throw new UsernameNotFoundException("User not found");
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername()).password(user.getPassword())
                    .disabled(!user.isEnabled()).authorities(Collections.emptyList())
                    .build();
        };
    }
    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return (request, response, authentication) -> {
            System.out.println(authentication.getName() + " logged in!");
            response.sendRedirect("/home");
        };
    }
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            System.out.println(authentication.getName() + " logged out!");
            response.sendRedirect("/accounts/login?logout");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/home"
                                , "/accounts/signup", "/accounts/login"
                                , "/post/accounts/register", "/post/accounts/login"
                                , "/accounts/verify", "/assets/**").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/paypal/**")
                )
                .formLogin(form -> form
                        .loginPage("/accounts/login").loginProcessingUrl("/post/accounts/login")
                        .successHandler(loginSuccessHandler()).defaultSuccessUrl("/home")
                        .failureUrl("/accounts/login?error")
                        .permitAll()
                )
                .rememberMe(rememberMe -> rememberMe
                        .tokenRepository(persistentTokenRepository(dataSource))
                        .tokenValiditySeconds(1209600)
                        .userDetailsService(userDetailsService())
                )
                .userDetailsService(userDetailsService())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler()).logoutSuccessUrl("/accounts/login?logout")
                        .permitAll()
                ).build();
    }
    @Bean
    public PersistentTokenRepository persistentTokenRepository(DataSource dataSource) {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        //repo.setCreateTableOnStartup(true);
        return repo;
    }

}
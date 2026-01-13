package mau.donate.config;

import mau.donate.service.database.DatabaseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.client.RestClient;

import javax.sql.DataSource;

@Configuration
public class AppConfig {
    public static DatabaseService dbService;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.driver-class-name}")
    private String dbDriver;

    public ApplicationContext context;
    public AppConfig(ApplicationContext context) {
        this.context = context;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl(dbUrl);
        ds.setUsername(dbUsername);
        ds.setPassword(dbPassword);
        ds.setDriverClassName(dbDriver);
        return ds;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource ds) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
        DatabaseService.setJdbcTemplate(jdbcTemplate);
        return jdbcTemplate;
    }

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void setStaticReference() {
        dbService = context.getBean(DatabaseService.class);
        DatabaseService.setJdbcTemplate(context.getBean(JdbcTemplate.class));
    }
}

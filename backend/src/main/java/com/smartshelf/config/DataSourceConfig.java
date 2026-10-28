package com.smartshelf.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${DATABASE_URL:#{null}}")
    private String databaseUrl;

    @Value("${spring.datasource.url:#{null}}")
    private String springDatasourceUrl;

    @Value("${spring.datasource.username:root}")
    private String username;

    @Value("${spring.datasource.password:sumit@2006}")
    private String password;

    @Bean
    @Primary
    public DataSource dataSource() {
        // 1. If Render's native DATABASE_URL is present (e.g. postgres://user:pass@host:5432/db)
        if (databaseUrl != null && !databaseUrl.isBlank()) {
            try {
                if (databaseUrl.startsWith("postgres://") || databaseUrl.startsWith("postgresql://")) {
                    log.info("Detected Render native DATABASE_URL environment variable.");
                    URI dbUri = new URI(databaseUrl);
                    String userInfo = dbUri.getUserInfo();
                    String dbUser = username;
                    String dbPass = password;
                    if (userInfo != null && userInfo.contains(":")) {
                        String[] parts = userInfo.split(":", 2);
                        dbUser = parts[0];
                        dbPass = parts[1];
                    }
                    int port = dbUri.getPort() == -1 ? 5432 : dbUri.getPort();
                    String path = dbUri.getPath();
                    if (path != null && path.startsWith("/")) {
                        path = path.substring(1);
                    }
                    String jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + port + "/" + path;
                    log.info("Connecting to Render PostgreSQL at: {}:{}", dbUri.getHost(), port);
                    return DataSourceBuilder.create()
                            .driverClassName("org.postgresql.Driver")
                            .url(jdbcUrl)
                            .username(dbUser)
                            .password(dbPass)
                            .build();
                }
            } catch (Exception e) {
                log.warn("Failed to parse DATABASE_URL as URI, falling back to standard datasource configuration: {}", e.getMessage());
            }
        }

        // 2. Standard Spring Datasource URL (MySQL, PostgreSQL, or H2)
        String url = (springDatasourceUrl != null && !springDatasourceUrl.isBlank())
                ? springDatasourceUrl
                : "jdbc:mysql://localhost:3306/grocery_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        log.info("Initializing DataSource with URL: {}", url);
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .build();
    }
}

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

    @Value("${SPRING_DATASOURCE_URL:#{null}}")
    private String springDatasourceUrl;

    @Value("${spring.datasource.url:#{null}}")
    private String propertiesUrl;

    @Value("${spring.datasource.username:sumit}")
    private String username;

    @Value("${spring.datasource.password:zexNLulHknNQrD0mLplvY0TQzezaLz3o}")
    private String password;

    @Bean
    @Primary
    public DataSource dataSource() {
        // Priority 1: DATABASE_URL (Render native variable)
        if (databaseUrl != null && !databaseUrl.isBlank()) {
            DataSource ds = buildDataSource(databaseUrl, username, password);
            if (ds != null) return ds;
        }

        // Priority 2: SPRING_DATASOURCE_URL (Custom env variable)
        if (springDatasourceUrl != null && !springDatasourceUrl.isBlank()) {
            DataSource ds = buildDataSource(springDatasourceUrl, username, password);
            if (ds != null) return ds;
        }

        // Priority 3: spring.datasource.url from application.properties
        if (propertiesUrl != null && !propertiesUrl.isBlank()) {
            DataSource ds = buildDataSource(propertiesUrl, username, password);
            if (ds != null) return ds;
        }

        // Ultimate Fallback: Embedded H2
        log.warn("No database URL resolved. Falling back to embedded H2 database.");
        return DataSourceBuilder.create()
                .driverClassName("org.h2.Driver")
                .url("jdbc:h2:mem:grocery_db;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE")
                .username("sa")
                .password("")
                .build();
    }

    private DataSource buildDataSource(String rawUrl, String defaultUser, String defaultPass) {
        String url = rawUrl.trim();
        String user = defaultUser;
        String pass = defaultPass;
        String driver = null;

        if (url.startsWith("postgres://") || url.startsWith("postgresql://")) {
            try {
                URI uri = new URI(url);
                String userInfo = uri.getUserInfo();
                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    user = parts[0];
                    pass = parts[1];
                }
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String path = uri.getPath();
                if (path != null && path.startsWith("/")) {
                    path = path.substring(1);
                }
                String host = uri.getHost();
                url = "jdbc:postgresql://" + host + ":" + port + "/" + path;
                if (!url.contains("sslmode")) {
                    url += "?sslmode=require";
                }
                driver = "org.postgresql.Driver";
            } catch (Exception e) {
                url = url.replaceFirst("^postgres(ql)?://", "jdbc:postgresql://");
                if (!url.contains("sslmode")) {
                    url += (url.contains("?") ? "&" : "?") + "sslmode=require";
                }
                driver = "org.postgresql.Driver";
            }
        } else if (url.startsWith("jdbc:postgresql:")) {
            driver = "org.postgresql.Driver";
            if (!url.contains("sslmode")) {
                url += (url.contains("?") ? "&" : "?") + "sslmode=require";
            }
        } else if (url.startsWith("jdbc:mysql:")) {
            driver = "com.mysql.cj.jdbc.Driver";
        } else if (url.startsWith("jdbc:h2:")) {
            driver = "org.h2.Driver";
        }

        log.info("Configured DataSource: {} (user: {})", url.replaceAll(":[^/@]+@", ":***@"), user);

        var builder = DataSourceBuilder.create()
                .url(url)
                .username(user)
                .password(pass);

        if (driver != null) {
            builder.driverClassName(driver);
        }

        return builder.build();
    }
}

package com.smartshelf.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.InetSocketAddress;
import java.net.Socket;
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
        // Case 1: Render native PostgreSQL DATABASE_URL
        if (databaseUrl != null && !databaseUrl.isBlank()) {
            try {
                if (databaseUrl.startsWith("postgres://") || databaseUrl.startsWith("postgresql://")) {
                    log.info("Detected Render native PostgreSQL DATABASE_URL.");
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
                    String sslParam = (dbUri.getQuery() != null && dbUri.getQuery().contains("sslmode"))
                            ? ""
                            : "?sslmode=require";
                    String jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + port + "/" + path + sslParam;
                    log.info("Connecting to Render PostgreSQL at: {}:{}", dbUri.getHost(), port);
                    return DataSourceBuilder.create()
                            .driverClassName("org.postgresql.Driver")
                            .url(jdbcUrl)
                            .username(dbUser)
                            .password(dbPass)
                            .build();
                }
            } catch (Exception e) {
                log.warn("Failed to parse DATABASE_URL: {}", e.getMessage());
            }
        }

        // Case 2: Custom SPRING_DATASOURCE_URL explicitly supplied (e.g. Cloud MySQL / Aiven / TiDB)
        if (springDatasourceUrl != null && !springDatasourceUrl.isBlank()) {
            log.info("Connecting to configured SPRING_DATASOURCE_URL: {}", springDatasourceUrl);
            return DataSourceBuilder.create()
                    .url(springDatasourceUrl)
                    .username(username)
                    .password(password)
                    .build();
        }

        // Case 3: Local MySQL on localhost:3306 (if port is open)
        if (isPortOpen("localhost", 3306)) {
            String defaultMysqlUrl = "jdbc:mysql://localhost:3306/grocery_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            log.info("Local MySQL 3306 detected. Connecting to: {}", defaultMysqlUrl);
            return DataSourceBuilder.create()
                    .driverClassName("com.mysql.cj.jdbc.Driver")
                    .url(defaultMysqlUrl)
                    .username(username)
                    .password(password)
                    .build();
        }

        // Case 4: Universal Cloud Self-Healing Fallback — Embedded H2 in MySQL Compatibility Mode
        log.warn("No external database URL configured and local MySQL 3306 unavailable. Starting embedded H2 (MySQL mode) with auto-seeding.");
        String h2Url = "jdbc:h2:mem:grocery_db;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE";
        return DataSourceBuilder.create()
                .driverClassName("org.h2.Driver")
                .url(h2Url)
                .username("sa")
                .password("")
                .build();
    }

    private boolean isPortOpen(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 1000);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

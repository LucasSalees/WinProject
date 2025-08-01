package com.project.system.config;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfiguration {

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .driverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
                .url("jdbc:sqlserver://192.168.9.107:1433;databaseName=teste_WinProject;encrypt=true;trustServerCertificate=true;")
                .username("input")
                .password("input@@1234@@")
                .build();
    }
}


package com.lanqespacio.backendhorarios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class SQLiteSourceConfig {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite:/root/backendhorarios/src/main/resources/horarios_db.db");
        dataSource.setUsername(""); // No se necesita usuario para SQLite
        dataSource.setPassword(""); // No se necesita contraseña para SQLite
        return dataSource;
    }
}

package bikram.businessmanager.utils;


import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceConfig {
    private static final HikariDataSource datasource;
    static {
        com.zaxxer.hikari.HikariConfig config = new com.zaxxer.hikari.HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:company.db?journal_mode=WAL");
        config.setDriverClassName("org.sqlite.JDBC");
        config.setMaximumPoolSize(1);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(30000);
        config.setMaxLifetime(1800000);
        config.setAutoCommit(false);

        config.addDataSourceProperty("busy_timeout", "5000");
        datasource = new HikariDataSource(config);
    }
    public static DataSource getDataSource(){return datasource;}
}

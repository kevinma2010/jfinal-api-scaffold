package com.mlongbo.jfinal.plugin;

import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.IDataSourceProvider;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * *
 * @author malongbo
 */
public class HikariCPPlugin implements IPlugin,IDataSourceProvider {

    private String jdbcUrl;
    private String user;
    private String password;
    private String driverClass = "com.mysql.jdbc.Driver";
    private int maxPoolSize = 10;

    private HikariDataSource dataSource;
    
    public HikariCPPlugin(String jdbcUrl, String user, String password) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
    }

    public HikariCPPlugin(String jdbcUrl, String user, String password, String driverClass, int maxPoolSize) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
        this.driverClass = driverClass;
        this.maxPoolSize = maxPoolSize;
    }
    
    @Override
    public boolean start() {
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(maxPoolSize);
//        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.setDriverClassName(driverClass);
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(user);
        config.setPassword(password);
        
        //防止中文乱码
        config.addDataSourceProperty("useUnicode", "true");
        config.addDataSourceProperty("characterEncoding", "utf8");
        
        config.setConnectionTestQuery("SELECT 1");

        this.dataSource = new HikariDataSource(config);
        
        return true;
    }

    @Override
    public boolean stop() {
        if (dataSource != null)
            dataSource.close();
        return true;
    }

    @Override
    public DataSource getDataSource() {
        return this.dataSource;
    }
}

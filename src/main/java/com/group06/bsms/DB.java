package com.group06.bsms;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DB {

    private static Connection db;

    private DB() {
    }

    public static void connectToDB(String configFile) throws SQLException {
        db = new HikariDataSource(new HikariConfig(configFile)).getConnection();
        db.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
    }
    
    public static void disconnectFromDB() throws SQLException {
        db.close();
    }

    public static Connection db() {
        return db;
    }
}

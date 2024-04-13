package com.group06.bsms;

import com.group06.bsms.auth.Hasher;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DB {

    private static Connection db;

    private DB() {
    }

    public static void connectToDB(String configFile) throws Exception {
        var props = new Properties();
        props.load(Main.class.getClassLoader().getResourceAsStream(configFile));

        if (props.getProperty("secret") != null) {
            props.setProperty(
                    "dataSource.password",
                    Hasher.decryptKey(props.getProperty("dataSource.password"))
            );
            props.remove("secret");
        }

        db = new HikariDataSource(new HikariConfig(props)).getConnection();
        db.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
    }

    public static void disconnectFromDB() throws SQLException {
        db.close();
    }

    public static Connection db() {
        return db;
    }
}

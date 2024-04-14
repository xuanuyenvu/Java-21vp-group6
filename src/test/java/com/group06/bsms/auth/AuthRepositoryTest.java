package com.group06.bsms.auth;

import com.group06.bsms.DB;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthRepositoryTest {

    private static Connection db;

    public AuthRepositoryTest() {
    }

    @BeforeAll
    public static void setUpClass() throws Exception {
        DB.connectToDB("env/bsms.properties");
        db = DB.db();
    }

    @AfterAll
    public static void tearDownClass() throws SQLException {
        DB.disconnectFromDB();
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testExistsAccountByCredentials() throws Exception {
        var instance = new AuthRepository(db);

        assertFalse(instance.selectAccountByCredentials(
                "1234567800", "123"
        ) != null);

        assertTrue(instance.selectAccountByCredentials(
                "1234567800", "employee"
        ) != null);
    }

    @Test
    public void testUpdateAccountPassword() throws Exception {
        var instance = new AuthRepository(db);

        var phone = "1234567800";
        var oldPassword = "employee";
        var newPassword = "admin";

        assertThrows(Exception.class, () -> {
            instance.updateAccountPassword(phone, oldPassword, "");
        });

        instance.updateAccountPassword(phone, oldPassword, newPassword);
        assertTrue(instance.selectAccountByCredentials(phone, newPassword) != null);

        instance.updateAccountPassword(phone, newPassword, oldPassword);
        assertTrue(instance.selectAccountByCredentials(phone, oldPassword) != null);
    }
}

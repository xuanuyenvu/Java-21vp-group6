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
    public static void setUpClass() throws SQLException {
        DB.connectToDB("/env/bsms.properties");
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

        assertFalse(instance.existsAccountByCredentials(
                "1234567890", "123"
        ));

        assertTrue(instance.existsAccountByCredentials(
                "1234567890", "password123"
        ));
    }

    @Test
    public void testUpdateAccountPassword() throws Exception {
        var instance = new AuthRepository(db);

        var phone = "1234567890";
        var oldPassword = "password123";
        var newPassword = "pass123";

        assertThrows(SQLException.class, () -> {
            instance.updateAccountPassword(phone, oldPassword, "");
        });

        instance.updateAccountPassword(phone, oldPassword, newPassword);
        assertTrue(instance.existsAccountByCredentials(phone, newPassword));

        instance.updateAccountPassword(phone, newPassword, oldPassword);
        assertTrue(instance.existsAccountByCredentials(phone, oldPassword));
    }
}

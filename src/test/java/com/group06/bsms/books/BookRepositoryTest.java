package com.group06.bsms.books;

import com.group06.bsms.DB;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;

public class BookRepositoryTest {

    private static Connection db;

    public BookRepositoryTest() {
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
}

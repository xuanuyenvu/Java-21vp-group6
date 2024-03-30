package com.group06.bsms.books;

import com.group06.bsms.DB;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookRepositoryTest {

    private static Connection db;

    public BookRepositoryTest() {
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
    public void testUpdateBookAttributeById() throws Exception {
        System.out.println("updateBookAttributeById");
        int bookId = 2;
        String attr = "salePrice";
        Object value = Double.valueOf(2121);
        BookRepository instance = new BookRepository(db);
        instance.updateBookAttributeById(bookId, attr, value);
        assertEquals(Double.valueOf(2121), instance.selectById(bookId).salePrice);
    }

}

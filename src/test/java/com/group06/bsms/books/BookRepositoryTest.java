package com.group06.bsms.books;

import com.group06.bsms.DB;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;
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
        int bookId = 2;
        String attr = "salePrice";
        Object value = Double.valueOf(2121);
        BookRepository instance = new BookRepository(db);
        instance.updateBookAttributeById(bookId, attr, value);
        assertEquals(Double.valueOf(2121), instance.selectById(bookId).salePrice);
    }

    @Test
    public void testGetNewBooks() throws Exception {
        BookRepository instance = new BookRepository(db);
        List<Book> result = instance.getNewBooks();
        for (var book : result) {
            System.out.println(book);
        }
    }

//    /**
//     * Test of getHotBooks method, of class BookRepository.
//     */
    @Test
    public void testGetHotBooks() throws Exception {
        System.out.println("getHotBooks");
        BookRepository instance = new BookRepository(db);
        List<Book> result = instance.getHotBooks();
        for (var book : result) {
            System.out.println(book);
        }
    }
//
//    /**
//     * Test of getOutOfStockBooks method, of class BookRepository.
//     */

    @Test
    public void testGetOutOfStockBooks() throws Exception {
        System.out.println("getOutOfStockBooks");
        BookRepository instance = new BookRepository(db);
        List<Book> result = instance.getOutOfStockBooks();
        for (var book : result) {
            System.out.println(book);
        }
    }
}

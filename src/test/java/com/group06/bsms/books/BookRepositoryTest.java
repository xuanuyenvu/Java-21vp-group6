package com.group06.bsms.books;

import com.group06.bsms.DB;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
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
    public void testSelectSearchSortFilterBooks() throws Exception {
        System.out.println("selectSearchSortFilterBooks");
        Map<Integer, SortOrder> sortValue = new HashMap<>();
        sortValue.put(1, SortOrder.ASCENDING);
        String searchBookString = "";
        int authorId = 0;
        int publisherId = 0;
        Double minPrice = null;
        Double maxPrice = null;
        List<Integer> listBookCategoryId = null;
        var instance = new BookRepository(db);
        // List<Book> result = instance.selectSearchSortFilterBooks(sortValue, searchBookString, "", authorId, publisherId, minPrice, maxPrice, listBookCategoryId);
//        assertEquals(expResult, result);
//        System.out.println(result.get(0).title);
//        assertEquals(3, result.size(), "Danh sách sách không đúng kích thước");
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of updateBookAttributeById method, of class BookRepository.
     */
    @Test
    public void testUpdateBookAttributeById() throws Exception {
        System.out.println("updateBookAttributeById");
        int bookId = 2;
        String attr = "salePrice";
        Object value = Double.valueOf(2121);
        BookRepository instance = new BookRepository(db);
        instance.updateBookAttributeById(bookId, attr, value);
        System.out.println("done");
        assertEquals(Double.valueOf(2121), instance.selectById(bookId).salePrice);
        // TODO review the generated test code and remove the default call to fail.
    }

}

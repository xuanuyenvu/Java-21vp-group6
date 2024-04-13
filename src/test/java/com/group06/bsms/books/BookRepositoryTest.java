package com.group06.bsms.books;

import com.group06.bsms.DB;
import com.group06.bsms.Repository;
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

//    @Test
//    public void testSelectTop10BooksWithHighestRevenue() throws Exception {
//        System.out.println("selectTop10BooksWithHighestRevenue");
//        Map<Integer, SortOrder> sortAttributeAndOrder = new HashMap<>();
//        sortAttributeAndOrder.put(4, SortOrder.ASCENDING);
//        BookRepository instance = new BookRepository(db);
//        List<Book> result = instance.selectTop10BooksWithHighestRevenue(sortAttributeAndOrder);
//        System.err.println(result);
//    }
}

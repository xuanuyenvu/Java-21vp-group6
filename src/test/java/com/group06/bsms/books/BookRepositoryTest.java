package com.group06.bsms.books;

import com.group06.bsms.DB;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
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
    public void testSelectBooksByFilter() throws Exception {
        var instance = new BookRepository(db);

        // Trường hợp 1: Chọn tất cả sách (không áp dụng bộ lọc)
//        int authorId1 = -1;
//        int publisherId1 = -1;
//        Double minPrice1 = null;
//        Double maxPrice1 = null;
//        List<Integer> listBookCategoryId1 = null;
//        List<Book> result1 = instance.selectBooksByFilter(authorId1, publisherId1, minPrice1, maxPrice1, listBookCategoryId1);
//        assertEquals(2, result1.size(), "Danh sách sách không đúng kích thước");

        // Trường hợp 2: Chọn sách của một tác giả cụ thể
        int authorId2 = 1;
        int publisherId2 = -1;
        Double minPrice2 = null;
        Double maxPrice2 = null;
        List<Integer> listBookCategoryId2 = null;
        List<Book> result2 = instance.selectBooksByFilter(authorId2, publisherId2, minPrice2, maxPrice2, listBookCategoryId2);
        assertEquals(1, result2.size(), "Danh sách sách không đúng kích thước");
        System.out.println(result2.get(0).title);
        assertEquals("Sample Book 1", result2.get(0).title , "Sách không đúng");
        

        // Trường hợp 3: Chọn sách theo giá
        int authorId3 = -1;
        int publisherId3 = -1;
        Double minPrice3 = 15.0;
        Double maxPrice3 = 25.0;
        List<Integer> listBookCategoryId3 = null;
        List<Book> result3 = instance.selectBooksByFilter(authorId3, publisherId3, minPrice3, maxPrice3, listBookCategoryId3);
        assertEquals(2, result3.size(), "Danh sách sách không đúng kích thước");
        System.out.println(result3.get(1).title);

        // Trường hợp 4: Chọn sách theo cateGory 
        int authorId4 = -1;
        int publisherId4 = -1;
        Double minPrice4 = null;
        Double maxPrice4 = null;
        List<Integer> listBookCategoryId4 = Arrays.asList(1, 2);
        List<Book> result4 = instance.selectBooksByFilter(authorId4, publisherId4, minPrice4, maxPrice4, listBookCategoryId4);
        assertEquals(1, result4.size(), "Danh sách sách không đúng kích thước");
    }

}

package com.group06.bsms;

import com.group06.bsms.books.Book;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RepositoryTest {

    private static Connection db;

    public RepositoryTest() {
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
    public void testCount() throws Exception {
        Repository instance = new Repository<Book>(db, Book.class);

        assertEquals(instance.count(), 3);
    }

    @Test
    public void testDeleteById() throws Exception {
        Repository instance = new Repository<Book>(db, Book.class);

        var book = new Book(
                1, 1, "Sample Book 4", 5,
                Date.valueOf(LocalDate.of(2020, 5, 3)),
                "6x9", "Dank", "Boring", 1, 10, false, 3,0
        );
        book.id = 4;

        instance.insert(
                book,
                "id",
                "authorId", "publisherId", "title", "pageCount", "publishDate",
                "dimension", "translatorName", "overview", "quantity",
                "salePrice", "isHidden", "hiddenParentCount", "maxImportPrice"
        );

        assertTrue(instance.existsById(4));

        instance.deleteById(4);

        assertNull(instance.selectById(4));

        assertThrows(Exception.class, () -> instance.deleteById(4));
    }

    @Test
    public void testExistsById() throws Exception {
        Repository instance = new Repository<Book>(db, Book.class);

        assertTrue(instance.existsById(1));
        assertTrue(instance.existsById(2));
        assertTrue(instance.existsById(3));
        assertFalse(instance.existsById(4));
    }

    @Test
    public void testSelectAll() throws Exception {
        Repository instance = new Repository<Book>(db, Book.class);

        assertEquals(
                instance.selectAll(
                        "title", "%",
                        0, 10,
                        "title", Repository.Sort.ASC,
                        "id", "title"
                )
                        .stream()
                        .map((book) -> ((Book) book).title)
                        .collect(Collectors.toList()),
                Arrays.asList("Sample Book 1", "Sample Book 2", "Sample Book 3")
        );

        assertEquals(
                instance.selectAll(
                        "title", "Sample%3",
                        0, 10,
                        "title", Repository.Sort.ASC,
                        "id", "title"
                )
                        .stream()
                        .map((book) -> ((Book) book).id)
                        .collect(Collectors.toList()),
                Arrays.asList(3)
        );

        assertEquals(
                instance.selectAll(
                        "title", "%",
                        1, 2,
                        "id", Repository.Sort.DESC,
                        "id"
                )
                        .stream()
                        .map((book) -> ((Book) book).id)
                        .collect(Collectors.toList()),
                Arrays.asList(2, 1)
        );
    }

    @Test
    public void testSelectAllWithMultipleSearchParams() throws Exception {
        Repository instance = new Repository<Book>(db, Book.class);

        Map<String, Object> searchParams1 = new HashMap<>();
        searchParams1.put("title", "sample%3");

        Map<String, Object> searchParams2 = new HashMap<>();
        searchParams2.put("translatorname", "translator b");
        searchParams2.put("overview", "captivating");

        assertEquals(
                instance.selectAll(
                        null,
                        0, 3,
                        "title", Repository.Sort.DESC,
                        "id", "title"
                )
                        .stream()
                        .map((book) -> ((Book) book).title)
                        .collect(Collectors.toList()),
                Arrays.asList("Sample Book 3", "Sample Book 2", "Sample Book 1")
        );

        assertEquals(
                instance.selectAll(
                        searchParams1,
                        0, 10,
                        "title", Repository.Sort.ASC,
                        "id", "title"
                )
                        .stream()
                        .map((book) -> ((Book) book).id)
                        .collect(Collectors.toList()),
                Arrays.asList(3)
        );

        assertEquals(
                instance.selectAll(
                        searchParams2,
                        0, null,
                        "title", Repository.Sort.ASC,
                        "id", "title"
                )
                        .stream()
                        .map((book) -> ((Book) book).id)
                        .collect(Collectors.toList()),
                Arrays.asList(2, 3)
        );

        assertEquals(
                instance.selectAll(
                        null,
                        0, null,
                        "id", Repository.Sort.ASC,
                        "id", "title"
                )
                        .stream()
                        .map((book) -> ((Book) book).id)
                        .collect(Collectors.toList()),
                Arrays.asList(1, 2, 3)
        );
    }

    @Test
    public void testSelectById() throws Exception {
        Repository instance = new Repository<Book>(db, Book.class);

        assertEquals(((Book) instance.selectById(1)).id, 1);
        assertEquals(((Book) instance.selectById(2)).title, "Sample Book 2");
        assertEquals(((Book) instance.selectById(3)).quantity, 30);
        assertNull(instance.selectById(4));
    }

    @Test
    public void testInsert() throws Exception {
        Repository instance = new Repository<Book>(db, Book.class);

        assertFalse(instance.existsById(4));

        var book = new Book(
                1, 1, "Sample Book 4", 5,
                Date.valueOf(LocalDate.of(2020, 5, 3)),
                "6x9", "Dank", "Boring", 1, 10,false, 3, 0
        );
        book.id = 4;

        instance.insert(
                book,
                "id",
                "authorId", "publisherId", "title", "pageCount", "publishDate",
                "dimension", "translatorName", "overview", "quantity",
                "salePrice", "hiddenParentCount", "maxImportPrice"
        );

        assertTrue(instance.existsById(4));
        assertEquals(((Book) instance.selectById(4)).pageCount, book.pageCount);

        instance.deleteById(4);
        assertFalse(instance.existsById(4));
    }

    @Test
    public void testUpdateById() throws Exception {
        Repository instance = new Repository<Book>(db, Book.class);

        assertEquals(((Book) instance.selectById(2)).title, "Sample Book 2");

        instance.updateById(2, "title", "Sample Book 22");

        assertEquals(((Book) instance.selectById(2)).title, "Sample Book 22");

        instance.updateById(2, "title", "Sample Book 2");

        assertEquals(((Book) instance.selectById(2)).title, "Sample Book 2");
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.group06.bsms.authors;

import com.group06.bsms.DB;
import com.group06.bsms.books.BookRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AuthorRepositoryTest {

    private static Connection db;

    public AuthorRepositoryTest() {
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

    /**
     * Test of selectAllAuthorNames method, of class AuthorRepository.
     */
    @Test
    public void testSelectAllAuthorNames() throws Exception {
        System.out.println("selectAllAuthorNames");
        AuthorRepository instance = new AuthorRepository(db);
//        List<Author> expResult = null;
        List<Author> result = instance.selectAllAuthorNames();
        for (Author i : result) {
            System.out.println(i.name);
        }
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
}

package com.group06.bsms.books;

import com.group06.bsms.Repository;
import java.sql.Connection;

public class BookRepository extends Repository<Book> implements BookDAO {
    public BookRepository(Connection db) {
        super(db, Book.class);
    }
}

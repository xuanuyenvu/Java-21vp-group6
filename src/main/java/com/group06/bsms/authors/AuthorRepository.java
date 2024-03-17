package com.group06.bsms.authors;

import java.sql.Connection;

import com.group06.bsms.Repository;

public class AuthorRepository extends Repository<Author> implements AuthorDAO {
    public AuthorRepository(Connection db) {
        super(db, Author.class);
    }
}

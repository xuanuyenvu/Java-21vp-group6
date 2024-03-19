package com.group06.bsms.authors;

import java.sql.Connection;

import com.group06.bsms.Repository;
import java.util.List;

public class AuthorRepository extends Repository<Author> implements AuthorDAO {

    public AuthorRepository(Connection db) {
        super(db, Author.class);
    }

    @Override
    public List<Author> selectAllAuthors() throws Exception {
        try {
            db.setAutoCommit(false);

            var authors = selectAll(
                    null,
                    0, 10,
                    "name", Sort.ASC
            );

            db.commit();

            return authors;

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }
}

package com.group06.bsms.authors;

import java.util.ArrayList;
import java.util.List;

public class AuthorService {

    private final AuthorDAO authorDAO;

    public AuthorService(AuthorDAO authorDAO) {
        this.authorDAO = authorDAO;
    }

    public List<Author> selectAllAuthors() {
        try {
            List<Author> authors = authorDAO.selectAllAuthors();
            return authors;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public int insertAuthorIfNotExists(String authorName) throws Exception {
        try {
            if (authorName == null) {
                return -1;
            } else {
                return authorDAO.insertAuthorIfNotExists(authorName);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public Author selectAuthor(int id) throws Exception {
        try {
            return authorDAO.selectAuthor(id);
        } catch (Exception e) {
            throw e;
        }
    }

    public Author selectAuthorByName(String authorName) throws Exception {
        try {
            return authorDAO.selectAuthorByName(authorName);
        } catch (Exception e) {
            throw e;
        }
    }
}

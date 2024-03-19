package com.group06.bsms.authors;

import java.util.ArrayList;
import java.util.List;

public class AuthorService {

    private final AuthorDAO authorDAO;

    public AuthorService(AuthorDAO authorDAO) {
        this.authorDAO = authorDAO;
    }

    public List<Author> getAllAuthors() {
        try {
            List<Author> authors = authorDAO.selectAllAuthors();
            return authors;
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<Author>();
        }
    }

    public int selectIdByName(String authorName) throws Exception {
        try {
            return authorDAO.selectIdByName(authorName);
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
}

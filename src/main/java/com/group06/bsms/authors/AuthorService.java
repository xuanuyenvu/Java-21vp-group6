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
}

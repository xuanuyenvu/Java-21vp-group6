package com.group06.bsms.authors;

public class AuthorService {
    private final AuthorDAO authorDAO;

    public AuthorService(AuthorDAO authorDAO) {
        this.authorDAO = authorDAO;
    }
}

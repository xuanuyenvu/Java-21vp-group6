package com.group06.bsms.books;

import java.util.List;

public class BookService {
    private final BookDAO bookDAO;

    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    public List<Book> getAllBooks() {
        try {
            List<Book> books = bookDAO.selectAllBooks();
            if (books != null) {
                for (var book : books) {
                    System.out.println(book);
                }
            }
            return books;
        }
        catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }
}
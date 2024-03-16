package com.group06.bsms.books;

import java.util.List;

public class BookService {
    private final BookDAO bookDAO;

    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    public void update(Book book) throws Exception, IllegalArgumentException {
        try {
            if (book == null)
                throw new IllegalArgumentException("Book object cannot be null");
            if (book.salePrice >= 1.1 * book.maxImportPrice)
                throw new Exception("Sale price must bigger than 1.1 * max import price");

            bookDAO.updateBook(book);
        } catch (Exception e) {
            throw e;
        }

    }

    public List<Book> getAllBooks() {
        try {
            List<Book> books = bookDAO.selectAllBooks();
            // if (books != null) {
            // for (var book : books) {
            // System.out.println(book);
            // }
            // }
            return books;
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    public List<Book> searchBooks(String title) {
        try {
            List<Book> books = bookDAO.selectBook(title);
            if (books != null) {
                for (var book : books) {
                    System.out.println(book);
                }
            }
            return books;
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }
}

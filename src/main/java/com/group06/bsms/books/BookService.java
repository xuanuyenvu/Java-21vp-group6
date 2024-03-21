package com.group06.bsms.books;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookService {

    private final BookDAO bookDAO;

    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    public void updateBook(Book book) throws Exception, IllegalArgumentException {
        try {
            if (book == null) {
                throw new IllegalArgumentException("Book object cannot be null");
            }
            if (Double.valueOf(book.maxImportPrice) == null && Double.valueOf(book.salePrice) != null) {
                throw new Exception("Cannot update sale price because the maximum import price is null");
            }
            if (book.salePrice >= 1.1 * book.maxImportPrice) {
                throw new Exception("Sale price must be bigger than 1.1 * maximum import price");
            }

            bookDAO.updateBook(book);
        } catch (Exception e) {
            throw e;
        }

    }

    public Book getBook(int id) throws Exception {
        try {
            return bookDAO.selectBook(id);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Book> getAllBooks() {
        try {
            List<Book> books = bookDAO.selectAllBooks();
            return books;
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<Book>();
        }
    }

    public List<Book> searchBooks(String title) {
        try {
            List<Book> books = bookDAO.selectBooks(title);
            return books;
        } catch (SQLException e) {
            System.out.println("An error occurred while searching for books: " + e.getMessage());
        } catch (Throwable e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
        return new ArrayList<Book>();
    }

    public void hideBook(int id) {
        try {
            bookDAO.hideBook(id);
        } catch (SQLException e) {
            System.out.println("An error occurred while hiding a book: " + e.getMessage());
        } catch (Throwable e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    public void showBook(int id) {
        try {
            bookDAO.showBook(id);
        } catch (SQLException e) {
            System.out.println("An error occurred while showing a book: " + e.getMessage());
        } catch (Throwable e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    public void insertBook(Book book) throws Exception {
        try {
            if (book == null) {
                throw new IllegalArgumentException("Book object cannot be null");
            }

            if (book.title == null || book.authorId == -1
                    || book.publisherId == -1
                    || book.publishDate == null
                    || book.categories.isEmpty()
                    || book.dimension == null
                    || book.pageCount == 0
                    || book.overview == null) {

                throw new IllegalArgumentException("Please fill in all required information.");
            }

            bookDAO.insertBook(book);
        } catch (Exception e) {
            throw e;
        }
    }

}

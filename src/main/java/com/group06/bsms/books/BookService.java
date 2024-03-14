package com.group06.bsms.books;

import java.util.ArrayList;
import java.util.List;

public class BookService {
    private final BookDAO bookDAO;

    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    public List<Book> getAllBooks() {
        try {
            List<Book> books = bookDAO.selectAllBooks();
            return books;
        }
        catch (Exception e) {
            System.out.println(e);
            return new ArrayList<Book>();
        }
    }

    public List<Book> searchBooks(String title){
        try {
            List<Book> books = bookDAO.selectBooks(title);
            return books;
        }
        catch (Exception e) {
            System.err.println(e);
            return null;
        }
    }

    public boolean hideBook(int id) {
        try {
            bookDAO.hideBook(id);
            return true;
        }
        catch (Exception e) {
            System.err.println(e);
            return false;
        }
    }

    public boolean showBook(int id) {
        try {
            bookDAO.showBook(id);
            return true;
        }
        catch (Exception e) {
            System.err.println(e);
            return false;
        }
    }
}
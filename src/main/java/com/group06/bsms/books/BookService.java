package com.group06.bsms.books;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;

public class BookService {

    private final BookDAO bookDAO;

    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
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

    public List<Book> searchSortFilterBook(int offset, int limit, Map<Integer, SortOrder> sortValue,
            String searchString, String searchChoice,
            int authorId, int publisherId, Double minPrice, Double maxPrice,
            List<Integer> listBookCategoryId) {
        try {
            List<Book> books = bookDAO.selectSearchSortFilterBooks(offset, limit, sortValue, searchString, searchChoice, authorId, publisherId, Double.MIN_VALUE, Double.MAX_VALUE, listBookCategoryId);
            return books;
        } catch (SQLException e) {
            System.out.println("An error occurred while get book: " + e.getMessage());
        } catch (Throwable e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
        return null;
    }

    public void updateBookAttributeById(int bookId, String attr, Object value) {
        try {
            bookDAO.updateBookAttributeById(bookId, attr, value);
        } catch (SQLException e) {
            System.out.println("An error occurred while update a book: " + e.getMessage());
        } catch (Throwable e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }
}

package com.group06.bsms.books;

import java.sql.Date;
import java.util.List;

public interface BookDAO {
    //get 
    boolean existsBookById(int id) throws Exception;
    List<Book> selectAllBooks() throws Exception;
    List<Book> selectBookByTitle(String title) throws Exception;
    List<Book> selectBookByAuthor(String authorName) throws Exception;
    //post 
    void createBook(Book book) throws Exception;
    //put 
    // void updateBook(int authorId, int publisherId, String title, int pageCount,
    //                 Date publishDate, String dimension, String translatorName,
    //                 String overview, double salePrice,
    //                 List<Category> categories) throws Exception;
    void enableBook(int id) throws Exception;
    void disableBook(int id) throws Exception;
    void updateHiddenParentCount() throws Exception;
}


package com.group06.bsms.books;

import java.util.List;

public interface BookDAO {
    //get 
    boolean existsBookById(int id) throws Exception;
    List<Book> selectAllBooks() throws Exception;
    List<Book> selectBooks(String title) throws Exception;

    void insertBook(Book book) throws Exception;
    //put 
    void showBook(int id) throws Exception;
    void hideBook(int id) throws Exception;
    void updateBookHiddenParentCount(int id) throws Exception;
    void updateBookHiddenParentCountByAuthorId(int authorId) throws Exception;
    void updateBookHiddenParentCountByPublisherId(int publisherId) throws Exception;
}


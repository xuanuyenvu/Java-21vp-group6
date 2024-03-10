package com.group06.bsms.books;

import java.util.List;

public class BookService {

    private final BookDAO bookDAO;

    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    public List<Book> filter(int authorId, int publisherId, Double minPrice, Double maxPrice, List<Integer> listBookCategoryId) throws Exception {
        return bookDAO.selectBooksByFilter(authorId, publisherId, minPrice, maxPrice, listBookCategoryId);
    }
}

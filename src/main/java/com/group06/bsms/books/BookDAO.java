package com.group06.bsms.books;

import java.util.List;

public interface BookDAO {
    List<Book> selectBooksByFilter(int authorId, int publisherId, Double minPrice, Double maxPrice, List<Integer> listBookCategoryId)
            throws Exception;
}

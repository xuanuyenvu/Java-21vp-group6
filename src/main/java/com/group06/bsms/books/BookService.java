package com.group06.bsms.books;


public class BookService {
    private final BookDAO bookDAO;

    public BookService(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    public void update(Book book) throws Exception{
        if(book.salePrice <= 1.1*book.maxImportPrice) throw new Exception("Sale price must bigger than 1.1 * max import price");
        bookDAO.update(book);
    }
}

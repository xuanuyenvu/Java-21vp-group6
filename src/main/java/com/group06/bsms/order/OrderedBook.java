package com.group06.bsms.order;

public class OrderedBook {
    public int orderSheetId;
    public int bookId;
    public String title;
    public Integer quantity;
    public Double pricePerBook;

    public OrderedBook() {

    }

    public OrderedBook(int bookId, String title, Integer quantity, Double pricePerBook) {
        this.bookId = bookId;
        this.title = title;
        this.quantity = quantity;
        this.pricePerBook = pricePerBook;

    }

    public OrderedBook(int orderSheetId, int bookId, String title, Integer quantity, Double pricePerBook) {
        this.orderSheetId = orderSheetId;
        this.bookId = bookId;
        this.title = title;
        this.quantity = quantity;
        this.pricePerBook = pricePerBook;

    }

    @Override
    public String toString() {
        return "OrderedBook {importSheetId=" + orderSheetId + ", bookId=" + bookId + ", title=" + title
                + ", quantity=" + quantity + ", pricePerBook=" + pricePerBook + "}";
    }

    
}
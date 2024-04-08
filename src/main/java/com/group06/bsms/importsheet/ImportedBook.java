package com.group06.bsms.importsheet;

public class ImportedBook {
    public int bookId;
    public String title;
    public Integer quantity;
    public Double pricePerBook;

    public ImportedBook() {

    }

    public ImportedBook(int bookId, String title, Integer quantity, Double pricePerBook) {
        this.bookId = bookId;
        this.title = title;
        this.quantity = quantity;
        this.pricePerBook = pricePerBook;

    }

    @Override
    public String toString() {
        return "Import Book {" + ", id=" + bookId + ", title=" + title + ", quantity="
                + quantity + ", pricePerBook=" + pricePerBook + "}";
    }

}
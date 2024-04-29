package com.group06.bsms.order;

public class ImportedBook {
    public int importSheetId;
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

    public ImportedBook(int impportedSheetId, int bookId, String title, Integer quantity, Double pricePerBook) {
        this.importSheetId = impportedSheetId;
        this.bookId = bookId;
        this.title = title;
        this.quantity = quantity;
        this.pricePerBook = pricePerBook;

    }

    @Override
    public String toString() {
        return "ImportedBook {importSheetId=" + importSheetId + ", bookId=" + bookId + ", title=" + title
                + ", quantity=" + quantity + ", pricePerBook=" + pricePerBook + "}";
    }

    
}
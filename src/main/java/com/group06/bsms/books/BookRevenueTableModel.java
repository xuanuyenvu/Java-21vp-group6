package com.group06.bsms.books;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.table.AbstractTableModel;

public class BookRevenueTableModel extends AbstractTableModel {

    private List<Book> books = new ArrayList<>();
    private String[] columns = {"Title", "Author", "Publisher", "Quantity", "Sale Price", "Revenue"};
    private final BookService bookService;

    public BookRevenueTableModel(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public int getRowCount() {
        return books.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    /**
     * @param row
     * @param col
     * @return value at [row, col] in the table
     */
    @Override
    public Object getValueAt(int row, int col) {
        if (row >= books.size()) {
            return null;
        }
        Book book = books.get(row);
        switch (col) {
            case 0:
                return book.title;
            case 1:
                return ((book.author == null) ? "" : book.author.name);
            case 2:
                return ((book.publisher == null) ? "" : book.publisher.name);
            case 3:
                return book.quantity;
            case 4:
                return book.salePrice;
            case 5:
                return book.revenue;
            default:
                return null;
        }
    }

    /**
     * @param val
     * @param row
     * @param col
     * @return value at [row, col] in the table
     */
    public Book getBook(int row) {
        return books.get(row);
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    public boolean contains(int id) {
        Optional<Book> foundBook = books.stream()
                .filter(book -> book.id == id)
                .findFirst();
        return foundBook.isPresent();
    }

    @Override
    public Class<?> getColumnClass(int col) {
        switch (col) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            case 3:
                return Integer.class;
            case 4:
                return Double.class;
            case 5:
                return Double.class;
            default:
                return null;
        }
    }

//    @Override
//    public boolean isCellEditable(int rowIndex, int columnIndex) {
//        return (columnIndex == 0 || columnIndex == 4 || columnIndex == 5);
//    }
    public void reloadAllBooks(List<Book> newBooks) {
        if (newBooks != null) {
            books.clear();
            fireTableDataChanged();
            for (var book : newBooks) {
                if (!contains(book.id)) {
                    addRow(book);
                }
            }
        }
    }

    public void loadNewBooks(List<Book> newBooks) {
        if (newBooks != null) {
            for (var book : newBooks) {
                if (!contains(book.id)) {
                    addRow(book);
                }
            }
        }
    }

    void addRow(Book book) {
        books.add(book);
//        SwingUtilities.invokeLater(() -> fireTableRowsInserted(books.size() - 1, books.size() - 1));
    }
}

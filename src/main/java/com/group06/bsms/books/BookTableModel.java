package com.group06.bsms.books;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

public class BookTableModel extends AbstractTableModel {
    private List<Book> books = new ArrayList<>();
    private String[] columns = {"Title", "Author", "Publisher", "Quantity", "Sale Price", "Actions"};
    
    @Override
    public int getRowCount() {
        return books.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Book book = books.get(row);
        switch (col) {
            case 0:
                return book.title;
            case 1:
                return book.author.name;
            case 2:
                return book.publisher.name;
            case 3:
                return book.quantity;
            case 4:
                return book.salePrice;
            default:
                return null;
        }
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

    public void loadNewBooks(List<Book> newBooks) {
        if (newBooks != null) {
            for (var book : newBooks) {
                if (!contains(book.id)) {
                    books.add(book);
                    SwingUtilities.invokeLater(() -> fireTableRowsInserted(books.size() - 1, books.size() - 1));
                }
            }
        }
    }

    public void reloadBooks(List<Book> newBooks) {
        if (newBooks != null) {
            books.clear();
            fireTableDataChanged();

            for (var book : newBooks) {
                if (!contains(book.id)) {
                    books.add(book);
                    // SwingUtilities.invokeLater(() -> fireTableRowsInserted(books.size() - 1, books.size() - 1));
                }
            }

            fireTableDataChanged();
        }
    }

}

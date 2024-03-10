package com.group06.bsms.books;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;


/**
 * A custom structure used to display a table
 */
public class BookTableModel extends AbstractTableModel {
    private List<Book> books = new ArrayList<>();
    private List<Object> action = new ArrayList<>();
    private String[] columns = {"Title", "Author", "Publisher", "Quantity", "Sale Price", "Actions"};
    
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
     * @return if want to get isHidden state, use getHiddenState instead
     */
    @Override
    public Object getValueAt(int row, int col) {
        if (row >= books.size()) return null;
        Book book = books.get(row);
        switch (col) {
            case 0:
                return book.title;
            case 1:
                return "";
            case 2:
                return "";
            case 3:
                return book.quantity;
            case 4:
                return book.salePrice;
            case 5:
                return action.get(row);
            default:
                return null;
        }
    }
    
    /**
     * @param val
     * @param row
     * @param col
     * @return value at [row, col] in the table
     * @return if want to set isHidden state, use setHiddenState instead
     */
    @Override
    public void setValueAt(Object val, int row, int col) {
        Book book = books.get(row);
        switch (col) {
            case 0:
                book.title = (String) val;
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                book.quantity = (Integer) val;
                break;
            case 4:
                book.salePrice = (Double) val;
                break;
            case 5:
                action.set(row, (Boolean) val);
                break;
            default:
                break;
        }
        fireTableCellUpdated(row, col);
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
            case 1:
            case 2:
                return String.class;
            case 3:
                return Integer.class;
            case 4:
                return Double.class;
            case 5:
                return Boolean.class;
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == 5);
    }

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
        action.add(!book.isHidden);
        SwingUtilities.invokeLater(() -> fireTableRowsInserted(books.size() - 1, books.size() - 1));  
    }
    
    void setHiddenState(int row) {
        books.get(row).isHidden = !books.get(row).isHidden;
    }

    boolean getHiddenState(int row) {
        return books.get(row).isHidden;
    }

}

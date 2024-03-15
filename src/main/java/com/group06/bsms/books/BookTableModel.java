package com.group06.bsms.books;

import com.group06.bsms.components.ActionBtn;
import com.group06.bsms.components.TableActionEvent;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

class TableActionCellEditor extends DefaultCellEditor {

    private final TableActionEvent event;

    public TableActionCellEditor(TableActionEvent event) {
        super(new JCheckBox());
        this.event = event;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Component com = super.getTableCellEditorComponent(table, value, isSelected, row, column);
        BookTableModel model = (BookTableModel) table.getModel();
        int isHidden = model.getHiddenState(table.convertRowIndexToModel(row));
        int modelRow = table.convertRowIndexToModel(row);

        ActionBtn action = new ActionBtn(isHidden);
        action.initEvent(event, modelRow, isHidden);

        action.setBackground(Color.WHITE);

        return action;
    }

}

class TableActionCellRender extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        int modelRow = table.convertRowIndexToModel(row);

        int isHidden = ((BookTableModel) table.getModel()).getHiddenState(modelRow);

//        Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        ActionBtn action = new ActionBtn(isHidden);
        action.setBackground(Color.WHITE);

        return action;
    }
}

/**
 * A custom structure used to display a table
 */
public class BookTableModel extends AbstractTableModel {

    private List<Book> books = new ArrayList<>();
    private List<Object> actionState = new ArrayList<>();
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
                return actionState.get(row);
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
                actionState.set(row, (Boolean) val);
                break;
            default:
                break;
        }
        fireTableCellUpdated(row, col);
    }

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
                return Boolean.class;
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == 0 || columnIndex == 1 || columnIndex == 2 || columnIndex == 3 || columnIndex == 4 || columnIndex == 5);
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
        actionState.add(!book.isHidden);
        SwingUtilities.invokeLater(() -> fireTableRowsInserted(books.size() - 1, books.size() - 1));
    }

    void setHiddenState(int row) {
        books.get(row).isHidden = !books.get(row).isHidden;
    }

    int getHiddenState(int row) {
        Book book = books.get(row);
        if (book.hiddenParentCount > 0) {
            return -1;
        }
        if (book.isHidden) {
            return 1;
        }
        return 0;
    }
}

package com.group06.bsms.authors;

import static com.group06.bsms.Main.app;
import com.group06.bsms.books.BookCRUD;
import com.group06.bsms.components.ActionBtn;
import com.group06.bsms.components.TableActionEvent;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;

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
        AuthorTableModel model = (AuthorTableModel) table.getModel();
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

        int isHidden = ((AuthorTableModel) table.getModel()).getHiddenState(modelRow);

        ActionBtn action = new ActionBtn(isHidden);
        action.setBackground(Color.WHITE);

        return action;
    }
}

/**
 * A custom structure used to display a table
 */
public class AuthorTableModel extends AbstractTableModel {

    private List<Author> authors = new ArrayList<>();
    private String[] columns = {"Name", "Actions"};
    public boolean editable = false;
    private final AuthorService authorService;
    private final BookCRUD bookCRUD;

    public AuthorTableModel(AuthorService authorService, BookCRUD bookCRUD) {
        this.bookCRUD = bookCRUD;
        this.authorService = authorService;
    }

    @Override
    public int getRowCount() {
        return authors.size();
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
        if (row >= authors.size()) {
            return null;
        }
        Author author = authors.get(row);
        switch (col) {
            case 0:
                return author.name;
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
        if (col == 1) {
            return;
        }

        if (!editable) {
            editable = true;
        }

        Author author = authors.get(row);
        switch (col) {
            case 0:
                if (!author.name.equals((String) val)) {
                    try {
                        authorService.updateAuthorAttributeById(author.id, "name", (String) val);
                        author.name = (String) val;

                        bookCRUD.loadAuthorInto();
                    } catch (Exception e) {
                        if (e.getMessage().contains("author_name_key")) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "An author with this name already exists",
                                    "BSMS Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        } else if (e.getMessage().contains("author_name_check")) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Name cannot be empty",
                                    "BSMS Error",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(
                                    app,
                                    e.getMessage(),
                                    "BSMS Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                }
                break;
            default:
                break;
        }
        fireTableCellUpdated(row, col);
    }

    public Author getAuthor(int row) {
        return authors.get(row);
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    public boolean contains(int id) {
        Optional<Author> foundAuthor = authors.stream()
                .filter(author -> author.id == id)
                .findFirst();
        return foundAuthor.isPresent();
    }

    @Override
    public Class<?> getColumnClass(int col) {
        switch (col) {
            case 0:
                return String.class;
            case 1:
                return Boolean.class;
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex == 0 || columnIndex == 1);
    }

    public void reloadAllAuthors(List<Author> newAuthors) {
        if (newAuthors != null) {
            authors.clear();
            fireTableDataChanged();
            for (var author : newAuthors) {
                if (!contains(author.id)) {
                    addRow(author);
                }
            }
        }
        editable = false;
    }

    public void loadNewAuthors(List<Author> newAuthors) {
        if (newAuthors != null) {
            for (var author : newAuthors) {
                if (!contains(author.id)) {
                    addRow(author);
                }
            }
        }
    }

    void addRow(Author author) {
        authors.add(author);
    }

    void setHiddenState(int row) {
        authors.get(row).isHidden = !authors.get(row).isHidden;
    }

    int getHiddenState(int row) {
        Author author = authors.get(row);
        if (author.isHidden) {
            return 1;
        }
        return 0;
    }
}

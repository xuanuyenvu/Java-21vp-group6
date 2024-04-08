package com.group06.bsms.categories;

import com.group06.bsms.categories.*;
import static com.group06.bsms.Main.app;
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
        CategoryTableModel model = (CategoryTableModel) table.getModel();
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

        int isHidden = ((CategoryTableModel) table.getModel()).getHiddenState(modelRow);

        ActionBtn action = new ActionBtn(isHidden);
        action.setBackground(Color.WHITE);

        return action;
    }
}

/**
 * A custom structure used to display a table
 */
public class CategoryTableModel extends AbstractTableModel {

    private List<Category> categories = new ArrayList<>();
    private String[] columns = {"Name", "Actions"};
    public boolean editable = false;
    private final CategoryService categoryService;

    public CategoryTableModel(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public int getRowCount() {
        return categories.size();
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
        if (row >= categories.size()) {
            return null;
        }
        Category category = categories.get(row);
        switch (col) {
            case 0:
                return category.name;
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

        Category category = categories.get(row);
        switch (col) {
            case 0:
                if (!category.name.equals((String) val)) {
                    try {
                        categoryService.updateCategoryAttributeById(category.id, "name", (String) val);
                        category.name = (String) val;
                    } catch (Exception e) {
                        if (e.getMessage().contains("category_name_key")) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "A category with this name already exists",
                                    "BSMS Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        } else {
                            JOptionPane.showMessageDialog(
                                    app,
                                    "An error has occurred: " + e.getMessage(),
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

    public Category getCategory(int row) {
        return categories.get(row);
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    public boolean contains(int id) {
        Optional<Category> foundCategory = categories.stream()
                .filter(category -> category.id == id)
                .findFirst();
        return foundCategory.isPresent();
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

    public void reloadAllCategories(List<Category> newCategories) {
        if (newCategories != null) {
            categories.clear();
            fireTableDataChanged();
            for (var category : newCategories) {
                if (!contains(category.id)) {
                    addRow(category);
                }
            }
        }
        editable = false;
    }

    public void loadNewCategories(List<Category> newCategories) {
        if (newCategories != null) {
            for (var category : newCategories) {
                if (!contains(category.id)) {
                    addRow(category);
                }
            }
        }
    }

    void addRow(Category category) {
        categories.add(category);
    }

    void setHiddenState(int row) {
        categories.get(row).isHidden = !categories.get(row).isHidden;
    }

    int getHiddenState(int row) {
        Category category = categories.get(row);
        if (category.isHidden) {
            return 1;
        }
        return 0;
    }
}

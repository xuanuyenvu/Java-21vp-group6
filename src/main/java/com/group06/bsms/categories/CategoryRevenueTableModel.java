package com.group06.bsms.categories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.table.AbstractTableModel;

public class CategoryRevenueTableModel extends AbstractTableModel {

    private List<Category> categories = new ArrayList<>();
    private String[] columns = {"Name", "Revenue"};
    private final CategoryService categoryService;

    public CategoryRevenueTableModel(CategoryService categoryService) {
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
                return ((category.name == null) ? "" : category.name);
            case 1:
                return ((category.revenue == null) ? "" : category.revenue);
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
                return Double.class;
            default:
                return null;
        }
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
}

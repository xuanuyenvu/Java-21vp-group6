package com.group06.bsms.importsheet;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import javax.swing.table.AbstractTableModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ImportSheetTableModel extends AbstractTableModel {
    private List<ImportSheet> importSheets = new ArrayList<>();
    private String[] columns = { "Employee", "Import Date", "Total Cost", "Actions" };
    private final ImportSheetService importSheetService;

    public ImportSheetTableModel(ImportSheetService importSheetService) {
        this.importSheetService = importSheetService;
    }

    @Override
    public int getRowCount() {
        return importSheets.size();
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
        if (row >= importSheets.size()) {
            return null;
        }
        ImportSheet importSheet = importSheets.get(row);
        switch (col) {
            case 0:
                return importSheet.employeeInChargeId;
            case 1:
                return importSheet.importDate;
            case 2:
                return importSheet.totalCost;
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object val, int row, int col) {

    }

    public ImportSheet getImportSheet(int row) {
        return importSheets.get(row);
    }

    public String getColumnName(int col) {
        return columns[col];
    }

    public boolean contains(int id) {
        Optional<ImportSheet> foundBook = importSheets.stream()
                .filter(importSheet -> importSheet.id == id)
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
        return false;
    }

    public void reloadAllImportSheets(List<ImportSheet> newImportSheets) {
        if (newImportSheets != null) {
            importSheets.clear();
            fireTableDataChanged();
            for (var importSheet : newImportSheets) {
                if (!contains(importSheet.id)) {
                    addRow(importSheet);
                }
            }
        }

    }

    public void loadNewBooks(List<ImportSheet> newImportSheets) {
        if (newImportSheets != null) {
            for (var importSheet : newImportSheets) {
                if (!contains(importSheet.id)) {
                    addRow(importSheet);
                }
            }
        }
    }

    private void addRow(ImportSheet importSheet) {
        importSheets.add(importSheet);
    }

}

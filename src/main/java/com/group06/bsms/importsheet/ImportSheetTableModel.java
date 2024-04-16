package com.group06.bsms.importsheet;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import javax.swing.table.AbstractTableModel;

import java.util.ArrayList;
import java.util.List;

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
}

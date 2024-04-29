package com.group06.bsms.order;

import javax.swing.table.DefaultTableModel;

public class ImportedBooksTableModel extends DefaultTableModel {

    private boolean tableEnabled;

    public ImportedBooksTableModel() {
        super(new Object[][]{{"", "", ""}},
                new String[]{"Title", "Quantity", "Price per book"});
        this.tableEnabled = true; 
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return tableEnabled;
    }

    public void setTableEnabled(boolean enabled) {
        this.tableEnabled = enabled;
        fireTableStructureChanged(); 
    }
}

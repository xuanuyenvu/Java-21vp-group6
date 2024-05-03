package com.group06.bsms.order;

import javax.swing.table.DefaultTableModel;

public class OrderedBooksTableModel extends DefaultTableModel {

    private boolean tableEnabled;

    public OrderedBooksTableModel() {
        super(new Object[][]{{"", "", ""}},
                new String[]{"Title", "Quantity", "Sale price"});
        this.tableEnabled = true; 
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 2;
    }

    public void setTableEnabled(boolean enabled) {
        this.tableEnabled = enabled;
        fireTableStructureChanged(); 
    }
}

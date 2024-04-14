package com.group06.bsms.importsheet;

import javax.swing.table.DefaultTableModel;

public class ImportSheetTableModel extends DefaultTableModel {

    public ImportSheetTableModel() {
        super(new Object[][]{{"", "", ""}},
                new String[]{"Title", "Quantity", "Price per book"});

    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }
}

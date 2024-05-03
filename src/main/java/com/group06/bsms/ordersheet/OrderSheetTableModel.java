package com.group06.bsms.ordersheet;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import com.group06.bsms.components.TableActionEvent;
import com.group06.bsms.components.UpdateActionBtn;
import java.awt.Color;
import java.awt.Component;
import java.text.SimpleDateFormat;
import javax.swing.table.AbstractTableModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

class DateCellRenderer extends DefaultTableCellRenderer {

    private static final SimpleDateFormat sdfTarget = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        if (value instanceof java.sql.Date) {
            value = sdfTarget.format((java.sql.Date) value);
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}

class TableActionCellEditor extends DefaultCellEditor {

    private final TableActionEvent event;

    public TableActionCellEditor(TableActionEvent event) {
        super(new JCheckBox());
        this.event = event;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        OrderSheetTableModel model = (OrderSheetTableModel) table.getModel();

        int modelRow = table.convertRowIndexToModel(row);

        UpdateActionBtn action = new UpdateActionBtn();
        action.initEvent(event, modelRow);

        action.setBackground(Color.WHITE);

        return action;
    }
}

class TableActionCellRender extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        UpdateActionBtn action = new UpdateActionBtn();
        action.setBackground(Color.WHITE);

        return action;
    }
}

public class OrderSheetTableModel extends AbstractTableModel {
    private List<OrderSheet> orderSheets = new ArrayList<>();
    private String[] columns = { "Employee", "Member", "Order Date", "Total Cost", "Actions"};
    private final OrderSheetService orderSheetService;

    public OrderSheetTableModel(OrderSheetService orderSheetService) {
        this.orderSheetService = orderSheetService;
    }

    @Override
    public int getRowCount() {
        return orderSheets.size();
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
        if (row >= orderSheets.size()) {
            return null;
        }
        OrderSheet orderSheet = orderSheets.get(row);
        return switch (col) {
            case 0 -> orderSheet.employee.phone;
            case 1 -> orderSheet.member.phone;
            case 2 -> orderSheet.orderDate;
            case 3 -> orderSheet.discountedTotalCost;
            default -> null;
        };
    }

    @Override
    public void setValueAt(Object val, int row, int col) {

    }

    public OrderSheet getOrderSheet(int row) {
        return orderSheets.get(row);
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    public boolean contains(int id) {
        Optional<OrderSheet> foundBook = orderSheets.stream()
                .filter(orderSheet -> orderSheet.id == id)
                .findFirst();
        return foundBook.isPresent();
    }

    @Override
    public Class<?> getColumnClass(int col) {
        return switch (col) {
            case 0 -> String.class;
            case 1 -> String.class;
            case 2 -> java.sql.Date.class;
            case 3 -> Double.class;
            default -> null;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 4;
    }

    public void reloadAllOrderSheets(List<OrderSheet> newOrderSheets) {
        if (newOrderSheets != null) {
            orderSheets.clear();
            fireTableDataChanged();
            for (var orderSheet : newOrderSheets) {
                if (!contains(orderSheet.id)) {
                    addRow(orderSheet);
                }
            }
        }

    }

    public void loadNewOrderSheets(List<OrderSheet> newOrderSheets) {
        if (newOrderSheets != null) {
            for (var orderSheet : newOrderSheets) {
                if (!contains(orderSheet.id)) {
                    addRow(orderSheet);
                }
            }
        }
    }

    private void addRow(OrderSheet orderSheet) {
        orderSheets.add(orderSheet);
    }

}

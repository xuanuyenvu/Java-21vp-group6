package com.group06.bsms.accounts;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.table.AbstractTableModel;

public class EmployeeRevenueTableModel extends AbstractTableModel {

    private List<Account> employees = new ArrayList<>();
    private String[] columns = {"Name", "Email", "Phone", "Address", "Gender", "Sale Quantity", "Revenue"};

    public EmployeeRevenueTableModel() {
    }

    @Override
    public int getRowCount() {
        return employees.size();
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
        if (row >= employees.size()) {
            return null;
        }
        Account employee = employees.get(row);
        switch (col) {
            case 0:
                return ((employee.name == null) ? "" : employee.name);
            case 1:
                return ((employee.email == null) ? "" : employee.email);
            case 2:
                return ((employee.phone == null) ? "" : employee.phone);
            case 3:
                return ((employee.address == null) ? "" : employee.address);
            case 4:
                return ((employee.gender == null) ? "" : employee.gender);
            case 5:
                return employee.revenue.saleQuantity;
            case 6:
                return ((employee.revenue.revenue == null) ? "" : employee.revenue.revenue);
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
    public Account getEmployee(int row) {
        return employees.get(row);
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    public boolean contains(int id) {
        Optional<Account> foundEmployee = employees.stream()
                .filter(employee -> employee.id == id)
                .findFirst();
        return foundEmployee.isPresent();
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
                return String.class;
            case 4:
                return String.class;
            case 5:
                return Integer.class;
            case 6:
                return Double.class;
            default:
                return null;
        }
    }

    public void reloadAllEmployees(List<Account> newEmployees) {
        if (newEmployees != null) {
            employees.clear();
            fireTableDataChanged();
            for (var employee : newEmployees) {
                if (!contains(employee.id)) {
                    addRow(employee);
                }
            }
        }
    }

    public void loadNewEmployees(List<Account> newEmployees) {
        if (newEmployees != null) {
            for (var employee : newEmployees) {
                if (!contains(employee.id)) {
                    addRow(employee);
                }
            }
        }
    }

    void addRow(Account employee) {
        employees.add(employee);
    }
}

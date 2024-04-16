package com.group06.bsms.accounts;

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
        AccountTableModel model = (AccountTableModel) table.getModel();
        int isHidden = model.getHiddenState(table.convertRowIndexToModel(row));
        int modelRow = table.convertRowIndexToModel(row);

        ActionBtn action = new ActionBtn(
                isHidden,
                "icons/lock.svg", "icons/unlock.svg",
                "Lock", "Unlock"
        );
        action.initEvent(event, modelRow, isHidden);

        action.setBackground(Color.WHITE);

        return action;
    }
}

class TableActionCellRender extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column
    ) {

        int modelRow = table.convertRowIndexToModel(row);

        int isHidden = ((AccountTableModel) table.getModel()).getHiddenState(modelRow);

        ActionBtn action = new ActionBtn(
                isHidden,
                "icons/lock.svg", "icons/unlock.svg",
                "Lock", "Unlock"
        );
        action.setBackground(Color.WHITE);

        return action;
    }
}

/**
 * A custom structure used to display a table
 */
public class AccountTableModel extends AbstractTableModel {

    private List<Account> accounts = new ArrayList<>();
    private String[] columns = {"Phone", "Name", "Email", "Address", "Gender", "Role", "Actions"};
    public boolean editable = false;
    private final AccountService accountService;
    private final BookCRUD bookCRUD;

    public AccountTableModel(AccountService accountService, BookCRUD bookCRUD) {
        this.bookCRUD = bookCRUD;
        this.accountService = accountService;
    }

    @Override
    public int getRowCount() {
        return accounts.size();
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
        if (row >= accounts.size()) {
            return null;
        }
        Account account = accounts.get(row);
        switch (col) {
            case 0:
                return account.phone;
            case 1:
                return account.name;
            case 2:
                return account.email;
            case 3:
                return account.address;
            case 4:
                return account.gender;
            case 5:
                return account.isAdmin ? "Admin" : "Employee";
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
        if (col == columns.length - 1) {
            return;
        }

        if (!editable) {
            editable = true;
        }

        Account account = accounts.get(row);
        try {
            switch (col) {
                case 0:
                    if (!account.phone.equals((String) val)) {
                        accountService.updateAccountAttributeById(account.id, "phone", (String) val);
                        account.phone = (String) val;
//                        bookCRUD.loadAccountInto();
                    }
                    break;
                case 1:
                    if (!((String) val).equals(account.name)) {
                        accountService.updateAccountAttributeById(
                                account.id,
                                "name",
                                ((String) val).equals("") ? null : (String) val
                        );
                        account.name = (String) val;

//                        bookCRUD.loadAccountInto();
                    }
                    break;
                case 2:
                    if (!((String) val).equals(account.email)) {
                        accountService.updateAccountAttributeById(
                                account.id,
                                "email",
                                ((String) val).equals("") ? null : (String) val
                        );
                        account.email = (String) val;

//                        bookCRUD.loadAccountInto();
                    }
                    break;
                case 3:
                    if (!((String) val).equals(account.address)) {
                        accountService.updateAccountAttributeById(
                                account.id,
                                "address",
                                ((String) val).equals("") ? null : (String) val
                        );
                        account.address = (String) val;

//                        bookCRUD.loadAccountInto();
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            if (ex.getMessage().contains("account_phone_check")) {
                JOptionPane.showMessageDialog(null, "Invalid phone format", "BSMS Error", JOptionPane.ERROR_MESSAGE);
            } else if (ex.getMessage().contains("account_phone_key")) {
                JOptionPane.showMessageDialog(null, "An account with this phone already exists", "BSMS Error", JOptionPane.ERROR_MESSAGE);
            } else if (ex.getMessage().contains("account_name_key")) {
                JOptionPane.showMessageDialog(null, "A account with this name already exists", "BSMS Error", JOptionPane.ERROR_MESSAGE);
            } else if (ex.getMessage().contains("account_name_check")) {
                JOptionPane.showMessageDialog(null, "Name cannot be empty", "BSMS Error", JOptionPane.ERROR_MESSAGE);
            } else if (ex.getMessage().contains("account_email_check")) {
                JOptionPane.showMessageDialog(null, "Invalid email format", "BSMS Error", JOptionPane.ERROR_MESSAGE);
            } else if (ex.getMessage().contains("account_email_key")) {
                JOptionPane.showMessageDialog(null, "A account with this email already exists", "BSMS Error", JOptionPane.ERROR_MESSAGE);
            } else if (ex.getMessage().contains("account_address_check")) {
                JOptionPane.showMessageDialog(null, "Address cannot be empty", "BSMS Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "BSMS Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        fireTableCellUpdated(row, col);
    }

    public Account getAccount(int row) {
        return accounts.get(row);
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    public boolean contains(int id) {
        Optional<Account> foundAccount = accounts.stream()
                .filter(account -> account.id == id)
                .findFirst();
        return foundAccount.isPresent();
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
                return String.class;
            case 6:
                return Boolean.class;
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return ((0 <= columnIndex && columnIndex <= 3) || columnIndex == 6);
    }

    public void reloadAllAccounts(List<Account> newAccounts) {
        if (newAccounts != null) {
            accounts.clear();
            fireTableDataChanged();
            for (var account : newAccounts) {
                if (!contains(account.id)) {
                    addRow(account);
                }
            }
        }
        editable = false;
    }

    public void loadNewAccounts(List<Account> newAccounts) {
        if (newAccounts != null) {
            for (var account : newAccounts) {
                if (!contains(account.id)) {
                    addRow(account);
                }
            }
        }
    }

    void addRow(Account account) {
        accounts.add(account);
    }

    void setHiddenState(int row) {
        accounts.get(row).isLocked = !accounts.get(row).isLocked;
    }

    int getHiddenState(int row) {
        Account account = accounts.get(row);
        if (account.isLocked) {
            return 1;
        }
        return 0;
    }
}

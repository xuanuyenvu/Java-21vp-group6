/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.group06.bsms.importsheet;

import com.group06.bsms.DB;
import com.group06.bsms.accounts.AccountRepository;
import com.group06.bsms.authors.AuthorRepository;
import com.group06.bsms.authors.AuthorService;
import com.group06.bsms.books.Book;
import com.group06.bsms.books.BookRepository;
import javax.swing.table.*;
import com.group06.bsms.books.BookService;
import com.group06.bsms.categories.CategoryRepository;
import com.group06.bsms.categories.CategoryService;
import com.group06.bsms.publishers.PublisherRepository;
import com.group06.bsms.publishers.PublisherService;
import com.group06.bsms.components.CustomTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.event.TableModelEvent;
import javax.swing.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TableModelListener;

public class AddImportSheet extends javax.swing.JPanel {

    /**
     * Creates new form importSheetUI
     */
    private BookService bookService;
    private ImportSheetService importSheetService;
    private Map<String, Book> bookMap;
    private boolean isSettingValue = false;

    public AddImportSheet() {

        this(new BookService(
                new BookRepository(DB.db()),
                new AuthorService(new AuthorRepository(DB.db())),
                new PublisherService(new PublisherRepository(DB.db()))),
                new ImportSheetService(
                        new ImportSheetRepository(DB.db(), new BookRepository(DB.db()), new AccountRepository(new DB.db()))));

    }

    public AddImportSheet(BookService bookService, ImportSheetService importSheetService) {

        this.bookService = bookService;
        this.importSheetService = importSheetService;
        this.bookMap = new HashMap<>();

        initComponents();

        importBooksTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
        importBooksTable.getActionMap().put("enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int editingRow = importBooksTable.getEditingRow();
                int editingColumn = importBooksTable.getEditingColumn();
                int rowCount = importBooksTable.getRowCount();
                if (editingColumn == 2) {
                    if (editingRow == rowCount - 1) {
                        DefaultTableModel model = (DefaultTableModel) importBooksTable.getModel();
                        model.addRow(new Object[model.getColumnCount()]);
                    }

                    importBooksTable.editCellAt(editingRow + 1, 0);
                    Rectangle cellRect = importBooksTable.getCellRect(editingRow + 1, 0, true);
                    importBooksTable.scrollRectToVisible(cellRect);
                    importBooksTable.requestFocusInWindow();

                }
            }
        });

        DefaultTableModel model = (DefaultTableModel) importBooksTable.getModel();
        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    updateTotalCost();
                }
            }
        });

        importBooksTable.setDefaultRenderer(Object.class, new CustomTableCellRenderer());

        importBooksTable.getColumnModel().getColumn(0).setCellEditor(new AutoSuggestComboBoxEditor());

        importBooksTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField() {
            {
                addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent evt) {
                        char inputChar = evt.getKeyChar();

                        if (Character.isLetter(inputChar)) {
                            setEditable(false);
                        } else {
                            setEditable(true);
                        }

                    }
                });
            }
        }));

        importBooksTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JTextField() {
            {
                addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent evt) {
                        char inputChar = evt.getKeyChar();
                        if (Character.isLetter(inputChar)) {
                            setEditable(false);
                        } else {
                            setEditable(true);
                        }

                    }
                });
            }
        }));

        importBooksTable.getModel().addTableModelListener((TableModelEvent e) -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                if (column == 0) {

                    if (!isSettingValue) {
                        String newTitle = (String) importBooksTable.getValueAt(row, column);
                        if (isDuplicateTitle(newTitle, row)) {

                            isSettingValue = true;
                            importBooksTable.setValueAt("", row, column);
                            isSettingValue = false;

                            JOptionPane.showMessageDialog(null, "There's already a " + newTitle + " row.", "BSMS Error",
                                    JOptionPane.ERROR_MESSAGE);
                            importBooksTable.requestFocusInWindow();

                        }
                    }
                }
                if (column == 1) {

                    if (!isSettingValue) {
                        String newTitle = (String) importBooksTable.getValueAt(row, column);
                        if (Integer.parseInt(newTitle) == 0) {

                            isSettingValue = true;
                            importBooksTable.setValueAt("", row, column);
                            isSettingValue = false;

                            JOptionPane.showMessageDialog(null, "Cannot have zero quantity", "BSMS Error",
                                    JOptionPane.ERROR_MESSAGE);
                            importBooksTable.requestFocusInWindow();

                        }
                    }
                }
            }
        });
    }

    public void setEmployeeId(String id) {
        employeeField.setText(id);
    }

    private void updateTotalCost() {
        double totalCost = 0.0;
        DefaultTableModel model = (DefaultTableModel) importBooksTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {

            String quantityStr = (String) model.getValueAt(i, 1);
            String pricePerBookStr = (String) model.getValueAt(i, 2);
            if (quantityStr != null && !quantityStr.trim().isEmpty()
                    && pricePerBookStr != null && !pricePerBookStr.trim().isEmpty()) {
                try {
                    double quantity = Double.parseDouble(quantityStr);
                    double pricePerBook = Double.parseDouble(pricePerBookStr);
                    totalCost += quantity * pricePerBook;
                } catch (NumberFormatException ex) {

                }
            }
        }
        totalCostField.setText(String.format("%.2f", totalCost));
    }

    private boolean isDuplicateTitle(String newTitle, int currentRow) {
        DefaultTableModel model = (DefaultTableModel) importBooksTable.getModel();
        int rowCount = model.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            if (i == currentRow) {
                continue;
            }
            String title = (String) model.getValueAt(i, 0);
            if (newTitle.equalsIgnoreCase(title)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        importSheetLabel = new javax.swing.JLabel();
        employeeLabel = new javax.swing.JLabel();
        employeeField = new javax.swing.JTextField();
        importDatePicker = new com.group06.bsms.components.DatePickerPanel();
        importDateLabel = new javax.swing.JLabel();
        totalCostField = new javax.swing.JTextField();
        totalCostLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        importBooksTable = new javax.swing.JTable();
        saveButton = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(944, 1503));

        importSheetLabel.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        importSheetLabel.setText("Add import sheet");

        employeeLabel.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_T);
        employeeLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        employeeLabel.setText("Employee");

        employeeField.setEditable(false);
        employeeField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        employeeField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        employeeField.setFocusable(false);
        employeeField.setMinimumSize(new java.awt.Dimension(440, 31));
        employeeField.setPreferredSize(new java.awt.Dimension(440, 31));

        importDatePicker.setMaximumSize(new java.awt.Dimension(215, 31));
        importDatePicker.setPlaceholder("dd/mm/yyyy");
        importDatePicker.setPreferredSize(new java.awt.Dimension(215, 31));

        importDateLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        importDateLabel.setText("Import Date");

        totalCostField.setEditable(false);
        totalCostField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        totalCostField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        totalCostField.setFocusable(false);
        totalCostField.setMinimumSize(new java.awt.Dimension(440, 31));
        totalCostField.setPreferredSize(new java.awt.Dimension(440, 31));

        totalCostLabel.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_T);
        totalCostLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        totalCostLabel.setText("Total cost");

        importBooksTable.setModel(new ImportedBooksTableModel());
        importBooksTable.setRowHeight(40);
        importBooksTable.setRowSelectionAllowed(false);
        jScrollPane1.setViewportView(importBooksTable);

        saveButton.setBackground(new java.awt.Color(65, 105, 225));
        saveButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        saveButton.setForeground(new java.awt.Color(255, 255, 255));
        saveButton.setMnemonic(java.awt.event.KeyEvent.VK_A);
        saveButton.setText("Save");
        saveButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(employeeField, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(employeeLabel)
                        .addComponent(importSheetLabel)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(importDateLabel)
                                .addComponent(importDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(totalCostLabel)
                                .addComponent(totalCostField, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 862, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(41, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(importSheetLabel)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(employeeLabel)
                        .addGap(2, 2, 2)
                        .addComponent(employeeField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(importDateLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(importDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(totalCostLabel)
                        .addGap(2, 2, 2)
                        .addComponent(totalCostField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(450, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private class AutoSuggestComboBoxEditor extends AbstractCellEditor implements TableCellEditor {

        private final JComboBox<String> comboBox = new JComboBox<>();
        private final Vector<String> suggestions = new Vector<>();

        public AutoSuggestComboBoxEditor() {
            comboBox.setEditable(true);
            JTextField textField = (JTextField) comboBox.getEditor().getEditorComponent();
            textField.addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    EventQueue.invokeLater(() -> {
                        String text = textField.getText();
                        if (text.length() == 0) {
                            comboBox.hidePopup();
                            setModel(new DefaultComboBoxModel<>(suggestions), "");
                        } else {

                            java.util.List<Book> books;
                            try {

                                books = bookService.searchBooksByTitle(text);
                            } catch (Exception ex) {
                                books = null;

                            }
                            suggestions.clear();
                            bookMap.clear();
                            if (books != null) {
                                for (var book : books) {
                                    suggestions.add(book.title);
                                    bookMap.put(book.title, book);
                                }
                            }
                            DefaultComboBoxModel<String> model = getSuggestedModel(suggestions, text);
                            if (model.getSize() == 0) {
                                comboBox.hidePopup();
                            } else {
                                setModel(model, text);
                                comboBox.showPopup();
                            }
                        }
                    });
                }
            });
            comboBox.setMaximumRowCount(10);
        }

        private void setModel(DefaultComboBoxModel<String> model, String str) {
            comboBox.setModel(model);
            comboBox.setSelectedIndex(-1);
            comboBox.getEditor().setItem(str);
        }

        private DefaultComboBoxModel<String> getSuggestedModel(java.util.List<String> list, String text) {
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            for (String s : list) {
                model.addElement(s);
            }
            return model;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            comboBox.setSelectedItem(value);
            return comboBox;
        }

        @Override
        public Object getCellEditorValue() {
            return comboBox.getEditor().getItem();
        }
    }

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveButtonActionPerformed
        // TODO add your handling code here:

        int employeeInChargeId;
        Date importDate;
        Double totalCost;

        boolean isTableValid = true;
        DefaultTableModel model = (DefaultTableModel) importBooksTable.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            String title = (String) model.getValueAt(i, 0);
            String quantityStr = (String) model.getValueAt(i, 1);
            String pricePerBookStr = (String) model.getValueAt(i, 2);

            if (!(title.equals("") && quantityStr.equals("") && pricePerBookStr.equals(""))) {
                if (title.equals("") || quantityStr.equals("") || pricePerBookStr.equals("")) {
                    System.out.println("hi");
                    isTableValid = false;
                    break;
                }
            }
        }

        if (isTableValid) {
            try {
                employeeInChargeId = Integer.parseInt(employeeField.getText());
                importDate = new java.sql.Date(importDatePicker.getDate().getTime());

                if (totalCostField.getText().isEmpty()) {
                    throw new Exception("Please input the books sheet");
                }
                totalCost = Double.valueOf(totalCostField.getText());

                List<ImportedBook> importedBooks = new ArrayList<>();

                for (int i = 0; i < model.getRowCount(); i++) {
                    String title = (String) model.getValueAt(i, 0);
                    String quantityStr = (String) model.getValueAt(i, 1);
                    String pricePerBookStr = (String) model.getValueAt(i, 2);
                    if ((title.equals("") && quantityStr.equals("") && pricePerBookStr.equals(""))) {
                        continue;
                    }
                    Book book = bookMap.get((String) model.getValueAt(i, 0));
                    if (book == null) {
                        throw new Exception("Cannot find book: " + (String) model.getValueAt(i, 0));
                    }
                    int bookId = book.id;
                    int quantity = Integer.parseInt((String) model.getValueAt(i, 1));
                    Double pricePerBook = Double.parseDouble((String) model.getValueAt(i, 2));

                    ImportedBook importedBook = new ImportedBook(bookId, title, quantity, pricePerBook);
                    importedBooks.add(importedBook);
                }

                ImportSheet importSheet = new ImportSheet(employeeInChargeId, importDate, totalCost, importedBooks);
                System.out.println(importSheet);

                try {
                    importSheetService.insertImportSheet(importSheet);
                    JOptionPane.showMessageDialog(null, "Import sheet added successfully.", "BSMS Information",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "An unspecified error occurred: " + e.getMessage(),
                            "BSMS Error",
                            JOptionPane.ERROR_MESSAGE);

                }
            } catch (Exception e) {

                JOptionPane.showMessageDialog(null, e.getMessage(), "BSMS Information",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please fill in all fields in the table.", "BSMS Information",
                    JOptionPane.ERROR_MESSAGE);
        }
    }// GEN-LAST:event_saveButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField employeeField;
    private javax.swing.JLabel employeeLabel;
    private javax.swing.JTable importBooksTable;
    private javax.swing.JLabel importDateLabel;
    private com.group06.bsms.components.DatePickerPanel importDatePicker;
    private javax.swing.JLabel importSheetLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextField totalCostField;
    private javax.swing.JLabel totalCostLabel;
    // End of variables declaration//GEN-END:variables
}

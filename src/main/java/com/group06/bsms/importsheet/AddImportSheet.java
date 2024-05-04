package com.group06.bsms.importsheet;

import com.group06.bsms.DB;
import com.group06.bsms.Main;
import com.group06.bsms.accounts.Account;
import com.group06.bsms.accounts.AccountRepository;
import com.group06.bsms.accounts.AccountService;
import com.group06.bsms.authors.AuthorRepository;
import com.group06.bsms.authors.AuthorService;
import com.group06.bsms.books.Book;
import com.group06.bsms.books.BookRepository;
import javax.swing.table.*;
import com.group06.bsms.books.BookService;
import com.group06.bsms.publishers.PublisherRepository;
import com.group06.bsms.publishers.PublisherService;
import com.group06.bsms.components.CustomTableCellRenderer;
import com.group06.bsms.dashboard.Dashboard;
import com.group06.bsms.utils.SVGHelper;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddImportSheet extends javax.swing.JPanel {

    /**
     * Creates new form importSheetUI
     */
    private BookService bookService;
    private AccountService accountService;
    private ImportSheetService importSheetService;
    private Map<String, Book> bookMap;
    private boolean isSettingValue = false;
    private Account employee;

    private ImportSheetCRUD importSheetCRUD;

    public void setImportSheetCRUD(ImportSheetCRUD importSheetCRUD) {
        this.importSheetCRUD = importSheetCRUD;
    }

    public AddImportSheet() {

        this(new BookService(
                new BookRepository(DB.db()),
                new AuthorService(new AuthorRepository(DB.db())),
                new PublisherService(new PublisherRepository(DB.db()))),
                new ImportSheetService(
                        new ImportSheetRepository(DB.db(), new BookRepository(DB.db()),
                                new AccountRepository(DB.db()))),
                new AccountService(new AccountRepository(DB.db())));

    }

    public AddImportSheet(BookService bookService, ImportSheetService importSheetService,
            AccountService accountService) {

        this.bookService = bookService;
        this.accountService = accountService;
        this.importSheetService = importSheetService;
        this.bookMap = new HashMap<>();
        this.loadEmployee(Main.getUserId());

        initComponents();

        this.importDatePicker.setDate(java.sql.Date.valueOf(LocalDate.now()));
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
                    importBooksTable.changeSelection(editingRow + 1, 0, false, false);
                    importBooksTable.editCellAt(editingRow + 1, 0);
                    importBooksTable.transferFocus();
                }
            }
        });

        DefaultTableModel model = (DefaultTableModel) importBooksTable.getModel();
        model.addTableModelListener((TableModelEvent e) -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                updateTotalCost();
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
                        String newQuantityStr = (String) importBooksTable.getValueAt(row, column);
                        try {
                            int newQuantity = Integer.parseInt(newQuantityStr);
                            if (newQuantity == 0) {

                                isSettingValue = true;
                                importBooksTable.setValueAt("", row, column);
                                isSettingValue = false;

                                JOptionPane.showMessageDialog(null, "Cannot have zero quantity", "BSMS Error",
                                        JOptionPane.ERROR_MESSAGE);
                                importBooksTable.requestFocusInWindow();
                            }

                        } catch (NumberFormatException nfe) {

                        }

                    }
                }
            }
        });

        importBooksTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", 0, 16));
        importBooksTable.getTableHeader().setReorderingAllowed(false);
        importBooksTable.setShowVerticalLines(true);
    }

    public void loadEmployee(int id) {
        try {
            this.employee = accountService.selectAccount(id);
        } catch (Exception e) {
        }
        if (employee != null) {
            employeeField.setText(employee.name);
        }
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
            if (newTitle.equalsIgnoreCase(title) && !newTitle.equals("")) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleBar = new javax.swing.JPanel();
        backButton = new javax.swing.JButton();
        pageName = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        groupFieldPanel = new javax.swing.JPanel();
        totalCostField = new javax.swing.JTextField();
        totalCostLabel = new javax.swing.JLabel();
        importBookScrollPane = new javax.swing.JScrollPane();
        importBooksTable = new javax.swing.JTable();
        saveButton = new javax.swing.JButton();
        employeeLabel = new javax.swing.JLabel();
        employeeField = new javax.swing.JTextField();
        importDatePicker = new com.group06.bsms.components.DatePickerPanel();
        importDateLabel = new javax.swing.JLabel();
        removeAllBtn = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(849, 661));
        setLayout(new java.awt.BorderLayout());

        titleBar.setPreferredSize(new java.awt.Dimension(849, 57));

        backButton.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        backButton.setForeground(UIManager.getColor("mutedColor"));
        backButton.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/arrow-back.svg", 
            Color.white, Color.white,
            18, 18
        ));
        backButton.setMnemonic(java.awt.event.KeyEvent.VK_BACK_SPACE);
        backButton.setToolTipText("Back to previous page");
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        backButton.setFocusable(false);
        backButton.setMargin(new java.awt.Insets(4, 14, 3, 14));
        backButton.setPreferredSize(new java.awt.Dimension(33, 33));
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                backButtonMouseExited(evt);
            }
        });
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        pageName.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        pageName.setText("Add import sheet");

        javax.swing.GroupLayout titleBarLayout = new javax.swing.GroupLayout(titleBar);
        titleBar.setLayout(titleBarLayout);
        titleBarLayout.setHorizontalGroup(
            titleBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(titleBarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pageName)
                .addContainerGap(648, Short.MAX_VALUE))
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        titleBarLayout.setVerticalGroup(
            titleBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(titleBarLayout.createSequentialGroup()
                .addGroup(titleBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(titleBarLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(pageName))
                    .addGroup(titleBarLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        add(titleBar, java.awt.BorderLayout.PAGE_START);

        totalCostField.setEditable(false);
        totalCostField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        totalCostField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        totalCostField.setFocusable(false);
        totalCostField.setMinimumSize(new java.awt.Dimension(440, 31));
        totalCostField.setPreferredSize(new java.awt.Dimension(440, 31));

        totalCostLabel.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_T);
        totalCostLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        totalCostLabel.setText("Total cost");

        importBooksTable.setModel(new com.group06.bsms.importsheet.ImportedBooksTableModel());
        importBooksTable.setRowHeight(40);
        importBooksTable.setRowSelectionAllowed(false);
        importBookScrollPane.setViewportView(importBooksTable);

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

        removeAllBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        removeAllBtn.setForeground(UIManager.getColor("mutedColor")
        );
        removeAllBtn.setMnemonic(java.awt.event.KeyEvent.VK_L);
        removeAllBtn.setText("Clear");
        removeAllBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        removeAllBtn.setDisplayedMnemonicIndex(1);
        removeAllBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAllBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout groupFieldPanelLayout = new javax.swing.GroupLayout(groupFieldPanel);
        groupFieldPanel.setLayout(groupFieldPanelLayout);
        groupFieldPanelLayout.setHorizontalGroup(
            groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(removeAllBtn)
                        .addGap(20, 20, 20)
                        .addComponent(saveButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, groupFieldPanelLayout.createSequentialGroup()
                        .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(employeeField, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(employeeLabel))
                        .addGap(18, 18, 18)
                        .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(importDateLabel)
                            .addComponent(importDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(totalCostField, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(totalCostLabel))))
                    .addComponent(importBookScrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 757, Short.MAX_VALUE))
                .addGap(50, 50, 50))
        );
        groupFieldPanelLayout.setVerticalGroup(
            groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                        .addComponent(employeeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(employeeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(groupFieldPanelLayout.createSequentialGroup()
                            .addComponent(importDateLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(importDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(groupFieldPanelLayout.createSequentialGroup()
                            .addComponent(totalCostLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(totalCostField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(importBookScrollPane)
                .addGap(14, 14, 14)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton)
                    .addComponent(removeAllBtn))
                .addGap(50, 50, 50))
        );

        add(groupFieldPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void removeAllBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAllBtnActionPerformed
        DefaultTableModel model = (DefaultTableModel) importBooksTable.getModel();
        totalCostField.setText("");
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
        model.addRow(new Object[model.getColumnCount()]);
        importBooksTable.requestFocusInWindow();
        importBooksTable.changeSelection(0, 0, false, false);
    }//GEN-LAST:event_removeAllBtnActionPerformed

    private void backButtonMouseEntered(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_backButtonMouseEntered
        // TODO add your handling code here:
    }// GEN-LAST:event_backButtonMouseEntered

    private void backButtonMouseExited(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_backButtonMouseExited
        // TODO add your handling code here:
    }// GEN-LAST:event_backButtonMouseExited

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_backButtonActionPerformed
        Dashboard.dashboard.switchTab("importSheetCRUD");
    }// GEN-LAST:event_backButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveButtonActionPerformed

        int employeeInChargeId;
        Date importDate;
        Double totalCost;

        boolean isTableValid = true;
        DefaultTableModel model = (DefaultTableModel) importBooksTable.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            String title = (String) model.getValueAt(i, 0);
            String quantityStr = (String) model.getValueAt(i, 1);
            String pricePerBookStr = (String) model.getValueAt(i, 2);

            if (!((title == null || title.equals(""))
                    && (quantityStr == null || quantityStr.equals(""))
                    && (pricePerBookStr == null || pricePerBookStr.equals("")))) {
                if (((title == null || title.equals(""))
                        || (quantityStr == null || quantityStr.equals(""))
                        || (pricePerBookStr == null || pricePerBookStr.equals("")))) {
                    isTableValid = false;
                    break;
                }
            }

        }

        if (isTableValid) {
            try {
                employeeInChargeId = employee.id;
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
                    if (((title == null || title.equals(""))
                            && (quantityStr == null || quantityStr.equals(""))
                            && (pricePerBookStr == null || pricePerBookStr.equals("")))) {
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

                try {
                    importSheetService.insertImportSheet(importSheet);
                    JOptionPane.showMessageDialog(null, "Import sheet added successfully.", "BSMS Information",
                            JOptionPane.INFORMATION_MESSAGE);
                    totalCostField.setText("");
                    while (model.getRowCount() > 0) {
                        model.removeRow(0);
                    }
                    model.addRow(new Object[model.getColumnCount()]);
                    importBooksTable.requestFocusInWindow();
                    importBooksTable.changeSelection(0, 0, false, false);

                } catch (Exception e) {
                    if (e.getMessage().contains("importedbook_priceperbook_check")) {
                        JOptionPane.showMessageDialog(null, "Price per book must be positive", "BSMS Error", JOptionPane.ERROR_MESSAGE);
                    } else if (e.getMessage().contains("importedbook_quantity_check")) {
                        JOptionPane.showMessageDialog(null, "Quantity must be positive", "BSMS Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, e.getMessage(), "BSMS Information",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception e) {
                if (e.getMessage().contains("importedbook_priceperbook_check")) {
                    JOptionPane.showMessageDialog(null, "Price per book must be positive", "BSMS Error", JOptionPane.ERROR_MESSAGE);
                } else if (e.getMessage().contains("importedbook_quantity_check")) {
                    JOptionPane.showMessageDialog(null, "Quantity must be positive", "BSMS Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "BSMS Information",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please fill in all fields in the table.", "BSMS Information",
                    JOptionPane.ERROR_MESSAGE);
        }

    }// GEN-LAST:event_saveButtonActionPerformed

    private class AutoSuggestComboBoxEditor extends AbstractCellEditor implements TableCellEditor {

        private final JComboBox<String> comboBox = new JComboBox<>();
        private final Vector<String> suggestions = new Vector<>();

        public AutoSuggestComboBoxEditor() {
            comboBox.setEditable(true);
            comboBox.setFont(new Font("Segoe UI", 0, 16));
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
            comboBox.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXX");

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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JTextField employeeField;
    private javax.swing.JLabel employeeLabel;
    private javax.swing.JPanel groupFieldPanel;
    private javax.swing.JScrollPane importBookScrollPane;
    private javax.swing.JTable importBooksTable;
    private javax.swing.JLabel importDateLabel;
    private com.group06.bsms.components.DatePickerPanel importDatePicker;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel pageName;
    private javax.swing.JButton removeAllBtn;
    private javax.swing.JButton saveButton;
    private javax.swing.JPanel titleBar;
    private javax.swing.JTextField totalCostField;
    private javax.swing.JLabel totalCostLabel;
    // End of variables declaration//GEN-END:variables
}

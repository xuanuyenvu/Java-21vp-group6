/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.group06.bsms.ordersheet;

import com.group06.bsms.DB;
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
import com.group06.bsms.components.DatePickerPanel;
import com.group06.bsms.dashboard.Dashboard;
import com.group06.bsms.members.MemberRepository;
import com.group06.bsms.members.MemberService;
import com.group06.bsms.members.Member;
import com.group06.bsms.utils.SVGHelper;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.event.TableModelEvent;
import javax.swing.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AddOrderSheet extends javax.swing.JPanel {

    /**
     * Creates new form importSheetUI
     */
    private BookService bookService;
    private AccountService accountService;
    private OrderSheetService orderSheetService;
    private MemberService memberService;
    private Map<String, Book> bookMap;
    private boolean isSettingValue = false;
    private Account employee;
    private Member member;
    private double discount = 0;

    private OrderSheetCRUD orderSheetCRUD;

    public void setOrderSheetCRUD(OrderSheetCRUD orderSheetCRUD) {
        this.orderSheetCRUD = orderSheetCRUD;
    }

    public DatePickerPanel getOrderDatePickerPanel() {
        return orderDatePicker;
    }

    public AddOrderSheet() {

        this(new BookService(
                new BookRepository(DB.db()),
                new AuthorService(new AuthorRepository(DB.db())),
                new PublisherService(new PublisherRepository(DB.db()))),
                new OrderSheetService(
                        new OrderSheetRepository(DB.db(), new AccountRepository(DB.db()),
                                new MemberRepository(DB.db()))),
                new AccountService(new AccountRepository(DB.db())),
                new MemberService(new MemberRepository(DB.db())));

    }

    public AddOrderSheet(BookService bookService, OrderSheetService orderSheetService, AccountService accountService,
            MemberService memberService) {

        this.bookService = bookService;
        this.accountService = accountService;
        this.orderSheetService = orderSheetService;
        this.memberService = memberService;
        this.bookMap = new HashMap<>();

        initComponents();

        orderDatePicker.setDate(new java.util.Date());
        var sm = new SimpleDateFormat("dd/MM/yyyy");
        orderDatePicker.setText(sm.format(orderDatePicker.getDate()));

        orderBookTable.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
        orderBookTable.getActionMap().put("enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int editingRow = orderBookTable.getEditingRow();
                int editingColumn = orderBookTable.getEditingColumn();
                int rowCount = orderBookTable.getRowCount();
                if (editingColumn == 2) {
                    if (editingRow == rowCount - 1) {
                        DefaultTableModel model = (DefaultTableModel) orderBookTable.getModel();
                        model.addRow(new Object[model.getColumnCount()]);
                    }
                    orderBookTable.changeSelection(editingRow + 1, 0, false, false);
                    orderBookTable.editCellAt(editingRow + 1, 0);
                    orderBookTable.transferFocus();
                }
            }
        });

        DefaultTableModel model = (DefaultTableModel) orderBookTable.getModel();
        model.addTableModelListener((TableModelEvent e) -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                updateTotalCost();
            }
        });

        orderBookTable.setDefaultRenderer(Object.class, new CustomTableCellRenderer());

        orderBookTable.getColumnModel().getColumn(0).setCellEditor(new AutoSuggestComboBoxEditor());

        orderBookTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField() {
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

        orderBookTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JTextField() {
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

        orderBookTable.getModel().addTableModelListener((TableModelEvent e) -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                if (column == 0) {

                    if (!isSettingValue) {
                        String newTitle = (String) orderBookTable.getValueAt(row, column);
                        if (isDuplicateTitle(newTitle, row)) {

                            isSettingValue = true;
                            orderBookTable.setValueAt("", row, column);
                            isSettingValue = false;

                            JOptionPane.showMessageDialog(null, "There's already a " + newTitle + " row.", "BSMS Error",
                                    JOptionPane.ERROR_MESSAGE);
                            orderBookTable.requestFocusInWindow();

                        }
                    }
                }
                if (column == 1) {

                    if (!isSettingValue) {
                        String newQuantityStr = (String) orderBookTable.getValueAt(row, column);
                        try {
                            int newQuantity = Integer.parseInt(newQuantityStr);
                            if (newQuantity == 0) {

                                isSettingValue = true;
                                orderBookTable.setValueAt("", row, column);
                                isSettingValue = false;

                                JOptionPane.showMessageDialog(null, "Cannot have zero quantity", "BSMS Error",
                                        JOptionPane.ERROR_MESSAGE);
                                orderBookTable.requestFocusInWindow();
                            }

                        } catch (NumberFormatException nfe) {

                        }

                    }
                }
            }
        });

        orderBookTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", 0, 16));
        orderBookTable.getTableHeader().setReorderingAllowed(false);
        orderBookTable.setShowVerticalLines(true);
    }

    public void loadEmployee(int id) {
        try {
            this.employee = accountService.selectAccount(id);
            employeeField.setText(employee.name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (employee != null) {
            employeeField.setText(employee.phone);
        }
    }

    public void loadMember(int id) {
        try {
            this.member = memberService.selectMember(id);
            memberNameField.setText(member.name);
            memberPhoneField.setText(member.phone);
            if (member.name.equals("Anonymous")) {
                discountField.setText("0%");
            } else {
                discountField.setText("5%");
                discount = 0.05;
            }
        } catch (Exception e) {

        }
    }

    private void updateTotalCost() {
        double totalCost = 0.0;
        DefaultTableModel model = (DefaultTableModel) orderBookTable.getModel();
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
        totalCostField.setText(String.format("%.2f", totalCost * (1 - discount)));
    }

    private boolean isDuplicateTitle(String newTitle, int currentRow) {
        DefaultTableModel model = (DefaultTableModel) orderBookTable.getModel();
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
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        formScrollPane = new javax.swing.JScrollPane();
        groupFieldPanel = new javax.swing.JPanel();
        totalCostField = new javax.swing.JTextField();
        totalCostLabel = new javax.swing.JLabel();
        orderBookScrollPane = new javax.swing.JScrollPane();
        orderBookTable = new javax.swing.JTable();
        saveButton = new javax.swing.JButton();
        employeeLabel = new javax.swing.JLabel();
        employeeField = new javax.swing.JTextField();
        orderDatePicker = new com.group06.bsms.components.DatePickerPanel();
        orderDateLabel = new javax.swing.JLabel();
        memberNameLabel = new javax.swing.JLabel();
        memberNameField = new javax.swing.JTextField();
        memberPhoneLabel = new javax.swing.JLabel();
        memberPhoneField = new javax.swing.JTextField();
        discountField = new javax.swing.JTextField();
        discountLabel = new javax.swing.JLabel();
        titleBar = new javax.swing.JPanel();
        backButton = new javax.swing.JButton();
        pageName = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();

        setPreferredSize(new java.awt.Dimension(849, 661));
        setLayout(new java.awt.BorderLayout());

        formScrollPane.setBorder(null);
        formScrollPane.setPreferredSize(new java.awt.Dimension(893, 661));

        totalCostField.setEditable(false);
        totalCostField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        totalCostField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        totalCostField.setFocusable(false);
        totalCostField.setMinimumSize(new java.awt.Dimension(440, 31));
        totalCostField.setPreferredSize(new java.awt.Dimension(440, 31));

        totalCostLabel.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_T);
        totalCostLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        totalCostLabel.setText("Total sale price");

        orderBookTable.setModel(new com.group06.bsms.ordersheet.OrderedBooksTableModel());
        orderBookTable.setRowHeight(40);
        orderBookTable.setRowSelectionAllowed(false);
        orderBookScrollPane.setViewportView(orderBookTable);

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

        orderDatePicker.setMaximumSize(new java.awt.Dimension(215, 31));
        orderDatePicker.setPlaceholder("dd/mm/yyyy");
        orderDatePicker.setPreferredSize(new java.awt.Dimension(215, 31));

        orderDateLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        orderDateLabel.setText("Order Date");

        memberNameLabel.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_T);
        memberNameLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        memberNameLabel.setText("Member");

        memberNameField.setEditable(false);
        memberNameField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        memberNameField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        memberNameField.setFocusable(false);
        memberNameField.setMinimumSize(new java.awt.Dimension(440, 31));
        memberNameField.setPreferredSize(new java.awt.Dimension(440, 31));
        memberNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memberNameFieldActionPerformed(evt);
            }
        });

        memberPhoneLabel.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_T);
        memberPhoneLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        memberPhoneLabel.setText("Member's phone");

        memberPhoneField.setEditable(false);
        memberPhoneField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        memberPhoneField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        memberPhoneField.setFocusable(false);
        memberPhoneField.setMinimumSize(new java.awt.Dimension(440, 31));
        memberPhoneField.setPreferredSize(new java.awt.Dimension(440, 31));
        memberPhoneField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memberPhoneFieldActionPerformed(evt);
            }
        });

        discountField.setEditable(false);
        discountField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        discountField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        discountField.setFocusable(false);
        discountField.setMinimumSize(new java.awt.Dimension(440, 31));
        discountField.setPreferredSize(new java.awt.Dimension(440, 31));

        discountLabel.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_T);
        discountLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        discountLabel.setText("Discount");

        javax.swing.GroupLayout groupFieldPanelLayout = new javax.swing.GroupLayout(groupFieldPanel);
        groupFieldPanel.setLayout(groupFieldPanelLayout);
        groupFieldPanelLayout.setHorizontalGroup(
            groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 778, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(saveButton))
                    .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(orderBookScrollPane)
                        .addGroup(groupFieldPanelLayout.createSequentialGroup()
                            .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                    .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(orderDateLabel)
                                        .addComponent(orderDatePicker, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(employeeField, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                            .addGap(19, 19, 19)
                                            .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(memberNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(memberNameLabel)))
                                        .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                            .addGap(18, 18, 18)
                                            .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(discountField, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                                    .addGap(1, 1, 1)
                                                    .addComponent(discountLabel))))))
                                .addComponent(employeeLabel))
                            .addGap(18, 18, 18)
                            .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(memberPhoneField, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(memberPhoneLabel)
                                .addComponent(totalCostField, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                    .addGap(1, 1, 1)
                                    .addComponent(totalCostLabel)))
                            .addGap(0, 0, Short.MAX_VALUE))))
                .addGap(42, 42, 42))
        );
        groupFieldPanelLayout.setVerticalGroup(
            groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                        .addComponent(employeeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(employeeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                        .addComponent(memberNameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(memberNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                        .addComponent(memberPhoneLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(memberPhoneField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(groupFieldPanelLayout.createSequentialGroup()
                            .addComponent(orderDateLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(orderDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(groupFieldPanelLayout.createSequentialGroup()
                            .addComponent(totalCostLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(totalCostField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                        .addComponent(discountLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(discountField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(orderBookScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(saveButton)
                .addGap(943, 943, 943))
        );

        formScrollPane.setViewportView(groupFieldPanel);

        add(formScrollPane, java.awt.BorderLayout.CENTER);

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
        pageName.setText("Add order sheet");

        javax.swing.GroupLayout titleBarLayout = new javax.swing.GroupLayout(titleBar);
        titleBar.setLayout(titleBarLayout);
        titleBarLayout.setHorizontalGroup(
            titleBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(titleBarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pageName)
                .addContainerGap(838, Short.MAX_VALUE))
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
    }// </editor-fold>//GEN-END:initComponents

    private void memberNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memberNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_memberNameFieldActionPerformed

    private void memberPhoneFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memberPhoneFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_memberPhoneFieldActionPerformed

    private void backButtonMouseEntered(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_backButtonMouseEntered
        // TODO add your handling code here:
    }// GEN-LAST:event_backButtonMouseEntered

    private void backButtonMouseExited(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_backButtonMouseExited
        // TODO add your handling code here:
    }// GEN-LAST:event_backButtonMouseExited

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_backButtonActionPerformed
        Dashboard.dashboard.switchTab("memberCRUD");
    }// GEN-LAST:event_backButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveButtonActionPerformed

        int employeeInChargeId, memberId;
        Date orderDate;
        Double totalCost;

        boolean isTableValid = true;
        DefaultTableModel model = (DefaultTableModel) orderBookTable.getModel();

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
                orderDate = new java.sql.Date(orderDatePicker.getDate().getTime());
                memberId = member.id;

                if (totalCostField.getText().isEmpty()) {
                    throw new Exception("Please input the books sheet");
                }
                totalCost = Double.valueOf(totalCostField.getText());

                List<OrderedBook> orderedBooks = new ArrayList<>();

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

                    OrderedBook orderedBook = new OrderedBook(bookId, title, quantity, pricePerBook);
                    orderedBooks.add(orderedBook);
                }

                OrderSheet orderSheet = new OrderSheet(employeeInChargeId, memberId, orderDate, totalCost,
                        orderedBooks);

                try {
                    orderSheetService.insertOrderSheet(orderSheet);
                    JOptionPane.showMessageDialog(null, "Order sheet added successfully.", "BSMS Information",
                            JOptionPane.INFORMATION_MESSAGE);
                            totalCostField.setText("");
                            while (model.getRowCount() > 0) {
                                model.removeRow(0);
                            }
                            model.addRow(new Object[model.getColumnCount()]);
                            orderBookTable.requestFocusInWindow();
                            orderBookTable.changeSelection(model.getRowCount() - 1, 0, false, false);
                            
                    
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

                                books = bookService.searchAvailableBooksByTitle(text);

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
            comboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    String selectedBookTitle = (String) comboBox.getSelectedItem();
                    if (selectedBookTitle != null) {
                        Book selectedBook = bookMap.get(selectedBookTitle);
                        if (selectedBook != null) {
                            int row = orderBookTable.getEditingRow();

                            orderBookTable.setValueAt(selectedBook.salePrice.toString(), row, 2);
                            DefaultTableModel model = (DefaultTableModel) orderBookTable.getModel();
                            model.addRow(new Object[model.getColumnCount()]);
                        }
                    }
                }
            });

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
    private javax.swing.JTextField discountField;
    private javax.swing.JLabel discountLabel;
    private javax.swing.JTextField employeeField;
    private javax.swing.JLabel employeeLabel;
    private javax.swing.JScrollPane formScrollPane;
    private javax.swing.JPanel groupFieldPanel;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField memberNameField;
    private javax.swing.JLabel memberNameLabel;
    private javax.swing.JTextField memberPhoneField;
    private javax.swing.JLabel memberPhoneLabel;
    private javax.swing.JScrollPane orderBookScrollPane;
    private javax.swing.JTable orderBookTable;
    private javax.swing.JLabel orderDateLabel;
    private com.group06.bsms.components.DatePickerPanel orderDatePicker;
    private javax.swing.JLabel pageName;
    private javax.swing.JButton saveButton;
    private javax.swing.JPanel titleBar;
    private javax.swing.JTextField totalCostField;
    private javax.swing.JLabel totalCostLabel;
    // End of variables declaration//GEN-END:variables
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.group06.bsms.importsheet;

import com.formdev.flatlaf.FlatClientProperties;
import com.group06.bsms.DB;
import com.group06.bsms.Main;
import com.group06.bsms.accounts.Account;
import com.group06.bsms.accounts.AccountRepository;
import com.group06.bsms.books.BookRepository;
import com.group06.bsms.utils.SVGHelper;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

public class ImportSheetCRUD extends javax.swing.JPanel {

    private final ImportSheetService importSheetService;
    private ImportSheetTableModel model;
    private Map<Integer, SortOrder> columnSortOrders = new HashMap<>();
    private int currentOffset = 0;
    //private final UpdateAccount updateAccount;
    //private final AddAccountInformation addAccountInfo;
    //private final BookCRUD bookCRUD;

    public void setCurrentOffset(int currentOffset) {
        this.currentOffset = currentOffset;
    }

    private final int limit = Main.ROW_LIMIT;
    private boolean isScrollAtBottom = false;

    public ImportSheetCRUD() {
        this(
                new ImportSheetService(new ImportSheetRepository(DB.db(), new BookRepository(DB.db()), new AccountRepository(DB.db())))
        );

    }

    public ImportSheetCRUD(ImportSheetService importSheetService) {
        this.importSheetService = importSheetService;
        this.model = new ImportSheetTableModel(importSheetService);

        initComponents();

        searchBar.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search");
        searchBar.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, SVGHelper.createSVGIconWithFilter(
                "icons/search.svg",
                Color.black, Color.black,
                14, 14
        ));

        setUpTable();

        loadImportSheetsIntoTable();
    }

    private void toggleSortOrder(int columnIndex) {
        if (columnIndex < 3) {
            SortOrder currentOrder = columnSortOrders.getOrDefault(columnIndex, SortOrder.UNSORTED);
            SortOrder newOrder = currentOrder == SortOrder.ASCENDING ? SortOrder.DESCENDING : SortOrder.ASCENDING;
            columnSortOrders.clear();
            columnSortOrders.put(columnIndex, newOrder);
        }
    }

    class CustomHeaderRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int modelColumn = table.convertColumnIndexToModel(column);
            SortOrder sortOrder = columnSortOrders.getOrDefault(modelColumn, SortOrder.UNSORTED);
            Icon sortIcon = null;
            if (column != 6) {
                if (sortOrder == SortOrder.ASCENDING) {
                    sortIcon = UIManager.getIcon("Table.descendingSortIcon");
                } else if (sortOrder == SortOrder.DESCENDING) {
                    sortIcon = UIManager.getIcon("Table.ascendingSortIcon");
                }
                setHorizontalAlignment(JLabel.LEFT);
            } else {
                setHorizontalAlignment(JLabel.CENTER);
                sortIcon = null;
            }
            setHorizontalTextPosition(JLabel.LEFT);
            label.setIcon(sortIcon);
            return label;
        }
    }

    public void setUpTable() {

        table.getColumnModel().getColumn(1).setCellRenderer(new DateCellRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(new TableActionCellRender());

        table.getTableHeader().setFont(new java.awt.Font("Segoe UI", 0, 16));
        table.setShowVerticalLines(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        columnSortOrders.put(0, SortOrder.ASCENDING);

        table.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());

        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnIndex = table.columnAtPoint(e.getPoint());
                toggleSortOrder(columnIndex);
                reloadImportSheets();
                table.getTableHeader().repaint();
            }
        });

        scrollBar.getVerticalScrollBar().addAdjustmentListener(e -> {
            if (!e.getValueIsAdjusting()) {
                //Check if scrolled to the bottom
                isScrollAtBottom
                        = e.getAdjustable().getMaximum() == e.getAdjustable().getValue() + e.getAdjustable().getVisibleAmount();
                if (isScrollAtBottom) {
                    loadImportSheetsIntoTable();
                }
            }
        });
    }

    public void reloadImportSheets() {
        reloadImportSheets(false);
    }

    public void reloadImportSheets(boolean reloadFilter) {
        currentOffset = 0;
        loadImportSheetsIntoTable();

        if (reloadFilter) {
//            bookCRUD.loadAccountInto();
        }
    }

    public void loadImportSheetsIntoTable() {
        var searchString
                = searchBar == null || searchBar.getText() == null
                ? ""
                : searchBar.getText();

        var searchChoiceKey = searchComboBox.getSelectedItem().toString();
        var searchChoiceMap = new HashMap<String, String>();
        searchChoiceMap.put("by Import Date", "ImportSheet.importDate");
        searchChoiceMap.put("by Employee's phone", "Account.phone");
        searchChoiceMap.put("by Total Cost", "ImportSheet.totalCost");
        var searchChoiceValue = searchChoiceMap.get(searchChoiceKey);

        try {
            int currentRowCount = 0;

            do {
                List<ImportSheet> importSheets = importSheetService.searchSortFilterImportSheets(
                        currentOffset, limit, columnSortOrders,
                        searchString, searchChoiceValue
                );

                if (currentOffset > 0) {
                    model.loadNewImportSheets(importSheets);
                } else {
                    model.reloadAllImportSheets(importSheets);
                }

                currentOffset += limit;

                if (currentRowCount == model.getRowCount()) {
                    break;
                }

                currentRowCount = model.getRowCount();
            } while (currentRowCount < 2 * limit);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "BSMS Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        importSheetLabel = new javax.swing.JLabel();
        searchBar = new javax.swing.JTextField();
        createBtn = new javax.swing.JButton();
        main = new javax.swing.JPanel();
        scrollBar = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        searchComboBox = new javax.swing.JComboBox<>();

        setMinimumSize(new java.awt.Dimension(928, 1503));

        importSheetLabel.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        importSheetLabel.setText("IMPORT SHEET");

        searchBar.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        searchBar.setFocusAccelerator('s');
        searchBar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBarActionPerformed(evt);
            }
        });

        createBtn.setBackground(UIManager.getColor("accentColor"));
        createBtn.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        createBtn.setForeground(new java.awt.Color(255, 255, 255));
        createBtn.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/add.svg",
            Color.black, Color.white, Color.white,
            14, 14
        ));
        createBtn.setMnemonic(java.awt.event.KeyEvent.VK_C);
        createBtn.setText("Create");
        createBtn.setToolTipText("");
        createBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        createBtn.setDisplayedMnemonicIndex(0);
        createBtn.setIconTextGap(2);
        createBtn.setMargin(new java.awt.Insets(10, 10, 10, 10));
        createBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createBtnActionPerformed(evt);
            }
        });

        main.setLayout(new java.awt.BorderLayout());

        table.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        table.setModel(this.model);
        table.setToolTipText("");
        table.setRowHeight(40);
        table.getTableHeader().setReorderingAllowed(false);
        scrollBar.setViewportView(table);

        main.add(scrollBar, java.awt.BorderLayout.CENTER);

        searchComboBox.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        searchComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "by Employee's phone", "by Import Date", "by Total Cost" }));
        searchComboBox.setPreferredSize(new java.awt.Dimension(154, 28));
        searchComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(importSheetLabel)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(main, javax.swing.GroupLayout.DEFAULT_SIZE, 836, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(searchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(searchComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(createBtn)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(50, 50, 50))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(importSheetLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(createBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(main, javax.swing.GroupLayout.DEFAULT_SIZE, 1299, Short.MAX_VALUE)
                .addGap(50, 50, 50))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void searchBarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBarActionPerformed
        reloadImportSheets();
    }//GEN-LAST:event_searchBarActionPerformed

    private void createBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createBtnActionPerformed
        //AdminDashboard.dashboard.switchTab("addAccountInformation");
    }//GEN-LAST:event_createBtnActionPerformed

    private void searchComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchComboBoxActionPerformed
        String choice = (String) searchComboBox.getSelectedItem();
        switch (choice) {
            case "by Employee's phone" ->
                searchBar.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search");
            case "by Import Date" ->
                searchBar.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "dd/MM/yyyy");
            case "by Total Cost 1" ->
                searchBar.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search");
            default -> {
            }
        }

    }//GEN-LAST:event_searchComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton createBtn;
    private javax.swing.JLabel importSheetLabel;
    private javax.swing.JPanel main;
    private javax.swing.JScrollPane scrollBar;
    private javax.swing.JTextField searchBar;
    private javax.swing.JComboBox<String> searchComboBox;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}

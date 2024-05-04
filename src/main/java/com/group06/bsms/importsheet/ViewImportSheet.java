package com.group06.bsms.importsheet;

import com.group06.bsms.DB;
import com.group06.bsms.accounts.AccountRepository;
import com.group06.bsms.authors.AuthorRepository;
import com.group06.bsms.authors.AuthorService;
import com.group06.bsms.books.Book;
import com.group06.bsms.books.BookRepository;
import javax.swing.table.*;
import com.group06.bsms.books.BookService;
import com.group06.bsms.components.CustomTableCellRenderer;
import com.group06.bsms.dashboard.Dashboard;
import com.group06.bsms.publishers.PublisherRepository;
import com.group06.bsms.publishers.PublisherService;
import com.group06.bsms.utils.SVGHelper;
import java.awt.Color;
import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.Map;

import java.util.List;
import javax.swing.UIManager;

public class ViewImportSheet extends javax.swing.JPanel {

    private BookService bookService;
    private ImportSheetService importSheetService;
    private Map<String, Book> bookMap;
    private ImportSheet importSheet;
    private ImportSheetCRUD importSheetCRUD;

    public void setImportSheetCRUD(ImportSheetCRUD importSheetCRUD) {
        this.importSheetCRUD = importSheetCRUD;
    }

    public ViewImportSheet() {

        this(new BookService(
                new BookRepository(DB.db()),
                new AuthorService(new AuthorRepository(DB.db())),
                new PublisherService(new PublisherRepository(DB.db()))),
                new ImportSheetService(
                        new ImportSheetRepository(DB.db(), new BookRepository(DB.db()), new AccountRepository(DB.db()))));

    }

    public ViewImportSheet(BookService bookService, ImportSheetService importSheetService) {

        this.bookService = bookService;
        this.importSheetService = importSheetService;
        this.bookMap = new HashMap<>();

        initComponents();

        importBooksTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", 0, 16));
        importBooksTable.setShowVerticalLines(true);
        importBooksTable.setDefaultRenderer(Object.class, new CustomTableCellRenderer());

        ((ImportedBooksTableModel) importBooksTable.getModel()).setTableEnabled(false);
    }

    public void loadImportSheet(int id) {
        try {
            
            importSheet = importSheetService.selectImportSheet(id);
            employeeField.setText(importSheet.employee.phone);
            SimpleDateFormat sdfTarget = new SimpleDateFormat("dd/MM/yyyy");
            String dateFormatted = sdfTarget.format(importSheet.importDate);
            importDateField.setText(dateFormatted);
            List<ImportedBook> importedBooks = importSheet.importedBooks;
            DefaultTableModel model = (DefaultTableModel) importBooksTable.getModel();
            model.setRowCount(0);
            for (ImportedBook importedBook : importedBooks) {

                Object[] rowData = {importedBook.title, importedBook.quantity, importedBook.pricePerBook};

                model.addRow(rowData);
            }

            totalCostField.setText(importSheet.totalCost.toString());
        } catch (Exception e) {

        }

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

        formScrollPane = new javax.swing.JScrollPane();
        groupFieldPanel = new javax.swing.JPanel();
        employeeLabel = new javax.swing.JLabel();
        employeeField = new javax.swing.JTextField();
        importDateLabel = new javax.swing.JLabel();
        totalCostField = new javax.swing.JTextField();
        totalCostLabel = new javax.swing.JLabel();
        jScrollPane = new javax.swing.JScrollPane();
        importBooksTable = new javax.swing.JTable();
        importDateField = new javax.swing.JTextField();
        titleBar = new javax.swing.JPanel();
        backButton = new javax.swing.JButton();
        title = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();

        setPreferredSize(new java.awt.Dimension(944, 1503));
        setLayout(new java.awt.BorderLayout());

        formScrollPane.setBorder(null);

        groupFieldPanel.setPreferredSize(new java.awt.Dimension(944, 1503));

        employeeLabel.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_T);
        employeeLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        employeeLabel.setText("Employee");

        employeeField.setEditable(false);
        employeeField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        employeeField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        employeeField.setFocusable(false);
        employeeField.setMinimumSize(new java.awt.Dimension(440, 31));
        employeeField.setPreferredSize(new java.awt.Dimension(440, 31));

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

        importBooksTable.setModel(new com.group06.bsms.importsheet.ImportedBooksTableModel());
        importBooksTable.setRowHeight(40);
        importBooksTable.setRowSelectionAllowed(false);
        jScrollPane.setViewportView(importBooksTable);

        importDateField.setEditable(false);
        importDateField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        importDateField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        importDateField.setFocusable(false);
        importDateField.setMinimumSize(new java.awt.Dimension(440, 31));
        importDateField.setPreferredSize(new java.awt.Dimension(440, 31));

        javax.swing.GroupLayout groupFieldPanelLayout = new javax.swing.GroupLayout(groupFieldPanel);
        groupFieldPanel.setLayout(groupFieldPanelLayout);
        groupFieldPanelLayout.setHorizontalGroup(
            groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                .addContainerGap(46, Short.MAX_VALUE)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 852, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                        .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(employeeField, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(employeeLabel))
                        .addGap(18, 18, 18)
                        .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                .addComponent(importDateLabel)
                                .addGap(152, 152, 152))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, groupFieldPanelLayout.createSequentialGroup()
                                .addComponent(importDateField, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                        .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(totalCostLabel)
                            .addComponent(totalCostField, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        groupFieldPanelLayout.setVerticalGroup(
            groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                        .addComponent(importDateLabel)
                        .addGap(37, 37, 37))
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                        .addComponent(employeeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(importDateField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(employeeField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                        .addComponent(totalCostLabel)
                        .addGap(2, 2, 2)
                        .addComponent(totalCostField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
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

        title.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        title.setText("Import sheet:");

        javax.swing.GroupLayout titleBarLayout = new javax.swing.GroupLayout(titleBar);
        titleBar.setLayout(titleBarLayout);
        titleBarLayout.setHorizontalGroup(
            titleBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(titleBarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(title)
                .addContainerGap(779, Short.MAX_VALUE))
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        titleBarLayout.setVerticalGroup(
            titleBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(titleBarLayout.createSequentialGroup()
                .addGroup(titleBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(titleBarLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(title))
                    .addGroup(titleBarLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        add(titleBar, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void backButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backButtonMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_backButtonMouseEntered

    private void backButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backButtonMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_backButtonMouseExited

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        Dashboard.dashboard.switchTab("importSheetCRUD");
    }//GEN-LAST:event_backButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JTextField employeeField;
    private javax.swing.JLabel employeeLabel;
    private javax.swing.JScrollPane formScrollPane;
    private javax.swing.JPanel groupFieldPanel;
    private javax.swing.JTable importBooksTable;
    private javax.swing.JTextField importDateField;
    private javax.swing.JLabel importDateLabel;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel title;
    private javax.swing.JPanel titleBar;
    private javax.swing.JTextField totalCostField;
    private javax.swing.JLabel totalCostLabel;
    // End of variables declaration//GEN-END:variables
}
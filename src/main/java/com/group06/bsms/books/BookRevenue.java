package com.group06.bsms.books;

import com.group06.bsms.DB;
import com.group06.bsms.authors.AuthorRepository;
import com.group06.bsms.authors.AuthorService;
import com.group06.bsms.categories.CategoryRepository;
import com.group06.bsms.categories.CategoryService;
import com.group06.bsms.publishers.PublisherRepository;
import com.group06.bsms.publishers.PublisherService;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

public class BookRevenue extends javax.swing.JPanel {

    private final BookService bookService;
    private BookRevenueTableModel model;
    private Map<Integer, SortOrder> columnSortOrders = new HashMap<>();
    private final int limit = 10;
    private static String previousComboBoxSelection;
    private LocalDate endDate = LocalDate.now();
    private LocalDate startDate = LocalDate.now().minusDays(7);

    public BookRevenue() {
        this(
                new BookService(
                        new BookRepository(DB.db()),
                        new AuthorService(new AuthorRepository(DB.db())),
                        new PublisherService(new PublisherRepository(DB.db())),
                        new CategoryService(new CategoryRepository(DB.db()))
                )
        );
    }

    public BookRevenue(BookService bookService) {
        this.bookService = bookService;
        this.model = new BookRevenueTableModel(bookService);
        initComponents();
        setUpTable();
        this.loadBooksIntoTable();
        isVisibleDatePicker(false);

        startDatePicker.setDate(new java.util.Date());
        var sm = new SimpleDateFormat("dd/MM/yyyy");
        startDatePicker.setText(sm.format(startDatePicker.getDate()));

        endDatePicker.setDate(new java.util.Date());
        endDatePicker.setText(sm.format(endDatePicker.getDate()));
    }

    public void setUpTable() {

        table.getTableHeader().setFont(new java.awt.Font("Segoe UI", 0, 16));
        table.setShowVerticalLines(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        columnSortOrders.put(5, SortOrder.DESCENDING);
        table.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());

        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnIndex = table.columnAtPoint(e.getPoint());
                toggleSortOrder(columnIndex);
                loadBooksIntoTable();
                table.getTableHeader().repaint();
            }
        });

        table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());

                table.editCellAt(row, column);
                table.setRowSelectionInterval(row, row);
            }
        });
    }

    public void loadBooksIntoTable() {
        try {
            List<Book> books = null;
            Date start = java.sql.Date.valueOf(startDate);
            Date end = java.sql.Date.valueOf(endDate);
            books = bookService.getTop10BooksWithHighestRevenue(columnSortOrders, start, end);
            model.reloadAllBooks(books);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "An error has occurred: " + e.getMessage(),
                    "BSMS Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void toggleSortOrder(int columnIndex) {
        SortOrder currentOrder = columnSortOrders.getOrDefault(columnIndex, SortOrder.UNSORTED);
        SortOrder newOrder = currentOrder == SortOrder.ASCENDING ? SortOrder.DESCENDING : SortOrder.ASCENDING;
        columnSortOrders.clear();
        columnSortOrders.put(columnIndex, newOrder);
    }

    class CustomHeaderRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int modelColumn = table.convertColumnIndexToModel(column);
            SortOrder sortOrder = columnSortOrders.getOrDefault(modelColumn, SortOrder.UNSORTED);
            Icon sortIcon = null;
            if (column == 3 || column == 4 || column == 5) {
                setHorizontalAlignment(JLabel.CENTER);
                if (sortOrder == SortOrder.ASCENDING) {
                    sortIcon = UIManager.getIcon("Table.descendingSortIcon");
                } else if (sortOrder == SortOrder.DESCENDING) {
                    sortIcon = UIManager.getIcon("Table.ascendingSortIcon");
                }
            } else {
                setHorizontalAlignment(JLabel.LEFT);
                if (sortOrder == SortOrder.ASCENDING) {
                    sortIcon = UIManager.getIcon("Table.descendingSortIcon");
                } else if (sortOrder == SortOrder.DESCENDING) {
                    sortIcon = UIManager.getIcon("Table.ascendingSortIcon");
                }
            }
            setHorizontalTextPosition(JLabel.LEFT);
            label.setIcon(sortIcon);
            return label;
        }
    }

    private void isVisibleDatePicker(boolean isVisible) {
        startDatePicker.setVisible(isVisible);
        endDatePicker.setVisible(isVisible);
        startDateLabel.setVisible(isVisible);
        endDateLabel.setVisible(isVisible);
        confimrBtn.setVisible(isVisible);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        bookRevenueLabel = new javax.swing.JLabel();
        scrollBar = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        durationDaysComboBox = new javax.swing.JComboBox<>();
        startDatePicker = new com.group06.bsms.components.DatePickerPanel();
        endDatePicker = new com.group06.bsms.components.DatePickerPanel();
        startDateLabel = new javax.swing.JLabel();
        endDateLabel = new javax.swing.JLabel();
        confimrBtn = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        bookRevenueLabel.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        bookRevenueLabel.setText("BEST SELLING BOOKS");

        table.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        table.setModel(this.model);
        table.setToolTipText("");
        table.setRowHeight(40);
        table.getTableHeader().setReorderingAllowed(false);
        scrollBar.setViewportView(table);

        durationDaysComboBox.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        durationDaysComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "by Week", "by Month", "Date to Date" }));
        durationDaysComboBox.setPreferredSize(new java.awt.Dimension(154, 28));
        durationDaysComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                durationDaysComboBoxActionPerformed(evt);
            }
        });

        startDatePicker.setMaximumSize(new java.awt.Dimension(215, 31));
        startDatePicker.setPlaceholder("dd/mm/yyyy");
        startDatePicker.setPreferredSize(new java.awt.Dimension(215, 31));

        endDatePicker.setMaximumSize(new java.awt.Dimension(215, 31));
        endDatePicker.setPlaceholder("dd/mm/yyyy");
        endDatePicker.setPreferredSize(new java.awt.Dimension(215, 31));

        startDateLabel.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        startDateLabel.setText("From Date");

        endDateLabel.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        endDateLabel.setText("To Date");

        confimrBtn.setBackground(new java.awt.Color(65, 105, 225));
        confimrBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        confimrBtn.setForeground(new java.awt.Color(255, 255, 255));
        confimrBtn.setMnemonic(java.awt.event.KeyEvent.VK_A);
        confimrBtn.setText("Confirm");
        confimrBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        confimrBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confimrBtnActionPerformed(evt);
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
                        .addComponent(bookRevenueLabel)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(scrollBar)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, 0)
                                .addComponent(durationDaysComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(startDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(startDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(endDateLabel)
                                    .addComponent(endDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(confimrBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 10, Short.MAX_VALUE)))
                        .addGap(38, 38, 38))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(bookRevenueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(endDateLabel)
                            .addComponent(startDateLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(startDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(endDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(22, 22, 22))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(confimrBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(durationDaysComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)))
                .addComponent(scrollBar, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                .addGap(50, 50, 50))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void durationDaysComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_durationDaysComboBoxActionPerformed
        if (durationDaysComboBox.getSelectedItem().toString() == previousComboBoxSelection) {
            return;
        }

        if (durationDaysComboBox.getSelectedItem().toString() == "by Week") {
            endDate = LocalDate.now();
            startDate = LocalDate.now().minusDays(7);
            isVisibleDatePicker(false);
            previousComboBoxSelection = "by Week";
            loadBooksIntoTable();
        } else if (durationDaysComboBox.getSelectedItem().toString() == "by Month") {
            endDate = LocalDate.now();
            startDate = LocalDate.now().minusDays(30);
            isVisibleDatePicker(false);
            previousComboBoxSelection = "by Month";
            loadBooksIntoTable();
        } else if (durationDaysComboBox.getSelectedItem().toString() == "Date to Date") {
            isVisibleDatePicker(true);
            previousComboBoxSelection = "by Date";
        }
    }//GEN-LAST:event_durationDaysComboBoxActionPerformed

    private void confimrBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confimrBtnActionPerformed
        LocalDate start = startDatePicker.getDateSQL().toLocalDate();
        LocalDate end = endDatePicker.getDateSQL().toLocalDate();

        if (start.isAfter(end)) {
            JOptionPane.showMessageDialog(null, "Start date must be before end date", "BSMS Error", JOptionPane.ERROR_MESSAGE);
        } else {
            startDate = start;
            endDate = end;
            loadBooksIntoTable();
        }
    }//GEN-LAST:event_confimrBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBookButton;
    private javax.swing.JButton addBookButton1;
    private javax.swing.JLabel bookRevenueLabel;
    private javax.swing.JButton confimrBtn;
    private javax.swing.JComboBox<String> durationDaysComboBox;
    private javax.swing.JLabel endDateLabel;
    private com.group06.bsms.components.DatePickerPanel endDatePicker;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane scrollBar;
    private javax.swing.JLabel startDateLabel;
    private com.group06.bsms.components.DatePickerPanel startDatePicker;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}

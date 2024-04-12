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
            books = bookService.getTop10BooksWithHighestRevenue(columnSortOrders);
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bookRevenueLabel = new javax.swing.JLabel();
        scrollBar = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();

        bookRevenueLabel.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        bookRevenueLabel.setText("BEST SELLING BOOKS");

        table.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        table.setModel(this.model);
        table.setToolTipText("");
        table.setRowHeight(40);
        table.getTableHeader().setReorderingAllowed(false);
        scrollBar.setViewportView(table);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollBar, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
                    .addComponent(bookRevenueLabel))
                .addGap(42, 42, 42))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(bookRevenueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addComponent(scrollBar, javax.swing.GroupLayout.DEFAULT_SIZE, 1299, Short.MAX_VALUE)
                .addGap(50, 50, 50))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bookRevenueLabel;
    private javax.swing.JScrollPane scrollBar;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}

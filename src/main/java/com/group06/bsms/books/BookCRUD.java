package com.group06.bsms.books;

import com.formdev.flatlaf.FlatClientProperties;
import com.group06.bsms.DB;
import com.group06.bsms.components.ActionBtn;
import com.group06.bsms.components.TableActionEvent;
import com.group06.bsms.utils.SVGHelper;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import static javax.swing.SortOrder.ASCENDING;
import static javax.swing.SortOrder.DESCENDING;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

public class BookCRUD extends javax.swing.JPanel {

    private final BookService bookService;
    private BookTableModel model;

    public BookCRUD() {
        this(new BookService(new BookRepository(DB.db())));
    }

    public BookCRUD(BookService bookService) {
        this.bookService = bookService;
        this.model = new BookTableModel();

        initComponents();

        searchBar.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search");
        searchBar.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, SVGHelper.createSVGIconWithFilter(
                "icons/search.svg",
                Color.black, Color.black,
                14, 14
        ));

        setUpTable();
        loadBooksIntoTable();
    }

    public ActionBtn getActionPanelFromCell(int row, int column) {
        if (table.isEditing() && table.getEditingRow() == row && table.getEditingColumn() == column) {
            return (ActionBtn) table.getCellEditor(row, column).getTableCellEditorComponent(table, null, false, row, column);
        } else {
            return (ActionBtn) table.getCellRenderer(row, column).getTableCellRendererComponent(table, null, false, false, row, column);
        }
    }

    private void loadBooksIntoTable() {
        try {
            var books = bookService.getAllBooks();
            if (books == null) throw new NullPointerException();

            model.loadNewBooks(books);
            // Notify Sorter that rows changed! VERY IMPORTANT, DO NOT DELETE
            table.getRowSorter().allRowsChanged();
        } 
        catch (NullPointerException e) {
            System.out.println("An error occurred while gettings book information: "+e.getMessage());
        }
        catch (Throwable e) {
            System.err.println(e);
        }
    }

    private void setUpTable() {
        table.getColumnModel().getColumn(5).setCellRenderer(new TableActionCellRender());

        table.getTableHeader().setFont(new java.awt.Font("Segoe UI", 0, 16));
        table.setShowVerticalLines(true);

        TableRowSorter<BookTableModel> sorter = new TableRowSorter<>(this.model);
        table.setRowSorter(sorter);
        sorter.setSortable(5, false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cellRenderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                setBackground(table.getTableHeader().getBackground());

                if (table.getRowSorter() != null) {
                    Icon sortIcon = UIManager.getIcon("Table.descendingSortIcon");
                    SortOrder sortOrder = SortOrder.UNSORTED;
                    if (!table.getRowSorter().getSortKeys().isEmpty()) {
                        SortKey sortKey = table.getRowSorter().getSortKeys().get(0);
                        if (sortKey.getColumn() == table.convertColumnIndexToModel(column)) {
                            sortOrder = sortKey.getSortOrder();
                            switch (sortOrder) {
                                case ASCENDING ->
                                    sortIcon = UIManager.getIcon("Table.ascendingSortIcon");
                                case DESCENDING ->
                                    sortIcon = UIManager.getIcon("Table.descendingSortIcon");
                            }
                        }
                    }
                    setIcon(sortIcon);
                    setHorizontalTextPosition(JLabel.LEFT);
                    setHorizontalAlignment(JLabel.LEFT);
                }

                return cellRenderer;
            }
        };

        table.getColumnModel().getColumn(0).setHeaderRenderer(leftRenderer);
        table.getColumnModel().getColumn(1).setHeaderRenderer(leftRenderer);
        table.getColumnModel().getColumn(2).setHeaderRenderer(leftRenderer);
        
        DefaultTableCellRenderer centerHeaderRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cellRenderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                setBackground(table.getTableHeader().getBackground());

                if (table.getRowSorter() != null) {
                    Icon sortIcon = UIManager.getIcon("Table.descendingSortIcon");
                    SortOrder sortOrder = SortOrder.UNSORTED;
                    if (!table.getRowSorter().getSortKeys().isEmpty()) {
                        SortKey sortKey = table.getRowSorter().getSortKeys().get(0);
                        if (sortKey.getColumn() == table.convertColumnIndexToModel(column)) {
                            sortOrder = sortKey.getSortOrder();
                            switch (sortOrder) {
                                case ASCENDING ->
                                    sortIcon = UIManager.getIcon("Table.ascendingSortIcon");
                                case DESCENDING ->
                                    sortIcon = UIManager.getIcon("Table.descendingSortIcon");
                            }
                        }
                    }
                    setIcon(sortIcon);
                    setHorizontalTextPosition(JLabel.LEFT);
                    setHorizontalAlignment(JLabel.CENTER);
                }

                return cellRenderer;
            }
        };
        
        table.getColumnModel().getColumn(3).setHeaderRenderer(centerHeaderRenderer);
        table.getColumnModel().getColumn(4).setHeaderRenderer(centerHeaderRenderer);

        TableActionEvent event = new TableActionEvent() {
            private boolean isHiddenBtn;

            @Override
            public void onEdit(int row) {
                System.out.println("Edit row " + row);
//                table.setRowSelectionInterval(row, row);
            }

            @Override
            public int onHide(int row) {
//                table.setRowSelectionInterval(row, row);

//                int index = table.convertRowIndexToModel(row);
                
                try {
                    if (model.getHiddenState(row) == 1) {
                        bookService.showBook(model.getBook(row).id);
                    } else if(model.getHiddenState(row) == 0) {
                        bookService.hideBook(model.getBook(row).id);
                    }    
                    model.setHiddenState(row);
                }
                catch (Exception e) {
                    System.out.println("Some error occurred while trying to hide a book: " + e.getMessage());
                }

                System.out.println("True value of book with title " + model.getValueAt(row, 0) + ": " + model.getHiddenState(row));
                return model.getHiddenState(row);
            }
        };

        table.getColumnModel().getColumn(5).setCellEditor(new TableActionCellEditor(event));

        table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());

                if (column == 5) {
                    table.editCellAt(row, column);
                    table.setRowSelectionInterval(row, row);
                }
            }
        });

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bookLabel = new javax.swing.JLabel();
        searchBar = new javax.swing.JTextField();
        createBtn = new javax.swing.JButton();
        filterBtn = new javax.swing.JButton();
        scrollBar = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jComboBox1 = new javax.swing.JComboBox<>();

        setAutoscrolls(true);

        bookLabel.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        bookLabel.setText("BOOKS");

        searchBar.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        searchBar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchBarActionPerformed(evt);
            }
        });

        createBtn.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        createBtn.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/add.svg",
            Color.black, Color.black,
            14, 14
        ));
        createBtn.setText("Create");
        createBtn.setToolTipText("");
        createBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        createBtn.setIconTextGap(2);
        createBtn.setMargin(new java.awt.Insets(10, 10, 10, 10));
        createBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createBtnActionPerformed(evt);
            }
        });

        filterBtn.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        filterBtn.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/filter.svg",
            Color.black, Color.black,
            14, 14));
    filterBtn.setText("Filter");
    filterBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    filterBtn.setIconTextGap(2);
    filterBtn.setMargin(new java.awt.Insets(10, 10, 10, 10));
    filterBtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            filterBtnActionPerformed(evt);
        }
    });

    table.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    table.setModel(this.model);
    table.setToolTipText("");
    table.setRowHeight(40);
    table.getTableHeader().setReorderingAllowed(false);
    scrollBar.setViewportView(table);

    jComboBox1.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addGap(42, 42, 42)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(bookLabel)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(scrollBar, javax.swing.GroupLayout.DEFAULT_SIZE, 836, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(searchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(20, 20, 20)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(20, 20, 20)
                            .addComponent(createBtn)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(filterBtn)))
                    .addGap(50, 50, 50))))
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addGap(48, 48, 48)
            .addComponent(bookLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(searchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(createBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(filterBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(18, 18, 18)
            .addComponent(scrollBar, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
            .addGap(30, 30, 30))
    );
    }// </editor-fold>//GEN-END:initComponents

    private void searchBarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBarActionPerformed
        
        var text = searchBar.getText();
        System.out.println("Value in searchBox: "+text);
        
        List<Book> books = bookService.searchBooks(text);

        model.reloadAllBooks(books);
        // Notify Sorter that rows changed! VERY IMPORTANT, DO NOT DELETE
        table.getRowSorter().allRowsChanged();
    
    }//GEN-LAST:event_searchBarActionPerformed

    private void createBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createBtnActionPerformed

    }//GEN-LAST:event_createBtnActionPerformed

    private void filterBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterBtnActionPerformed

    }//GEN-LAST:event_filterBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bookLabel;
    private javax.swing.JButton createBtn;
    private javax.swing.JButton filterBtn;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JScrollPane scrollBar;
    private javax.swing.JTextField searchBar;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
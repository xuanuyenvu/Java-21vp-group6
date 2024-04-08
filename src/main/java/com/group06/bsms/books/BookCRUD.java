package com.group06.bsms.books;

import com.formdev.flatlaf.FlatClientProperties;
import com.group06.bsms.DB;
import com.group06.bsms.Main;
import static com.group06.bsms.Main.app;
import com.group06.bsms.authors.Author;
import com.group06.bsms.authors.AuthorRepository;
import com.group06.bsms.authors.AuthorService;
import com.group06.bsms.categories.Category;
import com.group06.bsms.categories.CategoryRepository;
import com.group06.bsms.categories.CategoryService;
import com.group06.bsms.components.TableActionEvent;
import com.group06.bsms.dashboard.Dashboard;
import com.group06.bsms.publishers.Publisher;
import com.group06.bsms.publishers.PublisherRepository;
import com.group06.bsms.publishers.PublisherService;
import com.group06.bsms.utils.SVGHelper;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
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

public class BookCRUD extends javax.swing.JPanel {

    private final BookService bookService;
    private BookTableModel model;
    private Map<Integer, SortOrder> columnSortOrders = new HashMap<>();
    private int currentOffset = 0;
    private final UpdateBook updateBook;
    private final AddBookInformation addBookInfo;

    public void setCurrentOffset(int currentOffset) {
        this.currentOffset = currentOffset;
    }
    private final int limit = Main.ROW_LIMIT;
    private boolean isScrollAtBottom = false;

    public BookCRUD() {
        this(
                null,
                null,
                new BookService(
                        new BookRepository(DB.db()),
                        new AuthorService(new AuthorRepository(DB.db())),
                        new PublisherService(new PublisherRepository(DB.db())),
                        new CategoryService(new CategoryRepository(DB.db()))
                )
        );
    }

    public BookCRUD(UpdateBook updateBook, AddBookInformation addBookInfo) {
        this(
                updateBook,
                addBookInfo,
                new BookService(
                        new BookRepository(DB.db()),
                        new AuthorService(new AuthorRepository(DB.db())),
                        new PublisherService(new PublisherRepository(DB.db())),
                        new CategoryService(new CategoryRepository(DB.db()))
                )
        );
    }

    public BookCRUD(UpdateBook updateBook, AddBookInformation addBookInfo, BookService bookService) {
        this.updateBook = updateBook;
        this.addBookInfo = addBookInfo;
        this.bookService = bookService;
        this.bookFilter = new BookFilter(this);
        this.model = new BookTableModel(bookService);
        initComponents();

        searchBar.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search");
        searchBar.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, SVGHelper.createSVGIconWithFilter(
                "icons/search.svg",
                Color.black, Color.black,
                14, 14
        ));

        setUpTable();

        this.loadBooksIntoTable();
    }

    public void loadCategoryInto() {
        bookFilter.loadCategoryInto();
        updateBook.loadCategoryInto();
        addBookInfo.loadCategoryInto();
    }

    public void loadBooksIntoTable() {
        if (bookFilter == null) {
            return;
        }

        var searchString
                = searchBar == null || searchBar.getText() == null
                ? ""
                : searchBar.getText();

        String minPriceField = bookFilter.getMinPriceField().getText();
        Double minPrice = minPriceField.isEmpty() ? Double.MIN_VALUE : Double.valueOf(minPriceField);
        String maxPriceField = bookFilter.getMaxPriceField().getText();
        Double maxPrice = maxPriceField.isEmpty() ? Double.MAX_VALUE : Double.valueOf(maxPriceField);

        var searchChoiceKey = searchComboBox.getSelectedItem().toString();
        var searchChoiceMap = new HashMap<String, String>();
        searchChoiceMap.put("by Title", "Book.title");
        searchChoiceMap.put("by Author", "Author.name");
        searchChoiceMap.put("by Publisher", "Publisher.name");
        var searchChoiceValue = searchChoiceMap.get(searchChoiceKey);
        Author author = (Author) bookFilter.getAuthorAutoComp1().getSelectedObject();
        Publisher publisher = (Publisher) bookFilter.getPublisherAutoComp1().getSelectedObject();
        ArrayList<Category> categoriesList = bookFilter.getCategorySelectionPanel1().getListSelected();
        String filterTopBooks = (String) bookFilter.getFilterComboBox().getSelectedItem();

        try {
            int currentRowCount = 0;

            do {
                List<Book> books = null;
                if (filterTopBooks.equals("None")) {
                    books = bookService.searchSortFilterBook(currentOffset, limit, columnSortOrders,
                            searchString, searchChoiceValue, author, publisher, minPrice, maxPrice, categoriesList);
                } else if (filterTopBooks.equals("Top 20 Newest Books")) {
                    books = bookService.getNewBooks();
                } else if (filterTopBooks.equals("Top 20 Hottest Books")) {
                    books = bookService.getHotBooks();
                } else if (filterTopBooks.equals("Out-of-stock Books")) {
                    books = bookService.getOutOfStockBooks();
                }

                if (currentOffset > 0) {
                    model.loadNewBooks(books);
                } else {
                    model.reloadAllBooks(books);
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
                    "An error has occurred: " + e.getMessage(),
                    "BSMS Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void toggleSortOrder(int columnIndex) {
        if (columnIndex != 5) {
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
            if (column == 3 || column == 4) {
                setHorizontalAlignment(JLabel.CENTER);
                if (sortOrder == SortOrder.ASCENDING) {
                    sortIcon = UIManager.getIcon("Table.descendingSortIcon");
                } else if (sortOrder == SortOrder.DESCENDING) {
                    sortIcon = UIManager.getIcon("Table.ascendingSortIcon");
                }
            } else if (column != 5) {
                if (sortOrder == SortOrder.ASCENDING) {
                    sortIcon = UIManager.getIcon("Table.descendingSortIcon");
                } else if (sortOrder == SortOrder.DESCENDING) {
                    sortIcon = UIManager.getIcon("Table.ascendingSortIcon");
                }
                setHorizontalAlignment(JLabel.LEFT);
            } else {
                //Non sorted column action
                setHorizontalAlignment(JLabel.CENTER);
                sortIcon = null;
            }
            setHorizontalTextPosition(JLabel.LEFT);
            label.setIcon(sortIcon);
            return label;
        }
    }

    public void setUpTable() {
        table.getColumnModel().getColumn(5).setCellRenderer(new TableActionCellRender());

        table.getTableHeader().setFont(new java.awt.Font("Segoe UI", 0, 16));
        table.setShowVerticalLines(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        columnSortOrders.put(0, SortOrder.ASCENDING);

        table.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());

        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnIndex = table.columnAtPoint(e.getPoint());
                toggleSortOrder(columnIndex);
                reloadBooks();
                table.getTableHeader().repaint();
            }
        });
        TableActionEvent event = new TableActionEvent() {
            @Override
            public void onEdit(int row) {
                int bookId = model.getBook(row).id;
                updateBook.setBookById(bookId);
                Dashboard.dashboard.switchTab("updateBook");
            }

            @Override
            public int onHide(int row) {
                try {
                    if (model.getHiddenState(row) == 1) {
                        bookService.showBook(model.getBook(row).id);
                    } else if (model.getHiddenState(row) == 0) {
                        bookService.hideBook(model.getBook(row).id);
                    }
                    model.setHiddenState(row);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            app,
                            "An error has occurred while hiding book: " + e.getMessage(),
                            "BSMS Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }

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

        scrollBar.getVerticalScrollBar().addAdjustmentListener(e -> {
            if (!e.getValueIsAdjusting()) {
                //Check if scrolled to the bottom
                isScrollAtBottom = e.getAdjustable().getMaximum() == e.getAdjustable().getValue() + e.getAdjustable().getVisibleAmount();
                if (isScrollAtBottom) {
                    loadBooksIntoTable();
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
        searchComboBox = new javax.swing.JComboBox<>();
        main = new javax.swing.JPanel();
        bookFilter = new BookFilter(this);
        scrollBar = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();

        setAutoscrolls(true);

        bookLabel.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        bookLabel.setText("BOOKS");

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

        filterBtn.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        filterBtn.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/filter.svg",
            Color.black, Color.black,
            14, 14));
    filterBtn.setMnemonic(java.awt.event.KeyEvent.VK_F);
    filterBtn.setText("Filter");
    filterBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    filterBtn.setDisplayedMnemonicIndex(0);
    filterBtn.setIconTextGap(2);
    filterBtn.setMargin(new java.awt.Insets(10, 10, 10, 10));
    filterBtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            filterBtnActionPerformed(evt);
        }
    });

    searchComboBox.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    searchComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "by Title", "by Author", "by Publisher" }));
    searchComboBox.setPreferredSize(new java.awt.Dimension(154, 28));
    searchComboBox.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            searchComboBoxActionPerformed(evt);
        }
    });

    main.setLayout(new java.awt.BorderLayout());
    main.add(bookFilter, java.awt.BorderLayout.EAST);

    table.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
    table.setModel(this.model);
    table.setToolTipText("");
    table.setRowHeight(40);
    table.getTableHeader().setReorderingAllowed(false);
    scrollBar.setViewportView(table);

    main.add(scrollBar, java.awt.BorderLayout.CENTER);

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
                        .addComponent(main, javax.swing.GroupLayout.DEFAULT_SIZE, 836, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(searchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(20, 20, 20)
                            .addComponent(searchComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(20, 20, 20)
                            .addComponent(createBtn)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 278, Short.MAX_VALUE)
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
                .addComponent(searchComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(18, 18, 18)
            .addComponent(main, javax.swing.GroupLayout.DEFAULT_SIZE, 1299, Short.MAX_VALUE)
            .addGap(50, 50, 50))
    );
    }// </editor-fold>//GEN-END:initComponents

    public void reloadBooks() {
        reloadBooks(false);
    }

    public void reloadBooks(boolean reloadFilter) {
        currentOffset = 0;
        loadBooksIntoTable();

        if (reloadFilter) {
            bookFilter.loadAuthorInto();
            bookFilter.loadPublisherInto();

            updateBook.loadAuthorInto();
            updateBook.loadPublisherInto();

            addBookInfo.loadAuthorInto();
            addBookInfo.loadPublisherInto();
        }
    }

    private void searchBarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBarActionPerformed
        reloadBooks();
    }//GEN-LAST:event_searchBarActionPerformed

    private void createBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createBtnActionPerformed
        Dashboard.dashboard.switchTab("addBookInformation");
    }//GEN-LAST:event_createBtnActionPerformed

    private void filterBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterBtnActionPerformed
        bookFilter.setVisible(!bookFilter.isVisible());
    }//GEN-LAST:event_filterBtnActionPerformed

    private void searchComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchComboBoxActionPerformed
    }//GEN-LAST:event_searchComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.group06.bsms.books.BookFilter bookFilter;
    private javax.swing.JLabel bookLabel;
    private javax.swing.JButton createBtn;
    private javax.swing.JButton filterBtn;
    private javax.swing.JPanel main;
    private javax.swing.JScrollPane scrollBar;
    private javax.swing.JTextField searchBar;
    private javax.swing.JComboBox<String> searchComboBox;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}

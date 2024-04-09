package com.group06.bsms.authors;

import com.formdev.flatlaf.FlatClientProperties;
import com.group06.bsms.DB;
import com.group06.bsms.Main;
import static com.group06.bsms.Main.app;
import com.group06.bsms.books.BookCRUD;
import com.group06.bsms.components.TableActionEvent;
import com.group06.bsms.dashboard.Dashboard;
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

public class AuthorCRUD extends javax.swing.JPanel {

    private final AuthorService authorService;
    private AuthorTableModel model;
    private Map<Integer, SortOrder> columnSortOrders = new HashMap<>();
    private int currentOffset = 0;
    private final UpdateAuthor updateAuthor;
    private final AddAuthorInformation addAuthorInfo;
    private final BookCRUD bookCRUD;

    public void setCurrentOffset(int currentOffset) {
        this.currentOffset = currentOffset;
    }

    private final int limit = Main.ROW_LIMIT;
    private boolean isScrollAtBottom = false;

    public AuthorCRUD() {
        this(
                null,
                null,
                null,
                new AuthorService(new AuthorRepository(DB.db()))
        );
    }

    public AuthorCRUD(UpdateAuthor updateAuthor, AddAuthorInformation addAuthorInfo, BookCRUD bookCRUD) {
        this(
                updateAuthor,
                addAuthorInfo,
                bookCRUD,
                new AuthorService(new AuthorRepository(DB.db()))
        );
    }

    public AuthorCRUD(
            UpdateAuthor updateAuthor, AddAuthorInformation addAuthorInfo, BookCRUD bookCRUD,
            AuthorService authorService
    ) {
        this.updateAuthor = updateAuthor;
        this.addAuthorInfo = addAuthorInfo;
        this.bookCRUD = bookCRUD;
        this.authorService = authorService;

        this.model = new AuthorTableModel(authorService, bookCRUD);
        initComponents();

        searchBar.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search");
        searchBar.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, SVGHelper.createSVGIconWithFilter(
                "icons/search.svg",
                Color.black, Color.black,
                14, 14
        ));

        setUpTable();

        this.loadAuthorsIntoTable();
    }

    public void loadAuthorsIntoTable() {
        var searchString
                = searchBar == null || searchBar.getText() == null
                ? ""
                : searchBar.getText();

        try {
            int currentRowCount = 0;

            do {
                List<Author> authors = authorService.searchSortFilterAuthors(
                        currentOffset, limit, columnSortOrders, searchString
                );

                if (currentOffset > 0) {
                    model.loadNewAuthors(authors);
                } else {
                    model.reloadAllAuthors(authors);
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

    private void toggleSortOrder(int columnIndex) {
        if (columnIndex != 1) {
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
            if (column != 1) {
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
        table.getColumnModel().getColumn(1).setCellRenderer(new TableActionCellRender());

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
                reloadAuthors();
                table.getTableHeader().repaint();
            }
        });
        TableActionEvent event = new TableActionEvent() {
            @Override
            public void onEdit(int row) {
                int authorId = model.getAuthor(row).id;
                updateAuthor.setAuthorById(authorId);
                Dashboard.dashboard.switchTab("updateAuthor");
            }

            @Override
            public int onHide(int row) {
                try {
                    if (model.getHiddenState(row) == 1) {
                        authorService.showAuthor(model.getAuthor(row).id);
                    } else if (model.getHiddenState(row) == 0) {
                        authorService.hideAuthor(model.getAuthor(row).id);
                    }
                    model.setHiddenState(row);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            app,
                            e.getMessage(),
                            "BSMS Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }

                return model.getHiddenState(row);
            }
        };

        table.getColumnModel().getColumn(1).setCellEditor(new TableActionCellEditor(event));

        table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());

                if (column == 1) {
                    table.editCellAt(row, column);
                    table.setRowSelectionInterval(row, row);
                }
            }
        });

        scrollBar.getVerticalScrollBar().addAdjustmentListener(e -> {
            if (!e.getValueIsAdjusting()) {
                //Check if scrolled to the bottom
                isScrollAtBottom
                        = e.getAdjustable().getMaximum() == e.getAdjustable().getValue() + e.getAdjustable().getVisibleAmount();
                if (isScrollAtBottom) {
                    loadAuthorsIntoTable();
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
        main = new javax.swing.JPanel();
        scrollBar = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();

        setAutoscrolls(true);

        bookLabel.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        bookLabel.setText("AUTHORS");

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
                                .addComponent(createBtn)
                                .addGap(0, 0, Short.MAX_VALUE)))
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
                    .addComponent(createBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(main, javax.swing.GroupLayout.DEFAULT_SIZE, 1299, Short.MAX_VALUE)
                .addGap(50, 50, 50))
        );
    }// </editor-fold>//GEN-END:initComponents

    public void reloadAuthors() {
        reloadAuthors(false);
    }

    public void reloadAuthors(boolean reloadFilter) {
        currentOffset = 0;
        loadAuthorsIntoTable();

        if (reloadFilter) {
            bookCRUD.loadAuthorInto();
        }
    }

    private void searchBarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBarActionPerformed
        reloadAuthors();
    }//GEN-LAST:event_searchBarActionPerformed

    private void createBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createBtnActionPerformed
        Dashboard.dashboard.switchTab("addAuthorInformation");
    }//GEN-LAST:event_createBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bookLabel;
    private javax.swing.JButton createBtn;
    private javax.swing.JPanel main;
    private javax.swing.JScrollPane scrollBar;
    private javax.swing.JTextField searchBar;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}

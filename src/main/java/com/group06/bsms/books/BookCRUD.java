package com.group06.bsms.books;

import com.formdev.flatlaf.FlatClientProperties;
import com.group06.bsms.utils.SVGHelper;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

interface TableActionEvent {

    public void setIsHiddenBtn(boolean isHiddenBtn);

    public boolean isIsHiddenBtn();

    public void onEdit(int row);

    public void onHide(int row, Boolean isHidden);
}

class TableActionCellEditor extends DefaultCellEditor {

    private TableActionEvent event;

    public TableActionCellEditor(TableActionEvent event) {
        super(new JCheckBox());
        this.event = event;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        BookTableModel model = (BookTableModel) table.getModel();
        Boolean isHidden = (Boolean) model.getHiddenState(row);
        // System.out.println("Row with title "+model.getValueAt(row, 0));
        // System.out.println("Row: " + row + " Editor hide: " + isHidden);

        ActionPanel action = new ActionPanel(isHidden);
        action.initEvent(event, row, isHidden);
        action.setBackground(Color.WHITE);
        return action;
    }

}

class TableActionCellRender extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        boolean isHidden = ((BookTableModel) table.getModel()).getHiddenState(row);
        // System.out.println("Row: " + row + " Render: " + isHidden);
        
        Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        ActionPanel action = new ActionPanel(isHidden);
        action.setBackground(com.getBackground());
        return action;
    }
}

public class BookCRUD extends javax.swing.JPanel {
    
    private BookTableModel model;

    public BookCRUD() {
        this.model = new BookTableModel();
        
        initComponents();

        searchBar.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search");
        searchBar.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, SVGHelper.createSVGIconWithFilter(
                "icons/search.svg",
                Color.black, Color.black,
                14, 14
        ));
        
        setUpTable();
        addBookData();
    }

    public ActionPanel getActionPanelFromCell(int row, int column) {
        if (table.isEditing() && table.getEditingRow() == row && table.getEditingColumn() == column) {
            return (ActionPanel) table.getCellEditor(row, column).getTableCellEditorComponent(table, null, false, row, column);
        } else {
            return (ActionPanel) table.getCellRenderer(row, column).getTableCellRendererComponent(table, null, false, false, row, column);
        }
    }

    private void addBookData() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date d1 = new Date(sdf.parse("01/01/2003").getTime());
            Date d2 = new Date(sdf.parse("02/01/2010").getTime());
            List<Book> bookData = new ArrayList<>();
            bookData.add(new Book(1,1,"Book Title 1",100,d1,"Size 1","Translator Who","Book sucks", 100,145.500,0));
            bookData.add(new Book(1,1,"Book Title 2",120,d2,"Size 2","Translator What","Book sucks very much", 103,90.500,1));
            bookData.add(new Book(1,1,"Book Title 3",200,d2,"Size 3","Translator Which","Modern 21th Century Masterpiece TM", 103,90.500,1));
            for (var book : bookData) {
                model.addRow(book);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private void setUpTable() {
        table.getColumnModel().getColumn(5).setCellRenderer(new TableActionCellRender());

        table.getTableHeader().setFont(new java.awt.Font("Segoe UI", 0, 16));
        table.setShowVerticalLines(true);

       TableRowSorter<BookTableModel> sorter = new TableRowSorter<>(this.model);
    //    table.setRowSorter(sorter);
    //    sorter.setSortable(0, false);
    //    sorter.setSortable(1, false);
    //    sorter.setSortable(2, false);
    //    sorter.setSortable(5, false);
            

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 0) {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                setBackground(table.getTableHeader().getBackground());

                if (table.getRowSorter() != null) {
                    Icon sortIcon = null;
                    SortOrder sortOrder = SortOrder.UNSORTED;
                    if (table.getRowSorter().getSortKeys().size() > 0) {
                        SortKey sortKey = table.getRowSorter().getSortKeys().get(0);
                        if (sortKey.getColumn() == table.convertColumnIndexToModel(column)) {
                            sortOrder = sortKey.getSortOrder();
                            switch (sortOrder) {
                                case ASCENDING:
                                    sortIcon = UIManager.getIcon("Table.ascendingSortIcon");
                                    break;
                                case DESCENDING:
                                    sortIcon = UIManager.getIcon("Table.descendingSortIcon");
                                    break;
                            }
                        }
                    }
                    setIcon(sortIcon);
                    setHorizontalTextPosition(JLabel.LEFT);
                    setHorizontalAlignment(JLabel.LEFT);
                }

                return c;
            }
        };

        table.getColumnModel().getColumn(0).setHeaderRenderer(leftRenderer);
        table.getColumnModel().getColumn(1).setHeaderRenderer(leftRenderer);
        table.getColumnModel().getColumn(2).setHeaderRenderer(leftRenderer);

        TableActionEvent event = new TableActionEvent() {
            private boolean isHiddenBtn;

            @Override
            public void setIsHiddenBtn(boolean isHiddenBtn) {
                this.isHiddenBtn = isHiddenBtn;
            }

            @Override
            public boolean isIsHiddenBtn() {
                return isHiddenBtn;
            }

            @Override
            public void onEdit(int row) {
                System.out.println("Edit row " + row);
            }

            @Override
            public void onHide(int row, Boolean isHidden) {                
                model.setHiddenState(row);
                System.out.println("True value of row " + row + ": " + model.getHiddenState(row));
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
    table.setFocusable(false);
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
                .addGroup(layout.createSequentialGroup()
                    .addComponent(bookLabel)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(scrollBar, javax.swing.GroupLayout.DEFAULT_SIZE, 836, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(searchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
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
                .addComponent(filterBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGap(18, 18, 18)
            .addComponent(scrollBar, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
            .addGap(30, 30, 30))
    );
    }// </editor-fold>//GEN-END:initComponents

    private void searchBarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchBarActionPerformed

    private void createBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_createBtnActionPerformed

    private void filterBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_filterBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bookLabel;
    private javax.swing.JButton createBtn;
    private javax.swing.JButton filterBtn;
    private javax.swing.JScrollPane scrollBar;
    private javax.swing.JTextField searchBar;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}

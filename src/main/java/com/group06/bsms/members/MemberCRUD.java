package com.group06.bsms.members;

import com.group06.bsms.DB;
import com.group06.bsms.Main;
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

public class MemberCRUD extends javax.swing.JPanel {

    private final MemberService memberService;
    private MemberCRUDTableModel model;
    private AddMember addMember;
    private Map<Integer, SortOrder> columnSortOrders = new HashMap<>();
    private int currentOffset = 0;

    public void setCurrentOffset(int currentOffset) {
        this.currentOffset = currentOffset;
    }

    private final int limit = Main.ROW_LIMIT;
    private boolean isScrollAtBottom = false;

    public MemberCRUD() {
        this(null,
                new MemberService(
                        new MemberRepository(DB.db())
                )
        );
    }

    public MemberCRUD(AddMember addMember) {
        this(addMember,
                new MemberService(
                        new MemberRepository(DB.db())
                )
        );
    }

    public MemberCRUD(AddMember addMember, MemberService memberService) {
        this.memberService = memberService;
        this.addMember = addMember;
        this.model = new MemberCRUDTableModel(memberService);
        initComponents();
        setUpTable();
        loadMembersIntoTable();
        System.out.println(model.getColumnCount());

    }

    public void setUpTable() {

        table.getTableHeader().setFont(new java.awt.Font("Segoe UI", 0, 16));
        table.setShowVerticalLines(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new DateCellRenderer());

        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnIndex = table.columnAtPoint(e.getPoint());
                toggleSortOrder(columnIndex);

                reloadMembers();
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

        scrollBar.getVerticalScrollBar().addAdjustmentListener(e -> {
            if (!e.getValueIsAdjusting()) {
                //Check if scrolled to the bottom
                isScrollAtBottom
                        = e.getAdjustable().getMaximum() == e.getAdjustable().getValue() + e.getAdjustable().getVisibleAmount();
                if (isScrollAtBottom) {
                    loadMembersIntoTable();
                }
            }
        });
    }

    public void loadMembersIntoTable() {
        var searchString
                = searchBar == null || searchBar.getText() == null
                ? ""
                : searchBar.getText();

        var searchChoiceKey = searchComboBox.getSelectedItem().toString();
        var searchChoiceMap = new HashMap<String, String>();
        searchChoiceMap.put("by Phone", "Member.phone");
        searchChoiceMap.put("by Name", "Member.name");
        searchChoiceMap.put("by Email", "Member.email");
        searchChoiceMap.put("by Address", "Member.address");
        searchChoiceMap.put("by Date of Birth", "Member.dateOfBirth");

        var searchChoiceValue = searchChoiceMap.get(searchChoiceKey);

        try {
            int currentRowCount = 0;

            do {
                List<Member> membersList = memberService.searchSortFilterMembers(
                        currentOffset, limit, columnSortOrders,
                        searchString, searchChoiceValue
                );

                if (currentOffset > 0) {
                    model.loadNewMembers(membersList);
                } else {
                    model.reloadAllMembers(membersList);
                }

                currentOffset += limit;

                if (currentRowCount == model.getRowCount()) {
                    break;
                }

                currentRowCount = model.getRowCount();
            } while (currentRowCount < 2 * limit);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
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

            setHorizontalAlignment(JLabel.LEFT);
            if (sortOrder == SortOrder.ASCENDING) {
                sortIcon = UIManager.getIcon("Table.descendingSortIcon");
            } else if (sortOrder == SortOrder.DESCENDING) {
                sortIcon = UIManager.getIcon("Table.ascendingSortIcon");
            }

            setHorizontalTextPosition(JLabel.LEFT);
            label.setIcon(sortIcon);
            return label;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        memberRevenueLabel = new javax.swing.JLabel();
        tablePanel = new javax.swing.JPanel();
        scrollBar = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        searchBar = new javax.swing.JTextField();
        createBtn = new javax.swing.JButton();
        searchComboBox = new javax.swing.JComboBox<>();

        memberRevenueLabel.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        memberRevenueLabel.setText("MEMBERS");

        table.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        table.setModel(this.model);
        table.setToolTipText("");
        table.setRowHeight(40);
        table.getTableHeader().setReorderingAllowed(false);
        scrollBar.setViewportView(table);

        javax.swing.GroupLayout tablePanelLayout = new javax.swing.GroupLayout(tablePanel);
        tablePanel.setLayout(tablePanelLayout);
        tablePanelLayout.setHorizontalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 828, Short.MAX_VALUE)
            .addGroup(tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollBar, javax.swing.GroupLayout.DEFAULT_SIZE, 828, Short.MAX_VALUE))
        );
        tablePanelLayout.setVerticalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 298, Short.MAX_VALUE)
            .addGroup(tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(scrollBar, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE))
        );

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

        searchComboBox.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        searchComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "by Phone", "by Name", "by Email", "by Address", "by Date of Birth" }));
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
                        .addComponent(memberRevenueLabel)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(searchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(searchComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(createBtn))
                            .addComponent(tablePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 42, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(memberRevenueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(createBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addComponent(tablePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(85, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    public void reloadMembers() {
        reloadMembers(false);
    }

    public void reloadMembers(boolean reloadFilter) {
        currentOffset = 0;
        loadMembersIntoTable();

        if (reloadFilter) {
//            bookCRUD.loadAccountInto();
        }
    }

    private void searchBarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchBarActionPerformed
        reloadMembers();
    }//GEN-LAST:event_searchBarActionPerformed

    private void createBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createBtnActionPerformed
        Dashboard.dashboard.switchTab("addMember");
    }//GEN-LAST:event_createBtnActionPerformed

    private void searchComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchComboBoxActionPerformed

    }//GEN-LAST:event_searchComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton createBtn;
    private javax.swing.JLabel memberRevenueLabel;
    private javax.swing.JScrollPane scrollBar;
    private javax.swing.JTextField searchBar;
    private javax.swing.JComboBox<String> searchComboBox;
    private javax.swing.JTable table;
    private javax.swing.JPanel tablePanel;
    // End of variables declaration//GEN-END:variables
}

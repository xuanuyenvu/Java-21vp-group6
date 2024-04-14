package com.group06.bsms.accounts;

import com.group06.bsms.DB;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

public class EmployeeRevenue extends javax.swing.JPanel {

    private final AccountService accountService;
    private EmployeeRevenueTableModel model;
    private Map<Integer, SortOrder> columnSortOrders = new HashMap<>();
    private static String previousComboBoxSelection;
    private LocalDate endDate = LocalDate.now();
    private LocalDate startDate = LocalDate.now().minusDays(7);
    private List<Account> employees;

    public EmployeeRevenue() {
        this(
                new AccountService(
                        new AccountRepository(DB.db())
                )
        );
    }

    public EmployeeRevenue(AccountService accountService) {
        this.accountService = accountService;
        this.model = new EmployeeRevenueTableModel();
        initComponents();
        setUpTable();
        loadEmployeesIntoTable();
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
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        columnSortOrders.put(5, SortOrder.DESCENDING);
        table.getTableHeader().setDefaultRenderer(new CustomHeaderRenderer());

        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int columnIndex = table.columnAtPoint(e.getPoint());
                toggleSortOrder(columnIndex);
                loadEmployeesIntoTable();
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

    public void loadEmployeesIntoTable() {
        try {
            Date start = java.sql.Date.valueOf(startDate);
            Date end = java.sql.Date.valueOf(endDate);
            employees = accountService.getTop10EmployeesWithHighestRevenue(columnSortOrders, start, end);
            model.reloadAllEmployees(employees);
            showBarChart();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "An error has occurred: " + e.getMessage(),
                    "BSMS Error",
                    JOptionPane.ERROR_MESSAGE
            );
            System.out.println(e);
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
            if (column == 5) {
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

    public void showBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (var member : employees) {
            dataset.setValue(member.revenue, "$", member.name);
        }

        JFreeChart chart = ChartFactory.createBarChart("Top Sellers", "Seller's Name", "Revenue",
                dataset, PlotOrientation.VERTICAL, false, true, false);

        CategoryPlot categoryPlot = chart.getCategoryPlot();
        categoryPlot.setBackgroundPaint(UIManager.getColor("mutedBackground"));

        BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator("{1}, Revenue: {2}", NumberFormat.getCurrencyInstance(Locale.US)));
        renderer.setBarPainter(new StandardBarPainter());

        Color clr3 = UIManager.getColor("accentColor");
        renderer.setSeriesPaint(0, clr3);

        ChartPanel barpChartPanel = new ChartPanel(chart);
        barpChartPanel.setDomainZoomable(false);
        barpChartPanel.setRangeZoomable(false);
        barChartPanel.removeAll();
        barChartPanel.add(barpChartPanel, BorderLayout.CENTER);
        barChartPanel.validate();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        memberRevenueLabel = new javax.swing.JLabel();
        jTabbedPane = new javax.swing.JTabbedPane();
        tablePanel = new javax.swing.JPanel();
        scrollBar = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        chartPanel = new javax.swing.JPanel();
        confimrBtn = new javax.swing.JButton();
        startDatePicker = new com.group06.bsms.components.DatePickerPanel();
        endDatePicker = new com.group06.bsms.components.DatePickerPanel();
        durationDaysComboBox = new javax.swing.JComboBox<>();
        startDateLabel = new javax.swing.JLabel();
        endDateLabel = new javax.swing.JLabel();
        barChartPanel = new javax.swing.JPanel();

        memberRevenueLabel.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        memberRevenueLabel.setText("TOP SELLERS");

        jTabbedPane.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

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
            .addGroup(tablePanelLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(scrollBar, javax.swing.GroupLayout.DEFAULT_SIZE, 818, Short.MAX_VALUE))
        );
        tablePanelLayout.setVerticalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tablePanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(scrollBar, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        jTabbedPane.addTab("Table", tablePanel);

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

        startDatePicker.setMaximumSize(new java.awt.Dimension(215, 31));
        startDatePicker.setPlaceholder("dd/mm/yyyy");
        startDatePicker.setPreferredSize(new java.awt.Dimension(215, 31));

        endDatePicker.setMaximumSize(new java.awt.Dimension(215, 31));
        endDatePicker.setPlaceholder("dd/mm/yyyy");
        endDatePicker.setPreferredSize(new java.awt.Dimension(215, 31));

        durationDaysComboBox.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        durationDaysComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "by Week", "by Month", "Date to Date" }));
        durationDaysComboBox.setPreferredSize(new java.awt.Dimension(154, 28));
        durationDaysComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                durationDaysComboBoxActionPerformed(evt);
            }
        });

        startDateLabel.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        startDateLabel.setText("From Date");

        endDateLabel.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        endDateLabel.setText("To Date");

        barChartPanel.setLayout(new java.awt.GridLayout(1, 0));

        javax.swing.GroupLayout chartPanelLayout = new javax.swing.GroupLayout(chartPanel);
        chartPanel.setLayout(chartPanelLayout);
        chartPanelLayout.setHorizontalGroup(
            chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chartPanelLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(durationDaysComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(startDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(startDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(endDateLabel)
                    .addComponent(endDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(confimrBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(79, Short.MAX_VALUE))
            .addComponent(barChartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        chartPanelLayout.setVerticalGroup(
            chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, chartPanelLayout.createSequentialGroup()
                .addGroup(chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, chartPanelLayout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(confimrBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(chartPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(chartPanelLayout.createSequentialGroup()
                                .addGroup(chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(chartPanelLayout.createSequentialGroup()
                                        .addComponent(endDateLabel)
                                        .addGap(6, 6, 6))
                                    .addGroup(chartPanelLayout.createSequentialGroup()
                                        .addComponent(startDateLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addGroup(chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(startDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(endDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(chartPanelLayout.createSequentialGroup()
                                .addComponent(durationDaysComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(3, 3, 3)))))
                .addGap(15, 15, 15)
                .addComponent(barChartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Chart", chartPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane)
                        .addGap(50, 50, 50))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(memberRevenueLabel)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(memberRevenueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(jTabbedPane)
                .addGap(50, 50, 50))
        );

        jTabbedPane.getAccessibleContext().setAccessibleName("Table\nChart\n");
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
            columnSortOrders.clear();
            columnSortOrders.put(5, SortOrder.DESCENDING);
            loadEmployeesIntoTable();
        } else if (durationDaysComboBox.getSelectedItem().toString() == "by Month") {
            endDate = LocalDate.now();
            startDate = LocalDate.now().minusDays(30);
            isVisibleDatePicker(false);
            previousComboBoxSelection = "by Month";
            columnSortOrders.clear();
            columnSortOrders.put(5, SortOrder.DESCENDING);
            loadEmployeesIntoTable();
        } else if (durationDaysComboBox.getSelectedItem().toString() == "Date to Date") {
            isVisibleDatePicker(true);
            previousComboBoxSelection = "by Date";
            columnSortOrders.clear();
            columnSortOrders.put(5, SortOrder.DESCENDING);
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
            loadEmployeesIntoTable();
        }
    }//GEN-LAST:event_confimrBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel barChartPanel;
    private javax.swing.JPanel chartPanel;
    private javax.swing.JButton confimrBtn;
    private javax.swing.JComboBox<String> durationDaysComboBox;
    private javax.swing.JLabel endDateLabel;
    private com.group06.bsms.components.DatePickerPanel endDatePicker;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JLabel memberRevenueLabel;
    private javax.swing.JScrollPane scrollBar;
    private javax.swing.JLabel startDateLabel;
    private com.group06.bsms.components.DatePickerPanel startDatePicker;
    private javax.swing.JTable table;
    private javax.swing.JPanel tablePanel;
    // End of variables declaration//GEN-END:variables
}

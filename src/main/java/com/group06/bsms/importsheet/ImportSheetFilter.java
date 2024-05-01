package com.group06.bsms.importsheet;

import com.group06.bsms.components.DatePickerPanel;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class ImportSheetFilter extends javax.swing.JPanel {

    private final ImportSheetCRUD importSheetCRUD;

    private static String previousComboBoxSelection;
    private LocalDate endDate = LocalDate.now();
    private LocalDate startDate = LocalDate.now().minusDays(7);

    public DatePickerPanel getStartDatePicker() {
        return startDatePicker;
    }

    public DatePickerPanel getEndDatePicker() {
        return endDatePicker;
    }

    public ImportSheetFilter() {
        this(null);
    }

    public ImportSheetFilter(
            ImportSheetCRUD importSheetCRUD
    ) {
        this.importSheetCRUD = importSheetCRUD;

        initComponents();
        
        isVisibleDatePicker(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        groupFieldPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        label = new javax.swing.JLabel();
        durationDaysComboBox = new javax.swing.JComboBox<>();
        startDatePicker = new com.group06.bsms.components.DatePickerPanel();
        startDateLabel = new javax.swing.JLabel();
        endDatePicker = new com.group06.bsms.components.DatePickerPanel();
        endDateLabel = new javax.swing.JLabel();
        confimrBtn = new javax.swing.JButton();
        removeAllBtn = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        groupFieldPanel1.setBorder(new org.jdesktop.swingx.border.IconBorder());
        groupFieldPanel1.setMinimumSize(new java.awt.Dimension(0, 0));
        groupFieldPanel1.setPreferredSize(new java.awt.Dimension(300, 322));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 204, 204));
        jLabel1.setText("-");

        label.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_I);
        label.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        label.setText("Filter");
        label.setDisplayedMnemonicIndex(1);

        durationDaysComboBox.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        durationDaysComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Date to Date", "by Week", "by Month" }));
        durationDaysComboBox.setPreferredSize(new java.awt.Dimension(154, 28));
        durationDaysComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                durationDaysComboBoxActionPerformed(evt);
            }
        });

        startDatePicker.setMaximumSize(new java.awt.Dimension(215, 31));
        startDatePicker.setPlaceholder("dd/mm/yyyy");
        startDatePicker.setPreferredSize(new java.awt.Dimension(215, 31));

        startDateLabel.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        startDateLabel.setText("From Date");

        endDatePicker.setMaximumSize(new java.awt.Dimension(215, 31));
        endDatePicker.setPlaceholder("dd/mm/yyyy");
        endDatePicker.setPreferredSize(new java.awt.Dimension(215, 31));

        endDateLabel.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        endDateLabel.setText("To Date");

        confimrBtn.setBackground(new java.awt.Color(65, 105, 225));
        confimrBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        confimrBtn.setForeground(new java.awt.Color(255, 255, 255));
        confimrBtn.setMnemonic(java.awt.event.KeyEvent.VK_C);
        confimrBtn.setText("Confirm");
        confimrBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        confimrBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confimrBtnActionPerformed(evt);
            }
        });

        removeAllBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        removeAllBtn.setForeground(UIManager.getColor("mutedColor")
        );
        removeAllBtn.setMnemonic(java.awt.event.KeyEvent.VK_L);
        removeAllBtn.setText("Clear");
        removeAllBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        removeAllBtn.setDisplayedMnemonicIndex(1);
        removeAllBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAllBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout groupFieldPanel1Layout = new javax.swing.GroupLayout(groupFieldPanel1);
        groupFieldPanel1.setLayout(groupFieldPanel1Layout);
        groupFieldPanel1Layout.setHorizontalGroup(
            groupFieldPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(groupFieldPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, groupFieldPanel1Layout.createSequentialGroup()
                        .addComponent(label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(553, 553, 553))
                    .addGroup(groupFieldPanel1Layout.createSequentialGroup()
                        .addGroup(groupFieldPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(endDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(endDateLabel)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, groupFieldPanel1Layout.createSequentialGroup()
                                .addComponent(removeAllBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(confimrBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(groupFieldPanel1Layout.createSequentialGroup()
                        .addGroup(groupFieldPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(startDateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(startDatePicker, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(durationDaysComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        groupFieldPanel1Layout.setVerticalGroup(
            groupFieldPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanel1Layout.createSequentialGroup()
                .addComponent(label)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(durationDaysComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(startDateLabel)
                .addGap(3, 3, 3)
                .addComponent(startDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(groupFieldPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(groupFieldPanel1Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(groupFieldPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(endDateLabel)
                        .addGap(3, 3, 3)
                        .addComponent(endDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(groupFieldPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(confimrBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                            .addComponent(removeAllBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        add(groupFieldPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void isVisibleDatePicker(boolean isVisible) {
        startDatePicker.setVisible(isVisible);
        endDatePicker.setVisible(isVisible);
        startDateLabel.setVisible(isVisible);
        endDateLabel.setVisible(isVisible);
        confimrBtn.setVisible(isVisible);

    }

    private void durationDaysComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_durationDaysComboBoxActionPerformed
        if (durationDaysComboBox.getSelectedItem().toString().equals(previousComboBoxSelection)) {
            return;
        }

        switch (durationDaysComboBox.getSelectedItem().toString()) {
            case "by Week" -> {
                endDate = LocalDate.now();
                startDate = LocalDate.now().minusDays(7);
                isVisibleDatePicker(false);
                previousComboBoxSelection = "by Week";
                importSheetCRUD.loadImportSheetsIntoTableByFilter(startDate, endDate);
            }
            case "by Month" -> {
                endDate = LocalDate.now();
                startDate = LocalDate.now().minusDays(30);
                isVisibleDatePicker(false);
                previousComboBoxSelection = "by Month";
                importSheetCRUD.loadImportSheetsIntoTableByFilter(startDate, endDate);
            }
            case "Date to Date" -> {
                isVisibleDatePicker(true);
                previousComboBoxSelection = "by Date";
            }
            default -> {
            }
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
            importSheetCRUD.loadImportSheetsIntoTableByFilter(startDate, endDate);
        }
    }//GEN-LAST:event_confimrBtnActionPerformed

    private void removeAllBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAllBtnActionPerformed

        importSheetCRUD.reloadTables();
    }//GEN-LAST:event_removeAllBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton confimrBtn;
    private javax.swing.JComboBox<String> durationDaysComboBox;
    private javax.swing.JLabel endDateLabel;
    private com.group06.bsms.components.DatePickerPanel endDatePicker;
    private javax.swing.JPanel groupFieldPanel1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel label;
    private javax.swing.JButton removeAllBtn;
    private javax.swing.JLabel startDateLabel;
    private com.group06.bsms.components.DatePickerPanel startDatePicker;
    // End of variables declaration//GEN-END:variables
}

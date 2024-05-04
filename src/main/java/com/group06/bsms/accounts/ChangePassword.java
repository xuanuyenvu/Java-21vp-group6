package com.group06.bsms.accounts;

import com.group06.bsms.DB;
import javax.swing.*;

public class ChangePassword extends javax.swing.JPanel {

    private final AccountService accountService;
    private int accountId;

    public ChangePassword() {
        this(
                new AccountService(new AccountRepository(DB.db()))
        );
    }

    public ChangePassword(
            AccountService accountService
    ) {
        this.accountService = accountService;
        initComponents();

        currentPasswordField.putClientProperty("JTextField.placeholderText", "Current password");
        newPasswordField.putClientProperty("JTextField.placeholderText", "New password");
        confirmNewPasswordField.putClientProperty("JTextField.placeholderText", "Confirm new password");

        currentPasswordField.requestFocus();
    }

    public void setAccountById(int accountId) {
        this.accountId = accountId;
    }

    private void resetField() {
        currentPasswordField.setText("");
        newPasswordField.setText("");
        confirmNewPasswordField.setText("");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollForm = new javax.swing.JScrollPane();
        groupFieldPanel = new javax.swing.JPanel();
        confirmNewPasswordLabel = new javax.swing.JLabel();
        updateButton = new javax.swing.JButton();
        newPasswordLabel = new javax.swing.JLabel();
        pageName = new javax.swing.JLabel();
        newPasswordField = new javax.swing.JPasswordField();
        confirmNewPasswordField = new javax.swing.JPasswordField();
        currentPasswordLabel = new javax.swing.JLabel();
        currentPasswordField = new javax.swing.JPasswordField();

        setLayout(new java.awt.BorderLayout());

        jScrollForm.setBorder(null);
        jScrollForm.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollForm.setVerifyInputWhenFocusTarget(false);

        groupFieldPanel.setBorder(new org.jdesktop.swingx.border.IconBorder());
        groupFieldPanel.setMinimumSize(new java.awt.Dimension(440, 31));

        confirmNewPasswordLabel.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_T);
        confirmNewPasswordLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        confirmNewPasswordLabel.setLabelFor(confirmNewPasswordField);
        confirmNewPasswordLabel.setText("Confirm new password");

        updateButton.setBackground(new java.awt.Color(65, 105, 225));
        updateButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        updateButton.setForeground(new java.awt.Color(255, 255, 255));
        updateButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
        updateButton.setText("Change password");
        updateButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        updateButton.setMaximumSize(new java.awt.Dimension(440, 31));
        updateButton.setMinimumSize(new java.awt.Dimension(440, 31));
        updateButton.setPreferredSize(new java.awt.Dimension(440, 31));
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAccountButtonActionPerformed(evt);
            }
        });

        newPasswordLabel.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_T);
        newPasswordLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        newPasswordLabel.setLabelFor(newPasswordField);
        newPasswordLabel.setText("New password");

        pageName.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        pageName.setText("Change password");

        newPasswordField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        newPasswordField.setMinimumSize(new java.awt.Dimension(440, 31));
        newPasswordField.setPreferredSize(new java.awt.Dimension(440, 31));

        confirmNewPasswordField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        confirmNewPasswordField.setMinimumSize(new java.awt.Dimension(440, 31));
        confirmNewPasswordField.setPreferredSize(new java.awt.Dimension(440, 31));

        currentPasswordLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        currentPasswordLabel.setLabelFor(currentPasswordField);
        currentPasswordLabel.setText("Current password");

        currentPasswordField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        currentPasswordField.setMinimumSize(new java.awt.Dimension(440, 31));
        currentPasswordField.setPreferredSize(new java.awt.Dimension(440, 31));

        javax.swing.GroupLayout groupFieldPanelLayout = new javax.swing.GroupLayout(groupFieldPanel);
        groupFieldPanel.setLayout(groupFieldPanelLayout);
        groupFieldPanelLayout.setHorizontalGroup(
            groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                .addContainerGap(160, Short.MAX_VALUE)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(currentPasswordLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(confirmNewPasswordField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(confirmNewPasswordLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(newPasswordLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(newPasswordField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pageName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(currentPasswordField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(updateButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(160, Short.MAX_VALUE))
        );
        groupFieldPanelLayout.setVerticalGroup(
            groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(pageName)
                .addGap(20, 20, 20)
                .addComponent(currentPasswordLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(currentPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(newPasswordLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(confirmNewPasswordLabel)
                .addGap(6, 6, 6)
                .addComponent(confirmNewPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );

        jScrollForm.setViewportView(groupFieldPanel);

        add(jScrollForm, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void addAccountButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAccountButtonActionPerformed
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmNewPasswordField.getPassword());

        try {
            accountService.updatePasswordById(accountId, currentPassword, newPassword, confirmPassword);

            JOptionPane.showMessageDialog(null, "Password updated successfully.", "BSMS Information",
                    JOptionPane.INFORMATION_MESSAGE);

            resetField();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "BSMS Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_addAccountButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPasswordField confirmNewPasswordField;
    private javax.swing.JLabel confirmNewPasswordLabel;
    private javax.swing.JPasswordField currentPasswordField;
    private javax.swing.JLabel currentPasswordLabel;
    private javax.swing.JPanel groupFieldPanel;
    private javax.swing.JScrollPane jScrollForm;
    private javax.swing.JPasswordField newPasswordField;
    private javax.swing.JLabel newPasswordLabel;
    private javax.swing.JLabel pageName;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}

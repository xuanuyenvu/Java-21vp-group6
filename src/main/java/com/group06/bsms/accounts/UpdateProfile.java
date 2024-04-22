package com.group06.bsms.accounts;

import com.group06.bsms.DB;
import com.group06.bsms.components.*;
import javax.swing.*;

public class UpdateProfile extends javax.swing.JPanel {

    private final AccountService accountService;
    private Account account;    

    public UpdateProfile() {
        this(
                new AccountService(new AccountRepository(DB.db()))
        );
    }

    public UpdateProfile(
            AccountService accountService
    ) {
        this.accountService = accountService;
        initComponents();

        phoneField.putClientProperty("JTextField.placeholderText", "Account phone");
        nameField.putClientProperty("JTextField.placeholderText", "Account name");
        emailField.putClientProperty("JTextField.placeholderText", "Account email");
        addressField.putClientProperty("JTextField.placeholderText", "Account address");

        CustomLabelInForm.setColoredText(phoneLabel);

        phoneField.requestFocus();
    }

    public void setAccountById(int accountId) {
        try {
            account = accountService.getAccount(accountId);
            loadAccountInto();
        } catch (Exception e) {
            account = null;

            JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "BSMS Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAccountInto() {
        try {
            phoneField.setText(account.phone);
            nameField.setText(account.name);
            emailField.setText(account.email);
            addressField.setText(account.address);
            genderField.setSelectedItem(account.gender);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "BSMS Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollForm = new javax.swing.JScrollPane();
        groupFieldPanel = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        addCategoryButton = new javax.swing.JButton();
        emailLabel = new javax.swing.JLabel();
        emailField = new javax.swing.JTextField();
        addressField = new javax.swing.JTextField();
        addressLabel = new javax.swing.JLabel();
        genderLabel = new javax.swing.JLabel();
        phoneLabel = new javax.swing.JLabel();
        phoneField = new javax.swing.JTextField();
        genderField = new javax.swing.JComboBox<>();
        pageName = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        jScrollForm.setBorder(null);
        jScrollForm.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollForm.setVerifyInputWhenFocusTarget(false);

        groupFieldPanel.setBorder(new org.jdesktop.swingx.border.IconBorder());
        groupFieldPanel.setMinimumSize(new java.awt.Dimension(440, 31));

        nameLabel.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_T);
        nameLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        nameLabel.setLabelFor(nameField);
        nameLabel.setText("Name");

        nameField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        nameField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        nameField.setMinimumSize(new java.awt.Dimension(440, 31));
        nameField.setPreferredSize(new java.awt.Dimension(440, 31));

        addCategoryButton.setBackground(new java.awt.Color(65, 105, 225));
        addCategoryButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        addCategoryButton.setForeground(new java.awt.Color(255, 255, 255));
        addCategoryButton.setMnemonic(java.awt.event.KeyEvent.VK_U);
        addCategoryButton.setText("Update");
        addCategoryButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addCategoryButton.setDisplayedMnemonicIndex(0);
        addCategoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAccountButtonActionPerformed(evt);
            }
        });

        emailLabel.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_T);
        emailLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        emailLabel.setLabelFor(emailField);
        emailLabel.setText("Email");

        emailField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        emailField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        emailField.setMinimumSize(new java.awt.Dimension(440, 31));
        emailField.setPreferredSize(new java.awt.Dimension(440, 31));

        addressField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        addressField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        addressField.setMinimumSize(new java.awt.Dimension(440, 31));
        addressField.setPreferredSize(new java.awt.Dimension(440, 31));

        addressLabel.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_T);
        addressLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        addressLabel.setLabelFor(addressField);
        addressLabel.setText("Address");

        genderLabel.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_T);
        genderLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        genderLabel.setLabelFor(genderField);
        genderLabel.setText("Gender");

        phoneLabel.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_T);
        phoneLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        phoneLabel.setLabelFor(phoneField);
        phoneLabel.setText("Phone");

        phoneField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        phoneField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        phoneField.setMinimumSize(new java.awt.Dimension(440, 31));
        phoneField.setPreferredSize(new java.awt.Dimension(440, 31));
        phoneField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                phoneFieldActionPerformed(evt);
            }
        });

        genderField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        genderField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female", "Other" }));
        genderField.setMinimumSize(new java.awt.Dimension(440, 31));

        pageName.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        pageName.setText("My profile");

        javax.swing.GroupLayout groupFieldPanelLayout = new javax.swing.GroupLayout(groupFieldPanel);
        groupFieldPanel.setLayout(groupFieldPanelLayout);
        groupFieldPanelLayout.setHorizontalGroup(
            groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                .addContainerGap(91, Short.MAX_VALUE)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pageName)
                    .addComponent(addressLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(emailLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(phoneField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(nameField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(emailField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addressField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(genderLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(addCategoryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(phoneLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(nameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(genderField, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(96, Short.MAX_VALUE))
        );
        groupFieldPanelLayout.setVerticalGroup(
            groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(pageName)
                .addGap(18, 18, 18)
                .addComponent(phoneLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(phoneField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(nameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(emailLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(addressLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addressField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(genderLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(genderField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(addCategoryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );

        jScrollForm.setViewportView(groupFieldPanel);

        add(jScrollForm, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void addAccountButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAccountButtonActionPerformed
        String phone = phoneField.getText();
        String name = nameField.getText();
        String email = emailField.getText().equals("") ? null : emailField.getText();
        String address = addressField.getText();
        String gender = (String) genderField.getSelectedItem();

        try {
            if (account == null) {
                throw new Exception("Account data is empty");
            }

            var updatedAccount = new AccountWithPassword(
                    account.id, phone,
                    null,
                    email, address, name, gender,
                    account.isAdmin, account.isLocked
            );

            accountService.updateAccount(account, updatedAccount);

            JOptionPane.showMessageDialog(null, "Profile updated successfully.", "BSMS Information",
                    JOptionPane.INFORMATION_MESSAGE);

            setAccountById(account.id);
        } catch (Exception ex) {
            if (ex.getMessage().contains("account_phone_key")) {
                JOptionPane.showMessageDialog(null, "An account with this phone already exists", "BSMS Error", JOptionPane.ERROR_MESSAGE);
            } else if (ex.getMessage().contains("account_phone_check")) {
                JOptionPane.showMessageDialog(null, "Invalid phone format", "BSMS Error", JOptionPane.ERROR_MESSAGE);
            } else if (ex.getMessage().contains("account_email_check")) {
                JOptionPane.showMessageDialog(null, "Invalid email format", "BSMS Error", JOptionPane.ERROR_MESSAGE);
            } else if (ex.getMessage().contains("account_email_key")) {
                JOptionPane.showMessageDialog(null, "An account with this email already exists", "BSMS Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "BSMS Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_addAccountButtonActionPerformed

    private void phoneFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_phoneFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_phoneFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addCategoryButton;
    private javax.swing.JTextField addressField;
    private javax.swing.JLabel addressLabel;
    private javax.swing.JTextField emailField;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JComboBox<String> genderField;
    private javax.swing.JLabel genderLabel;
    private javax.swing.JPanel groupFieldPanel;
    private javax.swing.JScrollPane jScrollForm;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel pageName;
    private javax.swing.JTextField phoneField;
    private javax.swing.JLabel phoneLabel;
    // End of variables declaration//GEN-END:variables
}

package com.group06.bsms.publishers;

import com.group06.bsms.DB;
import com.group06.bsms.components.*;
import com.group06.bsms.dashboard.Dashboard;
import com.group06.bsms.utils.SVGHelper;
import java.awt.*;
import javax.swing.*;

public class AddPublisherInformation extends javax.swing.JPanel {

    private PublisherCRUD publisherCRUD;
    private final PublisherService publisherService;

    public AddPublisherInformation() {
        this(
                null,
                new PublisherService(new PublisherRepository(DB.db()))
        );
    }

    public AddPublisherInformation(PublisherCRUD publisherCRUD) {
        this(
                publisherCRUD,
                new PublisherService(new PublisherRepository(DB.db()))
        );
    }

    public void setPublisherCRUD(PublisherCRUD publisherCRUD) {
        this.publisherCRUD = publisherCRUD;
    }

    public AddPublisherInformation(
            PublisherCRUD publisherCRUD,
            PublisherService publisherService
    ) {
        this.publisherCRUD = publisherCRUD;
        this.publisherService = publisherService;
        initComponents();
        hiddenPropLabel.setVisible(false);

        nameField.putClientProperty("JTextField.placeholderText", "Publisher name");
        emailField.putClientProperty("JTextField.placeholderText", "Publisher email");
        addressField.putClientProperty("JTextField.placeholderText", "Publisher address");

        CustomLabelInForm.setColoredText(nameLabel);

        nameField.requestFocus();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        backButton = new javax.swing.JButton();
        pageName = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollForm = new javax.swing.JScrollPane();
        groupFieldPanel = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        hideCheckBox = new javax.swing.JCheckBox();
        hiddenPropLabel = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();
        addCategoryButton = new javax.swing.JButton();
        emailLabel = new javax.swing.JLabel();
        emailField = new javax.swing.JTextField();
        addressLabel = new javax.swing.JLabel();
        addressField = new javax.swing.JTextField();

        setLayout(new java.awt.BorderLayout());

        backButton.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        backButton.setForeground(UIManager.getColor("mutedColor"));
        backButton.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/arrow-back.svg", 
            Color.white, Color.white,
            18, 18
        ));
        backButton.setMnemonic(java.awt.event.KeyEvent.VK_BACK_SPACE);
        backButton.setToolTipText("Back to previous page");
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        backButton.setFocusable(false);
        backButton.setMargin(new java.awt.Insets(4, 14, 3, 14));
        backButton.setPreferredSize(new java.awt.Dimension(33, 33));
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                backButtonMouseExited(evt);
            }
        });
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        pageName.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        pageName.setText("Add publisher");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pageName)
                .addContainerGap(450, Short.MAX_VALUE))
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(pageName))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        add(jPanel1, java.awt.BorderLayout.PAGE_START);

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

        hideCheckBox.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        hideCheckBox.setText("Hidden Publisher");
        hideCheckBox.setIconTextGap(5);
        hideCheckBox.setMargin(new java.awt.Insets(2, 0, 2, 2));

        hiddenPropLabel.setFont(new java.awt.Font("Segoe UI", 2, 13)); // NOI18N
        hiddenPropLabel.setText("note sth");
        hiddenPropLabel.setMinimumSize(new java.awt.Dimension(423, 18));
        hiddenPropLabel.setPreferredSize(new java.awt.Dimension(423, 18));

        cancelButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cancelButton.setForeground(UIManager.getColor("mutedColor")
        );
        cancelButton.setText("Clear");
        cancelButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        addCategoryButton.setBackground(new java.awt.Color(65, 105, 225));
        addCategoryButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        addCategoryButton.setForeground(new java.awt.Color(255, 255, 255));
        addCategoryButton.setMnemonic(java.awt.event.KeyEvent.VK_A);
        addCategoryButton.setText("Add");
        addCategoryButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addCategoryButton.setDisplayedMnemonicIndex(0);
        addCategoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPublisherButtonActionPerformed(evt);
            }
        });

        emailLabel.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_T);
        emailLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        emailLabel.setText("Email");

        emailField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        emailField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        emailField.setMinimumSize(new java.awt.Dimension(440, 31));
        emailField.setPreferredSize(new java.awt.Dimension(440, 31));

        addressLabel.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_T);
        addressLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        addressLabel.setText("Address");

        addressField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        addressField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        addressField.setMinimumSize(new java.awt.Dimension(440, 31));
        addressField.setPreferredSize(new java.awt.Dimension(440, 31));

        javax.swing.GroupLayout groupFieldPanelLayout = new javax.swing.GroupLayout(groupFieldPanel);
        groupFieldPanel.setLayout(groupFieldPanelLayout);
        groupFieldPanelLayout.setHorizontalGroup(
            groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                .addContainerGap(95, Short.MAX_VALUE)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(addressLabel, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(addressField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(emailLabel, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(hideCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 444, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                    .addGap(21, 21, 21)
                                    .addComponent(hiddenPropLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 423, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, groupFieldPanelLayout.createSequentialGroup()
                                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(addCategoryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(nameField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(nameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(96, Short.MAX_VALUE))
        );
        groupFieldPanelLayout.setVerticalGroup(
            groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                .addGap(29, 29, 29)
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
                .addComponent(hideCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hiddenPropLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addCategoryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(58, 58, 58))
        );

        jScrollForm.setViewportView(groupFieldPanel);

        add(jScrollForm, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cancelButtonActionPerformed
        nameField.setText("");
        emailField.setText("");
        addressField.setText("");
        hideCheckBox.setSelected(false);
    }// GEN-LAST:event_cancelButtonActionPerformed

    private void addPublisherButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addPublisherButtonActionPerformed
        String name = nameField.getText();
        String email = emailField.getText().equals("") ? null : emailField.getText();
        String address = addressField.getText();
        boolean hideChecked = hideCheckBox.isSelected();

        try {
            publisherService.insertPublisher(name, email, address, hideChecked);

            cancelButtonActionPerformed(null);

            JOptionPane.showMessageDialog(null, "Publisher added successfully.", "BSMS Information",
                    JOptionPane.INFORMATION_MESSAGE);

            publisherCRUD.reloadPublishers(true);
        } catch (Exception ex) {
            if (ex.getMessage().contains("publisher_name_key")) {
                JOptionPane.showMessageDialog(null, "A publisher with this name already exists", "BSMS Error", JOptionPane.ERROR_MESSAGE);
            } else if (ex.getMessage().contains("publisher_name_check")) {
                JOptionPane.showMessageDialog(null, "Name cannot be empty", "BSMS Error", JOptionPane.ERROR_MESSAGE);
            } else if (ex.getMessage().contains("publisher_email_check")) {
                JOptionPane.showMessageDialog(null, "Invalid email format", "BSMS Error", JOptionPane.ERROR_MESSAGE);
            } else if (ex.getMessage().contains("publisher_email_key")) {
                JOptionPane.showMessageDialog(null, "A publisher with this email already exists", "BSMS Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "BSMS Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void backButtonMouseEntered(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_backButtonMouseEntered
        backButton.setIcon(SVGHelper.createSVGIconWithFilter("icons/arrow-back.svg", Color.black, Color.gray, 18, 18));
    }// GEN-LAST:event_backButtonMouseEntered

    private void backButtonMouseExited(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_backButtonMouseExited
        backButton.setIcon(SVGHelper.createSVGIconWithFilter("icons/arrow-back.svg", Color.black, Color.black, 18, 18));
    }// GEN-LAST:event_backButtonMouseExited

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        Dashboard.dashboard.switchTab("publisherCRUD");
    }//GEN-LAST:event_backButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addCategoryButton;
    private javax.swing.JTextField addressField;
    private javax.swing.JLabel addressLabel;
    private javax.swing.JButton backButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField emailField;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JPanel groupFieldPanel;
    private javax.swing.JLabel hiddenPropLabel;
    private javax.swing.JCheckBox hideCheckBox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollForm;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel pageName;
    // End of variables declaration//GEN-END:variables
}

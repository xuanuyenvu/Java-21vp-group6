package com.group06.bsms.publishers;

import com.group06.bsms.DB;
import com.group06.bsms.components.*;
import com.group06.bsms.dashboard.Dashboard;
import com.group06.bsms.utils.SVGHelper;
import java.awt.*;
import javax.swing.*;

public class UpdatePublisher extends javax.swing.JPanel {

    private PublisherCRUD publisherCRUD;
    private final PublisherService publisherService;
    private Publisher publisher;

    public void setPublisherCRUD(PublisherCRUD publisherCRUD) {
        this.publisherCRUD = publisherCRUD;
    }

    public UpdatePublisher() {
        this(
                null,
                new PublisherService(new PublisherRepository(DB.db()))
        );
    }

    public UpdatePublisher(PublisherCRUD publisherCRUD) {
        this(
                publisherCRUD,
                new PublisherService(new PublisherRepository(DB.db()))
        );
    }

    public UpdatePublisher(
            PublisherCRUD publisherCRUD,
            PublisherService publisherService
    ) {
        this.publisherCRUD = publisherCRUD;
        this.publisherService = publisherService;
        initComponents();

        nameField.putClientProperty("JTextField.placeholderText", "Publisher name");
        emailField.putClientProperty("JTextField.placeholderText", "Publisher email");
        addressField.putClientProperty("JTextField.placeholderText", "Publisher address");

        CustomLabelInForm.setColoredText(nameLabel);

        nameField.requestFocus();
    }

    public void setPublisherById(int publisherId) {
        try {
            publisher = publisherService.getPublisher(publisherId);
            loadPublisherInto();
        } catch (Exception e) {
            publisher = null;

            JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "BSMS Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPublisherInto() {
        try {
            nameField.setText(publisher.name);
            emailField.setText(publisher.email);
            addressField.setText(publisher.address);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage(),
                    "BSMS Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
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
        addCategoryButton = new javax.swing.JButton();
        emailLabel = new javax.swing.JLabel();
        emailField = new javax.swing.JTextField();
        addressField = new javax.swing.JTextField();
        addressLabel = new javax.swing.JLabel();

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
        pageName.setText("Update publisher");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pageName)
                .addContainerGap(392, Short.MAX_VALUE))
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
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

        addCategoryButton.setBackground(new java.awt.Color(65, 105, 225));
        addCategoryButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        addCategoryButton.setForeground(new java.awt.Color(255, 255, 255));
        addCategoryButton.setMnemonic(java.awt.event.KeyEvent.VK_U);
        addCategoryButton.setText("Update");
        addCategoryButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addCategoryButton.setDisplayedMnemonicIndex(0);
        addCategoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPublisherButtonActionPerformed(evt);
            }
        });

        emailLabel.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_T);
        emailLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        emailLabel.setLabelFor(nameField);
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
        addressLabel.setLabelFor(nameField);
        addressLabel.setText("Address");

        javax.swing.GroupLayout groupFieldPanelLayout = new javax.swing.GroupLayout(groupFieldPanel);
        groupFieldPanel.setLayout(groupFieldPanelLayout);
        groupFieldPanelLayout.setHorizontalGroup(
            groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                .addContainerGap(91, Short.MAX_VALUE)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                        .addComponent(addressLabel)
                        .addGap(379, 379, 379))
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                        .addComponent(emailLabel)
                        .addGap(400, 400, 400))
                    .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(groupFieldPanelLayout.createSequentialGroup()
                            .addGap(346, 346, 346)
                            .addComponent(addCategoryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(nameLabel, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(nameField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(emailField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addressField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(addCategoryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58))
        );

        jScrollForm.setViewportView(groupFieldPanel);

        add(jScrollForm, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void backButtonMouseEntered(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_backButtonMouseEntered
        backButton.setIcon(SVGHelper.createSVGIconWithFilter("icons/arrow-back.svg", Color.black, Color.gray, 18, 18));
    }// GEN-LAST:event_backButtonMouseEntered

    private void backButtonMouseExited(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_backButtonMouseExited
        backButton.setIcon(SVGHelper.createSVGIconWithFilter("icons/arrow-back.svg", Color.black, Color.black, 18, 18));
    }// GEN-LAST:event_backButtonMouseExited

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        Dashboard.dashboard.switchTab("publisherCRUD");
    }//GEN-LAST:event_backButtonActionPerformed

    private void addPublisherButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPublisherButtonActionPerformed
        String name = nameField.getText();
        String email = emailField.getText().equals("") ? null : emailField.getText();
        String address = addressField.getText();

        try {
            if (publisher == null) {
                throw new Exception("Publisher data is empty");
            }

            Publisher updatedPublisher = new Publisher(
                    publisher.id, name, email, address, publisher.isHidden
            );
            publisherService.updatePublisher(publisher, updatedPublisher);

            JOptionPane.showMessageDialog(null, "Publisher updated successfully.", "BSMS Information",
                    JOptionPane.INFORMATION_MESSAGE);

            publisherCRUD.reloadPublishers(true);
            setPublisherById(publisher.id);
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
    }//GEN-LAST:event_addPublisherButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addCategoryButton;
    private javax.swing.JTextField addressField;
    private javax.swing.JLabel addressLabel;
    private javax.swing.JButton backButton;
    private javax.swing.JTextField emailField;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JPanel groupFieldPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollForm;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel pageName;
    // End of variables declaration//GEN-END:variables
}

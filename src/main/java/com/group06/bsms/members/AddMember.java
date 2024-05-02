package com.group06.bsms.members;

import com.group06.bsms.DB;
import com.group06.bsms.components.*;
import com.group06.bsms.authors.*;
import com.group06.bsms.categories.*;
import com.group06.bsms.dashboard.Dashboard;
import com.group06.bsms.publishers.*;
import com.group06.bsms.utils.SVGHelper;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;

public class AddMember extends javax.swing.JPanel {

    private MemberCRUD memberCRUD;
    private final MemberService memberService;

    public AddMember() {
        this(
                null,
                new MemberService(new MemberRepository(DB.db()))
        );
    }

    public AddMember(MemberCRUD memberCRUD) {
        this(
                memberCRUD,
                new MemberService(new MemberRepository(DB.db())));
    }

    public void setBookCRUD(MemberCRUD memberCRUD) {
        this.memberCRUD = memberCRUD;
    }

    public AddMember(
            MemberCRUD memberCRUD,
            MemberService memberService) {
        this.memberCRUD = memberCRUD;
        this.memberService = memberService;
        initComponents();
        hiddenPropLabel.setVisible(false);

        dobPicker.setDate(new Date());
        var sm = new SimpleDateFormat("dd/MM/yyyy");
        dobPicker.setText(sm.format(dobPicker.getDate()));

        CustomLabelInForm.setColoredText(phoneLabel);
        CustomLabelInForm.setColoredText(nameLabel);
        CustomLabelInForm.setColoredText(emailLabel);
        CustomLabelInForm.setColoredText(dobLabel);
        CustomLabelInForm.setColoredText(addressLabel);
        CustomLabelInForm.setColoredText(genderLabel);
        hiddenPropLabel.setVisible(true);

        phoneField.requestFocus();
    }

    private void setPlaceholder(JTextArea textArea, String placeholder) {
        textArea.setText(placeholder);
        textArea.setForeground(UIManager.getColor("mutedColor"));

        textArea.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textArea.getText().equals(placeholder)) {
                    textArea.setText("");
                    textArea.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textArea.getText().isEmpty()) {
                    textArea.setText(placeholder);
                    textArea.setForeground(UIManager.getColor("mutedColor"));
                }
            }
        });
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
        dobLabel = new javax.swing.JLabel();
        hiddenPropLabel = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();
        addBookButton = new javax.swing.JButton();
        dobPicker = new com.group06.bsms.components.DatePickerPanel();
        phoneLabel = new javax.swing.JLabel();
        phoneField = new javax.swing.JTextField();
        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        emailLabel = new javax.swing.JLabel();
        emailField = new javax.swing.JTextField();
        addressLabel = new javax.swing.JLabel();
        addressField = new javax.swing.JTextField();
        genderComboBox = new javax.swing.JComboBox<>();
        genderLabel = new javax.swing.JLabel();

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
        pageName.setText("Add member");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pageName)
                .addContainerGap(474, Short.MAX_VALUE))
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

        dobLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        dobLabel.setLabelFor(dobPicker);
        dobLabel.setText("Date of Birth");

        hiddenPropLabel.setFont(new java.awt.Font("Segoe UI", 2, 13)); // NOI18N
        hiddenPropLabel.setText("The new member will have a discount of ........ %");
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

        addBookButton.setBackground(new java.awt.Color(65, 105, 225));
        addBookButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        addBookButton.setForeground(new java.awt.Color(255, 255, 255));
        addBookButton.setMnemonic(java.awt.event.KeyEvent.VK_A);
        addBookButton.setText("Add");
        addBookButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addBookButton.setDisplayedMnemonicIndex(0);
        addBookButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBookButtonActionPerformed(evt);
            }
        });

        dobPicker.setMaximumSize(new java.awt.Dimension(215, 31));
        dobPicker.setPlaceholder("dd/mm/yyyy");
        dobPicker.setPreferredSize(new java.awt.Dimension(215, 31));

        phoneLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        phoneLabel.setText("Phone");

        phoneField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        phoneField.setMinimumSize(new java.awt.Dimension(215, 31));
        phoneField.setPreferredSize(new java.awt.Dimension(215, 31));

        nameLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        nameLabel.setText("Name");

        nameField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        nameField.setMinimumSize(new java.awt.Dimension(215, 31));
        nameField.setPreferredSize(new java.awt.Dimension(215, 31));

        emailLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        emailLabel.setText("Email");

        emailField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        emailField.setMinimumSize(new java.awt.Dimension(215, 31));
        emailField.setPreferredSize(new java.awt.Dimension(215, 31));

        addressLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        addressLabel.setText("Address");

        addressField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        addressField.setMinimumSize(new java.awt.Dimension(215, 31));
        addressField.setPreferredSize(new java.awt.Dimension(215, 31));

        genderComboBox.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        genderComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female" }));
        genderComboBox.setPreferredSize(new java.awt.Dimension(154, 28));
        genderComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genderComboBoxActionPerformed(evt);
            }
        });

        genderLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        genderLabel.setLabelFor(dobPicker);
        genderLabel.setText("Gender");

        javax.swing.GroupLayout groupFieldPanelLayout = new javax.swing.GroupLayout(groupFieldPanel);
        groupFieldPanel.setLayout(groupFieldPanelLayout);
        groupFieldPanelLayout.setHorizontalGroup(
            groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                .addContainerGap(91, Short.MAX_VALUE)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hiddenPropLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 423, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(groupFieldPanelLayout.createSequentialGroup()
                            .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(phoneLabel)
                                .addComponent(phoneField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(nameLabel)
                                .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, groupFieldPanelLayout.createSequentialGroup()
                            .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(genderComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                    .addComponent(genderLabel)
                                    .addGap(0, 0, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, groupFieldPanelLayout.createSequentialGroup()
                                    .addGap(0, 0, Short.MAX_VALUE)
                                    .addComponent(addressLabel)
                                    .addGap(166, 166, 166)))
                            .addGap(227, 227, 227))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, groupFieldPanelLayout.createSequentialGroup()
                            .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(addBookButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(groupFieldPanelLayout.createSequentialGroup()
                            .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(emailLabel)
                                .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(dobPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(dobLabel)))
                        .addComponent(addressField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(94, Short.MAX_VALUE))
        );
        groupFieldPanelLayout.setVerticalGroup(
            groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                        .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                .addComponent(phoneLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(phoneField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                .addComponent(nameLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(emailLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                        .addComponent(dobLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dobPicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(addressLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addressField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(genderLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(genderComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(hiddenPropLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addBookButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(429, Short.MAX_VALUE))
        );

        jScrollForm.setViewportView(groupFieldPanel);

        add(jScrollForm, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cancelButtonActionPerformed
        phoneField.setText("");
        nameField.setText("");
        emailField.setText("");
        addressField.setText("");
        dobPicker.setDate(new Date());
        var sm = new SimpleDateFormat("dd/MM/yyyy");
        dobPicker.setText(sm.format(dobPicker.getDate()));

    }// GEN-LAST:event_cancelButtonActionPerformed

    private void addBookButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addBookButtonActionPerformed
        String phone = phoneField.getText();
        String name = nameField.getText();
        String email = emailField.getText();
        String gender = (String) genderComboBox.getSelectedItem();
        String address = addressField.getText();

        try {
            java.sql.Date dateOfBirth = dobPicker.getDateSQL();
            Member member = new Member();
            member.phone = phone;
            member.name = name;
            member.email = email;
            member.gender = gender;
            member.dateOfBirth = dateOfBirth;
            member.address = address;
            memberService.insertMember(member);
            cancelButtonActionPerformed(null);

            JOptionPane.showMessageDialog(null, "Member added successfully.", "BSMS Information",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            if (ex.getMessage().contains("member_phone_key")) {
                JOptionPane.showMessageDialog(null, "A member with this phone number already exists", "BSMS Error", JOptionPane.ERROR_MESSAGE);
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
        Dashboard.dashboard.switchTab("memberCRUD");
    }//GEN-LAST:event_backButtonActionPerformed

    private void genderComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genderComboBoxActionPerformed

    }//GEN-LAST:event_genderComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBookButton;
    private javax.swing.JTextField addressField;
    private javax.swing.JLabel addressLabel;
    private javax.swing.JButton backButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel dobLabel;
    private com.group06.bsms.components.DatePickerPanel dobPicker;
    private javax.swing.JTextField emailField;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JComboBox<String> genderComboBox;
    private javax.swing.JLabel genderLabel;
    private javax.swing.JPanel groupFieldPanel;
    private javax.swing.JLabel hiddenPropLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollForm;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel pageName;
    private javax.swing.JTextField phoneField;
    private javax.swing.JLabel phoneLabel;
    // End of variables declaration//GEN-END:variables
}

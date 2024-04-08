package com.group06.bsms.categories;

import com.group06.bsms.DB;
import com.group06.bsms.components.*;
import com.group06.bsms.dashboard.Dashboard;
import com.group06.bsms.utils.SVGHelper;
import java.awt.*;
import javax.swing.*;

public class UpdateCategory extends javax.swing.JPanel {

    private CategoryCRUD categoryCRUD;
    private final CategoryService categoryService;
    private Category category;

    public void setCategoryCRUD(CategoryCRUD categoryCRUD) {
        this.categoryCRUD = categoryCRUD;
    }

    public UpdateCategory() {
        this(
                null,
                new CategoryService(new CategoryRepository(DB.db()))
        );
    }

    public UpdateCategory(CategoryCRUD categoryCRUD) {
        this(
                categoryCRUD,
                new CategoryService(new CategoryRepository(DB.db()))
        );
    }

    public UpdateCategory(
            CategoryCRUD categoryCRUD,
            CategoryService categoryService
    ) {
        this.categoryCRUD = categoryCRUD;
        this.categoryService = categoryService;
        initComponents();

        nameField.putClientProperty("JTextField.placeholderText", "Category name");

        CustomLabelInForm.setColoredText(nameLabel);

        nameField.requestFocus();
    }

    public void setCategoryById(int categoryId) {
        try {
            category = categoryService.getCategory(categoryId);
            loadCategoryInto();
        } catch (Exception e) {
            category = null;

            JOptionPane.showMessageDialog(null,
                    "An error occurred while getting category information: " + e.getMessage(),
                    "BSMS Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCategoryInto() {
        try {
            nameField.setText(category.name);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "An error occurred while getting category information: " + e.getMessage(),
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
        pageName.setText("Update category");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pageName)
                .addContainerGap(413, Short.MAX_VALUE))
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
                addCategoryButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout groupFieldPanelLayout = new javax.swing.GroupLayout(groupFieldPanel);
        groupFieldPanel.setLayout(groupFieldPanelLayout);
        groupFieldPanelLayout.setHorizontalGroup(
            groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                .addContainerGap(91, Short.MAX_VALUE)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, groupFieldPanelLayout.createSequentialGroup()
                            .addGap(346, 346, 346)
                            .addComponent(addCategoryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(nameLabel))
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        Dashboard.dashboard.switchTab("categoryCRUD");
    }//GEN-LAST:event_backButtonActionPerformed

    private void addCategoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCategoryButtonActionPerformed
        String name = nameField.getText();
        try {
            if (category == null) {
                throw new Exception("Category data is empty");
            }

            Category updatedCategory = new Category(
                    category.id, name, category.isHidden
            );
            categoryService.updateCategory(category, updatedCategory);

            JOptionPane.showMessageDialog(null, "Category updated successfully.", "BSMS Information",
                    JOptionPane.INFORMATION_MESSAGE);

            categoryCRUD.reloadCategories(true);
            setCategoryById(category.id);
        } catch (Exception ex) {
            if (ex.getMessage().contains("category_name_key")) {
                JOptionPane.showMessageDialog(null, "A category with this name already exists", "BSMS Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "BSMS Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_addCategoryButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addCategoryButton;
    private javax.swing.JButton backButton;
    private javax.swing.JPanel groupFieldPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollForm;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel pageName;
    // End of variables declaration//GEN-END:variables
}

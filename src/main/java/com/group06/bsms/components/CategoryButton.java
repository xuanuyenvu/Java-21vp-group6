package com.group06.bsms.components;

import com.group06.bsms.utils.SVGHelper;
import java.awt.Color;
import java.awt.Dimension;

public class CategoryButton extends javax.swing.JPanel {

    private final CategorySelectionPanel categorySelectionPanel;

    public CategoryButton(CategorySelectionPanel categorySelectionPanel, String name) {
        initComponents();
        this.categorySelectionPanel = categorySelectionPanel;

        categoryName.setText(name);
        Dimension newSize = new Dimension(categoryName.getPreferredSize().width + 67, 28);
        this.setPreferredSize(newSize);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        categoryName = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        deleteButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(32767, 33));
        setMinimumSize(new java.awt.Dimension(50, 33));
        setPreferredSize(new java.awt.Dimension(60, 33));

        categoryName.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        categoryName.setText("History History");

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setPreferredSize(new java.awt.Dimension(7, 40));

        deleteButton.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        deleteButton.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/close.svg", 
            Color.black, Color.black, 
            14, 14
        ));
        deleteButton.setToolTipText("Delete");
        deleteButton.setBorderPainted(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        deleteButton.setFocusPainted(false);
        deleteButton.setFocusable(false);
        deleteButton.setPreferredSize(new java.awt.Dimension(28, 28));
        deleteButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                deleteButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                deleteButtonMouseExited(evt);
            }
        });
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(categoryName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(categoryName, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addGap(3, 3, 3))
            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        categorySelectionPanel.deleteCategory(categoryName.getText(), this);
        this.getParent().remove(this);
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void deleteButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deleteButtonMouseEntered
        deleteButton.setIcon(SVGHelper.createSVGIconWithFilter("icons/close.svg", Color.black, Color.gray, 14, 14));
    }//GEN-LAST:event_deleteButtonMouseEntered

    private void deleteButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deleteButtonMouseExited
        deleteButton.setIcon(SVGHelper.createSVGIconWithFilter("icons/close.svg", Color.black, Color.black, 14, 14));
    }//GEN-LAST:event_deleteButtonMouseExited

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel categoryName;
    private javax.swing.JButton deleteButton;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
}

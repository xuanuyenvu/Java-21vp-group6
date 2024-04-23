package com.group06.bsms.components;

import com.group06.bsms.utils.SVGHelper;
import java.awt.Color;
import java.awt.event.ActionEvent;

public class UpdateActionBtn extends javax.swing.JPanel {



    public UpdateActionBtn() {
        
        initComponents();
       
    }

    public void initEvent(TableActionEvent event, int row) {
        
        editBtn.addActionListener((ActionEvent e) -> {
            event.onEdit(row);
        });

    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        editBtn = new javax.swing.JButton();

        setToolTipText("");
        setPreferredSize(new java.awt.Dimension(97, 25));

        editBtn.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/search.svg",
            Color.black, Color.black,
            14, 14
        ));
        editBtn.setToolTipText("Edit");
        editBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        editBtn.setPreferredSize(new java.awt.Dimension(25, 25));
        editBtn.setRolloverEnabled(false);
        editBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(editBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(editBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void editBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editBtnActionPerformed

    }//GEN-LAST:event_editBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton editBtn;
    // End of variables declaration//GEN-END:variables
}

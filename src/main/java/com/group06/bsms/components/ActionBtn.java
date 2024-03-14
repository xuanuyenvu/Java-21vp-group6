package com.group06.bsms.components;

import com.group06.bsms.utils.SVGHelper;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;

public class ActionBtn extends javax.swing.JPanel {

    private Boolean hidden;

    public ActionBtn(boolean isHidden) {
        initComponents();
        this.hidden = isHidden;
        updateHideButtonAppearance();
    }

    public void initEvent(TableActionEvent event, int row, boolean isHidden) {
        this.hidden = isHidden;

        updateHideButtonAppearance();

        editBtn.addActionListener((ActionEvent e) -> {
            System.out.println("Edit button clicked!!!");
            event.onEdit(row);
        });

        hideBtn.addActionListener((ActionEvent e) -> {
            System.out.println("Hide button clicked!!!");
            hidden = (event.onHide(row));

            updateHideButtonAppearance();
        });
    }

    private void updateHideButtonAppearance() {
        if (hidden) {
            hideBtn.setIcon(SVGHelper.createSVGIconWithFilter("icons/unhide.svg", Color.black, Color.black, 14, 14));
            hideBtn.setToolTipText("Unhide");
        } else {
            hideBtn.setIcon(SVGHelper.createSVGIconWithFilter("icons/hide.svg", Color.black, Color.black, 14, 14));
            hideBtn.setToolTipText("Hide");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        editBtn = new javax.swing.JButton();
        hideBtn = new javax.swing.JButton();

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

        hideBtn.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/hide.svg",
            Color.black, Color.black,
            14, 14
        ));
        hideBtn.setToolTipText("Hide");
        hideBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        hideBtn.setPreferredSize(new java.awt.Dimension(25, 25));
        hideBtn.setRolloverEnabled(false);
        hideBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hideBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(editBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(hideBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(editBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(hideBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void editBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editBtnActionPerformed

    }//GEN-LAST:event_editBtnActionPerformed

    private void hideBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hideBtnActionPerformed

    }//GEN-LAST:event_hideBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton editBtn;
    private javax.swing.JButton hideBtn;
    // End of variables declaration//GEN-END:variables
}

package com.group06.bsms.books;

import com.group06.bsms.utils.SVGHelper;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ActionPanel extends javax.swing.JPanel {

    private Boolean isHideBtnHidden;

    public void setIsHideBtnHidden(boolean isHidden) {
        this.isHideBtnHidden = isHidden;
    }
    
    public boolean isIsHideBtnHidden() {
        return isHideBtnHidden;
    }

    public ActionPanel(boolean isHidden) {
        initComponents();
        this.isHideBtnHidden = isHidden;
        updateHideButtonAppearance();
    }

    public void initEvent(TableActionEvent event, int row, boolean isHidden) {
        
        this.isHideBtnHidden = isHidden;

        updateHideButtonAppearance();
        System.out.println("Action panel init, isHidden value: " + isHidden);
        
        editBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Edit button clicked!!!");
                event.onEdit(row);
            }
        });

        hideBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Hide button clicked!!!");
                event.onHide(row, isHideBtnHidden);
                isHideBtnHidden = !isHideBtnHidden;
                updateHideButtonAppearance();
            }
        });
    }

    private void updateHideButtonAppearance() {
        System.out.println("Updated Hide Button Appearance. State of isHideBtnHidden: "+isHideBtnHidden);
        if (isHideBtnHidden) {
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
        // TODO add your handling code here:
        System.out.println("Edit button clicked");

    }//GEN-LAST:event_editBtnActionPerformed

    private void hideBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hideBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hideBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton editBtn;
    private javax.swing.JButton hideBtn;
    // End of variables declaration//GEN-END:variables
}

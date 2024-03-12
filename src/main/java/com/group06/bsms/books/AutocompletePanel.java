package com.group06.bsms.books;

import com.formdev.flatlaf.FlatClientProperties;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxUI;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

public class AutocompletePanel extends javax.swing.JPanel {

    public AutocompletePanel() {
        initComponents();

        autoCompleteButton.setEditable(true);
        AutoCompleteDecorator.decorate(autoCompleteButton);
    }

    public void updateListButton(ArrayList<String> list) {
        for (String element : list) {
            autoCompleteButton.addItem(element);
        }
        autoCompleteButton.setSelectedItem(null);
    }

    public String getText() {
        Object selectedItem = autoCompleteButton.getSelectedItem();
        if (selectedItem != null) {
            return selectedItem.toString();
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        autoCompleteButton = new javax.swing.JComboBox<>();

        autoCompleteButton.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        autoCompleteButton.setPreferredSize(new java.awt.Dimension(72, 31));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(autoCompleteButton, 0, 188, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(autoCompleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> autoCompleteButton;
    // End of variables declaration//GEN-END:variables
}

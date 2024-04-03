package com.group06.bsms.components;

import java.util.List;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

public class AutocompletePanel<T extends Object> extends javax.swing.JPanel {

    // ArrayList<Object> listAllObjects = null;

    public AutocompletePanel() {
        initComponents();
        // listAllObjects = new ArrayList<>();

        autoCompleteButton.setEditable(true);
        AutoCompleteDecorator.decorate(autoCompleteButton);

    }

    public void updateList(List<T> list) {

        for (T element : list) {
            // method toString() return attr name of Object
            autoCompleteButton.addItem(element);
        }
        autoCompleteButton.setSelectedItem(null);
    }

    @SuppressWarnings("unchecked")
    public T getSelectedObject() {
        // text field = autoCompleteButton.getSelectedItem() => String
        // doc text field => neu no ton tai trong jcombobox, listObject
        // kiem tra trong listObejct coi coi textField nay ko
        // neu ko co thi return -1;
        // neu co thi tra id

        return (T) autoCompleteButton.getSelectedItem();
    }

    public void setSelectedObject(T object) {
        autoCompleteButton.setSelectedItem(object);
    }

    public String getText() {
        return autoCompleteButton.getSelectedItem().toString();
    }

    public void setEmptyText() {
        autoCompleteButton.setSelectedItem(null);
    }

    public void setPlaceHolderText(String text) {
        autoCompleteButton.putClientProperty("JTextField.placeholderText", text);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        autoCompleteButton = new javax.swing.JComboBox<>();

        setRequestFocusEnabled(false);

        autoCompleteButton.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        autoCompleteButton.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        autoCompleteButton.setOpaque(true);
        autoCompleteButton.setPreferredSize(new java.awt.Dimension(72, 31));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(autoCompleteButton, 0, 188, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(autoCompleteButton, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<T> autoCompleteButton;
    // End of variables declaration//GEN-END:variables

}
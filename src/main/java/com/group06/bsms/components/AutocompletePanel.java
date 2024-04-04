package com.group06.bsms.components;

import java.util.ArrayList;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

public class AutocompletePanel extends javax.swing.JPanel {

    public interface StateChanged {

        void run(java.awt.event.ItemEvent evt);
    }

    ArrayList<Object> listAllObjects = null;
    StateChanged stateChanged;

    public AutocompletePanel() {
        this((evt) -> {
        });
    }

    public AutocompletePanel(StateChanged stateChanged) {
        initComponents();
        listAllObjects = new ArrayList<>();
        this.stateChanged = stateChanged;

        autoCompleteButton.setEditable(true);
        AutoCompleteDecorator.decorate(autoCompleteButton);

    }

    public <Object> void updateList(ArrayList<Object> list) {
        listAllObjects.clear();
        listAllObjects = new ArrayList<>(list);

        for (Object element : list) {
            autoCompleteButton.addItem(element.toString());
        }
        autoCompleteButton.setSelectedItem(null);
    }

    public Object getSelectedObject() {
        int index = autoCompleteButton.getSelectedIndex();
        if (index == -1) {
            return null;
        }
        return listAllObjects.get(index);
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        autoCompleteButton = new javax.swing.JComboBox<>();

        setRequestFocusEnabled(false);

        autoCompleteButton.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        autoCompleteButton.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        autoCompleteButton.setOpaque(true);
        autoCompleteButton.setPreferredSize(new java.awt.Dimension(72, 31));
        autoCompleteButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                autoCompleteButtonItemStateChanged(evt);
            }
        });

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

    private void autoCompleteButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_autoCompleteButtonItemStateChanged
        try {
            if (autoCompleteButton.getSelectedItem() != null) {
                stateChanged.run(evt);
            }
        } catch (Exception e) {
        }
    }//GEN-LAST:event_autoCompleteButtonItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> autoCompleteButton;
    // End of variables declaration//GEN-END:variables
}

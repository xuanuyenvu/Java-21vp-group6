package com.group06.bsms.components;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.event.ItemEvent;
import java.util.ArrayList;

public class CategorySelectionPanel extends javax.swing.JPanel {

    private CategorySelectionListener listener;
    ArrayList<String> listSelected = null;
    private int count = 0;

    public CategorySelectionPanel() {
        initComponents();
        listSelected = new ArrayList<>();
    }

    private void createToggleButton(String name) {
        CategoryButton categoryButton = new CategoryButton(this, name);
        categoryButton.putClientProperty(FlatClientProperties.STYLE, "arc: 36;");
        categoriesPanel.add(categoryButton);
    }

    public void updateList(ArrayList<String> listUnselected, ArrayList<String> currentCategories) {
        for (String category : listUnselected) {
            addButton.addItem(category);
        }

        if (currentCategories != null) {
            listSelected.clear();
            listSelected = new ArrayList<>(currentCategories);
            for (String categorySelected : listSelected) {
                createToggleButton(categorySelected);
            }
        }

    }

    public void deleteCategory(String name) {
        listSelected.remove(name);

        categoriesPanel.revalidate();
        categoriesPanel.repaint();

        notifyListener();
    }

    public void setEmptyList() {
        listSelected.clear();
    }

    public <T extends CategorySelectionListener> void setCategorySelectionListener(T listener) {
        this.listener = listener;
    }

    private void notifyListener() {
        if (listener != null) {
            listener.onCategoriesChanged(listSelected.size());
        }
    }

    public ArrayList getListSelected() {
        return listSelected;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addButton = new javax.swing.JComboBox<>();
        categoriesPanel = new javax.swing.JPanel();

        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setPreferredSize(new java.awt.Dimension(0, 0));

        addButton.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        addButton.setToolTipText("Add category");
        addButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addButton.setMinimumSize(new java.awt.Dimension(30, 32));
        addButton.setName(""); // NOI18N
        addButton.setOpaque(true);
        addButton.setPreferredSize(new java.awt.Dimension(30, 32));
        addButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                addButtonItemStateChanged(evt);
            }
        });
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        categoriesPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        categoriesPanel.setPreferredSize(new java.awt.Dimension(0, 0));
        categoriesPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 6));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(categoriesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(categoriesPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_addButtonItemStateChanged
        if (count == 0) {
            count++;
        } else {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                String newCategory = String.valueOf(addButton.getSelectedItem());
                if (!listSelected.contains(newCategory)) {
                    listSelected.add(newCategory);
                    createToggleButton(newCategory);

                    categoriesPanel.revalidate();
                    categoriesPanel.repaint();

                    notifyListener();
                }
            }
        }
    }//GEN-LAST:event_addButtonItemStateChanged

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed

    }//GEN-LAST:event_addButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> addButton;
    private javax.swing.JPanel categoriesPanel;
    // End of variables declaration//GEN-END:variables
}

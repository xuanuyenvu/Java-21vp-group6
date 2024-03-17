package com.group06.bsms.components;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.event.ItemEvent;
import java.util.ArrayList;

public class CategorySelectionPanel extends javax.swing.JPanel {

    private CategorySelectionListener listener;
    ArrayList<String> listSelected = null;

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

    public String getText() {

        if (listSelected.isEmpty()) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            for (String category : listSelected) {
                sb.append(category).append(", ");
            }

            if (sb.length() > 2) {
                sb.delete(sb.length() - 2, sb.length());
            }
            return sb.toString();
        }
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
            System.out.println("duoc goi ne");
        }
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
        addButton.setDoubleBuffered(true);
        addButton.setLightWeightPopupEnabled(false);
        addButton.setMinimumSize(new java.awt.Dimension(29, 32));
        addButton.setName(""); // NOI18N
        addButton.setPreferredSize(new java.awt.Dimension(29, 32));
        addButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                addButtonItemStateChanged(evt);
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
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            String newCategory = String.valueOf(addButton.getSelectedItem());
            System.out.println("do ko");
            if (!listSelected.contains(newCategory)) {
                listSelected.add(newCategory);
                createToggleButton(newCategory);

                categoriesPanel.revalidate();
                categoriesPanel.repaint();

                notifyListener();
            }
        }
    }//GEN-LAST:event_addButtonItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> addButton;
    private javax.swing.JPanel categoriesPanel;
    // End of variables declaration//GEN-END:variables
}

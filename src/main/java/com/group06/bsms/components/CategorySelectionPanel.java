package com.group06.bsms.components;

import com.formdev.flatlaf.FlatClientProperties;
import com.group06.bsms.categories.Category;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.util.ArrayList;

public class CategorySelectionPanel extends javax.swing.JPanel {

    private CategorySelectionListener listener;
    ArrayList<Category> listAllCategories = null;
    ArrayList<Category> listSelected = null;
    private int count = 0;

    public CategorySelectionPanel() {
        initComponents();
        listAllCategories = new ArrayList<>();
        listSelected = new ArrayList<>();
    }

    private void createToggleButton(String name) {
        var categoryButton = new CategoryButton(this, name);
        categoryButton.putClientProperty(FlatClientProperties.STYLE, "arc: 36;");
        categoriesPanel.add(categoryButton);
    }

    public void updateList(ArrayList<Category> listAllCategories, ArrayList<Category> currentCategories) {
        this.listAllCategories.clear();
        this.listAllCategories = new ArrayList<>(listAllCategories);

        for (Category category : listAllCategories) {
            addButton.addItem(category.name);
        }

        if (currentCategories != null) {
            listSelected.clear();
            categoriesPanel.removeAll();

            for (Category categorySelected : currentCategories) {
                addCategory(categorySelected);
            }
        }

        addButton.setSelectedIndex(-1);
        this.changeSize(1.1f);
    }

    public void deleteCategory(String name, CategoryButton instance) {
        Category categoryToRemove = null;
        for (Category categorySelected : listSelected) {
            if (categorySelected.name.equals(name)) {
                categoryToRemove = categorySelected;
            }
        }

        listSelected.remove(categoryToRemove);

        addButton.setSelectedIndex(-1);

        notifyListener();
    }

    public void setEmptyList() {
        listSelected.clear();
        categoriesPanel.removeAll();
        addButton.setSelectedIndex(-1);
        this.revalidate();
        this.repaint();
    }

    public <T extends CategorySelectionListener> void setCategorySelectionListener(T listener) {
        this.listener = listener;
    }

    private void notifyListener() {
        if (listener != null) {
            listener.onCategoriesChanged(listSelected.size());
        }
    }

    public void changeSize(float numPerRow) {
        int numOfCategories = listSelected.size();
        int newHeight = 40 + ((int) (numOfCategories / numPerRow) * 35);
        this.setPreferredSize(new Dimension(getWidth(), newHeight));
    }

    public ArrayList<Category> getListSelected() {
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

    private void addCategory(Category category) {
        if (!listSelected.contains(category)) {
            listSelected.add(category);
            createToggleButton(category.name);

            categoriesPanel.revalidate();
            categoriesPanel.repaint();

            notifyListener();

        }
    }

    private void addButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_addButtonItemStateChanged
        if (count == 0) {
            count++;
            return;
        }

        if (evt.getStateChange() == ItemEvent.SELECTED) {
            int indexNewCategory = addButton.getSelectedIndex();

            if (!listSelected.contains(listAllCategories.get(indexNewCategory))) {
                listSelected.add(listAllCategories.get(indexNewCategory));
                createToggleButton(
                        listAllCategories.get(indexNewCategory).name
                );

                notifyListener();
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

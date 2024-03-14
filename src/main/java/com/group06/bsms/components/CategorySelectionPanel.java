package com.group06.bsms.components;

import com.formdev.flatlaf.FlatClientProperties;
import java.util.ArrayList;

public class CategorySelectionPanel extends javax.swing.JPanel {

    ArrayList<String> listSelected = null;
    private int heightPanel = 0;

    public CategorySelectionPanel() {
        initComponents();
        listSelected = new ArrayList<>();
        addButton.setToolTipText("Add category");
    }

    private void createToggleButton(String name) {
        CategoryButton categoryButton = new CategoryButton(this, name);
        categoryButton.putClientProperty(FlatClientProperties.STYLE, "arc: 36;");
        add(categoryButton);
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
        heightPanel = 45 * ((int) (listSelected.size() / 3) + 1);
        revalidate();
        repaint();
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

    public int getHeightPanel() {
        return heightPanel;
    }

    public void setEmptyList() {
        listSelected.clear();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addButton = new javax.swing.JComboBox<>();

        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 7, 6));

        addButton.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        addButton.setToolTipText("");
        addButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addButton.setMinimumSize(new java.awt.Dimension(29, 32));
        addButton.setName(""); // NOI18N
        addButton.setPreferredSize(new java.awt.Dimension(29, 32));
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        add(addButton);
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        String newCategory = String.valueOf(addButton.getSelectedItem());
        System.out.println("do ko");
        if (!listSelected.contains(newCategory)) {
            listSelected.add(newCategory);
            createToggleButton(newCategory);

            heightPanel = 45 * ((int) (listSelected.size() / 3) + 1);
            revalidate();
            repaint();
        }
    }//GEN-LAST:event_addButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> addButton;
    // End of variables declaration//GEN-END:variables
}

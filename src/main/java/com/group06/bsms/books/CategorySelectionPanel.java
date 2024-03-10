package com.group06.bsms.books;

import com.formdev.flatlaf.FlatClientProperties;
import com.group06.bsms.utils.SVGHelper;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class CategorySelectionPanel extends javax.swing.JPanel {

    ArrayList<String> listUnselected = null;
    ArrayList<String> listSelected = null;

    public CategorySelectionPanel() {
        initComponents();

        String[] categories = new String[]{
            "Comic",
            "Fiction",
            "Mystery",
            "Romance",
            "Thriller",
            "Travel",
            "History",
            "Poetry",
            "Business"
        };
        listUnselected = new ArrayList<>(Arrays.asList(categories));
        listSelected = new ArrayList<>();

        listSelected.add(categories[0]);
        listSelected.add(categories[6]);

        addButton.setToolTipText("Add category");

        Icon icon = SVGHelper.createSVGIconWithFilter("icons/add.svg", Color.black, Color.black, 16, 16);
        addButton.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton(icon);
                button.setBackground(UIManager.getColor("ComboBox.background"));
                button.setBorder(null);
                return button;
            }
        });
        
        addButton.putClientProperty(FlatClientProperties.STYLE, "arc: 9;");
        updateAddButton();

        if (!listSelected.isEmpty()) {
            for (String categorySelected : listSelected) {
                createToggleButton(categorySelected);
            }
        }
    }

    private void createToggleButton(String name) {
        CategoryButton categoryButton = new CategoryButton(this, name);
//        listUnselected.remove(name);
//        updateAddButton();
        categoryButton.putClientProperty(FlatClientProperties.STYLE, "arc: 90;");
        add(categoryButton);
    }

    private void updateAddButton() {
        for (String category : listUnselected) {
            addButton.addItem(category);
        }
    }

    public void deleteCategory(String name) {
        listSelected.remove(name);
//        listUnselected.add(name);
//        updateAddButton();

        revalidate();
        repaint();
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
        addButton.setMinimumSize(new java.awt.Dimension(28, 32));
        addButton.setName(""); // NOI18N
        addButton.setPreferredSize(new java.awt.Dimension(28, 32));
        add(addButton);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> addButton;
    // End of variables declaration//GEN-END:variables
}

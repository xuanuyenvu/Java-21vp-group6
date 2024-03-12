package com.group06.bsms.books;

import com.formdev.flatlaf.FlatClientProperties;
import com.group06.bsms.utils.SVGHelper;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.JLabel;

class CustomLabel {

    public static void setColoredText(JLabel label, String text, String textColor, String asteriskColor) {
        String labelText = "<html><font color='" + textColor + "'>" + text + "</font><font color='" + asteriskColor + "'> *</font></html>";
        label.setText(labelText);
    }
}

public class AddBookInformation extends javax.swing.JPanel {

    private final ArrayList<String> authors = new ArrayList<>();
    private final ArrayList<String> publishers = new ArrayList<>();

    public AddBookInformation() {
        initComponents();
        backButton.setIcon(SVGHelper.createSVGIconWithFilter("icons/arrow-back.svg", Color.black, Color.black, 24, 17));
        backButton.setToolTipText("Back to previous page");

        authors.add("J.K. Rowling");
        authors.add("Stephen King");
        authors.add("Ernest Hemingway");
        authors.add("Harper Lee");
        publishers.add("Toni Morrison");
        publishers.add("F. Scott Fitzgerald");
        publishers.add("Maya Angelou");
        authorAutoComp.updateListButton(authors);
        publisherAutoComp.updateListButton(publishers);

        titleLabel.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Book Title");
        scrollPane.putClientProperty(FlatClientProperties.STYLE,
                "arc: 9;"
                + "thumbArc: 3;"
                + "thumbInsets: 2,2,2,2;");

        CustomLabel.setColoredText(titleLabel, "Title", "black", "red");
        CustomLabel.setColoredText(authorLabel, "Author", "black", "red");
        CustomLabel.setColoredText(publisherLabel, "Publisher", "black", "red");
        CustomLabel.setColoredText(publishDateLabel, "Publisher Date", "black", "red");
        CustomLabel.setColoredText(categoryLabel, "Category", "black", "red");
        CustomLabel.setColoredText(dimensionLabel, "Dimension", "black", "red");
        CustomLabel.setColoredText(pagesLabel, "Pages", "black", "red");
        CustomLabel.setColoredText(translatorLabel, "Translator", "black", "red");
        CustomLabel.setColoredText(overviewLabel, "Overview", "black", "red");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backButton = new javax.swing.JButton();
        pageName = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        groupFieldPanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        titleField = new javax.swing.JTextField();
        authorLabel = new javax.swing.JLabel();
        publisherLabel = new javax.swing.JLabel();
        publishDateLabel = new javax.swing.JLabel();
        categoryLabel = new javax.swing.JLabel();
        categorySelectionPanel = new com.group06.bsms.books.CategorySelectionPanel();
        dimensionLabel = new javax.swing.JLabel();
        dimensionField = new javax.swing.JTextField();
        pagesLabel = new javax.swing.JLabel();
        translatorField = new javax.swing.JTextField();
        translatorLabel = new javax.swing.JLabel();
        overviewLabel = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        overviewTextArea = new javax.swing.JTextArea();
        hideCheckBox = new javax.swing.JCheckBox();
        hiddenPropLabel = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        cancelButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        publisherAutoComp = new com.group06.bsms.books.AutocompletePanel();
        authorAutoComp = new com.group06.bsms.books.AutocompletePanel();
        pagesSpinner = new javax.swing.JSpinner();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();

        backButton.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new java.awt.Dimension(33, 33));

        pageName.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        pageName.setText("Add book information");

        jScrollPane1.setBorder(null);
        jScrollPane1.setVerifyInputWhenFocusTarget(false);

        groupFieldPanel.setBorder(new org.jdesktop.swingx.border.IconBorder());
        groupFieldPanel.setMinimumSize(new java.awt.Dimension(440, 31));

        titleLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        titleLabel.setText("Title *");

        titleField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        titleField.setMinimumSize(new java.awt.Dimension(440, 31));
        titleField.setPreferredSize(new java.awt.Dimension(440, 31));

        authorLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        authorLabel.setText("Author *");

        publisherLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        publisherLabel.setText("Publisher *");

        publishDateLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        publishDateLabel.setText("Publish Date *");

        categoryLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        categoryLabel.setText("Category *");

        categorySelectionPanel.setAutoscrolls(true);
        categorySelectionPanel.setMaximumSize(new java.awt.Dimension(446, 32767));
        categorySelectionPanel.setMinimumSize(new java.awt.Dimension(446, 45));
        categorySelectionPanel.setPreferredSize(new java.awt.Dimension(446, 41));

        dimensionLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        dimensionLabel.setText("Dimension *");

        dimensionField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        dimensionField.setMinimumSize(new java.awt.Dimension(140, 31));
        dimensionField.setPreferredSize(new java.awt.Dimension(140, 31));

        pagesLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        pagesLabel.setText("Pages *");

        translatorField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        translatorField.setMinimumSize(new java.awt.Dimension(180, 31));
        translatorField.setPreferredSize(new java.awt.Dimension(180, 31));

        translatorLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        translatorLabel.setText("Translator *");

        overviewLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        overviewLabel.setText("Overview *");

        scrollPane.setAutoscrolls(true);

        overviewTextArea.setColumns(20);
        overviewTextArea.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        overviewTextArea.setLineWrap(true);
        overviewTextArea.setRows(5);
        overviewTextArea.setDragEnabled(true);
        overviewTextArea.setMinimumSize(new java.awt.Dimension(440, 20));
        scrollPane.setViewportView(overviewTextArea);

        hideCheckBox.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        hideCheckBox.setText("Hidden Book");

        hiddenPropLabel.setFont(new java.awt.Font("Segoe UI", 2, 13)); // NOI18N
        hiddenPropLabel.setText("note sth");
        hiddenPropLabel.setMinimumSize(new java.awt.Dimension(423, 18));
        hiddenPropLabel.setPreferredSize(new java.awt.Dimension(423, 18));

        cancelButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cancelButton.setText("Cancel");
        cancelButton.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        cancelButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        addButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        addButton.setText("Add");
        addButton.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        addButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        publisherAutoComp.setPreferredSize(new java.awt.Dimension(215, 31));

        authorAutoComp.setMinimumSize(new java.awt.Dimension(440, 31));
        authorAutoComp.setPreferredSize(new java.awt.Dimension(440, 31));

        pagesSpinner.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        pagesSpinner.setMinimumSize(new java.awt.Dimension(140, 31));
        pagesSpinner.setName(""); // NOI18N
        pagesSpinner.setPreferredSize(new java.awt.Dimension(140, 31));

        jXDatePicker1.setMaximumSize(new java.awt.Dimension(215, 31));
        jXDatePicker1.setMinimumSize(new java.awt.Dimension(215, 31));
        jXDatePicker1.setPreferredSize(new java.awt.Dimension(215, 31));

        javax.swing.GroupLayout groupFieldPanelLayout = new javax.swing.GroupLayout(groupFieldPanel);
        groupFieldPanel.setLayout(groupFieldPanelLayout);
        groupFieldPanelLayout.setHorizontalGroup(
            groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2)
            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                .addContainerGap(45, Short.MAX_VALUE)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                        .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(titleField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(authorAutoComp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(categoryLabel)
                                    .addComponent(authorLabel)
                                    .addComponent(titleLabel)
                                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                        .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(publisherLabel)
                                            .addComponent(publisherAutoComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                                .addGap(10, 10, 10)
                                                .addComponent(publishDateLabel))
                                            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jXDatePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addComponent(categorySelectionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(hideCheckBox))
                        .addContainerGap(45, Short.MAX_VALUE))
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                        .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(hiddenPropLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 423, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(overviewLabel)
                                        .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                            .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(dimensionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(dimensionLabel))
                                            .addGap(10, 10, 10)
                                            .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(pagesLabel)
                                                .addComponent(pagesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGap(10, 10, 10)
                                            .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(translatorLabel)
                                                .addComponent(translatorField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addComponent(scrollPane))))
                            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        groupFieldPanelLayout.setVerticalGroup(
            groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(titleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(titleField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(authorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(authorAutoComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(publisherLabel)
                    .addComponent(publishDateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(publisherAutoComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jXDatePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(categoryLabel)
                .addGap(0, 0, 0)
                .addComponent(categorySelectionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pagesLabel)
                    .addComponent(translatorLabel)
                    .addComponent(dimensionLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(translatorField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pagesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dimensionField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(overviewLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(hideCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hiddenPropLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(427, 427, 427))
        );

        jScrollPane1.setViewportView(groupFieldPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pageName)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pageName, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 920, Short.MAX_VALUE)
                .addContainerGap(171, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        String title = titleField.getText();
        String author = authorAutoComp.getText();
        String publisher = publisherAutoComp.getText();
        String category = categorySelectionPanel.getText();
        String dimension = dimensionField.getText();
        Object pages = pagesSpinner.getValue();
        String translator = translatorField.getText();
        String overview = overviewTextArea.getText();
        boolean hideChecked = hideCheckBox.isSelected();

        if (title != null && !title.isEmpty()
                && author != null && !author.isEmpty()
                && publisher != null && !publisher.isEmpty()
                && category != null && !category.isEmpty()
                && dimension != null && !dimension.isEmpty()
                && pages != null
                && translator != null && !translator.isEmpty()
                && overview != null && !overview.isEmpty()) {

            String newBookInfo = title + "; "
                    + author + "; "
                    + publisher + "; "
                    + category + "; "
                    + dimension + "; "
                    + pages + "; "
                    + translator + "; "
                    + overview + "; "
                    + hideChecked;
            System.out.print(newBookInfo);
        } else {
            
        }
    }//GEN-LAST:event_addButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private com.group06.bsms.books.AutocompletePanel authorAutoComp;
    private javax.swing.JLabel authorLabel;
    private javax.swing.JButton backButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel categoryLabel;
    private com.group06.bsms.books.CategorySelectionPanel categorySelectionPanel;
    private javax.swing.JTextField dimensionField;
    private javax.swing.JLabel dimensionLabel;
    private javax.swing.JPanel groupFieldPanel;
    private javax.swing.JLabel hiddenPropLabel;
    private javax.swing.JCheckBox hideCheckBox;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private javax.swing.JLabel overviewLabel;
    private javax.swing.JTextArea overviewTextArea;
    private javax.swing.JLabel pageName;
    private javax.swing.JLabel pagesLabel;
    private javax.swing.JSpinner pagesSpinner;
    private javax.swing.JLabel publishDateLabel;
    private com.group06.bsms.books.AutocompletePanel publisherAutoComp;
    private javax.swing.JLabel publisherLabel;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextField titleField;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextField translatorField;
    private javax.swing.JLabel translatorLabel;
    // End of variables declaration//GEN-END:variables
}

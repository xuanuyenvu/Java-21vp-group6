package com.group06.bsms.books;

import com.formdev.flatlaf.FlatClientProperties;
import com.group06.bsms.utils.SVGHelper;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;



public class UpdateBook extends javax.swing.JPanel {

    private final ArrayList<String> authors = new ArrayList<>();
    private final ArrayList<String> publishers = new ArrayList<>();

    public UpdateBook() {
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

        CustomLabel.setColoredText(titleLabel, "Title", "#666666", "red");
        CustomLabel.setColoredText(authorLabel, "Author", "#666666", "red");
        CustomLabel.setColoredText(publisherLabel, "Publisher", "#666666", "red");
        CustomLabel.setColoredText(publishDateLabel, "Publisher Date", "#666666", "red");
        CustomLabel.setColoredText(categoryLabel, "Category", "#666666", "red");
        CustomLabel.setColoredText(dimensionLabel, "Dimension", "#666666", "red");
        CustomLabel.setColoredText(pagesLabel, "Pages", "#666666", "red");
        CustomLabel.setColoredText(overviewLabel, "Overview", "#666666", "red");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFormattedTextField1 = new javax.swing.JFormattedTextField();
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
        categorySelectionPanel = new com.group06.bsms.books.components.CategorySelectionPanel();
        dimensionLabel = new javax.swing.JLabel();
        dimensionField = new javax.swing.JTextField();
        pagesLabel = new javax.swing.JLabel();
        translatorField = new javax.swing.JTextField();
        translatorLabel = new javax.swing.JLabel();
        overviewLabel = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        overviewTextArea = new javax.swing.JTextArea();
        cancelButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        publisherAutoComp = new com.group06.bsms.books.components.AutocompletePanel();
        authorAutoComp = new com.group06.bsms.books.components.AutocompletePanel();
        pagesSpinner = new javax.swing.JSpinner();
        publishDatePicker = new org.jdesktop.swingx.JXDatePicker();
        importPriceLabel = new javax.swing.JLabel();
        importPriceField = new javax.swing.JTextField();
        salePriceLabel = new javax.swing.JLabel();
        salePriceField = new javax.swing.JFormattedTextField();

        jFormattedTextField1.setText("jFormattedTextField1");

        backButton.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new java.awt.Dimension(33, 33));
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                backButtonMouseExited(evt);
            }
        });

        pageName.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        pageName.setText("Update book");

        jScrollPane1.setBorder(null);
        jScrollPane1.setVerifyInputWhenFocusTarget(false);

        groupFieldPanel.setBorder(new org.jdesktop.swingx.border.IconBorder());
        groupFieldPanel.setMinimumSize(new java.awt.Dimension(440, 31));

        titleLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        titleLabel.setForeground(new java.awt.Color(102, 102, 102));
        titleLabel.setText("Title *");

        titleField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        titleField.setMinimumSize(new java.awt.Dimension(440, 31));
        titleField.setPreferredSize(new java.awt.Dimension(440, 31));
        titleField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                titleFieldActionPerformed(evt);
            }
        });

        authorLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        authorLabel.setForeground(new java.awt.Color(102, 102, 102));
        authorLabel.setText("Author *");

        publisherLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        publisherLabel.setForeground(new java.awt.Color(102, 102, 102));
        publisherLabel.setText("Publisher *");

        publishDateLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        publishDateLabel.setForeground(new java.awt.Color(102, 102, 102));
        publishDateLabel.setText("Publish Date *");

        categoryLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        categoryLabel.setForeground(new java.awt.Color(102, 102, 102));
        categoryLabel.setText("Category *");

        categorySelectionPanel.setAutoscrolls(true);
        categorySelectionPanel.setMaximumSize(new java.awt.Dimension(449, 32767));
        categorySelectionPanel.setMinimumSize(new java.awt.Dimension(449, 45));
        categorySelectionPanel.setPreferredSize(new java.awt.Dimension(449, 45));

        dimensionLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        dimensionLabel.setForeground(new java.awt.Color(102, 102, 102));
        dimensionLabel.setText("Dimension *");

        dimensionField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        dimensionField.setMinimumSize(new java.awt.Dimension(140, 31));
        dimensionField.setPreferredSize(new java.awt.Dimension(140, 31));

        pagesLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        pagesLabel.setForeground(new java.awt.Color(102, 102, 102));
        pagesLabel.setText("Pages *");

        translatorField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        translatorField.setMinimumSize(new java.awt.Dimension(180, 31));
        translatorField.setPreferredSize(new java.awt.Dimension(180, 31));

        translatorLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        translatorLabel.setForeground(new java.awt.Color(102, 102, 102));
        translatorLabel.setText("Translator");

        overviewLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        overviewLabel.setForeground(new java.awt.Color(102, 102, 102));
        overviewLabel.setText("Overview *");

        scrollPane.setAutoscrolls(true);

        overviewTextArea.setColumns(20);
        overviewTextArea.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        overviewTextArea.setLineWrap(true);
        overviewTextArea.setRows(5);
        overviewTextArea.setDragEnabled(true);
        overviewTextArea.setMinimumSize(new java.awt.Dimension(440, 20));
        scrollPane.setViewportView(overviewTextArea);

        cancelButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cancelButton.setForeground(new java.awt.Color(177, 177, 177));
        cancelButton.setText("Cancel");
        cancelButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cancelButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                cancelButtonMouseExited(evt);
            }
        });
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        addButton.setBackground(new java.awt.Color(65, 105, 225));
        addButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        addButton.setForeground(new java.awt.Color(255, 255, 255));
        addButton.setText("Add");
        addButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                addButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addButtonMouseExited(evt);
            }
        });
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

        publishDatePicker.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        publishDatePicker.setMaximumSize(new java.awt.Dimension(215, 31));
        publishDatePicker.setMinimumSize(new java.awt.Dimension(215, 31));
        publishDatePicker.setPreferredSize(new java.awt.Dimension(215, 31));

        importPriceLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        importPriceLabel.setForeground(new java.awt.Color(102, 102, 102));
        importPriceLabel.setText("Import Price *");

        importPriceField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        importPriceField.setText("Hello world");
        importPriceField.setEnabled(false);
        importPriceField.setMinimumSize(new java.awt.Dimension(140, 31));
        importPriceField.setPreferredSize(new java.awt.Dimension(140, 31));

        salePriceLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        salePriceLabel.setForeground(new java.awt.Color(102, 102, 102));
        salePriceLabel.setText("Sale Price *");

        salePriceField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        salePriceField.setMinimumSize(new java.awt.Dimension(140, 31));
        salePriceField.setPreferredSize(new java.awt.Dimension(140, 31));
        salePriceField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                salePriceFieldKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout groupFieldPanelLayout = new javax.swing.GroupLayout(groupFieldPanel);
        groupFieldPanel.setLayout(groupFieldPanelLayout);
        groupFieldPanelLayout.setHorizontalGroup(
            groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                .addContainerGap(70, Short.MAX_VALUE)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
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
                                            .addComponent(publishDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addComponent(categorySelectionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(70, Short.MAX_VALUE))
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                                    .addComponent(scrollPane))
                                .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                    .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(importPriceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(importPriceLabel))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(salePriceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(salePriceLabel)))))
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
                    .addComponent(publishDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(categoryLabel)
                .addGap(0, 0, 0)
                .addComponent(categorySelectionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pagesLabel)
                    .addComponent(translatorLabel)
                    .addComponent(dimensionLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(translatorField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pagesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dimensionField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(overviewLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                        .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(importPriceLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(importPriceField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                        .addComponent(salePriceLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(salePriceField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(35, 35, 35)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(465, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(groupFieldPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pageName)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jScrollPane1)
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
                .addComponent(jScrollPane1))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        titleField.setText("");
        authorAutoComp.setEmptyText();
        publisherAutoComp.setEmptyText();
        categorySelectionPanel.setEmptyList();
        dimensionField.setText("");
        pagesSpinner.setValue(0);
        translatorField.setText("");
        overviewTextArea.setText("");
        importPriceField.setText("");
        salePriceField.setText("");
        
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
        

        if (!title.isEmpty() && !author.isEmpty()
                && !publisher.isEmpty() && publishDatePicker.getDate() != null
                && !category.isEmpty() && !dimension.isEmpty()
                && !pages.equals(0) && !overview.isEmpty()) {
            
            java.sql.Date publishDate = new java.sql.Date(publishDatePicker.getDate().getTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = dateFormat.format(publishDate);

            String newBookInfo = title + "; "
                    + author + "; "
                    + publisher + "; "
                    + formattedDate + "; "
                    + category + "; "
                    + dimension + "; "
                    + pages + "; "
                    + translator + "; "
                    + overview + "; "
                    ;
            System.out.print(newBookInfo);
        } else {
            JOptionPane.showMessageDialog(null, "Please fill in all required information!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void addButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addButtonMouseEntered
        addButton.setBackground(new Color(59, 98, 214));
    }//GEN-LAST:event_addButtonMouseEntered

    private void addButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_addButtonMouseExited
        addButton.setBackground(new Color(65, 105, 225));
    }//GEN-LAST:event_addButtonMouseExited

    private void cancelButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelButtonMouseExited
        cancelButton.setBackground(Color.white);
    }//GEN-LAST:event_cancelButtonMouseExited

    private void cancelButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cancelButtonMouseEntered
        cancelButton.setBackground(new Color(250, 250, 250));
    }//GEN-LAST:event_cancelButtonMouseEntered

    private void backButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backButtonMouseEntered
        backButton.setIcon(SVGHelper.createSVGIconWithFilter("icons/arrow-back.svg", Color.black, Color.gray, 24, 17));
    }//GEN-LAST:event_backButtonMouseEntered

    private void backButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backButtonMouseExited
        backButton.setIcon(SVGHelper.createSVGIconWithFilter("icons/arrow-back.svg", Color.black, Color.black, 24, 17));
    }//GEN-LAST:event_backButtonMouseExited

    private void titleFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_titleFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_titleFieldActionPerformed

    private void salePriceFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_salePriceFieldKeyTyped
        // TODO add your handling code here:
        char enter = evt.getKeyChar();
        if(!(Character.isDigit(enter))){
            evt.consume();
        }
    }//GEN-LAST:event_salePriceFieldKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private com.group06.bsms.books.components.AutocompletePanel authorAutoComp;
    private javax.swing.JLabel authorLabel;
    private javax.swing.JButton backButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel categoryLabel;
    private com.group06.bsms.books.components.CategorySelectionPanel categorySelectionPanel;
    private javax.swing.JTextField dimensionField;
    private javax.swing.JLabel dimensionLabel;
    private javax.swing.JPanel groupFieldPanel;
    private javax.swing.JTextField importPriceField;
    private javax.swing.JLabel importPriceLabel;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel overviewLabel;
    private javax.swing.JTextArea overviewTextArea;
    private javax.swing.JLabel pageName;
    private javax.swing.JLabel pagesLabel;
    private javax.swing.JSpinner pagesSpinner;
    private javax.swing.JLabel publishDateLabel;
    private org.jdesktop.swingx.JXDatePicker publishDatePicker;
    private com.group06.bsms.books.components.AutocompletePanel publisherAutoComp;
    private javax.swing.JLabel publisherLabel;
    private javax.swing.JFormattedTextField salePriceField;
    private javax.swing.JLabel salePriceLabel;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextField titleField;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextField translatorField;
    private javax.swing.JLabel translatorLabel;
    // End of variables declaration//GEN-END:variables
}

package com.group06.bsms.books;

import com.group06.bsms.DB;
import com.group06.bsms.components.*;
import com.group06.bsms.authors.*;
import com.group06.bsms.categories.*;
import com.group06.bsms.publishers.*;
import com.group06.bsms.utils.SVGHelper;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class AddBookInformation extends javax.swing.JPanel implements CategorySelectionListener {

    private final BookService bookService;
    private final AuthorService authorService;
    private final PublisherService publisherService;
    private final CategoryService categoryService;

    public AddBookInformation() {
        this(
                new BookService(
                        new BookRepository(DB.db()),
                        new AuthorService(new AuthorRepository(DB.db())),
                        new PublisherService(new PublisherRepository(DB.db())),
                        new CategoryService(new CategoryRepository(DB.db()))),
                new AuthorService(new AuthorRepository(DB.db())),
                new PublisherService(new PublisherRepository(DB.db())),
                new CategoryService(new CategoryRepository(DB.db())));
    }

    public AddBookInformation(BookService bookService, AuthorService authorService, PublisherService publisherService,
            CategoryService categoryService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.publisherService = publisherService;
        this.categoryService = categoryService;
        initComponents();
        hiddenPropLabel.setVisible(false);

        loadAuthorInto();
        loadPublisherInto();
        loadCategoryInto();
        categorySelectionPanel.setCategorySelectionListener(this);

        titleField.putClientProperty("JTextField.placeholderText", "Enter book title");
        dimensionField.putClientProperty("JTextField.placeholderText", "Length x width x height");
        translatorField.putClientProperty("JTextField.placeholderText", "Enter translator's name");
        setPlaceholder(overviewTextArea, "Enter overview description");

        CustomLabelInForm.setColoredText(titleLabel);
        CustomLabelInForm.setColoredText(authorLabel);
        CustomLabelInForm.setColoredText(publisherLabel);
        CustomLabelInForm.setColoredText(publishDateLabel);
        CustomLabelInForm.setColoredText(categoryLabel);
        CustomLabelInForm.setColoredText(dimensionLabel);
        CustomLabelInForm.setColoredText(pagesLabel);
        CustomLabelInForm.setColoredText(overviewLabel);

        titleField.requestFocus();
    }

    private void setPlaceholder(JTextArea textArea, String placeholder) {
        textArea.setText(placeholder);
        textArea.setForeground(UIManager.getColor("mutedColor"));

        textArea.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textArea.getText().equals(placeholder)) {
                    textArea.setText("");
                    textArea.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textArea.getText().isEmpty()) {
                    textArea.setText(placeholder);
                    textArea.setForeground(UIManager.getColor("mutedColor"));
                }
            }
        });
    }

    @Override
    public void onCategoriesChanged(int numOfCategories) {
        int newHeight = (40 + ((int) (numOfCategories / 3.1) * 35));
        categorySelectionPanel.setPreferredSize(new Dimension(categorySelectionPanel.getWidth(), newHeight));
        jScrollForm.revalidate();
        jScrollForm.repaint();
    }

    private void loadAuthorInto() {
        try {
            var authors = new ArrayList<Author>(authorService.selectAllAuthors());
            if (authors == null) {
                throw new NullPointerException();
            }

            authorAutoComp.updateList(authors);

        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null, "An error occurred while getting author information: " + e.getMessage(),
                    "BSMS Error", JOptionPane.ERROR_MESSAGE);
        } catch (Throwable e) {
            JOptionPane.showMessageDialog(null, "An unspecified error occurred: " + e.getMessage(), "BSMS Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPublisherInto() {
        try {
            var publishers = new ArrayList<Publisher>(publisherService.selectAllPublishers());
            if (publishers == null) {
                throw new NullPointerException();
            }

            publisherAutoComp.updateList(publishers);

        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null,
                    "An error occurred while getting publisher information: " + e.getMessage(), "BSMS Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Throwable e) {
            JOptionPane.showMessageDialog(null, "An unspecified error occurred: " + e.getMessage(), "BSMS Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCategoryInto() {
        try {
            var categories = new ArrayList<Category>(categoryService.selectAllCategories());
            if (categories == null) {
                throw new NullPointerException();
            }

            categorySelectionPanel.updateList(categories, null);

        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null,
                    "An error occurred while getting category information: " + e.getMessage(), "BSMS Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Throwable e) {
            JOptionPane.showMessageDialog(null, "An unspecified error occurred: " + e.getMessage(), "BSMS Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backButton = new javax.swing.JButton();
        pageName = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollForm = new javax.swing.JScrollPane();
        groupFieldPanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        titleField = new javax.swing.JTextField();
        authorLabel = new javax.swing.JLabel();
        publisherLabel = new javax.swing.JLabel();
        publishDateLabel = new javax.swing.JLabel();
        categoryLabel = new javax.swing.JLabel();
        categorySelectionPanel = new com.group06.bsms.components.CategorySelectionPanel();
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
        cancelButton = new javax.swing.JButton();
        addBookButton = new javax.swing.JButton();
        publisherAutoComp = new com.group06.bsms.components.AutocompletePanel();
        authorAutoComp = new com.group06.bsms.components.AutocompletePanel();
        pagesSpinner = new javax.swing.JSpinner();
        publishDatePicker = new com.group06.bsms.components.DatePickerPanel();

        backButton.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        backButton.setForeground(UIManager.getColor("mutedColor"));
        backButton.setIcon(SVGHelper.createSVGIconWithFilter(
                "icons/arrow-back.svg",
                Color.white, Color.white,
                22, 18));
        backButton.setToolTipText("Back to previous page");
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        backButton.setFocusable(false);
        backButton.setMargin(new java.awt.Insets(4, 14, 3, 14));
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
        pageName.setText("Add book information");

        jScrollForm.setBorder(null);
        jScrollForm.setVerifyInputWhenFocusTarget(false);

        groupFieldPanel.setBorder(new org.jdesktop.swingx.border.IconBorder());
        groupFieldPanel.setMinimumSize(new java.awt.Dimension(440, 31));

        titleLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        titleLabel.setLabelFor(titleField);
        titleLabel.setText("Title");

        titleField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        titleField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        titleField.setMinimumSize(new java.awt.Dimension(440, 31));
        titleField.setPreferredSize(new java.awt.Dimension(440, 31));

        authorLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        authorLabel.setLabelFor(authorAutoComp);
        authorLabel.setText("Author");

        publisherLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        publisherLabel.setLabelFor(publisherAutoComp);
        publisherLabel.setText("Publisher");

        publishDateLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        publishDateLabel.setLabelFor(groupFieldPanel);
        publishDateLabel.setText("Publish Date");

        categoryLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        categoryLabel.setLabelFor(categorySelectionPanel);
        categoryLabel.setText("Category");

        categorySelectionPanel.setAutoscrolls(true);
        categorySelectionPanel.setMaximumSize(new java.awt.Dimension(440, 32767));
        categorySelectionPanel.setMinimumSize(new java.awt.Dimension(440, 40));
        categorySelectionPanel.setPreferredSize(new java.awt.Dimension(440, 40));

        dimensionLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        dimensionLabel.setLabelFor(dimensionField);
        dimensionLabel.setText("Dimension");

        dimensionField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        dimensionField.setMinimumSize(new java.awt.Dimension(215, 31));
        dimensionField.setPreferredSize(new java.awt.Dimension(215, 31));

        pagesLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        pagesLabel.setLabelFor(pagesSpinner);
        pagesLabel.setText("Pages");

        translatorField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        translatorField.setMinimumSize(new java.awt.Dimension(440, 31));
        translatorField.setPreferredSize(new java.awt.Dimension(440, 31));

        translatorLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        translatorLabel.setLabelFor(translatorField);
        translatorLabel.setText("Translator");

        overviewLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        overviewLabel.setLabelFor(overviewTextArea);
        overviewLabel.setText("Overview");

        scrollPane.setAutoscrolls(true);

        overviewTextArea.setColumns(20);
        overviewTextArea.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        overviewTextArea.setLineWrap(true);
        overviewTextArea.setRows(5);
        overviewTextArea.setDragEnabled(true);
        overviewTextArea.setMaximumSize(new java.awt.Dimension(440, 2147483647));
        overviewTextArea.setMinimumSize(new java.awt.Dimension(440, 20));
        overviewTextArea.setPreferredSize(new java.awt.Dimension(440, 114));
        scrollPane.setViewportView(overviewTextArea);

        hideCheckBox.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        hideCheckBox.setText("Hidden Book");
        hideCheckBox.setIconTextGap(5);
        hideCheckBox.setMargin(new java.awt.Insets(2, 0, 2, 2));

        hiddenPropLabel.setFont(new java.awt.Font("Segoe UI", 2, 13)); // NOI18N
        hiddenPropLabel.setText("note sth");
        hiddenPropLabel.setMinimumSize(new java.awt.Dimension(423, 18));
        hiddenPropLabel.setPreferredSize(new java.awt.Dimension(423, 18));

        cancelButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        cancelButton.setForeground(UIManager.getColor("mutedColor"));
        cancelButton.setText("Cancel");
        cancelButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        addBookButton.setBackground(new java.awt.Color(65, 105, 225));
        addBookButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        addBookButton.setForeground(new java.awt.Color(255, 255, 255));
        addBookButton.setText("Add");
        addBookButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addBookButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBookButtonActionPerformed(evt);
            }
        });

        publisherAutoComp.setPlaceHolderText("Search by publisher's name");
        publisherAutoComp.setPreferredSize(new java.awt.Dimension(215, 31));
        publisherAutoComp.setRequestFocusEnabled(true);

        authorAutoComp.setMinimumSize(new java.awt.Dimension(440, 31));
        authorAutoComp.setPlaceHolderText("Search by author's name");
        authorAutoComp.setPreferredSize(new java.awt.Dimension(440, 31));
        authorAutoComp.setRequestFocusEnabled(true);

        pagesSpinner.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        pagesSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        pagesSpinner.setMinimumSize(new java.awt.Dimension(215, 31));
        pagesSpinner.setName(""); // NOI18N
        pagesSpinner.setPreferredSize(new java.awt.Dimension(215, 31));

        publishDatePicker.setMaximumSize(new java.awt.Dimension(215, 31));
        publishDatePicker.setPlaceholder("dd/mm/yyyy");
        publishDatePicker.setPreferredSize(new java.awt.Dimension(215, 31));

        javax.swing.GroupLayout groupFieldPanelLayout = new javax.swing.GroupLayout(groupFieldPanel);
        groupFieldPanel.setLayout(groupFieldPanelLayout);
        groupFieldPanelLayout.setHorizontalGroup(
                groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                .addContainerGap(61, Short.MAX_VALUE)
                                .addGroup(
                                        groupFieldPanelLayout
                                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(translatorLabel)
                                                .addGroup(groupFieldPanelLayout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                                false)
                                                        .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                                                .addGroup(groupFieldPanelLayout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(dimensionLabel)
                                                                        .addComponent(dimensionField,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(10, 10, 10)
                                                                .addGroup(groupFieldPanelLayout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(pagesSpinner,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(pagesLabel)))
                                                        .addComponent(categorySelectionPanel,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 440,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                                                .addGap(21, 21, 21)
                                                                .addComponent(
                                                                        hiddenPropLabel,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 423,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(hideCheckBox)
                                                        .addComponent(overviewLabel)
                                                        .addComponent(authorLabel)
                                                        .addComponent(categoryLabel)
                                                        .addComponent(titleLabel)
                                                        .addGroup(
                                                                javax.swing.GroupLayout.Alignment.TRAILING,
                                                                groupFieldPanelLayout.createSequentialGroup()
                                                                        .addComponent(cancelButton,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                94,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                        .addComponent(
                                                                                addBookButton,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                94,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addGap(4, 4, 4)))
                                                .addGroup(groupFieldPanelLayout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(
                                                                titleField, javax.swing.GroupLayout.PREFERRED_SIZE, 440,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(
                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                groupFieldPanelLayout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.TRAILING,
                                                                        false)
                                                                        .addGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                groupFieldPanelLayout
                                                                                        .createSequentialGroup()
                                                                                        .addGroup(groupFieldPanelLayout
                                                                                                .createParallelGroup(
                                                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                .addComponent(
                                                                                                        publisherLabel)
                                                                                                .addComponent(
                                                                                                        publisherAutoComp,
                                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                        .addGap(10, 10, 10)
                                                                                        .addGroup(groupFieldPanelLayout
                                                                                                .createParallelGroup(
                                                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                .addComponent(
                                                                                                        publishDatePicker,
                                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                .addComponent(
                                                                                                        publishDateLabel)))
                                                                        .addComponent(authorAutoComp,
                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)))
                                                .addGroup(groupFieldPanelLayout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                                false)
                                                        .addComponent(scrollPane,
                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(translatorField,
                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addContainerGap(61, Short.MAX_VALUE)));
        groupFieldPanelLayout.setVerticalGroup(
                groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(titleLabel)
                                .addGap(4, 4, 4)
                                .addComponent(titleField, javax.swing.GroupLayout.PREFERRED_SIZE, 31,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(14, 14, 14)
                                .addComponent(authorLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(authorAutoComp, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(14, 14, 14)
                                .addGroup(groupFieldPanelLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(publisherLabel)
                                        .addComponent(publishDateLabel))
                                .addGap(4, 4, 4)
                                .addGroup(groupFieldPanelLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(publishDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(publisherAutoComp, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(14, 14, 14)
                                .addComponent(categoryLabel)
                                .addGap(0, 0, 0)
                                .addComponent(categorySelectionPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(12, 12, 12)
                                .addGroup(groupFieldPanelLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(dimensionLabel)
                                        .addComponent(pagesLabel))
                                .addGap(4, 4, 4)
                                .addGroup(groupFieldPanelLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(dimensionField, javax.swing.GroupLayout.PREFERRED_SIZE, 31,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(pagesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(27, 27, 27)
                                .addComponent(translatorLabel)
                                .addGap(4, 4, 4)
                                .addComponent(translatorField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(14, 14, 14)
                                .addComponent(overviewLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 101,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(14, 14, 14)
                                .addComponent(hideCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(hiddenPropLabel, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(groupFieldPanelLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(addBookButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(550, Short.MAX_VALUE)));

        jScrollForm.setViewportView(groupFieldPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(pageName)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jScrollForm));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(pageName, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(backButton, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1)
                                .addComponent(jScrollForm)));
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cancelButtonActionPerformed
        titleField.setText("");
        authorAutoComp.setEmptyText();
        publisherAutoComp.setEmptyText();
        publishDatePicker.setEmptyText();
        categorySelectionPanel.setEmptyList();
        dimensionField.setText("");
        pagesSpinner.setValue(0);
        translatorField.setText("");
        overviewTextArea.setText("");
        hideCheckBox.setSelected(false);
    }// GEN-LAST:event_cancelButtonActionPerformed

    private void addBookButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addBookButtonActionPerformed
        String title = titleField.getText();
        ArrayList<Category> categoriesList = categorySelectionPanel.getListSelected();
        String dimension = dimensionField.getText();
        Object pages = pagesSpinner.getValue();
        String translator = translatorField.getText();
        String overview = overviewTextArea.getText();
        boolean hideChecked = hideCheckBox.isSelected();
        try {
            java.sql.Date publishDate = new java.sql.Date(publishDatePicker.getDate().getTime());
            Author author = authorAutoComp.getSelectedObject();
            if (author == null) {
                if (!authorAutoComp.getText().equals("")) {
                    
                    author = new Author(authorAutoComp.getText());
                } else
                    throw new Exception("Author cannot be empty");
            }
            Publisher publisher = publisherAutoComp.getSelectedObject();
            if (publisher == null) {
                if (!publisherAutoComp.getText().equals("")) {
                    publisher = new Publisher(publisherAutoComp.getText());
                } else
                    throw new Exception("Publisher cannot be empty");
            }
            
            bookService.insertBook(title, author, publisher, categoriesList, publishDate,
                    dimension, pages, translator, overview, hideChecked);
            JOptionPane.showMessageDialog(null, "Book added successfully.", "BSMS Information",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (NullPointerException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Date is not null", "BSMS Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "BSMS Error", JOptionPane.ERROR_MESSAGE);
        }

    }// GEN-LAST:event_addBookButtonActionPerformed

    private void backButtonMouseEntered(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_backButtonMouseEntered
        backButton.setIcon(SVGHelper.createSVGIconWithFilter("icons/arrow-back.svg", Color.black, Color.gray, 24, 17));
    }// GEN-LAST:event_backButtonMouseEntered

    private void backButtonMouseExited(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_backButtonMouseExited
        backButton.setIcon(SVGHelper.createSVGIconWithFilter("icons/arrow-back.svg", Color.black, Color.black, 24, 17));
    }// GEN-LAST:event_backButtonMouseExited

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBookButton;
    private com.group06.bsms.components.AutocompletePanel<Author> authorAutoComp;
    private javax.swing.JLabel authorLabel;
    private javax.swing.JButton backButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel categoryLabel;
    private com.group06.bsms.components.CategorySelectionPanel categorySelectionPanel;
    private javax.swing.JTextField dimensionField;
    private javax.swing.JLabel dimensionLabel;
    private javax.swing.JPanel groupFieldPanel;
    private javax.swing.JLabel hiddenPropLabel;
    private javax.swing.JCheckBox hideCheckBox;
    private javax.swing.JScrollPane jScrollForm;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel overviewLabel;
    private javax.swing.JTextArea overviewTextArea;
    private javax.swing.JLabel pageName;
    private javax.swing.JLabel pagesLabel;
    private javax.swing.JSpinner pagesSpinner;
    private javax.swing.JLabel publishDateLabel;
    private com.group06.bsms.components.DatePickerPanel publishDatePicker;
    private com.group06.bsms.components.AutocompletePanel<Publisher> publisherAutoComp;
    private javax.swing.JLabel publisherLabel;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextField titleField;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextField translatorField;
    private javax.swing.JLabel translatorLabel;
    // End of variables declaration//GEN-END:variables
}

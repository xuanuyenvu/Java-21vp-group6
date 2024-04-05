
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

public class UpdateBook extends javax.swing.JPanel implements CategorySelectionListener {

        private final BookService bookService;
        private final AuthorService authorService;
        private final PublisherService publisherService;
        private final CategoryService categoryService;
        private Book book;

        public UpdateBook() {
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

        public UpdateBook(BookService bookService, AuthorService authorService, PublisherService publisherService,
                        CategoryService categoryService) {
                this.bookService = bookService;
                this.authorService = authorService;
                this.publisherService = publisherService;
                this.categoryService = categoryService;

                initComponents();

                loadAuthorInto();
                loadPublisherInto();
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

        public void setBookById(int bookId) {
                
                try {
                        this.book = bookService.getBook(bookId);
                        loadBookInto();
                } catch (Exception e) {
                        this.book = null;
                        JOptionPane.showMessageDialog(null,
                                        "An error occurred while getting book information: " + e.getMessage(),
                                        "BSMS Error",
                                        JOptionPane.ERROR_MESSAGE);

                }
               
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

        private void loadBookInto() {
                try {
                        if(book == null){
                                throw new Exception("please call setBookById(int bookId)");
                        }
                        titleField.setText(book.title);
                        dimensionField.setText(book.dimension);
                        pagesSpinner.setValue(book.pageCount);
                        translatorField.setText(book.translatorName);
                        overviewTextArea.setText(book.overview);
                        if (book.maxImportPrice != null) {
                                importPriceTextField.setText(Double.toString(book.maxImportPrice));
                        } else
                                salePriceTextField.setEnabled(false);
                        salePriceTextField.setText(book.salePrice.toString());

                        authorAutoComp.setSelectedObject(book.author);

                        publisherAutoComp.setSelectedObject(book.publisher);
                        publishDatePicker.setDate(book.publishDate);
                        var categories = categoryService.selectAllCategories();

                        categorySelectionPanel.updateList((ArrayList<Category>) categories,
                                        (ArrayList<Category>) book.categories);

                } catch (Exception e) {

                        JOptionPane.showMessageDialog(null,
                                        "An error occurred while getting book information: " + e.getMessage(),
                                        "BSMS Error", JOptionPane.ERROR_MESSAGE);
                }

        }

        private void loadAuthorInto() {
                try {

                        authorAutoComp.updateList((ArrayList<Author>) authorService.selectAllAuthors());

                } catch (NullPointerException e) {
                        JOptionPane.showMessageDialog(null,
                                        "An error occurred while getting author information: " + e.getMessage(),
                                        "BSMS Error", JOptionPane.ERROR_MESSAGE);
                } catch (Throwable e) {
                        JOptionPane.showMessageDialog(null, "An unspecified error occurred: " + e.getMessage(),
                                        "BSMS Error",
                                        JOptionPane.ERROR_MESSAGE);
                }
        }

        private void loadPublisherInto() {
                try {

                        publisherAutoComp.updateList((ArrayList<Publisher>) publisherService.selectAllPublishers());

                } catch (NullPointerException e) {
                        JOptionPane.showMessageDialog(null,
                                        "An error occurred while getting publisher information: " + e.getMessage(),
                                        "BSMS Error",
                                        JOptionPane.ERROR_MESSAGE);
                } catch (Throwable e) {
                        JOptionPane.showMessageDialog(null, "An unspecified error occurred: " + e.getMessage(),
                                        "BSMS Error",
                                        JOptionPane.ERROR_MESSAGE);
                }
        }

        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated
        // <editor-fold defaultstate="collapsed" desc="Generated
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
                updateBookButton = new javax.swing.JButton();
                publisherAutoComp = new com.group06.bsms.components.AutocompletePanel();
                authorAutoComp = new com.group06.bsms.components.AutocompletePanel();
                pagesSpinner = new javax.swing.JSpinner();
                publishDatePicker = new com.group06.bsms.components.DatePickerPanel();
                importPriceLabel = new javax.swing.JLabel();
                salePriceTextField = new javax.swing.JTextField();
                salePriceLabel = new javax.swing.JLabel();
                importPriceTextField = new javax.swing.JTextField();

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
                pageName.setText("Update book");

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

                updateBookButton.setBackground(new java.awt.Color(65, 105, 225));
                updateBookButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
                updateBookButton.setForeground(new java.awt.Color(255, 255, 255));
                updateBookButton.setText("Update");
                updateBookButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                updateBookButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                updateBookButtonActionPerformed(evt);
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

                importPriceLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
                importPriceLabel.setLabelFor(dimensionField);
                importPriceLabel.setText("Import price");

                salePriceTextField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
                salePriceTextField.setMinimumSize(new java.awt.Dimension(215, 31));
                salePriceTextField.setPreferredSize(new java.awt.Dimension(215, 31));
                salePriceTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                        public void keyPressed(java.awt.event.KeyEvent evt) {
                                salePriceTextFieldKeyPressed(evt);
                        }
                });

                salePriceLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
                salePriceLabel.setLabelFor(pagesSpinner);
                salePriceLabel.setText("Sale price");

                importPriceTextField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
                importPriceTextField.setEnabled(false);
                importPriceTextField.setMinimumSize(new java.awt.Dimension(215, 31));
                importPriceTextField.setPreferredSize(new java.awt.Dimension(215, 31));

                javax.swing.GroupLayout groupFieldPanelLayout = new javax.swing.GroupLayout(groupFieldPanel);
                groupFieldPanel.setLayout(groupFieldPanelLayout);
                groupFieldPanelLayout.setHorizontalGroup(
                                groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                                                .addContainerGap(61, Short.MAX_VALUE)
                                                                .addGroup(groupFieldPanelLayout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                false)
                                                                                .addGroup(groupFieldPanelLayout
                                                                                                .createSequentialGroup()
                                                                                                .addGroup(groupFieldPanelLayout
                                                                                                                .createParallelGroup(
                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                .addComponent(dimensionLabel)
                                                                                                                .addComponent(dimensionField,
                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                .addGroup(groupFieldPanelLayout
                                                                                                                .createParallelGroup(
                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                .addGroup(groupFieldPanelLayout
                                                                                                                                .createSequentialGroup()
                                                                                                                                .addGap(10, 10, 10)
                                                                                                                                .addComponent(pagesLabel))
                                                                                                                .addGroup(groupFieldPanelLayout
                                                                                                                                .createSequentialGroup()
                                                                                                                                .addPreferredGap(
                                                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                                                                .addComponent(pagesSpinner,
                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                Short.MAX_VALUE))))
                                                                                .addComponent(categorySelectionPanel,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                440,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(overviewLabel)
                                                                                .addComponent(authorLabel)
                                                                                .addComponent(categoryLabel)
                                                                                .addComponent(titleLabel)
                                                                                .addComponent(translatorLabel)
                                                                                .addComponent(scrollPane)
                                                                                .addComponent(translatorField,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE)
                                                                                .addGroup(groupFieldPanelLayout
                                                                                                .createParallelGroup(
                                                                                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                                                                                .addComponent(updateBookButton,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                94,
                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                .addGroup(groupFieldPanelLayout
                                                                                                                .createSequentialGroup()
                                                                                                                .addGroup(groupFieldPanelLayout
                                                                                                                                .createParallelGroup(
                                                                                                                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                                                                                                                .addGroup(groupFieldPanelLayout
                                                                                                                                                .createSequentialGroup()
                                                                                                                                                .addComponent(importPriceLabel)
                                                                                                                                                .addGap(149, 149,
                                                                                                                                                                149))
                                                                                                                                .addGroup(groupFieldPanelLayout
                                                                                                                                                .createSequentialGroup()
                                                                                                                                                .addComponent(importPriceTextField,
                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                                .addPreferredGap(
                                                                                                                                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                                                                                                                .addGroup(groupFieldPanelLayout
                                                                                                                                .createParallelGroup(
                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                                .addComponent(salePriceTextField,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                .addComponent(salePriceLabel))))
                                                                                .addComponent(titleField,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE)
                                                                                .addGroup(groupFieldPanelLayout
                                                                                                .createSequentialGroup()
                                                                                                .addGroup(groupFieldPanelLayout
                                                                                                                .createParallelGroup(
                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                .addComponent(publisherLabel)
                                                                                                                .addComponent(publisherAutoComp,
                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                .addGap(10, 10, 10)
                                                                                                .addGroup(groupFieldPanelLayout
                                                                                                                .createParallelGroup(
                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                                                false)
                                                                                                                .addGroup(groupFieldPanelLayout
                                                                                                                                .createSequentialGroup()
                                                                                                                                .addComponent(publishDateLabel)
                                                                                                                                .addGap(136, 136,
                                                                                                                                                136))
                                                                                                                .addComponent(publishDatePicker,
                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                Short.MAX_VALUE)))
                                                                                .addComponent(authorAutoComp,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE))
                                                                .addContainerGap(61, Short.MAX_VALUE)));
                groupFieldPanelLayout.setVerticalGroup(
                                groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(groupFieldPanelLayout.createSequentialGroup()
                                                                .addGap(29, 29, 29)
                                                                .addComponent(titleLabel)
                                                                .addGap(4, 4, 4)
                                                                .addComponent(titleField,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                31,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(14, 14, 14)
                                                                .addComponent(authorLabel)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(authorAutoComp,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(14, 14, 14)
                                                                .addGroup(groupFieldPanelLayout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(publisherLabel)
                                                                                .addComponent(publishDateLabel))
                                                                .addGap(4, 4, 4)
                                                                .addGroup(groupFieldPanelLayout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                                                                .addComponent(publishDatePicker,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(publisherAutoComp,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(14, 14, 14)
                                                                .addComponent(categoryLabel)
                                                                .addGap(0, 0, 0)
                                                                .addComponent(categorySelectionPanel,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                .addGap(12, 12, 12)
                                                                .addGroup(groupFieldPanelLayout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(dimensionLabel)
                                                                                .addComponent(pagesLabel))
                                                                .addGap(4, 4, 4)
                                                                .addGroup(groupFieldPanelLayout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(dimensionField,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                31,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(pagesSpinner,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(27, 27, 27)
                                                                .addComponent(translatorLabel)
                                                                .addGap(4, 4, 4)
                                                                .addComponent(translatorField,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(14, 14, 14)
                                                                .addComponent(overviewLabel)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(scrollPane,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                101,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addGroup(groupFieldPanelLayout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(importPriceLabel)
                                                                                .addComponent(salePriceLabel))
                                                                .addGap(4, 4, 4)
                                                                .addGroup(groupFieldPanelLayout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(salePriceTextField,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                31,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(importPriceTextField,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                31,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(18, 18, 18)
                                                                .addComponent(updateBookButton,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                33,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addContainerGap(545, Short.MAX_VALUE)));

                jScrollForm.setViewportView(groupFieldPanel);

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
                this.setLayout(layout);
                layout.setHorizontalGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                                .addGap(18, 18, 18)
                                                                .addComponent(backButton,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(pageName)
                                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE))
                                                .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(jScrollForm));
                layout.setVerticalGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addGroup(layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                false)
                                                                                .addComponent(pageName,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE)
                                                                                .addComponent(backButton,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                Short.MAX_VALUE))
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jSeparator1,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                10,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(1, 1, 1)
                                                                .addComponent(jScrollForm)));
        }// </editor-fold>//GEN-END:initComponents

        private void salePriceTextFieldKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_salePriceTextFieldKeyPressed
                // TODO add your handling code here:

                char inputChar = evt.getKeyChar();
                if (Character.isLetter(inputChar)) {
                        salePriceTextField.setEditable(false);
                } else {
                        salePriceTextField.setEditable(true);
                }

        }// GEN-LAST:event_salePriceTextFieldKeyPressed

        private void updateBookButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addBookButtonActionPerformed

                try {
                        if (book == null) {
                                throw new Exception("The book is empty");
                        }
                        String title = titleField.getText();
                        ArrayList<Category> categoriesList = categorySelectionPanel.getListSelected();
                        String dimension = dimensionField.getText();
                        int pages = (Integer) pagesSpinner.getValue();
                        String translator = translatorField.getText();
                        String overview = overviewTextArea.getText();

                        Double salePrice = Double.parseDouble(salePriceTextField.getText());
                        java.sql.Date publishDate = new java.sql.Date(publishDatePicker.getDate().getTime());
                        Author author = (Author) authorAutoComp.getSelectedObject();
                        if (author == null) {
                                if (!authorAutoComp.getText().equals("")) {
                                        author = new Author(authorAutoComp.getText());
                                } else
                                        throw new Exception("Author cannot be empty");
                        }

                        Publisher publisher = (Publisher) publisherAutoComp.getSelectedObject();
                        if (publisher == null) {
                                if (!publisherAutoComp.getText().equals("")) {
                                        publisher = new Publisher(publisherAutoComp.getText());
                                } else
                                        throw new Exception("Publisher cannot be empty");
                        }

                        Book updatedBook = new Book(author.id, publisher.id, title, pages, publishDate,
                                        dimension, translator, overview, book.quantity, salePrice, book.isHidden,
                                        book.hiddenParentCount,
                                        book.maxImportPrice);

                        updatedBook.id = book.id;
                        updatedBook.categories = categoriesList;
                        updatedBook.author = author;
                        updatedBook.publisher = publisher;
                        bookService.updateBook(book, updatedBook);

                        JOptionPane.showMessageDialog(null, "Book updated successfully.", "BSMS Information",
                                        JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, ex.getMessage(), "BSMS Error", JOptionPane.ERROR_MESSAGE);
                }

        }// GEN-LAST:event_addBookButtonActionPerformed

        private void backButtonMouseEntered(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_backButtonMouseEntered
                backButton.setIcon(SVGHelper.createSVGIconWithFilter("icons/arrow-back.svg", Color.black, Color.gray,
                                24, 17));
        }// GEN-LAST:event_backButtonMouseEntered

        private void backButtonMouseExited(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_backButtonMouseExited
                backButton.setIcon(SVGHelper.createSVGIconWithFilter("icons/arrow-back.svg", Color.black, Color.black,
                                24, 17));
        }// GEN-LAST:event_backButtonMouseExited

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private com.group06.bsms.components.AutocompletePanel authorAutoComp;
        private javax.swing.JLabel authorLabel;
        private javax.swing.JButton backButton;
        private javax.swing.JLabel categoryLabel;
        private com.group06.bsms.components.CategorySelectionPanel categorySelectionPanel;
        private javax.swing.JTextField dimensionField;
        private javax.swing.JLabel dimensionLabel;
        private javax.swing.JPanel groupFieldPanel;
        private javax.swing.JLabel importPriceLabel;
        private javax.swing.JTextField importPriceTextField;
        private javax.swing.JScrollPane jScrollForm;
        private javax.swing.JSeparator jSeparator1;
        private javax.swing.JLabel overviewLabel;
        private javax.swing.JTextArea overviewTextArea;
        private javax.swing.JLabel pageName;
        private javax.swing.JLabel pagesLabel;
        private javax.swing.JSpinner pagesSpinner;
        private javax.swing.JLabel publishDateLabel;
        private com.group06.bsms.components.DatePickerPanel publishDatePicker;
        private com.group06.bsms.components.AutocompletePanel publisherAutoComp;
        private javax.swing.JLabel publisherLabel;
        private javax.swing.JLabel salePriceLabel;
        private javax.swing.JTextField salePriceTextField;
        private javax.swing.JScrollPane scrollPane;
        private javax.swing.JTextField titleField;
        private javax.swing.JLabel titleLabel;
        private javax.swing.JTextField translatorField;
        private javax.swing.JLabel translatorLabel;
        private javax.swing.JButton updateBookButton;
        // End of variables declaration//GEN-END:variables
}

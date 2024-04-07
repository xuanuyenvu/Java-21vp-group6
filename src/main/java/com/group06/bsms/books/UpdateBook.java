package com.group06.bsms.books;

import com.group06.bsms.DB;
import com.group06.bsms.components.*;
import com.group06.bsms.authors.*;
import com.group06.bsms.categories.*;
import com.group06.bsms.dashboard.Dashboard;
import com.group06.bsms.publishers.*;
import com.group06.bsms.utils.SVGHelper;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;

public class UpdateBook extends javax.swing.JPanel implements CategorySelectionListener {

    private BookCRUD bookCRUD;
    private final BookService bookService;
    private final AuthorService authorService;
    private final PublisherService publisherService;
    private final CategoryService categoryService;
    private Book book;

    public void setBookCRUD(BookCRUD bookCRUD) {
        this.bookCRUD = bookCRUD;
    }

    public UpdateBook() {
        this(
                null,
                new BookService(
                        new BookRepository(DB.db()),
                        new AuthorService(new AuthorRepository(DB.db())),
                        new PublisherService(new PublisherRepository(DB.db())),
                        new CategoryService(new CategoryRepository(DB.db()))),
                new AuthorService(new AuthorRepository(DB.db())),
                new PublisherService(new PublisherRepository(DB.db())),
                new CategoryService(new CategoryRepository(DB.db())));
    }

    public UpdateBook(BookCRUD bookCRUD) {
        this(
                bookCRUD,
                new BookService(
                        new BookRepository(DB.db()),
                        new AuthorService(new AuthorRepository(DB.db())),
                        new PublisherService(new PublisherRepository(DB.db())),
                        new CategoryService(new CategoryRepository(DB.db()))),
                new AuthorService(new AuthorRepository(DB.db())),
                new PublisherService(new PublisherRepository(DB.db())),
                new CategoryService(new CategoryRepository(DB.db())));
    }

    public UpdateBook(
            BookCRUD bookCRUD,
            BookService bookService,
            AuthorService authorService, PublisherService publisherService,
            CategoryService categoryService) {
        this.bookCRUD = bookCRUD;
        this.bookService = bookService;
        this.authorService = authorService;
        this.publisherService = publisherService;
        this.categoryService = categoryService;
        initComponents();

        loadAuthorInto();
        loadPublisherInto();
        categorySelectionPanel.setCategorySelectionListener(this);

        publishDatePicker.setDate(new Date());
        var sm = new SimpleDateFormat("dd/MM/yyyy");
        publishDatePicker.setText(sm.format(publishDatePicker.getDate()));

        titleField.putClientProperty("JTextField.placeholderText", "Book title");
        dimensionField.putClientProperty("JTextField.placeholderText", "LxWxH cm");
        translatorField.putClientProperty("JTextField.placeholderText", "Translator name");

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
            book = bookService.getBook(bookId);
            loadBookInto();
        } catch (Exception e) {
            book = null;

            JOptionPane.showMessageDialog(null,
                    "An error occurred while getting book information: " + e.getMessage(),
                    "BSMS Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onCategoriesChanged(int numOfCategories) {
        categorySelectionPanel.changeSize(1.1f);

        jScrollForm.revalidate();
        jScrollForm.repaint();
    }

    private void loadBookInto() {
        try {
            titleField.setText(book.title);
            dimensionField.setText(book.dimension);
            pagesSpinner.setValue(book.pageCount);
            translatorField.setText(book.translatorName);
            overviewTextArea.setText(book.overview);

            if (book.maxImportPrice != null) {
                importPriceTextField.setText(Double.toString(book.maxImportPrice));
            } else {
                salePriceTextField.setEnabled(false);
            }

            if (book.salePrice != null) {
                salePriceTextField.setText(Double.toString(book.salePrice));
            } else {
                salePriceTextField.setText("");
            }

            authorAutoComp.setEmptyText();
            authorAutoComp.setSelectedObject(book.author);

            publisherAutoComp.setEmptyText();
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
            var authors = new ArrayList<Author>(authorService.selectAllAuthors());
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        backButton = new javax.swing.JButton();
        pageName = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollForm = new javax.swing.JScrollPane();
        groupFieldPanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        titleField = new javax.swing.JTextField();
        authorLabel = new javax.swing.JLabel();
        publisherLabel = new javax.swing.JLabel();
        publishDateLabel = new javax.swing.JLabel();
        categoryLabel = new javax.swing.JLabel();
        dimensionLabel = new javax.swing.JLabel();
        dimensionField = new javax.swing.JTextField();
        pagesLabel = new javax.swing.JLabel();
        translatorField = new javax.swing.JTextField();
        translatorLabel = new javax.swing.JLabel();
        overviewLabel = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        overviewTextArea = new javax.swing.JTextArea();
        addBookButton = new javax.swing.JButton();
        publisherAutoComp = new com.group06.bsms.components.AutocompletePanel();
        authorAutoComp = new com.group06.bsms.components.AutocompletePanel();
        pagesSpinner = new javax.swing.JSpinner();
        publishDatePicker = new com.group06.bsms.components.DatePickerPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        categorySelectionPanel = new com.group06.bsms.components.CategorySelectionPanel();
        dimensionLabel1 = new javax.swing.JLabel();
        importPriceTextField = new javax.swing.JTextField();
        salePriceTextField = new javax.swing.JTextField();
        dimensionLabel2 = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        backButton.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        backButton.setForeground(UIManager.getColor("mutedColor"));
        backButton.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/arrow-back.svg", 
            Color.white, Color.white,
            18, 18
        ));
        backButton.setMnemonic(java.awt.event.KeyEvent.VK_BACK_SPACE);
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
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        pageName.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        pageName.setText("Update book");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pageName)
                .addContainerGap(454, Short.MAX_VALUE))
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(pageName))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jScrollForm.setBorder(null);
        jScrollForm.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollForm.setVerifyInputWhenFocusTarget(false);

        groupFieldPanel.setBorder(new org.jdesktop.swingx.border.IconBorder());
        groupFieldPanel.setMinimumSize(new java.awt.Dimension(440, 31));

        titleLabel.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_T);
        titleLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        titleLabel.setLabelFor(titleField);
        titleLabel.setText("Title");
        titleLabel.setDisplayedMnemonicIndex(0);

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
        publishDateLabel.setLabelFor(publishDatePicker);
        publishDateLabel.setText("Publish Date");

        categoryLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        categoryLabel.setLabelFor(categorySelectionPanel);
        categoryLabel.setText("Category");

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

        scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setAutoscrolls(true);

        overviewTextArea.setColumns(20);
        overviewTextArea.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        overviewTextArea.setLineWrap(true);
        overviewTextArea.setDragEnabled(true);
        overviewTextArea.setMaximumSize(new java.awt.Dimension(440, 2147483647));
        overviewTextArea.setMinimumSize(new java.awt.Dimension(0, 0));
        scrollPane.setViewportView(overviewTextArea);

        addBookButton.setBackground(new java.awt.Color(65, 105, 225));
        addBookButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        addBookButton.setForeground(new java.awt.Color(255, 255, 255));
        addBookButton.setMnemonic(java.awt.event.KeyEvent.VK_U);
        addBookButton.setText("Update");
        addBookButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        addBookButton.setDisplayedMnemonicIndex(0);
        addBookButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBookButtonActionPerformed(evt);
            }
        });

        publisherAutoComp.setPlaceHolderText("Publisher name");
        publisherAutoComp.setPreferredSize(new java.awt.Dimension(215, 31));
        publisherAutoComp.setRequestFocusEnabled(true);

        authorAutoComp.setMinimumSize(new java.awt.Dimension(440, 31));
        authorAutoComp.setPlaceHolderText("Author name");
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

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        categorySelectionPanel.setAutoscrolls(true);
        categorySelectionPanel.setMaximumSize(new java.awt.Dimension(440, 40));
        categorySelectionPanel.setMinimumSize(new java.awt.Dimension(440, 40));
        categorySelectionPanel.setPreferredSize(new java.awt.Dimension(440, 40));
        jScrollPane1.setViewportView(categorySelectionPanel);

        dimensionLabel1.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        dimensionLabel1.setLabelFor(importPriceTextField);
        dimensionLabel1.setText("Import price");

        importPriceTextField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        importPriceTextField.setEnabled(false);
        importPriceTextField.setMinimumSize(new java.awt.Dimension(215, 31));
        importPriceTextField.setPreferredSize(new java.awt.Dimension(215, 31));

        salePriceTextField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        salePriceTextField.setMinimumSize(new java.awt.Dimension(215, 31));
        salePriceTextField.setPreferredSize(new java.awt.Dimension(215, 31));
        salePriceTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                salePriceTextFieldKeyPressed(evt);
            }
        });

        dimensionLabel2.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        dimensionLabel2.setLabelFor(salePriceTextField);
        dimensionLabel2.setText("Sale price");

        javax.swing.GroupLayout groupFieldPanelLayout = new javax.swing.GroupLayout(groupFieldPanel);
        groupFieldPanel.setLayout(groupFieldPanelLayout);
        groupFieldPanelLayout.setHorizontalGroup(
            groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                .addContainerGap(91, Short.MAX_VALUE)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(translatorLabel)
                    .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(addBookButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(groupFieldPanelLayout.createSequentialGroup()
                            .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(dimensionLabel)
                                .addComponent(dimensionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(10, 10, 10)
                            .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(pagesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(pagesLabel)))
                        .addComponent(overviewLabel)
                        .addComponent(authorLabel)
                        .addComponent(categoryLabel)
                        .addComponent(titleLabel))
                    .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(titleField, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, groupFieldPanelLayout.createSequentialGroup()
                                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(publisherLabel)
                                    .addComponent(publisherAutoComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(10, 10, 10)
                                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(publishDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(publishDateLabel)))
                            .addComponent(authorAutoComp, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(translatorField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                        .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dimensionLabel1)
                            .addComponent(importPriceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dimensionLabel2)
                            .addComponent(salePriceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(91, Short.MAX_VALUE))
        );
        groupFieldPanelLayout.setVerticalGroup(
            groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanelLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(titleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(titleField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(authorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(authorAutoComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(publisherLabel)
                    .addComponent(publishDateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(publishDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(publisherAutoComp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addComponent(categoryLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                .addGap(14, 14, 14)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dimensionLabel)
                    .addComponent(pagesLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dimensionField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pagesSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addComponent(translatorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(translatorField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(overviewLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(groupFieldPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                        .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                        .addGap(14, 14, 14)
                        .addComponent(dimensionLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(importPriceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(groupFieldPanelLayout.createSequentialGroup()
                        .addComponent(dimensionLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(salePriceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(14, 14, 14)
                .addComponent(addBookButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58))
        );

        jScrollForm.setViewportView(groupFieldPanel);

        add(jScrollForm, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void addBookButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addBookButtonActionPerformed
        String title = titleField.getText();
        ArrayList<Category> categoriesList = categorySelectionPanel.getListSelected();
        String dimension = dimensionField.getText();
        Object pages = pagesSpinner.getValue();
        String translator = translatorField.getText();
        String overview = overviewTextArea.getText();
        Double salePrice = "".equals(salePriceTextField.getText())
                ? null
                : Double.valueOf(salePriceTextField.getText());
        try {
            if (book == null) {
                throw new Exception("Book data is empty");
            }

            java.sql.Date publishDate = publishDatePicker.getDateSQL();

            if (title == null || title.equals("")) {
                throw new Exception("Title cannot be empty");
            }

            Author author = (Author) authorAutoComp.getSelectedObject();
            if (author == null) {
                if (!authorAutoComp.getText().equals("")) {
                    author = new Author(authorAutoComp.getText());
                } else {
                    throw new Exception("Author cannot be empty");
                }
            }

            Publisher publisher = (Publisher) publisherAutoComp.getSelectedObject();
            if (publisher == null) {
                if (!publisherAutoComp.getText().equals("")) {
                    publisher = new Publisher(publisherAutoComp.getText());
                } else {
                    throw new Exception("Publisher cannot be empty");
                }
            }

            if (categoriesList.isEmpty()) {
                throw new Exception("Categories cannot be empty");
            }

            if (dimension == null || dimension.equals("")) {
                throw new Exception("Dimension cannot be empty");
            }

            if (pages == null || pages.equals(0)) {
                throw new Exception("Pages cannot be 0");
            }

            if (overview == null || overview.equals("")) {
                throw new Exception("Overview cannot be empty");
            }

            Book updatedBook = new Book(
                    author.id, publisher.id, title, (int) pages, publishDate,
                    dimension, translator, overview, book.quantity, salePrice,
                    book.isHidden, book.hiddenParentCount, book.maxImportPrice
            );
            updatedBook.id = book.id;
            updatedBook.categories = categoriesList;
            updatedBook.author = author;
            updatedBook.publisher = publisher;
            bookService.updateBook(book, updatedBook);

            loadAuthorInto();
            loadPublisherInto();
            setBookById(book.id);

            JOptionPane.showMessageDialog(null, "Book updated successfully.", "BSMS Information",
                    JOptionPane.INFORMATION_MESSAGE);

            bookCRUD.reloadBooks(true);
        } catch (Exception ex) {
            if (ex.getMessage().contains("book_publishdate_check")) {
                JOptionPane.showMessageDialog(null, "Publish date must be before today", "BSMS Error", JOptionPane.ERROR_MESSAGE);
            } else if (ex.getMessage().contains("book_title_key")) {
                JOptionPane.showMessageDialog(null, "A book with this title already exists", "BSMS Error", JOptionPane.ERROR_MESSAGE);
            } else if (ex.getMessage().contains("book_dimension_check")) {
                JOptionPane.showMessageDialog(null, "Invalid dimension format (must be 'LxWxH cm').", "BSMS Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "BSMS Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void backButtonMouseEntered(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_backButtonMouseEntered
        backButton.setIcon(SVGHelper.createSVGIconWithFilter("icons/arrow-back.svg", Color.black, Color.gray, 18, 18));
    }// GEN-LAST:event_backButtonMouseEntered

    private void backButtonMouseExited(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_backButtonMouseExited
        backButton.setIcon(SVGHelper.createSVGIconWithFilter("icons/arrow-back.svg", Color.black, Color.black, 18, 18));
    }// GEN-LAST:event_backButtonMouseExited

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        Dashboard.dashboard.switchTab("bookCRUD");
    }//GEN-LAST:event_backButtonActionPerformed

    private void salePriceTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_salePriceTextFieldKeyPressed
        char inputChar = evt.getKeyChar();
        if (Character.isLetter(inputChar)) {
            salePriceTextField.setEditable(false);
        } else {
            salePriceTextField.setEditable(true);
        }
    }//GEN-LAST:event_salePriceTextFieldKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBookButton;
    private com.group06.bsms.components.AutocompletePanel authorAutoComp;
    private javax.swing.JLabel authorLabel;
    private javax.swing.JButton backButton;
    private javax.swing.JLabel categoryLabel;
    private com.group06.bsms.components.CategorySelectionPanel categorySelectionPanel;
    private javax.swing.JTextField dimensionField;
    private javax.swing.JLabel dimensionLabel;
    private javax.swing.JLabel dimensionLabel1;
    private javax.swing.JLabel dimensionLabel2;
    private javax.swing.JPanel groupFieldPanel;
    private javax.swing.JTextField importPriceTextField;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollForm;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel overviewLabel;
    private javax.swing.JTextArea overviewTextArea;
    private javax.swing.JLabel pageName;
    private javax.swing.JLabel pagesLabel;
    private javax.swing.JSpinner pagesSpinner;
    private javax.swing.JLabel publishDateLabel;
    private com.group06.bsms.components.DatePickerPanel publishDatePicker;
    private com.group06.bsms.components.AutocompletePanel publisherAutoComp;
    private javax.swing.JLabel publisherLabel;
    private javax.swing.JTextField salePriceTextField;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextField titleField;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextField translatorField;
    private javax.swing.JLabel translatorLabel;
    // End of variables declaration//GEN-END:variables
}

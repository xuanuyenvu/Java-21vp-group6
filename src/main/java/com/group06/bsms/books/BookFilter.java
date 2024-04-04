package com.group06.bsms.books;

import com.formdev.flatlaf.FlatClientProperties;
import com.group06.bsms.DB;
import com.group06.bsms.authors.Author;
import com.group06.bsms.authors.AuthorRepository;
import com.group06.bsms.authors.AuthorService;
import com.group06.bsms.categories.Category;
import com.group06.bsms.categories.CategoryRepository;
import com.group06.bsms.categories.CategoryService;
import com.group06.bsms.components.AutocompletePanel;
import com.group06.bsms.components.CategorySelectionListener;
import com.group06.bsms.components.CategorySelectionPanel;
import com.group06.bsms.publishers.Publisher;
import com.group06.bsms.publishers.PublisherRepository;
import com.group06.bsms.publishers.PublisherService;
import com.group06.bsms.utils.SVGHelper;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class BookFilter extends javax.swing.JPanel implements CategorySelectionListener {

    private final BookCRUD bookCRUD;
    private final AuthorService authorService;
    private final PublisherService publisherService;
    private final CategoryService categoryService;

    public CategorySelectionPanel getCategorySelectionPanel1() {
        return categorySelectionPanel1;
    }

    public JTextField getMaxPriceField() {
        return maxPriceField;
    }

    public JTextField getMinPriceField() {
        return minPriceField;
    }

    public AutocompletePanel getPublisherAutoComp1() {
        return publisherAutoComp1;
    }

    public AutocompletePanel getAuthorAutoComp1() {
        return authorAutoComp1;
    }

    public JComboBox<String> getFilterComboBox() {
        return filterComboBox;
    }

    public BookFilter() {
        this(
                null,
                new AuthorService(new AuthorRepository(DB.db())),
                new PublisherService(new PublisherRepository(DB.db())),
                new CategoryService(new CategoryRepository(DB.db()))
        );
    }

    public BookFilter(BookCRUD bookCRUD) {
        this(
                bookCRUD,
                new AuthorService(new AuthorRepository(DB.db())),
                new PublisherService(new PublisherRepository(DB.db())),
                new CategoryService(new CategoryRepository(DB.db()))
        );
    }

    public BookFilter(
            BookCRUD bookCRUD,
            AuthorService authorService,
            PublisherService publisherService,
            CategoryService categoryService
    ) {
        this.bookCRUD = bookCRUD;
        this.authorService = authorService;
        this.publisherService = publisherService;
        this.categoryService = categoryService;

        initComponents();
        loadAuthorInto();
        loadCategoryInto();
        loadPublisherInto();
        categorySelectionPanel1.setCategorySelectionListener(this);
        minPriceField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "From");
        maxPriceField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "To");
        minPriceField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, SVGHelper.createSVGIconWithFilter(
                "icons/dollar.svg",
                Color.black, Color.black,
                14, 14
        ));
        maxPriceField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, SVGHelper.createSVGIconWithFilter(
                "icons/dollar.svg",
                Color.black, Color.black,
                14, 14
        ));
    }

    private void loadAuthorInto() {
        try {
            var authors = new ArrayList<Author>(authorService.selectAllAuthors());
            if (authors == null) {
                throw new NullPointerException();
            }

            authorAutoComp1.updateList(authors);

        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null, "An error occurred while getting author information: " + e.getMessage(), "BSMS Error", JOptionPane.ERROR_MESSAGE);
        } catch (Throwable e) {
            JOptionPane.showMessageDialog(null, "An unspecified error occurred: " + e.getMessage(), "BSMS Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPublisherInto() {
        try {
            var publishers = new ArrayList<Publisher>(publisherService.selectAllPublishers());
            if (publishers == null) {
                throw new NullPointerException();
            }

            publisherAutoComp1.updateList(publishers);

        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null, "An error occurred while getting publisher information: " + e.getMessage(), "BSMS Error", JOptionPane.ERROR_MESSAGE);
        } catch (Throwable e) {
            JOptionPane.showMessageDialog(null, "An unspecified error occurred: " + e.getMessage(), "BSMS Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCategoryInto() {
        try {
            var categories = new ArrayList<Category>(categoryService.selectAllCategories());
            if (categories == null) {
                throw new NullPointerException();
            }

            categorySelectionPanel1.updateList(categories, null);

        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null, "An error occurred while getting category information: " + e.getMessage(), "BSMS Error", JOptionPane.ERROR_MESSAGE);
        } catch (Throwable e) {
            JOptionPane.showMessageDialog(null, "An unspecified error occurred: " + e.getMessage(), "BSMS Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onCategoriesChanged(int numOfCategories) {
        int newHeight = (40 + ((int) (numOfCategories / 1.1) * 35));
        categorySelectionPanel1.setPreferredSize(new Dimension(categorySelectionPanel1.getWidth(), newHeight));
        this.revalidate();
        this.repaint();

        bookCRUD.setCurrentOffset(0);
        bookCRUD.loadBooksIntoTable();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        groupFieldPanel1 = new javax.swing.JPanel();
        authorLabel1 = new javax.swing.JLabel();
        publisherLabel1 = new javax.swing.JLabel();
        categoryLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        categorySelectionPanel1 = new com.group06.bsms.components.CategorySelectionPanel();
        salePriceLabel = new javax.swing.JLabel();
        minPriceField = new javax.swing.JTextField();
        removeAllBtn = new javax.swing.JButton();
        publisherAutoComp1 = new AutocompletePanel((evt) -> filter(evt));
        authorAutoComp1 = new AutocompletePanel((evt) -> filter(evt));
        maxPriceField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        authorLabel2 = new javax.swing.JLabel();
        filterComboBox = new javax.swing.JComboBox<>();

        setLayout(new java.awt.BorderLayout());

        groupFieldPanel1.setBorder(new org.jdesktop.swingx.border.IconBorder());
        groupFieldPanel1.setMinimumSize(new java.awt.Dimension(0, 0));
        groupFieldPanel1.setPreferredSize(new java.awt.Dimension(300, 322));

        authorLabel1.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        authorLabel1.setText("Author");

        publisherLabel1.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        publisherLabel1.setText("Publisher");

        categoryLabel1.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        categoryLabel1.setText("Category");

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setMaximumSize(new java.awt.Dimension(1000, 100));

        categorySelectionPanel1.setAutoscrolls(true);
        categorySelectionPanel1.setMaximumSize(new java.awt.Dimension(440, 32767));
        categorySelectionPanel1.setMinimumSize(new java.awt.Dimension(220, 40));
        categorySelectionPanel1.setPreferredSize(new java.awt.Dimension(220, 40));
        jScrollPane1.setViewportView(categorySelectionPanel1);

        salePriceLabel.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        salePriceLabel.setText("Sale price");

        minPriceField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        minPriceField.setMinimumSize(new java.awt.Dimension(0, 31));
        minPriceField.setPreferredSize(new java.awt.Dimension(215, 31));
        minPriceField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minPriceFieldActionPerformed(evt);
            }
        });
        minPriceField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                minPriceFieldKeyPressed(evt);
            }
        });

        removeAllBtn.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        removeAllBtn.setForeground(UIManager.getColor("mutedColor")
        );
        removeAllBtn.setText("Clear");
        removeAllBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        removeAllBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAllBtnActionPerformed(evt);
            }
        });

        publisherAutoComp1.setPlaceHolderText("Search by publisher's name");
        publisherAutoComp1.setPreferredSize(new java.awt.Dimension(215, 31));
        publisherAutoComp1.setRequestFocusEnabled(true);

        authorAutoComp1.setPlaceHolderText("Search by author's name");
        authorAutoComp1.setPreferredSize(new java.awt.Dimension(220, 31));
        authorAutoComp1.setRequestFocusEnabled(true);

        maxPriceField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        maxPriceField.setMinimumSize(new java.awt.Dimension(0, 31));
        maxPriceField.setPreferredSize(new java.awt.Dimension(215, 31));
        maxPriceField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxPriceFieldActionPerformed(evt);
            }
        });
        maxPriceField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                maxPriceFieldKeyPressed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 204, 204));
        jLabel1.setText("-");

        authorLabel2.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        authorLabel2.setText("Filter");

        filterComboBox.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        filterComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "Top 20 Newest Books", "Top 20 Hottest Books", "Out-of-stock Books" }));
        filterComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout groupFieldPanel1Layout = new javax.swing.GroupLayout(groupFieldPanel1);
        groupFieldPanel1.setLayout(groupFieldPanel1Layout);
        groupFieldPanel1Layout.setHorizontalGroup(
            groupFieldPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(groupFieldPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, groupFieldPanel1Layout.createSequentialGroup()
                        .addGroup(groupFieldPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(groupFieldPanel1Layout.createSequentialGroup()
                                .addComponent(removeAllBtn)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(groupFieldPanel1Layout.createSequentialGroup()
                                .addComponent(minPriceField, javax.swing.GroupLayout.DEFAULT_SIZE, 1, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(maxPriceField, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(312, 312, 312))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, groupFieldPanel1Layout.createSequentialGroup()
                        .addGroup(groupFieldPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(authorLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(authorLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(553, 553, 553))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, groupFieldPanel1Layout.createSequentialGroup()
                        .addGroup(groupFieldPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(filterComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(publisherAutoComp1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(authorAutoComp1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, groupFieldPanel1Layout.createSequentialGroup()
                                .addComponent(salePriceLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(215, 215, 215))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, groupFieldPanel1Layout.createSequentialGroup()
                                .addComponent(publisherLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(219, 219, 219)))
                        .addGap(312, 312, 312))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, groupFieldPanel1Layout.createSequentialGroup()
                        .addComponent(categoryLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(221, 221, 221))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        groupFieldPanel1Layout.setVerticalGroup(
            groupFieldPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(groupFieldPanel1Layout.createSequentialGroup()
                .addComponent(authorLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(authorLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(authorAutoComp1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(publisherLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(publisherAutoComp1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(salePriceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(groupFieldPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(minPriceField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxPriceField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(categoryLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 15, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeAllBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        add(groupFieldPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void filter(java.awt.event.ItemEvent evt) {
        bookCRUD.setCurrentOffset(0);
        bookCRUD.loadBooksIntoTable();
//        bookCRUD.setUpTable();
    }

    private void removeAllBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAllBtnActionPerformed
        authorAutoComp1.setEmptyText();
        publisherAutoComp1.setEmptyText();
        categorySelectionPanel1.setEmptyList();
        minPriceField.setText("");
        maxPriceField.setText("");
        filterComboBox.setSelectedItem("None");
        bookCRUD.loadBooksIntoTable();
    }//GEN-LAST:event_removeAllBtnActionPerformed

    private void minPriceFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_minPriceFieldKeyPressed
        char inputChar = evt.getKeyChar();
        if (Character.isLetter(inputChar)) {
            minPriceField.setEditable(false);
        } else {
            minPriceField.setEditable(true);
        }
    }//GEN-LAST:event_minPriceFieldKeyPressed

    private void maxPriceFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_maxPriceFieldKeyPressed
        char inputChar = evt.getKeyChar();
        if (Character.isLetter(inputChar)) {
            maxPriceField.setEditable(false);
        } else {
            maxPriceField.setEditable(true);
        }
    }//GEN-LAST:event_maxPriceFieldKeyPressed

    private void filterComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterComboBoxActionPerformed
        bookCRUD.setCurrentOffset(0);
        bookCRUD.loadBooksIntoTable();
    }//GEN-LAST:event_filterComboBoxActionPerformed

    private void minPriceFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minPriceFieldActionPerformed
        bookCRUD.setCurrentOffset(0);
        bookCRUD.loadBooksIntoTable();
    }//GEN-LAST:event_minPriceFieldActionPerformed

    private void maxPriceFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxPriceFieldActionPerformed
        bookCRUD.setCurrentOffset(0);
        bookCRUD.loadBooksIntoTable();
    }//GEN-LAST:event_maxPriceFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.group06.bsms.components.AutocompletePanel authorAutoComp1;
    private javax.swing.JLabel authorLabel1;
    private javax.swing.JLabel authorLabel2;
    private javax.swing.JLabel categoryLabel1;
    private com.group06.bsms.components.CategorySelectionPanel categorySelectionPanel1;
    private javax.swing.JComboBox<String> filterComboBox;
    private javax.swing.JPanel groupFieldPanel1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField maxPriceField;
    private javax.swing.JTextField minPriceField;
    private com.group06.bsms.components.AutocompletePanel publisherAutoComp1;
    private javax.swing.JLabel publisherLabel1;
    private javax.swing.JButton removeAllBtn;
    private javax.swing.JLabel salePriceLabel;
    // End of variables declaration//GEN-END:variables
}

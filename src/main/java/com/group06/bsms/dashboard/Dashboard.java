package com.group06.bsms.dashboard;

import com.group06.bsms.Main;
import com.group06.bsms.accounts.UpdateAccount;
import com.group06.bsms.authors.AddAuthorInformation;
import com.group06.bsms.authors.AuthorCRUD;
import com.group06.bsms.authors.UpdateAuthor;
import com.group06.bsms.books.AddBookInformation;
import com.group06.bsms.books.BookCRUD;
import com.group06.bsms.books.UpdateBook;
import com.group06.bsms.categories.AddCategoryInformation;
import com.group06.bsms.categories.CategoryCRUD;
import com.group06.bsms.categories.UpdateCategory;
import com.group06.bsms.publishers.AddPublisherInformation;
import com.group06.bsms.publishers.PublisherCRUD;
import com.group06.bsms.publishers.UpdatePublisher;
import com.group06.bsms.importsheet.ImportSheetCRUD;
import com.group06.bsms.importsheet.AddImportSheet;
import com.group06.bsms.importsheet.ViewImportSheet;
import com.group06.bsms.members.AddMember;
import com.group06.bsms.members.MemberCRUD;
import com.group06.bsms.ordersheet.AddOrderSheet;
import com.group06.bsms.ordersheet.OrderSheetCRUD;
import com.group06.bsms.ordersheet.ViewOrderSheet;
import com.group06.bsms.utils.SVGHelper;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;

public class Dashboard extends javax.swing.JPanel {

    public static final Dashboard dashboard = new Dashboard();
    private final UpdateBook updateBook = new UpdateBook();
    private final AddBookInformation addBookInfo = new AddBookInformation();
    private final BookCRUD bookCRUD = new BookCRUD(updateBook, addBookInfo);

    private final UpdateCategory updateCategory = new UpdateCategory();
    private final AddCategoryInformation addCategoryInfo = new AddCategoryInformation();
    private final CategoryCRUD categoryCRUD = new CategoryCRUD(updateCategory, addCategoryInfo, bookCRUD);

    private final UpdateAuthor updateAuthor = new UpdateAuthor();
    private final AddAuthorInformation addAuthorInfo = new AddAuthorInformation();
    private final AuthorCRUD authorCRUD = new AuthorCRUD(updateAuthor, addAuthorInfo, bookCRUD);

    private final UpdatePublisher updatePublisher = new UpdatePublisher();
    private final AddPublisherInformation addPublisherInfo = new AddPublisherInformation();
    private final PublisherCRUD publisherCRUD = new PublisherCRUD(updatePublisher, addPublisherInfo, bookCRUD);

    private final UpdateAccount updateUser = new UpdateAccount();

    private final AddImportSheet addImportSheet = new AddImportSheet();
    private final ViewImportSheet viewImportSheet = new ViewImportSheet();
    private final ImportSheetCRUD importSheetCRUD = new ImportSheetCRUD(viewImportSheet, addImportSheet);

    private final AddMember addMember = new AddMember();
    private final AddOrderSheet addOrderSheet = new AddOrderSheet();
    private final MemberCRUD memberCRUD = new MemberCRUD(addMember, addOrderSheet);
    private final ViewOrderSheet viewOrderSheet = new ViewOrderSheet();
    private final OrderSheetCRUD orderSheetCRUD = new OrderSheetCRUD(viewOrderSheet, memberCRUD);

    private Dashboard() {
        initComponents();
        layout = new CardLayout();
        main.setLayout(layout);

        updateBook.setBookCRUD(bookCRUD);
        addBookInfo.setBookCRUD(bookCRUD);
        main.add(bookCRUD, "bookCRUD");
        main.add(updateBook, "updateBook");
        main.add(addBookInfo, "addBookInformation");

        updateCategory.setCategoryCRUD(categoryCRUD);
        addCategoryInfo.setCategoryCRUD(categoryCRUD);
        main.add(categoryCRUD, "categoryCRUD");
        main.add(updateCategory, "updateCategory");
        main.add(addCategoryInfo, "addCategoryInformation");

        updateAuthor.setAuthorCRUD(authorCRUD);
        addAuthorInfo.setAuthorCRUD(authorCRUD);
        main.add(authorCRUD, "authorCRUD");
        main.add(updateAuthor, "updateAuthor");
        main.add(addAuthorInfo, "addAuthorInformation");

        updatePublisher.setPublisherCRUD(publisherCRUD);
        addPublisherInfo.setPublisherCRUD(publisherCRUD);
        main.add(publisherCRUD, "publisherCRUD");
        main.add(updatePublisher, "updatePublisher");
        main.add(addPublisherInfo, "addPublisherInformation");

        main.add(updateUser, "updateUser");

        addImportSheet.setImportSheetCRUD(importSheetCRUD);
        main.add(importSheetCRUD, "importSheetCRUD");
        main.add(addImportSheet, "addImportSheet");
        main.add(viewImportSheet, "viewImportSheet");

        main.add(memberCRUD, "memberCRUD");
        main.add(addMember, "addMember");

        main.add(orderSheetCRUD, "orderSheetCRUD");
        main.add(viewOrderSheet, "viewOrderSheet");
        main.add(addOrderSheet, "addOrderSheet");

    }

    public void switchTab(String tab) {
        layout.show(main, tab);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        logo = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        books = new javax.swing.JButton();
        authors = new javax.swing.JButton();
        publishers = new javax.swing.JButton();
        categories = new javax.swing.JButton();
        importSheets = new javax.swing.JButton();
        orderSheet = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        account = new javax.swing.JButton();
        logout = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 8), new java.awt.Dimension(0, 8), new java.awt.Dimension(32767, 8));
        main = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jToolBar1.setBackground(UIManager.getColor("subBackground"));
        jToolBar1.setFloatable(true);
        jToolBar1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar1.setRollover(true);
        jToolBar1.setMaximumSize(new java.awt.Dimension(64, 65788));
        jToolBar1.setMinimumSize(new java.awt.Dimension(64, 64));
        jToolBar1.setPreferredSize(new java.awt.Dimension(64, 64));
        jToolBar1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jToolBar1ComponentResized(evt);
            }
        });

        logo.setBackground(UIManager.getColor("accentColor"));
        logo.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/book.svg", 
            Color.black, Color.white,
            28, 28
        ));
        logo.setFocusPainted(false);
        logo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        logo.setIconTextGap(0);
        logo.setMaximumSize(new java.awt.Dimension(58, 58));
        logo.setMinimumSize(new java.awt.Dimension(58, 58));
        logo.setPreferredSize(new java.awt.Dimension(58, 58));
        jToolBar1.add(logo);
        jToolBar1.add(filler1);

        books.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/book.svg", 
            Color.black, Color.black,
            28, 28
        ));
        books.setMnemonic('1');
        books.setToolTipText("Books");
        books.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        books.setMaximumSize(new java.awt.Dimension(58, 58));
        books.setMinimumSize(new java.awt.Dimension(58, 58));
        books.setPreferredSize(new java.awt.Dimension(58, 58));
        books.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                booksActionPerformed(evt);
            }
        });
        jToolBar1.add(books);

        authors.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/person.svg", 
            Color.black, Color.black,
            28, 28
        ));
        authors.setMnemonic('2');
        authors.setToolTipText("Authors");
        authors.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        authors.setMaximumSize(new java.awt.Dimension(58, 58));
        authors.setMinimumSize(new java.awt.Dimension(58, 58));
        authors.setPreferredSize(new java.awt.Dimension(58, 58));
        authors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                authorsActionPerformed(evt);
            }
        });
        jToolBar1.add(authors);

        publishers.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/publisher.svg", 
            Color.black, Color.black,
            28, 28
        ));
        publishers.setMnemonic('3');
        publishers.setToolTipText("Publishers");
        publishers.setFocusable(false);
        publishers.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        publishers.setMaximumSize(new java.awt.Dimension(58, 58));
        publishers.setMinimumSize(new java.awt.Dimension(58, 58));
        publishers.setPreferredSize(new java.awt.Dimension(58, 58));
        publishers.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        publishers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                publishersActionPerformed(evt);
            }
        });
        jToolBar1.add(publishers);

        categories.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/category.svg", 
            Color.black, Color.black,
            28, 28
        ));
        categories.setMnemonic('4');
        categories.setToolTipText("Categories");
        categories.setFocusable(false);
        categories.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        categories.setMaximumSize(new java.awt.Dimension(58, 58));
        categories.setMinimumSize(new java.awt.Dimension(58, 58));
        categories.setPreferredSize(new java.awt.Dimension(58, 58));
        categories.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        categories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoriesActionPerformed(evt);
            }
        });
        jToolBar1.add(categories);

        importSheets.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/import-sheet.svg", 
            Color.black, Color.black,
            28, 28
        ));
        importSheets.setMnemonic('4');
        importSheets.setToolTipText("Import sheets");
        importSheets.setFocusable(false);
        importSheets.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        importSheets.setMaximumSize(new java.awt.Dimension(58, 58));
        importSheets.setMinimumSize(new java.awt.Dimension(58, 58));
        importSheets.setPreferredSize(new java.awt.Dimension(58, 58));
        importSheets.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        importSheets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importSheetsActionPerformed(evt);
            }
        });
        jToolBar1.add(importSheets);

        orderSheet.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/order-sheet.svg", 
            Color.black, Color.black,
            28, 28
        ));
        orderSheet.setMnemonic('4');
        orderSheet.setToolTipText("Order sheets");
        orderSheet.setFocusable(false);
        orderSheet.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        orderSheet.setMaximumSize(new java.awt.Dimension(58, 58));
        orderSheet.setMinimumSize(new java.awt.Dimension(58, 58));
        orderSheet.setPreferredSize(new java.awt.Dimension(58, 58));
        orderSheet.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        orderSheet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderSheetActionPerformed(evt);
            }
        });
        jToolBar1.add(orderSheet);
        jToolBar1.add(filler2);

        account.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/account.svg", 
            Color.black, UIManager.getColor("accentColor"),
            28, 28
        ));
        account.setMnemonic('9');
        account.setToolTipText("Account");
        account.setFocusPainted(false);
        account.setFocusable(false);
        account.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        account.setIconTextGap(0);
        account.setMaximumSize(new java.awt.Dimension(58, 58));
        account.setMinimumSize(new java.awt.Dimension(58, 58));
        account.setPreferredSize(new java.awt.Dimension(58, 58));
        account.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        account.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                accountActionPerformed(evt);
            }
        });
        jToolBar1.add(account);

        logout.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/logout.svg", 
            Color.black, UIManager.getColor("redColor"),
            28, 28
        ));
        logout.setMnemonic('0');
        logout.setToolTipText("Logout");
        logout.setFocusPainted(false);
        logout.setFocusable(false);
        logout.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        logout.setIconTextGap(0);
        logout.setMaximumSize(new java.awt.Dimension(58, 58));
        logout.setMinimumSize(new java.awt.Dimension(58, 58));
        logout.setPreferredSize(new java.awt.Dimension(58, 58));
        logout.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        logout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutActionPerformed(evt);
            }
        });
        jToolBar1.add(logout);
        jToolBar1.add(filler3);

        add(jToolBar1, java.awt.BorderLayout.LINE_START);

        javax.swing.GroupLayout mainLayout = new javax.swing.GroupLayout(main);
        main.setLayout(mainLayout);
        mainLayout.setHorizontalGroup(
            mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 335, Short.MAX_VALUE)
        );
        mainLayout.setVerticalGroup(
            mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 524, Short.MAX_VALUE)
        );

        add(main, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jToolBar1ComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jToolBar1ComponentResized
        if (jToolBar1.getSize().equals(new Dimension(64, 64))) {
            jToolBar1.setOrientation(JToolBar.HORIZONTAL);
            jToolBar1.setPreferredSize(new Dimension(512, 64));
            jToolBar1.revalidate();
            jToolBar1.repaint();
        } else {
            jToolBar1.setPreferredSize(new Dimension(64, 64));
        }
    }//GEN-LAST:event_jToolBar1ComponentResized

    private void booksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_booksActionPerformed
        switchTab("bookCRUD");
        bookCRUD.reloadBooks();
    }//GEN-LAST:event_booksActionPerformed

    private void categoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoriesActionPerformed
        switchTab("categoryCRUD");
        categoryCRUD.reloadCategories();
    }//GEN-LAST:event_categoriesActionPerformed

    private void authorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_authorsActionPerformed
        switchTab("authorCRUD");
        authorCRUD.reloadAuthors();
    }//GEN-LAST:event_authorsActionPerformed

    private void publishersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_publishersActionPerformed
        switchTab("publisherCRUD");
        publisherCRUD.reloadPublishers();
    }//GEN-LAST:event_publishersActionPerformed

    private void logoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutActionPerformed
        var options = new String[]{"Logout", "Cancel"};

        if (JOptionPane.showOptionDialog(
                Main.getApp(),
                "Are you sure you want to logout?",
                "BSMS Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        ) == JOptionPane.YES_OPTION) {
            switchTab("bookCRUD");
            Main.getApp().switchTab("login");
        }
    }//GEN-LAST:event_logoutActionPerformed

    private void accountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accountActionPerformed
        updateUser.setAccountById(Main.getUserId());
        switchTab("updateAccount");
    }//GEN-LAST:event_accountActionPerformed

    private void importSheetsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importSheetsActionPerformed
        switchTab("importSheetCRUD");
        importSheetCRUD.reloadImportSheets();
    }//GEN-LAST:event_importSheetsActionPerformed

    private void orderSheetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderSheetActionPerformed
        // TODO add your handling code here:
        switchTab("orderSheetCRUD");
        orderSheetCRUD.reloadOrderSheets();
    }//GEN-LAST:event_orderSheetActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.CardLayout layout;
    private javax.swing.JButton account;
    private javax.swing.JButton authors;
    private javax.swing.JButton books;
    private javax.swing.JButton categories;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JButton importSheets;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton logo;
    private javax.swing.JButton logout;
    private javax.swing.JPanel main;
    private javax.swing.JButton orderSheet;
    private javax.swing.JButton publishers;
    // End of variables declaration//GEN-END:variables
}

package com.group06.bsms.dashboard;

import com.group06.bsms.books.AddBookInformation;
import com.group06.bsms.books.BookCRUD;
import com.group06.bsms.books.UpdateBook;
import java.awt.CardLayout;
import javax.swing.UIManager;

public class Dashboard extends javax.swing.JPanel {

    public static final Dashboard dashboard = new Dashboard();

    private Dashboard() {
        initComponents();
        layout = new CardLayout();
        main.setLayout(layout);

        var updateBook = new UpdateBook();
        var addBookInfo = new AddBookInformation();
        var bookCRUD = new BookCRUD(updateBook, addBookInfo);
        updateBook.setBookCRUD(bookCRUD);
        addBookInfo.setBookCRUD(bookCRUD);

        main.add(bookCRUD, "bookCRUD");
        main.add(updateBook, "updateBook");
        main.add(addBookInfo, "addBookInformation");
    }

    public void switchTab(String tab) {
        layout.show(main, tab);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sidebar = new javax.swing.JPanel();
        main = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        sidebar.setBackground(UIManager.getColor("mutedBackground"));
        sidebar.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        javax.swing.GroupLayout sidebarLayout = new javax.swing.GroupLayout(sidebar);
        sidebar.setLayout(sidebarLayout);
        sidebarLayout.setHorizontalGroup(
            sidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        sidebarLayout.setVerticalGroup(
            sidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        add(sidebar, java.awt.BorderLayout.LINE_START);

        javax.swing.GroupLayout mainLayout = new javax.swing.GroupLayout(main);
        main.setLayout(mainLayout);
        mainLayout.setHorizontalGroup(
            mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        mainLayout.setVerticalGroup(
            mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        add(main, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private java.awt.CardLayout layout;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel main;
    private javax.swing.JPanel sidebar;
    // End of variables declaration//GEN-END:variables
}

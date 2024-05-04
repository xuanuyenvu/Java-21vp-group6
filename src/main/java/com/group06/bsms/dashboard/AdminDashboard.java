package com.group06.bsms.dashboard;

import com.group06.bsms.Main;
import com.group06.bsms.accounts.EmployeeRevenue;
import com.group06.bsms.accounts.AccountCRUD;
import com.group06.bsms.accounts.AddAccountInformation;
import com.group06.bsms.accounts.ChangePassword;
import com.group06.bsms.accounts.UpdateAccount;
import com.group06.bsms.accounts.UpdateProfile;
import com.group06.bsms.books.BookCRUD;
import com.group06.bsms.books.BookRevenue;
import com.group06.bsms.categories.CategoryRevenue;
import com.group06.bsms.members.MemberRevenue;
import com.group06.bsms.utils.SVGHelper;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;

public class AdminDashboard extends javax.swing.JPanel {

    public static final AdminDashboard dashboard = new AdminDashboard();
    private final BookRevenue bookRevenue = new BookRevenue();
    private final CategoryRevenue categoryRevenue = new CategoryRevenue();
    private final MemberRevenue memberRevenue = new MemberRevenue();
    private final EmployeeRevenue employeeRevenue = new EmployeeRevenue();

    private final UpdateAccount updateAccount = new UpdateAccount();
    private final UpdateProfile updateProfile = new UpdateProfile();
    private final ChangePassword changePassword = new ChangePassword();
    private final AddAccountInformation addAccountInfo = new AddAccountInformation();
    private final AccountCRUD accountCRUD = new AccountCRUD(updateAccount, addAccountInfo, new BookCRUD());

    private AdminDashboard() {
        initComponents();
        layout = new CardLayout();
        main.setLayout(layout);

        main.add(bookRevenue, "bookRevenue");
        main.add(categoryRevenue, "categoryRevenue");
        main.add(memberRevenue, "memberRevenue");
        main.add(employeeRevenue, "employeeRevenue");

        updateAccount.setAccountCRUD(accountCRUD);
        addAccountInfo.setAccountCRUD(accountCRUD);
        main.add(accountCRUD, "accountCRUD");
        main.add(updateAccount, "updateAccount");
        main.add(addAccountInfo, "addAccountInformation");

        main.add(updateProfile, "updateProfile");
        main.add(changePassword, "changePassword");
    }

    public void switchTab(String tab) {
        layout.show(main, tab);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        itemsOfAccount = new javax.swing.JPopupMenu();
        profile = new javax.swing.JMenuItem();
        password = new javax.swing.JMenuItem();
        jToolBar1 = new javax.swing.JToolBar();
        logo = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        accounts = new javax.swing.JButton();
        books = new javax.swing.JButton();
        categories = new javax.swing.JButton();
        authors = new javax.swing.JButton();
        publishers = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        account = new javax.swing.JButton();
        logout = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 8), new java.awt.Dimension(0, 8), new java.awt.Dimension(32767, 8));
        main = new javax.swing.JPanel();

        profile.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        profile.setText("Update profile");
        profile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profileActionPerformed(evt);
            }
        });
        itemsOfAccount.add(profile);

        password.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        password.setText("Change password");
        password.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordActionPerformed(evt);
            }
        });
        itemsOfAccount.add(password);

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

        accounts.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/person.svg", 
            Color.black, Color.black,
            28, 28
        ));
        accounts.setMnemonic('1');
        accounts.setToolTipText("Accounts");
        accounts.setFocusable(false);
        accounts.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        accounts.setMaximumSize(new java.awt.Dimension(58, 58));
        accounts.setMinimumSize(new java.awt.Dimension(58, 58));
        accounts.setPreferredSize(new java.awt.Dimension(58, 58));
        accounts.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        accounts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                accountsActionPerformed(evt);
            }
        });
        jToolBar1.add(accounts);

        books.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/book.svg", 
            Color.black, Color.black,
            28, 28
        ));
        books.setMnemonic('2');
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

        categories.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/category.svg", 
            Color.black, Color.black,
            28, 28
        ));
        categories.setMnemonic('3');
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

        authors.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/person.svg", 
            Color.black, Color.black,
            28, 28
        ));
        authors.setMnemonic('4');
        authors.setToolTipText("Customer");
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
            "icons/person.svg", 
            Color.black, Color.black,
            28, 28
        ));
        publishers.setMnemonic('5');
        publishers.setToolTipText("Employees");
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
        account.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                accountMousePressed(evt);
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
            .addGap(0, 640, Short.MAX_VALUE)
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
        switchTab("bookRevenue");
    }//GEN-LAST:event_booksActionPerformed

    private void categoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoriesActionPerformed
        switchTab("categoryRevenue");
    }//GEN-LAST:event_categoriesActionPerformed

    private void authorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_authorsActionPerformed
        switchTab("memberRevenue");
    }//GEN-LAST:event_authorsActionPerformed

    private void publishersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_publishersActionPerformed
        switchTab("employeeRevenue");
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

    private void accountsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accountsActionPerformed
        switchTab("accountCRUD");
        accountCRUD.reloadAccounts();
    }//GEN-LAST:event_accountsActionPerformed

    private void accountMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_accountMousePressed
        itemsOfAccount.show(evt.getComponent(), evt.getX(), evt.getY());
    }//GEN-LAST:event_accountMousePressed

    private void profileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_profileActionPerformed
        updateProfile.setAccountById(Main.getUserId());
        switchTab("updateProfile");
    }//GEN-LAST:event_profileActionPerformed

    private void passwordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordActionPerformed
        changePassword.setAccountById(Main.getUserId());
        switchTab("changePassword");
    }//GEN-LAST:event_passwordActionPerformed

    private java.awt.CardLayout layout;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton account;
    private javax.swing.JButton accounts;
    private javax.swing.JButton authors;
    private javax.swing.JButton books;
    private javax.swing.JButton categories;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JPopupMenu itemsOfAccount;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton logo;
    private javax.swing.JButton logout;
    private javax.swing.JPanel main;
    private javax.swing.JMenuItem password;
    private javax.swing.JMenuItem profile;
    private javax.swing.JButton publishers;
    // End of variables declaration//GEN-END:variables
}

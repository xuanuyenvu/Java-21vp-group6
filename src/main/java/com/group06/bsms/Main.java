package com.group06.bsms;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;

import com.group06.bsms.auth.Login;
import com.group06.bsms.dashboard.AdminDashboard;
import com.group06.bsms.dashboard.Dashboard;
import com.group06.bsms.utils.SVGHelper;
import java.awt.CardLayout;
import java.awt.Color;
import javax.swing.*;

public class Main extends JFrame {

    private static Main app;
    private static int userId;
    public static final boolean INDEV = true;
    public static final int BREAK_POINT = 640;
    public static final int ROW_LIMIT = 10;

    private static boolean darkMode = false;

    public static Main getApp() {
        return app;
    }

    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int newUserId) {
        userId = newUserId;
    }

    private Main() {
        if (!INDEV) {
            setExtendedState(MAXIMIZED_BOTH);
        }

        initComponents();
        layout = new CardLayout();
        panel.setLayout(layout);
         panel.add(new Login(), "login");
         panel.add(Dashboard.dashboard, "dashboard");
         panel.add(AdminDashboard.dashboard, "adminDashboard");
        
        if (INDEV) {
            setSize(BREAK_POINT * 2, BREAK_POINT);
        }

        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BSMS");
        setIconImage(SVGHelper.createSVGIconWithFilter(
            "icons/book.svg",
            Color.black,
            Color.black, Color.white,
            null, null
        ).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        panel.setLayout(new java.awt.CardLayout());
        getContentPane().add(panel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowClosed
        try {
            DB.disconnectFromDB();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    app,
                    "Could not disconnect from database. Please restart your computer.",
                    "BSMS Critical error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }// GEN-LAST:event_formWindowClosed

    public void switchTab(String tab) {
        layout.show(panel, tab);
    }

    public void toggleTheme() {
        darkMode = !darkMode;

        FlatAnimatedLafChange.showSnapshot();
        if (darkMode) {
            FlatDarkLaf.setup();
        } else {
            FlatLightLaf.setup();
        }
        SwingUtilities.updateComponentTreeUI(this);
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    public static void main(String args[]) {
        try {
            FlatLaf.registerCustomDefaultsSource("themes");
            FlatLightLaf.setup();

            DB.connectToDB("env/bsms.properties");

            java.awt.EventQueue.invokeLater(() -> {
                app = new Main();
                app.setVisible(true);
            });
        } catch (Exception e) {
            java.awt.EventQueue.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        app,
                        "Could not connect to database. Please try again later.",
                        "BSMS Critical error",
                        JOptionPane.ERROR_MESSAGE);
            });
        }
    }

    private java.awt.CardLayout layout;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables
}

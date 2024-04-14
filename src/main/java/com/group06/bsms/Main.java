package com.group06.bsms;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.group06.bsms.accounts.EmployeeRevenue;
import com.group06.bsms.auth.Login;
import com.group06.bsms.dashboard.Dashboard;
import com.group06.bsms.members.MemberRevenue;
import com.group06.bsms.utils.SVGHelper;
import java.awt.CardLayout;
import java.awt.Color;
import javax.swing.*;

public class Main extends JFrame {

    public static Main app;
    public static final boolean INDEV = true;
    public static final int BREAK_POINT = 640;
    public static final boolean DARK_MODE = false;
    public static final int ROW_LIMIT = 10;

    private Main() {
        if (!INDEV) {
            setExtendedState(MAXIMIZED_BOTH);
        }

        initComponents();
        layout = new CardLayout();
        panel.setLayout(layout);
        panel.add(new Login(), "login");
        panel.add(Dashboard.dashboard, "dashboard");
        panel.add(new EmployeeRevenue(), "dashboard");

        switchTab("dashboard");

        if (INDEV) {
            setSize(BREAK_POINT * 2, BREAK_POINT);
        }

        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
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

    public static void main(String args[]) {
        FlatLaf.registerCustomDefaultsSource("themes");

        if (DARK_MODE) {
            FlatDarkLaf.setup();
        } else {
            FlatLightLaf.setup();
        }

        try {
            DB.connectToDB("/env/bsms.properties");

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

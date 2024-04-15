package com.group06.bsms.auth;

import com.formdev.flatlaf.FlatClientProperties;
import com.group06.bsms.DB;
import com.group06.bsms.Main;
import static com.group06.bsms.Main.app;
import com.group06.bsms.utils.SVGHelper;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class Login extends JPanel {

    private final AuthService authService;

    public Login() {
        this(new AuthService(new AuthRepository(DB.db())));
    }

    public Login(AuthService authService) {
        this.authService = authService;

        initComponents();

        phone.putClientProperty("JTextField.placeholderText", "Enter your phone");
        password.putClientProperty("JTextField.placeholderText", "Enter your password");

        logo.setMargin(new Insets(12, 12, 12, 12));
        logo.putClientProperty(FlatClientProperties.STYLE, ""
                + "background: @accentColor;"
                + "arc: 36;"
                + "buttonType: borderless;"
        );

        try {
            if (authService.isFirstLogin()) {
                JOptionPane.showMessageDialog(
                        app,
                        "This seems to be your first time logging in.\n"
                        + "Choose your phone and password and we will register your account!",
                        "BSMS Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    app,
                    "Unable to get accounts. Please login normally or try restarting.",
                    "BSMS Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        aside = new javax.swing.JPanel();
        asideForm = new javax.swing.JPanel();
        logo = new javax.swing.JButton();
        asideTitle = new javax.swing.JLabel();
        asideSubtitle = new javax.swing.JLabel();
        description = new javax.swing.JLabel();
        main = new javax.swing.JPanel();
        form = new javax.swing.JPanel();
        title = new javax.swing.JLabel();
        subtitle = new javax.swing.JLabel();
        phoneLabel = new javax.swing.JLabel();
        phone = new javax.swing.JTextField();
        passwordLabel = new javax.swing.JLabel();
        password = new javax.swing.JPasswordField();
        login = new javax.swing.JButton();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        setLayout(new java.awt.GridBagLayout());

        aside.setBackground(UIManager.getColor("mutedBackground")
        );
        aside.setPreferredSize(new java.awt.Dimension(0, 0));
        aside.setLayout(new java.awt.GridBagLayout());

        asideForm.setOpaque(false);

        logo.setBackground(UIManager.getColor("accentColor"));
        logo.setIcon(SVGHelper.createSVGIconWithFilter(
            "icons/book.svg", 
            Color.black, Color.white,
            72, 72
        ));
        logo.setFocusPainted(false);
        logo.setFocusable(false);
        logo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        logo.setIconTextGap(0);

        asideTitle.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        asideTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        asideTitle.setText("BSMS");
        asideTitle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        asideSubtitle.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        asideSubtitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        asideSubtitle.setText("Bookstore Management System");
        asideSubtitle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        description.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        description.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        description.setText("<html><center>A powerful, yet easy-to-use<br>application for managing<br>bookstore data.</center></html>");
        description.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout asideFormLayout = new javax.swing.GroupLayout(asideForm);
        asideForm.setLayout(asideFormLayout);
        asideFormLayout.setHorizontalGroup(
            asideFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(asideSubtitle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(asideFormLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(description, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(asideFormLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(asideFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(asideTitle)
                    .addComponent(logo))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        asideFormLayout.setVerticalGroup(
            asideFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(asideFormLayout.createSequentialGroup()
                .addComponent(logo)
                .addGap(18, 18, 18)
                .addComponent(asideTitle)
                .addGap(0, 0, 0)
                .addComponent(asideSubtitle)
                .addGap(36, 36, 36)
                .addComponent(description, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        aside.add(asideForm, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 40);
        add(aside, gridBagConstraints);

        main.setPreferredSize(new java.awt.Dimension(0, 0));
        main.setLayout(new java.awt.GridBagLayout());

        title.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        title.setText("Welcome back!");

        subtitle.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        subtitle.setForeground(UIManager.getColor("mutedColor"));
        subtitle.setText("Enter your login details below");

        phoneLabel.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        phoneLabel.setLabelFor(phone);
        phoneLabel.setText("Phone");

        phone.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        phone.setToolTipText("");
        phone.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        passwordLabel.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        passwordLabel.setLabelFor(password);
        passwordLabel.setText("Password");

        password.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N

        login.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        login.setMnemonic('L');
        login.setText("Login");
        login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout formLayout = new javax.swing.GroupLayout(form);
        form.setLayout(formLayout);
        formLayout.setHorizontalGroup(
            formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(phone)
            .addComponent(password)
            .addGroup(formLayout.createSequentialGroup()
                .addGroup(formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(title)
                    .addComponent(subtitle)
                    .addComponent(passwordLabel)
                    .addComponent(phoneLabel))
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(login, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        formLayout.setVerticalGroup(
            formLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formLayout.createSequentialGroup()
                .addComponent(title)
                .addGap(0, 0, 0)
                .addComponent(subtitle)
                .addGap(18, 18, 18)
                .addComponent(phoneLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(phone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(passwordLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(login)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        main.add(form, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 1.0;
        add(main, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        var gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;

        if (getWidth() > Main.BREAK_POINT) {
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            aside.setVisible(true);
        } else {
            gridBagConstraints.anchor = GridBagConstraints.CENTER;
            aside.setVisible(false);
        }

        main.add(form, gridBagConstraints);
    }//GEN-LAST:event_formComponentResized

    private void loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginActionPerformed
        var phoneValue = phone.getText();
        var passwordValue = new String(password.getPassword());

        try {
            if (authService.authenticate(phoneValue, passwordValue)) {
                Main.app.switchTab("adminDashboard");
            } else {
                Main.app.switchTab("dashboard");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    Main.app,
                    e.getMessage(),
                    "BSMS Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_loginActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel aside;
    private javax.swing.JPanel asideForm;
    private javax.swing.JLabel asideSubtitle;
    private javax.swing.JLabel asideTitle;
    private javax.swing.JLabel description;
    private javax.swing.JPanel form;
    private javax.swing.JButton login;
    private javax.swing.JButton logo;
    private javax.swing.JPanel main;
    private javax.swing.JPasswordField password;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JTextField phone;
    private javax.swing.JLabel phoneLabel;
    private javax.swing.JLabel subtitle;
    private javax.swing.JLabel title;
    // End of variables declaration//GEN-END:variables
}

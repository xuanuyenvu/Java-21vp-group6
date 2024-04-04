package com.group06.bsms.components;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.JOptionPane;

public class DatePickerPanel extends javax.swing.JPanel {

    public DatePickerPanel() {
        initComponents();
        setDefault();
        addTextFieldListener();
    }

    public void setText(String text){
        jTextField.setText(text);
        
    }
    
    public void setDate(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        jTextField.setText(dateFormat.format(date));
        jXDatePicker.setDate(date);
    }

    public void setPlaceholder(String text) {
        jTextField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, text);
    }

    public Date getDate() {
        return jXDatePicker.getDate();
    }

    public java.sql.Date getDateSQL() {
        return new java.sql.Date(jXDatePicker.getDate().getTime());
    }

    private void addTextFieldListener() {
        jTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String text = jTextField.getText().trim();

                if (!isValidDateFormat(text)) {
                    jTextField.setText("");
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date date = dateFormat.parse(text);
                        jXDatePicker.setDate(date);
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid date format: " + text, "BSMS Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private boolean isValidDateFormat(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private void setDefault() {
        LocalDate now = LocalDate.now(ZoneId.systemDefault());
        Date today = java.sql.Date.valueOf(now);
        jXDatePicker.setDate(today);
        jXDatePicker.getMonthView().setUpperBound(today);

        jXDatePicker.addActionListener((ActionEvent e) -> {
            Date selectedDate = jXDatePicker.getDate();
            if (selectedDate != null) {
                LocalDate selectedLocalDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (selectedLocalDate.compareTo(now) > 0) {
                    jXDatePicker.setDate(today);
                }
            }
        });
    }

    public void setEmptyText() {
        jTextField.setText("");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jXDatePicker = new org.jdesktop.swingx.JXDatePicker();
        jTextField = new javax.swing.JTextField();

        setMaximumSize(new java.awt.Dimension(32767, 31));
        setMinimumSize(new java.awt.Dimension(0, 0));
        setPreferredSize(new java.awt.Dimension(0, 0));

        jXDatePicker.setMaximumSize(new java.awt.Dimension(30, 31));
        jXDatePicker.setMinimumSize(new java.awt.Dimension(30, 31));
        jXDatePicker.setPreferredSize(new java.awt.Dimension(30, 31));
        jXDatePicker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXDatePickerActionPerformed(evt);
            }
        });

        jTextField.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jTextField.setPreferredSize(new java.awt.Dimension(184, 31));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jXDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jXDatePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jXDatePickerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXDatePickerActionPerformed
        Date selectedDate = jXDatePicker.getDate();
        if (selectedDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = dateFormat.format(selectedDate);
            jTextField.setText(formattedDate);
        }
    }//GEN-LAST:event_jXDatePickerActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField jTextField;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker;
    // End of variables declaration//GEN-END:variables
}

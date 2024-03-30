package com.group06.bsms.components;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.UIManager;

public class CustomLabelInForm {

    public static void setColoredText(JLabel label) {
        Color color = UIManager.getColor("redColor");
        label.setText(String.format("<html>%s<font color='%s'> *</font></html>", label.getText(), getColorHex(color)));
    }

    private static String getColorHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}

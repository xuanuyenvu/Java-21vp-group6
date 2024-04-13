/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.group06.bsms.importsheet;

import com.group06.bsms.books.Book;
import com.group06.bsms.books.BookService;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.Vector;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

public class AutoSuggestComboBoxEditor extends AbstractCellEditor implements TableCellEditor {

        private final JComboBox<String> comboBox;
        private final Vector<String> suggestions;
        private final BookService bookService;
        private Map<String, Book> bookMap;
        public AutoSuggestComboBoxEditor(BookService bookService, Map<String, Book> bookMap) {
            comboBox = new JComboBox<>();
            suggestions = new Vector<>();
            this.bookService = bookService;
            this.bookMap = bookMap;
            initComponents();
            
        }
        private void initComponents(){
            comboBox.setEditable(true);
            JTextField textField = (JTextField) comboBox.getEditor().getEditorComponent();
            textField.addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    EventQueue.invokeLater(() -> {
                        String text = textField.getText();
                        if (text.length() == 0) {
                            comboBox.hidePopup();
                            setModel(new DefaultComboBoxModel<>(suggestions), "");
                        } else {

                            java.util.List<Book> books;
                            try {

                                books = bookService.searchBooksByTitle(text);
                            } catch (Exception ex) {
                                books = null;

                            }
                            suggestions.clear();
                            bookMap.clear();
                            for (var book : books) {
                                suggestions.add(book.title);
                                bookMap.put(book.title, book);
                            }
                            DefaultComboBoxModel<String> model = getSuggestedModel(suggestions, text);
                            if (model.getSize() == 0) {
                                comboBox.hidePopup();
                            } else {
                                setModel(model, text);
                                comboBox.showPopup();
                            }
                        }
                    });
                }
            });
        }
        
        private void setModel(DefaultComboBoxModel<String> model, String str) {
            comboBox.setModel(model);
            comboBox.setSelectedIndex(-1);
            comboBox.getEditor().setItem(str);
        }

        private DefaultComboBoxModel<String> getSuggestedModel(java.util.List<String> list, String text) {
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            for (String s : list) {
                model.addElement(s);
            }
            return model;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            comboBox.setSelectedItem(value);
            return comboBox;
        }

        @Override
        public Object getCellEditorValue() {
            return comboBox.getEditor().getItem();
        }
    }
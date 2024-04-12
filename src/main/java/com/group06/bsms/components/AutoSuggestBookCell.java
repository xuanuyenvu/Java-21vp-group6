package com.group06.bsms.components;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;
import java.util.ArrayList;
import java.util.List;
import com.group06.bsms.books.Book;
import com.group06.bsms.books.BookService;

public class AutoSuggestBookCell extends AbstractCellEditor implements TableCellEditor {

    private final JComboBox<Book> comboBox = new JComboBox<>();
    private final Vector<Book> suggestions = new Vector<>();
    private final BookService bookService;

    public AutoSuggestBookCell(BookService bookService) {
        this.bookService = bookService;
        initComponents();

    }

    public void initComponents() {
        comboBox.setEditable(true);
        comboBox.setRenderer(new BookComboBoxRenderer());
        JTextField textField = (JTextField) comboBox.getEditor().getEditorComponent();
        textField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                EventQueue.invokeLater(() -> {
                    String text = textField.getText();
                    if (text.length() == 0) {
                        comboBox.hidePopup();
                        setModel(new DefaultComboBoxModel<>(suggestions), null);
                    } else {
                        List<Book> books;
                        try {

                            books = bookService.searchBooksByTitle(text);

                        } catch (Exception ex) {
                            books = new ArrayList<>();
                        }
                        if (books.isEmpty()) {
                            comboBox.hidePopup();
                        } else {
                            setModel(new DefaultComboBoxModel<>(books.toArray(new Book[0])), null);
                            comboBox.showPopup();
                        }
                    }
                });
            }
        });
    }

    private void setModel(DefaultComboBoxModel<Book> model, Book selectedBook) {
        comboBox.setModel(model);
        if (selectedBook != null) {
            comboBox.setSelectedItem(selectedBook);
        } else {
            comboBox.setSelectedIndex(-1);
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value instanceof Book) {
            comboBox.setSelectedItem(value);
        }
        return comboBox;
    }

    @Override
    public Object getCellEditorValue() {
        return comboBox.getSelectedItem();
    }

    
    private static class BookComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Book) {
                Book book = (Book) value;
                setText(book.title); 
            }
            return this;
        }
    }
}

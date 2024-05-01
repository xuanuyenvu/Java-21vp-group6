package com.group06.bsms.members;

import java.awt.Component;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.text.ParseException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;


class DateCellRenderer extends DefaultTableCellRenderer {
   
    private static final SimpleDateFormat sdfTarget = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof java.sql.Date) {
            value = sdfTarget.format((java.sql.Date) value);
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
};

public class MemberCRUDTableModel extends AbstractTableModel {

    private List<Member> members = new ArrayList<>();
    private String[] columns = { "Name", "Email", "Phone", "Address", "Date of birth", "Gender" };
    private final MemberService memberService;
    public boolean editable = false;

    public MemberCRUDTableModel(MemberService memberService) {
        this.memberService = memberService;
        
    }

    @Override
    public int getRowCount() {
        return members.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    /**
     * @param row
     * @param col
     * @return value at [row, col] in the table
     */
    @Override
    public Object getValueAt(int row, int col) {
        if (row >= members.size()) {
            return null;
        }
        Member member = members.get(row);
        switch (col) {
            case 0:
                return ((member.name == null) ? "" : member.name);
            case 1:
                return ((member.email == null) ? "" : member.email);
            case 2:
                return ((member.phone == null) ? "" : member.phone);
            case 3:
                return ((member.address == null) ? "" : member.address);
            case 4:
                return ((member.dateOfBirth == null) ? "" : member.dateOfBirth);
            case 5:
                return ((member.gender == null) ? "" : member.gender);
           
            default:
                return null;
        }
    }

    /**
     * @param val
     * @param row
     * @param col
     * @return value at [row, col] in the table
     */

    @Override
    public void setValueAt(Object val, int row, int col) {
        if (col == 5) {
            return;
        }

        if (!editable) {
            editable = true;
        }

        Member member = members.get(row);
        try {
            switch (col) {
                case 2:
                    if (!member.phone.equals((String) val)) {
                        memberService.updateMemberAttributeById(member.id, "phone", (String) val);
                        member.phone = (String) val;

                    }
                    break;
                case 0:
                    if (!((String) val).equals(member.name)) {
                        memberService.updateMemberAttributeById(
                                member.id,
                                "name",
                                ((String) val).equals("") ? null : (String) val);
                        member.name = (String) val;

                    }
                    break;
                case 1:
                    if (!((String) val).equals(member.email)) {
                        memberService.updateMemberAttributeById(
                                member.id,
                                "email",
                                ((String) val).equals("") ? null : (String) val);
                        member.email = (String) val;

                    }
                    break;
                case 3:
                    if (!((String) val).equals(member.address)) {
                        memberService.updateMemberAttributeById(
                                member.id,
                                "address",
                                ((String) val).equals("") ? null : (String) val);
                        member.address = (String) val;

                    }
                    break;
                case 4:
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    var dob = new java.sql.Date(sdf.parse((String) val).getTime());
                    if (!(dob == member.dateOfBirth)) {
                        memberService.updateMemberAttributeById(
                                member.id,
                                "dateOfBirth",
                                dob);
                        member.address = (String) val;

                    }
                    break;

                default:
                    break;
            }

        } catch (ParseException e) {
            JOptionPane.showMessageDialog(null, "Invalid date format", "BSMS Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            if (ex.getMessage().contains("member_phone_check")) {
                JOptionPane.showMessageDialog(null, "Invalid phone format", "BSMS Error", JOptionPane.ERROR_MESSAGE);
            } else if (ex.getMessage().contains("member_phone_key")) {
                JOptionPane.showMessageDialog(null, "An member with this phone already exists", "BSMS Error",
                        JOptionPane.ERROR_MESSAGE);
            } else if (ex.getMessage().contains("member_name_key")) {
                JOptionPane.showMessageDialog(null, "A member with this name already exists", "BSMS Error",
                        JOptionPane.ERROR_MESSAGE);
            } else if (ex.getMessage().contains("member_name_check")) {
                JOptionPane.showMessageDialog(null, "Name cannot be empty", "BSMS Error", JOptionPane.ERROR_MESSAGE);
            } else if (ex.getMessage().contains("member_email_check")) {
                JOptionPane.showMessageDialog(null, "Invalid email format", "BSMS Error", JOptionPane.ERROR_MESSAGE);
            } else if (ex.getMessage().contains("member_email_key")) {
                JOptionPane.showMessageDialog(null, "A member with this email already exists", "BSMS Error",
                        JOptionPane.ERROR_MESSAGE);
            } else if (ex.getMessage().contains("member_address_check")) {
                JOptionPane.showMessageDialog(null, "Address cannot be empty", "BSMS Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "BSMS Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        fireTableCellUpdated(row, col);
    }

    public Member getMember(int row) {
        return members.get(row);
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    public boolean contains(int id) {
        Optional<Member> foundMember = members.stream()
                .filter(member -> member.id == id)
                .findFirst();
        return foundMember.isPresent();
    }

    @Override
    public Class<?> getColumnClass(int col) {
        switch (col) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            case 3:
                return String.class;
            case 4:
                return Date.class;
            case 5:
                return String.class;
            
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex >= 0 && columnIndex < 5);
    }

    public void reloadAllMembers(List<Member> newMembers) {
        if (newMembers != null) {
            members.clear();
            fireTableDataChanged();
            for (var member : newMembers) {
                if (!contains(member.id)) {
                    addRow(member);
                }
            }
        }
    }

    public void loadNewMembers(List<Member> newMembers) {
        if (newMembers != null) {
            for (var member : newMembers) {
                if (!contains(member.id)) {
                    addRow(member);
                }
            }
        }
    }

    void addRow(Member member) {
        members.add(member);
    }
}

package com.group06.bsms.members;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.table.AbstractTableModel;

public class MemberRevenueTableModel extends AbstractTableModel {

    private List<Member> members = new ArrayList<>();
    private String[] columns = {"Name", "Email", "Phone", "Address", "Date of birth", "Gender", "Revenue"};
    private final MemberService memberService;

    public MemberRevenueTableModel(MemberService memberService) {
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
            case 6:
                return ((member.revenue == null) ? "" : member.revenue);
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
            case 6:
                return Double.class;
            default:
                return null;
        }
    }

//    @Override
//    public boolean isCellEditable(int rowIndex, int columnIndex) {
//        return (columnIndex == 0 || columnIndex == 4 || columnIndex == 5);
//    }
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

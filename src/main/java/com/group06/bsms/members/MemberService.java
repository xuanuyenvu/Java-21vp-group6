package com.group06.bsms.members;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;
import java.time.LocalDate;

public class MemberService {

    private final MemberDAO memberDAO;

    public MemberService(MemberDAO memberDAO) {
        this.memberDAO = memberDAO;
    }

    List<Member> getTop10MembersWithHighestRevenue(Map<Integer, SortOrder> sortAttributeAndOrder,
            Date startDate, Date endDate) throws Exception {
        List<Member> members = memberDAO.selectTop10MembersWithHighestRevenue(sortAttributeAndOrder, startDate,
                endDate);
        return members;
    }

    public List<Member> selectAllMembers() {
        try {
            List<Member> members = memberDAO.selectAllMembers();
            return members;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public Member selectMember(int id) throws Exception {
        try {
            return memberDAO.selectMember(id);
        } catch (Exception e) {
            throw e;
        }
    }

    public Member selectMemberByPhone(String memberPhone) throws Exception {
        try {
            return memberDAO.selectMemberByPhone(memberPhone);
        } catch (Exception e) {
            throw e;
        }
    }

    public Member getMember(int id) throws Exception {
        try {
            Member Member = memberDAO.selectMember(id);
            if (Member == null) {
                throw new Exception("Cannot find Member with id = " + id);
            }
            return Member;
        } catch (Exception e) {
            throw e;
        }
    }

    public void updateMemberAttributeById(int memberId, String attr, Object value) throws Exception {
        memberDAO.updateMemberAttributeById(memberId, attr, value);
    }

    public void updateMember(Member updatedMember) throws Exception {
        try {
            if (updatedMember.phone == null || updatedMember.phone.equals("")) {
                throw new Exception("Phone cannot be empty");
            }
            if (updatedMember.dateOfBirth == null) {
                throw new Exception("Date of Birth cannot be empty");
            }
            if (updatedMember.name == null || updatedMember.name.equals("")) {
                throw new Exception("Name cannot be empty");
            }
            if (updatedMember.gender == null || updatedMember.gender.equals("")) {
                throw new Exception("Gender cannot be empty");
            }
            if ("".equals(updatedMember.email)) {
                updatedMember.email = null;
            }
            if ("".equals(updatedMember.address)) {
                updatedMember.address = null;
            }

            memberDAO.updateMember(updatedMember);
        } catch (Exception e) {
            throw e;
        }
    }

    public void insertMember(Member member) throws Exception {
        if (member.phone == null || member.phone.equals("")) {
            throw new Exception("Phone cannot be empty");
        }
        if (member.dateOfBirth == null) {
            throw new Exception("Date of Birth cannot be empty");
        }
        if(member.dateOfBirth.equals(Date.valueOf(LocalDate.now()))){
            throw new Exception("your date of birth must be before today");
        }
        if (member.name == null || member.name.equals("")) {
            throw new Exception("Name cannot be empty");
        }
        if (member.gender == null || member.gender.equals("")) {
            throw new Exception("Gender cannot be empty");
        }
        if ("".equals(member.email)) {
            member.email = null;
        }
        if ("".equals(member.address)) {
            member.address = null;
        }

        memberDAO.insertMember(member);
    }

    public List<Member> searchSortFilterMembers(
            int offset, int limit, Map<Integer, SortOrder> sortValue,
            String searchString, String searchChoice) throws Exception {

        List<Member> members = memberDAO.selectSearchSortFilterMembers(
                offset, limit, sortValue, searchString, searchChoice);

        return members;
    }
}

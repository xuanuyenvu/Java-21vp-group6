package com.group06.bsms.members;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;


public interface MemberDAO {

        List<Member> selectTop10MembersWithHighestRevenue(Map<Integer, SortOrder> sortAttributeAndOrder,
                        Date startDate, Date endDate) throws Exception;

        List<Member> selectAllMembers() throws Exception;

        public Member selectMember(int id) throws Exception;

        public Member selectMemberByPhone(String memberPhone) throws Exception;

        void insertMember(Member member) throws Exception;

        void updateMember(Member member) throws Exception;

        void updateMemberAttributeById(int memberId, String attr, Object value) throws Exception;

        public List<Member> selectSearchSortFilterMembers(
                        int offset, int limit, Map<Integer, SortOrder> sortValue,
                        String searchString, String searchChoice) throws Exception;

}

package com.group06.bsms.members;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;

public class MemberService {

    private final MemberDAO memberDAO;

    public MemberService(MemberDAO memberDAO) {
        this.memberDAO = memberDAO;
    }

    List<Member> getTop10MembersWithHighestRevenue(Map<Integer, SortOrder> sortAttributeAndOrder,
            Date startDate, Date endDate) throws Exception {
        List<Member> members = memberDAO.selectTop10MembersWithHighestRevenue(sortAttributeAndOrder, startDate, endDate);
        return members;
    }
}

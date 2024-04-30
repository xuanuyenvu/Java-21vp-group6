package com.group06.bsms.members;

import com.group06.bsms.Repository;
import com.group06.bsms.accounts.Account;
import com.group06.bsms.accounts.AccountWithPassword;
import com.group06.bsms.auth.Hasher;
import com.group06.bsms.revenues.Revenue;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;

public class MemberRepository extends Repository<Member> implements MemberDAO {

    public MemberRepository(Connection db) {
        super(db, Member.class);

    }

    @Override
    public List<Member> selectTop10MembersWithHighestRevenue(Map<Integer, SortOrder> sortAttributeAndOrder,
            Date startDate, Date endDate) throws Exception {
        List<Member> result = new ArrayList<>();
        try {
            db.setAutoCommit(false);
            String stringQuery = """
                    SELECT top_10.*
                    FROM
                        (SELECT Member.*,
                         COALESCE(SUM(OrderedBook.pricePerbook * OrderedBook.quantity), 0) AS revenue,
                    	 COALESCE(SUM(OrderedBook.quantity), 0) AS saleQuantity
                         FROM Member
                         JOIN OrderSheet ON Member.id = OrderSheet.memberId
                         JOIN OrderedBook ON OrderedBook.orderSheetId = OrderSheet.id
                         WHERE orderDate BETWEEN ? AND ?
                         GROUP BY Member.id
                         ORDER BY revenue DESC
                         LIMIT 10) AS top_10
                    """;

            for (Map.Entry<Integer, SortOrder> entry : sortAttributeAndOrder.entrySet()) {
                Integer attribute = entry.getKey();
                SortOrder sortOrder = entry.getValue();

                var sortAttributes = new ArrayList<String>(List.of(
                        " ORDER BY name ",
                        " ORDER BY email ",
                        " ORDER BY phone ",
                        " ORDER BY address ",
                        " ORDER BY dateOfBirth ",
                        " ORDER BY gender ",
                        " ORDER BY saleQuantity ",
                        " ORDER BY revenue "));

                var sortOrders = new HashMap<SortOrder, String>();
                sortOrders.put(SortOrder.ASCENDING, " ASC ");
                sortOrders.put(SortOrder.DESCENDING, " DESC ");

                stringQuery += sortAttributes.get(attribute);
                stringQuery += sortOrders.get(sortOrder);
            }
            try (PreparedStatement preparedStatement = db.prepareStatement(stringQuery)) {

                preparedStatement.setDate(1, startDate);
                preparedStatement.setDate(2, endDate);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        var member = populate(resultSet);
                        member.revenue = new Revenue(
                                resultSet.getDouble("revenue"),
                                resultSet.getInt("saleQuantity"));
                        result.add(member);
                    }
                }
                db.commit();
            }
            return result;
        } catch (Exception e) {
            db.rollback();
            if (e.getMessage().equals("Entity not found")) {
                throw new Exception("Member not found");
            }
            throw e;
        }
    }

    public List<Member> selectAllMembers() throws Exception {
        try {
            var members = selectAll(
                    null,
                    0, null,
                    "name", Sort.ASC,
                    "name", "id", "email",
                    "address", "dateOfBirth", "phone",
                    "gender");

            return members;
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    public Member selectMember(int id) throws Exception {
        try {
            Member member = selectById(id);
            if (member == null) {
                throw new Exception("Member not found");
            }

            return member;
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    public Member selectMemberByPhone(String memberPhone) throws Exception {
        Member member = new Member();
        try {
            db.setAutoCommit(false);
            try (var selectAccountQuery = db.prepareStatement(
                    "SELECT * FROM Member WHERE phone = ?")) {
                selectAccountQuery.setString(1, memberPhone);

                var result = selectAccountQuery.executeQuery();
                while (result.next()) {
                    member = populate(result);
                }

                db.commit();
            }
            return member;
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    public void insertMember(Member member) throws Exception {
        try {
            db.setAutoCommit(false);

            try (var query = db.prepareStatement(
                    """
                                insert into Member(
                                    phone, name, gender, dateOfBirth, email, address
                                ) values (
                                    ?, ?, ?, ?, ?, ?
                                )
                            """)) {
                int index = 1;
                query.setString(index++, member.phone);
                query.setString(index++, member.name);
                query.setString(index++, member.gender);
                query.setDate(index++, new java.sql.Date(member.dateOfBirth.getTime()));
                query.setString(index++, member.email);
                query.setString(index++, member.address);

                var result = query.executeUpdate();

                db.commit();

                if (result == 0) {
                    throw new Exception("Internal database error");
                }
            }
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    public void updateMemberAttributeById(int memberId, String attr, Object value) throws Exception {
        try {
            updateById(memberId, attr, value);
        } catch (Exception e) {
            db.rollback();

            if (e.getMessage().equals("Entity not found")) {
                throw new Exception("Account not found");
            }

            throw e;
        }
    }

    public void updateMember(Member updatedMember) throws Exception {
        try {
            this.update(
                    updatedMember,
                    "name", "email",
                    "address", "dateOfBirth", "phone",
                    "gender");

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    public List<Member> selectSearchSortFilterMembers(
            int offset, int limit, Map<Integer, SortOrder> sortValue,
            String searchString, String searchChoice) throws Exception {
        List<Member> result = new ArrayList<>();

        try {
            db.setAutoCommit(false);

            String stringQuery = "SELECT * FROM Member";

            stringQuery += " WHERE " + searchChoice
                    + (searchChoice.trim().equals("Member.dateOfBirth") ? " = ? " : " LIKE ?");

            for (Map.Entry<Integer, SortOrder> entry : sortValue.entrySet()) {
                Integer key = entry.getKey();
                SortOrder value = entry.getValue();

                var sortKeys = new ArrayList<String>(List.of(
                        " ORDER BY Member.phone ",
                        " ORDER BY Member.name ",
                        " ORDER BY Member.gender ",
                        " ORDER BY Member.email ",
                        " ORDER BY Member.address ",
                        " ORDER BY Member.dateOfBirht "));

                var sortValues = new HashMap<SortOrder, String>();
                sortValues.put(SortOrder.ASCENDING, " ASC ");
                sortValues.put(SortOrder.DESCENDING, " DESC ");

                stringQuery += sortKeys.get(key);
                stringQuery += sortValues.get(value);
            }

            stringQuery += " OFFSET ? LIMIT ? ";

            try (PreparedStatement preparedStatement = db.prepareStatement(stringQuery)) {
                int parameterIndex = 1;
                preparedStatement.setString(parameterIndex++, "%" + searchString + "%");

                preparedStatement.setInt(parameterIndex++, offset);
                preparedStatement.setInt(parameterIndex++, limit);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        result.add(populate(resultSet));
                    }
                }
            }

            db.commit();

            return result;
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }
}

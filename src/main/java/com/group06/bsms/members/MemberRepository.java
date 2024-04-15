package com.group06.bsms.members;

import com.group06.bsms.Repository;
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
                                resultSet.getInt("saleQuantity")
                        );
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
}

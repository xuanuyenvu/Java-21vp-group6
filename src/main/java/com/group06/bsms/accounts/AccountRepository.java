package com.group06.bsms.accounts;

import com.group06.bsms.Repository;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;

public class AccountRepository extends Repository<Account> implements AccountDAO {

    public AccountRepository(Connection db) {
        super(db, Account.class);

    }

    @Override
    public List<Account> selectTop10EmployeesWithHighestRevenue(Map<Integer, SortOrder> sortAttributeAndOrder,
            Date startDate, Date endDate) throws Exception {
        List<Account> result = new ArrayList<>();
        try {
            db.setAutoCommit(false);
            String stringQuery = "SELECT top_10.*\n"
                    + "FROM\n"
                    + "    (SELECT Account.*,\n"
                    + "     COALESCE(SUM(OrderedBook.pricePerbook * OrderedBook.quantity), 0) AS revenue\n"
                    + "     FROM Account\n"
                    + "     JOIN OrderSheet ON Account.id = OrderSheet.employeeInchargeId\n"
                    + "     JOIN OrderedBook ON OrderedBook.orderSheetId = OrderSheet.id\n"
                    + "     WHERE orderDate BETWEEN ? AND ?\n"
                    + "     GROUP BY Account.id\n"
                    + "     ORDER BY revenue DESC\n"
                    + "     LIMIT 10) AS top_10\n";

            for (Map.Entry<Integer, SortOrder> entry : sortAttributeAndOrder.entrySet()) {
                Integer attribute = entry.getKey();
                SortOrder sortOrder = entry.getValue();

                var sortAttributes = new ArrayList<String>(List.of(
                        " ORDER BY name ",
                        " ORDER BY email ",
                        " ORDER BY phone ",
                        " ORDER BY address ",
                        " ORDER BY gender ",
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
                        result.add(populate(resultSet));
                    }
                }
                db.commit();
            }
            return result;
        } catch (Exception e) {
            db.rollback();
            if (e.getMessage().equals("Entity not found")) {
                throw new Exception("Employee not found");
            }
            throw e;
        }
    }
}

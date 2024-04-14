package com.group06.bsms.categories;

import java.sql.Connection;

import com.group06.bsms.Repository;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;

public class CategoryRepository extends Repository<Category> implements CategoryDAO {

    public CategoryRepository(Connection db) {
        super(db, Category.class);
    }

    @Override
    public List<Category> selectAllCategories() throws Exception {
        try {
            db.setAutoCommit(false);

            var categories = selectAll(
                    null,
                    0, null,
                    "name", Sort.ASC,
                    "name", "id", "isHidden"
            );

            db.commit();

            return categories;

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public List<Category> selectTop10CategoriesWithHighestRevenue(Map<Integer, SortOrder> sortAttributeAndOrder,
            Date startDate, Date endDate) throws Exception {
        List<Category> result = new ArrayList<>();
        try {
            db.setAutoCommit(false);
            String stringQuery = "SELECT top_10.*\n"
                    + "	FROM\n"
                    + "	(SELECT Category.*,\n"
                    + "		   COALESCE(SUM(OrderedBook.pricePerbook * OrderedBook.quantity), 0) AS revenue\n"
                    + "FROM Category\n"
                    + "JOIN BookCategory ON BookCategory.CategoryId = Category.id\n"
                    + "JOIN Book ON Book.id = BookCategory.bookId\n"
                    + "LEFT JOIN OrderedBook ON OrderedBook.bookId = Book.id\n"
                    + "LEFT JOIN OrderSheet ON OrderedBook.orderSheetId = OrderSheet.id\n"
                    + "WHERE orderDate BETWEEN ? AND ?\n"
                    + "GROUP BY Category.id\n"
                    + "ORDER BY revenue DESC\n"
                    + "LIMIT 10\n"
                    + " ) AS top_10";

            for (Map.Entry<Integer, SortOrder> entry : sortAttributeAndOrder.entrySet()) {
                Integer attribute = entry.getKey();
                SortOrder sortOrder = entry.getValue();

                var sortAttributes = new ArrayList<String>(List.of(
                        " ORDER BY name ",
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
                throw new Exception("Category not found");
            }
            throw e;
        }
    }
}

package com.group06.bsms.categories;

import java.sql.Connection;

import com.group06.bsms.Repository;
import com.group06.bsms.revenues.Revenue;

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
                    "name", "id", "isHidden");

            db.commit();

            return categories;

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public Category selectCategory(int id) throws Exception {
        try {
            Category category = selectById(id);
            if (category == null) {
                throw new Exception("Entity not found");
            }
            return category;
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void updateCategoryAttributeById(int categoryId, String attr, Object value) throws Exception {
        try {
            updateById(categoryId, attr, value);
        } catch (Exception e) {
            db.rollback();

            if (e.getMessage().equals("Entity not found")) {
                throw new Exception("Category not found");
            }

            throw e;
        }
    }

    @Override
    public void showCategory(int id) throws Exception {
        try {
            var category = this.selectById(id);

            if (category == null || category.isHidden == false) {
                return;
            }

            db.setAutoCommit(false);

            try (PreparedStatement preparedStatement = db.prepareStatement(""
                    + "update book set hiddenParentCount = hiddenParentCount - 1 "
                    + "where exists( "
                    + " select * from bookCategory "
                    + " where bookId = id and categoryId = ? "
                    + ")")) {
                preparedStatement.setInt(1, id);

                preparedStatement.executeUpdate();
            }

            db.commit();

            updateById(id, "isHidden", false);
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void hideCategory(int id) throws Exception {
        try {
            var category = this.selectById(id);

            if (category == null || category.isHidden == true) {
                return;
            }

            db.setAutoCommit(false);

            try (PreparedStatement preparedStatement = db.prepareStatement(""
                    + "update book set hiddenParentCount = hiddenParentCount + 1 "
                    + "where exists( "
                    + " select * from bookCategory "
                    + " where bookId = id and categoryId = ?"
                    + ")")) {
                preparedStatement.setInt(1, id);

                preparedStatement.executeUpdate();
            }

            db.commit();

            updateById(id, "isHidden", true);
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void updateCategory(Category category, Category updatedCategory) throws Exception {
        try {
            update(updatedCategory, "name");
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void insertCategory(Category category) throws Exception {
        try {
            this.insert(category, "name", "isHidden");
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public List<Category> selectSearchSortFilterCategories(
            int offset, int limit, Map<Integer, SortOrder> sortValue,
            String searchString) throws Exception {
        List<Category> result = new ArrayList<>();

        try {
            db.setAutoCommit(false);

            String stringQuery = "SELECT * FROM Category";

            stringQuery += " WHERE LOWER(name) LIKE LOWER(?) ";

            for (Map.Entry<Integer, SortOrder> entry : sortValue.entrySet()) {
                Integer key = entry.getKey();
                SortOrder value = entry.getValue();

                var sortKeys = new ArrayList<String>(List.of(
                        " ORDER BY Category.name "));

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

    @Override
    public List<Category> selectTop10CategoriesWithHighestRevenue(Map<Integer, SortOrder> sortAttributeAndOrder,
            Date startDate, Date endDate) throws Exception {
        List<Category> result = new ArrayList<>();
        try {
            db.setAutoCommit(false);
            String stringQuery = """
                                 SELECT top_10.*
                                 FROM
                                 	(SELECT Category.*,
                                      COALESCE(SUM(OrderedBook.pricePerbook * OrderedBook.quantity), 0) AS revenue,
                                 	 COALESCE(SUM(OrderedBook.quantity), 0) AS saleQuantity
                                      FROM Category
                                      JOIN BookCategory ON BookCategory.CategoryId = Category.id
                                      JOIN Book ON Book.id = BookCategory.bookId
                                      LEFT JOIN OrderedBook ON OrderedBook.bookId = Book.id
                                      LEFT JOIN OrderSheet ON OrderedBook.orderSheetId = OrderSheet.id
                                      WHERE orderDate BETWEEN ? AND ?
                                      GROUP BY Category.id
                                      ORDER BY revenue DESC
                                      LIMIT 10) AS top_10 
                                 """;

            for (Map.Entry<Integer, SortOrder> entry : sortAttributeAndOrder.entrySet()) {
                Integer attribute = entry.getKey();
                SortOrder sortOrder = entry.getValue();

                var sortAttributes = new ArrayList<String>(List.of(
                        " ORDER BY name ",
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
                        var category = populate(resultSet);
                        category.revenue = new Revenue(
                                resultSet.getDouble("revenue"),
                                resultSet.getInt("saleQuantity")
                        );
                        result.add(category);
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

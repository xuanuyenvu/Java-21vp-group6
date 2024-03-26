package com.group06.bsms.categories;

import java.sql.Connection;

import com.group06.bsms.Repository;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository extends Repository<Category> implements CategoryDAO {

    public CategoryRepository(Connection db) {
        super(db, Category.class);
    }

    @Override
    public List<Category> selectAllCategoryNames() throws Exception {
        try {
            db.setAutoCommit(false);

            var categories = selectAll(
                    null,
                    0, null,
                    "name", Sort.ASC,
                    "id", "name","isHidden"
            );

            db.commit();

            return categories;

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    public List<Category> selectByName(List<String> categoriesName) throws Exception {
        try {
            db.setAutoCommit(false);

            List<Category> categories = new ArrayList<>();

            for (String categoryName : categoriesName) {
                var selectCategoriesQuery = db.prepareStatement(
                        "SELECT id, name, isHidden FROM Category WHERE name = ?");
                selectCategoriesQuery.setString(1, categoryName);
                var result = selectCategoriesQuery.executeQuery();

                while (result.next()) {
                    categories.add(new Category(
                            result.getInt("id"),
                            result.getString("name"),
                            result.getBoolean("isHidden")));
                }
            }

            db.commit();

            return categories;
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

}

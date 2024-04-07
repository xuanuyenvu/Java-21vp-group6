package com.group06.bsms.categories;

import java.sql.Connection;

import com.group06.bsms.Repository;
import java.util.List;

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
}

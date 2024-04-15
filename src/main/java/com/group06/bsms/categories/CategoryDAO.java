package com.group06.bsms.categories;

import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;

public interface CategoryDAO {

    List<Category> selectAllCategories() throws Exception;

    Category selectCategory(int id) throws Exception;

    void showCategory(int id) throws Exception;

    void hideCategory(int id) throws Exception;

    void updateCategory(Category category, Category updatedCategory) throws Exception;

    void insertCategory(Category category) throws Exception;

    void updateCategoryAttributeById(int categoryId, String attr, Object value) throws Exception;

    public List<Category> selectSearchSortFilterCategories(
            int offset, int limit, Map<Integer, SortOrder> sortValue,
            String searchString
    ) throws Exception;
}

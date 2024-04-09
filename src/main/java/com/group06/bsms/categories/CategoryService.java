package com.group06.bsms.categories;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;

public class CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public Category getCategory(int id) throws Exception {
        try {
            Category category = categoryDAO.selectCategory(id);
            if (category == null) {
                throw new Exception("Cannot find category with id = " + id);
            }
            return category;
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Category> selectAllCategories() {
        try {
            List<Category> categories = categoryDAO.selectAllCategories();
            return categories;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void updateCategoryAttributeById(int categoryId, String attr, Object value) throws Exception {
        categoryDAO.updateCategoryAttributeById(categoryId, attr, value);
    }

    public void updateCategory(Category category, Category updatedCategory) throws Exception {
        try {
            if (updatedCategory.name == null || updatedCategory.name.equals("")) {
                throw new Exception("Title cannot be empty");
            }

            categoryDAO.updateCategory(category, updatedCategory);
        } catch (Exception e) {
            throw e;
        }
    }

    public void insertCategory(String name, boolean hideChecked) throws Exception {
        if (name == null || name.equals("")) {
            throw new Exception("Name cannot be empty");
        }

        Category category = new Category();

        category.name = name;
        category.isHidden = hideChecked;

        categoryDAO.insertCategory(category);
    }

    public void hideCategory(int id) throws Exception {
        categoryDAO.hideCategory(id);
    }

    public void showCategory(int id) throws Exception {
        categoryDAO.showCategory(id);
    }

    public List<Category> searchSortFilterCategories(
            int offset, int limit, Map<Integer, SortOrder> sortValue,
            String searchString
    ) throws Exception {

        List<Category> categories = categoryDAO.selectSearchSortFilterCategories(
                offset, limit, sortValue, searchString
        );

        return categories;
    }
}

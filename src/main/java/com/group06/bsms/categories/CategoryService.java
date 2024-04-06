package com.group06.bsms.categories;

import java.util.ArrayList;
import java.util.List;

public class CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public List<Category> selectAllCategories() {
        try {
            List<Category> categories = categoryDAO.selectAllCategories();
            return categories;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}

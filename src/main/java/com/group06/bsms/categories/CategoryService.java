package com.group06.bsms.categories;

import java.util.ArrayList;
import java.util.List;

public class CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public List<Category> getAllCategories() {
        try {
            List<Category> categories = categoryDAO.selectAllCategories();
            return categories;
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<Category>();
        }
    }

    public List<Category> selectByName(List<String> categoriesName) throws Exception {
        try {
            return categoryDAO.selectByName(categoriesName);
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<Category>();
        }
    }
}

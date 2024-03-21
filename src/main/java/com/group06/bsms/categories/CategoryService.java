package com.group06.bsms.categories;

import java.util.ArrayList;
import java.util.List;

public class CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public List<Category> selectAllCategoryNames() {
        try {
            List<Category> categories = categoryDAO.selectAllCategoryNames();
            return categories;
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public List<Category> selectByName(List<String> categoriesName) throws Exception {
        try {
            if (categoriesName.isEmpty()) {
                return new ArrayList<>();
            } else {
                return categoryDAO.selectByName(categoriesName);
            }
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }
}

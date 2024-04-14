package com.group06.bsms.categories;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;

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

    List<Category> getTop10CategoriesWithHighestRevenue(Map<Integer, SortOrder> sortAttributeAndOrder,
            Date startDate, Date endDate) throws Exception {
        List<Category> books = categoryDAO.selectTop10CategoriesWithHighestRevenue(sortAttributeAndOrder, startDate, endDate);
        return books;
    }
}

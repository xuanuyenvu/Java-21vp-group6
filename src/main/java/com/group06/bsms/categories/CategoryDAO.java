package com.group06.bsms.categories;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;

public interface CategoryDAO {

    List<Category> selectAllCategories() throws Exception;

    List<Category> selectTop10CategoriesWithHighestRevenue(Map<Integer, SortOrder> sortAttributeAndOrder,
            Date startDate, Date endDate) throws Exception;
}

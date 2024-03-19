package com.group06.bsms.categories;

import java.util.List;

public interface CategoryDAO {
    
    List<Category> selectAllCategories() throws Exception;
    public List<Category> selectByName(List<String> categoriesName) throws Exception;
}

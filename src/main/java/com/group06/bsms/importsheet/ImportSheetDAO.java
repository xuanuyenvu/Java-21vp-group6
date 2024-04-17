package com.group06.bsms.importsheet;

import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;

public interface ImportSheetDAO {

    void insertImportSheet(ImportSheet importSheet) throws Exception;

    ImportSheet selectImportSheet(int id) throws Exception;

    public List<ImportSheet> selectSearchSortFilterImportSheets(
            int offset, int limit, Map<Integer, SortOrder> sortValue,
            String searchString, String searchChoice
    ) throws Exception;

}

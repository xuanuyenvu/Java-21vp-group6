package com.group06.bsms.order;

import java.sql.Date;
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

    List<ImportSheet> selectTop10ImportSheetsWithHighestRevenue(Map<Integer, SortOrder> sortAttributeAndOrder,
                        Date startDate, Date endDate) throws Exception;
}

package com.group06.bsms.order;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;

public class ImportSheetService {

    private final ImportSheetDAO importSheetDAO;

    public ImportSheetService(ImportSheetDAO importSheetDAO) {
        this.importSheetDAO = importSheetDAO;
    }

    public void insertImportSheet(ImportSheet importSheet) throws Exception {
        try {
            importSheetDAO.insertImportSheet(importSheet);
        } catch (Exception e) {
            throw e;
        }
    }

    public ImportSheet selectImportSheet(int id) throws Exception {
        try {
            return importSheetDAO.selectImportSheet(id);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<ImportSheet> searchSortFilterImportSheets(
            int offset, int limit, Map<Integer, SortOrder> sortValue,
            String searchString, String searchChoice
    ) throws Exception {

        List<ImportSheet> importSheets = importSheetDAO.selectSearchSortFilterImportSheets(
                offset, limit, sortValue, searchString, searchChoice
        );

        return importSheets;
    }
    
        List<ImportSheet> getTop10ImportSheetsWithHighestRevenue(Map<Integer, SortOrder> sortAttributeAndOrder,
            Date startDate, Date endDate) throws Exception {
        return importSheetDAO.selectTop10ImportSheetsWithHighestRevenue(sortAttributeAndOrder, startDate, endDate);
        
    }

}

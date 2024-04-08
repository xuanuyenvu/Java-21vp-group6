package com.group06.bsms.importsheet;

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

}

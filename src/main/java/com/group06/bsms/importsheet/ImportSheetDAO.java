package com.group06.bsms.importsheet;

import java.util.List;

public interface ImportSheetDAO {

    void insertImportSheet(ImportSheet importSheet) throws Exception;

    ImportSheet selectImportSheet(int id) throws Exception;

}

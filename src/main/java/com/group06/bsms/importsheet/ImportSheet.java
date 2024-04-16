package com.group06.bsms.importsheet;

import java.sql.Date;
import java.util.List;

public class ImportSheet {
    public int id;
    public int employeeInChargeId;
    public Date importDate;
    public Double totalCost;
    public List<ImportedBook> importedBooks;


    public ImportSheet() {
       
    }

    public ImportSheet(int employeeInChargeId, Date importDate, Double totalCost, List<ImportedBook> importedBooks) {
        
        this.employeeInChargeId = employeeInChargeId;
        this.importDate = importDate;
        this.totalCost = totalCost;
        this.importedBooks = importedBooks;
    }


    @Override
    public String toString() {
        return "ImportSheet {id=" + id + ", employeeInChargeId=" + employeeInChargeId + ", importDate=" + importDate
                + ", totalCost=" + totalCost + ", importBooks=" + importedBooks + "}";
    }


}

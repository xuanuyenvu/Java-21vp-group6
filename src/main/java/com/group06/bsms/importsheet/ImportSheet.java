package com.group06.bsms.importsheet;

import java.sql.Date;
import java.util.List;

import com.group06.bsms.accounts.Account;

public class ImportSheet {

    public int id;
    public int employeeInChargeId;
    public Account employee;
    public Date importDate;
    public Double totalCost;
    public List<ImportedBook> importedBooks;

    public ImportSheet() {

    }

    public ImportSheet(int id, int employeeInChargeId, Date importDate, Double totalCost, List<ImportedBook> importedBooks) {
        this.id = id;
        this.employeeInChargeId = employeeInChargeId;
        this.importDate = importDate;
        this.totalCost = totalCost;
        this.importedBooks = importedBooks;
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

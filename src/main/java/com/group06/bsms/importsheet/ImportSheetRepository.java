package com.group06.bsms.importsheet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import com.group06.bsms.Repository;
import com.group06.bsms.accounts.AccountRepository;
import com.group06.bsms.books.BookRepository;
import com.group06.bsms.books.Book;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.swing.SortOrder;

public class ImportSheetRepository extends Repository<ImportSheet> implements ImportSheetDAO {

    private final BookRepository bookRepository;
    private final AccountRepository accountRepository;

    public ImportSheetRepository(Connection db, BookRepository bookRepository, AccountRepository accountRepository) {
        super(db, ImportSheet.class);
        this.bookRepository = bookRepository;
        this.accountRepository = accountRepository;

    }

    @Override
    public void insertImportSheet(ImportSheet importSheet) throws Exception {
        try {
            db.setAutoCommit(false);
            if (importSheet == null) {
                throw new NullPointerException("The parameer cannot be null");
            }
            if (importSheet.importedBooks.isEmpty()) {
                throw new Exception("The imported books is empty");
            }
            if (importSheet.employeeInChargeId == 0) {
                throw new Exception("The employee id is empty");

            }
            if (importSheet.importDate == null) {
                throw new Exception("The import date is empty");
            }
            if (importSheet.totalCost == null || importSheet.totalCost < 0) {
                throw new Exception("Invalid total cost");
            }

            try (PreparedStatement insertImportSheetQuery = db.prepareStatement(
                    "INSERT INTO ImportSheet (employeeInChargeId, importDate, totalCost) "
                    + "VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {

                insertImportSheetQuery.setInt(1, importSheet.employeeInChargeId);
                insertImportSheetQuery.setDate(2, importSheet.importDate);
                insertImportSheetQuery.setDouble(3, importSheet.totalCost);

                int rowsAffected = insertImportSheetQuery.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Insertion failed, no rows affected.");
                }

                ResultSet generatedKeys = insertImportSheetQuery.getGeneratedKeys();
                if (generatedKeys.next()) {
                    importSheet.id = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Insertion failed, no ID obtained.");
                }

                insertImportedBooksList(importSheet.id, importSheet.importedBooks);

            }
        } catch (Exception e) {
            db.rollback();
            throw e;
        }

    }

    private void insertImportedBooksList(int importSheetId, List<ImportedBook> importedBooks) throws Exception {
        try {
            db.setAutoCommit(false);
            if (importedBooks == null || importedBooks.isEmpty()) {
                throw new NullPointerException("The parameter cannot be null or empty");
            }
            int importedBookResults[] = null;

            String insertQuery = "INSERT INTO ImportedBook (importSheetId, bookId, quantity, pricePerBook) VALUES (?, ?, ?, ?)";
            try (var importedBookStatement = db.prepareStatement(insertQuery)) {
                for (ImportedBook importedBook : importedBooks) {

                    importedBookStatement.setInt(1, importSheetId);
                    importedBookStatement.setInt(2, importedBook.bookId);
                    importedBookStatement.setInt(3, importedBook.quantity);
                    importedBookStatement.setDouble(4, importedBook.pricePerBook);
                    importedBookStatement.addBatch();
                }
                importedBookResults = importedBookStatement.executeBatch();

                db.commit();

                for (int importedBookResult : importedBookResults) {
                    if (importedBookResult == PreparedStatement.EXECUTE_FAILED) {
                        throw new Exception("Cannot insert imported books");
                    }
                }

                for (ImportedBook importedBook : importedBooks) {
                    updateMaxImportPriceAndSalePriceOfBook(importedBook.bookId, importedBook.pricePerBook);
                }
            }
        } catch (Exception e) {
            db.rollback();
            throw e;
        }

    }

    private void updateMaxImportPriceAndSalePriceOfBook(int id, Double maxImportPrice) throws Exception {
        try {

            if (maxImportPrice == null) {
                throw new NullPointerException("The parameter cannot be null or empty");
            }

            Book book = bookRepository.selectById(id);
            if (book == null) {
                throw new NullPointerException("Cannot find book");
            }

            if (book.maxImportPrice == null || book.maxImportPrice < maxImportPrice) {
                book.maxImportPrice = maxImportPrice;
            }
            if (book.salePrice == null || book.salePrice < 1.1 * book.maxImportPrice) {
                book.salePrice = 1.1 * book.maxImportPrice;
            }
            bookRepository.updateBookAttributeById(id, "maxImportPrice", book.salePrice);
            bookRepository.updateBookAttributeById(id, "salePrice", book.salePrice);

        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public ImportSheet selectImportSheet(int id) throws Exception {
        try {
            ImportSheet importSheet = selectById(id);
            if (importSheet == null) {
                throw new Exception("Entity not found");
            }
            db.setAutoCommit(false);
            try (var selectImportedBooksQuery = db.prepareStatement(
                    "SELECT ib.bookId, b.title, ib.quantity, ib.pricePerBook FROM ImportedBook ib JOIN Book b ON ib.bookId = b.id WHERE ib.importSheetId = ?")) {
                selectImportedBooksQuery.setInt(1, id);
                var result = selectImportedBooksQuery.executeQuery();

                if (importSheet.importedBooks == null) {
                    importSheet.importedBooks = new ArrayList<>();
                }

                while (result.next()) {
                    importSheet.importedBooks.add(new ImportedBook(id,
                            result.getInt("bookId"),
                            result.getString("title"),
                            result.getInt("quantity"),
                            result.getDouble("pricePerBook")));
                }

                db.commit();
            }
            importSheet.employee = accountRepository.selectAccount(importSheet.employeeInChargeId);

            return importSheet;

        } catch (Exception e) {

            throw e;

        }
    }

    @Override
    public List<ImportSheet> selectTop10ImportSheetsWithHighestRevenue(Map<Integer, SortOrder> sortAttributeAndOrder,
            java.sql.Date startDate, java.sql.Date endDate) throws Exception {

        List<ImportSheet> result = new ArrayList<>();
        try {
            db.setAutoCommit(false);
            String stringQuery = """
                        SELECT * FROM
                        (SELECT ImportSheet.id, ImportSheet.employeeInChargeId, ImportSheet.totalCost, ImportSheet.importDate, Account.phone
                             FROM ImportSheet JOIN Account ON Account.id = ImportSheet.employeeInChargeId
                             WHERE ImportSheet.importDate BETWEEN ? AND ?
                             ORDER BY ImportSheet.totalCost DESC
                             )
                    AS top_10
                        """;

            if (!sortAttributeAndOrder.isEmpty()) {
                stringQuery += " ORDER BY ";
                for (Map.Entry<Integer, SortOrder> entry : sortAttributeAndOrder.entrySet()) {
                    Integer key = entry.getKey();
                    SortOrder value = entry.getValue();

                    String sortKey;
                    switch (key) {
                        case 0 ->
                            sortKey = "top_10.phone";
                        case 1 ->
                            sortKey = "top_10.importDate";
                        case 2 ->
                            sortKey = "top_10.totalCost";
                        default ->
                            throw new IllegalArgumentException("Invalid sort key: " + key);
                    }

                    stringQuery += sortKey + (value == SortOrder.ASCENDING ? " ASC" : " DESC") + ", ";
                }

                stringQuery = stringQuery.substring(0, stringQuery.length() - 2);
            }
           
            try (PreparedStatement preparedStatement = db.prepareStatement(stringQuery)) {

                preparedStatement.setDate(1, startDate);
                preparedStatement.setDate(2, endDate);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        ImportSheet importSheet = new ImportSheet(resultSet.getInt("id"),
                                resultSet.getInt("employeeInChargeId"),
                                resultSet.getDate("importDate"),
                                resultSet.getDouble("totalCost"), null);
                        importSheet.employee = accountRepository.selectAccount(importSheet.employeeInChargeId);

                        result.add(importSheet);
                    }
                }
                 
                db.commit();

            }
            return result;
        } catch (Exception e) {
            db.rollback();
            if (e.getMessage().equals("Entity not found")) {
                throw new Exception("Book not found");
            }
            throw e;
        }

    }

    @Override
    public List<ImportSheet> selectSearchSortFilterImportSheets(int offset, int limit, Map<Integer, SortOrder> sortValue, String searchString, String searchChoice, java.sql.Date startDate, java.sql.Date endDate) throws Exception {

        List<ImportSheet> result = new ArrayList<>();
        try {
            db.setAutoCommit(false);

            String stringQuery = "SELECT ImportSheet.id, ImportSheet.employeeInChargeId, ImportSheet.totalCost, ImportSheet.importDate, Account.phone FROM ImportSheet JOIN Account ON Account.id = ImportSheet.employeeInChargeId WHERE Importsheet.importDate BETWEEN ? AND ?";

            stringQuery += " AND " + searchChoice
                    + (searchChoice.trim().equals("Account.phone") ? " LIKE ?" : " = ? ");

            if (!sortValue.isEmpty()) {
                stringQuery += " ORDER BY ";
                for (Map.Entry<Integer, SortOrder> entry : sortValue.entrySet()) {
                    Integer key = entry.getKey();
                    SortOrder value = entry.getValue();

                    String sortKey;
                    switch (key) {
                        case 0 ->
                            sortKey = "Account.phone";
                        case 1 ->
                            sortKey = "ImportSheet.importDate";
                        case 2 ->
                            sortKey = "ImportSheet.totalCost";
                        default ->
                            throw new IllegalArgumentException("Invalid sort key: " + key);
                    }

                    stringQuery += sortKey + (value == SortOrder.ASCENDING ? " ASC" : " DESC") + ", ";
                }

                stringQuery = stringQuery.substring(0, stringQuery.length() - 2);
            }

            stringQuery += " OFFSET ? LIMIT ? ";

            try (PreparedStatement preparedStatement = db.prepareStatement(stringQuery)) {
                int parameterIndex = 1;
                preparedStatement.setDate(parameterIndex++, new java.sql.Date(startDate.getTime()));
                preparedStatement.setDate(parameterIndex++, new java.sql.Date(endDate.getTime()));

                if (searchChoice.trim().equals("ImportSheet.importDate")) {
                    if (searchString == null || searchString.isEmpty()) {
                        return result;
                    } else {
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date searchDate = dateFormat.parse(searchString);
                            preparedStatement.setDate(parameterIndex++, new java.sql.Date(searchDate.getTime()));
                        } catch (ParseException e) {
                            return result;
                        }

                    }
                } else if (searchChoice.trim().equals("ImportSheet.totalCost")) {
                    if (searchString == null || searchString.isEmpty()) {
                        return result;
                    } else {
                        try {
                            double searchValue = Double.parseDouble(searchString);
                            preparedStatement.setDouble(parameterIndex++, searchValue);
                        } catch (NumberFormatException e) {
                            return result;
                        }
                    }
                } else {
                    preparedStatement.setString(parameterIndex++, "%" + searchString + "%");
                }

                preparedStatement.setInt(parameterIndex++, offset);

                preparedStatement.setInt(parameterIndex++, limit);
               
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        ImportSheet importSheet = new ImportSheet(resultSet.getInt("id"),
                                resultSet.getInt("employeeInChargeId"),
                                resultSet.getDate("importDate"),
                                resultSet.getDouble("totalCost"), null);
                        importSheet.employee = accountRepository.selectAccount(importSheet.employeeInChargeId);

                        result.add(importSheet);
                    }
                }
            }

            db.commit();

            return result;
        } catch (Exception e) {

            db.rollback();
            throw e;
        }
    }

}

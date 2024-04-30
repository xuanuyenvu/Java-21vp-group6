package com.group06.bsms.order;

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
import java.util.HashMap;
import java.util.Map;
import javax.swing.SortOrder;

public class OrderSheetRepository extends Repository<OrderSheet> implements OrderSheetDAO {

    private final BookRepository bookRepository;
    private final AccountRepository accountRepository;

    public OrderSheetRepository(Connection db, BookRepository bookRepository, AccountRepository accountRepository) {
        super(db, OrderSheet.class);
        this.bookRepository = bookRepository;
        this.accountRepository = accountRepository;

    }

    @Override
    public void insertOrderSheet(OrderSheet orderSheet) throws Exception {
        try {
            db.setAutoCommit(false);
            if (orderSheet == null) {
                throw new NullPointerException("The parameer cannot be null");
            }
            if (orderSheet.orderedBooks.isEmpty()) {
                throw new Exception("The imported books is empty");
            }

            try (PreparedStatement insertorderSheetQuery = db.prepareStatement(
                    "INSERT INTO orderSheet (employeeInChargeId, importDate, totalCost) "
                    + "VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {

                insertorderSheetQuery.setInt(1, orderSheet.employeeInChargeId);
                insertorderSheetQuery.setDate(2, orderSheet.orderDate);
                insertorderSheetQuery.setDouble(3, orderSheet.totalCost);

                int rowsAffected = insertorderSheetQuery.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Insertion failed, no rows affected.");
                }

                ResultSet generatedKeys = insertorderSheetQuery.getGeneratedKeys();
                if (generatedKeys.next()) {
                    orderSheet.id = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Insertion failed, no ID obtained.");
                }

                insertImportedBooksList(orderSheet.id, orderSheet.importedBooks);

            }
        } catch (Exception e) {
            db.rollback();
            throw e;
        }

    }

    private void insertImportedBooksList(int orderSheetId, List<ImportedBook> importedBooks) throws Exception {
        try {
            db.setAutoCommit(false);
            if (importedBooks == null || importedBooks.isEmpty()) {
                throw new NullPointerException("The parameter cannot be null or empty");
            }
            int importedBookResults[] = null;

            String insertQuery = "INSERT INTO ImportedBook (orderSheetId, bookId, quantity, pricePerBook) VALUES (?, ?, ?, ?)";
            try (var importedBookStatement = db.prepareStatement(insertQuery)) {
                for (ImportedBook importedBook : importedBooks) {

                    importedBookStatement.setInt(1, orderSheetId);
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
    public orderSheet selectorderSheet(int id) throws Exception {
        try {
            orderSheet orderSheet = selectById(id);
            if (orderSheet == null) {
                throw new Exception("Entity not found");
            }
            db.setAutoCommit(false);
            try (var selectImportedBooksQuery = db.prepareStatement(
                    "SELECT ib.bookId, b.title, ib.quantity, ib.pricePerBook FROM ImportedBook ib JOIN Book b ON ib.bookId = b.id WHERE ib.orderSheetId = ?")) {
                selectImportedBooksQuery.setInt(1, id);
                var result = selectImportedBooksQuery.executeQuery();

                if (orderSheet.importedBooks == null) {
                    orderSheet.importedBooks = new ArrayList<>();
                }

                while (result.next()) {
                    orderSheet.importedBooks.add(new ImportedBook(id,
                            result.getInt("bookId"),
                            result.getString("title"),
                            result.getInt("quantity"),
                            result.getDouble("pricePerBook")));
                }

                db.commit();
            }
            orderSheet.employee = accountRepository.selectAccount(orderSheet.employeeInChargeId);

            return orderSheet;

        } catch (Exception e) {

            throw e;

        }
    }

    @Override
    public List<orderSheet> selectSearchSortFilterorderSheets(
            int offset, int limit, Map<Integer, SortOrder> sortValue,
            String searchString, String searchChoice
    ) throws Exception {
        List<orderSheet> result = new ArrayList<>();

        try {
            db.setAutoCommit(false);

            String stringQuery = "SELECT orderSheet.id, orderSheet.employeeInChargeId, orderSheet.totalCost, orderSheet.importDate, Account.phone FROM orderSheet JOIN Account ON Account.id = orderSheet.employeeInChargeId";

            stringQuery += " WHERE " + searchChoice + (searchChoice.trim().equals("Account.phone") ? " LIKE ?" : " = ? ");

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
                            sortKey = "orderSheet.importDate";
                        case 2 ->
                            sortKey = "orderSheet.totalCost";
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

                if (searchChoice.trim().equals("orderSheet.importDate")) {
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
                } else if (searchChoice.trim().equals("orderSheet.totalCost")) {
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
                        orderSheet orderSheet = new orderSheet(resultSet.getInt("id"),
                                resultSet.getInt("employeeInChargeId"),
                                resultSet.getDate("importDate"),
                                resultSet.getDouble("totalCost"), null
                        );
                        orderSheet.employee = accountRepository.selectAccount(orderSheet.employeeInChargeId);

                        result.add(orderSheet);
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

    @Override
    public List<orderSheet> selectTop10orderSheetsWithHighestRevenue(Map<Integer, SortOrder> sortAttributeAndOrder, java.sql.Date startDate, java.sql.Date endDate) throws Exception {

        List<orderSheet> result = new ArrayList<>();
        try {
            db.setAutoCommit(false);
            String stringQuery = """
                                 SELECT * FROM
                                 (SELECT orderSheet.id, orderSheet.employeeInChargeId, orderSheet.totalCost, orderSheet.importDate, Account.phone 
                                      FROM orderSheet JOIN Account ON Account.id = orderSheet.employeeInChargeId   
                                      WHERE orderSheet.importDate BETWEEN ? AND ?
                                      ORDER BY orderSheet.totalCost DESC
                                      LIMIT 10)
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
                System.out.println(preparedStatement.toString());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        orderSheet orderSheet = new orderSheet(resultSet.getInt("id"),
                                resultSet.getInt("employeeInChargeId"),
                                resultSet.getDate("importDate"),
                                resultSet.getDouble("totalCost"), null
                        );
                        orderSheet.employee = accountRepository.selectAccount(orderSheet.employeeInChargeId);

                        result.add(orderSheet);
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

}

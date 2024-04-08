package com.group06.bsms.importsheet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import com.group06.bsms.Repository;
import com.group06.bsms.books.BookRepository;
import com.group06.bsms.books.Book;

public class ImportSheetRepository extends Repository<ImportSheet> implements ImportSheetDAO {
    private final BookRepository bookRepository;

    public ImportSheetRepository(Connection db, BookRepository bookRepository) {
        super(db, ImportSheet.class);
        this.bookRepository = bookRepository;

    }

    @Override
    public void insertImportSheet(ImportSheet importSheet) throws Exception {
        try {
            db.setAutoCommit(false);
            if (importSheet == null)
                throw new NullPointerException("The parameer cannot be null");

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
            if (importedBooks == null || importedBooks.isEmpty())
                throw new NullPointerException("The parameter cannot be null or empty");
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

            if (maxImportPrice == null)
                throw new NullPointerException("The parameter cannot be null or empty");

            Book book = bookRepository.selectById(id);
            if (book == null)
                throw new NullPointerException("Cannot find book");

            if (book.maxImportPrice == null || book.maxImportPrice < maxImportPrice)
                book.maxImportPrice = maxImportPrice;
            if (book.salePrice == null || book.salePrice < 1.1 * book.maxImportPrice)
                book.salePrice = 1.1 * book.maxImportPrice;
            bookRepository.updateBookAttributeById(id, "maxImportPrice", book.salePrice);
            bookRepository.updateBookAttributeById(id, "salePrice", book.salePrice);

        } catch (Exception e) {
            throw e;
        }

    }

}

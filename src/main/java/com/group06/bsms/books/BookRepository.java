package com.group06.bsms.books;

import com.group06.bsms.Repository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookRepository extends Repository<Book> implements BookDAO {

    public BookRepository(Connection db) {
        super(db, Book.class);
    }

    @Override
    public List<Book> selectBooksByFilter(int authorId, int publisherId, Double minPrice, Double maxPrice, List<Integer> listBookCategoryId) throws Exception {
        List<Book> result = new ArrayList<>();
        try {
            db.setAutoCommit(false);

            String stringQuery = "SELECT DISTINCT Book.* "
                    + "FROM Book "
                    + "JOIN BookCategory ON Book.id = BookCategory.bookId WHERE 1 = 1 ";

            if (authorId > 0) {
                stringQuery += " AND Book.authorId = ?";
            }

            if (publisherId > 0) {
                stringQuery += " AND Book.publisherId = ?";
            }

            if (minPrice != null) {
                stringQuery += " AND Book.salePrice >= ?";
            }

            if (maxPrice != null) {
                stringQuery += " AND Book.salePrice <= ?";
            }

            if (listBookCategoryId != null && !listBookCategoryId.isEmpty()) {
                for (int i = 0; i < listBookCategoryId.size(); i++) {
                    stringQuery += " AND EXISTS ("
                            + "     SELECT 1"
                            + "     FROM BookCategory bc" + i
                            + "     WHERE bc" + i + ".bookId = Book.id AND bc" + i + ".categoryId = ?"
                            + " )";
                }
            }

            try (PreparedStatement preparedStatement = db.prepareStatement(stringQuery)) {
                int parameterIndex = 1;

                if (authorId > 0) {
                    preparedStatement.setInt(parameterIndex++, authorId);
                }

                if (publisherId > 0) {
                    preparedStatement.setInt(parameterIndex++, publisherId);
                }

                if (minPrice != null) {
                    preparedStatement.setDouble(parameterIndex++, minPrice);
                }

                if (maxPrice != null) {
                    preparedStatement.setDouble(parameterIndex++, maxPrice);
                }

                if (listBookCategoryId != null && !listBookCategoryId.isEmpty()) {
                    for (Integer categoryId : listBookCategoryId) {
                        preparedStatement.setInt(parameterIndex++, categoryId);
                    }
                }

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        result.add(populate(resultSet));
                    }
                }
            }
            
            db.commit();
            return result;

        } catch (SQLException e) {
            db.rollback();
            throw e;
        }
    }
}

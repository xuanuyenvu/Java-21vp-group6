package com.group06.bsms.books;

import com.group06.bsms.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.group06.bsms.authors.Author;
import com.group06.bsms.authors.AuthorRepository;
import com.group06.bsms.publishers.Publisher;
import com.group06.bsms.publishers.PublisherRepository;
import com.group06.bsms.categories.Category;
import java.sql.Date;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.SortOrder;

public class BookRepository extends Repository<Book> implements BookDAO {

    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;

    public BookRepository(Connection db) {
        super(db, Book.class);
        this.authorRepository = new AuthorRepository(db);
        this.publisherRepository = new PublisherRepository(db);
    }

    @Override
    public Book selectBook(int id) throws Exception {
        try {
            Book book = selectById(id);
            if (book == null) {
                throw new Exception("Entity not found");
            }
            book.author = authorRepository.selectById(book.authorId);
            book.publisher = publisherRepository.selectById(book.publisherId);
            db.setAutoCommit(false);
            try (var selectBookCategoriesQuery = db.prepareStatement(
                    "SELECT c.id, c.name, c.isHidden FROM Category c JOIN BookCategory bc ON c.id = bc.categoryId WHERE bc.bookId = ?")) {
                selectBookCategoriesQuery.setInt(1, id);
                var result = selectBookCategoriesQuery.executeQuery();
                if (book.categories == null) {
                    book.categories = new ArrayList<>();
                }
                while (result.next()) {
                    book.categories.add(new Category(
                            result.getInt("id"),
                            result.getString("name"),
                            result.getBoolean("isHidden")));
                }
                db.commit();
            }
            return book;
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            throw e;
        }
    }

    @Override
    public void updateBook(Book book, Book updatedBook) throws Exception {
        try {

            update(updatedBook, "authorId", "publisherId", "title", "pageCount", "publishDate", "dimension",
                    "translatorName", "overview", "salePrice", "hiddenParentCount");

            List<Category> insertedCategories = new ArrayList<>(updatedBook.categories);
            insertedCategories.removeAll(book.categories);
            insertBookCategories(book.id, insertedCategories);

            List<Category> deletedCategories = new ArrayList<>(book.categories);
            deletedCategories.removeAll(updatedBook.categories);
            deleteBookCategories(book.id, deletedCategories);
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    private void deleteBookCategories(int bookId, List<Category> categories) throws Exception {
        try {
            if (!categories.isEmpty()) {
                int deleteBookCategoryResults[] = null;

                db.setAutoCommit(false);

                String deleteQuery = "DELETE FROM bookCategory WHERE bookId = ? AND categoryId = ?";
                try (var deleteBookCategoryStatement = db.prepareStatement(deleteQuery)) {
                    for (Category category : categories) {
                        deleteBookCategoryStatement.setInt(1, bookId);
                        deleteBookCategoryStatement.setInt(2, category.id);
                        deleteBookCategoryStatement.addBatch();
                    }

                    deleteBookCategoryResults = deleteBookCategoryStatement.executeBatch();

                    db.commit();

                    for (int deleteBookCategoryResult : deleteBookCategoryResults) {
                        if (deleteBookCategoryResult == PreparedStatement.EXECUTE_FAILED) {
                            throw new Exception("Cannot delete book's categories");
                        }
                    }
                }
            }
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    private void insertBookCategories(int bookId, List<Category> categories) throws Exception {
        try {

            if (!categories.isEmpty()) {
                int addBookCategoryResults[] = null;

                db.setAutoCommit(false);

                String insertQuery = "INSERT INTO bookCategory (bookId, categoryId) VALUES (?, ?)";
                try (var addBookCategoryStatement = db.prepareStatement(insertQuery)) {
                    for (Category category : categories) {
                        addBookCategoryStatement.setInt(1, bookId);
                        addBookCategoryStatement.setInt(2, category.id);
                        addBookCategoryStatement.addBatch();
                    }
                    addBookCategoryResults = addBookCategoryStatement.executeBatch();

                    db.commit();

                    for (int addBookCategoryResult : addBookCategoryResults) {
                        if (addBookCategoryResult == PreparedStatement.EXECUTE_FAILED) {
                            throw new Exception("Cannot insert book's categories");
                        }
                    }
                }
            }
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public boolean existsBookById(int id)
            throws Exception {
        try {
            db.setAutoCommit(false);

            Book book = selectById(id);

            db.commit();

            if (book == null) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void insertBook(Book book) throws Exception {
        try {
            db.setAutoCommit(false);

            // Insert into Book table
            try (PreparedStatement insertBookQuery = db.prepareStatement(
                    "INSERT INTO Book (authorId, publisherId, title, pageCount, publishDate, dimension, translatorName, overview, isHidden, hiddenParentCount, quantity, salePrice) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, null)",
                    Statement.RETURN_GENERATED_KEYS)) {
                insertBookQuery.setInt(1, book.authorId);
                insertBookQuery.setInt(2, book.publisherId);
                insertBookQuery.setString(3, book.title);
                insertBookQuery.setInt(4, book.pageCount);
                insertBookQuery.setDate(5, book.publishDate);
                insertBookQuery.setString(6, book.dimension);
                insertBookQuery.setString(7, book.translatorName);
                insertBookQuery.setString(8, book.overview);
                insertBookQuery.setBoolean(9, book.isHidden);
                insertBookQuery.setInt(10, book.hiddenParentCount);
                int rowsAffected = insertBookQuery.executeUpdate();

                if (rowsAffected == 0) {
                    throw new SQLException("Insertion failed, no rows affected.");
                }

                ResultSet generatedKeys = insertBookQuery.getGeneratedKeys();
                int bookId;
                if (generatedKeys.next()) {
                    bookId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Insertion failed, no ID obtained.");
                }
                try (PreparedStatement insertCategoryBookQuery = db.prepareStatement(
                        "INSERT INTO BookCategory (bookId, categoryId) "
                        + "VALUES (?, ?)")) {
                    for (Category category : book.categories) {
                        insertCategoryBookQuery.setInt(1, bookId);
                        insertCategoryBookQuery.setInt(2, category.id);
                        insertCategoryBookQuery.addBatch();
                    }

                    int[] categoryRowsAffected = insertCategoryBookQuery.executeBatch();
                    for (int affectedRows : categoryRowsAffected) {
                        if (affectedRows == 0) {
                            throw new SQLException("Insertion into CategoryBook table failed, no rows affected.");
                        }
                    }
                }
                db.commit();
            }
        } catch (SQLException e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void showBook(int id)
            throws Exception {
        try {
            db.setAutoCommit(false);

            var book = selectById(id);

            if (book != null && book.hiddenParentCount > 0) {
                throw new Exception("Publisher and/or Author Hidden");
            }

            updateById(id, "ishidden", false);
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void hideBook(int id)
            throws Exception {
        try {
            db.setAutoCommit(false);

            var book = selectById(id);

            if (book != null && book.hiddenParentCount > 0) {
                throw new Exception("Publisher and/or Author Hidden");
            }

            updateById(id, "isHidden", true);

            db.commit();

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void updateBookHiddenParentCount(int id)
            throws Exception {
        try {
            db.setAutoCommit(false);

            Book book = selectById(id);
            Author author = authorRepository.selectById(book.authorId);
            Publisher publisher = publisherRepository.selectById(book.publisherId);

            Integer hiddenParentCount = 0;
            if (author.isHidden) {
                hiddenParentCount += 1;
            }
            if (publisher.isHidden) {
                hiddenParentCount += 1;
            }

            updateById(id, "hiddenparentcount", hiddenParentCount.toString());

            db.commit();

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void updateBookHiddenParentCountByAuthorId(int authorId)
            throws Exception {
        try {
            db.setAutoCommit(false);

            Map<String, Object> map = new HashMap<>();
            map.put("authorid", authorId);

            List<Book> books = selectAll(map, 0, null, null, null, "id");

            for (Book book : books) {
                updateBookHiddenParentCount(book.id);
            }

            db.commit();

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void updateBookHiddenParentCountByPublisherId(int publisherId)
            throws Exception {
        try {
            db.setAutoCommit(false);

            Map<String, Object> map = new HashMap<>();
            map.put("publisherid", publisherId);

            List<Book> books = selectAll(map, 0, null, null, null, "id");

            for (Book book : books) {
                updateBookHiddenParentCount(book.id);
            }

            db.commit();

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public List<Book> selectSearchSortFilterBooks(int offset, int limit, Map<Integer, SortOrder> sortValue,
            String searchString, String searchChoice,
            int authorId, int publisherId, Double minPrice, Double maxPrice,
            List<Integer> listBookCategoryId) throws Exception {
        List<Book> result = new ArrayList<>();
        try {
            db.setAutoCommit(false);

            String stringQuery = "SELECT * "
                    + " FROM Book "
                    + " LEFT JOIN BookCategory ON Book.id = BookCategory.bookId "
                    + " JOIN Author ON Author.id = book.AuthorId "
                    + " JOIN Publisher ON Publisher.id = Book.publisherId ";

            stringQuery += " WHERE " + searchChoice + " LIKE ? ";

            if (authorId > 0) {
                stringQuery += " AND Book.authorId = ? ";
            }

            if (publisherId > 0) {
                stringQuery += " AND Book.publisherId = ? ";
            }

            stringQuery += " AND (Book.salePrice >= ? AND Book.salePrice <= ? OR Book.salePrice is null)";

            if (listBookCategoryId != null && !listBookCategoryId.isEmpty()) {
                for (int i = 0; i < listBookCategoryId.size(); i++) {
                    stringQuery += " AND EXISTS ("
                            + "     SELECT 1"
                            + "     FROM BookCategory bc" + i
                            + "     WHERE bc" + i + ".bookId = Book.id AND bc" + i + ".categoryId = ?"
                            + " )";
                }
            }

            for (Map.Entry<Integer, SortOrder> entry : sortValue.entrySet()) {
                Integer key = entry.getKey();
                SortOrder value = entry.getValue();

                var sortKeys = new ArrayList<String>(List.of(
                        " ORDER BY Book.title ",
                        " ORDER BY Author.name ",
                        " ORDER BY Publisher.name ",
                        " ORDER BY Book.quantity ",
                        " ORDER BY Book.salePrice "));

                var sortValues = new HashMap<SortOrder, String>();
                sortValues.put(SortOrder.ASCENDING, " ASC ");
                sortValues.put(SortOrder.DESCENDING, " DESC ");

                stringQuery += sortKeys.get(key);
                stringQuery += sortValues.get(value);
            }

            stringQuery += " OFFSET ? LIMIT ? ";

            try (PreparedStatement preparedStatement = db.prepareStatement(stringQuery)) {
                int parameterIndex = 1;
                preparedStatement.setString(parameterIndex++, "%" + searchString + "%");

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

                preparedStatement.setInt(parameterIndex++, offset);
                preparedStatement.setInt(parameterIndex++, limit);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        result.add(populate(resultSet));
                    }
                }
            }
            db.commit();
            for (var book : result) {
                book.author = authorRepository.selectById(book.authorId);
                book.publisher = publisherRepository.selectById(book.publisherId);
            }
            return result;
        } catch (SQLException e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void updateBookAttributeById(int bookId, String attr, Object value) throws Exception {
        try {
            db.setAutoCommit(false);

            updateById(bookId, attr, value);

            db.commit();

        } catch (Exception e) {
            db.rollback();
            if (e.getMessage().equals("Entity not found")) {
                throw new Exception("Book not found");
            }
            throw e;
        }
    }

    @Override
    public List<Book> getNewBooks() throws Exception {
        List<Book> result = new ArrayList<>();
        try {
            db.setAutoCommit(false);
            try (var query = db.prepareStatement(
                    "SELECT *\n"
                    + "FROM Book B\n"
                    + "ORDER BY B.publishDate DESC\n"
                    + "LIMIT 20;")) {
                try (ResultSet resultSet = query.executeQuery()) {
                    while (resultSet.next()) {
                        result.add(populate(resultSet));
                    }
                }
                db.commit();
                for (var book : result) {
                    book.author = authorRepository.selectById(book.authorId);
                    book.publisher = publisherRepository.selectById(book.publisherId);
                }
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
    public List<Book> getHotBooks() throws Exception {
        List<Book> result = new ArrayList<>();
        try {
            db.setAutoCommit(false);
            try (var query = db.prepareStatement(
                    "SELECT B.*, COALESCE(SUM(OB.quantity), 0) AS total_quantity_ordered\n"
                    + "FROM Book B\n"
                    + "LEFT JOIN OrderedBook OB ON OB.bookId = B.id\n"
                    + "GROUP BY B.id\n"
                    + "ORDER BY total_quantity_ordered DESC\n"
                    + "LIMIT 20;")) {
                try (ResultSet resultSet = query.executeQuery()) {
                    while (resultSet.next()) {
                        result.add(populate(resultSet));
                    }
                }
                db.commit();
                for (var book : result) {
                    book.author = authorRepository.selectById(book.authorId);
                    book.publisher = publisherRepository.selectById(book.publisherId);
                }
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
    public List<Book> getOutOfStockBooks() throws Exception {
        List<Book> result = new ArrayList<>();
        try {
            db.setAutoCommit(false);

            try (var query = db.prepareStatement(
                    "SELECT * FROM BOOK WHERE QUANTITY = 0")) {
                try (ResultSet resultSet = query.executeQuery()) {
                    while (resultSet.next()) {
                        result.add(populate(resultSet));
                    }
                }
                db.commit();
                for (var book : result) {
                    book.author = authorRepository.selectById(book.authorId);
                    book.publisher = publisherRepository.selectById(book.publisherId);
                }
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
    public List<Book> selectTop10BooksWithHighestRevenue(Map<Integer, SortOrder> sortAttributeAndOrder,
            Date startDate, Date endDate) throws Exception {
        List<Book> result = new ArrayList<>();
        try {
            db.setAutoCommit(false);
            String stringQuery = "SELECT top_10.*\n"
                    + "FROM\n"
                    + "    (SELECT Book.*,\n"
                    + "     COALESCE(SUM(OrderedBook.pricePerbook * OrderedBook.quantity), 0) AS revenue\n"
                    + "     FROM Book\n"
                    + "     LEFT JOIN OrderedBook ON OrderedBook.bookid = Book.id\n"
                    + "     LEFT JOIN OrderSheet ON OrderedBook.orderSheetId = OrderSheet.id\n"
                    + "     WHERE orderDate BETWEEN ? AND ?\n"
                    + "     GROUP BY Book.id\n"
                    + "     ORDER BY revenue DESC\n"
                    + "	 LIMIT 10) AS top_10\n"
                    + "JOIN Author ON Author.id = top_10.authorId\n"
                    + "JOIN Publisher ON Publisher.id = top_10.publisherId\n";

            for (Map.Entry<Integer, SortOrder> entry : sortAttributeAndOrder.entrySet()) {
                Integer attribute = entry.getKey();
                SortOrder sortOrder = entry.getValue();

                var sortAttributes = new ArrayList<String>(List.of(
                        " ORDER BY top_10.title ",
                        " ORDER BY Author.name ",
                        " ORDER BY Publisher.name ",
                        " ORDER BY top_10.quantity ",
                        " ORDER BY top_10.salePrice ",
                        " ORDER BY top_10.revenue "));

                var sortOrders = new HashMap<SortOrder, String>();
                sortOrders.put(SortOrder.ASCENDING, " ASC ");
                sortOrders.put(SortOrder.DESCENDING, " DESC ");

                stringQuery += sortAttributes.get(attribute);
                stringQuery += sortOrders.get(sortOrder);
            }
            try (PreparedStatement preparedStatement = db.prepareStatement(stringQuery)) {

                preparedStatement.setDate(1, startDate);
                preparedStatement.setDate(2, endDate);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        result.add(populate(resultSet));
                    }
                }
                db.commit();
                for (var book : result) {
                    book.author = authorRepository.selectById(book.authorId);
                    book.publisher = publisherRepository.selectById(book.publisherId);
                }
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

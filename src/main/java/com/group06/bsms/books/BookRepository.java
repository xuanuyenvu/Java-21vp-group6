package com.group06.bsms.books;

import com.group06.bsms.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.group06.bsms.authors.Author;
import com.group06.bsms.authors.AuthorRepository;
import com.group06.bsms.publishers.Publisher;
import com.group06.bsms.publishers.PublisherRepository;

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
    public List<Book> selectAllBooks()
            throws Exception {
        try {
            db.setAutoCommit(false);

            var list = selectAll(
                    null,
                    0, 10,
                    "title", Sort.ASC,
                    "id", "authorid", "publisherid", "title",
                    "quantity", "saleprice",
                    "ishidden", "hiddenparentcount");

            for (var book : list) {
                book.author = authorRepository.selectById(book.authorId);
                book.publisher = publisherRepository.selectById(book.publisherId);
            }

            db.commit();

            return list;

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public List<Book> selectBooks(String title)
            throws Exception {
        try {
            db.setAutoCommit(false);

            Map<String, Object> searchParams = new HashMap<>();
            searchParams.put("title", title);

            var list = selectAll(
                    searchParams,
                    0, 10,
                    "title", Sort.ASC,
                    "id", "authorid", "publisherid", "title",
                    "quantity", "saleprice",
                    "ishidden", "hiddenparentcount");

            for (var book : list) {
                book.author = authorRepository.selectById(book.authorId);
                book.publisher = publisherRepository.selectById(book.publisherId);
            }

            db.commit();

            return list;

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void insertBook(Book book)
            throws Exception {
        try {
            db.setAutoCommit(false);

            db.commit();

        } catch (Exception e) {
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
    public List<Book> selectBooksByFilter(int authorId, int publisherId, Double minPrice, Double maxPrice,
            List<Integer> listBookCategoryId) throws Exception {
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

    @Override
    public List<Book> selectSearchSortFilterBooks(int offset, int limit, Map<Integer, SortOrder> sortValue,
            String searchString, String searchChoice,
            int authorId, int publisherId, Double minPrice, Double maxPrice,
            List<Integer> listBookCategoryId) throws Exception {
        List<Book> result = new ArrayList<>();
        try {
            db.setAutoCommit(false);

            String stringQuery = "SELECT DISTINCT * "
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

            if (minPrice != null) {
                stringQuery += " AND Book.salePrice >= ? ";
            }

            if (maxPrice != null) {
                stringQuery += " AND Book.salePrice <= ? ";
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

            for (Map.Entry<Integer, SortOrder> entry : sortValue.entrySet()) {
                Integer key = entry.getKey();
                SortOrder value = entry.getValue();

                var sortKeys = new ArrayList<String>(List.of(
                        " ORDER BY Book.title ",
                        " ORDER BY Author.name ",
                        " ORDER BY Publisher.name ",
                        " ORDER BY Book.quantity ",
                        " ORDER BY Book.salePrice "
                ));

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
}

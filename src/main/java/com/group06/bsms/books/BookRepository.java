package com.group06.bsms.books;

import com.group06.bsms.Repository;
import com.group06.bsms.authors.Author;
import com.group06.bsms.authors.AuthorRepository;
import com.group06.bsms.categories.Category;
import com.group06.bsms.publishers.Publisher;
import com.group06.bsms.publishers.PublisherRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                    "ishidden", "hiddenparentcount"
            );

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
                    "ishidden", "hiddenparentcount"
            );

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
    public void insertBook(Book book) throws Exception {
        try {
            db.setAutoCommit(false);
            var insertBookQuery = db.prepareStatement(
                    "INSERT INTO books (authorId, publisherId, title, pageCount, publishDate, dimension, translatorName, overview, hiddenParentCount) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

            insertBookQuery.setInt(1, book.authorId);
            insertBookQuery.setInt(2, book.publisherId);
            insertBookQuery.setString(3, book.title);
            insertBookQuery.setInt(4, book.pageCount);
            insertBookQuery.setDate(5, book.publishDate);
            insertBookQuery.setString(6, book.dimension);
            insertBookQuery.setString(7, book.translatorName);
            insertBookQuery.setString(8, book.overview);
            insertBookQuery.setInt(9, book.hiddenParentCount);

            int insertBookResult = insertBookQuery.executeUpdate();

            int[] insertBookCategoryResults = null;
            if (!book.categories.isEmpty()) {
                String insertQuery = "INSERT INTO bookCategory (bookId, categoryId) VALUES (?, ?)";
                var addBookCategoryStatement = db.prepareStatement(insertQuery);
                for (Category category : book.categories) {
                    addBookCategoryStatement.setInt(1, book.id);
                    addBookCategoryStatement.setInt(2, category.id);
                    addBookCategoryStatement.addBatch();
                }
                insertBookCategoryResults = addBookCategoryStatement.executeBatch();
            }

            db.commit();

            if (insertBookResult == 0) {
                throw new Exception("Entity not found");
            }

            for (int addBookCategoryResult : insertBookCategoryResults) {
                if (addBookCategoryResult == PreparedStatement.EXECUTE_FAILED) {
                    throw new Exception("Cannot update book's categories");
                }
            }

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
}

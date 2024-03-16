package com.group06.bsms.books;

import com.google.gson.JsonObject;
import com.group06.bsms.Repository;
import com.group06.bsms.authors.Author;
import com.group06.bsms.categories.Category;
import com.group06.bsms.publishers.Publisher;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.util.List;

public class BookRepository extends Repository<Book> implements BookDAO {
    public BookRepository(Connection db) {
        super(db, Book.class);
    }

    @Override
    public void updateBook(Book book) throws Exception {
        try {
            db.setAutoCommit(false);

            var updateBookQuery = db.prepareStatement(
                    "UPDATE books SET authorId=?, publisherId=?, title=?, pageCount=?, publishDate=?, dimension=?, translatorName=?, overview=?, quantity=?, salePrice=?, hiddenParentCount=?, WHERE id=?");

            updateBookQuery.setInt(1, book.authorId);
            updateBookQuery.setInt(2, book.publisherId);
            updateBookQuery.setString(3, book.title);
            updateBookQuery.setInt(4, book.pageCount);
            updateBookQuery.setDate(5, book.publishDate);
            updateBookQuery.setString(6, book.dimension);
            updateBookQuery.setString(7, book.translatorName);
            updateBookQuery.setString(8, book.overview);
            updateBookQuery.setInt(9, book.quantity);
            updateBookQuery.setDouble(10, book.salePrice);
            updateBookQuery.setInt(11, book.hiddenParentCount);
            updateBookQuery.setInt(13, book.id);

            var updateBookResult = updateBookQuery.executeUpdate();

            int addBookCategoryResults[] = null;
            if (!book.categories.isEmpty()) {
                String insertQuery = "INSERT INTO bookCategory (bookId, categoryId) VALUES VALUES (?, ?)";
                var addBookCategoryStatement = db.prepareStatement(insertQuery);
                for (Category category : book.categories) {
                    addBookCategoryStatement.setInt(1, book.id);
                    addBookCategoryStatement.setInt(1, category.id);
                }
                addBookCategoryResults = addBookCategoryStatement.executeBatch();
            }

            db.commit();

            if (updateBookResult == 0) {
                throw new Exception("Entity not found");
            }

            for (int addBookCategoryResult : addBookCategoryResults) {
                if (addBookCategoryResult == PreparedStatement.EXECUTE_FAILED)
                    throw new Exception("Cannot update book's categories");
            }

            
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    private Author getAuthor(int authorId)
            throws Exception {
        try {
            db.setAutoCommit(false);

            Author author;

            var query1 = db.prepareStatement("""
                        select * from author
                        where id = ?
                    """);
            query1.setInt(1, authorId);

            var result = query1.executeQuery();

            if (result.next()) {
                author = new Author(authorId, result.getString("name"), result.getString("overview"),
                        result.getBoolean("ishidden"));
            } else
                throw new Exception("Entity not found");

            db.commit();

            return author;

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    private Publisher getPublisher(int publisherId)
            throws Exception {
        try {
            db.setAutoCommit(false);

            Publisher publisher;

            var query2 = db.prepareStatement("""
                        select * from publisher
                        where id = ?
                    """);

            query2.setInt(1, publisherId);

            var result2 = query2.executeQuery();

            if (result2.next()) {
                publisher = new Publisher(publisherId, result2.getString("name"), result2.getString("email"),
                        result2.getString("address"), result2.getBoolean("ishidden"));
            } else
                throw new Exception("Entity not found");

            return publisher;

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

            db.commit();

            return true;

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
                    "id", "authorid", "publisherid", "title", "pagecount",
                    "publishdate", "dimension", "translatorname",
                    "overview", "quantity", "saleprice",
                    "hiddenparentcount");

            for (var book : list) {
                book.author = getAuthor(book.authorId);
                book.publisher = getPublisher(book.publisherId);
            }

            db.commit();

            return list;

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public List<Book> selectBook(String title)
            throws Exception {
        try {
            db.setAutoCommit(false);

            var jsonSearch = new JsonObject();
            jsonSearch.addProperty("title", title);

            System.out.println(jsonSearch.toString());

            var list = selectAll(
                    jsonSearch,
                    0, 10,
                    "title", Sort.ASC,
                    "id", "authorid", "publisherid", "title", "pagecount",
                    "publishdate", "dimension", "translatorname",
                    "overview", "quantity", "saleprice",
                    "hiddenparentcount");

            for (var book : list) {
                book.author = getAuthor(book.authorId);
                book.publisher = getPublisher(book.publisherId);
            }

            db.commit();

            return list;

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    // post methods
    @Override
    public void createBook(Book book)
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
    public void enableBook(int id)
            throws Exception {
        try {
            db.setAutoCommit(false);

            updateById(id, "ishidden", "false");

            db.commit();

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void disableBook(int id)
            throws Exception {
        try {
            db.setAutoCommit(false);

            var book = selectById(id);

            if (book.hiddenParentCount > 0) {
                throw new Exception("Publisher and/or Author Hidden");
            }

            updateById(id, "ishidden", "true");

            db.commit();

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    /**
     * Given a book's ID and
     * 
     * @param id Book's ID
     */
    @Override
    public void updateHiddenParentCount(int id)
            throws Exception {
        try {
            db.setAutoCommit(false);

            Book book = selectById(id);
            Author author = getAuthor(book.authorId);
            Publisher publisher = getPublisher(book.publisherId);

            Integer hiddenParentCount = 0;
            if (author.isHidden)
                hiddenParentCount += 1;
            if (publisher.isHidden)
                hiddenParentCount += 1;

            updateById(id, "hiddenparentcount", hiddenParentCount.toString());

            db.commit();

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void updateHiddenParentCountByAuthorId(int authorId)
            throws Exception {
        try {
            db.setAutoCommit(false);

            JsonObject json = new JsonObject();
            json.addProperty("authorid", authorId);

            List<Book> books = selectAll(json, 0, null, null, null, "id");

            for (Book book : books) {
                updateHiddenParentCount(book.id);
            }

            db.commit();

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void updateHiddenParentCountByPublisherId(int publisherId)
            throws Exception {
        try {
            db.setAutoCommit(false);

            JsonObject json = new JsonObject();
            json.addProperty("authorid", publisherId);

            List<Book> books = selectAll(json, 0, null, null, null, "id");

            for (Book book : books) {
                updateHiddenParentCount(book.id);
            }

            db.commit();

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }
}

package com.group06.bsms.books;

import com.group06.bsms.Repository;
import com.group06.bsms.authors.Author;
import com.group06.bsms.authors.AuthorRepository;
import com.group06.bsms.publishers.Publisher;
import com.group06.bsms.publishers.PublisherRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
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

    @Override
    public boolean existsBookById(int id)
            throws Exception {
        try {
            db.setAutoCommit(false);

            Book book = selectById(id);

            db.commit();

            if (book == null) return false;
            else return true;

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
                "id","authorid","publisherid","title",
                "quantity","saleprice",
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

            Map<String,Object> searchParams  = new HashMap<>();
            searchParams.put("title", title);
    
            var list = selectAll(
                searchParams,
                0, 10,
                "title", Sort.ASC,
                "id","authorid","publisherid","title",
                "quantity","saleprice",
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

            updateById(id, "isHidden",true);

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
    public void updateBookHiddenParentCountByAuthorId(int authorId)
            throws Exception {
        try {
            db.setAutoCommit(false);

            Map<String,Object>map = new HashMap<>();
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

            Map<String,Object>map = new HashMap<>();
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

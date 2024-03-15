package com.group06.bsms.books;

import com.google.gson.JsonObject;
import com.group06.bsms.Repository;
import com.group06.bsms.authors.Author;
import com.group06.bsms.publishers.Publisher;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookRepository extends Repository<Book> implements BookDAO {
    public BookRepository(Connection db) {
        super(db, Book.class);
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
                author = new Author(authorId, result.getString("name"), result.getString("overview"), result.getBoolean("ishidden"));
            }
            else throw new Exception("Entity not found");

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
                publisher = new Publisher(publisherId, result2.getString("name"), result2.getString("email"), result2.getString("address"), result2.getBoolean("ishidden"));
            }
            else throw new Exception("Entity not found");

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
                "id","authorid","publisherid","title",
                "quantity","saleprice",
                "ishidden", "hiddenparentcount"
            );

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
    public List<Book> selectBooks(String title)
            throws Exception {
        try {
            db.setAutoCommit(false);

            Map<String,Object> map  = new HashMap<>();
            map.put("title", title);
    
            var list = selectAll(
                null,
                0, 10,
                "title", Sort.ASC,
                "id","authorid","publisherid","title",
                "quantity","saleprice",
                "ishidden", "hiddenparentcount"
            );

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
            Author author = getAuthor(book.authorId);
            Publisher publisher = getPublisher(book.publisherId);

            Integer hiddenParentCount = 0;
            if (author.isHidden) hiddenParentCount += 1;
            if (publisher.isHidden) hiddenParentCount += 1;

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

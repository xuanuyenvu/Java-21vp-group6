package com.group06.bsms.books;

import com.group06.bsms.Repository;
import com.group06.bsms.authors.Author;
import com.group06.bsms.publishers.Publisher;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.util.List;

public class BookRepository extends Repository<Book> implements BookDAO {
    public BookRepository(Connection db) {
        super(db, Book.class);
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

            var list = selectAll1(
                null,
                0, 10,
                "title", Sort.ASC,
                "id","authorid","publisherid","title","pagecount",
                "publishdate","dimension","translatorname",
                "overview","quantity","saleprice",
                "hiddenparentcount"
            );

            for (var book : list) {
                var query1 = db.prepareStatement("""
                            select * from author
                            where id = ?
                        """);
                query1.setInt(1, book.authorId);

                var result = query1.executeQuery();

                if (result.next()) {
                    book.author = new Author(book.authorId, result.getString("name"), result.getString("overview"), result.getBoolean("ishidden"));
                }
                else throw new Exception("Entity not found");
                

                var query2 = db.prepareStatement("""
                            select * from publisher
                            where id = ?
                        """);
                
                query2.setInt(1, book.publisherId);

                var result2 = query2.executeQuery();                

                if (result2.next()) {
                    book.publisher = new Publisher(book.publisherId, result2.getString("name"), result2.getString("email"), result2.getString("address"), result2.getBoolean("ishidden"));
                }
                else throw new Exception("Entity not found");
        
            }
            
            db.commit();

            return list;

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }
    @Override
    public List<Book> selectBookByTitle(String title)
            throws Exception {
        try {
            db.setAutoCommit(false);
    
            db.commit();
    
            return null;
    
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }
    @Override
    public List<Book> selectBookByAuthor(String authorName) 
            throws Exception {
        try {
            db.setAutoCommit(false);

            db.commit();

            return null;

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }
    //post methods
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
    // @Override
    // public void updateBook(int authorId, int publisherId, String title, int pageCount,
    //                 Date publishDate, String dimension, String translatorName,
    //                 String overview, double salePric,
    //                 List<Category> categories)
    //         throws Exception {
    //     try {
    //         db.setAutoCommit(false);

    //         db.commit();

    //     } catch (Exception e) {
    //         db.rollback();
    //         throw e;
    //     }
    // }
    @Override
    public void enableBook(int id)
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
    public void disableBook(int id)
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
    public void updateHiddenParentCount()
            throws Exception {
        try {
            db.setAutoCommit(false);

            db.commit();

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

}

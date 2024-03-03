package com.group06.bsms.books;

import com.group06.bsms.Repository;
import java.sql.Connection;

public class BookRepository extends Repository<Book> implements BookDAO {
    public BookRepository(Connection db) {
        super(db, Book.class);
    }
    @Override
    public void update(Book book) throws Exception {
        try {
            db.setAutoCommit(false);

            var query = db.prepareStatement(
                    "UPDATE books SET authorId=?, publisherId=?, title=?, pageCount=?, publishDate=?, dimension=?, translatorName=?, overview=?, quantity=?, salePrice=?, hiddenParentCount=?, WHERE id=?");

            query.setInt(1, book.authorId);
            query.setInt(2, book.publisherId);
            query.setString(3, book.title);
            query.setInt(4, book.pageCount);
            query.setDate(5, book.publishDate);
            query.setString(6, book.dimension);
            query.setString(7, book.translatorName);
            query.setString(8, book.overview);
            query.setInt(9, book.quantity);
            query.setDouble(10, book.salePrice);
            query.setInt(11, book.hiddenParentCount);
            //query.setDouble(12, book.maxImportPrice);
            query.setInt(13, book.id);

            var result = query.executeUpdate();

            db.commit();

            if (result == 0) {
                throw new Exception("Entity not found");
            }

            // query = db.prepareStatement("DELETE * FROM BookCategory WHERE bookId = ?");
            // query.setInt(1, book.id);

            // result = query.executeUpdate();

            // if (result == 0) {
            //     throw new Exception("Can't remove old book's category");
            // }
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

}

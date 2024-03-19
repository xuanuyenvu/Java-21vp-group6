package com.group06.bsms.publishers;

import java.sql.Connection;

import com.group06.bsms.Repository;
import java.sql.Statement;
import java.util.List;

public class PublisherRepository extends Repository<Publisher> implements PublisherDAO {

    public PublisherRepository(Connection db) {
        super(db, Publisher.class);
    }

    @Override
    public List<Publisher> selectAllPublishers() throws Exception {
        try {
            db.setAutoCommit(false);

            var publishers = selectAll(
                    null,
                    0, null,
                    "name", Sort.ASC
            );

            db.commit();

            return publishers;

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    public int selectIdByName(String publisherName) throws Exception {
        try {
            db.setAutoCommit(false);

            var query = db.prepareStatement(
                    "SELECT id FROM Publisher WHERE name = ?");

            query.setString(1, publisherName);

            var result = query.executeQuery();

            int publisherId = -1;

            if (result.next()) {
                publisherId = result.getInt("id");
            } else {
                var insertQuery = db.prepareStatement(
                        "INSERT INTO Publisher (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
                insertQuery.setString(1, publisherName);
                insertQuery.executeUpdate();

                var generatedKeys = insertQuery.getGeneratedKeys();
                if (generatedKeys.next()) {
                    publisherId = generatedKeys.getInt(1);
                }
            }

            db.commit();

            return publisherId;
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    public Publisher selectPublisher(int id) throws Exception {
        try {
            Publisher publisher = selectById(id);
            if (publisher == null) {
                throw new Exception("Publisher not found");
            }

            db.commit();

            return publisher;

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }
}

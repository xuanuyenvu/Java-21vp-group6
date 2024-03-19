package com.group06.bsms.publishers;

import java.sql.Connection;

import com.group06.bsms.Repository;
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
                    0, 10,
                    "name", Sort.ASC
            );

            db.commit();

            return publishers;

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }
}

package com.group06.bsms.publishers;

import java.sql.Connection;

import com.group06.bsms.Repository;

public class PublisherRepository extends Repository<Publisher> implements PublisherDAO {
    public PublisherRepository(Connection db) {
        super(db, Publisher.class);
    }
}

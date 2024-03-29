package com.group06.bsms.publishers;

import java.util.List;

public interface PublisherDAO {

    List<Publisher> selectAllPublishers() throws Exception;

    public int insertPublisherIfNotExists(String publisherName) throws Exception;

    public Publisher selectPublisher(int id) throws Exception;

    public Publisher selectPublisherByName(String publisherName) throws Exception;
}

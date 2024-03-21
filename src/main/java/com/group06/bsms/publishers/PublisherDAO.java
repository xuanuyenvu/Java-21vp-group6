package com.group06.bsms.publishers;

import java.util.List;

public interface PublisherDAO {
    
    List<Publisher> selectAllPublisherNames() throws Exception;
    public int insertPublisherIfNotExists(String publisherName) throws Exception;
    public Publisher selectPublisher(int id) throws Exception;
}

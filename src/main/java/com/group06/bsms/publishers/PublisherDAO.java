package com.group06.bsms.publishers;

import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;

public interface PublisherDAO {

    List<Publisher> selectAllPublishers() throws Exception;

    public int insertPublisherIfNotExists(String publisherName) throws Exception;

    public Publisher selectPublisher(int id) throws Exception;

    public Publisher selectPublisherByName(String publisherName) throws Exception;

    void showPublisher(int id) throws Exception;

    void hidePublisher(int id) throws Exception;

    void updatePublisher(Publisher publisher, Publisher updatedPublisher) throws Exception;

    void insertPublisher(Publisher publisher) throws Exception;

    void updatePublisherAttributeById(int publisherId, String attr, Object value) throws Exception;

    public List<Publisher> selectSearchSortFilterPublishers(
            int offset, int limit, Map<Integer, SortOrder> sortValue,
            String searchString
    ) throws Exception;
}

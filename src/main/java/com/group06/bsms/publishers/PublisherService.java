package com.group06.bsms.publishers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;

public class PublisherService {

    private final PublisherDAO publisherDAO;

    public PublisherService(PublisherDAO publisherDAO) {
        this.publisherDAO = publisherDAO;
    }

    public List<Publisher> selectAllPublishers() {
        try {
            List<Publisher> publishers = publisherDAO.selectAllPublishers();
            return publishers;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public int insertPublisherIfNotExists(String publisherName) throws Exception {
        try {
            if (publisherName == null) {
                return -1;
            } else {
                return publisherDAO.insertPublisherIfNotExists(publisherName);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public Publisher selectPublisher(int id) throws Exception {
        try {
            return publisherDAO.selectPublisher(id);
        } catch (Exception e) {
            throw e;
        }
    }

    public Publisher selectPublisherByName(String publisherName) throws Exception {
        try {
            return publisherDAO.selectPublisherByName(publisherName);
        } catch (Exception e) {
            throw e;
        }
    }

    public Publisher getPublisher(int id) throws Exception {
        try {
            Publisher publisher = publisherDAO.selectPublisher(id);
            if (publisher == null) {
                throw new Exception("Cannot find publisher with id = " + id);
            }
            return publisher;
        } catch (Exception e) {
            throw e;
        }
    }

    public void updatePublisherAttributeById(int publisherId, String attr, Object value) throws Exception {
        publisherDAO.updatePublisherAttributeById(publisherId, attr, value);
    }

    public void updatePublisher(Publisher publisher, Publisher updatedPublisher) throws Exception {
        try {
            if (updatedPublisher.name == null || updatedPublisher.name.equals("")) {
                throw new Exception("Name cannot be empty");
            }

            publisherDAO.updatePublisher(publisher, updatedPublisher);
        } catch (Exception e) {
            throw e;
        }
    }

    public void insertPublisher(String name, String email, String address, boolean hideChecked) throws Exception {
        if (name == null || name.equals("")) {
            throw new Exception("Name cannot be empty");
        }

        Publisher publisher = new Publisher();

        publisher.name = name;
        publisher.email = email;
        publisher.address = address;
        publisher.isHidden = hideChecked;

        publisherDAO.insertPublisher(publisher);
    }

    public void hidePublisher(int id) throws Exception {
        publisherDAO.hidePublisher(id);
    }

    public void showPublisher(int id) throws Exception {
        publisherDAO.showPublisher(id);
    }

    public List<Publisher> searchSortFilterPublishers(
            int offset, int limit, Map<Integer, SortOrder> sortValue,
            String searchString
    ) throws Exception {

        List<Publisher> publishers = publisherDAO.selectSearchSortFilterPublishers(
                offset, limit, sortValue, searchString
        );

        return publishers;
    }
}

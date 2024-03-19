package com.group06.bsms.publishers;

import java.util.ArrayList;
import java.util.List;

public class PublisherService {

    private final PublisherDAO publisherDAO;

    public PublisherService(PublisherDAO publisherDAO) {
        this.publisherDAO = publisherDAO;
    }

    public List<Publisher> getAllPublishers() {
        try {
            List<Publisher> publishers = publisherDAO.selectAllPublishers();
            return publishers;
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<Publisher>();
        }
    }

    public int selectIdByName(String publisherName) throws Exception {
        try {
            return publisherDAO.selectIdByName(publisherName);
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
}

package com.group06.bsms.publishers;

import java.util.List;

public interface PublisherDAO {
    
    List<Publisher> selectAllPublishers() throws Exception;
}

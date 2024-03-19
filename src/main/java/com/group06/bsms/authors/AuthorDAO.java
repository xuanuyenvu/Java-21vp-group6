package com.group06.bsms.authors;

import java.util.List;

public interface AuthorDAO {
    
    List<Author> selectAllAuthors() throws Exception;
}

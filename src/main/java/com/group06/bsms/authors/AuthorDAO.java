package com.group06.bsms.authors;

import java.util.List;

public interface AuthorDAO {

    List<Author> selectAllAuthors() throws Exception;
    public int selectIdByName(String authorName) throws Exception;
    public Author selectAuthor(int id) throws Exception;
}

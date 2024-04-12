package com.group06.bsms.authors;

import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;

public interface AuthorDAO {

    List<Author> selectAllAuthors() throws Exception;

    public int insertAuthorIfNotExists(String authorName) throws Exception;

    public Author selectAuthor(int id) throws Exception;

    public Author selectAuthorByName(String authorName) throws Exception;

    void showAuthor(int id) throws Exception;

    void hideAuthor(int id) throws Exception;

    void updateAuthor(Author author, Author updatedAuthor) throws Exception;

    void insertAuthor(Author author) throws Exception;

    void updateAuthorAttributeById(int authorId, String attr, Object value) throws Exception;

    public List<Author> selectSearchSortFilterAuthors(
            int offset, int limit, Map<Integer, SortOrder> sortValue,
            String searchString
    ) throws Exception;
}

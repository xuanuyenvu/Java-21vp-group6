package com.group06.bsms.authors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;

public class AuthorService {

    private final AuthorDAO authorDAO;

    public AuthorService(AuthorDAO authorDAO) {
        this.authorDAO = authorDAO;
    }

    public List<Author> selectAllAuthors() {
        try {
            List<Author> authors = authorDAO.selectAllAuthors();
            return authors;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public int insertAuthorIfNotExists(String authorName) throws Exception {
        try {
            if (authorName == null) {
                return -1;
            } else {
                return authorDAO.insertAuthorIfNotExists(authorName);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public Author selectAuthor(int id) throws Exception {
        try {
            return authorDAO.selectAuthor(id);
        } catch (Exception e) {
            throw e;
        }
    }

    public Author selectAuthorByName(String authorName) throws Exception {
        try {
            return authorDAO.selectAuthorByName(authorName);
        } catch (Exception e) {
            throw e;
        }
    }

    public Author getAuthor(int id) throws Exception {
        try {
            Author author = authorDAO.selectAuthor(id);
            if (author == null) {
                throw new Exception("Cannot find author with id = " + id);
            }
            return author;
        } catch (Exception e) {
            throw e;
        }
    }

    public void updateAuthorAttributeById(int authorId, String attr, Object value) throws Exception {
        authorDAO.updateAuthorAttributeById(authorId, attr, value);
    }

    public void updateAuthor(Author author, Author updatedAuthor) throws Exception {
        try {
            if (updatedAuthor.name == null || updatedAuthor.name.equals("")) {
                throw new Exception("Name cannot be empty");
            }

            authorDAO.updateAuthor(author, updatedAuthor);
        } catch (Exception e) {
            throw e;
        }
    }

    public void insertAuthor(String name, String overview, boolean hideChecked) throws Exception {
        if (name == null || name.equals("")) {
            throw new Exception("Name cannot be empty");
        }

        Author author = new Author();

        author.name = name;
        author.overview = overview;
        author.isHidden = hideChecked;

        authorDAO.insertAuthor(author);
    }

    public void hideAuthor(int id) throws Exception {
        authorDAO.hideAuthor(id);
    }

    public void showAuthor(int id) throws Exception {
        authorDAO.showAuthor(id);
    }

    public List<Author> searchSortFilterAuthors(
            int offset, int limit, Map<Integer, SortOrder> sortValue,
            String searchString
    ) throws Exception {

        List<Author> authors = authorDAO.selectSearchSortFilterAuthors(
                offset, limit, sortValue, searchString
        );

        return authors;
    }
}

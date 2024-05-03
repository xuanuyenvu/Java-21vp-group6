package com.group06.bsms.authors;

import java.sql.Connection;

import com.group06.bsms.Repository;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;

public class AuthorRepository extends Repository<Author> implements AuthorDAO {

    public AuthorRepository(Connection db) {
        super(db, Author.class);
    }

    @Override
    public List<Author> selectAllAuthors() throws Exception {
        try {
            var authors = selectAll(
                    null,
                    0, null,
                    "name", Sort.ASC,
                    "name", "id", "overview", "isHidden");

            return authors;
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public int insertAuthorIfNotExists(String authorName) throws Exception {
        try {
            db.setAutoCommit(false);

            try (var query = db.prepareStatement(
                    "SELECT id FROM Author WHERE name = ?")) {
                query.setString(1, authorName);

                var result = query.executeQuery();

                int authorId = -1;

                if (result.next()) {
                    authorId = result.getInt("id");
                } else {
                    try (var insertQuery = db.prepareStatement(
                            "INSERT INTO Author (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
                        insertQuery.setString(1, authorName);
                        insertQuery.executeUpdate();

                        var generatedKeys = insertQuery.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            authorId = generatedKeys.getInt(1);
                        }
                    }
                }

                db.commit();
                return authorId;
            }
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    public Author selectAuthor(int id) throws Exception {
        try {
            Author author = selectById(id);
            if (author == null) {
                throw new Exception("Author not found");
            }

            return author;
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public Author selectAuthorByName(String authorName) throws Exception {
        Author author = new Author();
        try {
            db.setAutoCommit(false);
            try (var selectAuthorQuery = db.prepareStatement(
                    "SELECT * FROM Author WHERE name = ?")) {
                selectAuthorQuery.setString(1, authorName);
                var result = selectAuthorQuery.executeQuery();
                while (result.next()) {
                    author = populate(result);
                }
                db.commit();
            }
            return author;
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void updateAuthorAttributeById(int authorId, String attr, Object value) throws Exception {
        try {
            updateById(authorId, attr, value);
        } catch (Exception e) {
            db.rollback();

            if (e.getMessage().equals("Entity not found")) {
                throw new Exception("Author not found");
            }

            throw e;
        }
    }

    @Override
    public void showAuthor(int id) throws Exception {
        try {
            var author = this.selectById(id);

            if (author == null || author.isHidden == false) {
                return;
            }

            db.setAutoCommit(false);

            try (PreparedStatement preparedStatement = db.prepareStatement(""
                    + "update book set hiddenParentCount = hiddenParentCount - 1 "
                    + "where authorId = ?"
            )) {
                preparedStatement.setInt(1, id);

                preparedStatement.executeUpdate();
            }

            db.commit();

            updateById(id, "isHidden", false);
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void hideAuthor(int id) throws Exception {
        try {
            var author = this.selectById(id);

            if (author == null || author.isHidden == true) {
                return;
            }

            db.setAutoCommit(false);

            try (PreparedStatement preparedStatement = db.prepareStatement(""
                    + "update book set hiddenParentCount = hiddenParentCount + 1 "
                    + "where authorId = ?"
            )) {
                preparedStatement.setInt(1, id);

                preparedStatement.executeUpdate();
            }

            db.commit();

            updateById(id, "isHidden", true);
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void updateAuthor(Author author, Author updatedAuthor) throws Exception {
        try {
            update(updatedAuthor, "name", "overview");
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void insertAuthor(Author author) throws Exception {
        try {
            this.insert(author, "name", "overview", "isHidden");
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public List<Author> selectSearchSortFilterAuthors(
            int offset, int limit, Map<Integer, SortOrder> sortValue,
            String searchString
    ) throws Exception {
        List<Author> result = new ArrayList<>();

        try {
            db.setAutoCommit(false);

            String stringQuery = "SELECT * FROM Author";

            stringQuery += " WHERE LOWER(name) LIKE LOWER(?) ";

            for (Map.Entry<Integer, SortOrder> entry : sortValue.entrySet()) {
                Integer key = entry.getKey();
                SortOrder value = entry.getValue();

                var sortKeys = new ArrayList<String>(List.of(
                        " ORDER BY Author.name "
                ));

                var sortValues = new HashMap<SortOrder, String>();
                sortValues.put(SortOrder.ASCENDING, " ASC ");
                sortValues.put(SortOrder.DESCENDING, " DESC ");

                stringQuery += sortKeys.get(key);
                stringQuery += sortValues.get(value);
            }

            stringQuery += " OFFSET ? LIMIT ? ";

            try (PreparedStatement preparedStatement = db.prepareStatement(stringQuery)) {
                int parameterIndex = 1;
                preparedStatement.setString(parameterIndex++, "%" + searchString + "%");

                preparedStatement.setInt(parameterIndex++, offset);
                preparedStatement.setInt(parameterIndex++, limit);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        result.add(populate(resultSet));
                    }
                }
            }

            db.commit();

            return result;
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }
}

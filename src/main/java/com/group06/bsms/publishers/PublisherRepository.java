package com.group06.bsms.publishers;

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

public class PublisherRepository extends Repository<Publisher> implements PublisherDAO {

    public PublisherRepository(Connection db) {
        super(db, Publisher.class);
    }

    @Override
    public List<Publisher> selectAllPublishers() throws Exception {
        try {
            var publishers = selectAll(
                    null,
                    0, null,
                    "name", Sort.ASC,
                    "name", "id", "email",
                    "address", "isHidden");

            return publishers;
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public int insertPublisherIfNotExists(String publisherName) throws Exception {
        try {
            db.setAutoCommit(false);

            try (var query = db.prepareStatement(
                    "SELECT id FROM Publisher WHERE name = ?")) {
                query.setString(1, publisherName);

                var result = query.executeQuery();

                int publisherId = -1;

                if (result.next()) {
                    publisherId = result.getInt("id");
                } else {
                    try (var insertQuery = db.prepareStatement(
                            "INSERT INTO Publisher (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
                        insertQuery.setString(1, publisherName);
                        insertQuery.executeUpdate();

                        var generatedKeys = insertQuery.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            publisherId = generatedKeys.getInt(1);
                        }
                    }
                }

                db.commit();
                return publisherId;
            }
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    public Publisher selectPublisher(int id) throws Exception {
        try {
            Publisher publisher = selectById(id);
            if (publisher == null) {
                throw new Exception("Publisher not found");
            }

            return publisher;
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public Publisher selectPublisherByName(String publisherName) throws Exception {
        Publisher publisher = new Publisher();
        try {
            db.setAutoCommit(false);
            try (var selectPublisherQuery = db.prepareStatement(
                    "SELECT * FROM Publisher WHERE name = ?")) {
                selectPublisherQuery.setString(1, publisherName);

                var result = selectPublisherQuery.executeQuery();
                while (result.next()) {
                    publisher = populate(result);
                }

                db.commit();
            }
            return publisher;
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void updatePublisherAttributeById(int publisherId, String attr, Object value) throws Exception {
        try {
            updateById(publisherId, attr, value);
        } catch (Exception e) {
            db.rollback();

            if (e.getMessage().equals("Entity not found")) {
                throw new Exception("Publisher not found");
            }

            throw e;
        }
    }

    @Override
    public void showPublisher(int id) throws Exception {
        try {
            var publisher = this.selectById(id);

            if (publisher == null || publisher.isHidden == false) {
                return;
            }

            db.setAutoCommit(false);

            try (PreparedStatement preparedStatement = db.prepareStatement(""
                    + "update book set hiddenParentCount = hiddenParentCount - 1 "
                    + "where publisherId = ?"
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
    public void hidePublisher(int id) throws Exception {
        try {
            var publisher = this.selectById(id);

            if (publisher == null || publisher.isHidden == true) {
                return;
            }

            db.setAutoCommit(false);

            try (PreparedStatement preparedStatement = db.prepareStatement(""
                    + "update book set hiddenParentCount = hiddenParentCount + 1 "
                    + "where publisherId = ?"
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
    public void updatePublisher(Publisher publisher, Publisher updatedPublisher) throws Exception {
        try {
            update(updatedPublisher, "name", "email", "address");
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void insertPublisher(Publisher publisher) throws Exception {
        try {
            this.insert(publisher, "name", "email", "address", "isHidden");
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public List<Publisher> selectSearchSortFilterPublishers(
            int offset, int limit, Map<Integer, SortOrder> sortValue,
            String searchString
    ) throws Exception {
        List<Publisher> result = new ArrayList<>();

        try {
            db.setAutoCommit(false);

            String stringQuery = "SELECT * FROM Publisher";

            stringQuery += " WHERE name LIKE ? ";

            for (Map.Entry<Integer, SortOrder> entry : sortValue.entrySet()) {
                Integer key = entry.getKey();
                SortOrder value = entry.getValue();

                var sortKeys = new ArrayList<String>(List.of(
                        " ORDER BY Publisher.name ",
                        " ORDER BY Publisher.email ",
                        " ORDER BY Publisher.address "
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

package com.group06.bsms;

import java.sql.Connection;
import java.sql.ResultSet;
import static java.sql.Types.NULL;
import java.util.ArrayList;
import java.util.List;

import org.jfree.data.json.impl.JSONObject;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Repository<Entity extends Object> {

    public static enum Sort {
        ASC,
        DESC
    }

    protected final Connection db;
    private final Class<?> entityClass;

    public Repository(Connection db, Class<?> entityClass) {
        this.db = db;
        this.entityClass = entityClass;
    }

    public int count() throws Exception {

        try {
            db.setAutoCommit(false);

            var query = db.prepareStatement(
                    "select count(*) from " + entityClass.getSimpleName()
            );

            var result = query.executeQuery();

            db.commit();

            if (!result.next()) {
                throw new Exception("Interal database error");
            }

            return result.getInt(1);
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    public void deleteById(int id) throws Exception {

        try {
            db.setAutoCommit(false);

            var query = db.prepareStatement(
                    "delete from " + entityClass.getSimpleName() + " "
                    + "where id = ?"
            );

            query.setInt(1, id);

            var result = query.executeUpdate();

            db.commit();

            if (result == 0) {
                throw new Exception("Entity not found");
            }
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    public boolean existsById(int id) throws Exception {

        try {
            db.setAutoCommit(false);

            var query = db.prepareStatement(
                    "select * from " + entityClass.getSimpleName() + " "
                    + "where id = ?"
            );

            query.setInt(1, id);

            var result = query.executeQuery();

            db.commit();

            return result.next();
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    /**
     *
     * @param jsonSearchString optional (null if none)
     * example: "{attribute1: value1, attribute2: value2}" temporarily only string
     * @param start 
     * @param count
     * @param sortAttr optional (null if none)
     * @param sortTerm Sort.ASC or Sort.DESC
     * @param attributes
     * @return [count] entity from the [start]'th entity of "select [attributes]
     * from entity where [searchAttr] like '%[searchTerm]%' order by [sortAttr]
     * [sortTerm]"
     */
    public List<Entity> selectAll1(
            String jsonSearchString,
            int start, int count,
            String sortAttr, Sort sortTerm,
            String... attributes
    ) throws Exception {

        try {
            db.setAutoCommit(false);

            if (!isValidIdentifier(sortAttr)) {
                throw new Exception("Invalid sort attribute");
            }

            var attributesQuery = new StringBuilder();

            for (var attribute : attributes) {
                if (!isValidIdentifier(attribute)) {
                    throw new Exception("Invalid select attribute: '" + attribute + "'");
                }

                attributesQuery.append(attribute).append(", ");
            }

            var searchQuery = new StringBuilder();

            if (jsonSearchString != null) {
                searchQuery.append("where");

                jsonSearchString.replaceAll(".*\".*", "\\\"");
                JsonObject json = new JsonParser().parse(jsonSearchString).getAsJsonObject();

                for (String key : json.keySet()) {
                    if (!isValidIdentifier(key)) {
                        throw new Exception("Invalid search attribute");
                    }
                    if (searchQuery.length() > 0) {
                        searchQuery.append(" and ");
                        searchQuery.append(key).append(" ilike '").append(json.get(key)).append("'");
                    }
                }
            }

            var sortQuery = "";

            if (sortAttr != null) {
                sortQuery = "order by " + sortAttr + " " + sortTerm.toString();
            }

            attributesQuery.setLength(attributesQuery.length() - 2);

            var query = db.prepareStatement(
                    "select " + attributesQuery + " "
                    + "from " + entityClass.getSimpleName() + " "
                    + searchQuery + " "
                    + sortQuery 
                    + " limit ? offset ?"
            );

            query.setInt(1, count);
            query.setInt(2, start);

            var resultSet = query.executeQuery();
            var result = new ArrayList<Entity>();

            db.commit();

            while (resultSet.next()) {
                result.add(populate(resultSet));
            }

            return result;
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }
    /**
     *
     * @param searchAttr
     * @param searchTerm
     * @param start
     * @param count
     * @param sortAttr
     * @param sortTerm
     * @param attributes
     * @return [count] entity from the [start]'th entity of "select [attributes]
     * from entity where [searchAttr] like '%[searchTerm]%' order by [sortAttr]
     * [sortTerm]"
     */
    public List<Entity> selectAll(
            String searchAttr, Object searchTerm,
            int start, int count,
            String sortAttr, Sort sortTerm,
            String... attributes
    ) throws Exception {

        try {
            db.setAutoCommit(false);

            if (!isValidIdentifier(sortAttr)) {
                throw new Exception("Invalid sort attribute");
            }

            if (!isValidIdentifier(searchAttr)) {
                throw new Exception("Invalid search attribute");
            }

            var attributesQuery = new StringBuilder();

            for (var attribute : attributes) {
                if (!isValidIdentifier(attribute)) {
                    throw new Exception("Invalid select attribute");
                }

                attributesQuery.append(attribute).append(", ");
            }

            attributesQuery.setLength(attributesQuery.length() - 2);

            var query = db.prepareStatement(
                    "select " + attributesQuery + " "
                    + "from " + entityClass.getSimpleName() + " "
                    + "where " + searchAttr + " ilike ? "
                    + "order by " + sortAttr + " "
                    + sortTerm.toString() + " limit ? offset ?"
            );

            query.setObject(1, searchTerm);
            query.setInt(2, count);
            query.setInt(3, start);

            var resultSet = query.executeQuery();
            var result = new ArrayList<Entity>();

            db.commit();

            while (resultSet.next()) {
                result.add(populate(resultSet));
            }

            return result;
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }
    

    public Entity selectById(int id) throws Exception {

        try {
            db.setAutoCommit(false);

            var query = db.prepareStatement(
                    "select * from " + entityClass.getSimpleName() + " "
                    + "where id =  ?"
            );

            query.setInt(1, id);

            var result = query.executeQuery();

            db.commit();

            return result.next() ? populate(result) : null;
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    public Entity insert(Entity entity, String... attributes) throws Exception {
        try {
            db.setAutoCommit(false);

            var attributesQuery = new StringBuilder(
                    "insert into " + entityClass.getSimpleName() + " ("
            );
            var valuesQuery = new StringBuilder(") values (");

            for (var attribute : attributes) {
                if (!isValidIdentifier(attribute)) {
                    throw new Exception("invalid select attribute");
                }

                attributesQuery.append(attribute).append(", ");
                valuesQuery.append("?, ");
            }

            attributesQuery.setLength(attributesQuery.length() - 2);
            valuesQuery.setLength(valuesQuery.length() - 2);

            var query = db.prepareStatement(
                    attributesQuery.append(valuesQuery).append(")").toString()
            );

            int index = 1;

            for (String attribute : attributes) {
                var field = entityClass.getDeclaredField(attribute);

                field.setAccessible(true);

                var value = field.get(entity);

                if (value != null) {
                    query.setObject(index++, value);
                } else {
                    query.setNull(index++, NULL);
                }

                field.setAccessible(false);
            }

            var result = query.executeUpdate();

            db.commit();

            if (result == 0) {
                throw new Exception("Internal database error");
            }

            return entity;
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    public void updateById(int id, String attr, String value)
            throws Exception {

        try {
            db.setAutoCommit(false);

            if (!isValidIdentifier(attr)) {
                throw new Exception("invalid set attribute");
            }

            var query = db.prepareStatement(
                    "update " + entityClass.getSimpleName() + " "
                    + "set " + attr + " = ? where id = ?"
            );

            query.setString(1, value);
            query.setInt(2, id);

            var result = query.executeUpdate();

            db.commit();

            if (result == 0) {
                throw new Exception("Entity not found");
            }
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    public Entity populate(ResultSet result)
            throws Exception {

        Entity entity = (Entity) entityClass.getDeclaredConstructor().newInstance();

        for (var field : entityClass.getDeclaredFields()) {
            try {
                var value = result.getObject(field.getName());

                field.setAccessible(true);

                field.set(entity, value);

                field.setAccessible(false);
            } catch (Exception e) {

            }
        }

        return entity;
    }

    public boolean isValidIdentifier(String identifier) {
        return identifier.matches("^[a-zA-Z_][a-zA-Z0-9_]{0,62}$");
    }
}

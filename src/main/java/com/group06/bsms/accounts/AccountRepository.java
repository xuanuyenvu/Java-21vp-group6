package com.group06.bsms.accounts;

import com.group06.bsms.revenues.Revenue;
import java.sql.Date;
import java.sql.Connection;

import com.group06.bsms.Repository;
import com.group06.bsms.auth.Hasher;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;

public class AccountRepository extends Repository<Account> implements AccountDAO {

    public AccountRepository(Connection db) {
        super(db, Account.class);

    }

    @Override
    public List<Account> selectTop10EmployeesWithHighestRevenue(Map<Integer, SortOrder> sortAttributeAndOrder,
            Date startDate, Date endDate) throws Exception {
        List<Account> result = new ArrayList<>();
        try {
            db.setAutoCommit(false);
            String stringQuery = """
                                 SELECT top_10.*
                                 FROM
                                     (SELECT Account.*,
                                      COALESCE(SUM(OrderedBook.pricePerbook * OrderedBook.quantity), 0) AS revenue,
                                 	 COALESCE(SUM(OrderedBook.quantity), 0) AS saleQuantity
                                      FROM Account
                                      JOIN OrderSheet ON Account.id = OrderSheet.employeeInchargeId
                                      JOIN OrderedBook ON OrderedBook.orderSheetId = OrderSheet.id
                                      WHERE orderDate BETWEEN ? AND ?
                                      GROUP BY Account.id
                                      ORDER BY revenue DESC
                                      LIMIT 10) AS top_10\n
                                 """;

            for (Map.Entry<Integer, SortOrder> entry : sortAttributeAndOrder.entrySet()) {
                Integer attribute = entry.getKey();
                SortOrder sortOrder = entry.getValue();

                var sortAttributes = new ArrayList<String>(List.of(
                        " ORDER BY name ",
                        " ORDER BY email ",
                        " ORDER BY phone ",
                        " ORDER BY address ",
                        " ORDER BY gender ",
                        " ORDER BY saleQuantity ",
                        " ORDER BY revenue "));

                var sortOrders = new HashMap<SortOrder, String>();
                sortOrders.put(SortOrder.ASCENDING, " ASC ");
                sortOrders.put(SortOrder.DESCENDING, " DESC ");

                stringQuery += sortAttributes.get(attribute);
                stringQuery += sortOrders.get(sortOrder);
            }
            try (PreparedStatement preparedStatement = db.prepareStatement(stringQuery)) {
                preparedStatement.setDate(1, startDate);
                preparedStatement.setDate(2, endDate);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        var account = populate(resultSet);
                        account.revenue = new Revenue(
                                resultSet.getDouble("revenue"),
                                resultSet.getInt("saleQuantity")
                        );
                        result.add(account);
                    }
                }
                db.commit();
            }
            return result;
        } catch (Exception e) {
            db.rollback();
            if (e.getMessage().equals("Entity not found")) {
                throw new Exception("Employee not found");
            }
            throw e;
        }
    }

    @Override
    public List<Account> selectAllAccounts() throws Exception {
        try {
            var accounts = selectAll(
                    null,
                    0, null,
                    "name", Sort.ASC,
                    "name", "id", "email",
                    "address", "isLocked", "phone",
                    "gender", "isAdmin"
            );

            return accounts;
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    public Account selectAccount(int id) throws Exception {
        try {
            Account account = selectById(id);
            if (account == null) {
                throw new Exception("Account not found");
            }

            return account;
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public Account selectAccountByName(String accountName) throws Exception {
        Account account = new Account();
        try {
            db.setAutoCommit(false);
            try (var selectAccountQuery = db.prepareStatement(
                    "SELECT * FROM Account WHERE name = ?")) {
                selectAccountQuery.setString(1, accountName);

                var result = selectAccountQuery.executeQuery();
                while (result.next()) {
                    account = populate(result);
                }

                db.commit();
            }
            return account;
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void updateAccountAttributeById(int accountId, String attr, Object value) throws Exception {
        try {
            updateById(accountId, attr, value);
        } catch (Exception e) {
            db.rollback();

            if (e.getMessage().equals("Entity not found")) {
                throw new Exception("Account not found");
            }

            throw e;
        }
    }

    @Override
    public void unlockAccount(int id) throws Exception {
        try {
            updateById(id, "isLocked", false);
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void lockAccount(int id) throws Exception {
        try {
            updateById(id, "isLocked", true);
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void updateAccount(Account account, AccountWithPassword updatedAccount) throws Exception {
        try {
            this.update(
                    updatedAccount,
                    "name", "email",
                    "address", "isLocked", "phone",
                    "gender", "isAdmin"
            );

            if (updatedAccount.password != null) {
                this.updateById(
                        updatedAccount.id,
                        "password",
                        Hasher.encryptPassword(updatedAccount.password)
                );
            }
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void insertAccount(AccountWithPassword account) throws Exception {
        try {
            db.setAutoCommit(false);

            try (var query = db.prepareStatement(
                    """
                            insert into Account(
                                name, email, password, address, isLocked, phone,
                                gender, isAdmin
                            ) values (
                                ?, ?, ?, ?, ?, ?, ?, ?
                            )
                        """
            )) {
                int index = 1;
                query.setString(index++, account.name);
                query.setString(index++, account.email);
                query.setString(index++, Hasher.encryptPassword(account.password));
                query.setString(index++, account.address);
                query.setBoolean(index++, account.isLocked);
                query.setString(index++, account.phone);
                query.setString(index++, account.gender);
                query.setBoolean(index++, account.isAdmin);

                var result = query.executeUpdate();

                db.commit();

                if (result == 0) {
                    throw new Exception("Internal database error");
                }
            }
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public List<Account> selectSearchSortFilterAccounts(
            int offset, int limit, Map<Integer, SortOrder> sortValue,
            String searchString, String searchChoice
    ) throws Exception {
        List<Account> result = new ArrayList<>();

        try {
            db.setAutoCommit(false);

            String stringQuery = "SELECT * FROM Account";

            stringQuery += " WHERE LOWER(" + searchChoice + ") LIKE LOWER(?) ";

            for (Map.Entry<Integer, SortOrder> entry : sortValue.entrySet()) {
                Integer key = entry.getKey();
                SortOrder value = entry.getValue();

                var sortKeys = new ArrayList<String>(List.of(
                        " ORDER BY Account.phone ",
                        " ORDER BY Account.name ",
                        " ORDER BY Account.email ",
                        " ORDER BY Account.address ",
                        " ORDER BY Account.gender ",
                        " ORDER BY Account.isAdmin "
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

    public boolean checkPasswordById(int id, String password) throws Exception {
        try {
            db.setAutoCommit(false);

            var query = db.prepareStatement(
                    "SELECT password FROM Account WHERE id = ?"
            );

            query.setInt(1, id);

            var result = query.executeQuery();

            db.commit();

            if (result.next()) {
                String hashedPassword = result.getString("password");
                return Hasher.checkPassword(password, hashedPassword);
            } else {
                throw new Exception("Account not found");
            }
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    public void updatePasswordById(int id, String password) throws Exception {

        try {
            db.setAutoCommit(false);

            updateById(id, "password", Hasher.encryptPassword(password));

            db.commit();
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }
}

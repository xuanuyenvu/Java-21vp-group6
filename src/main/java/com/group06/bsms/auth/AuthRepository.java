package com.group06.bsms.auth;

import com.group06.bsms.Repository;
import com.group06.bsms.accounts.Account;
import java.sql.Connection;
import java.sql.Statement;

public class AuthRepository extends Repository<Account> implements AuthDAO {

    public AuthRepository(Connection db) {
        super(db, Account.class);
    }

    @Override
    public Account selectAccountByCredentials(String phone, String password)
            throws Exception {

        try {
            db.setAutoCommit(false);

            var query = db.prepareStatement(
                    "select * from Account where phone = ?"
            );

            query.setString(1, phone);

            var result = query.executeQuery();

            db.commit();

            if (result.next() && Hasher.checkPassword(
                    password,
                    result.getString("password")
            )) {
                return populate(result);
            }

            return null;

        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void updateAccountPassword(
            String phone, String oldPassword, String newPassword)
            throws Exception {

        if ("".equals(newPassword)) {
            throw new IllegalArgumentException("New password cannot be empty");
        }

        if (selectAccountByCredentials(phone, oldPassword) == null) {
            throw new Exception("Account not found");
        }

        try {
            db.setAutoCommit(false);

            var query = db.prepareStatement("""
                update Account set password = ? where phone = ?
            """);

            query.setString(1, Hasher.encryptPassword(newPassword));
            query.setString(2, phone);

            var result = query.executeUpdate();

            db.commit();

            if (result == 0) {
                throw new Exception("Account not found");
            }
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public boolean existsAccounts() throws Exception {
        return this.count() > 0;
    }

    @Override
    public int insertAccount(String phone, String password, boolean isAdmin, boolean isLocked) throws Exception {
        try {
            db.setAutoCommit(false);

            try (var query = db.prepareStatement(
                    "insert into Account(phone, password, isAdmin, isLocked) values (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                query.setString(1, phone);
                query.setString(2, Hasher.encryptPassword(password));
                query.setBoolean(3, isAdmin);
                query.setBoolean(4, isLocked);

                var result = query.executeUpdate();

                db.commit();

                if (result == 0) {
                    throw new Exception("Internal database error");
                }

                var generatedKeys = query.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new Exception("Unable to get user id");
                }
            }
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }
}

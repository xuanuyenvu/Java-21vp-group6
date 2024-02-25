package com.group06.bsms.auth;

import com.group06.bsms.Repository;
import com.group06.bsms.accounts.Account;
import java.sql.Connection;

public class AuthRepository extends Repository<Account> implements AuthDAO {

    public AuthRepository(Connection db) {
        super(db, Account.class);
    }

    @Override
    public boolean existsAccountByCredentials(String phone, String password)
            throws Exception {

        try {
            db.setAutoCommit(false);

            var query = db.prepareStatement(
                    "select * from Account where phone = ? and password = ?"
            );

            query.setString(1, phone);
            query.setString(2, password);

            var result = query.executeQuery();

            db.commit();

            return result.next();
        } catch (Exception e) {
            db.rollback();
            throw e;
        }
    }

    @Override
    public void updateAccountPassword(
            String phone, String oldPassword, String newPassword)
            throws Exception {

        try {
            db.setAutoCommit(false);

            var query = db.prepareStatement("""
                update Account set password = ?
                where phone = ? and password = ?
            """);

            query.setString(1, newPassword);
            query.setString(2, phone);
            query.setString(3, oldPassword);

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
}

package com.group06.bsms.auth;

import com.group06.bsms.accounts.Account;

public interface AuthDAO {

    Account selectAccountByCredentials(String phone, String password)
            throws Exception;

    void updateAccountPassword(String phone, String oldPassword, String newPassword)
            throws Exception;

    boolean existsAccounts() throws Exception;

    void insertAccount(String phone, String password, boolean isAdmin, boolean isLocked) throws Exception;
}

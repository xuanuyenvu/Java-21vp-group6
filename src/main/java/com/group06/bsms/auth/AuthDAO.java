package com.group06.bsms.auth;

public interface AuthDAO {

    boolean existsAccountByCredentials(String phone, String password)
            throws Exception;

    void updateAccountPassword(String phone, String oldPassword, String newPassword)
            throws Exception;
}

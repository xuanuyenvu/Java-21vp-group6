package com.group06.bsms.auth;

public class AuthService {

    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public boolean authenticate(String phone, String password) throws Exception {
        if ("".equals(phone)) {
            throw new IllegalArgumentException("Phone cannot be empty");
        }

        if ("".equals(password)) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (isFirstLogin()) {
            try {
                authDAO.insertAccount(phone, password, true, false);
            } catch (Exception e) {
                if (e.getMessage().contains("account_phone_check")) {
                    throw new Exception("Invalid phone format");
                } else if (e.getMessage().contains("account_phone_key")) {
                    throw new Exception("An account with this phone already exists");
                } else {
                    throw e;
                }
            }

            return true;
        } else {
            var account = authDAO.selectAccountByCredentials(phone, password);

            if (account == null) {
                throw new Exception("Incorrect phone or password");
            }

            if (account.isLocked) {
                throw new Exception("Account is locked");
            }

            return account.isAdmin;
        }
    }

    public boolean isFirstLogin() throws Exception {
        return !authDAO.existsAccounts();
    }
}

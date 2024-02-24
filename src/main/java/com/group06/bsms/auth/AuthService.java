package com.group06.bsms.auth;

public class AuthService {

    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public boolean authenticate(String phone, String password) throws Exception {
        if (phone == null || password == null) {
            return false;
        }

        return authDAO.existsAccountByCredentials(phone, password);
    }
}

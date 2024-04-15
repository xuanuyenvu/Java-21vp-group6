package com.group06.bsms.accounts;

import java.util.Objects;

public class AccountWithPassword extends Account {

    public String password;

    public AccountWithPassword() {
    }

    public AccountWithPassword(
            int id,
            String phone, String password, String email,
            String address, String name, String gender,
            boolean isAdmin, boolean isLocked
    ) {
        super(id, phone, email, address, name, gender, isAdmin, isLocked);
        this.password = password;
    }

    public AccountWithPassword(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        final AccountWithPassword other = (AccountWithPassword) obj;

        return Objects.equals(this.password, other.password);
    }
}

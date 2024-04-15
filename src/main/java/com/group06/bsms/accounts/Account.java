package com.group06.bsms.accounts;

import com.group06.bsms.revenues.Revenue;
import java.util.Objects;

public class Account {

    public int id;
    public String phone;
    public String email;
    public String address;
    public String name;
    public String gender;
    public boolean isAdmin;
    public boolean isLocked;
    public Revenue revenue;

    public Account() {
    }

    public Account(
            int id,
            String phone, String email,
            String address, String name, String gender,
            boolean isAdmin, boolean isLocked
    ) {
        this.id = id;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.name = name;
        this.gender = gender;
        this.isAdmin = isAdmin;
        this.isLocked = isLocked;
    }

    public Account(String phone, String email) {
        this.phone = phone;
        this.email = email;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Account other = (Account) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.isAdmin != other.isAdmin) {
            return false;
        }
        if (this.isLocked != other.isLocked) {
            return false;
        }
        if (!Objects.equals(this.phone, other.phone)) {
            return false;
        }
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        if (!Objects.equals(this.address, other.address)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.gender, other.gender);
    }

}

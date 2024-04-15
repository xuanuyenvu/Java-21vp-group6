package com.group06.bsms.accounts;

import com.group06.bsms.revenues.Revenue;

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

    public Account(int id, String phone, String email, String address, String name, String gender, boolean isAdmin, boolean isLocked) {
        this.id = id;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.name = name;
        this.gender = gender;
        this.isAdmin = isAdmin;
        this.isLocked = isLocked;
    }

    @Override
    public String toString() {
        return "Account{" + "id=" + id + ", phone=" + phone + ", email=" + email + ", address=" + address + ", name=" + name + ", gender=" + gender + ", isAdmin=" + isAdmin + ", isLocked=" + isLocked + '}';
    }
}

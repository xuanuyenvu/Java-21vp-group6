package com.group06.bsms.order.member;

import java.sql.Date;

public class Member {
    public int id;
    public String phone;
    public String name;
    public String gender;
    public Date dateOfBirth;
    public String email;
    public String address;

    public Member(int id, String phone, String name, String gender, Date dateOfBirth, String email, String address) {
        this.id = id;
        this.phone = phone;
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.address = address;
    }

    public Member(String phone, String name, String gender, Date dateOfBirth, String email, String address) {
        this.phone = phone;
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.address = address;
    }

    @Override
    public String toString() {
        return "Member [id=" + id + ", phone=" + phone + ", name=" + name + ", gender=" + gender + ", dateOfBirth="
                + dateOfBirth + ", email=" + email + ", address=" + address + "]";
    }

    

    
}

package com.group06.bsms.members;

import com.group06.bsms.revenues.Revenue;
import java.util.Date;

public class Member {

    public int id;
    public String phone;
    public String email;
    public String address;
    public String name;
    public String gender;
    public Date dateOfBirth;
    public Revenue revenue;

    public Member() {
    }

    public Member(int id, String phone, String email, String address, String name, String gender, Date dateOfBirth, Revenue revenue) {
        this.id = id;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.revenue = revenue;
    }

    @Override
    public String toString() {
        return "Member{" + "id=" + id + ", phone=" + phone + ", email=" + email + ", address=" + address + ", name=" + name + ", gender=" + gender + ", dateOfBirth=" + dateOfBirth + ", revenue=" + revenue + '}';
    }
}

package com.group06.bsms.publishers;

public class Publisher {
    public int id;
    public String name;
    public String email;
    public String address;
    public boolean isHidden;

    public Publisher(int id, String name, String email, String address, boolean isHidden) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.isHidden = isHidden;
    }

    public Publisher(int id, String name, String email, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
    }

    @Override
    public String toString() {
        return "Publisher [id=" + id + ", name=" + name + ", email=" + email + ", address=" + address + ", isHidden="
                + isHidden + "]";
    }

    
}

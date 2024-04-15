package com.group06.bsms.publishers;

public class Publisher {

    public int id;
    public String name;
    public String email;
    public String address;
    public boolean isHidden;

    public Publisher() {
    }

    public Publisher(String name) {
        this.name = name;
    }

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
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Publisher other = (Publisher) obj;
        return (id == other.id
                && name.equals(other.name)
                && email.equals(other.email)
                && address.equals(other.address)
                && isHidden == other.isHidden);
    }
}

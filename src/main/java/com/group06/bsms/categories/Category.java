package com.group06.bsms.categories;

public class Category {
    public int id;
    public String name;
    public boolean isHidden;

    public Category() {

    }

    public Category(int id, String name, boolean isHidden) {
        this.id = id;
        this.name = name;
        this.isHidden = isHidden;
    }

}
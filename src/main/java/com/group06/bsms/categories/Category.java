package com.group06.bsms.categories;

public class Category {
    public String id;
    public String name;
    public String isHidden;

    public Category() {

    }

    public Category(String id, String name, String isHidden) {
        this.id = id;
        this.name = name;
        this.isHidden = isHidden;
    }

}
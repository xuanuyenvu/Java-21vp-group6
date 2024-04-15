package com.group06.bsms.categories;

import com.group06.bsms.revenues.Revenue;

public class Category {

    public int id;
    public String name;
    public boolean isHidden;
    public Revenue revenue;

    public Category() {
    }

    public Category(int id, String name, boolean isHidden) {
        this.id = id;
        this.name = name;
        this.isHidden = isHidden;
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
        Category other = (Category) obj;
        return (id == other.id && name.equals(other.name) && isHidden == other.isHidden);
    }

}

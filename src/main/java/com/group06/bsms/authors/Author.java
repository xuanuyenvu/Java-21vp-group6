package com.group06.bsms.authors;

public class Author {
    public int id;
    public String name;
    public String overview;
    public boolean isHidden;

    public Author() {
    }

    public Author(int id, String name, String overview, boolean isHidden) {
        this.id = id;
        this.name = name;
        this.overview = overview;
        this.isHidden = isHidden;
    }

    public Author(int id, String name, String overview) {
        this.id = id;
        this.name = name;
        this.overview = overview;
        this.isHidden = false;
    }

    @Override
    public String toString() {
        return "Author [id=" + id + ", name=" + name + ", overview=" + overview + ", isHidden=" + isHidden + "]";
    }
}

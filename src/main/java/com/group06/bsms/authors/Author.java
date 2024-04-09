package com.group06.bsms.authors;

public class Author {

    public int id;
    public String name;
    public String overview;
    public boolean isHidden;

    public Author() {
    }

    public Author(String name) {
        this.name = name;
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
        Author other = (Author) obj;
        return (id == other.id
                && name.equals(other.name)
                && overview.equals(other.overview)
                && isHidden == other.isHidden);
    }
}

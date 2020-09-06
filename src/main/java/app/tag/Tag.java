package app.tag;

import app.bookmark.Bookmark;

import java.util.List;
import java.util.Objects;


public class Tag {
    private int id;
    private String name;

    public Tag() {

    }

    public Tag(int Id, String Name) {
        this.id = Id;
        this.name = Name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Tag t = (Tag) obj;
        return id == t.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }
}

package model;

import java.util.HashSet;
import java.util.Set;

public class User {
    Long id = 0L;
    String name = "Аноним";
    Boolean logged = false;
    Set<String> tags = new HashSet<String>();

    public User() {
    }

    public boolean isLogged() {
        return logged;
    }

    public User setLogged(boolean logged) {
        this.logged = logged;
        return this;
    }

    public Set<String> getTags() {
        return tags;
    }

    public User setTags(Set<String> tags) {
        this.tags = tags;
        return this;
    }

    public User addTag(String tag) {
        this.tags.add(tag);
        return this;
    }

    public User removeTag(String tag) {
        this.tags.remove(tag);
        return this;
    }

    public long getId() {
        return id;
    }

    public User setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }
}

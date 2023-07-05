package ca.uwaterloo.cs446.journeytogether;

import java.io.Serializable;

public class User implements Serializable {
    private String id; // this should be used as the primary key
    private String name;

    public User() {}

    public User(String id){
        this.id = id;
        this.name = null;
    }

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
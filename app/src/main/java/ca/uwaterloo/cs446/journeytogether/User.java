package ca.uwaterloo.cs446.journeytogether;

import java.io.Serializable;

public class User implements Serializable {

    private String firstName;
    private String lastName;
    private String id; // this should be used as the primary key

    public User(String id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public boolean equals(User other) {
        return this.id.equals(other.id);
    }

    public String getDisplayName() {
        return String.format("%s %s", firstName, lastName);
    }
}
package ca.uwaterloo.ece452.journeytogether;

public class User {

    private String firstName;
    private String lastName;
    private String id; // this should be used as the primary key

    public User(String id) {
        this.id = id;
    }

    public boolean equals(User other) {
        return this.id.equals(other.id);
    }
}

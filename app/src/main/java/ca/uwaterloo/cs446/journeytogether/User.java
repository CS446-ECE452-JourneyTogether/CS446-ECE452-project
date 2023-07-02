package ca.uwaterloo.cs446.journeytogether;

import java.io.Serializable;

public class User implements Serializable {
    private String id; // this should be used as the primary key
    private String firstName;
    private String lastName;
    private String phoneNum;
    private String driverLicense;

    public User() {}

    public User(String id){
        this.id = id;
        this.firstName = null;
        this.lastName = null;
        this.phoneNum = null;
        this.driverLicense = null;
    }

    public User(String id, String firstName, String lastName, String phoneNum, String driverLicense) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNum = phoneNum;
        this.driverLicense = driverLicense;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + firstName + ' ' + lastName + '\'' +
                '}';
    }
}
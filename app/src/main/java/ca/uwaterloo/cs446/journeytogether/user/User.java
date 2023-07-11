package ca.uwaterloo.cs446.journeytogether.user;

import java.io.Serializable;

public class User implements Serializable {
    private String email; // this should be used as the primary key
    private String firstName;
    private String lastName;
    private Boolean isDriver;

    // this field will remain null for basically every user except for the user themselves
    private String phoneNum;
    private String driverLicense;

    public User(String id) {
        this.email = id;
        this.firstName = "";
        this.lastName = "";
        this.isDriver = false;
        this.phoneNum = "";
        this.driverLicense = "";
    }

    public User(String id, Boolean isDriver) {
        this.email = id;
        this.isDriver = isDriver;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getDriver() {
        return isDriver;
    }

    public void setDriver(Boolean driver) {
        isDriver = driver;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }
}
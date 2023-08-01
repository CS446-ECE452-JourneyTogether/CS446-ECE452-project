package ca.uwaterloo.cs446.journeytogether.schema;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import ca.uwaterloo.cs446.journeytogether.common.FirestoreCollection;

public class User implements Serializable {
    private String id; // this should be used as the primary key
    private String firstName;
    private String lastName;

    // this field will remain null for basically every user except for the user themselves
    private String email;
    private String phoneNum;

    // this field will remain null for basically every user except for the user themselves
    private String driverLicense;
    private boolean isDriver;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Add this field to store the IDs of collected trips
    private List<String> collectedTrips;

    private static final String COLLECTION_PATH = "jt_user";
    public static final FirestoreCollection<User> firestore =
            new FirestoreCollection<>(
                    COLLECTION_PATH,
                    (onto, document) -> { onto.update(document); },
                    (value) -> value.asMap(),
                    (onto, id) -> onto.setId(id),
                    () -> new User()
            );


    public void update(DocumentSnapshot document) {
        try {
            this.firstName = (String) document.get("firstName");
            this.lastName = (String) document.get("lastName");
            this.email = (String) document.get("email");
            this.phoneNum = (String) document.get("phoneNum");
            this.isDriver = (boolean) document.get("isDriver");
            this.collectedTrips = (List<String>) document.get("collectedTrips");

            if (this.isDriver) {
                this.driverLicense = (String) document.get("driverLicense");
            }

        } catch (ClassCastException e) {
            Log.e("E", String.format("Casting error occurred with User %s: %s", this.id, e.getMessage()));
        }
    }

    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("firstName", this.firstName);
        map.put("lastName", this.lastName);
        map.put("email", this.email);
        map.put("phoneNum", this.phoneNum);
        map.put("isDriver", this.isDriver);
        map.put("collectedTrips", this.collectedTrips);

        if (this.isDriver) {
            map.put("driverLicense", this.driverLicense);
        }

        return map;
    }

    private void setId(String id) {
        this.id = id;
    }

    public User() {}

    public User(String email){
        this.email = email;
        this.firstName = null;
        this.lastName = null;
        this.phoneNum = null;
        this.driverLicense = null;
        this.isDriver = false;
        this.collectedTrips = new ArrayList<>();
    }

    public List<String> getCollectedTrips() {
        return collectedTrips;
    }

    public void setCollectedTrips(List<String> collectedTrips) {
        this.collectedTrips = collectedTrips;
    }

    public User(String email, String firstName, String lastName, String phoneNum, boolean isDriver) {
        this.id = email;
        this.email =email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNum = phoneNum;
        this.driverLicense = null;
        this.isDriver = isDriver;
        this.collectedTrips = new ArrayList<>();
    }

    public User(String email, boolean isDriver) {
        this.email = email;
        this.isDriver = isDriver;
        this.collectedTrips = new ArrayList<>();
    }

    public User(String driverL,String email, String firstName, String lastName, String phoneNum, boolean isDriver) {
        this.id = email;
        this.email =email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNum = phoneNum;
        this.driverLicense = driverL;
        this.isDriver = isDriver;
        this.collectedTrips = new ArrayList<>();
    }

    public String getDocumentId() {
        return id;
    }

    public String getId() {
        return id;
    }

    public boolean getIsDriver() { return isDriver; }

    public String getDisplayName() { return String.format("%s %s", firstName, lastName); }

    public String getPhoneNum() {return String.format("%s",phoneNum);}

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + firstName + ' ' + lastName + '\'' +
                '}';
    }

    public static CompletableFuture<User> getUserByEmail(String email) {

        CompletableFuture<User> futureUser = new CompletableFuture<>();

        User.firestore.makeQuery(
                v -> v.whereEqualTo("email", email),
                arr -> {
                    if (arr.isEmpty()) {
                        futureUser.completeExceptionally(new Exception("Query failed"));
                        return;
                    }
                    futureUser.complete(arr.get(0));
                },
                () -> {
                    futureUser.completeExceptionally(new Exception("Query failed"));
                }
        );

        return futureUser;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
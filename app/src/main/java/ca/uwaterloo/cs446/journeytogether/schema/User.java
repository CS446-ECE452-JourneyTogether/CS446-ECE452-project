package ca.uwaterloo.cs446.journeytogether.schema;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import ca.uwaterloo.cs446.journeytogether.common.FirestoreCollection;

public class User implements Serializable {
    private String id; // this should be used as the primary key
    private String firstName;
    private String lastName;

    // this field will remain null for basically every user except for the user themselves
    private String phoneNum;

    // this field will remain null for basically every user except for the user themselves
    private String driverLicense;

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
            this.phoneNum = (String) document.get("phoneNum");
            this.driverLicense = (String) document.get("driverLicense");
        } catch (ClassCastException e) {
            Log.e("E", String.format("Casting error occurred with User %s: %s", this.id, e.getMessage()));
        }
    }

    public Map<String, Object> asMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("firstName", this.firstName);
        map.put("lastName", this.lastName);
        map.put("phoneNum", this.phoneNum);
        map.put("driverLicense", this.driverLicense);

        return map;
    }

    private void setId(String id) {
        this.id = id;
    }

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

    public String getDocumentId() {
        return id;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() { return String.format("%s %s", firstName, lastName); }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + firstName + ' ' + lastName + '\'' +
                '}';
    }
}
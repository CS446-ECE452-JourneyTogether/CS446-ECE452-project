package ca.uwaterloo.cs446.journeytogether.common;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.CompletableFuture;

import ca.uwaterloo.cs446.journeytogether.schema.User;

public class CurrentUser {
    static private FirebaseAuth mAuth;

    public static CompletableFuture<User> getCurrentUser() {
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentFirebaseUser = mAuth.getCurrentUser();

        CompletableFuture<User> futureUser = new CompletableFuture<>();
        User.firestore.makeQuery(
                v -> v.whereEqualTo("email", currentFirebaseUser.getEmail()),
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
}

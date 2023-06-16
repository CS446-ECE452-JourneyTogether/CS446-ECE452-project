package ca.uwaterloo.cs446.journeytogether;

public class UserSession {
    private static UserSession instance;
    private boolean isLoggedIn;

    private UserSession() {
        isLoggedIn = false;
    }

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }
}


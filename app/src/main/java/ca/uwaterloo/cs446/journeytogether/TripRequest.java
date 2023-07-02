package ca.uwaterloo.cs446.journeytogether;

public class TripRequest {

    private User driver;
    private User passenger;
    private int seatRequest;
    private boolean sharePhone;
    private String pickupAddr;
    private String comment;

    public TripRequest() {}

    public TripRequest(User driver, User passenger, int seatRequest, boolean sharePhone, String pickupAddr, String comment) {
        this.driver = driver;
        this.passenger = passenger;
        this.seatRequest = seatRequest;
        this.sharePhone = sharePhone;
        this.pickupAddr = pickupAddr;
        this.comment = comment;
    }

    // Getters and Setters for each property

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public User getPassenger() {
        return passenger;
    }

    public void setPassenger(User passenger) {
        this.passenger = passenger;
    }

    public int getSeatRequest() {
        return seatRequest;
    }

    public void setSeatRequest(int seatRequest) {
        this.seatRequest = seatRequest;
    }

    public boolean isSharePhone() {
        return sharePhone;
    }

    public void setSharePhone(boolean sharePhone) {
        this.sharePhone = sharePhone;
    }

    public String getPickupAddr() {
        return pickupAddr;
    }

    public void setPickupAddr(String pickupAddr) {
        this.pickupAddr = pickupAddr;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

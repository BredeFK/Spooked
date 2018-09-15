package gamejam.spooked.com.spooked;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Spook {
    private String userID;
    private double latitude;
    private double longitude;

    Spook(){
    }

    Spook(String userID, double latitude, double longitude){
        this.userID = userID;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUserID() {
        return userID;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

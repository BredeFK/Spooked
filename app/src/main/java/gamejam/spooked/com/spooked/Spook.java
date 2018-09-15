package gamejam.spooked.com.spooked;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Spook {
    private String userID;
    private double latitude;
    private double longitude;
    private int date;

    Spook(){
    }

    Spook(String userID, double latitude, double longitude, int date){
        this.userID = userID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
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

    public int getDate() {
        return date;
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

    public void setDate(int date) {
        this.date = date;
    }
}

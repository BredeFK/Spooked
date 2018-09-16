package gamejam.spooked.com.spooked;

public class User {
    private String uid;
    private String name;
    private String email;
    private double lastLatitude;
    private double lastLongitude;

    public User(){

    }

    public User(String uid, String name, String email, double latitude, double longitude){
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.lastLatitude = latitude;
        this.lastLongitude = longitude;
    }

    public User(String uid, String name, String email){
        this.uid = uid;
        this.name = name;
        this.email = email;
        lastLatitude = 100.0;
        lastLongitude = 100.0;
    }

    public void updateLastPos(double lastLatitude, double lastLongitude){
        this.lastLatitude = lastLatitude;
        this.lastLongitude = lastLongitude;
    }

    public String getUid() {
        return uid;
    }

    public String getName(){
        return name;
    }

    public String getEmail() {
        return email;
    }

    public double getLastLatitude() {
        return lastLatitude;
    }

    public double getLastLongitude(){
        return lastLongitude;
    }

    public boolean hasLocation(){
        return (lastLongitude != 100 && lastLatitude != 100);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLastLatitude(double lastLatitude) {
        this.lastLatitude = lastLatitude;
    }

    public void setLastLongitude(double lastLongitude) {
        this.lastLongitude = lastLongitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

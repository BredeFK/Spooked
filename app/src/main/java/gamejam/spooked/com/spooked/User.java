package gamejam.spooked.com.spooked;

public class User {
    private String Uid;
    private String name;
    private String email;
    private double lastLatitude;
    private double lastLongitude;

    public User(){

    }

    public User(String Uid, String name, String email, double latitude, double longitude){
        this.Uid = Uid;
        this.name = name;
        this.email = email;
        this.lastLatitude = latitude;
        this.lastLongitude = longitude;
    }

    public User(String Uid, String name, String email){
        this.Uid = Uid;
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
        return Uid;
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
}

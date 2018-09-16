package gamejam.spooked.com.spooked;

public class Message {
    public String userName;
    public String message;
    public String userID;
    public long date;

    public Message() {}

    public Message(String userName, String userID, String message, long date){
        this.userName = userName;
        this.userID = userID;
        this.message = message;
        this.date = date;
    }
}

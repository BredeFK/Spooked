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

    public void setDate(long date) {
        this.date = date;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public long getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public String getUserName() {
        return userName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

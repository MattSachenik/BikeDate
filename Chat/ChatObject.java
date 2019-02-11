package bikedate.org.bikedate.Chat;

public class ChatObject {
    private String message;
    private Boolean currentUser;
    private long time;
    private String createdByUser;

    public ChatObject(String message, Boolean currentUser, long time, String createdByUser){
        this.message = message;
        this.currentUser = currentUser;
        this.time = time;
        this.createdByUser = createdByUser;
    }

    public String getMessage(){
        return message;
    }
    public void setMessage(String message){
        this.message = message;
    }

    public Boolean getCurrentUser(){
        return currentUser;
    }
    public void setCurrentUser(Boolean currentUser){
        this.currentUser = currentUser;
    }

    public long getTime(){
        return time;
    }
    public void setTime(long time){
        this.time = time;
    }

    public String getCreator(){
        return createdByUser;
    }
    public void setCreator(String creator){
        this.createdByUser = creator;
    }

}
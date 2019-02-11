package bikedate.org.bikedate.Groups;

public class Group {
    private String name, key, chatId;
    //private ArrayList<rider> groupMembers;

    public Group(String name, String key, String chatId)
    {
        this.name = name;
        this.key = key;
        this.chatId = chatId;
        //this.groupMembers = groupMembers;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getChatId() {
        return chatId;
    }

}

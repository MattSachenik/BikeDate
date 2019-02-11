package bikedate.org.bikedate.Cards;

public class cards {
    private String userId;
    private String name;
    private String distance;
    private String profileImageUrl;
    private String bikeType;

    public cards (String userId, String name, String bikeType, String distance, String profileImageUrl){
        this.userId = userId;
        this.name = name;
        this.distance = distance;
        this.profileImageUrl = profileImageUrl;
        this.bikeType = bikeType;
    }

    public String getUserId(){
        return userId;
    }
    public void setUserID(String userID){
        this.userId = userId;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getBikeType(){
        switch (bikeType) {
            case "none":
                return "None";
            case "cruiser":
                return "Cruiser";
            case "electricBicycle":
                return "Electric Bicycle";
            case "master":
                return "Road";
            case "mountain":
                return "Mountain";
            case "singleSpeed":
                return "Single Speed";
        }
        return "Cruiser";
    }
    public void setBikeType(String name){
        this.bikeType = bikeType;
    }

    public String getDistance(){
        return distance;
    }
    public void setDistance(String distance){
        this.distance = distance;
    }

    public String getProfileImageUrl(){
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl){
        this.profileImageUrl = profileImageUrl;
    }

}
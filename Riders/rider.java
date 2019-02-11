package bikedate.org.bikedate.Riders;

public class rider {
    private String name;
    private String type;
    private String imageUrl;
    private String riderId;

    public rider(String name, String type, String imageUrl, String riderId) {
        this.name = name;
        this.type = type;
        this.imageUrl = imageUrl;
        this.riderId = riderId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        switch (type){
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
        return type;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public String getRiderId() {
        return riderId;
    }
}
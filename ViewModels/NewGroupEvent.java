package bikedate.org.bikedate.ViewModels;

import bikedate.org.bikedate.Groups.Group;

public class NewGroupEvent {
    public Group newGroup;
    public NewGroupEvent(Group newGroup) {
        this.newGroup = newGroup;
    }
}

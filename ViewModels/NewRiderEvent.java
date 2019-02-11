package bikedate.org.bikedate.ViewModels;

import bikedate.org.bikedate.Riders.rider;

public class NewRiderEvent {
    public rider newRider;
    public NewRiderEvent(rider newRider) {
        this.newRider = newRider;
    }
}

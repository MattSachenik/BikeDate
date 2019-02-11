package bikedate.org.bikedate.ViewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import bikedate.org.bikedate.Riders.rider;

public class ChatsViewModel extends ViewModel {

    private final MutableLiveData<rider> muteRider = new MutableLiveData<>();

    public void add(rider newRider) {
        muteRider.setValue(newRider);
    }

    public LiveData<rider> getRider() {
        return muteRider;
    }

}

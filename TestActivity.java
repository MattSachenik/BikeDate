package bikedate.org.bikedate;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;

public class TestActivity extends AppCompatActivity implements OnLocationUpdatedListener, OnActivityUpdatedListener {

    //Screen Elements
    TextView mLatTV, mLongTV;
    Button mRandomBtn;

    //Database Elements
    private FirebaseAuth mAuth;
    private String currentUId;
    private DatabaseReference usersDb;
    private DatabaseReference currentUserDb;

    //Location Elements
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationGooglePlayServicesProvider provider;
    private static final int LOCATION_PERMISSION_ID = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mRandomBtn = findViewById(R.id.randomBtn);
        mLatTV = findViewById(R.id.latTV);
        mLongTV = findViewById(R.id.longTV);

        // Location permission not granted
        if (ContextCompat.checkSelfPermission(TestActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TestActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);

        }
        getLocation();

    }

    private void getLocation() {
        //long mLocTrackingInterval = 1000 * 5; // 5 sec
        float trackingDistance = 0;
        LocationAccuracy trackingAccuracy = LocationAccuracy.HIGH;

        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(trackingAccuracy)
                .setDistance(trackingDistance);

        SmartLocation.with(this)
                .location()
                .config(builder.build())
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        processLocation(location);
                        Toast.makeText(TestActivity.this, "5", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processLocation(Location location) {
        mLatTV.setText(String.valueOf(location.getLatitude()));
        mLongTV.setText(String.valueOf(location.getLongitude()));
    }

    @Override
    public void onLocationUpdated(Location location) {

    }

    @Override
    public void onActivityUpdated(DetectedActivity detectedActivity) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
    }

}


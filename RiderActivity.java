package bikedate.org.bikedate;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bikedate.org.bikedate.InfiniteAdapters.ProfileInfiniteAdapter;

public class RiderActivity extends AppCompatActivity {

    static RiderActivity riderActivity;

    //Screen elements
    private LoopingViewPager viewPager;
    private PageIndicatorView mIndicator;
    private TextView mDescriptionTV, mNameageTV, mBiketypeTV, mUsernameTV, mDistanceTV;
    private LinearLayout mShareLocationLL;
    private View mLastLine;
    private JellyToggleButton mLocationToggle;
    private Toolbar mToolbar;

    private ProfileInfiniteAdapter adapter;

    private String riderId, image1Url, image2Url, image3Url, image4Url;

    private boolean isFriend;

    //DB Elements
    private FirebaseAuth mAuth;
    private String currentUId;
    private DatabaseReference riderDb, userDb, currUserDb;

    //Location
    private Location userLocation;
    private Location riderLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider);

        riderActivity = this;

        //Toolbar Setup
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.backarrow_white);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Link Screen Elements
        viewPager = findViewById(R.id.viewpager);
        mIndicator = findViewById(R.id.indicator);
        mDescriptionTV = findViewById(R.id.descriptionTV);
        mNameageTV = findViewById(R.id.nameageTV);
        mBiketypeTV = findViewById(R.id.biketypeTV);
        mUsernameTV = findViewById(R.id.usernameTV);
        mDistanceTV = findViewById(R.id.distanceTV);
        mLocationToggle = findViewById(R.id.showLocationToggle);
        mShareLocationLL = findViewById(R.id.shareLocationLL);
        mLastLine = findViewById(R.id.lastLine);

        //Get Rider Id From Fragments
        riderId = getIntent().getExtras().getString("riderId");

        //Locations
        userLocation = new Location("Location A");
        riderLocation = new Location("Location B");

        isFriend = getIntent().getExtras().getBoolean("isFriend");
        if(!isFriend)
        {
            mShareLocationLL.setVisibility(View.GONE);
            mLastLine.setVisibility(View.GONE);
            mUsernameTV.setVisibility(View.INVISIBLE);
        }
        else
        {
            //Menu
            mToolbar.inflateMenu(R.menu.menu_rider);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId() == R.id.riderSettings)
                    {
                        Intent intent = new Intent(RiderActivity.this, RiderSettingsActivity.class);
                        Bundle b = new Bundle();
                        b.putString("key", riderId);
                        b.putBoolean("isRiderActivity", true);
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                    return false;
                }
            });
        }
        getUserLocation();
    }

    private void getUserLocation() {
        userDb = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();
        currUserDb = userDb.child(currentUId);
        currUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    if (dataSnapshot.child("location").child("latitude") != null) {
                        double riderLat = dataSnapshot.child("location").child("latitude").getValue(Double.class);
                        userLocation.setLatitude(riderLat);
                    }
                    if (dataSnapshot.child("location").child("longitude") != null) {
                        double riderLong = dataSnapshot.child("location").child("longitude").getValue(Double.class);
                        userLocation.setLongitude(riderLong);
                    }
                }
                getRiderInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RiderActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserInfo() {
        if (isFriend) {
            Map userInfo = new HashMap();
            userInfo.put("showLocation", mLocationToggle.isChecked());
            userDb.child(riderId).child("connections").child("match").child(currentUId).updateChildren(userInfo);
        }
    }

    private void getRiderInfo() {
        //Get DB Elements
        riderDb = FirebaseDatabase.getInstance().getReference().child("Users").child(riderId);
        riderDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> images = new ArrayList<>();
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    //Glide.with(CustomizeActivity.this).clear(mImage1);
                    if (map.get("image1Url") != null) {
                        image1Url = map.get("image1Url").toString();
                        images.add(image1Url);
                    }
                    if (map.get("image2Url") != null) {
                        image2Url = map.get("image2Url").toString();
                        images.add(image2Url);
                    }
                    if (map.get("image3Url") != null) {
                        image3Url = map.get("image3Url").toString();
                        images.add(image3Url);
                    }
                    if (map.get("image4Url") != null) {
                        image4Url = map.get("image4Url").toString();
                        images.add(image4Url);
                    }
                    if (map.get("description") != null) {
                        String description = map.get("description").toString();
                        if(description.isEmpty())
                            mDescriptionTV.setText("No Description Available");
                        else
                            mDescriptionTV.setText(description);
                    }
                    if (map.get("name") != null && map.get("age") != null) {
                        String age = "";
                        String name = map.get("name").toString();
                        age = map.get("age").toString();
                        if(name.isEmpty())
                            mNameageTV.setText("No Name, " + age);
                        else
                            mNameageTV.setText(name + ", " + age);
                    }
                    else if (map.get("name") != null ){
                        String name = map.get("name").toString();
                        mNameageTV.setText(name);
                    }
                    else if (map.get("age") != null) {
                        String age = map.get("age").toString();
                        mNameageTV.setText("No Name, " + age);
                    }
                    else {
                        mNameageTV.setText("No Name or Age");
                    }
                    if (map.get("biketype") != null) {
                        String biketype = map.get("biketype").toString();
                        switch (biketype) {
                            case "none":
                                mBiketypeTV.setText("  None");
                                break;
                            case "cruiser":
                                mBiketypeTV.setText("  Cruiser");
                                break;
                            case "electricBicycle":
                                mBiketypeTV.setText("  Electric Bicycle");
                                break;
                            case "master":
                                mBiketypeTV.setText("  Road");
                                break;
                            case "mountain":
                                mBiketypeTV.setText("  Mountain");
                                break;
                            case "singleSpeed":
                                mBiketypeTV.setText("  Single Speed");
                                break;
                        }
                    }
                    if (map.get("username") != null) {
                        String username = map.get("username").toString();
                        mUsernameTV.setText(" " + username);
                    }
                    if(dataSnapshot.child("location").hasChildren()){
                        if (dataSnapshot.child("location").child("latitude") != null) {
                            riderLocation.setLatitude(dataSnapshot.child("location").child("latitude").getValue(Double.class));
                        }
                        if (dataSnapshot.child("location").child("longitude") != null) {
                            riderLocation.setLongitude(dataSnapshot.child("location").child("longitude").getValue(Double.class));
                        }
                        double distanceBetweenMiles = Math.ceil(userLocation.distanceTo(riderLocation) / 1609.344 * 10) / 10;
                        mDistanceTV.setText(" " + String.valueOf(distanceBetweenMiles) + " mi");
                    }
                    if (map.get("username") != null) {
                        String username = map.get("username").toString();
                        mUsernameTV.setText(" " + username);
                    }
                    if (dataSnapshot.child("connections").child("match").child(currentUId).child("showLocation").exists()){
                        if ((Boolean) dataSnapshot.child("connections").child("match").child(currentUId).child("showLocation").getValue()){
                            mLocationToggle.setChecked(true);
                        }
                    }
                }
                adapter = new ProfileInfiniteAdapter(getBaseContext(), images, true);
                viewPager.setAdapter(adapter);

                //Custom bind indicator
                mIndicator.setCount(viewPager.getIndicatorCount());
                viewPager.setIndicatorPageChangeListener(new LoopingViewPager.IndicatorPageChangeListener() {
                    @Override
                    public void onIndicatorProgress(int selectingPosition, float progress) {
                        mIndicator.setProgress(selectingPosition, progress);
                    }

                    @Override
                    public void onIndicatorPageChange(int newIndicatorPosition) {
                        //mIndicator.setSelection(newIndicatorPosition);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RiderActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        saveUserInfo();
        super.onBackPressed();
    }

    public static RiderActivity getInstance(){
        return riderActivity;
    }

    /*
    private ArrayList<String> getUserImages() {
        return images;
    }
    */

}

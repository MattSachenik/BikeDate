package bikedate.org.bikedate;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bikedate.org.bikedate.Cards.arrayAdapter;
import bikedate.org.bikedate.Cards.cards;
import bikedate.org.bikedate.Riders.rider;
import bikedate.org.bikedate.ViewModels.RidersViewModel;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationAccuracy;
import io.nlopez.smartlocation.location.config.LocationParams;

public class MainFragment extends Fragment {

    View mainView; //Global View

    //Screen Elements
    //private Button mSettingsBtn, mPeopleBtn;
    SwipeFlingAdapterView flingContainer;

    //Database Elements
    private FirebaseAuth mAuth;
    private String currentUId;
    private boolean cruiser, electricBicycle, master, mountain, singleSpeed;
    private ArrayList<String> mPreferences;
    private DatabaseReference usersDb;
    private DatabaseReference chatsDb;
    private DatabaseReference locationDb;

    //Cards
    private cards cards_data[];
    private bikedate.org.bikedate.Cards.arrayAdapter arrayAdapter;
    private int i;
    ListView listView;
    List<cards> rowItems;

    //Location Elements
    private Location userLocation;
    private Location riderLocation;
    private int distancePreference;
    private static final int LOCATION_PERMISSION_ID = 1001;
    private double riderLat, riderLong;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mainView == null) {
            mainView = inflater.inflate(R.layout.fragment_main, container, false);

            //fling Container
            flingContainer = mainView.findViewById(R.id.frame);

            //Connecting DB elements
            usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
            chatsDb = FirebaseDatabase.getInstance().getReference().child("Chats");
            mAuth = FirebaseAuth.getInstance();
            currentUId = mAuth.getCurrentUser().getUid();
            locationDb = usersDb.child(currentUId).child("location");

            //Connecting User Location
            userLocation = new Location("Location A");
            riderLocation = new Location("Location B");

            //View cards
            flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
                @Override
                public void onItemClicked(int itemPosition, Object dataObject) {
                    //Toast.makeText(getActivity(), "Item Clicked", Toast.LENGTH_SHORT).show();
                    cards obj = (cards) dataObject;
                    Intent intent = new Intent(getContext(), RiderActivity.class);
                    Bundle b = new Bundle();
                    b.putString("riderId", obj.getUserId());
                    b.putBoolean("isFriend", false);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            });

            // Location permission not granted
            if (ActivityCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
            } else
                getLocation();
        }

        return mainView;
    }

    private void fillCards() {
        checkUserType();

        //Card Object
        rowItems = new ArrayList<cards>();
        arrayAdapter = new arrayAdapter(getActivity(), R.layout.test, rowItems);

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("hide").child(currentUId).setValue(true);
                Toast.makeText(getActivity(), "Hide", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("ride").child(currentUId).setValue(true);
                isConnectionMatch(userId, obj);
                Toast.makeText(getActivity(), "Let's Ride!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) { }

            @Override
            public void onScroll(float scrollProgressPercent) { }
        });
    }

    private void isConnectionMatch(String userId, final cards obj) {
        DatabaseReference currentUserConnectionsDb = usersDb.child(currentUId).child("connections").child("ride").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Toast.makeText(getActivity(), "New Match!", Toast.LENGTH_SHORT).show();
                    String key = chatsDb.push().getKey();
                    Map newPost = new HashMap();
                    newPost.put("key", key);
                    chatsDb.updateChildren(newPost);
                    //add user to other riders matches
                    usersDb.child(dataSnapshot.getKey()).child("connections").child("match").child(currentUId).child("chatId").setValue(key);
                    //add rider to user's matches
                    usersDb.child(currentUId).child("connections").child("match").child(dataSnapshot.getKey()).child("chatId").setValue(key);
                    ViewModelProviders.of(getActivity()).get(RidersViewModel.class).add(new rider(obj.getName(), obj.getBikeType(), obj.getDistance(), dataSnapshot.getKey()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    String userType;
    public void checkUserType(){
        final FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference userDb = usersDb.child(user.getUid());
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("preferences").hasChildren()){
                        //cruiser, electricBicycle, master, mountain, singleSpeed
                        mPreferences = new ArrayList<>();
                        if (dataSnapshot.child("preferences").child("cruiser").getValue(Boolean.class))
                            mPreferences.add("cruiser");
                        if (dataSnapshot.child("preferences").child("electricBicycle").getValue(Boolean.class))
                            mPreferences.add("electricBicycle");
                        if (dataSnapshot.child("preferences").child("master").getValue(Boolean.class))
                            mPreferences.add("master");
                        if (dataSnapshot.child("preferences").child("mountain").getValue(Boolean.class))
                            mPreferences.add("mountain");
                        if (dataSnapshot.child("preferences").child("singleSpeed").getValue(Boolean.class))
                            mPreferences.add("singleSpeed");
                    }
                    if (dataSnapshot.child("settings").hasChildren()){
                        distancePreference = dataSnapshot.child("settings").child("distance").getValue(Integer.class);
                    }
                    if (dataSnapshot.child("biketype").getValue() != null){
                        userType = dataSnapshot.child("biketype").getValue().toString();
                        final String userTypeFinal = userType;
                        findUsers(userTypeFinal);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }

    public void findUsers(final String userType){
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists() && !dataSnapshot.getKey().equals(currentUId) && dataSnapshot.child("username").getValue() != null && dataSnapshot.child("biketype").getValue() != null && !dataSnapshot.child("connections").child("ride").hasChild(currentUId) && !dataSnapshot.child("connections").child("hide").hasChild(currentUId) && dataSnapshot.child("preferences").hasChildren() && (Boolean) dataSnapshot.child("preferences").child(userType).getValue() && mPreferences.contains(dataSnapshot.child("biketype").getValue().toString())) {
                    double distanceBetweenMiles = 0;
                    if (dataSnapshot.child("location").hasChildren()) {
                        riderLat = dataSnapshot.child("location").child("latitude").getValue(Double.class);
                        riderLong = dataSnapshot.child("location").child("longitude").getValue(Double.class);
                        riderLocation.setLatitude(riderLat);
                        riderLocation.setLongitude(riderLong);
                        distanceBetweenMiles = Math.ceil(userLocation.distanceTo(riderLocation) / 1609.344 * 10) / 10;
                    }
                    //when distance setting is set
                    if(distanceBetweenMiles < distancePreference) {
                        String image1Url = "default";
                        if (dataSnapshot.child("image1Url").exists() && !dataSnapshot.child("image1Url").getValue().equals("default")) {
                            image1Url = dataSnapshot.child("image1Url").getValue().toString();
                        }
                        cards item = new cards(dataSnapshot.getKey(), dataSnapshot.child("username").getValue().toString(), dataSnapshot.child("biketype").getValue().toString(), new String(distanceBetweenMiles + " miles"), image1Url);
                        rowItems.add(item);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
    }

    private void getLocation() {
        //long mLocTrackingInterval = 1000 * 5; // 5 sec
        float trackingDistance = 0;
        LocationAccuracy trackingAccuracy = LocationAccuracy.HIGH;

        LocationParams.Builder builder = new LocationParams.Builder()
                .setAccuracy(trackingAccuracy)
                .setDistance(trackingDistance);

        SmartLocation.with(getActivity())
                .location()
                .config(builder.build())
                .oneFix()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {
                        processLocation(location);
                    }
                });
    }

    private void processLocation(Location location) {
        Map newPost = new HashMap();
        newPost.put("latitude", location.getLatitude());
        newPost.put("longitude", location.getLongitude());
        locationDb.updateChildren(newPost);
        userLocation.setLatitude(location.getLatitude());
        userLocation.setLongitude(location.getLongitude());
        fillCards();
    }
}
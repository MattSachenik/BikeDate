package bikedate.org.bikedate;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapFragment extends Fragment {

    //Map Elements
    MapView mMapView;
    private GoogleMap googleMap;

    //Variables
    float latitude, longitude;

    //Database Elements
    private FirebaseAuth mAuth;
    private DatabaseReference userDb, matchDb, currentUserDb;
    private String currentUId;

    //Arraylists

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        //get DB elements
        userDb = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();

        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                //googleMap.setMyLocationEnabled(true);
                addMyMarker();

                // For dropping a marker at a point on the Map
                //LatLng sydney = new LatLng(-34, 151);
                //googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));
                getAvailableUserIds();

                // For zooming automatically to the location of the marker
                //CameraPosition cameraPosition = new CameraPosition.Builder().target(googleMap.).zoom(12).build();
                //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
        return view;
    }

    private void addMyMarker() {
        currentUserDb = userDb.child(currentUId);
        currentUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    if (dataSnapshot.child("location").exists() && dataSnapshot.child("location").hasChildren()){
                        latitude = dataSnapshot.child("location").child("latitude").getValue(Float.class);
                        longitude = dataSnapshot.child("location").child("longitude").getValue(Float.class);
                        if(dataSnapshot.child("username").exists()){
                            if(dataSnapshot.child("name").exists()){
                                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(latitude, longitude)).title(dataSnapshot.child("name").getValue().toString()).snippet(dataSnapshot.child("username").getValue().toString());
                                Marker marker = googleMap.addMarker(markerOptions);
                                if(dataSnapshot.child("image1Url").exists()){
                                    loadMarkerIcon(marker, dataSnapshot.child("image1Url").getValue().toString());
                                }
                            }
                            else{
                                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Name Unavailable").snippet(dataSnapshot.child("username").getValue().toString());
                                Marker marker = googleMap.addMarker(markerOptions);
                                if(dataSnapshot.child("image1Url").exists()){
                                    loadMarkerIcon(marker, dataSnapshot.child("image1Url").getValue().toString());
                                }
                            }
                        }
                        LatLng myLoc = new LatLng(latitude, longitude);
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(myLoc).zoom(12).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAvailableUserIds() {
        matchDb = userDb.child(currentUId).child("connections").child("match");
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> riderIds = new ArrayList<>();
                if (dataSnapshot.exists()){
                    for(DataSnapshot match : dataSnapshot.getChildren()){
                        //&& match.child("showLocation").getValue() != null
                        if (match.child("showLocation").exists()  && (Boolean) match.child("showLocation").getValue())
                            riderIds.add(match.getKey());
                    }
                }
                addMarkers(riderIds);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addMarkers(final ArrayList<String> riderIds) {
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    for(String riderId : riderIds){
                        if (dataSnapshot.child(riderId).exists() && dataSnapshot.child(riderId).child("location").hasChildren()){
                            latitude = dataSnapshot.child(riderId).child("location").child("latitude").getValue(Float.class);
                            longitude = dataSnapshot.child(riderId).child("location").child("longitude").getValue(Float.class);
                            if(dataSnapshot.child(riderId).child("username").exists()){
                                if(dataSnapshot.child(riderId).child("name").exists()){
                                    MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(latitude, longitude)).title(dataSnapshot.child(riderId).child("name").getValue().toString()).snippet(dataSnapshot.child(riderId).child("username").getValue().toString());
                                    Marker marker = googleMap.addMarker(markerOptions);
                                    if(dataSnapshot.child(riderId).child("image1Url").exists()){
                                        loadMarkerIcon(marker, dataSnapshot.child(riderId).child("image1Url").getValue().toString());
                                    }
                                }
                                else{
                                    MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Name Unavailable").snippet(dataSnapshot.child(riderId).child("username").getValue().toString());
                                    Marker marker = googleMap.addMarker(markerOptions);
                                    if(dataSnapshot.child(riderId).child("image1Url").exists()){
                                        loadMarkerIcon(marker, dataSnapshot.child(riderId).child("image1Url").getValue().toString());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        /*
        for (int i = 0; i < 50; i++) {
            double delta = 0.1 * i;
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(lat + delta, lng + delta)).title("Marker Title").snippet("Marker Description");
            //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.person));
            Marker marker = googleMap.addMarker(markerOptions);
            loadMarkerIcon(marker);
        }
        */
    }

    private void loadMarkerIcon(final Marker marker, final String imageUrl) {
        Glide.with(this).asBitmap().load(imageUrl).apply(RequestOptions.circleCropTransform())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Bitmap smallMarker = Bitmap.createScaledBitmap(resource, 300, 300, false);
                        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(smallMarker);
                        marker.setIcon(icon);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}
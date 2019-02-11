package bikedate.org.bikedate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.appyvet.materialrangebar.RangeBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nightonke.jellytogglebutton.JellyToggleButton;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity{
    //Screen Elements
    private JellyToggleButton mSingleSpeedToggle, mMountainToggle, mRoadToggle, mCruiserToggle, mElectricToggle;
    private Button mLogOutBtn, mBackToOptionsBtn;
    private RangeBar mAgeBar, mDistanceBar;

    //Database Elements
    private FirebaseAuth mAuth;
    private DatabaseReference mSettingsDb, mPreferencesDb;

    //Strings
    private String userId;

    //Ints
    private int ageMin, ageMax;
    private int distance;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Connect
        mLogOutBtn = findViewById(R.id.logOutBtn);
        mBackToOptionsBtn = findViewById(R.id.backToOptionsBtn);
        mAgeBar = findViewById(R.id.ageBar);
        mDistanceBar = findViewById(R.id.distanceBar);
        mSingleSpeedToggle = findViewById(R.id.singleSpeedToggle);
        mMountainToggle = findViewById(R.id.mountainToggle);
        mRoadToggle= findViewById(R.id.roadToggle);
        mCruiserToggle = findViewById(R.id.cruiserToggle);
        mElectricToggle = findViewById(R.id.electricToggle);

        //Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        mSettingsDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("settings");
        mPreferencesDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("preferences");

        getUserInfo();

        mBackToOptionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mLogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                mAuth.signOut();
                Toast.makeText(SettingsActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void saveUserInfo() {
        //age distance
        ageMin = mAgeBar.getLeftIndex() + 18;
        ageMax = mAgeBar.getRightIndex() + 18;
        distance = mDistanceBar.getRightIndex() + 2;
        Map userInfo = new HashMap();
        userInfo.put("ageMin", ageMin);
        userInfo.put("ageMax", ageMax);
        userInfo.put("distance", distance);
        mSettingsDb.updateChildren(userInfo);

        //Bike preferences
        Map userPreferences = new HashMap();
        userPreferences.put("singleSpeed", mSingleSpeedToggle.isChecked());
        userPreferences.put("mountain", mMountainToggle.isChecked());
        userPreferences.put("electricBicycle", mElectricToggle.isChecked());
        userPreferences.put("cruiser", mCruiserToggle.isChecked());
        userPreferences.put("master", mRoadToggle.isChecked());
        mPreferencesDb.updateChildren(userPreferences);
    }

    private void getUserInfo() {
        mSettingsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("ageMin") != null && map.get("ageMax") != null)
                    {
                        ageMin = ((Long) map.get("ageMin")).intValue() - 18;
                        ageMax = ((Long) map.get("ageMax")).intValue() - 18;
                        mAgeBar.setRangePinsByIndices(ageMin, ageMax);
                    }
                    if (map.get("distance") != null)
                    {
                        distance = ((Long) map.get("distance")).intValue() - 1;
                        mDistanceBar.setSeekPinByValue(distance);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SettingsActivity.this, "Error Retrieving Info", Toast.LENGTH_SHORT).show();
            }
        });

        mPreferencesDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    if (dataSnapshot.child("cruiser").getValue(Boolean.class))
                        mCruiserToggle.setChecked(true);
                    if (dataSnapshot.child("electricBicycle").getValue(Boolean.class))
                        mElectricToggle.setChecked(true);
                    if (dataSnapshot.child("master").getValue(Boolean.class))
                        mRoadToggle.setChecked(true);
                    if (dataSnapshot.child("mountain").getValue(Boolean.class))
                        mMountainToggle.setChecked(true);
                    if (dataSnapshot.child("singleSpeed").getValue(Boolean.class))
                        mSingleSpeedToggle.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SettingsActivity.this, "Error Retrieving Info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        saveUserInfo();
        super.onBackPressed();
    }
}

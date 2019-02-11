package bikedate.org.bikedate;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nightonke.jellytogglebutton.JellyToggleButton;

import java.util.HashMap;
import java.util.Map;

public class MatchTypesActivity extends AppCompatActivity {

    //Screen Elements
    private JellyToggleButton mSingleSpeedToggle, mMountainToggle, mRoadToggle, mCruiserToggle, mElectricToggle;
    private Button mSetPreferencesBtn;
    private TextView mQuestionTV, mSsBTV, mMbTV, mRbTv, mCruiserTV, mEbTV;


    //Database Elements
    private FirebaseAuth mAuth;
    private String currentUId;
    private DatabaseReference usersDb;
    private DatabaseReference currentUserDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_types);

        //Toggles
        mSingleSpeedToggle = findViewById(R.id.singleSpeedToggle);
        mMountainToggle = findViewById(R.id.mountainToggle);
        mRoadToggle= findViewById(R.id.roadToggle);
        mCruiserToggle = findViewById(R.id.cruiserToggle);
        mElectricToggle = findViewById(R.id.electricToggle);
        mSetPreferencesBtn = (Button) findViewById(R.id.setPreferencesBtn);

        //Typeface
        Typeface verdana = Typeface.createFromAsset(getAssets(), "fonts/Verdana.ttf");

        //TextViews
        mQuestionTV = findViewById(R.id.questionTV);
        mSsBTV = findViewById(R.id.ssbTV);
        mMbTV = findViewById(R.id.mbTV);
        mRbTv = findViewById(R.id.rbTV);
        mCruiserTV = findViewById(R.id.cruiserTV);
        mEbTV = findViewById(R.id.ebTV);


        mQuestionTV.setTypeface(verdana);
        mSsBTV.setTypeface(verdana);
        mMbTV.setTypeface(verdana);
        mRbTv.setTypeface(verdana);
        mCruiserTV.setTypeface(verdana);
        mEbTV.setTypeface(verdana);

        //Connect Database
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();

        //Strings
        final String currentUId = mAuth.getCurrentUser().getUid();

        mSetPreferencesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            currentUserDb = usersDb.child(currentUId).child("preferences");
            Map newPost = new HashMap();
            newPost.put("singleSpeed", mSingleSpeedToggle.isChecked());
            newPost.put("mountain", mMountainToggle.isChecked());
            newPost.put("electricBicycle", mElectricToggle.isChecked());
            newPost.put("cruiser", mCruiserToggle.isChecked());
            newPost.put("master", mRoadToggle.isChecked());
            currentUserDb.updateChildren(newPost);

            startActivity(new Intent(MatchTypesActivity.this, TabActivity.class));
            finish();
            }
        });


    }
}

/*      //register Button
        final CircularProgressButton registerBtn2 = findViewById(R.id.registerBtn2);
        final Button mActionBtn = (Button) findViewById(R.id.actionButton);

        registerBtn2.setIndeterminateProgressMode(true);
        registerBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registerBtn2.isProgress())  {
                    registerBtn2.showCancel();
                    registerBtn2.showIdle();
                }
                else if (registerBtn2.isIdle() ){
                    registerBtn2.showProgress();
                }
                else
                {
                    registerBtn2.showIdle();
                }
            }
        });
        mActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerBtn2.showError();
            }
        });
        */

/*
//Bike Selection

package bikedate.org.bikedate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.rd.PageIndicatorView;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {
    LoopingViewPager viewPager;
    BikeTypeInfiniteAdapter adapter;
    PageIndicatorView mIndicator;

    Button mRandomBtn;
    TextView mPageNumberTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        //Connect Screen Elements
        viewPager = findViewById(R.id.viewpager);
        mIndicator = findViewById(R.id.indicator);
        mRandomBtn = findViewById(R.id.randomBtn);
        mPageNumberTV = findViewById(R.id.pageNumberTV);

        //Fill Infinite Adapter With Slides
        adapter = new BikeTypeInfiniteAdapter(this, createDummyItems(), true);
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

        mRandomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = viewPager.getIndicatorPosition();
                switch (count) {
                    case 0:
                        mPageNumberTV.setText("haha");
                        break;
                    case 1:
                        mPageNumberTV.setText("lol");
                        break;
                    case 2:
                        mPageNumberTV.setText("lmao");
                        break;
                    case 3:
                        mPageNumberTV.setText("kaka");
                        break;
                    case 4:
                        mPageNumberTV.setText("rofl");
                        break;
                }
            }
        });
    }

    private ArrayList<Integer> createDummyItems()
    {
        ArrayList<Integer> items = new ArrayList<>();
        items.add(0, 1);
        items.add(1, 2);
        items.add(2, 3);
        items.add(3, 4);
        items.add(4, 0);
        return items;
    }


}
 */
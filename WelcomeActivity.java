package bikedate.org.bikedate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.asksira.loopingviewpager.LoopingViewPager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rd.PageIndicatorView;

import java.util.ArrayList;

import bikedate.org.bikedate.InfiniteAdapters.BikeTypeInfiniteAdapter;

public class WelcomeActivity extends AppCompatActivity {

    //Screen Elements
    private View mFadeOutLayer1, mFadeOutLayer2, mFadeOutLayer3;
    private LinearLayout mFadeOutWelcome, mFadeOutLGS;
    private EditText mBikeTypeET;
    private Button mSkipBtn, mEnterBtn;

    //ViewPage Elements
    private LoopingViewPager viewPager;
    private PageIndicatorView mIndicator;
    private BikeTypeInfiniteAdapter adapter;

    //Database Elements
    private FirebaseAuth mAuth;
    private String currentUId;
    private DatabaseReference usersDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //Views
        mFadeOutLayer1 = (View) findViewById(R.id.fadeOutLayer1);
        mFadeOutLayer2 = (View) findViewById(R.id.fadeOutLayer2);
        mFadeOutLayer3 = (View) findViewById(R.id.fadeOutLayer3);

        //LLs
        mFadeOutWelcome = (LinearLayout) findViewById(R.id.fadeOutWelcome);
        mFadeOutLGS = (LinearLayout) findViewById(R.id.fadeOutLGS);

        //Btns
        mSkipBtn = (Button) findViewById(R.id.skipBtn);
        mEnterBtn = (Button) findViewById(R.id.enterBtn);

        //ET

        //Connect Viewpage Elements
        viewPager = findViewById(R.id.viewpager);
        mIndicator = findViewById(R.id.indicator);

        //Connect Database
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();

        //Strings
        final String currentUId = mAuth.getCurrentUser().getUid();

        //Animations
        final Animation fadeOut5 = new AlphaAnimation(1f, 0f);
        fadeOut5.setDuration(1500);
        fadeOut5.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFadeOutLayer3.setVisibility(View.GONE);
                //mSkipBtn.setEnabled(true);
            }
        });

        final Animation fadeOut4 = new AlphaAnimation(1f, 0f);
        fadeOut4.setDuration(1500);
        fadeOut4.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFadeOutLGS.setVisibility(LinearLayout.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFadeOutLayer3.startAnimation(fadeOut5);
                    }
                }, 500);
            }
        });

        final Animation fadeOut3 = new AlphaAnimation(1f, 0f);
        fadeOut3.setDuration(1500);
        fadeOut3.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFadeOutLayer2.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFadeOutLGS.startAnimation(fadeOut4);
                    }
                }, 500);
            }
        });

        final Animation fadeOut2 = new AlphaAnimation(1f, 0f);
        fadeOut2.setDuration(1500);
        fadeOut2.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFadeOutWelcome.setVisibility(LinearLayout.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFadeOutLayer2.startAnimation(fadeOut3);
                    }
                }, 500);
            }
        });

        final Animation fadeOut = new AlphaAnimation(1f, 0f);
        fadeOut.setDuration(1500);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFadeOutLayer1.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFadeOutWelcome.startAnimation(fadeOut2);
                    }
                }, 500);
            }
        });

        //Begin animation
        mFadeOutLayer1.startAnimation(fadeOut);

        //Code
        //Fill Infinite Adapter With Slides
        adapter = new BikeTypeInfiniteAdapter(this, createBikeItems(), true);
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

        mSkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, TabActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        mEnterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //usersDb.child(currentUId).child("biketype").setValue(mBikeTypeET.getText().toString());
                int count = viewPager.getIndicatorPosition();
                switch (count) {
                    case 0:
                        usersDb.child(currentUId).child("biketype").setValue("singleSpeed");
                        break;
                    case 1:
                        usersDb.child(currentUId).child("biketype").setValue("mountain");
                        break;
                    case 2:
                        usersDb.child(currentUId).child("biketype").setValue("master");
                        break;
                    case 3:
                        usersDb.child(currentUId).child("biketype").setValue("cruiser");
                        break;
                    case 4:
                        usersDb.child(currentUId).child("biketype").setValue("electricBicycle");
                        break;
                }
                startActivity(new Intent(WelcomeActivity.this, MatchTypesActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("You haven't finised creating your profile.  Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        WelcomeActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private ArrayList<Integer> createBikeItems()
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

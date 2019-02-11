package bikedate.org.bikedate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.greenrobot.eventbus.EventBus;

import bikedate.org.bikedate.ViewModels.RemoveRiderEvent;

public class RiderSettingsActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;

    private Button mSendReportBtn, mRemoveRiderBtn;

    private EditText mReportET;

    private String currentUserID, instanceID, removeRiderKey, reasonForReport;

    private DatabaseReference userMatchDb, riderMatchDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_settings);

        //Make sure EditTexts stay down unless clicked
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Toolbar Setup
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.backarrow_white);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        instanceID = FirebaseInstanceId.getInstance().getId();
        Toast.makeText(this, instanceID, Toast.LENGTH_SHORT).show();

        mReportET = findViewById(R.id.reportET);
        mSendReportBtn = findViewById(R.id.sendReportBtn);
        mSendReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mReportET.getText().toString().isEmpty())
                {
                    reasonForReport = mReportET.getText().toString();

                }
            }
        });

        mRemoveRiderBtn = findViewById(R.id.removeRiderBtn);
        mRemoveRiderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeRider();
            }
        });
    }

    private void removeRider() {
        //Remove from Databases
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        removeRiderKey = getIntent().getExtras().getString("key");

        //Remove rider from current user's matches
        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("match").child(removeRiderKey).removeValue();

        //Remove current user from rider's matches
        FirebaseDatabase.getInstance().getReference().child("Users").child(removeRiderKey).child("connections").child("match").child(currentUserID).removeValue();

        //Remove from Groups list
        RemoveRiderEvent event = new RemoveRiderEvent(removeRiderKey);
        EventBus.getDefault().post(event);
        if (getIntent().getExtras().getBoolean("isRiderActivity"))
            RiderActivity.getInstance().finish();
        else
            ChatActivity.getInstance().finish();
        finish();
    }

}

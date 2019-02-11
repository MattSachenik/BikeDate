package bikedate.org.bikedate;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import bikedate.org.bikedate.GroupMembers.GroupMemberAdapter;
import bikedate.org.bikedate.Riders.rider;
import bikedate.org.bikedate.ViewModels.RemoveGroupEvent;

public class GroupSettingsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mGroupMembersAdapter;
    private RecyclerView.LayoutManager mGroupMembersLayoutManager;

    ArrayList<rider> groupMembers = new ArrayList<>();

    private Button mLeaveGroupBtn;

    private android.support.v7.widget.Toolbar mToolbar;

    private DatabaseReference groupDb, memberDb, userGroupDb;

    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);

        //Toolbar Setup
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.backarrow_white);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        
        //leave group
        mLeaveGroupBtn = findViewById(R.id.leaveGroupBtn);
        mLeaveGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveGroup();
            }
        });

        mRecyclerView = findViewById(R.id.recycler);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mGroupMembersLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mGroupMembersLayoutManager);
        mGroupMembersAdapter = new GroupMemberAdapter(getDataSetMembers(), this);
        mRecyclerView.setAdapter(mGroupMembersAdapter);

        getGroupMembers();
    }

    private void leaveGroup() {
        //Remove from Databases
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        memberDb = groupDb.child(currentUserID);
        memberDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        userGroupDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("groups").child(getIntent().getExtras().getString("key"));
        userGroupDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Remove from Groups list
        RemoveGroupEvent event = new RemoveGroupEvent(getIntent().getExtras().getString("key"));
        EventBus.getDefault().post(event);

        GroupChatActivity.getInstance().finish();
        finish();
    }

    private void getGroupMembers() {
        groupDb = FirebaseDatabase.getInstance().getReference().child("Groups").child(getIntent().getExtras().getString("key")).child("riders");
        groupDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for(DataSnapshot member : dataSnapshot.getChildren()){
                        FetchMatchInformation(member.getKey());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(GroupSettingsActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void FetchMatchInformation(String key) {
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String name = "";
                    String type = "";
                    String profileImageUrl = "";
                    String id = "";
                    if(dataSnapshot.child("username").getValue()!=null){
                        name = dataSnapshot.child("username").getValue().toString();
                    }
                    if(dataSnapshot.child("biketype").getValue()!=null){
                        type = dataSnapshot.child("biketype").getValue().toString();
                    }
                    if(dataSnapshot.child("image1Url").getValue()!=null){
                        profileImageUrl = dataSnapshot.child("image1Url").getValue().toString();
                    }

                    id = dataSnapshot.getKey();

                    rider obj = new rider(name, type, profileImageUrl, id);
                    groupMembers.add(obj);
                    mGroupMembersAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private List<rider> getDataSetMembers() {
        return groupMembers;
    }
}

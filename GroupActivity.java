package bikedate.org.bikedate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bikedate.org.bikedate.AddRider.AddRiderAdapter;
import bikedate.org.bikedate.Groups.Group;
import bikedate.org.bikedate.Riders.rider;
import bikedate.org.bikedate.ViewModels.NewGroupEvent;

public class GroupActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mFriendsAdapter;
    private RecyclerView.LayoutManager mFriendsLayoutManager;

    ArrayList<rider> friends = new ArrayList<>();
    ArrayList<rider> groupRiders = new ArrayList<>();

    private Button mCreateGroupBtn;
    private EditText mGroupNameET;
    private android.support.v7.widget.Toolbar mToolbar;

    private String currentUserID;

    private DatabaseReference groupsDb, newGroupDb, usersDb, chatsDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        //Make sure EditTexts stay down unless clicked
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Toolbar Setup
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.backarrow_white);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mCreateGroupBtn = findViewById(R.id.createGroupBtn);
        mGroupNameET = findViewById(R.id.groupNameET);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRecyclerView = findViewById(R.id.recycler);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mFriendsLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mFriendsLayoutManager);
        mFriendsAdapter = new AddRiderAdapter(getDataSetFriends(), this);
        mRecyclerView.setAdapter(mFriendsAdapter);

        getUserMatchId();

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");
        chatsDb = FirebaseDatabase.getInstance().getReference().child("Chats");
        mCreateGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(mGroupNameET.getText().toString().isEmpty()) {
                Toast.makeText(GroupActivity.this, "No Group Name", Toast.LENGTH_SHORT).show();
            }
            else {
                groupsDb = FirebaseDatabase.getInstance().getReference().child("Groups");
                groupRiders = ((AddRiderAdapter) mRecyclerView.getAdapter()).getGroupRiders();

                //make group and give it a key
                String key = groupsDb.push().getKey();
                Map newPost = new HashMap();
                newPost.put("key", key);
                groupsDb.updateChildren(newPost);

                //initial group info
                newGroupDb = groupsDb.child(key);
                String chatKey = chatsDb.push().getKey();
                Map newPost2 = new HashMap();
                Map originalRiders = new HashMap();
                usersDb.child(currentUserID).child("connections").child("groups").child(key).setValue(true);
                originalRiders.put(currentUserID, "true");
                for(rider addRider : groupRiders)
                {
                    originalRiders.put(addRider.getRiderId(), "true");
                    usersDb.child(addRider.getRiderId()).child("connections").child("groups").child(key).setValue(true);
                }
                newPost2.put("name", mGroupNameET.getText().toString());
                newPost2.put("chatId", chatKey);
                newPost2.put("riders", originalRiders);
                newGroupDb.updateChildren(newPost2);

                Group newGroup = new Group(mGroupNameET.getText().toString(), key, chatKey);
                NewGroupEvent event = new NewGroupEvent(newGroup);
                EventBus.getDefault().post(event);

                onBackPressed();
            }
            }
        });
    }

    private void getUserMatchId() {
        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("match");
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for(DataSnapshot match : dataSnapshot.getChildren()){
                        FetchMatchInformation(match.getKey());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(GroupActivity.this, "Error", Toast.LENGTH_LONG).show();
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
                    friends.add(obj);
                    mFriendsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private List<rider> getDataSetFriends() {
        return friends;
    }
}

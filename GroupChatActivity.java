package bikedate.org.bikedate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bikedate.org.bikedate.Chat.ChatAdapter;
import bikedate.org.bikedate.Chat.ChatObject;

public class GroupChatActivity extends AppCompatActivity {

    static GroupChatActivity groupChatActivity;

    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private LinearLayoutManager mChatLayoutManager;
    private long time;
    private boolean alreadyLoaded;

    private EditText mSendET;

    private ImageView mSendIV;

    private String currentUId, chatId;

    private DatabaseReference groupChatDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        groupChatActivity = this;

        //Toolbar Setup
        mToolbar = findViewById(R.id.toolbar);

        currentUId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        alreadyLoaded = false;

        //IDs
        chatId = getIntent().getExtras().getString("chatId");
        groupChatDb = FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId);

        //Toolbar
        setUpChatToolbar(getIntent().getExtras().getString("groupName"));

        getChatMessages();

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mChatLayoutManager.setStackFromEnd(true);
        mChatLayoutManager.setReverseLayout(false);
        mChatAdapter = new ChatAdapter(getDataSetChat(), this);
        mRecyclerView.setAdapter(mChatAdapter);

        mSendET = findViewById(R.id.message);
        mSendIV = findViewById(R.id.send);

        mSendIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void setUpChatToolbar(String groupName) {
        //Title
        mToolbar.setTitle(groupName);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));

        //Menu
        mToolbar.inflateMenu(R.menu.menu_group_chat);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
            if(item.getItemId() == R.id.groupSettings)
            {
                Intent intent = new Intent(GroupChatActivity.this, GroupSettingsActivity.class);
                Bundle b = new Bundle();
                b.putString("key", getIntent().getExtras().getString("key"));
                intent.putExtras(b);
                startActivity(intent);
            }
            return false;
            }
        });

        //Back arrow
        mToolbar.setNavigationIcon(R.drawable.backarrow_white);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void sendMessage() {
        //Get message text
        String sendMessageText = mSendET.getText().toString();

        time = System.currentTimeMillis();

        if(!sendMessageText.isEmpty()){
            DatabaseReference newMessageDb = groupChatDb.push();

            Map newMessage = new HashMap();
            newMessage.put("createdByUser", currentUId);
            newMessage.put("text", sendMessageText);
            newMessage.put("time", time);

            newMessageDb.setValue(newMessage);
        }
        mSendET.setText(null);
    }

    private void getChatMessages() {
        groupChatDb.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                alreadyLoaded = true;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });

        groupChatDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()){
                    String message = "";
                    String createdByUser = "";
                    time = 0;

                    if(dataSnapshot.child("text").getValue()!=null){
                        message = dataSnapshot.child("text").getValue().toString();
                    }
                    if(dataSnapshot.child("createdByUser").getValue()!=null){
                        createdByUser = dataSnapshot.child("createdByUser").getValue().toString();
                    }
                    if(dataSnapshot.child("time").exists() && !dataSnapshot.child("time").getValue().toString().contains(":")){
                        time = dataSnapshot.child("time").getValue(Long.class);
                    }

                    if(message != null && createdByUser != null){
                        Boolean currentUserBoolean = false;
                        if(createdByUser.equals(currentUId)){
                            currentUserBoolean = true;
                        }
                        ChatObject newMessage = new ChatObject(message, currentUserBoolean, time, createdByUser);
                        resultsChat.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();
                        if(alreadyLoaded)
                        {
                            mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount());
                        }
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

    public static GroupChatActivity getInstance(){
        return groupChatActivity;
    }

    private ArrayList<ChatObject> resultsChat = new ArrayList<>();
    private List<ChatObject> getDataSetChat() {
        return resultsChat;
    }

}

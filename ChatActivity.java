package bikedate.org.bikedate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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

import bikedate.org.bikedate.Chat.ChatAdapter;
import bikedate.org.bikedate.Chat.ChatObject;
import bikedate.org.bikedate.Riders.rider;
import bikedate.org.bikedate.ViewModels.NewRiderEvent;

public class ChatActivity extends AppCompatActivity {

    static ChatActivity chatActivity;

    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private LinearLayoutManager mChatLayoutManager;
    private long time;
    private boolean alreadyLoaded, chatOrGroup;

    private EditText mSendET;

    private ImageView mSendIV;

    private String currentUId, riderId, chatId, imageUrl, riderName, riderType;

    private DatabaseReference chatIdDb, chatsDb, riderDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatActivity = this;

        //Toolbar Setup
        mToolbar = findViewById(R.id.toolbar);

        currentUId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        alreadyLoaded = false;

        //IDs
        riderId = getIntent().getExtras().getString("riderId");
        riderType = getIntent().getExtras().getString("riderType");

        //DBs
        chatIdDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUId).child("connections").child("match").child(riderId).child("chatId");
        chatsDb = FirebaseDatabase.getInstance().getReference().child("Chats");

        //Fetch ID for chat messages
        getChatId();

        //Toolbar
        setUpChatToolbar();

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

        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ChatActivity.this, String.valueOf(resultsChat.size()), Toast.LENGTH_LONG).show();
            }
        }, 1000);
        */
    }

    private void setUpChatToolbar() {
        riderDb = FirebaseDatabase.getInstance().getReference().child("Users").child(riderId);
        riderDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    if (dataSnapshot.child("image1Url").exists()) {
                        imageUrl = dataSnapshot.child("image1Url").getValue().toString();
                    }
                    if (dataSnapshot.child("name").exists()) {
                        riderName = dataSnapshot.child("name").getValue().toString();
                    }
                    else if (dataSnapshot.child("username").exists()) {
                        riderName = dataSnapshot.child("username").getValue().toString();
                    }
                    else{
                        riderName = " Name Unavailable";
                    }

                    /*
                    if(dataSnapshot.child("location").hasChildren()){
                        if (dataSnapshot.child("location").child("latitude") != null) {
                            riderLocation.setLatitude(dataSnapshot.child("location").child("latitude").getValue(Double.class));
                        }
                        if (dataSnapshot.child("location").child("longitude") != null) {
                            riderLocation.setLongitude(dataSnapshot.child("location").child("longitude").getValue(Double.class));
                        }
                    }
                    */
                }

                Glide.with(ChatActivity.this).asBitmap().load(imageUrl).apply(RequestOptions.circleCropTransform())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                Bitmap smallMarker = Bitmap.createScaledBitmap(resource, 100, 100, false);
                                Drawable d = new BitmapDrawable(getResources(), smallMarker);
                                mToolbar.setLogo(d);
                            }
                        });

                mToolbar.setTitle("  " + riderName);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));

        //Back arrow
        mToolbar.setNavigationIcon(R.drawable.backarrow_white);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //Menu
        mToolbar.inflateMenu(R.menu.menu_rider);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.riderSettings)
                {
                    Intent intent = new Intent(ChatActivity.this, RiderSettingsActivity.class);
                    Bundle b = new Bundle();
                    b.putString("key", riderId);
                    b.putBoolean("isRiderActivity", false);
                    intent.putExtras(b);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    private void sendMessage() {
        //Get message text
        String sendMessageText = mSendET.getText().toString();

        time = System.currentTimeMillis();

        if(!sendMessageText.isEmpty()){
            DatabaseReference newMessageDb = chatsDb.push();

            Map newMessage = new HashMap();
            newMessage.put("createdByUser", currentUId);
            newMessage.put("text", sendMessageText);
            newMessage.put("time", time);

            newMessageDb.setValue(newMessage);
        }

        mSendET.setText(null);

        if (mChatAdapter.getItemCount() == 0){
            addRiderToChatsFragment();
        }
    }

    private void addRiderToChatsFragment() {
        Map newPost = new HashMap();
        newPost.put("chatStarted", true);

        //chatStarted = true for both current user and rider, sent to DB
        FirebaseDatabase.getInstance().getReference().child("Users").child(riderId).child("connections").child("match").child(currentUId).updateChildren(newPost);
        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUId).child("connections").child("match").child(riderId).updateChildren(newPost);

        NewRiderEvent event = new NewRiderEvent(new rider(riderName, riderType, imageUrl, riderId));
        EventBus.getDefault().post(event);
    }

    private void getChatId() {
        chatIdDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    chatId = dataSnapshot.getValue().toString();
                    chatsDb = chatsDb.child(chatId);
                    getChatMessages();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getChatMessages() {
        chatsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                alreadyLoaded = true;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });

        chatsDb.addChildEventListener(new ChildEventListener() {
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

                    if(message!=null && createdByUser!=null){
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

    public static ChatActivity getInstance(){
        return chatActivity;
    }

    private ArrayList<ChatObject> resultsChat = new ArrayList<>();
    private List<ChatObject> getDataSetChat() {
        return resultsChat;
    }

}

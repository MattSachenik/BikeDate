package bikedate.org.bikedate;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import bikedate.org.bikedate.Riders.rider;
import bikedate.org.bikedate.Riders.ridersAdapter;
import bikedate.org.bikedate.ViewModels.NewRiderEvent;
import bikedate.org.bikedate.ViewModels.RemoveRiderEvent;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {


    public ChatsFragment() {
        // Required empty public constructor
    }

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatsAdapter;
    private RecyclerView.LayoutManager mChatsLayoutManager;

    ArrayList<rider> chats = new ArrayList<>();

    private String currentUserID;

    String testValue;

    private rider newRider;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View matchesView = inflater.inflate(R.layout.fragment_chats, container, false);
        chats.clear();
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRecyclerView = matchesView.findViewById(R.id.recycler);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mChatsLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mChatsLayoutManager);
        mChatsAdapter = new ridersAdapter(getDataSetMatches(), getActivity());
        mRecyclerView.setAdapter(mChatsAdapter);

        getActiveChats();

        return matchesView;
    }

    private void getActiveChats() {
        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("match");
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for(DataSnapshot match : dataSnapshot.getChildren()){
                        if(match.child("chatStarted").exists()) {
                            FetchMatchInformation(match.getKey());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
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
                    chats.add(obj);
                    mChatsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Subscribe
    public void onEvent(NewRiderEvent event) {
        chats.add(event.newRider);
        mChatsAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mChatsAdapter);
    }

    /*
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
    */

    public void addRider(int pos, rider itemData) {
        chats.add(pos, itemData);
        mChatsAdapter.notifyItemInserted(pos);
    }

    @Subscribe
    public void onEvent(RemoveRiderEvent event) {
        String removedRiderKey = event.removedRiderKey;
        rider removedRider = null;
        for (rider Rider : chats)
        {
            if(Rider.getRiderId().equals(removedRiderKey))
            {
                removedRider = Rider;
                break;
            }
        }
        chats.remove(removedRider);
        mChatsAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mChatsAdapter);
    }

    private List<rider> getDataSetMatches() {
        return chats;
    }


}

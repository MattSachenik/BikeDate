package bikedate.org.bikedate;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nihaskalam.progressbuttonlibrary.CircularProgressButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import bikedate.org.bikedate.Riders.rider;
import bikedate.org.bikedate.Riders.ridersAdapter;
import bikedate.org.bikedate.ViewModels.RemoveRiderEvent;
import bikedate.org.bikedate.ViewModels.RidersViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class MatchesFragment extends Fragment {

    public MatchesFragment() {
        // Required empty public constructor
    }

    private EditText mRiderName;
    private CircularProgressButton mAddFriendCPB;

    private boolean isFirstMatch, alreadyFriend;
    int check;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;

    private FloatingActionButton mAddFriendFAB;
    private FloatingActionsMenu mAddReferFAM;

    ArrayList<rider> resultsMatches = new ArrayList<rider>();

    private String currentUserID, userKey, username, removedRiderKey;

    private List<String> myData;

    DatabaseReference userDb, matchDb, ref;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelProviders.of(getActivity()).get(RidersViewModel.class).getRider().observe(this, new Observer<rider>() {
            @Override
            public void onChanged(@Nullable rider newRider) {
                if (getDataSetMatches().size() != 0 || isFirstMatch) {
                    addRider(resultsMatches.size(), newRider);
                    mMatchesAdapter.notifyDataSetChanged();
                    mRecyclerView.setAdapter(mMatchesAdapter);
                }
                isFirstMatch = false;
            }
        });
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
        View matchesView = inflater.inflate(R.layout.fragment_matches, container, false);
        isFirstMatch = false;
        resultsMatches.clear();
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRecyclerView = matchesView.findViewById(R.id.recycler);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdapter = new ridersAdapter(getDataSetMatches(), getActivity());
        mRecyclerView.setAdapter(mMatchesAdapter);

        getUserMatchId();

        mAddFriendFAB = matchesView.findViewById(R.id.addFriendFAB);
        mAddReferFAM = matchesView.findViewById(R.id.addReferFAM);
        mAddFriendFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriend();
            }
        });

        return matchesView;
    }

    private void addFriend() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_rider, null);

        mRiderName = mView.findViewById(R.id.riderNameET);
        mAddFriendCPB = mView.findViewById(R.id.addFriendCPB);
        mAddFriendCPB.setIndeterminateProgressMode(true);
        mAddFriendCPB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = mRiderName.getText().toString();
                mAddFriendCPB.showProgress();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!username.isEmpty()) {
                            ref=FirebaseDatabase.getInstance().getReference().child("Users");
                            ref.orderByChild("username").equalTo(username).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        for (DataSnapshot data : dataSnapshot.getChildren())
                                            userKey = data.getKey();
                                        if (currentUserID.equals(userKey))
                                        {
                                            mAddFriendCPB.setErrorText("You Can't Add Yourself");
                                            mAddFriendCPB.showError();
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mAddFriendCPB.showIdle();
                                                }
                                            }, 2500);
                                        }
                                        else
                                            addRiderByKey();
                                            ref.removeEventListener(this);
                                    }
                                    else {
                                        mAddFriendCPB.setErrorText("Username Does Not Exist");
                                        mAddFriendCPB.showError();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mAddFriendCPB.showIdle();
                                            }
                                        }, 2500);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        else {
                            mAddFriendCPB.setErrorText("Please Type In A Username");
                            mAddFriendCPB.showError();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mAddFriendCPB.showIdle();
                                }
                            }, 2500);
                        }
                    }
                }, 1500);
            }
        });
        mBuilder.setView(mView);
        mBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mAddReferFAM.collapse();
            }
        });
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void addRiderByKey() {
        alreadyFriend = false;
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && !userKey.isEmpty()){
                    for(DataSnapshot match : dataSnapshot.getChildren()){
                        if (match.getKey().equals(userKey)){
                            alreadyFriend = true;
                            mAddFriendCPB.setErrorText("Rider Already Added");
                            mAddFriendCPB.showError();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mAddFriendCPB.showIdle();
                                }
                            }, 2500);
                            break;
                        }
                    }
                    if(!alreadyFriend)
                    {
                        //get chat key for new rider
                        String key = FirebaseDatabase.getInstance().getReference().child("Chats").push().getKey();
                        //assign new rider to current user's match list
                        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("match").child(userKey).child("chatId").setValue(key);
                        //assign current user to new rider's match list
                        FirebaseDatabase.getInstance().getReference().child("Users").child(userKey).child("connections").child("match").child(currentUserID).child("chatId").setValue(key);

                        //add rider to matches list
                        final DatabaseReference newRiderDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userKey);
                        newRiderDb.addListenerForSingleValueEvent(new ValueEventListener() {
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
                                    resultsMatches.add(obj);
                                    mMatchesAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        mAddFriendCPB.showComplete();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mAddFriendCPB.showIdle();
                            }
                        }, 2500);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getUserMatchId() {
        matchDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("match");
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for(DataSnapshot match : dataSnapshot.getChildren()){
                        FetchMatchInformation(match.getKey());
                    }
                }
                else
                {
                    isFirstMatch = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void FetchMatchInformation(String key) {
        userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
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
                    resultsMatches.add(obj);
                    mMatchesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displaySnackbar(String text, String actionName, View.OnClickListener action) {
        Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG)
                .setAction(actionName, action);

        View v = snack.getView();
        v.setBackgroundColor(getResources().getColor(R.color.pb_blue_dark));
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
        ((TextView) v.findViewById(android.support.design.R.id.snackbar_action)).setTextColor(Color.BLACK);

        snack.show();
    }

    private void addRider(int pos, rider itemData) {
        resultsMatches.add(pos, itemData);
        mMatchesAdapter.notifyItemInserted(pos);
    }

    private int removeRider(rider itemData) {
        int pos = resultsMatches.indexOf(itemData);
        resultsMatches.remove(itemData);
        mMatchesAdapter.notifyItemRemoved(pos);
        return pos;
    }

    @Subscribe
    public void onEvent(RemoveRiderEvent event) {
        removedRiderKey = event.removedRiderKey;
        rider removedRider = null;
        for (rider Rider : resultsMatches)
        {
            if(Rider.getRiderId().equals(removedRiderKey))
            {
                removedRider = Rider;
                break;
            }
        }
        resultsMatches.remove(removedRider);
        mMatchesAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mMatchesAdapter);
    }


    private List<rider> getDataSetMatches() {
        return resultsMatches;
    }


}

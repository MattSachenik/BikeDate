package bikedate.org.bikedate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import bikedate.org.bikedate.Groups.Group;
import bikedate.org.bikedate.Groups.GroupsAdapter;
import bikedate.org.bikedate.ViewModels.NewGroupEvent;
import bikedate.org.bikedate.ViewModels.RemoveGroupEvent;


public class GroupsFragment extends Fragment {

    public GroupsFragment() {
        // Required empty public constructor
    }

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mGroupsAdapter;
    private RecyclerView.LayoutManager mGroupsLayoutManager;

    private Button mNewGroupBtn;

    ArrayList<Group> groups = new ArrayList<>();

    private String currentUserID;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        final View groupsView = inflater.inflate(R.layout.fragment_groups, container, false);
        groups.clear();
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRecyclerView = groupsView.findViewById(R.id.recycler);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mGroupsLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mGroupsLayoutManager);
        mGroupsAdapter = new GroupsAdapter(getDataSetMatches(), getActivity());
        mRecyclerView.setAdapter(mGroupsAdapter);

        getGroups();

        mNewGroupBtn = groupsView.findViewById(R.id.newGroupBtn);
        mNewGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(groupsView.getContext(), GroupActivity.class);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return groupsView;
    }

    private void getGroups() {
        DatabaseReference groupsDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("groups");
        groupsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for(DataSnapshot group : dataSnapshot.getChildren()){
                        FetchGroupInformation(group.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void FetchGroupInformation(final String key) {
        DatabaseReference groupsDb = FirebaseDatabase.getInstance().getReference().child("Groups").child(key);
        groupsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String name = "";
                    String chatId = "";
                    if(dataSnapshot.child("name").getValue() != null && dataSnapshot.child("chatId").getValue() != null){
                        name = dataSnapshot.child("name").getValue().toString();
                        chatId = dataSnapshot.child("chatId").getValue().toString();
                        Group obj = new Group(name, key, chatId);
                        groups.add(obj);
                        mGroupsAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Subscribe
    public void onEvent(NewGroupEvent event) {
        groups.add(event.newGroup);
        mGroupsAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mGroupsAdapter);
    }

    @Subscribe
    public void onEvent(RemoveGroupEvent event) {
        String deadGroupKey = event.deadGroupKey;
        Group deadGroup = null;
        for (Group group : groups)
        {
            if(group.getKey().equals(deadGroupKey))
            {
                deadGroup = group;
                break;
            }
        }
        groups.remove(deadGroup);
        mGroupsAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mGroupsAdapter);
    }

    private List<Group> getDataSetMatches() {
        return groups;
    }

}

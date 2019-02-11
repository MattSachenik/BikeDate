package bikedate.org.bikedate.Groups;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import bikedate.org.bikedate.GroupChatActivity;
import bikedate.org.bikedate.R;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsViewHolders>{
    private List<Group> groupsList;
    private Context context;

    public GroupsAdapter(List<Group> groupsList, Context context){
        this.groupsList = groupsList;
        this.context = context;
    }

    @NonNull
    @Override
    public GroupsViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        GroupsViewHolders gvh = new GroupsViewHolders(layoutView);

        return gvh;
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupsViewHolders holder, int position) {
        holder.mGroupName.setText(groupsList.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), GroupChatActivity.class);
                Bundle b = new Bundle();
                b.putString("groupName", groupsList.get(holder.getAdapterPosition()).getName());
                b.putString("key", groupsList.get(holder.getAdapterPosition()).getKey());
                b.putString("chatId", groupsList.get(holder.getAdapterPosition()).getChatId());
                intent.putExtras(b);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupsList.size();
    }

}

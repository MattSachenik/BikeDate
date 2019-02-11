package bikedate.org.bikedate.GroupMembers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import bikedate.org.bikedate.R;
import bikedate.org.bikedate.Riders.rider;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberViewHolders> {

    private List<rider> membersList;
    private Context context;
    private SparseBooleanArray itemStateArray= new SparseBooleanArray();

    public GroupMemberAdapter(List<rider> membersList, Context context){
        this.membersList = membersList;
        this.context = context;
    }

    @NonNull
    @Override
    public GroupMemberViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_member, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        GroupMemberViewHolders gmvh = new GroupMemberViewHolders(layoutView);

        return gmvh;
    }

    @Override
    public void onBindViewHolder(final GroupMemberViewHolders holder, final int position) {
        holder.mRiderName.setText(membersList.get(position).getName());
        holder.mRiderType.setText(membersList.get(position).getType());
        if(membersList.get(position).getImageUrl() != null && !membersList.get(position).getImageUrl().equals("default")){
            Glide.with(context).load(membersList.get(position).getImageUrl()).apply(RequestOptions.circleCropTransform()).into(holder.mRiderImage);
        }
    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }

}


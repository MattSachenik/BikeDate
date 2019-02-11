package bikedate.org.bikedate.Riders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import bikedate.org.bikedate.ChatActivity;
import bikedate.org.bikedate.R;
import bikedate.org.bikedate.RiderActivity;

public class ridersAdapter extends RecyclerView.Adapter<ridersViewHolders> {

    private List<rider> matchesList;
    private Context context;

    //Database
    private DatabaseReference chatDb;
    private DatabaseReference userDb;
    private FirebaseAuth mAuth;
    private String currentUId;

    public ridersAdapter(List<rider> matchesList, Context context){
        this.matchesList = matchesList;
        this.context = context;
    }

    @NonNull
    @Override
    public ridersViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rider, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ridersViewHolders rvh = new ridersViewHolders(layoutView);

        return rvh;
    }

    @Override
    public void onBindViewHolder(final ridersViewHolders holder, final int position) {
        holder.mRiderName.setText(matchesList.get(position).getName());
        holder.mRiderType.setText(matchesList.get(position).getType());
        if(matchesList.get(position).getImageUrl() != null && !matchesList.get(position).getImageUrl().equals("default")){
            Glide.with(context).load(matchesList.get(position).getImageUrl()).apply(RequestOptions.circleCropTransform()).into(holder.mRiderImage);
        }
        holder.mRiderChat.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                currentUId = mAuth.getCurrentUser().getUid();
                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                Bundle b = new Bundle();
                b.putString("riderId", matchesList.get(holder.getAdapterPosition()).getRiderId());
                b.putString("riderName", matchesList.get(holder.getAdapterPosition()).getName());
                b.putString("riderType", matchesList.get(holder.getAdapterPosition()).getType());
                intent.putExtras(b);
                view.getContext().startActivity(intent);
            }
        });
        holder.mAddToGroup.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view) {
                //AlertDialog.Builder mBuilder = new AlertDialog.Builder(view.getContext());
                //View mView = getLayoutInflater().inflate(R.layout.dialog_add_rider, null);
                //View mView =  LayoutInflater.inflate(R.layout.dialog_add_rider, null);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RiderActivity.class);
                Bundle b = new Bundle();
                b.putString("riderId", matchesList.get(holder.getAdapterPosition()).getRiderId());
                b.putBoolean("isFriend", true);
                intent.putExtras(b);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return matchesList.size();
    }

}

package bikedate.org.bikedate.AddRider;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import bikedate.org.bikedate.R;
import bikedate.org.bikedate.Riders.rider;

public class AddRiderAdapter extends RecyclerView.Adapter<AddRiderViewHolders> {

    private List<rider> matchesList;
    private Context context;
    private SparseBooleanArray itemStateArray= new SparseBooleanArray();

    //Database
    private DatabaseReference chatDb;
    private DatabaseReference userDb;
    private FirebaseAuth mAuth;
    private String currentUId;

    public AddRiderAdapter(List<rider> matchesList, Context context){
        this.matchesList = matchesList;
        this.context = context;
    }

    @NonNull
    @Override
    public AddRiderViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_addrider, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        AddRiderViewHolders arvh = new AddRiderViewHolders(layoutView);

        return arvh;
    }

    @Override
    public void onBindViewHolder(final AddRiderViewHolders holder, final int position) {
        holder.mRiderName.setText(matchesList.get(position).getName());
        holder.mRiderType.setText(matchesList.get(position).getType());
        if(matchesList.get(position).getImageUrl() != null && !matchesList.get(position).getImageUrl().equals("default")){
            Glide.with(context).load(matchesList.get(position).getImageUrl()).apply(RequestOptions.circleCropTransform()).into(holder.mRiderImage);
        }
        if (!itemStateArray.get(position, false)) {
            holder.mAddRiderCB.setChecked(false);}
        else {
            holder.mAddRiderCB.setChecked(true);
        }
        holder.mAddRiderCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!itemStateArray.get(position, false)) {
                    holder.mAddRiderCB.setChecked(true);
                    itemStateArray.put(position, true);
                }
                else  {
                    holder.mAddRiderCB.setChecked(false);
                    itemStateArray.put(position, false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return matchesList.size();
    }

    private ArrayList<rider> groupRiders = new ArrayList<>();
    public ArrayList<rider> getGroupRiders()
    {
        groupRiders.clear();
        for(Integer position = 0; position < matchesList.size(); position++){
            if (itemStateArray.get(position))
                groupRiders.add(matchesList.get(position));
        }
        return groupRiders;
    }

}


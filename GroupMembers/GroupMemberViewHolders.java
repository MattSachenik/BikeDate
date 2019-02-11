package bikedate.org.bikedate.GroupMembers;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import bikedate.org.bikedate.R;

public class GroupMemberViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView mRiderName, mRiderType;
    public ImageView mRiderImage;
    public GroupMemberViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mRiderName = itemView.findViewById(R.id.ridername);
        mRiderType = itemView.findViewById(R.id.ridertype);
        mRiderImage = itemView.findViewById(R.id.riderimage);
    }

    @Override
    public void onClick(View view) {
    }
}
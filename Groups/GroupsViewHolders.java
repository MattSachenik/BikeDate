package bikedate.org.bikedate.Groups;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import bikedate.org.bikedate.R;

public class GroupsViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView mGroupName;

    public GroupsViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mGroupName = itemView.findViewById(R.id.groupname);
    }

    @Override
    public void onClick(View v) {

    }
}

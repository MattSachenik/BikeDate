package bikedate.org.bikedate.AddRider;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import bikedate.org.bikedate.R;

public class AddRiderViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView mRiderName, mRiderType;
    public ImageView mRiderImage;
    public CheckBox mAddRiderCB;
    public AddRiderViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mRiderName = itemView.findViewById(R.id.ridername);
        mRiderType = itemView.findViewById(R.id.ridertype);
        mRiderImage = itemView.findViewById(R.id.riderimage);
        mAddRiderCB = itemView.findViewById(R.id.addRiderCB);
    }

    @Override
    public void onClick(View view) {
    }
}
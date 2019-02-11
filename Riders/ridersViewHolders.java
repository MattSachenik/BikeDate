package bikedate.org.bikedate.Riders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import bikedate.org.bikedate.R;

public class ridersViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView mRiderName, mRiderType;
    public ImageView mRiderImage, mRiderChat, mAddToGroup;
    public ridersViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mRiderName = itemView.findViewById(R.id.ridername);
        mRiderType = itemView.findViewById(R.id.ridertype);
        mRiderImage = itemView.findViewById(R.id.riderimage);
        mRiderChat = itemView.findViewById(R.id.riderchat);
        mAddToGroup = itemView.findViewById(R.id.addToGroup);
    }


    @Override
    public void onClick(View view) {
        //Toast.makeText(view.getContext(), "Hi", Toast.LENGTH_SHORT).show();
        //Intent intent = new Intent(view.getContext(), ChatActivity.class);
        //Bundle b = new Bundle();
        //b.putString("matchId", mMatchId.getText().toString());
        //intent.putExtras(b);
        //view.getContext().startActivity(intent);

    }

}

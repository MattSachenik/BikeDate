package bikedate.org.bikedate.Chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import bikedate.org.bikedate.R;

public class ChatViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView mMessage, mName, mTime;
    public LinearLayout mContainer, mNameTimeContainer, mLeftorRight;
    public ChatViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMessage = itemView.findViewById(R.id.message);
        mName = itemView.findViewById(R.id.nameTV);
        mTime = itemView.findViewById(R.id.timeTV);
        mContainer = itemView.findViewById(R.id.container);
        mNameTimeContainer = itemView.findViewById(R.id.nametimecontainer);
        mLeftorRight = itemView.findViewById(R.id.leftoright);
    }

    @Override
    public void onClick(View view) {
    }
}
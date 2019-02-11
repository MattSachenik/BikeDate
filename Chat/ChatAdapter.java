package bikedate.org.bikedate.Chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bikedate.org.bikedate.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolders>{
    private List<ChatObject> chatList;
    private Context context;


    public ChatAdapter(List<ChatObject> matchesList, Context context){
        this.chatList = matchesList;
        this.context = context;
    }

    @Override
    public ChatViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ChatViewHolders rcv = new ChatViewHolders(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(final ChatViewHolders holder, int position) {
        //Before anything get the user's name
        DatabaseReference creatorDb = FirebaseDatabase.getInstance().getReference().child("Users").child(chatList.get(position).getCreator());
        creatorDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String creatorName = "";
                    if (dataSnapshot.child("name").getValue() != null && !dataSnapshot.child("name").getValue().toString().isEmpty()) {
                        creatorName = dataSnapshot.child("name").getValue().toString();
                        holder.mName.setText(creatorName);
                    }
                    else if (dataSnapshot.child("username").getValue() != null)
                    {
                        creatorName = dataSnapshot.child("username").getValue().toString();
                        holder.mName.setText(creatorName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.mMessage.setText(chatList.get(position).getMessage());
        if(chatList.get(position).getCurrentUser()){
            /*
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.END;
            holder.mMessage.setLayoutParams(params);
            holder.mNameTimeContainer.setLayoutParams(params);
            */
            //holder.mMessage.setTextColor(Color.parseColor("#404040"));
            //holder.mContainer.setBackgroundColor(Color.parseColor("#F4F4F4"));
            holder.mContainer.setBackground(context.getResources().getDrawable(R.drawable.background_gray_rounded));
            //holder.mLeftorRight.setGravity(Gravity.END);
        }
        else{
            //holder.mLeftorRight.setGravity(Gravity.START);
            /*
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.START;
            holder.mNameTimeContainer.setLayoutParams(params);
            holder.mMessage.setLayoutParams(params);
            */
            //holder.mMessage.setTextColor(Color.parseColor("#404040"));
            //holder.mContainer.setBackgroundColor(Color.parseColor("#2DB4C8"));
            holder.mContainer.setBackground(context.getResources().getDrawable(R.drawable.backgroud_blue_chat));
        }
        //Show time if available
        if(chatList.get(position).getTime() != 0) {
            long offset = System.currentTimeMillis() - chatList.get(position).getTime();
            long timeSent = System.currentTimeMillis() - offset;
            Date creationDate = new Date(timeSent);
            Calendar cal = Calendar.getInstance();
            cal.setTime(creationDate);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy'\n'hh:mm a");
            holder.mTime.setText(dateFormat.format(cal.getTime()));
        }
        else{
            holder.mTime.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}
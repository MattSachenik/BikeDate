package bikedate.org.bikedate.InfiniteAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import bikedate.org.bikedate.R;

public class ProfileInfiniteAdapter extends LoopingPagerAdapter<String> {
    private static final int VIEW_TYPE_NORMAL = 100;
    ///private ArrayList<String> itemList;
    //private static final int VIEW_TYPE_SPECIAL = 101;

    public ProfileInfiniteAdapter(Context context, ArrayList<String> itemList, boolean isInfinite) {
        super(context, itemList, isInfinite);
        //this.itemList = itemList;
    }

    @Override
    protected int getItemViewType(int listPosition) {
        //if (itemList.get(listPosition) == 0)
        //    return VIEW_TYPE_SPECIAL;
        return VIEW_TYPE_NORMAL;
    }

    @Override
    protected View inflateView(int viewType, ViewGroup container, int listPosition) {
        //if (viewType == VIEW_TYPE_SPECIAL)
        //    return LayoutInflater.from(context).inflate(R.layout.item_special, container, false);
        return LayoutInflater.from(context).inflate(R.layout.item_profile_images, container, false);
    }

    @Override
    protected void bindView(View convertView, int listPosition, int viewType) {
        //if (viewType == VIEW_TYPE_SPECIAL)
        //    return;
        ImageView image= convertView.findViewById(R.id.image);
        Glide.with(convertView.getContext()).load(itemList.get(listPosition)).into(image);
    }
}

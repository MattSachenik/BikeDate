package bikedate.org.bikedate.InfiniteAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.asksira.loopingviewpager.LoopingPagerAdapter;

import java.util.ArrayList;

import bikedate.org.bikedate.R;

public class BikeTypeInfiniteAdapter extends LoopingPagerAdapter<Integer> {

    private static final int VIEW_TYPE_NORMAL = 100;
    //private static final int VIEW_TYPE_SPECIAL = 101;

    public BikeTypeInfiniteAdapter(Context context, ArrayList<Integer> itemList, boolean isInfinite) {
        super(context, itemList, isInfinite);
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
        return LayoutInflater.from(context).inflate(R.layout.item_pager, container, false);
    }

    @Override
    protected void bindView(View convertView, int listPosition, int viewType) {
        //if (viewType == VIEW_TYPE_SPECIAL)
        //    return;
        convertView.findViewById(R.id.image).setBackground(context.getResources().getDrawable(getBackgroundImage(listPosition)));
        TextView description = convertView.findViewById(R.id.description);
        description.setText(getDescription(listPosition));
    }


    private int getBackgroundImage (int number) {
        switch (number) {
            case 0:
                return R.drawable.singlespeed;
            case 1:
                return R.drawable.mountain;
            case 2:
                return R.drawable.road;
            case 3:
                return R.drawable.cruiser;
            case 4:
                return R.drawable.electric;
            case 5:
                return R.drawable.singlespeed;
            default:
                return R.drawable.singlespeed;
        }
    }

    private String getDescription (int number) {
        switch (number) {
            case 0:
                return "Single Speed";
            case 1:
                return "Mountain";
            case 2:
                return "Road";
            case 3:
                return "Cruiser / Touring";
            case 4:
                return "Electric";
            case 5:
                return "Default";
            default:
                return "Default";
        }
    }
}
package bikedate.org.bikedate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class OptionsFragment extends Fragment {
    private static final String TAG = "Options";

    ImageView mSettingsIV, mCustomizeProfileIV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_options,container,false);

        //Connect
        mSettingsIV = view.findViewById(R.id.settingsIV);
        mCustomizeProfileIV = view.findViewById(R.id.customizeProfileIV);

        /*
        //Set Button Actions
        mFindRidersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionsFragment.this, MainFragment.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        mGreenSwipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionsFragment.this, MainFragment.class));
                finish();
            }
        });
        */
        mCustomizeProfileIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CustomizeActivity.class));
            }
        });

        mSettingsIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });
        /*
        mOtherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OptionsFragment.this, MainFragment.class));
                finish();
            }
        });
        */
        return view;
    }
}

package bikedate.org.bikedate;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import bikedate.org.bikedate.ViewModels.RidersViewModel;

public class TabActivity extends AppCompatActivity {

    private static final String TAG = "TabActivity";

    private ViewPager mViewPager;

    @Override
    protected void onStart() {
        super.onStart();
        /*
        if( getIntent().getExtras() != null)
        {
            Toast.makeText(this,"working", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "Not WOrking", Toast.LENGTH_SHORT).show();
        */
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        //ViewModels
        RidersViewModel model = ViewModelProviders.of(this).get(RidersViewModel.class);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout mTabLayout = findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        setupViewPager(mViewPager);
        mTabLayout.getTabAt(0).setIcon(R.drawable.settings);
        mTabLayout.getTabAt(1).setIcon(R.drawable.bike);
        mTabLayout.getTabAt(2).setIcon(R.drawable.people);
        mTabLayout.getTabAt(3).setIcon(R.drawable.location);

        //Set Homepage
        mViewPager.setCurrentItem(1);
    }

    public void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new OptionsFragment(), "Options");
        adapter.addFragment(new MainFragment(), "Search");
        adapter.addFragment(new FriendsFragment(), "Social");
        adapter.addFragment(new MapFragment(), "Map");
        viewPager.setAdapter(adapter);
    }

}


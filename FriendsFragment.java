package bikedate.org.bikedate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FriendsFragment extends Fragment {

    private ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends,container,false);
        // Set up the ViewPager with the sections adapter.
        mViewPager = view.findViewById(R.id.container);
        setupViewPager(mViewPager);
        TabLayout mTabLayout = view.findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        setupViewPager(mViewPager);

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getChildFragmentManager());
        adapter.addFragment(new MatchesFragment(), "Riders");
        adapter.addFragment(new ChatsFragment(), "Chats");
        adapter.addFragment(new GroupsFragment(), "Groups");
        viewPager.setAdapter(adapter);
    }

    /*
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return result;
    }
    */

}

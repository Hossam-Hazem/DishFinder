package layout;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pc.dishfinder.Place;
import com.example.pc.dishfinder.R;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {
    TabAdapter tabAdapter;

    public MainFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View fragmentView =  inflater.inflate(R.layout.fragment_main, container, false);

        ViewPager viewPager = (ViewPager) fragmentView.findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) fragmentView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return fragmentView;
    }

    private void setupViewPager(ViewPager viewPager) {
        tabAdapter = new TabAdapter(getFragmentManager());
        tabAdapter.addFragment(new PlaceAllFragment(), "All Places");
        tabAdapter.addFragment(new PlaceFavoritesFragment(), "Favorites");
        viewPager.setAdapter(tabAdapter);
    }

    public void removeFavoriteFromAdapter(Place item) {
        ((PlaceFavoritesFragment)tabAdapter.getItem(1)).removeFavoriteFromAdapter(item);

    }

    public void addFavoriteToAdapter(Place item) {
        ((PlaceFavoritesFragment)tabAdapter.getItem(1)).addFavoriteToAdapter(item);
    }

    static class TabAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public TabAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }



}

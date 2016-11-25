package com.example.pc.dishfinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import layout.MainFragment;
import layout.PlaceAllFragment;
import layout.PlaceDetailFragment;

public class MainActivity extends PlaceParentActivity implements PlaceAllFragment.TwoPaneInterface {

    boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceAllFragment())
//                    .commit();
//        }

        if (findViewById(R.id.place_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                PlaceDetailFragment placeDetailFragment = new PlaceDetailFragment();
                Bundle bundle = new Bundle();
                // bundle.putSerializable("movieDetails",place);
                bundle.putBoolean("twoPane", true);
                placeDetailFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.place_detail_container, placeDetailFragment)
                        .commit();
            }

        } else {
            mTwoPane = false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Class intentClass;

        switch (id) {
            case R.id.action_settings:
                intentClass = MainSettingsActivity.class;
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        startActivity(new Intent(this, intentClass));
        return true;
    }


    @Override
    public void listItemClickCallback(Place place) {
        if (mTwoPane) {
            openMovieDetailTwoPane(place);
        } else {
            openPlaceDetailOnePane(place);
        }
    }

    private void openMovieDetailTwoPane(Place place) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.place_detail_container, PlaceDetailFragment.newFragmentWithBundle(place, true))
                .addToBackStack(null)
                .commit();
    }

    private void OpenPlaceDetailTwoPaneInit(Place place) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.place_detail_container, PlaceDetailFragment.newFragmentWithBundle(place, true))
                .commit();
    }

    private void openPlaceDetailOnePane(Place place) {
        Intent intent = new Intent(this, PlaceDetailActivity.class);
        intent.putExtra(PlaceDetailActivity.PLACE_SERIALIZABLE_KEY, place);

        startActivity(intent);
    }

    public void removePlaceFromFavorites(Place item) {
        MainFragment fragment = (MainFragment) this.getSupportFragmentManager().findFragmentById(R.id.mainFragment);
        fragment.removeFavoriteFromAdapter(item);
    }

    public void addPlaceToFavorites(Place item) {
        MainFragment fragment = (MainFragment) this.getSupportFragmentManager().findFragmentById(R.id.mainFragment);
        fragment.addFavoriteToAdapter(item);
    }

    public void onAdapterFinish(ArrayList<Place> places) {
        if (mTwoPane && !places.isEmpty()) {
            OpenPlaceDetailTwoPaneInit(places.get(0));
        }
    }

}

package layout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.pc.dishfinder.MainActivity;
import com.example.pc.dishfinder.Place;
import com.example.pc.dishfinder.PlaceDetailActivity;
import com.example.pc.dishfinder.PlaceDetailsConnector;
import com.example.pc.dishfinder.PlaceParentActivity;
import com.example.pc.dishfinder.R;
import com.squareup.picasso.Picasso;

import static com.example.pc.dishfinder.PlaceDetailActivity.PLACE_SERIALIZABLE_KEY;

public class PlaceDetailFragment extends Fragment implements View.OnClickListener{
    Place placeDetails;
    View fragmentView;
    boolean isFavorite;
    boolean twoPane;
    ShareActionProvider mShareActionProvider;


    public PlaceDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (!getArguments().containsKey(PlaceDetailActivity.PLACE_SERIALIZABLE_KEY)) {
            return null;
        }
        twoPane = getArguments().getBoolean("twoPane");
        fragmentView = inflater.inflate(R.layout.fragment_place_detail, container, false);
        AppCompatActivity currentActivity = (AppCompatActivity) getActivity();
        if (!twoPane) {
            Toolbar toolbar = (Toolbar) fragmentView.findViewById(R.id.toolbar);
            currentActivity.setSupportActionBar(toolbar);
            currentActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                fragmentView.findViewById(R.id.place_detail_container).setFitsSystemWindows(true);
            }
        }

        placeDetails = (Place) getArguments().getSerializable("placeDetails");
        isFavorite = placeDetails.isFavorite(getContext());

        PlaceDetailsConnector connector = new PlaceDetailsConnector(placeDetails, new PlaceDetailsConnector.OnFinishCallback() {
            @Override
            public void onFinished() {
                linkPlaceDetailsUI();
            }
        });
        connector.execute();

        ((FloatingActionButton) fragmentView.findViewById(R.id.favorite_button)).setOnClickListener(this);
        ((Button) fragmentView.findViewById(R.id.placeReviewsButton)).setOnClickListener(this);
        ((ImageView) fragmentView.findViewById(R.id.backdrop)).setOnClickListener(this);
        ((Button) fragmentView.findViewById(R.id.placeMapButton)).setOnClickListener(this);

        setFavButton(fragmentView);

        return fragmentView;
    }



//    public void setShareProviderIntent(Trailer trailer) { //TODO
//        if (mShareActionProvider != null) {
//            mShareActionProvider.setShareIntent(
//                    ((MovieParentActivity) getActivity())
//                            .createShareMovieIntent(
//                                    movieDetails.getName(), trailer.getURL()));
//
//        } else {
//            Log.d(PlaceDetailFragment.class.getSimpleName(), "Share Action Provider is null?");
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.favorite_button: {
                if (isFavorite) {
                    removePlaceFavorite(v);
                } else {
                    setPlaceFavorite(v);
                }
                break;
            }
            case R.id.placeReviewsButton: {
                Bundle bundle = new Bundle();
                bundle.putSerializable("reviews", placeDetails.getReviews());
                ((PlaceParentActivity) getActivity()).openReviewsFragment(bundle);
                break;
            }
//            case R.id.movie_trailers_button: {
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("trailers", trailersList);
//                ((MovieParentActivity) getActivity()).openTrailersFragment(bundle);
//                break;
//            }
            case R.id.backdrop: {
                Bundle bundle = new Bundle();
                bundle.putString("uri", placeDetails.getDefaultImageURL());
                ((PlaceParentActivity) getActivity()).openImageFragment(bundle);
                break;
            }
            case R.id.placeMapButton: {
                // Create a Uri from an intent string. Use the result to create an Intent.
                Uri gmmIntentUri = Uri.parse("geo:"+placeDetails.getLat()+","+placeDetails.getLng()+"?z=19");

                // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                // Make the Intent explicit by setting the Google Maps package
                mapIntent.setPackage("com.google.android.apps.maps");

                // Attempt to start an activity that can handle the Intent
                startActivity(mapIntent);
            }

        }
    }

    private void setToolbar() {
//        final Toolbar toolbar = (Toolbar) fragmentView.findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void linkPlaceDetailsUI() {
        if (placeDetails != null) {
            setPlaceTitle();
            loadBackdrop();
            setPlaceRating();
            setPlaceAddress();
            setPlacePhoneNumber();
            setPlaceWebsite();
            setPlaceType();
        }
    }

    private void setPlaceRating() {
        final RatingBar ratingBar = (RatingBar) fragmentView.findViewById(R.id.ratingBar);
        float fiveBasedRating = Float.parseFloat(placeDetails.getRating()) / 2;
        ratingBar.setRating(fiveBasedRating);
    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) fragmentView.findViewById(R.id.backdrop);
        Picasso.with(getActivity()).load(placeDetails.getDefaultImageURL()).into(imageView);
    }

    private void setPlaceTitle() {
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) fragmentView.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(placeDetails.getName());
    }

    private void setPlacePhoneNumber(){
        final TextView textView = (TextView) fragmentView.findViewById(R.id.phoneNumber);
        textView.setText(placeDetails.getPhoneNumber());
    }

    private void setPlaceAddress(){
        final TextView textView = (TextView) fragmentView.findViewById(R.id.address);
        textView.setText(placeDetails.getAddress());
    }

    private void setPlaceWebsite(){
        final TextView textView = (TextView) fragmentView.findViewById(R.id.website);
        textView.setText(placeDetails.getWebsite());
    }

    private void setPlaceType(){
        final TextView textView = (TextView) fragmentView.findViewById(R.id.placeType);
        textView.setText(placeDetails.getTypeString());
    }
    private void setFavButtonUnFavorite(View v) {
        FloatingActionButton button = (FloatingActionButton) v.findViewById(R.id.favorite_button);

        button.setImageDrawable(getResources().getDrawable(R.drawable.ic_unfavorite));

        setFavButtonBackground(button, 1);
    }

    private void setFavButtonFavorite(View v) {
        FloatingActionButton button = (FloatingActionButton) v.findViewById(R.id.favorite_button);

        button.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite));

        setFavButtonBackground(button, 0);
    }

    private void setFavButton(View v, int mode) {
        switch (mode) {
            case 0:
                setFavButtonFavorite(v);
                break;
            case 1:
                setFavButtonUnFavorite(v);
                break;
        }
    }

    private void setFavButtonBackground(FloatingActionButton button, int mode) {
        switch (mode) {
            case 0:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    button.getBackground().setColorFilter(Color.parseColor("#ff80cbc4"), PorterDuff.Mode.MULTIPLY);
                break;
            case 1: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    button.getBackground().setColorFilter(Color.parseColor("#ff009688"), PorterDuff.Mode.MULTIPLY);
                break;
            }

        }
    }

    private void setFavButton(View v) {
        if (isFavorite) {
            setFavButton(v, 1);
        } else {
            setFavButton(v, 0);
        }
    }

    public void setPlaceFavorite(View v) {
        boolean isSuccess = placeDetails.setFavorite(getContext());
        if (isSuccess) {
            isFavorite = true;
            if (twoPane)
                ((MainActivity) getActivity()).addPlaceToFavorites(placeDetails);
            setFavButton(v);
        } else {
            throw new UnsupportedOperationException("error fel insert favorite");
        }
    }

    public void removePlaceFavorite(View v) {
        boolean isSuccess = placeDetails.removeFavorite(getContext());

        if (isSuccess) {
            isFavorite = false;
            if (twoPane)
                ((MainActivity) getActivity()).removePlaceFromFavorites(placeDetails);
            setFavButton(v);
        } else {
            throw new UnsupportedOperationException("error fel delete favorite");
        }
    }

    public static PlaceDetailFragment newFragmentWithBundle(Place place, boolean twoPane) {
        PlaceDetailFragment fragment = new PlaceDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("twoPane", twoPane);
        bundle.putSerializable(PLACE_SERIALIZABLE_KEY, place);
        fragment.setArguments(bundle);
        return fragment;
    }

}

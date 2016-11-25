package layout;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.pc.dishfinder.Place;
import com.example.pc.dishfinder.R;

public class PlaceFavoritesFragment extends PlaceListFragment {


    public PlaceFavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        connector.execute("favorites","-33.8670522,151.1957362","10500");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_place_favorites, container, false);

        super.onCreateViewInit(fragmentView);

        GridView gridView = (GridView) fragmentView.findViewById(R.id.gird_item_place);

        gridView.setEmptyView(fragmentView.findViewById(R.id.empty));

        return fragmentView;
    }

    public void addFavoriteToAdapter(Place item) {
        mPlacesAdapter.add(item);
    }

    public void removeFavoriteFromAdapter(Place item) {
        mPlacesAdapter.remove(item);
    }
}

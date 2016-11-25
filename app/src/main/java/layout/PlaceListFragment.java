package layout;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pc.dishfinder.MainActivity;
import com.example.pc.dishfinder.Place;
import com.example.pc.dishfinder.PlacesListConnector;
import com.example.pc.dishfinder.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlaceListFragment extends Fragment {
    public interface OnFinishAdapterEmpty {
        public void onFinished();
    }
    protected PlaceAllFragment.PlaceAdapter mPlacesAdapter;
    protected PlacesListConnector connector;
    protected OnFinishAdapterEmpty onFinishAdapterEmpty;
    @Override
    public void onStart() {
        super.onStart();
        connector = new PlacesListConnector(getContext(), mPlacesAdapter, new PlacesListConnector.OnFinishCallback() {
            @Override
            public void onFinished(ArrayList<Place> places) {
                if (places.isEmpty() && onFinishAdapterEmpty != null) {
                    onFinishAdapterEmpty.onFinished();
                } else {
                    ((MainActivity) getActivity()).onAdapterFinish(places);
                }

            }
        });


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPlacesAdapter = new PlaceAdapter(getActivity());
    }

    public void onCreateViewInit(View fragmentView){
        GridView gridView = (GridView) fragmentView.findViewById(R.id.gird_item_place);
        gridView.setAdapter(mPlacesAdapter);
        gridView.setOnItemClickListener( new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Place item = (Place) mPlacesAdapter.getItem(position);
                ((TwoPaneInterface) getActivity()).listItemClickCallback(item);


            }
        });
    }



    public class PlaceAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<Place> placesList;

        public PlaceAdapter(Context c)
        {
            mContext = c;
            placesList = new ArrayList<>();

        }
        public PlaceAdapter(Context c, ArrayList<Place> places) {
            mContext = c;
            placesList = places;

        }
        public int getCount() {
            return placesList.size();
        }

        public void add(Place item){
            placesList.add(item);
            super.notifyDataSetChanged();
        }
        public void clear(){
            placesList.clear();
            super.notifyDataSetChanged();
        }

        public void remove(Place item) {
            placesList.remove(item);
            super.notifyDataSetChanged();
        }

        public Object getItem(int position) {
            return placesList.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            View view;
            LayoutInflater inflater = LayoutInflater.from(mContext);
            if(convertView == null){
                view = inflater.inflate(R.layout.grid_item_places,parent,false);

            }
            else{
                view = convertView;
            }
            imageView = (ImageView) view.findViewById(R.id.grid_item_place_imageView);
            Picasso.with(mContext).load(placesList.get(position).getLogoImageURL()).resize(400,400).centerCrop().into(imageView);
            return view;
        }
    }

    public interface TwoPaneInterface {
        void listItemClickCallback(Place placeItem);
    }

    public void initOnFinishAdapterEmpty(final String message) {
        onFinishAdapterEmpty = new OnFinishAdapterEmpty() {
            @Override
            public void onFinished() {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                onFinishAdapterEmpty = null;
            }
        };
    }
}

package com.example.pc.dishfinder;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.example.pc.dishfinder.data.PlaceContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import layout.PlaceAllFragment;


public class PlacesListConnector extends AsyncTask<String,Void,ArrayList<Place>> {
    public interface OnFinishCallback{
        void onFinished(ArrayList<Place> places);
    }
    final String BASEURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    PlaceAllFragment.PlaceAdapter adapter;
    Context mContext;
    OnFinishCallback onFinishCallback;

    public PlacesListConnector(Context mContext, PlaceAllFragment.PlaceAdapter adapter){
        this.adapter = adapter;
        this.mContext = mContext;
    }
    public PlacesListConnector(Context mContext, PlaceAllFragment.PlaceAdapter adapter, OnFinishCallback onFinishCallback){
        this.adapter = adapter;
        this.mContext = mContext;
        this.onFinishCallback = onFinishCallback;
    }

    @Override
    protected ArrayList<Place> doInBackground(String... params) {
        return getPlacesList(params[0], params[1], params[2]);
    }

    @Override
    protected void onPostExecute(ArrayList<Place> places) {
        super.onPostExecute(places);
        adapter.clear();
        Iterator<Place> i = places.iterator();
        while(i.hasNext()){
            adapter.add(i.next());
        }

        onFinishCallback.onFinished(places);

    }

    public ArrayList<Place> getPlacesList(String ACTION, String location, String radius){
        if(ACTION.equals("favorites")){
            return getPlacesListFromDB(ACTION);
        }
        else{
            return getPlacesListFromWeb(location, radius);
        }

    }
    private ArrayList<Place> getPlacesListFromDB(String ACTION) {
        return PlaceContract.FavoriteEntry.getFavorites(mContext);
    }

    @Nullable
    public static ArrayList<Place> getPlaceDataFromJson(String placesJSONStr){
        JSONArray placesJSON;
        ArrayList<Place> places = new ArrayList<>();
        try {
            if(placesJSONStr != null) {
                placesJSON = (new JSONObject(placesJSONStr)).getJSONArray("results");
                int total;
                total = placesJSON.length();
                JSONObject placeJSON;
                String placeId;
                String placeName;
                Boolean placeIsOpen;
                String placeRating;
                ArrayList<String> placeTypes;
                String placeLng;
                String placeLat;
                ArrayList<String> placePictures;
                for (int c = 0; c < total; c++) {
                    placeJSON = placesJSON.getJSONObject(c);

                    placeId = placeJSON.getString("place_id");
                    placeName = placeJSON.getString("name");
                    if(placeJSON.has("opening_hours") && placeJSON.getJSONObject("opening_hours").has("open_now")) {
                        placeIsOpen = placeJSON.getJSONObject("opening_hours").getBoolean("open_now");
                    }
                    else{
                        placeIsOpen = null;
                    }
                    placeRating = placeJSON.optString("rating","0");
                    placeTypes = new ArrayList<>();
                    JSONArray placeTypesJSON = placeJSON.getJSONArray("types");
                    for(int i = 0;i<placeTypesJSON.length();i++){
                        placeTypes.add(placeTypesJSON.getString(i));
                    }
                    JSONObject locationJSON = placeJSON.getJSONObject("geometry").getJSONObject("location");
                    placeLng = locationJSON.getString("lng");
                    placeLat = locationJSON.getString("lat");
                    placePictures = new ArrayList<>();
                    if(placeJSON.has("photos")) {
                        JSONArray placePicturesJSON = placeJSON.optJSONArray("photos");
                        for (int i = 0; i < placePicturesJSON.length(); i++) {
                            JSONObject photo = placePicturesJSON.getJSONObject(i);
                            placePictures.add(photo.getString("photo_reference"));
                        }
                    }

                    Place place = new Place(
                            placeId,
                            placeName,
                            placeIsOpen,
                            placeRating,
                            placeLat,
                            placeLng,
                            placeTypes,
                            placePictures);
                    places.add(place);
                }
            }

            return places;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    private ArrayList<Place> getPlacesListFromWeb(String location,String radius){
        final String API_KEY = MyConfig.GOOGLE_PLACES_API_KEY;
        final String API_PARAM = "key";
        final String TYPE_PARAM = "types";
        final String TYPE_DATA="bar|cafe|casino";
        final String RADIUS_PARAM = "radius";
        final String LOCATION_PARAM = "location";


        Uri uri =  Uri.parse(BASEURL).buildUpon()
                .appendQueryParameter(API_PARAM,API_KEY)
                .appendQueryParameter(TYPE_PARAM,TYPE_DATA)
                .appendQueryParameter(RADIUS_PARAM,radius)
                .appendQueryParameter(LOCATION_PARAM,location)
                .build();

        String JSONStr = MyConnection.connect(uri.toString());

        return getPlaceDataFromJson(JSONStr);
    }
}


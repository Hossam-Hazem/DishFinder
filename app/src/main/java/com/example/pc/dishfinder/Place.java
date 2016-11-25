package com.example.pc.dishfinder;


import android.content.Context;
import android.net.Uri;

import com.example.pc.dishfinder.data.PlaceContract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class Place implements Serializable {
    private String id;
    private String name;
    private Boolean isOpen;
    private String rating;
    private String lat;
    private String lng;
    private String phoneNumber;
    private String address;
    private String website;
    private ArrayList<String> type;
    private ArrayList<String> PhotoIds;
    private ArrayList<Review> reviews;
    private String logoId;

    private final String PHOTO_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo";
    public Place(String id, String name, boolean isOpen, String rating, String lat, String lng, String phoneNumber, String address, String website, ArrayList<String> type, ArrayList<String> photoIds, ArrayList<Review> reviews) {
        this.id = id;
        this.name = name;
        this.isOpen = isOpen;
        this.rating = rating;
        this.type = type;
        this.lat = lat;
        this.lng = lng;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.website = website;
        this.PhotoIds = photoIds;
        this.reviews = reviews;
        this.logoId = photoIds.get(0);
    }

    public Place(String id, String name, Boolean isOpen, String rating, String lat, String lng, ArrayList<String> type, ArrayList<String> photoIds) {
        this.id = id;
        this.name = name;
        this.isOpen = isOpen;
        this.rating = rating;
        this.lat = lat;
        this.lng = lng;
        this.type = type;
        this.PhotoIds = photoIds;
        this.logoId = !photoIds.isEmpty()? photoIds.get(0) : null;
    }

    public Place(String id, String name, String lat, String lng, String phoneNumber, String address, String rating, String website) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.rating = rating;
        this.website = website;
    }
    public Place(String id, String name, String lat, String lng, String phoneNumber, String address, String rating, String website, String logo) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.rating = rating;
        this.website = website;
        this.logoId = logo;
    }

    public void setAdditionalInfo(String phoneNumber, String address, String website, ArrayList<Review> reviews, ArrayList<String> photosId, ArrayList<String> types){
        this.address = address;
        this.website = website;
        this.phoneNumber = phoneNumber;
        this.reviews = reviews;
        this.PhotoIds = photosId;
        this.type = types;
        this.logoId = !photosId.isEmpty()? photosId.get(0) : null;
    }

    public String getName() {
        return name;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public ArrayList<String> getPhotoIds() {
        return PhotoIds;
    }

    public String getId() {
        return id;
    }

    public String getRating() {
        return rating;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getLoc(){
        //TODO
        return null;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getWebsite() {
        return website;
    }

    public ArrayList<String> getType() {
        return type;
    }

    public ArrayList<Review> getReviews(){
        return reviews;
    }

    public String getTypeString(){
        Iterator<String> i = type.iterator();
        String result = "";
        if(i.hasNext())
            result += i.next();
        while(i.hasNext()){
            result += ", "+i.next();
        }
        return result;
    }

    public String getLogo() {
        return logoId != null ? logoId : "";
    }

    public String getLogoImageURL(){
        if(logoId != null && !logoId.isEmpty()) {
            return getImage("400","400",logoId);
        }
        else{

            return "https://pbs.twimg.com/profile_images/600060188872155136/st4Sp6Aw.jpg";
        }
    }

    public String getDefaultImageURL() {
        if(!PhotoIds.isEmpty()) {
            return getImage("1700",null,getPhotoIds().get(0));
        }
        else{
            return "https://lh4.googleusercontent.com/RuUa24h5MU1ukiNHkpy5Z78YEMrUPQ_ltx3UgXPTWlGRM4p0c3sFDeo1Bs1ZKrmkt9FXQlG4YBBB4mY=w1152-h734";
        }
    }

    public String getImage(String maxWidth,String maxHeight, String ref){
        final String API_KEY = MyConfig.GOOGLE_PLACES_API_KEY;
        final String API_PARAM = "key";
        final String MAX_WIDTH_PARAM = "maxwidth";
        final String PHOTO_REFERENCE_PARAM = "photoreference";
        final String MAX_HEIGHT_PARAM = "maxheight";
        Uri.Builder uriBuilder = Uri.parse(PHOTO_BASE_URL).buildUpon()
                .appendQueryParameter(API_PARAM, API_KEY)
                .appendQueryParameter(PHOTO_REFERENCE_PARAM, ref);
        if(maxWidth != null)
            uriBuilder.appendQueryParameter(MAX_WIDTH_PARAM, maxWidth);
        if(maxHeight != null)
            uriBuilder.appendQueryParameter(MAX_HEIGHT_PARAM, maxHeight);
        return uriBuilder.build().toString();
    }

    public boolean isFavorite(Context context){
        return PlaceContract.FavoriteEntry.checkPlaceExistsById(context,id);
    }

    public boolean setFavorite(Context context){
        return PlaceContract.FavoriteEntry.insert(
                context,
                this
        );
    }
    public boolean removeFavorite(Context context){
        return PlaceContract.FavoriteEntry.delete(
                context,
                this.id
        );
    }
}

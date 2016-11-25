package layout;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pc.dishfinder.R;

import java.util.Calendar;

public class PlaceAllFragment extends PlaceListFragment implements LocationListener {
    PermissionCallback locationPermissionCallback;
    LocationCallback getLocationCallback;
    LocationManager mLocationManager;
    Location location;
    final String DEFAULT_LOCATION = "30.0444,31.2357";

    public PlaceAllFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final int searchRadius = prefs.getInt(getString(R.string.searchRadius), 20);
        getLocationCallback = new LocationCallback() {
            @Override
            public void onSuccess(Location location) {
                String StrLoc;
                if (location == null) {
                    StrLoc = DEFAULT_LOCATION;
                } else {
                    StrLoc = location.getLatitude() + "," + location.getLongitude();
                }

                connector.execute("normal", StrLoc, (searchRadius * 1000) + "");
            }

            @Override
            public void onFail() {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                prefs.getInt(getString(R.string.searchRadius), 10);
                connector.execute("normal", DEFAULT_LOCATION, (searchRadius * 1000) + "");
            }
        };
        locationPermissionCallback = new PermissionCallback() {
            @Override
            public void onAccept() {
                getGPSLocation(getLocationCallback);
            }

            @Override
            public void onReject() {
                connector.execute("normal", DEFAULT_LOCATION, (searchRadius * 1000) + "");
            }
        };
        initOnFinishAdapterEmpty("No places returned from API");
        requestLocation();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_place_all, container, false);

        super.onCreateViewInit(fragmentView);

        return fragmentView;
    }

    @SuppressWarnings({"MissingPermission"})
    public void getGPSLocation(LocationCallback locationCallback){
        if ( !mLocationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }
        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null || location.getTime() < Calendar.getInstance().getTimeInMillis() -  1000) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        else{
            locationCallback.onSuccess(location);
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        getLocationCallback.onFail();
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void requestLocation() {
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if(Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else {
                locationPermissionCallback.onAccept();
            }
        }
        else
            locationPermissionCallback.onAccept();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionCallback.onAccept();
                } else {
                    locationPermissionCallback.onReject();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    // Required functions
    public void onProviderDisabled(String arg0) {}
    public void onProviderEnabled(String arg0) {}

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager.removeUpdates(this);
            getLocationCallback.onSuccess(location);
        }
        else {
            getLocationCallback.onFail();
        }
    }

    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}

    public interface PermissionCallback {
        public void onAccept();
        public void onReject();
    }
    public interface LocationCallback{
        public void onSuccess(Location location);
        public void onFail();
    }
}

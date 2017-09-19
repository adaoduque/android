package localizacao.geo.geolocalizacao;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by adao on 2/9/17.
 */

public final class GPSTracker implements LocationListener {

    private final Context mContext;

    // flag for GPS status
    private boolean isGPSEnabled = false;

    // flag for network status
    private boolean isNetworkEnabled = false;

    // flag for GPS status
    private boolean canGetLocation = false;

    //Object to manipule location and status provider
    private LocationManager locationManager;
    private Location location;

    //Coordinates
    private double latitude;
    private double longitude;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE = 50; // 50 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME = 1000 * 60 * 20; // 20 minute

    /**
     * Constructor
     *
     * @return void
     */
    public GPSTracker(Context context) {

        //Set context application
        this.mContext = context;

        this.getLocation();

    }

    /**
     * Function to get the user's current location
     *
     * @return void
     */
    public void getLocation() {

        //Create instance of class LocationManager
        this.locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        //Getting GPS status, GPS is enabled ?
        this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        //Getting network status, Network is enabled ?
        this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        //Checks if can get Location
        if( !this.isGPSEnabled && !this.isNetworkEnabled ) {
            //No GPS and network provider is enabled :(
        }else {

            //GPS or/and Network is enabled
            this.canGetLocation = true;

            //Checks is GPS enabled ?
            if ( this.isGPSEnabled ) {

                //Can permission to access GPS Provider ?
                try {

                    //Set provider (GPS), Min time update, min distance update and the listener
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME,
                            MIN_DISTANCE, this);

                    //Get last know location by GPS
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                } catch (SecurityException e) {
                    Log.d("LOG", "GPS provider access denied");
                    // lets the user know there is a problem with the gps
                }

            }else {

                //Try get position with Network provider
                // Set provider (Network), Min time update, min distance update and the listener
                try {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME,
                            MIN_DISTANCE, this);

                    //Get last know location by network
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                } catch (SecurityException e) {
                    // lets the user know there is a problem with the gps
                }
            }

        }

    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your app
     *
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            try {
                locationManager.removeUpdates( GPSTracker.this );
            } catch (SecurityException e) {
                // lets the user know there is a problem with the gps
            }
        }
    }

    /**
     * Function to get latitude
     *
     */
    public double getLatitude() {
        if( latitude == 0.0) {
            try {
                latitude = location.getLatitude();
            } catch (SecurityException e) {
                // lets the user know there is a problem with the gps
            }
        }

        return latitude;
    }

    /**
     * Function to get longitude
     *
     */
    public double getLongitude() {
        if( longitude == 0.0) {
            try {
                longitude = location.getLongitude();
            } catch (SecurityException e) {
                // lets the user know there is a problem with the gps
            }
        }

        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * launch Settings Options
     *
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("Seu GPS está desligado");

        // Setting Dialog Message
        alertDialog
                .setMessage("Gostaria de ir até as configurações e ativá-lo ?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Sim :)",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton("Não",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(final Location location) {
        this.longitude = location.getLongitude();
        this.latitude  = location.getLatitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d("LOG", "Provider " + s + " status changed");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d("LOG", "Provider " + s + " enabled");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d("LOG", "Provider " + s + " disabled");
    }

}


package localizacao.geo.geolocalizacao;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by adao on 2/10/17.
 */

public class MyLocation {
    private Timer timer1;
    private LocationManager lm;
    private LocationResult locationResult;
    private boolean gps_enabled=false;
    private boolean network_enabled=false;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE = 50; // 50 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME = 1000 * 60 * 20; // 20 minutes

    public boolean getLocation(Context context, LocationResult result) {

        //I use LocationResult callback class to pass location value from MyLocation to user code.
        locationResult=result;
        if(lm==null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //exceptions will be thrown if provider is not permitted.
        try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){ Log.d("Exception", ex.getMessage() ); }
        try{network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){ Log.d("Exception", ex.getMessage() );  }

        //don't start listeners if no provider is enabled
        if(!gps_enabled && !network_enabled)
            return false;

        try {
            if(gps_enabled)
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListenerGps);
            if(network_enabled)
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListenerNetwork);

        } catch (SecurityException e) {
            // lets the user know there is a problem with the gps
        }

        timer1=new Timer();
        timer1.schedule(new GetLastLocation(), 20000);
        return true;
    }

    private LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.gotLocation(location);
            try {
                lm.removeUpdates(this);
                lm.removeUpdates(locationListenerNetwork);
            } catch (SecurityException e) {
                // lets the user know there is a problem with the gps
            }

        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    private LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            locationResult.gotLocation(location);
            try {
                lm.removeUpdates(this);
                lm.removeUpdates(locationListenerGps);
            } catch (SecurityException e) {
                // lets the user know there is a problem with the gps
            }
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    private class GetLastLocation extends TimerTask {
        @Override
        public void run() {
            try {
                lm.removeUpdates(locationListenerGps);
                lm.removeUpdates(locationListenerNetwork);
                Location net_loc=null, gps_loc=null;
                if(gps_enabled)
                    gps_loc=lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(network_enabled)
                    net_loc=lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                //if there are both values use the latest one
                if(gps_loc!=null && net_loc!=null){
                    if(gps_loc.getTime()>net_loc.getTime())
                        locationResult.gotLocation(gps_loc);
                    else
                        locationResult.gotLocation(net_loc);
                    return;
                }

                if(gps_loc!=null){
                    locationResult.gotLocation(gps_loc);
                    return;
                }
                if(net_loc!=null){
                    locationResult.gotLocation(net_loc);
                    return;
                }

                locationResult.gotLocation(null);
            } catch (SecurityException e) {
                // lets the user know there is a problem with the gps
            }
        }
    }

    public static abstract class LocationResult{
        public abstract void gotLocation(Location location);
    }
}

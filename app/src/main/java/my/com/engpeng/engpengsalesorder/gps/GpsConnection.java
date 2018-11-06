package my.com.engpeng.engpengsalesorder.gps;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static my.com.engpeng.engpengsalesorder.Global.RC_GPS;

public class GpsConnection implements LocationListener {

    private static final int MIN_SECONDS = 30 * 1000;
    private static final int MIN_METER = 5;

    private LocationManager locationManager;
    private boolean isGpsProviderEnabled, isNetworkProviderEnabled;
    private boolean isAvailable = false;
    private String selectedProvider;
    private Context context;

    public GpsConnection(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isGpsProviderEnabled || isNetworkProviderEnabled) {
            isAvailable = true;
        }

        if (isAvailable && checkPermission(this.context)) {
            Log.e("checkPermission", "checkPermission");
            if (isGpsProviderEnabled) {
                selectedProvider = LocationManager.GPS_PROVIDER;
            } else if (isNetworkProviderEnabled) {
                selectedProvider = LocationManager.NETWORK_PROVIDER;
            }
            locationManager.requestLocationUpdates(selectedProvider, MIN_SECONDS, MIN_METER, this);
        }
    }

    public Location getLastKnownLocation() {
        if (checkPermission(this.context)) {
            return locationManager.getLastKnownLocation(selectedProvider);
        } else {
            return null;
        }
    }

    public Location switchProviderAndGetLocation() {
        if (checkPermission(this.context)) {
            if (selectedProvider.equals(LocationManager.GPS_PROVIDER)) {
                selectedProvider = LocationManager.NETWORK_PROVIDER;
            } else {
                selectedProvider = LocationManager.GPS_PROVIDER;
            }
            locationManager.requestLocationUpdates(selectedProvider, MIN_SECONDS, MIN_METER, this);
            return locationManager.getLastKnownLocation(selectedProvider);
        } else {
            return null;
        }
    }

    public void removeConnection() {
        locationManager.removeUpdates(this);
    }

    public static boolean checkPermission(Context context) {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    public static void requestPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                RC_GPS);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
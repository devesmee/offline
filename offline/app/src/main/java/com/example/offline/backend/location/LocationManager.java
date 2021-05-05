package com.example.offline.backend.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.offline.backend.location.geofence.GeofenceArea;
import com.example.offline.backend.location.geofence.GeofenceHelper;
import com.example.offline.backend.location.service.LocationService;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

@SuppressLint("StaticFieldLeak")
public class LocationManager {


    private final Activity mActivity;
    public static final int PERMISSION_REQUEST_CODE = 10001;
    private static LocationManager mLocationManager;
    private static final String TAG = "Location Manager";
    LocationService mLocationService;
    Intent mServiceIntent;
    private GeofencingClient mGeofencingClient;
    private GeofenceHelper mGeofenceHelper;
    private final GeofenceArea mFontysGeofenceArea;
    private boolean isBackgroundLocationDialogShown = false;


    private LocationManager(Activity activity) {
        this.mActivity = activity;
        mFontysGeofenceArea = new GeofenceArea(700, "FONTYS_TQ", new LatLng(51.45093386577719, 5.453252146398889));
    }

    /**
     * Get singleton instance of the LocationManager
     *
     * @param activity to initiate the location manager
     * @return singleton instance
     */
    public static LocationManager getInstance(Activity activity) {
        if (mLocationManager == null) {
            mLocationManager = new LocationManager(activity);
        }

        return mLocationManager;
    }

    /**
     * Starts the location tracking services only if user has granted permissions
     */
    public void startLocationServices() {
        if (ContextCompat.checkSelfPermission(this.mActivity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mLocationService = new LocationService();
            mServiceIntent = new Intent(mActivity, LocationService.class);
            mActivity.startService(mServiceIntent);
            mGeofenceHelper = new GeofenceHelper(mActivity);
            mGeofencingClient = LocationServices.getGeofencingClient(mActivity);
            addGeofence(this.mFontysGeofenceArea.getCoordinates(), this.mFontysGeofenceArea.getGeofenceRadius());
        }
    }

    /**
     * Adds a new Geofence to the app
     *
     * @param latLng coordinates of the Geofence area
     * @param radius of the Geofence
     */
    private void addGeofence(LatLng latLng, float radius) {
        try {
            Geofence geofence = mGeofenceHelper.getGeofence(this.mFontysGeofenceArea.getGeofenceId(), latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
            GeofencingRequest geofencingRequest = mGeofenceHelper.getGeofencingRequest(geofence);
            PendingIntent pendingIntent = mGeofenceHelper.getPendingIntent();
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions();
                return;
            }

            mGeofencingClient.addGeofences(geofencingRequest, pendingIntent)
                    .addOnSuccessListener(aVoid ->
                            Log.d(TAG, "onSuccess: Geofence added"))
                    .addOnFailureListener(e -> {
                        String message = mGeofenceHelper.getErrorString(e);
                        Log.e(TAG, "onFailure: " + message);
                    });

        } catch (Exception e) {
            Log.d(TAG, "addGeofence: " + e);
        }

    }

    /**
     * Request location permissions
     */
    public void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(this.mActivity, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }


    /**
     * Method used when the user has to manually enable the background services
     * Shows info in Alert Dialog
     * Redirects user to app settings
     */
    public void onRequestPermissionRationale() {
        if (!isBackgroundLocationDialogShown) {
            isBackgroundLocationDialogShown = true;
            AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
            dialog.setTitle("Background location required ");
            dialog.setCancelable(false);
            dialog.setMessage("The application needs to keep track of your location while you are not using the phone in order to function properly.To enable background location services in the application please go to Permissions -> Location -> Allow all the time.");
            dialog.setPositiveButton("Settings", (dialog1, which) -> {
                Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",
                        mActivity.getApplicationContext().getPackageName(), null));
                mActivity.startActivityForResult(i, 1002);
            });
            dialog.setNegativeButton("Cancel", (dialogInterface, i) -> isBackgroundLocationDialogShown = false);
            AlertDialog alertDialog = dialog.create();
            alertDialog.show();

        }
    }

    /**
     * Method used to re ask the user for location permission after denial
     * Shows info in Alert Dialog
     */
    public void onLocationPermissionDeniedOnce() {
        new AlertDialog.Builder(mActivity)
                .setTitle("Location permissions not approved")
                .setMessage("If you do not approve the location permissions this app will not function properly.")
                .setPositiveButton("Approve", (dialogInterface, i) -> {
                    //Prompt the user once explanation has been shown
                    this.requestPermissions();
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
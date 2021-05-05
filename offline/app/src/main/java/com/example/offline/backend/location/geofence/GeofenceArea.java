package com.example.offline.backend.location.geofence;

import com.google.android.gms.maps.model.LatLng;

public class GeofenceArea {

    private float geofenceRadius;
    private String geofenceId;
    private LatLng coordinates;


    public GeofenceArea(float geofenceRadius, String geofenceId, LatLng coordinates) {
        this.geofenceRadius = geofenceRadius;
        this.geofenceId = geofenceId;
        this.coordinates = coordinates;
    }

    public float getGeofenceRadius() {
        return geofenceRadius;
    }

    public String getGeofenceId() {
        return geofenceId;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }
}

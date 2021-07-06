package com.example.unitapp.model;

import com.google.android.gms.maps.model.LatLng;

public class HaversineDistance {
    private static final int EARTH_RADIUS = 6371000; //RADIO en METROS

    public static double distance(LatLng start, LatLng end) {

        double dLat  = Math.toRadians((end.latitude - start.latitude));
        double dLong = Math.toRadians((end.longitude - start.longitude));

        double startLat = Math.toRadians(start.latitude);
        double endLat   = Math.toRadians(end.latitude);

        double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    public static double haversin(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }
}

package com.example.amb;



import org.osmdroid.util.GeoPoint;

import java.util.HashMap;

public class HospitalLocationManager {

    private final HashMap<String, GeoPoint> hospitalCoordinates;

    public HospitalLocationManager() {
        hospitalCoordinates = new HashMap<>();

        // Add hospitals with their latitude and longitude
        hospitalCoordinates.put("HOSP01", new GeoPoint(13.9299, 75.5695)); // Nanjappa Hospital
        hospitalCoordinates.put("HOSP02", new GeoPoint(13.9291, 75.5661)); // SIMS
        hospitalCoordinates.put("HOSP03", new GeoPoint(13.9315, 75.5698)); // Subbaiah Hospital
        hospitalCoordinates.put("HOSP04", new GeoPoint(13.9310, 75.5733)); // Shanthinilaya
        hospitalCoordinates.put("HOSP05", new GeoPoint(14.1651, 75.0403)); // Sagar Hospital
        hospitalCoordinates.put("HOSP06", new GeoPoint(13.9333, 75.5712)); // Apoorva
        hospitalCoordinates.put("HOSP07", new GeoPoint(13.9360, 75.5750)); // Ashwini Hospital
        hospitalCoordinates.put("HOSP08", new GeoPoint(13.9305, 75.5678)); // Heart Care
        hospitalCoordinates.put("HOSP09", new GeoPoint(13.9320, 75.5744)); // Kushal Hospital

        // Add more as needed
    }

    public HashMap<String, GeoPoint> getHospitalCoordinates() {
        return hospitalCoordinates;
    }
}

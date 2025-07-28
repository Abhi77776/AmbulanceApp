package com.example.amb; // Make sure this matches your actual package name

import org.osmdroid.util.GeoPoint; // From the osmdroid library
import java.util.ArrayList;
import java.util.List;

public class PolylineUtils {

    /**
     * Decodes an encoded polyline string into a List of GeoPoints.
     * This algorithm is commonly used by services like Google Directions API and OSRM.
     *
     * @param encodedPath The string representing the encoded polyline.
     * @param precision   The precision factor used during encoding (typically 5 for OSRM and standard polylines,
     *                    or 6 for polyline6 format).
     * @return A List of {@link GeoPoint} objects representing the decoded path. Returns an empty list
     *         if the encodedPath is null, empty, or malformed.
     */
    public static List<GeoPoint> decode(final String encodedPath, int precision) {
        // Basic check for null or empty input
        if (encodedPath == null || encodedPath.isEmpty()) {
            return new ArrayList<>(); // Return an empty list, not null
        }

        int len = encodedPath.length();
        // Pre-allocate list with a reasonable guess for capacity
        final List<GeoPoint> path = new ArrayList<>(len / 2);

        int index = 0;
        int lat = 0;
        int lng = 0;
        double factor = Math.pow(10, precision); // e.g., 10^5 = 100000.0

        while (index < len) {int b, shift = 0, result = 0;

            // Decode latitude
            do {
                if (index >= len) { // Check bounds to prevent StringIndexOutOfBoundsException
                    // Malformed polyline or unexpected end
                    return path; // Return what has been decoded so far, or an empty list if nothing
                }
                b = encodedPath.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;

            // Decode longitude
            do {
                if (index >= len) { // Check bounds
                    return path;
                }
                b = encodedPath.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            path.add(new GeoPoint(((double) lat / factor), ((double) lng / factor)));
        }
        return path;
    }
}
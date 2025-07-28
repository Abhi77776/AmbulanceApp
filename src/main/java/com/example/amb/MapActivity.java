package com.example.amb; // Replace with your actual package

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MyLocationNewOverlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.api.IMapController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;



public class MapActivity extends AppCompatActivity {
    private static final String TAG = "MapActivity";

    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;
    private GpsMyLocationProvider myLocationProvider;
    private GeoPoint selectedHospitalLocation;
    private String selectedHospitalCode;
    private GeoPoint currentUserLocation;
    private boolean firstFixAchieved = false;
    private boolean routeDrawn = false;

    private Handler locationFixTimeoutHandler = new Handler(Looper.getMainLooper());
    private Runnable locationFixTimeoutRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Log.d(TAG, "onCreate: MapActivity created.");

        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        myLocationProvider = new GpsMyLocationProvider(this);
        myLocationOverlay = new MyLocationNewOverlay(myLocationProvider, mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay);

        setupMapAndLocation();
    }

    private void setupMapAndLocation() {
        Log.d(TAG, "setupMapAndLocation: Initializing location timeout.");

        locationFixTimeoutRunnable = () -> {
            if (!firstFixAchieved) {
                Log.w(TAG, "setupMapAndLocation: Location fix timed out.");
                runOnUiThread(() -> {
                    Toast.makeText(MapActivity.this, "Could not get current location. Trying last known.", Toast.LENGTH_LONG).show();
                    Location lastKnown = myLocationProvider.getLastKnownLocation();
                    if (lastKnown != null) {
                        currentUserLocation = new GeoPoint(lastKnown.getLatitude(), lastKnown.getLongitude());
                        proceedWithMapSetup(currentUserLocation);
                    } else if (selectedHospitalLocation != null) {
                        mapView.getController().setCenter(selectedHospitalLocation);
                        mapView.getController().setZoom(15.0);
                        addHospitalMarker(selectedHospitalLocation);
                        Toast.makeText(MapActivity.this, "User location unavailable. Centered on hospital.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MapActivity.this, "User and hospital locations unavailable.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        locationFixTimeoutHandler.postDelayed(locationFixTimeoutRunnable, 10000);
        Log.d(TAG, "setupMapAndLocation: Location fix timeout scheduled.");

        myLocationOverlay.runOnFirstFix(() -> {
            Log.d(TAG, "runOnFirstFix: First location fix obtained!");
            firstFixAchieved = true;
            locationFixTimeoutHandler.removeCallbacks(locationFixTimeoutRunnable);

            Location userLoc = myLocationOverlay.getLastFix();
            if (userLoc == null) {
                runOnUiThread(() -> Toast.makeText(MapActivity.this, "Error: Location fix reported, but location is null.", Toast.LENGTH_LONG).show());
                return;
            }

            currentUserLocation = new GeoPoint(userLoc.getLatitude(), userLoc.getLongitude());
            runOnUiThread(() -> proceedWithMapSetup(currentUserLocation));
        });
    }

    private void proceedWithMapSetup(GeoPoint userGeoPoint) {
        if (userGeoPoint == null) {
            Toast.makeText(this, "User location is unavailable for map setup.", Toast.LENGTH_SHORT).show();
            if (selectedHospitalLocation != null) {
                mapView.getController().setCenter(selectedHospitalLocation);
                mapView.getController().setZoom(15.0);
                addHospitalMarker(selectedHospitalLocation);
            }
            return;
        }

        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
        mapController.setCenter(userGeoPoint);

        if (selectedHospitalLocation != null) {
            addHospitalMarker(selectedHospitalLocation);
            if (!routeDrawn) {
                drawRoute(userGeoPoint, selectedHospitalLocation);
            }
        } else {
            Toast.makeText(this, "Hospital location missing, cannot draw route.", Toast.LENGTH_SHORT).show();
        }
        mapView.invalidate();
    }

    private void addHospitalMarker(GeoPoint hospitalLocation) {
        if (hospitalLocation == null) return;

        Marker hospitalMarker = new Marker(mapView);
        hospitalMarker.setPosition(hospitalLocation);
        hospitalMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        hospitalMarker.setTitle("Hospital: " + selectedHospitalCode);
        mapView.getOverlays().add(hospitalMarker);
        mapView.invalidate();
    }

    private void drawRoute(GeoPoint start, GeoPoint end) {
        if (start == null || end == null || routeDrawn) return;

        String urlString = String.format(Locale.US,
                "http://10.0.2.2:5000/route/v1/driving/%.6f,%.6f;%.6f,%.6f?overview=full&geometries=geojson",
                start.getLongitude(), start.getLatitude(), end.getLongitude(), end.getLatitude());

        new FetchRouteTask().execute(urlString);
    }

    private class FetchRouteTask extends AsyncTask<String, Void, ArrayList<GeoPoint>> {
        @Override
        protected ArrayList<GeoPoint> doInBackground(String... urls) {
            if (urls.length == 0 || urls[0] == null || urls[0].isEmpty()) return null;

            ArrayList<GeoPoint> routePoints = new ArrayList<>();
            HttpURLConnection conn = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(urls[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(15000);

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder jsonResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        jsonResponse.append(line);
                    }

                    JSONObject obj = new JSONObject(jsonResponse.toString());
                    JSONArray coordinates = obj.getJSONArray("routes")
                            .getJSONObject(0).getJSONObject("geometry")
                            .getJSONArray("coordinates");
                    for (int i = 0; i < coordinates.length(); i++) {
                        JSONArray coord = coordinates.getJSONArray(i);
                        routePoints.add(new GeoPoint(coord.getDouble(1), coord.getDouble(0)));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (reader != null) reader.close();
                    if (conn != null) conn.disconnect();
                } catch (IOException ignored) {}
            }
            return routePoints;
        }

        @Override
        protected void onPostExecute(ArrayList<GeoPoint> geoPoints) {
            if (geoPoints != null && !geoPoints.isEmpty()) {
                Polyline routePolyline = new Polyline();
                routePolyline.setPoints(geoPoints);
                routePolyline.setColor(Color.RED);
                routePolyline.setWidth(8.0f);
                mapView.getOverlayManager().add(routePolyline);
                mapView.invalidate();
                routeDrawn = true;
                Toast.makeText(MapActivity.this, "Route displayed!", Toast.LENGTH_SHORT).show();
            } else {
                routeDrawn = false;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE));
        if (mapView != null) mapView.onResume();

        if (myLocationProvider != null && myLocationOverlay != null &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            myLocationProvider.startLocationProvider(myLocationOverlay);
            myLocationOverlay.enableMyLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Configuration.getInstance().save(this, getPreferences(MODE_PRIVATE));
        if (mapView != null) mapView.onPause();

        if (myLocationProvider != null) myLocationProvider.stopLocationProvider();
        if (myLocationOverlay != null) myLocationOverlay.disableMyLocation();

        if (locationFixTimeoutHandler != null && locationFixTimeoutRunnable != null) {
            locationFixTimeoutHandler.removeCallbacks(locationFixTimeoutRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) mapView.onDetach();
        if (myLocationProvider != null) {
            myLocationProvider.destroy();
            myLocationProvider = null;
        }
        myLocationOverlay = null;
        mapView = null;

        if (locationFixTimeoutHandler != null && locationFixTimeoutRunnable != null) {
            locationFixTimeoutHandler.removeCallbacks(locationFixTimeoutRunnable);
        }
        locationFixTimeoutHandler = null;
        locationFixTimeoutRunnable = null;
    }
} // End of MapActivity

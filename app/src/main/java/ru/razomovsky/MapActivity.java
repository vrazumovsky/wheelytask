package ru.razomovsky;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

import ru.razomovsky.base.ToolbarActivity;
import ru.razomovsky.server.CabLocation;

/**
 * Created by vadim on 22/10/16.
 */

public class MapActivity extends ToolbarActivity implements OnMapReadyCallback {

    private static final String TAG = "MapActivity";

    public static final String CAB_LOCATIONS_ARG = "ru.razomovsky.MapActivity.CAB_LOCATIONS_ARG";
    public static final String CAB_LOCATIONS_INTENT_FILTER =
            "ru.razomovsky.MapActivity.CAB_LOCATIONS_INTENT_FILTER";

    private GoogleMap map;
    MarkerView markerView;

    /**
     * key is the id of the cab
     */
    private Map<Integer, Marker> cabs = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setToolbar(R.string.cab_tracking);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapCabsTracking);
        mapFragment.getMapAsync(this);
        markerView = new MarkerView(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapReady called");
        map = googleMap;
    }

    private Marker putMarker(int id, LatLng position) {
        markerView.setCabId(id);
        Bitmap bitmap = markerView.createBitmap();
        MarkerOptions markerOptions = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                .position(position);
        return map.addMarker(markerOptions);
    }

    private void updateCabs(CabLocation[] locations) {
        Map<Integer, Marker> newCabs = new HashMap<>();
        for (CabLocation location : locations) {
            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
            Marker marker = cabs.get(location.getCabId());

            if (marker == null) {
                marker = putMarker(location.getCabId(), position);
            } else {
                marker.setPosition(position);
            }

            newCabs.put(location.getCabId(), marker);
        }

        cabs = newCabs;
    }

}

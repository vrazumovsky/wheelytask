package ru.razomovsky;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

import ru.razomovsky.auth.LoginActivity;
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

    public static final int DISCONNECT_MENU_ITEM_ID = 1;
    public static final String DISCONNECT_MENU_ITEM_TITLE = "Disconnect";

    private GoogleMap map;
    private MarkerView markerView;

    /**
     * key is the id of the cab
     */
    private Map<Integer, Marker> cabs = new HashMap<>();
    private CabLocation[] cabLocations;

    private CabLocationsBroadcastReceiver receiver = new CabLocationsBroadcastReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String hash = ((WheelyTaskApp) getApplication()).getHash();
        if (hash != null && !hash.equals("")) {
            Log.d(TAG, "login succeeded");
            Log.d(TAG, hash);
        } else {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

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
        map.setMyLocationEnabled(true);
        if (cabLocations != null) {
            updateCabs(cabLocations);
        }
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray(CAB_LOCATIONS_ARG, cabLocations);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Parcelable[] parcelableArray = savedInstanceState.getParcelableArray(CAB_LOCATIONS_ARG);
        if (parcelableArray == null) {
            return;
        }
        cabLocations = (CabLocation[]) parcelableArray;
        if (map != null) {
            updateCabs(cabLocations);
        }
    }

    private class CabLocationsBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            CabLocation[] locations =
                    (CabLocation[]) intent.getParcelableArrayExtra(CAB_LOCATIONS_ARG);
            if (locations == null) {
                throw new IllegalStateException(
                        "Need to set cab locations in CAB_LOCATIONS_INTENT_FILTER broadcast");
            }
            cabLocations = locations;
            if (map == null) {
                return;
            }
            updateCabs(locations);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(CAB_LOCATIONS_INTENT_FILTER));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, DISCONNECT_MENU_ITEM_ID, 0, DISCONNECT_MENU_ITEM_TITLE);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case DISCONNECT_MENU_ITEM_ID:
                ((WheelyTaskApp) getApplication()).disconnect();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }

}

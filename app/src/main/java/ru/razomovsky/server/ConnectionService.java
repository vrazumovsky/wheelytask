package ru.razomovsky.server;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.neovisionaries.ws.client.OpeningHandshakeException;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.util.List;
import java.util.Map;

import ru.razomovsky.MapActivity;
import ru.razomovsky.auth.LoginActivity;

/**
 * Created by vadim on 20/10/16.
 */

public class ConnectionService extends Service implements GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "ConnectionService";

    private static final String BACKEND_URL = "ws://mini-mdt.wheely.com/";

    public static final String USER_NAME_ARG = "ru.razomovsky.server.ConnectionService.USER_NAME_ARG";
    public static final String PASSWORD_ARG = "ru.razomovsky.server.ConnectionService.PASSWORD_ARG";

    private WebSocket ws;
    private GoogleApiClient mGoogleApiClient;
    private boolean isNeedLocationUpdates = false;

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "new location: " + location);
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (isNeedLocationUpdates) {
            requestLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                onHandleIntent(intent);
            }
        }).start();

        return START_REDELIVER_INTENT;
    }

    private void requestLocationUpdates() {
        if (!mGoogleApiClient.isConnected()) {
            Log.w(TAG, "google client does not connected");
            isNeedLocationUpdates = true;
            return;
        }
        Log.w(TAG, "requestLocationUpdates");

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(10000); // 10 seconds

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "permission not granted");
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                locationRequest, locationListener);
        isNeedLocationUpdates = false;

    }

    private void removeLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, locationListener);
        }
    }

    protected void onHandleIntent(Intent intent) {
        WebSocketFactory factory = new WebSocketFactory();
        try {

            String userName = intent.getStringExtra(USER_NAME_ARG);
            String password = intent.getStringExtra(PASSWORD_ARG);
            String socketUrl = BACKEND_URL +
                    "?username=" + userName +
                    "&password=" + password;


            if (ws != null) {
                ws.disconnect();
                ws = null;
            }

            ws = factory.createSocket(socketUrl);
            ws.addListener(new WebSocketAdapter() {

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    super.onConnected(websocket, headers);
                    Log.d(TAG, "Connected to server");
                    sendLoginResultBroadcast(ResponseCodes.SUCCESS);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            requestLocationUpdates();
                        }
                    });
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                    Log.d(TAG, "disconnected: " + websocket.getURI().toString());
                }

                @Override
                public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                    super.onCloseFrame(websocket, frame);
                    Log.d(TAG, "closed");
                }

                @Override
                public void onTextMessage(WebSocket websocket, String text) throws Exception {
                    super.onTextMessage(websocket, text);
                    Log.d(TAG, text);
                    CabLocation[] locations = new Gson().fromJson(text, CabLocation[].class);
                    sendCabLocationsBroadcast(locations);
                }

                @Override
                public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                    super.onError(websocket, cause);
                    cause.printStackTrace();
                }

                @Override
                public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                    super.onConnectError(websocket, exception);
                    exception.printStackTrace();
                }

            });
            ws.connect();

        } catch (OpeningHandshakeException e) {
            int code = e.getStatusLine().getStatusCode();
            Log.w(TAG, "Error code: " + String.valueOf(code));
            sendLoginResultBroadcast(code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendLoginResultBroadcast(int result){
        Intent intent = new Intent (LoginActivity.LOGIN_RESULT_INTENT_FILTER);
        intent.putExtra(LoginActivity.LOGIN_RESULT_ARG, result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendCabLocationsBroadcast(CabLocation[] locations) {
        Intent intent = new Intent (MapActivity.CAB_LOCATIONS_INTENT_FILTER);
        intent.putExtra(MapActivity.CAB_LOCATIONS_ARG, locations);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeLocationUpdates();
    }
}

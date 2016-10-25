package ru.razomovsky.server;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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

public class ConnectionService extends IntentService {

    private static final String TAG = "ConnectionService";

    private static final String BACKEND_URL = "ws://mini-mdt.wheely.com";

    public static final String USER_NAME_ARG = "ru.razomovsky.server.ConnectionService.USER_NAME_ARG";
    public static final String PASSWORD_ARG = "ru.razomovsky.server.ConnectionService.PASSWORD_ARG";

    public ConnectionService() {
        this("ConnectionService");
    }
    public ConnectionService(String name) {
        super(name);
        setIntentRedelivery(true);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        WebSocketFactory factory = new WebSocketFactory();
        try {

            String userName = intent.getStringExtra(USER_NAME_ARG);
            String password = intent.getStringExtra(PASSWORD_ARG);
            String socketUrl = BACKEND_URL +
                    "?username=" + userName +
                    "&password=" + password;

            WebSocket ws = factory.createSocket(socketUrl);
            ws.addListener(new WebSocketAdapter() {

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    super.onConnected(websocket, headers);
                    Log.d(TAG, "Connected to server");
                    sendLoginResultBroadcast(ResponseCodes.SUCCESS);

                    websocket.sendText("{\n" +
                            "\n" +
                            "\"lat\": 55.373703,\n" +
                            "\n" +
                            "\"lon\": 37.474764\n" +
                            "\n" +
                            "}");
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
}

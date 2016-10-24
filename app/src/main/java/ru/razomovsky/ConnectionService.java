package ru.razomovsky;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.util.List;
import java.util.Map;

/**
 * Created by vadim on 20/10/16.
 */

public class ConnectionService extends IntentService {

    private static final String TAG = "ConnectionService";

    private static final String BACKEND_URL = "ws://mini-mdt.wheely.com";

    public static final String USER_NAME_ARG = "ru.razomovsky.ConnectionService.USER_NAME_ARG";
    public static final String PASSWORD_ARG = "ru.razomovsky.ConnectionService.PASSWORD_ARG";

    public ConnectionService() {
        super("ConnectionService");
    }
    public ConnectionService(String name) {
        super(name);
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
                    Log.d(TAG, "***********************************");
                    Log.d(TAG, websocket.getURI().toString());
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

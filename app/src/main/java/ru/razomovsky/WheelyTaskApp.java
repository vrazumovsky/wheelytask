package ru.razomovsky;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;

import ru.razomovsky.server.ConnectionService;

/**
 * Created by vadim on 29/10/16.
 */

public class WheelyTaskApp extends Application {

    public static final String HASH_ARG = "ru.razomovsky.WheelyTaskApp.hash";

    private SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();

        preferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);

    }

    public void saveHash(String hash) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(HASH_ARG, hash);
        editor.apply();
    }

    public String getHash() {
        return preferences.getString(HASH_ARG, null);
    }

    public void disconnect() {
        saveHash(null);
        stopService(new Intent(this, ConnectionService.class));
    }
}

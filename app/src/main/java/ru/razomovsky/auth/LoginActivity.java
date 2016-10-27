package ru.razomovsky.auth;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ru.razomovsky.MapActivity;
import ru.razomovsky.R;
import ru.razomovsky.base.ToolbarActivity;
import ru.razomovsky.server.ConnectionService;
import ru.razomovsky.server.ResponseCodes;
import ru.razomovsky.ui.ProgressDialogFragment;

public class LoginActivity extends ToolbarActivity {

    public static final String TAG = "LoginActivity";
    
    public static final String LOGIN_RESULT_ARG = "ru.razumovsky.auth.LoginActivity.LOGIN_RESULT_ARG";
    public static final String LOGIN_RESULT_INTENT_FILTER =
            "ru.razumovsky.auth.LoginActivity.LOGIN_RESULT_INTENT_FILTER";

    private static final int REQUEST_LOCATION_PERMISSION = 17;

    private EditText loginEditText;
    private EditText passwordEditText;
    private ProgressDialogFragment dialogFragment;
    private boolean requestInProgress = false;

    private LoginResultBroadcastReceiver receiver = new LoginResultBroadcastReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setToolbar(R.string.auth);

        loginEditText = (EditText) findViewById(R.id.login_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);



        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoginPasswordValid()) {
                    if (dialogFragment == null) {
                        dialogFragment = new ProgressDialogFragment();
                    }
                    dialogFragment.show(getSupportFragmentManager(), null);
                    requestInProgress = true;
                    requestLocationPermission();
                } else {
                    Toast.makeText(LoginActivity.this,
                            R.string.empty_login_password_warning, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            startConnectionService();
        }
    }

    private void startConnectionService() {
        Intent intent = new Intent(LoginActivity.this, ConnectionService.class);
        intent.putExtra(ConnectionService.USER_NAME_ARG, loginEditText.getText().toString());
        intent.putExtra(ConnectionService.PASSWORD_ARG, passwordEditText.getText().toString());
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: Permision granted");
                    startConnectionService();
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: Permision denied ");
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            requestInProgress = false;
                            dialogFragment.dismiss();
                            Toast.makeText(LoginActivity.this,
                                    R.string.permission_error, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                break;
        }
    }

    private boolean isLoginPasswordValid() {
        return !isEditTextEmpty(loginEditText) && !isEditTextEmpty(passwordEditText);
    }

    private boolean isEditTextEmpty(EditText editText) {
        return editText.getText() == null || editText.getText().toString().equals("");
    }

    private class LoginResultBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            dialogFragment.dismiss();
            int result = intent.getIntExtra(LOGIN_RESULT_ARG, -1);
            if (result == -1) {
                throw new IllegalStateException("Need to set login result in broadcast");
            } else if (result == ResponseCodes.FORBIDDEN) {
                Toast.makeText(LoginActivity.this,
                        R.string.login_password_error, Toast.LENGTH_SHORT).show();
            } else if (result == ResponseCodes.SUCCESS) {
                startActivity(new Intent(LoginActivity.this, MapActivity.class));
            } else {
//                throw new IllegalArgumentException("Unsupported result code: " + result);
                Toast.makeText(LoginActivity.this,
                        "Unsupported result code: " + result, Toast.LENGTH_SHORT).show();
            }
            requestInProgress = false;

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(LOGIN_RESULT_INTENT_FILTER));
        if (dialogFragment != null) {
            dialogFragment.dismiss();
        }
        passwordEditText.setText("");
    }


    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
}

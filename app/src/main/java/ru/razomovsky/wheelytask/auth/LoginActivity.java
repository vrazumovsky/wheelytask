package ru.razomovsky.wheelytask.auth;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.razomovsky.wheelytask.MapActivity;
import ru.razomovsky.wheelytask.R;
import ru.razomovsky.wheelytask.base.ToolbarActivity;
import ru.razomovsky.wheelytask.server.ConnectionService;
import ru.razomovsky.wheelytask.server.ResponseCodes;
import ru.razomovsky.wheelytask.ui.ProgressDialogFragment;

public class LoginActivity extends ToolbarActivity {

    public static final String TAG = "LoginActivity";
    private static final String DIALOG_TAG = "DIALOG_TAG";
    private static final String PROGRESS_ARG = "PROGRESS_ARG";

    
    public static final String LOGIN_RESULT_ARG = "ru.razumovsky.auth.LoginActivity.LOGIN_RESULT_ARG";
    public static final String LOGIN_RESULT_INTENT_FILTER =
            "ru.razumovsky.auth.LoginActivity.LOGIN_RESULT_INTENT_FILTER";

    private static final int REQUEST_LOCATION_PERMISSION = 17;

    private EditText loginEditText;
    private EditText passwordEditText;
    private ProgressDialogFragment dialogFragment = new ProgressDialogFragment();

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
                    requestInProgress = true;
                    dialogFragment.show(getSupportFragmentManager(), DIALOG_TAG);
                    requestLocationPermission();
                } else {
                    Toast.makeText(LoginActivity.this,
                            R.string.login_password_warning, Toast.LENGTH_SHORT).show();
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
        return !isEditTextEmpty(loginEditText) && !isEditTextEmpty(passwordEditText) &&
                !isContainsSpaceChar(loginEditText.getText().toString()) &&
                !isContainsSpaceChar(passwordEditText.getText().toString());
    }

    static final Pattern spaceCharPattern = Pattern.compile("\\s");

    private boolean isContainsSpaceChar(String string) {
        return spaceCharPattern.matcher(string).find();
    }

    private boolean isEditTextEmpty(EditText editText) {
        return editText.getText() == null || editText.getText().toString().equals("");
    }

    private class LoginResultBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int result = intent.getIntExtra(LOGIN_RESULT_ARG, -1);
            if (result == -1) {
                throw new IllegalStateException("Need to set login result in broadcast");
            } else if (result == ResponseCodes.FORBIDDEN) {
                Toast.makeText(LoginActivity.this,
                        R.string.login_password_error, Toast.LENGTH_SHORT).show();
            } else if (result == ResponseCodes.SUCCESS) {
                startActivity(new Intent(LoginActivity.this, MapActivity.class));
                finish();
            } else if (result == ResponseCodes.SERVICE_UNAVAILABLE) {
                Toast.makeText(LoginActivity.this,
                        R.string.service_unavailable, Toast.LENGTH_SHORT).show();
            } else {
//                throw new IllegalArgumentException("Unsupported result code: " + result);
                Toast.makeText(LoginActivity.this,
                        "Unsupported result code: " + result, Toast.LENGTH_SHORT).show();
            }
            requestInProgress = false;
            dialogFragment.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!requestInProgress) {
            dialogFragment.dismiss();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(PROGRESS_ARG, requestInProgress);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ProgressDialogFragment fragmentByTag =
                (ProgressDialogFragment) getSupportFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (fragmentByTag != null) {
            dialogFragment = fragmentByTag;
        }
        requestInProgress = savedInstanceState.getBoolean(PROGRESS_ARG);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(LOGIN_RESULT_INTENT_FILTER));
        passwordEditText.setText("");
    }


    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
}

package ru.razomovsky.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ru.razomovsky.R;
import ru.razomovsky.base.ToolbarActivity;
import ru.razomovsky.server.ConnectionService;

public class LoginActivity extends ToolbarActivity {

    private EditText loginEditText;
    private EditText passwordEditText;


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
                    Intent intent = new Intent(LoginActivity.this, ConnectionService.class);
                    intent.putExtra(ConnectionService.USER_NAME_ARG, loginEditText.getText().toString());
                    intent.putExtra(ConnectionService.PASSWORD_ARG, passwordEditText.getText().toString());
                    startService(intent);
                } else {
                    Toast.makeText(LoginActivity.this,
                            R.string.empty_login_password_warning, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isLoginPasswordValid() {
        return !isEditTextEmpty(loginEditText) && !isEditTextEmpty(passwordEditText);
    }

    private boolean isEditTextEmpty(EditText editText) {
        return editText.getText() == null || editText.getText().toString().equals("");
    }
}

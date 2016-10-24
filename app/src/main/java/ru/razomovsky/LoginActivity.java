package ru.razomovsky;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ru.razomovsky.base.ToolbarActivity;

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
                Intent intent = new Intent(LoginActivity.this, ConnectionService.class);
                intent.putExtra(ConnectionService.USER_NAME_ARG, loginEditText.getText());
                intent.putExtra(ConnectionService.PASSWORD_ARG, passwordEditText.getText());
                startService(new Intent(LoginActivity.this, ConnectionService.class));
            }
        });
    }
}

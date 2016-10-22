package profunion.razomovsky;

import android.os.Bundle;

import profunion.razomovsky.base.ToolbarActivity;

public class LoginActivity extends ToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setToolbar(R.string.auth);
    }
}

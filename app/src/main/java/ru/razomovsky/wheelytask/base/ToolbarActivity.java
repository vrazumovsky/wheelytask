package ru.razomovsky.wheelytask.base;

import android.support.v7.app.AppCompatActivity;

import ru.razomovsky.wheelytask.R;


/**
 * Created by vadim on 21/10/16.
 */

public class ToolbarActivity extends AppCompatActivity {

    public void setToolbar(int titleResourceId) {
        setToolbar(getString(titleResourceId));
    }

    public void setToolbar(String title) {
        android.support.v7.widget.Toolbar mActionBarToolbar =
                (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        mActionBarToolbar.setTitle(title);
        setSupportActionBar(mActionBarToolbar);
    }

    public void setToolbar() {
        setToolbar("");
    }

}

package profunion.razomovsky.base;

import android.support.v7.app.AppCompatActivity;

import profunion.razomovsky.R;


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

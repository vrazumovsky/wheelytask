package profunion.razomovsky;

import android.os.Bundle;

import profunion.razomovsky.base.ToolbarActivity;

/**
 * Created by vadim on 22/10/16.
 */

public class MapActivity extends ToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setToolbar(R.string.cab_tracking);
    }
}

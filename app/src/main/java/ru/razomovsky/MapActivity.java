package ru.razomovsky;

import android.os.Bundle;

import ru.razomovsky.base.ToolbarActivity;

/**
 * Created by vadim on 22/10/16.
 */

public class MapActivity extends ToolbarActivity {

    public static final String CAB_LOCATIONS_ARG = "ru.razomovsky.MapActivity.CAB_LOCATIONS_ARG";
    public static final String CAB_LOCATIONS_INTENT_FILTER =
            "ru.razomovsky.MapActivity.CAB_LOCATIONS_INTENT_FILTER";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setToolbar(R.string.cab_tracking);
    }
}

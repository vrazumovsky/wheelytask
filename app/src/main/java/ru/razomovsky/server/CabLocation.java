package ru.razomovsky.server;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vadim on 25/10/16.
 */

public class CabLocation {

    @SerializedName("id")
    private int cabId;

    @SerializedName("lat")
    private double latitude;

    @SerializedName("lon")
    private double longitude;
}

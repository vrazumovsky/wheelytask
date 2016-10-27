package ru.razomovsky.server;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vadim on 25/10/16.
 */

public class CabLocation implements Parcelable {

    @SerializedName("id")
    private int cabId;

    @SerializedName("lat")
    private double latitude;

    @SerializedName("lon")
    private double longitude;

    private CabLocation() {}

    protected CabLocation(Parcel in) {
        cabId = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<CabLocation> CREATOR = new Creator<CabLocation>() {
        @Override
        public CabLocation createFromParcel(Parcel in) {
            return new CabLocation(in);
        }

        @Override
        public CabLocation[] newArray(int size) {
            return new CabLocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(cabId);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }

    @Override
    public String toString() {
        return "id: " + cabId + "; lat: " + latitude + "; lon: " + longitude;
    }

    public int getCabId() {
        return cabId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}

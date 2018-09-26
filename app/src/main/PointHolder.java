package com.example.home.secretary_kim;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PointHolder extends RecyclerView.ViewHolder{
    TextView title;
    TextView distance;

    public PointHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.holder_point_name);
        distance = itemView.findViewById(R.id.holder_point_distance);
    }

    public void set(Context context, double dlati, double dlongi, Point point) {
        String name = null;
        try {
            Geocoder geocoder = new Geocoder(context, Locale.KOREA);

            List<Address> address = geocoder.getFromLocation(dlati, dlongi, 1);
            if (address != null && address.size() > 0) {
                name = address.get(0).getAddressLine(0).toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("*","지명에러");
        }

        this.title.setText(name);
        String distance = convert(getCurrentDistance(dlati, dlongi, point.latitude, point.longtitude));
        this.distance.setText(distance);
    }

    private String convert(int distance) {
        if(distance > 999) {
            float a = (float)distance;
            float f = a / 1000f;

            return String.valueOf(f) + "km";

        } else {
            return String.valueOf(distance) + "m";
        }
    }

    private int getCurrentDistance(double alati, double alongi, double blati, double blongi) {
        double distance = 0;

        Location a = new Location("A");
        a.setLatitude(alati);
        a.setLongitude(alongi);

        Location b = new Location("B");
        b.setLatitude(blati);
        b.setLongitude(blongi);

        distance = a.distanceTo(b);

        return (int)distance;
    }
}


package com.example.home.secretary_kim;

import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class PointHolder extends RecyclerView.ViewHolder {
    TextView title;
    TextView distance;

    public PointHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.holder_point_name);
        distance = itemView.findViewById(R.id.holder_point_distance);
    }

    public void set(double dlati, double dlongi, Point point) {
        title.setText(point.name);
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


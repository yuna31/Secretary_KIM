package com.example.home.secretary_kim;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class PointAdapter extends RecyclerView.Adapter<PointHolder> {
    public ArrayList<Point> list = new ArrayList<>();
    private double latitude = 37.517400f;
    private double longitude = 127.021924f;
    Context context;

    public PointAdapter(Context context) {
        this.context = context;
        list = getMapPoint();
    }
    @NonNull
    @Override
    public PointHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PointHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_point, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PointHolder holder, int position) {
        holder.set(context, latitude, longitude, list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        notifyDataSetChanged();
    }

    private ArrayList<Point> getMapPoint() {
        ArrayList<Point> list = new ArrayList<>();

        list.add(getPoint("맥도날드", 37.5147400f, 127.021924f));
        list.add(getPoint("가로수길", 37.519446f, 127.023126f));
        list.add(getPoint("아오리의 행방불명", 37.519059f, 127.023776f));
        list.add(getPoint("키친랩 가로수길점", 37.521601f, 127.021769f));
        list.add(getPoint("C27 가로수길점", 37.520711f, 127.023231f));

        return list;
    }

    private Point getPoint(String name, double latitude, double longitude){
        Point p = new Point();
        p.name = name;
        p.latitude = latitude;
        p.longtitude = longitude;

        return p;
    }
}

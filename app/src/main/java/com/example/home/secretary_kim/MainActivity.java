package com.example.home.secretary_kim;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Context;
import android.content.ReceiverCallNotAllowedException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements
            BottomNavigationView.OnNavigationItemSelectedListener,
            OnMapReadyCallback,
            GoogleMap.OnMyLocationButtonClickListener,
            GoogleMap.OnMyLocationClickListener,
            LocationListener{

    private final int FRAGMENT_MAP = 1;
    private final int FRAGMENT_LIST = 2;
    private final int FRAGMENT_SETTING = 3;


    private LocationManager locationManager;
    private RecyclerView mBottomSheet;
    private BottomSheetBehavior mBehavior;
    private PointAdapter adapter;
    FragmentMap f;

    private void showBottomSheetView() {
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        mBottomSheet = findViewById(R.id.bottomSheet);
        mBottomSheet.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(mBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else if(mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
                return false;
            }
        });

        mBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_HIDDEN) {
                    mBehavior.setPeekHeight(300);
                    mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        adapter = new PointAdapter();
        mBottomSheet.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBottomSheet.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mBottomSheet.setAdapter(adapter);
        mBehavior.setPeekHeight(300);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mBottomSheet.post(new Runnable() {
            @Override
            public void run() {
                mBehavior.setPeekHeight(300);
                mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.navigation_home:
                FragmentMap map = new FragmentMap();
                transaction.replace(R.id.container, map);
                transaction.commit();
                mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return true;
            case R.id.navigation_dashboard:
                FragmentList list = new FragmentList();
                transaction.replace(R.id.container, list);
                transaction.commit();
                showBottomSheetView();
                return true;
            case R.id.navigation_notifications:
                FragmentSetting setting = new FragmentSetting();
                transaction.replace(R.id.container, setting);
                transaction.commit();
                mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                return true;
        }

        return false;
    }

    private ArrayList<LatLng> getMapPoint(GoogleMap googleMap) {
        ArrayList<LatLng> list = new ArrayList<>();

        list.add(getLatLng("맥도날드", 37.5147400f, 127.021924f, googleMap));
        list.add(getLatLng("가로수길", 37.519446f, 127.023126f, googleMap));
        list.add(getLatLng("아오리의 행방불명", 37.519059f, 127.023776f, googleMap));
        list.add(getLatLng("키친랩 가로수길점", 37.521601f, 127.021769f, googleMap));
        list.add(getLatLng("C27 가로수길점", 37.520711f, 127.023231f, googleMap));

        return list;
    }

    private LatLng getLatLng(String title, double latitude, double longitude, GoogleMap googleMap) {
        LatLng l = new LatLng(latitude, longitude);

        MarkerOptions m = new MarkerOptions();
        m.position(l);
        m.title(title);

        googleMap.addMarker(m);

        return l;
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        ArrayList<LatLng> list = getMapPoint(googleMap);


        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(list.get(0), 14f));

        TedPermission.with(this)
                .setPermissionListener(new PermissionListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionGranted() {
                        googleMap.setMyLocationEnabled(true);
                        googleMap.setOnMyLocationButtonClickListener(MainActivity.this);
                        googleMap.setOnMyLocationClickListener(MainActivity.this);
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                    }
                })
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    private void showBottomSheetDialogFullScreen() {
        f = new FragmentMap();
        f.show(getSupportFragmentManager(), "dialog");
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "onMyLocationButtonClick", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "현재 위치값 받아오는중", Toast.LENGTH_SHORT).show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, MainActivity.this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, MainActivity.this);
        return false;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "onMyLocationClick", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "현재 위치 받아옴", Toast.LENGTH_SHORT).show();
        adapter.setPoint(location.getLatitude(), location.getLongitude());
        locationManager.removeUpdates(MainActivity.this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
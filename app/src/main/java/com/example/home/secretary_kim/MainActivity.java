package com.example.home.secretary_kim;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;


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

    public static int latlonCnt = 0;
    public static String[] latitude = new String[20];
    public static String[] longitude = new String[20];

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


        final Handler handler = new Handler();
        new Thread() {
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String url = "http://13.209.64.57:8080/getPlaceList.jsp";
                        NetworkTask networkTask = new NetworkTask(url, null);
                        networkTask.execute();
                    }
                }, 300);
            }
        }.start();

        final Handler handler1 = new Handler();
        new Thread() {
            public void run() {
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        makeMap();
                    }
                }, 1000);
            }
        }.start();

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

//        new Timer().schedule(new TimerTask() {
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                });
//            }
//         }, 1200);

    }

    void makeMap() {
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
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
                //MainActivity map = new MainActivity();
                transaction.replace(R.id.container, map);
                transaction.commit();
                mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return true;
            case R.id.navigation_dashboard:
                FragmentList list = new FragmentList();
                transaction.replace(R.id.container, list);
                transaction.commit();
                //showBottomSheetView();
                return true;
            case R.id.navigation_notifications:

//                FragmentSetting setting = new FragmentSetting();
//                transaction.replace(R.id.container, setting);
//                transaction.commit();
                Intent i = new Intent(getApplicationContext(), FragmentSettingActivity.class);
                startActivityForResult(i, 0);

                //mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                return true;
        }

        return false;
    }

    private ArrayList<LatLng> getMapPoint(GoogleMap googleMap) {
        ArrayList<LatLng> list = new ArrayList<>();

        for(int i = 0; i < latlonCnt; i++) {
            if(i%2 == 1) {
                list.add(getLatLng("!긴급!", Double.parseDouble(latitude[i]), Double.parseDouble(longitude[i]), googleMap));
            }
            else {
                list.add(getLatLng(i+"번째", Double.parseDouble(latitude[i]), Double.parseDouble(longitude[i]), googleMap));
            }
        }

//        list.add(getLatLng("가로수길", 37.519446f, 127.023126f, googleMap));
//        list.add(getLatLng("아오리의 행방불명", 37.519059f, 127.023776f, googleMap));
//        list.add(getLatLng("키친랩 가로수길점", 37.521601f, 127.021769f, googleMap));
//        list.add(getLatLng("C27 가로수길점", 37.520711f, 127.023231f, googleMap));

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
        //Toast.makeText(this, "onMyLocationButtonClick", Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "현재 위치값 받아오는중", Toast.LENGTH_SHORT).show();
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
        //Toast.makeText(this, "현재 위치 받아옴", Toast.LENGTH_SHORT).show();
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


    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url;
        private ContentValues values;
        public NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result;
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
            // doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            // jsp 처리 결과 메시지를 가져옴 contains를 통해 이벤트 처리
//            if (s.contains("success")) {
//                Toast.makeText(getContext(), "리스트 생성 성공", Toast.LENGTH_LONG).show();
//                //finish();
//            }

            int latCnt = 0;
            int lonCnt = 0;

            StringTokenizer str = new StringTokenizer(s, "{\"\":\\/\",\"\":\\/\"}");
            int countTokens = str.countTokens();
            System.out.println("token 수 : " + countTokens);

            String[] temp = new String[countTokens];

            for(int i = 0; i < countTokens; i++) {
                temp[i] = str.nextToken();
                System.out.println("**" + i +"번째 토큰 : " + temp[i]);

                if(i % 3 == 0 && i != 0) {
                    latitude[latCnt] = temp[i];
                    System.out.println(latCnt + "번째 lat : " + latitude[latCnt]);
                    latCnt++;
                } else if(i % 3 == 2 && i != 0) {
                    longitude[lonCnt] = temp[i];
                    System.out.println(lonCnt + "번째 lon : " + longitude[lonCnt]);
                    lonCnt++;
                }
            }
            latlonCnt = latCnt;

//            System.out.println("in function get count " + Umailcnt);
        }
    }


    public class RequestHttpURLConnection {
        public String request(String _url, ContentValues _params) {
            HttpURLConnection urlConn = null;
            StringBuffer sbParams = new StringBuffer();
            //StringBuffer에 파라미터 연결
            // 보낼 데이터가 없으면 파라미터를 비운다.
            if (_params == null) {
            }

            try {
                URL url = new URL(_url);
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("POST");
                urlConn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
                urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;charset=UTF-8");


                String strParams = sbParams.toString(); //sbParams에 정리한 파라미터들을 스트링으로 저장. 예)id=id1&pw=123;
                OutputStream os = urlConn.getOutputStream();
                os.write(strParams.getBytes("UTF-8")); // 출력 스트림에 출력.
                os.flush(); // 출력 스트림을 플러시(비운다)하고 버퍼링 된 모든 출력 바이트를 강제 실행.
                os.close();


                if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                    return null;

                // 읽어온 결과물 리턴.
                // 요청한 URL의 출력물을 BufferedReader로 받는다.
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

                String line;
                String page = "";

                // 라인을 받아와 합친다.
                while ((line = reader.readLine()) != null){
                    System.out.println("line : '" + line + "'");
                    page += line;
                }
                System.out.println("page : " + page);

                return page;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(urlConn != null)
                    urlConn.disconnect();
            }

            return null;
        }

    }
}

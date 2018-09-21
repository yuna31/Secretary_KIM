package com.example.home.secretary_kim.VR;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.home.secretary_kim.EmergencyActivity;
import com.example.home.secretary_kim.MainActivity;
import com.example.home.secretary_kim.R;
import com.example.home.secretary_kim.SpeechActivity;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class BluetoothActivity extends AppCompatActivity {

    private static final String TAG = "BluetoothActivity";

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private static final boolean D = true;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;

    public static final int MODE_REQUEST = 1;

    private static final int STATE_SENDING = 1;
    private static final int STATE_NO_SENDING = 2;
    private int mSendingState;

    public static final int NULL_IMGARRAY = 0;
    public static final int NOT_NULL_IMGARRAY = 1;
    public static int arrState;

    private static final int CNT_4 = 4;
    private Button bluetooth_btn;
    private Button request_btn;
    private Button sos_btn;
    private Button s0woo_btn;
    private Button yuna_btn;

    private BluetoothService bluetoothService = null;
    private StringBuffer mOutStringBuffer;

    public static Bitmap[] imgArray = null;
    public int cnt = 0;

    private ProgressDialog prBar;

    //블루투스로 값 받아오는 핸들러
    private final Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
                case MESSAGE_STATE_CHANGE:
                    if(D){
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    }
                    switch (msg.arg1){
                        case BluetoothService.STATE_CONNECTED:
                            Toast.makeText(getApplicationContext(), "블루투스 연결 성공", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Toast.makeText(getApplicationContext(),"블루투스 연결 중", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothService.STATE_FAIL:
                            Toast.makeText(getApplicationContext(), "블루투스 연결 실패", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    Log.d(TAG, "MESSAGE_WRITE");
                    Toast.makeText(getApplicationContext(), "메세지 쓰는 중", Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_READ:
                    int tmp = msg.arg2;
                    byte[] t2 = (byte[])msg.obj;
                    if(t2!=null){
                        Log.d(TAG, t2.toString());
                        setImg(t2);
                    }

                    break;
            }
        }
    };

    //ProgressDialog 핸들러
    Handler prHandler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what == CNT_4){
                prBar.dismiss();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_bluetooth);

        checkPerm();

        if(bluetoothService == null){
            bluetoothService = new BluetoothService(this, mHandler);
            mOutStringBuffer = new StringBuffer("");
        }

        //화면 시작하자마자 블루투스 연결
        if(bluetoothService.getDeviceState()){
            bluetoothService.enableBluetooth();
        }
        else{
            finish();
        }

        bluetooth_btn = (Button)findViewById(R.id.bluetooth_btn);
        bluetooth_btn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(bluetoothService.getDeviceState()){
                    bluetoothService.enableBluetooth();
                }
                else{
                    finish();
                }
            }
        });

        prBar = new ProgressDialog(this);
        prBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prBar.setMessage("이미지 로딩중입니다");

        request_btn = (Button)findViewById(R.id.request_btn);
        request_btn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Log.i(TAG, "Start Capture");
                imgArray = new Bitmap[4];
                arrState = NULL_IMGARRAY;
                cnt = 0;

                if(bluetoothService.getState() == BluetoothService.STATE_CONNECTED){
                    sendMessage("1", MODE_REQUEST);
                    prBar.show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "블루투스 연결 필요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sos_btn = (Button) findViewById(R.id.sos_btn);
        sos_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), EmergencyActivity.class);
                startActivityForResult(i, 0);
            }
        });

        s0woo_btn = (Button) findViewById(R.id.s0woo_btn);
        s0woo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SpeechActivity.class);
                startActivityForResult(i, 0);
            }
        });

        yuna_btn = (Button) findViewById(R.id.yuna_btn);
        yuna_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(i, 0);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(TAG, "onActivityResult" + resultCode);

        switch(requestCode){
            case REQUEST_ENABLE_BT:
                if(resultCode== Activity.RESULT_OK){
                    bluetoothService.scanDevice();
                }
                else{
                    Log.d(TAG, "Bluetooth in not enable");
                }
                break;

            case REQUEST_CONNECT_DEVICE:
                if(resultCode == Activity.RESULT_OK){
                    bluetoothService.getDeviceInfo(data);
                }
                break;

        }
    }

    private synchronized void sendMessage(String message, int mode){    //안드로이드 -> 아두이노
        if(mSendingState == STATE_SENDING){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mSendingState = STATE_SENDING;

        if(bluetoothService.getState() != BluetoothService.STATE_CONNECTED){
            mSendingState = STATE_NO_SENDING;
            return;
        }

        if(message.length() > 0){
            if(message == "1"){
                bluetoothService.write(message, mode);
            }

        }

        mSendingState = STATE_NO_SENDING;
        notify();
    }

    //카메라에서 비트맵 이미지 가져오기
    public void setImg(byte[] buf){
        Bitmap bmp = BitmapFactory.decodeByteArray(buf, 0, buf.length);
        if(bmp != null){
            //Toast.makeText(getApplicationContext(), "비트맵 생성!!!!!!!!!!!!" + cnt, Toast.LENGTH_SHORT).show();
            imgArray[cnt] = bmp;
            cnt++;
            if(cnt==4){
                //checkPerm();

                prHandler.sendEmptyMessage(CNT_4);
                arrState = NOT_NULL_IMGARRAY;

                //사진 4장 -> VrPanoramaActivity로 넘어감
                Intent i = new Intent(getApplicationContext(), VrPanoramaActivity.class);
                startActivityForResult(i,0);
            }
        }
        else {
            //Toast.makeText(getApplicationContext(), "실패다 ㅅㅂ...", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkPerm(){
        //나중에 permission 분리할 수 있다면 하는걸로.. 처음 시작할 때 넣는게 나을 거 같다
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED //음성 퍼미션
                || ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {

            }
        } else {
            //startUsingSpeechSDK();
        }

        //위치 퍼미션
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //startUsingSpeechSDK();
                } else {
                    finish();
                }
                break;
            default:
                break;
        }
    }
}
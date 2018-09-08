package com.example.home.secretary_kim.VR;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.home.secretary_kim.R;
import com.example.home.secretary_kim.SpeechActivity;

public class BluetoothActivity  extends AppCompatActivity {

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

    //private Button bluetooth_btn;
    private Button cam_btn;
    private Button speech_btn;

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

        //화면 시작하자마자 블루투스 연결
        if(bluetoothService.getDeviceState()){
            bluetoothService.enableBluetooth();
        }
        else{
            finish();
        }

//        bluetooth_btn = (Button)findViewById(R.id.bluetooth_btn);
//        bluetooth_btn.setOnClickListener(new Button.OnClickListener(){
//
//            @Override
//            public void onClick(View view) {
//                if(bluetoothService.getDeviceState()){
//                    bluetoothService.enableBluetooth();
//                }
//                else{
//                    finish();
//                }
//            }
//        });

        prBar = new ProgressDialog(this);
        prBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        prBar.setMessage("이미지 로딩중입니다");

        cam_btn = (Button)findViewById(R.id.cam_btn);
        cam_btn.setOnClickListener(new Button.OnClickListener(){

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

        speech_btn = (Button)findViewById(R.id.speech_btn);
        speech_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(), SpeechActivity.class);
                startActivity(intent);
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
}

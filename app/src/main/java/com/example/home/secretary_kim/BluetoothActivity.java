package com.example.home.secretary_kim;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.nio.ByteBuffer;

public class BluetoothActivity  extends AppCompatActivity {

    private static final String TAG = "MAIN";

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private Button bluetooth_btn;
    private Button cam_btn;
    private ImageView img_view;

    private BluetoothService bluetoothService = null;
    private StringBuffer mOutStringBuffer;

    private static final boolean D = true;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_WRITE = 2;

    public static final int MODE_REQUEST = 1;

    //private int mSeletedBtn;

    private static final int STATE_SENDING = 1;
    private static final int STATE_NO_SENDING = 2;
    private int mSendingState;

    private final Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);

            switch(msg.what){
                case MESSAGE_STATE_CHANGE:
                    if(D){
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    }
                    switch(msg.arg1){
                        case BluetoothService.STATE_CONNECTED:
                            Toast.makeText(getApplicationContext(), "블루투스 연결 성공", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothService.STATE_FAIL:
                            Toast.makeText(getApplicationContext(), "블루투스 연결 실패", Toast.LENGTH_SHORT).show();
                            break;

                    }
                    break;
                case MESSAGE_WRITE:

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_bluetooth);

        bluetooth_btn = (Button)findViewById(R.id.bluetooth_btn);
        bluetooth_btn.setOnClickListener(mClickListener);

        cam_btn = (Button)findViewById(R.id.cam_btn);
        cam_btn.setOnClickListener(mClickListener);

        if(bluetoothService == null){
            bluetoothService = new BluetoothService(this, mHandler);
            mOutStringBuffer = new StringBuffer("");
        }
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

    private View.OnClickListener mClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.bluetooth_btn:
                    if(bluetoothService.getDeviceState()){
                        bluetoothService.enableBluetooth();
                    }
                    else{
                        finish();
                    }
                    break;
                case R.id.cam_btn:
                    if(bluetoothService.getState() == BluetoothService.STATE_CONNECTED){
                        sendMessage("16", MODE_REQUEST);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "블루투스 연결 필요", Toast.LENGTH_SHORT).show();
                    }
                    break;

                default:
                    break;
            }

        }

    };

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
            if(message == "16"){
                int msg = Integer.parseInt(message);

                byte[] send = ByteBuffer.allocate(Integer.SIZE/8).putInt(msg).array();  //사진 찍을려면 int 16 값이 넘어가야 되기 때문에 Str->Int->Byte 변형
                bluetoothService.write(send, mode);

                mOutStringBuffer.setLength(0);
            }

        }

        mSendingState = STATE_NO_SENDING;
        notify();
    }
}

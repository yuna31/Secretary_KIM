package com.example.home.secretary_kim.VR;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.home.secretary_kim.R;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

public class VrPanoramaActivity extends Activity {
    private static final String TAG = "VRTESTACTIVITY";

    private VrPanoramaView panoramaView;
    private ImageActivity imgActivity;
    private Bitmap img;
    private Bitmap[] imgArray;
    public boolean loadImageSuccessful;

    private VrPanoramaView.Options panoOptions = new VrPanoramaView.Options();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vr_layout);
        this.imgArray = BluetoothActivity.imgArray;

        if(BluetoothActivity.arrState == BluetoothActivity.NULL_IMGARRAY){
            img = BitmapFactory.decodeResource(this.getResources(), R.drawable.waiting);
        }
        else if(BluetoothActivity.arrState == BluetoothActivity.NOT_NULL_IMGARRAY){
            panoramaImg();
        }

        panoramaView = (VrPanoramaView)findViewById(R.id.pano_view);
        panoramaView.setEventListener(new ActivityEventListener());

        panoOptions.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER;
        panoramaView.loadImageFromBitmap(img, panoOptions);
        panoramaView.setDisplayMode(3);

        panoramaView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //SpeechActivity 백그라운드로 실행 -> 핸들러 이용해야될 듯ㅅㅂ
                //삐- 소리 후 음성 인식하기
                //음성 인식이 완료되면 비트맵 이미지 변경 -> 좌표에만 빨간색 사각형 표시
                //음성 인식 -> "전송" 이면 해당 비트맵 이미지 저장 후 전송
                return true;
            }
        });
    }

    //파노라마 이미지 생성
    public void panoramaImg(){
        imgActivity = new ImageActivity(imgArray);

        img = imgActivity.makePanorama();

        for(int i = 3; i >= 0; i--){
            imgArray[i].recycle();
            imgArray[i] = null;
        }
    }

    @Override
    protected void onPause() {
        panoramaView.pauseRendering();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        panoramaView.resumeRendering();
    }

    @Override
    protected void onDestroy() {
        // Destroy the widget and free memory.
        panoramaView.shutdown();
        super.onDestroy();
    }

    /**
     * Listen to the important events from widget.
     */
    private class ActivityEventListener extends VrPanoramaEventListener {
        /**
         * Called by pano widget on the UI thread when it's done loading the image.
         */
        @Override
        public void onLoadSuccess() {
            loadImageSuccessful = true;
        }

        /**
         * Called by pano widget on the UI thread on any asynchronous error.
         */
        @Override
        public void onLoadError(String errorMessage) {
            loadImageSuccessful = false;
            Toast.makeText(
                    VrPanoramaActivity.this, "Error loading pano: " + errorMessage, Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error loading pano: " + errorMessage);
        }
    }
}

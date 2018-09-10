package com.example.home.secretary_kim.VR;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.home.secretary_kim.R;
import com.example.home.secretary_kim.VoiceRecoActivity;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.kakao.sdk.newtoneapi.SpeechRecognizeListener;
import com.kakao.sdk.newtoneapi.SpeechRecognizerActivity;
import com.kakao.sdk.newtoneapi.SpeechRecognizerClient;
import com.kakao.sdk.newtoneapi.SpeechRecognizerManager;
import com.kakao.sdk.newtoneapi.impl.util.PermissionUtils;

import java.util.ArrayList;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class VrPanoramaActivity extends Activity implements SpeechRecognizeListener {
    private static final String TAG = "VRTESTACTIVITY";

    private VrPanoramaView panoramaView;
    //private ImageActivity imgActivity;
    private Bitmap img_pano;    //그냥 4장 연결만 한거
    private Bitmap img; //좌표판이랑 좌표까지 있는거
    private Bitmap img_result;   //사각형 그려진거
    private Bitmap[] imgArray;
    public boolean loadImageSuccessful;

    private VrPanoramaView.Options panoOptions = new VrPanoramaView.Options();

    private SpeechRecognizerClient client;
    private String resultString;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String serviceType = SpeechRecognizerClient.SERVICE_TYPE_WEB;

        setContentView(R.layout.vr_layout);
        this.imgArray = BluetoothActivity.imgArray;

        if(BluetoothActivity.arrState == BluetoothActivity.NULL_IMGARRAY){
            img = BitmapFactory.decodeResource(this.getResources(), R.drawable.waiting);
        }
        else if(BluetoothActivity.arrState == BluetoothActivity.NOT_NULL_IMGARRAY){
            panoramaImg();
        }

        SpeechRecognizerManager.getInstance().initializeLibrary(this);

        panoramaView = (VrPanoramaView)findViewById(R.id.pano_view);
        panoramaView.setEventListener(new ActivityEventListener());

        panoOptions.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER;
        panoramaView.loadImageFromBitmap(img, panoOptions);
        panoramaView.setDisplayMode(3);

        if(PermissionUtils.checkAudioRecordPermission(VrPanoramaActivity.this)) {

            SpeechRecognizerClient.Builder builder = new SpeechRecognizerClient.Builder().
                    setServiceType(serviceType);

            client = builder.build();
        }

        //터치 -> 음성인식
        panoramaView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //Toast.makeText(getApplicationContext(), "토스트", Toast.LENGTH_SHORT).show();

                //음성 인식
                client.setSpeechRecognizeListener(VrPanoramaActivity.this);
                client.startRecording(true);

                return true;
            }
        });
    }

    //파노라마 이미지 생성
    public void panoramaImg(){
        ImageActivity imgActivity = new ImageActivity(imgArray);
        img_pano = imgActivity.panoramaImg_just();
        img = imgActivity.makePanorama();

        for(int i = 3; i >= 0; i--){
            imgArray[i].recycle();
            imgArray[i] = null;
        }
    }

    public Bitmap panoramaImg_result(String r1, String r2){
        ImageActivity imageActivity = new ImageActivity(img_pano);
        img_result = imageActivity.panoramaImg_result(r1, r2);
        return img_result;
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

        // API를 더이상 사용하지 않을 때 finalizeLibrary()를 호출한다.
        SpeechRecognizerManager.getInstance().finalizeLibrary();
    }

    @Override
    public void onReady() {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {

    }

//    public void makeAlertDialog(final String result) {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("음성인식 결과");
//        builder.setMessage(result);
//
//        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//                detachString(result);
//            }
//        });
//
//        builder.setNegativeButton("다시하기", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//                String serviceType = SpeechRecognizerClient.SERVICE_TYPE_WEB;
//                Intent intent = new Intent(getApplicationContext(), VoiceRecoActivity.class);
//                intent.putExtra(SpeechRecognizerActivity.EXTRA_KEY_SERVICE_TYPE, serviceType);
//                startActivityForResult(intent, 0);
//            }
//        });
//
//        builder.show();
//    }

    //수정이 필요하다
    public String[] detachString(String result) {
        String[] tmp = new String[2];
        if(result.length()>=7) {
            String from = result.substring(0,3);
            String to = result.substring(((result.length())-3), result.length());
            //System.out.println("*************************************시작: "+ from + "도착" + to);
            Toast.makeText(getApplicationContext(),"시작: "+ from + " 도착: " + to, Toast.LENGTH_LONG).show();
            Log.d(TAG, "시작, 도착 : " + from + ", " + to);
            tmp[0] = from;
            tmp[1] = to;
            Log.d(TAG, "TMP : " + tmp[0] + ", " + tmp[1]);
        }
        return tmp;
    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        Log.e("SpeechSampleActivity", "onError : " + errorMsg);

        client = null;
    }

    @Override
    public void onPartialResult(String partialResult) {

    }

    @Override
    public void onResults(Bundle results) {
        final StringBuilder builder = new StringBuilder();
        Log.i("SpeechSampleActivity", "onResults");

        final ArrayList<String> texts = results.getStringArrayList(SpeechRecognizerClient.KEY_RECOGNITION_RESULTS);
        final ArrayList<Integer> confs = results.getIntegerArrayList(SpeechRecognizerClient.KEY_CONFIDENCE_VALUES);

        final Activity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // finishing일때는 처리하지 않는다.
                if (activity.isFinishing()) return;

                builder.append(texts.get(0));
                resultString = builder.toString();
                Log.d(TAG, "VOICE : " + resultString); //xxx-xxx
                String[] tmp = detachString(resultString);
                panoramaView.loadImageFromBitmap(panoramaImg_result(tmp[0], tmp[1]), panoOptions);
                //makeAlertDialog(builder.toString());
            }
        });
    }

    @Override
    public void onAudioLevel(float audioLevel) {

    }

    @Override
    public void onFinished() {

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

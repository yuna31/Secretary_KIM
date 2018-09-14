package com.example.home.secretary_kim.VR;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.home.secretary_kim.LocationActivity;
import com.example.home.secretary_kim.R;
import com.example.home.secretary_kim.S3UploadActivity;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.kakao.sdk.newtoneapi.SpeechRecognizeListener;
import com.kakao.sdk.newtoneapi.SpeechRecognizerClient;
import com.kakao.sdk.newtoneapi.SpeechRecognizerManager;
import com.kakao.sdk.newtoneapi.impl.util.PermissionUtils;

import java.util.ArrayList;

public class VrPanoramaActivity extends Activity implements SpeechRecognizeListener {
    private static final String TAG = "VRTESTACTIVITY";
    LocationActivity locationActivity;

    private VrPanoramaView panoramaView;
    //private ImageActivity imgActivity;
    private Bitmap img_pano;    //그냥 4장 연결만 한거
    private Bitmap img; //좌표판이랑 좌표까지 있는거
    public static Bitmap img_result;   //사각형 그려진거
    private Bitmap[] imgArray;
    public boolean loadImageSuccessful;

    private VrPanoramaView.Options panoOptions = new VrPanoramaView.Options();

    private SpeechRecognizerClient client;
    private String resultString;
    boolean isSpeechPerm = false;
    SpeechRecognizerClient.Builder builder;

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
            isSpeechPerm = true;
            builder = new SpeechRecognizerClient.Builder().setServiceType(serviceType);

            client = builder.build();
        }

        //터치 -> 음성인식
        panoramaView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //Toast.makeText(getApplicationContext(), "토스트", Toast.LENGTH_SHORT).show();
                if(isSpeechPerm == true){
                    client.setSpeechRecognizeListener(VrPanoramaActivity.this);
                    client.startRecording(true);
                }
                else{
                    client = builder.build();
                    isSpeechPerm = true;
                    Log.d(TAG, "isSpeechPerm false");
                }

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

    public Bitmap panoramaImg_result(String r1, String r2, int cnt){
        ImageActivity imageActivity = new ImageActivity(img_pano);
        img_result = imageActivity.panoramaImg_result(r1, r2, cnt);
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

    //수정이 필요하다 -> 완료
    public String[] detachString(String result) {
        Log.d("SPEECHTEST", "RESULT, length : " + result + ", " + result.length());
        String[] tmp = new String[3];;
        if(result.length()>=6) {    //xxx-yyy / xxx 갖다줘
            String from = result.substring(0,3);
            String to = result.substring(((result.length())-3), result.length());
            //System.out.println("*************************************시작: "+ from + "도착" + to);
            Toast.makeText(getApplicationContext(),"시작: "+ from + " 도착: " + to, Toast.LENGTH_LONG).show();
            Log.d(TAG, "시작, 도착 : " + from + ", " + to);
            tmp[0] = "move";
            tmp[1] = from;
            tmp[2] = to;
        }
        else if(result.length()>=3) {//xxx -> 무조건 갖다줘
            if(!result.equals("갖다줘") && !result.equals("옮겨줘")){
                tmp[0] = "take";
                tmp[1] = result;
                tmp[2] = "-1";
            }
            if(result.equals("갖다 줘") || result.equals("갖다줘") || result.equals("옮겨줘")){
                tmp[0] = "send";
                tmp[1] = result;
                tmp[2] = "-1";
            }

        }
        else if(result.length()==2){
            if(result.equals("전송")){
                tmp[0] = "send";
                tmp[1] = result;
                tmp[2] = "-1";
            }
        }
        else{
            tmp[0] = "etc";
            tmp[1] = "-1";
            tmp[2] = "-1";
        }
        Log.d(TAG, "TMP : " + tmp[0] + ", " + tmp[1] + ", " + tmp[2]);
        return tmp;
    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        Log.e("SpeechSampleActivity", "onError : " + errorMsg);

        isSpeechPerm =false;
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
                if(tmp[0] == "move"){   //옮겨줘 -> 사각형 두 개
                    panoramaView.loadImageFromBitmap(panoramaImg_result(tmp[1], tmp[2], 2), panoOptions);
                }
                else if(tmp[0] == "take"){  //갖다줘 -> 사각형 한 개
                    panoramaView.loadImageFromBitmap(panoramaImg_result(tmp[1], tmp[2], 1), panoOptions);
                }
                else if(tmp[0] == "send"){  //사진 전송
                    Log.d(TAG, "전송");
                    panoramaView.setDisplayMode(2);
                    Toast.makeText(getApplicationContext(), "전송할거", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(getApplicationContext(), S3UploadActivity.class);
                    startActivityForResult(i, 0);
                }
                else{
                    Log.d(TAG, "잘못된 음성");
                }
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

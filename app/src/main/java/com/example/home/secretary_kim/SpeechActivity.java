package com.example.home.secretary_kim;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kakao.sdk.newtoneapi.SpeechRecognizeListener;
import com.kakao.sdk.newtoneapi.SpeechRecognizerActivity;
import com.kakao.sdk.newtoneapi.SpeechRecognizerClient;
import com.kakao.sdk.newtoneapi.SpeechRecognizerManager;
import com.kakao.sdk.newtoneapi.impl.util.PermissionUtils;

import java.util.ArrayList;
import java.util.StringTokenizer;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by s0woo on 2018-07-15.
 * Edited by s0woo on 2018-08-28.
 */

public class SpeechActivity extends Activity implements View.OnClickListener, SpeechRecognizeListener{
    private SpeechRecognizerClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);


        //나중에 permission 분리할 수 있다면 하는걸로.. 처음 시작할 때 넣는게 나을 거 같다
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {

            }
        } else {
            //startUsingSpeechSDK();
        }


        SpeechRecognizerManager.getInstance().initializeLibrary(this);
        findViewById(R.id.speechbutton).setOnClickListener(this);
        findViewById(R.id.cancelbutton).setOnClickListener(this);
        findViewById(R.id.restartbutton).setOnClickListener(this);
        findViewById(R.id.stopbutton).setOnClickListener(this);
        findViewById(R.id.uibutton).setOnClickListener(this);
        findViewById(R.id.connectbutton).setOnClickListener(this);
        findViewById(R.id.storagebutton).setOnClickListener(this);
        findViewById(R.id.downloadbutton).setOnClickListener(this);
        setButtonsStatus(true);
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


    @Override
    public void onDestroy() {
        super.onDestroy();

        // API를 더이상 사용하지 않을 때 finalizeLibrary()를 호출한다.
        SpeechRecognizerManager.getInstance().finalizeLibrary();
    }

    private void setButtonsStatus(boolean enabled) {
        findViewById(R.id.speechbutton).setEnabled(enabled);
        findViewById(R.id.restartbutton).setEnabled(!enabled);
        findViewById(R.id.cancelbutton).setEnabled(!enabled);
        findViewById(R.id.stopbutton).setEnabled(!enabled);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        String serviceType = SpeechRecognizerClient.SERVICE_TYPE_WEB;


        // 음성인식 버튼 listener
        if (id == R.id.speechbutton) {
            if(PermissionUtils.checkAudioRecordPermission(this)) {

                SpeechRecognizerClient.Builder builder = new SpeechRecognizerClient.Builder().
                        setServiceType(serviceType);

                client = builder.build();
                client.setSpeechRecognizeListener(this);
                client.startRecording(true);

                setButtonsStatus(false);
            }
        }
        // 음성인식 취소버튼 listener
        else if (id == R.id.cancelbutton) {
            if (client != null) {
                client.cancelRecording();
            }

            setButtonsStatus(true);
        }
        // 음성인식 재시작버튼 listener
        else if (id == R.id.restartbutton) {
            if (client != null) {
                client.cancelRecording();
                client.startRecording(true);
            }
        }
        // 음성인식 중지버튼 listener
        else if (id == R.id.stopbutton) {
            if (client != null) {
                client.stopRecording();
            }
        }

        if (id == R.id.uibutton) {
            Intent i = new Intent(getApplicationContext(), VoiceRecoActivity.class);
            i.putExtra(SpeechRecognizerActivity.EXTRA_KEY_SERVICE_TYPE, serviceType);
            startActivityForResult(i, 0);
        }
        else if (id == R.id.connectbutton) {
            Intent i = new Intent(getApplicationContext(), MakeConnActivity.class);
            startActivityForResult(i, 0);
        }
        else if (id == R.id.storagebutton) {
            Intent i = new Intent(getApplicationContext(), S3UploadActivity.class);
            startActivityForResult(i, 0);
        }
        else if (id == R.id.downloadbutton) {
            Intent i = new Intent(getApplicationContext(), S3DownloadActivity.class);
            startActivityForResult(i, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(VoiceRecoActivity.EXTRA_KEY_RESULT_ARRAY);

            final StringBuilder builder = new StringBuilder();

            /* 결과 다 보여주는 코드
            for (String result : results) {
                builder.append(result);
                builder.append("\n");
            }
            */

            //제일 가능성있는 값 하나만 가지고 오기
            builder.append(results.get(0));
            makeAlertDialog(builder.toString());

            /*
            new AlertDialog.Builder(this).
                    setMessage(builder.toString()).
                    setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    }).
                    show();
                    */
        }
        else if (requestCode == RESULT_CANCELED) {
            // 음성인식의 오류 등이 아니라 activity의 취소가 발생했을 때.
            if (data == null) {
                return;
            }

            int errorCode = data.getIntExtra(VoiceRecoActivity.EXTRA_KEY_ERROR_CODE, -1);
            String errorMsg = data.getStringExtra(VoiceRecoActivity.EXTRA_KEY_ERROR_MESSAGE);

            if (errorCode != -1 && !TextUtils.isEmpty(errorMsg)) {
                new AlertDialog.Builder(this).
                        setMessage(errorMsg).
                        setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).
                        show();
            }
        }
    }

    public void makeAlertDialog(final String result) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("음성인식 결과");
        builder.setMessage(result);

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                detachString(result);
            }
        });

        builder.setNegativeButton("다시하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                String serviceType = SpeechRecognizerClient.SERVICE_TYPE_WEB;
                Intent intent = new Intent(getApplicationContext(), VoiceRecoActivity.class);
                intent.putExtra(SpeechRecognizerActivity.EXTRA_KEY_SERVICE_TYPE, serviceType);
                startActivityForResult(intent, 0);
            }
        });

        builder.show();
    }

    //수정이 필요하다
    public void detachString(String result) {
        if(result.length()>7) {
            String from = result.substring(0,3);
            String to = result.substring(5,9);
            //System.out.println("*************************************시작: "+ from + "도착" + to);
            Toast.makeText(getApplicationContext(),"시작: "+ from + " 도착: " + to, Toast.LENGTH_LONG).show();
        }
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

    @Override
    public void onError(int errorCode, String errorMsg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setButtonsStatus(true);
            }
        });

        client = null;
    }

    @Override
    public void onPartialResult(String partialResult) {

    }

    //버튼사용하는 결과창
    @Override
    public void onResults(final Bundle results) {
        final StringBuilder builder = new StringBuilder();

        final ArrayList<String> texts = results.getStringArrayList(SpeechRecognizerClient.KEY_RECOGNITION_RESULTS);
        final ArrayList<Integer> confs = results.getIntegerArrayList(SpeechRecognizerClient.KEY_CONFIDENCE_VALUES);

        /*전체 결과 다 받아오기
        for (int i = 0; i < texts.size(); i++) {
            builder.append(texts.get(i));
            builder.append(" (");
            builder.append(confs.get(i).intValue());
            builder.append(")\n");
        }
        */

        final Activity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // finishing일때는 처리하지 않는다.
                if (activity.isFinishing()) return;

                builder.append(texts.get(0));
                builder.append(" (가능성 : ");
                builder.append(confs.get(0).intValue());
                builder.append(")\n");
                makeAlertDialog(builder.toString());
                /*
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity).
                        setMessage(builder.toString()).
                        setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog.show();
                */

                setButtonsStatus(true);
            }
        });

        client = null;
    }

    @Override
    public void onAudioLevel(float audioLevel) {
    }

    @Override
    public void onFinished() {
    }

}

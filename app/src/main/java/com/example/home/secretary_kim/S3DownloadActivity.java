package com.example.home.secretary_kim;


import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.example.home.secretary_kim.LOGIN.LoginActivity;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.helper.log.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by s0woo on 2018-08-20.
 */

public class S3DownloadActivity extends AppCompatActivity {
    String ReceiverEmail;
    String fileName = "";
    String userID;

    private VrPanoramaView.Options panoOptions = new VrPanoramaView.Options();

    private Button ok_btn;
    private Button old_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vr_layout_btn);

        requestMe();

        final Handler handler = new Handler();
        new Thread() {
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String url = "http://13.209.64.57:8080/getNewFilename.jsp";
                        NetworkTask networkTask = new NetworkTask(url, null);
                        networkTask.execute();
                    }
                }, 1500);
            }
        }.start();

        final Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread() {
                    public void run() {
                        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                                getApplicationContext(),
                                "ap-northeast-2:29422a03-b373-4e0a-85e7-4c1a9a28d16d", // 자격 증명 풀 ID
                                Regions.AP_NORTHEAST_2 // 리전
                        );

                        final AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
                        TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());

                        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
                        s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");

                        S3Object s3Object = s3.getObject("s0woo", fileName);

                        try (InputStream is = s3Object.getObjectContent()) {
                            byte[] bytes = IOUtils.toByteArray(is);
                            System.out.println("********************download bytearray : " + bytes);
                            System.out.println("********************temp length : " + bytes.length);
                            set(bytes);
//                            bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//
//                            //UI를 변경하기 위한 Thread
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    ImageView imageView = (ImageView) findViewById(R.id.bitmapView);
//                                    imageView.setImageBitmap(bmp);
//                                }
//                            });

                            is.close();
                        } catch (IOException e){
                            e.printStackTrace();
                        } catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        },4000);

        ok_btn = (Button) findViewById(R.id.ok_btn);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(i, 0);
                finish();
            }
        });

        old_btn = (Button) findViewById(R.id.old_btn);
        old_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), S3DownloadOldActivity.class);
                i.putExtra("UserMail", userID);
                startActivityForResult(i, 0);
                finish();
            }
        });

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
            S3DownloadActivity.RequestHttpURLConnection requestHttpURLConnection = new S3DownloadActivity.RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result;
        }


        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
            // doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            // jsp 처리 결과 메시지를 가져옴 contains를 통해 이벤트 처리
            try {
                JSONObject json = new JSONObject(s);
                fileName = json.getString("fileName");
                System.out.println("**********get filename in server : " + fileName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class RequestHttpURLConnection {
        public String request(String _url, ContentValues _params) {
            HttpURLConnection urlConn = null;
            StringBuffer sbParams = new StringBuffer();
            userID = getIntent().getStringExtra("UserMail");
            String secretaryID = ReceiverEmail; //수정할것
            System.out.println("request user : " + userID + " " +  secretaryID);

            //StringBuffer에 파라미터 연결
            // 보낼 데이터가 없으면 파라미터를 비운다.
            if (_params == null) {
                sbParams.append("secretaryID=" + secretaryID);
                sbParams.append("&userID=" + userID);
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
                    //System.out.println("line : '" + line + "'");
                    page += line;
                }
                //System.out.println("page : " + page);

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

    private void requestMe() {
        List<String> keys = new ArrayList<>();
        keys.add("kakao_account.email");

        UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(i, 0);
                Toast.makeText(getApplicationContext(), "다시 로그인해주세요", Toast.LENGTH_LONG).show();
                System.out.println("****** " + errorResult.getErrorMessage());
            }

            @Override
            public void onSuccess(MeV2Response response) {
                Logger.d("email: " + response.getKakaoAccount().getEmail());
                ReceiverEmail = response.getKakaoAccount().getEmail();
                //Toast.makeText(getApplicationContext(), "kakao email : " + response.getKakaoAccount().getEmail(), Toast.LENGTH_SHORT).show();
                System.out.println("@@@@@@@@@@Email : " + ReceiverEmail);
            }
        });
    }

    public void set(byte[] bytes){
        VrPanoramaView panoramaView;
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        panoramaView = (VrPanoramaView)findViewById(R.id.pano_view);
        panoramaView.setDisplayMode(1);
        panoOptions.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER;
        panoramaView.loadImageFromBitmap(bmp, panoOptions);
    }
}

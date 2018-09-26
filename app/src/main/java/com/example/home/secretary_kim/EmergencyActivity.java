package com.example.home.secretary_kim;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.home.secretary_kim.LOGIN.LoginActivity;
import com.example.home.secretary_kim.VR.LocationClass;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.helper.log.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by s0woo on 2018-08-20.
 * Edited by s0woo on 2018-09-04.
 */

public class EmergencyActivity extends AppCompatActivity {
    String SenderEmail; String SenderName;
    private LocationClass locationClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        TextView textView = (TextView)findViewById(R.id.textview);

        //업로드 성공하면 다음 동작 수행해야함
        requestMe();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String url = "http://13.209.64.57:8080/emergency.jsp";
                NetworkTask networkTask = new NetworkTask(url, null);
                networkTask.execute();
            }
        }, 1300);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= 23) {
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                System.out.println("Permission: "+permissions[0]+ "was "+grantResults[0]);
                //resume tasks needing this permission
            }
        }
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            // jsp 처리 결과 메시지를 가져옴 contains를 통해 이벤트 처리
            //testView.setText(s);
            if (s.contains("success")) {
                Toast.makeText(getApplicationContext(), "알림전송성공", Toast.LENGTH_LONG).show();
                finish();
            }
            if (s.contains("java.lang.IllegalArgumentException: registrationIds cannot be empty")) {
                Toast.makeText(getApplicationContext(), "관리자에게 사용자-관리자 연결을 요청하세요", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }


    public class RequestHttpURLConnection {
        public String request(String _url, ContentValues _params) {
            HttpURLConnection urlConn = null;
            StringBuffer sbParams = new StringBuffer();
            String userName = SenderName;
            String userID = SenderEmail; //수정할것
            //*******************************************************
            String place = locationClass.getLoc();
            String message = SenderName + "님의 긴급호출입니다 (" + place + ")";

            //StringBuffer에 파라미터 연결
            // 보낼 데이터가 없으면 파라미터를 비운다.
            if (_params == null) {
                sbParams.append("userID=" + userID);
                sbParams.append("&message=" + message);
            }

            try {
                URL url = new URL(_url);
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setRequestMethod("POST");
                urlConn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
                urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

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
                // 출력물의 라인과 그 합에 대한 변수.
                String line;
                String page = "";
                // 라인을 받아와 합친다.
                while ((line = reader.readLine()) != null){
                    page += line;
                }
                System.out.println("upload 결과 page " + page);
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
        keys.add("properties.nickname");
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
                //Logger.d("email: " + response.getKakaoAccount().getEmail());
                SenderName = response.getNickname();
                SenderEmail = response.getKakaoAccount().getEmail();
                //Toast.makeText(getApplicationContext(), "kakao email : " + response.getKakaoAccount().getEmail(), Toast.LENGTH_LONG).show();
                System.out.println("@@@@@전송자 정보 : " + SenderName + " " + SenderEmail);
            }
        });
    }

}


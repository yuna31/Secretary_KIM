package com.example.home.secretary_kim;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.kakao.network.NetworkTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Created by s0woo 2018-08-14.
 * Edited by s0woo 2018-08-25.
 *                 2018-09-01.
 * 사용자와 관리자 연결하는 DB 생성 + push 알림을 위한 firebase token 생성
 */

public class MakeConnActivity extends AppCompatActivity  {

    public EditText ed_userId, ed_secretaryID;
    public Button saveBtn;
    String userID, secretaryID, secretaryUUID;

    Connection conn = null;
    Statement stmt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makeconn);

        ed_userId = (EditText)findViewById(R.id.userID);
        ed_secretaryID = (EditText)findViewById(R.id.secretaryID);
        saveBtn = (Button)findViewById(R.id.save_button);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyFirebaseInstanceIDService firebaseInstanceIDService = new MyFirebaseInstanceIDService();

                userID = ed_userId.getText().toString();
                secretaryID = ed_secretaryID.getText().toString();
                secretaryUUID = firebaseInstanceIDService.getToken(); //UUID 받아오는 코드로 수정해야함

                String url = "http://13.209.64.57:8080/dbSave.jsp";

                NetworkTask networkTask = new NetworkTask(url, null);
                networkTask.execute();
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
            if (s.contains("성공")) {
                Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_SHORT).show();
                //finish();
            }
        }
    }


    public class RequestHttpURLConnection {
        public String request(String _url, ContentValues _params) {
            HttpURLConnection urlConn = null;
            StringBuffer sbParams = new StringBuffer();

            //StringBuffer에 파라미터 연결
            // 보낼 데이터가 없으면 파라미터를 비운다.
            if (_params == null) {
                sbParams.append("userID=" + userID);
                sbParams.append("&secretaryID=" + secretaryID);
                sbParams.append("&secretaryUUID=" + secretaryUUID);
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

    public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

        private static final String TAG = "MyFirebaseIIDService";

        // [START refresh_token]
        @Override
        public void onTokenRefresh() {
            // Get updated InstanceID token.
            String token = FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, "Refreshed token: " + token);
        }

        public String getToken() {
            String token = FirebaseInstanceId.getInstance().getToken();
            System.out.println("###########token : " + token);
            return token;
        }
    }
}

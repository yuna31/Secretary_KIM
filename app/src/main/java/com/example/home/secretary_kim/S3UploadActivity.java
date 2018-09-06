package com.example.home.secretary_kim;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by s0woo on 2018-08-20.
 */

public class S3UploadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s3upload);

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-northeast-2:29422a03-b373-4e0a-85e7-4c1a9a28d16d", // 자격 증명 풀 ID
                Regions.AP_NORTHEAST_2 // 리전
        );

        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());

        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
        s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");

        String url = "http://13.209.64.57:8080/pushNoti.jsp";

        NetworkTask networkTask = new NetworkTask(url, null);
        networkTask.execute();


        /*
        TransferObserver observer = transferUtility.upload(
                "s0woo",     // 업로드 할 버킷 이름
                OBJECT_KEY,    // 버킷에 저장할 파일의 이름
                MY_FILE        // 버킷에 저장할 파일
        );



        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

            }

            @Override
            public void onError(int id, Exception ex) {
                ex.printStackTrace();
            }
        });
        */

        // 음성인식 된 파일 업로드
        // 파일 업로드 할 때 파일 이름 환자 이름으로 해야할지..???... 그래야 서버에서 파악해서 노티 보낼 수 있을 것 같고...
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

/*
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            // jsp 처리 결과 메시지를 가져옴 contains를 통해 이벤트 처리
            //testView.setText(s);
            if (s.contains("성공")) {
                Toast.makeText(getApplicationContext(), "등록성공", Toast.LENGTH_LONG).show();
                //finish();
            }
        }
        */
    }


    public class RequestHttpURLConnection {
        public String request(String _url, ContentValues _params) {
            HttpURLConnection urlConn = null;
            StringBuffer sbParams = new StringBuffer();
            String userID = "ssss"; //수정할것

            //StringBuffer에 파라미터 연결
            // 보낼 데이터가 없으면 파라미터를 비운다.
            if (_params == null) {
                sbParams.append("userID=" + userID);
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
}


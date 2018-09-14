package com.example.home.secretary_kim;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.example.home.secretary_kim.LOGIN.LoginActivity;
import com.example.home.secretary_kim.VR.VrPanoramaActivity;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.helper.log.Logger;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by s0woo on 2018-08-20.
 * Edited by s0woo on 2018-09-04.
 */

public class S3UploadActivity extends AppCompatActivity {
    String SenderEmail;
    byte[] bytes;
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd hhmmss");
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s3upload);

        //합치고나면 지워도되는
        if (Build.VERSION.SDK_INT >= 23) {

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                System.out.println("Permission is granted");
                //return true;            }else{
                System.out.println("Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                //return false;
            }
        }else{
            Toast.makeText(this, "External Storage Permission is Grant", Toast.LENGTH_SHORT).show();
            System.out.println("External Storage Permission is Grant ");
            //return true;
        }

        new Thread() {
            public void run() {
                //받아온 파노라마로 수정필요
                Bitmap orgImage;
                if(VrPanoramaActivity.img_result != null){
                    orgImage = VrPanoramaActivity.img_result;
                }
                else{
                    orgImage = BitmapFactory.decodeFile("/storage/emulated/0/test.jpg");
                }

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                orgImage.compress(Bitmap.CompressFormat.PNG, 100, stream);

                //S3에 전송하고 bitmap을 byte[]로 변환해서 넣기
                byte[] bytes = stream.toByteArray();
                //System.out.println("###########byte array : " + bytes);
                //System.out.println("###########byte array size : " + bytes.length);
                mNow = System.currentTimeMillis();
                mDate = new Date(mNow);
                fileName = mFormat.format(mDate);
                //System.out.println("###############fileName : " + fileName);
                uploadToS3(fileName, bytes);
            }
        }.start();

        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 1500);

        //업로드 성공하면 다음 동작 수행해야함
        requestMe();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String url = "http://13.209.64.57:8080/pushNoti.jsp";
                NetworkTask networkTask = new NetworkTask(url, null);
                networkTask.execute();
            }
        }, 1000);
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

    public void uploadToS3(final String OBJECT_KEY, byte[] bis) {
        System.out.println("In uploadToS3 function byte : " + bis);
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-northeast-2:29422a03-b373-4e0a-85e7-4c1a9a28d16d", // 자격 증명 풀 ID
                Regions.AP_NORTHEAST_2 // 리전
        );

        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());

        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
        s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");

        InputStream is = new ByteArrayInputStream(bis);

        ObjectMetadata metadata = new ObjectMetadata();
        //metadata.setContentType("image/jpeg");
        Long contentLength = Long.valueOf(bis.length);
        System.out.println("$$$$$long size :: " + contentLength);
        metadata.setContentLength(contentLength);

        PutObjectRequest putObjectRequest = new PutObjectRequest(
                "s0woo",
                OBJECT_KEY,
                is,
                metadata
        );


        try {
            PutObjectResult putObjectResult = s3.putObject(putObjectRequest);
        }catch (AmazonServiceException ase) {
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Error Message: " + ace.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            // doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
//            // jsp 처리 결과 메시지를 가져옴 contains를 통해 이벤트 처리
//            //testView.setText(s);
//            if (s.contains("success")) {
//                Toast.makeText(getApplicationContext(), "등록성공", Toast.LENGTH_LONG).show();
//
//                //finish();
//            }
//        }
    }


    public class RequestHttpURLConnection {
        public String request(String _url, ContentValues _params) {
            HttpURLConnection urlConn = null;
            StringBuffer sbParams = new StringBuffer();
            String userID = SenderEmail; //수정할것

            System.out.println("###############fileName : " + fileName);

            //StringBuffer에 파라미터 연결
            // 보낼 데이터가 없으면 파라미터를 비운다.
            if (_params == null) {
                sbParams.append("userID=" + userID);
                sbParams.append("&fileName=" + fileName);
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
                SenderEmail = response.getKakaoAccount().getEmail();
                //Toast.makeText(getApplicationContext(), "kakao email : " + response.getKakaoAccount().getEmail(), Toast.LENGTH_LONG).show();
                //System.out.println("@@@@@@@@@@Email : " + tempEmail);
            }
        });
    }
}


package com.example.home.secretary_kim;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.home.secretary_kim.LOGIN.LoginActivity;
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
import java.util.StringTokenizer;

/**
 * Created by s0woo on 2018-09-12.
 */

public class MakeUserListActivity extends AppCompatActivity {
    String ReceiverEmail;
    public static int Unamecnt,newFileNamecnt, Umailcnt;
    //String[] Umail, newFileName, Uname;
    //String[] temp;
    public static String[] temp = new String[100];
    public static String[] Umail = new String[10];
    public static String[] newFileName = new String[10];
    public static String[] Uname = new String[10];
    public static String[][] UserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makeuserlist);

      }

    public void onStart() {
        super.onStart();

        requestMe();

        final Handler handler = new Handler();
        new Thread() {
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String url = "http://13.209.64.57:8080/makeUserList.jsp";
                        NetworkTask networkTask = new NetworkTask(url, null);
                        networkTask.execute();
                    }
                }, 800);
            }
        }.start();


        final Handler handler1 = new Handler();
        new Thread() {
            public void run() {
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendUserCnt();
                        makeInfoArray();
                    }
                }, 1500);
            }
        }.start();

        System.out.println("확인 : " + temp[0]);

        final Handler handler2 = new Handler();
        new Thread() {
            public void run() {
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("값 확인 : " + temp[1]);
                        Intent outIntent = getIntent();
                        outIntent.putExtra("UserInfo", temp);
                        setResult(RESULT_OK, outIntent);
                        finish();
                    }
                }, 4000);
            }
        }.start();

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
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
            // doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            // jsp 처리 결과 메시지를 가져옴 contains를 통해 이벤트 처리
            if (s.contains("success")) {
                Toast.makeText(getApplicationContext(), "리스트 생성 성공", Toast.LENGTH_LONG).show();
                finish();
            }

            StringTokenizer str = new StringTokenizer(s, "{\",\":\"}");
            int countTokens = str.countTokens();
            System.out.println("token 수 : " + countTokens);

            Unamecnt = 0;
            newFileNamecnt = 0;
            Umailcnt = 0;

            //String[] temp = new String[countTokens];
//            String[] Umail = new String[countTokens];
//            String[] newFileName = new String[countTokens];
//            String[] Uname = new String[countTokens];

            for(int i = 0; i < countTokens; i++) {
                temp[i] = str.nextToken();
                System.out.println("**" + i +"번째 토큰 : " + temp[i]);

                if(i % 6 == 0 && i != 0) {
                    Umail[Umailcnt] = temp[i];
                    System.out.println(Umailcnt + "번째 mail : " + Umail[Umailcnt]);
                    Umailcnt++;
                } else if(i % 6 == 4 && i != 0) {
                    newFileName[newFileNamecnt] = temp[i];
                    System.out.println(newFileNamecnt + "번째 file : " + newFileName[newFileNamecnt]);
                    newFileNamecnt++;
                } else if(i % 6 == 2) {
                    Uname[Unamecnt] = temp[i];
                    System.out.println(Unamecnt + "번째 name : " + Uname[Unamecnt]);
                    Unamecnt++;
                }
            }

            System.out.println("in function get count " + Umailcnt);
            //makeInfoArray(Umailcnt, Uname, newFileName, Umail);
        }
    }

    public class RequestHttpURLConnection {
        public String request(String _url, ContentValues _params) {
            HttpURLConnection urlConn = null;
            StringBuffer sbParams = new StringBuffer();
            String secretaryID = ReceiverEmail; //수정할것
            System.out.println("request : " + secretaryID);

            //StringBuffer에 파라미터 연결
            // 보낼 데이터가 없으면 파라미터를 비운다.
            if (_params == null) {
                sbParams.append("secretaryID=" + secretaryID);
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
                    System.out.println("line : '" + line + "'");
                    page += line;
                }
                System.out.println("page : " + page);

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
                Toast.makeText(getApplicationContext(), "kakao email : " + response.getKakaoAccount().getEmail(), Toast.LENGTH_SHORT).show();
                System.out.println("@@@@@@@@@@Email : " + ReceiverEmail);
            }
        });
    }

    public static int sendUserCnt() {
        return Unamecnt;
    }

    public static String[][] makeInfotwoDimensionalArray() {
        String[][] UserInfo = new String[Unamecnt][3];

        for(int i=0 ; i < Unamecnt; i++) {
            UserInfo[i][0] = Uname[i];
        }
        for(int i=0 ; i < Unamecnt; i++) {
            UserInfo[i][1] = newFileName[i];
        }
        for(int i=0 ; i < Unamecnt; i++) {
            UserInfo[i][2] = Umail[i];
        }

        for(int i = 0; i<Unamecnt; i++) {
            for(int j = 0; j<3; j++) {
                System.out.println(i + " " + j + " : " + UserInfo[i][j]);
            }
        }
        return UserInfo;
    }

    public static String[] makeInfoArray() {
        String[] UserInfo = new String[Unamecnt*3];

        for(int i=0 ; i < Unamecnt*3; i++) {
            if(i%3 == 0) {
                UserInfo[i] = Uname[i/3];
                System.out.println(i + "번째 name : " + Uname[i/3] + " " + UserInfo[i]);
            }else if (i%3 == 1) {
                UserInfo[i] = newFileName[i/3];
            }else if (i%3 ==2) {
                UserInfo[i] = Umail[i/3];
            }
         }

        return UserInfo;
    }
}
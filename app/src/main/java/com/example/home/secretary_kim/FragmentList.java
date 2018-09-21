package com.example.home.secretary_kim;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
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

import static android.app.Activity.RESULT_OK;

/**
 * Created by yuna on 2018-08-10.
 */

public class FragmentList extends Fragment {

    private Context context;
    private ListAdapter adapter;
    private String[][] path;
    private ListView listView;
    //private ImageButton search;
    private ImageButton add;
    String ReceiverEmail;

    public static int Unamecnt,newFileNamecnt, Umailcnt;
    public static String[] Umail = new String[10];
    public static String[] newFileName = new String[10];
    public static String[] Uname = new String[10];

    final static int ACT_EDIT = 0;

    public FragmentList() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (ListView) view.findViewById(R.id.list_view);
        //search = (ImageButton) view.findViewById(R.id.search_button);
        //add = (ImageButton) view.findViewById(R.id.add_button);

        requestMe();

        final Handler handler = new Handler();
        new Thread() {
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String url = "http://13.209.64.57:8080/makeUserList.jsp";
                        FragmentList.NetworkTask networkTask = new FragmentList.NetworkTask(url, null);
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
                        //System.out.println("값 확인 : " + Uname[0]);
                        String[][] path = new String[Unamecnt][3];

                        for(int i=0 ; i < Unamecnt; i++) {
                            path[i][0] = Uname[i];
                        }
                        for(int i=0 ; i < Unamecnt; i++) {
                            path[i][1] = newFileName[i];
                        }
                        for(int i=0 ; i < Unamecnt; i++) {
                            path[i][2] = Umail[i];
                        }

                        for(int i = 0; i<Unamecnt; i++) {
                            for(int j = 0; j<3; j++) {
                                System.out.println(i + " " + j + " : " + path[i][j]);
                            }
                        }

                        adapter = new ListAdapter(context, path, true);
                        listView.setAdapter(adapter);


                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {

                            }
                        });
                    }
                }, 1300);
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
                Toast.makeText(getContext(), "리스트 생성 성공", Toast.LENGTH_LONG).show();
                //finish();
            }

            StringTokenizer str = new StringTokenizer(s, "{\",\":\"}");
            int countTokens = str.countTokens();
            System.out.println("token 수 : " + countTokens);

            Unamecnt = 0;
            newFileNamecnt = 0;
            Umailcnt = 0;

            String[] temp = new String[countTokens];

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
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivityForResult(i, 0);
                Toast.makeText(getContext(), "다시 로그인해주세요", Toast.LENGTH_LONG).show();
                System.out.println("****** " + errorResult.getErrorMessage());
            }

            @Override
            public void onSuccess(MeV2Response response) {
                Logger.d("email: " + response.getKakaoAccount().getEmail());
                ReceiverEmail = response.getKakaoAccount().getEmail();
                Toast.makeText(getContext(), "kakao email : " + response.getKakaoAccount().getEmail(), Toast.LENGTH_SHORT).show();
                System.out.println("@@@@@@@@@@Email : " + ReceiverEmail);
            }
        });
    }


}
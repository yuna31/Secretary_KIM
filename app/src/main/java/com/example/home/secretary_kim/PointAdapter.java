package com.example.home.secretary_kim;
 import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
 import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
 import static com.google.vr.cardboard.ThreadUtils.runOnUiThread;
 public class PointAdapter extends RecyclerView.Adapter<PointHolder> {
    public ArrayList<Point> list = new ArrayList<>();
    private double latitude = 37.517400f;
    private double longitude = 127.021924f;
     public static int latlonCnt = 0;
    public static String[] latitudeArray = new String[10];
    public static String[] longitudeArray = new String[10];
    PointHolder holder;
     public PointAdapter() {
        final Handler handler = new Handler();
        new Thread() {
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String url = "http://13.209.64.57:8080/getPlaceList.jsp";
                        NetworkTask networkTask = new NetworkTask(url, null);
                        networkTask.execute();
                     }
                }, 30);
            }
        }.start();
         list = getMapPoint();
    }
    @NonNull
    @Override
    public PointHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    public PointHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
         return new PointHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_point, parent, false));
     }
     @Override
    public void onBindViewHolder(@NonNull PointHolder holder, int position) {
        holder.set(latitude, longitude, list.get(position));
@@ -40,11 +80,16 @@ public void setPoint(double latitude, double longitude) {
    private ArrayList<Point> getMapPoint() {
        ArrayList<Point> list = new ArrayList<>();
         list.add(getPoint("맥도날드", 37.5147400f, 127.021924f));
        list.add(getPoint("가로수길", 37.519446f, 127.023126f));
        list.add(getPoint("아오리의 행방불명", 37.519059f, 127.023776f));
        list.add(getPoint("키친랩 가로수길점", 37.521601f, 127.021769f));
        list.add(getPoint("C27 가로수길점", 37.520711f, 127.023231f));
        for(int i = 0; i < latlonCnt; i++)
        {
           list.add(getPoint(i+"번째", Double.parseDouble(latitudeArray[i]), Double.parseDouble(longitudeArray[i])));
        }
//
//        list.add(getPoint("맥도날드", 37.5147400f, 127.021924f));
        list.add(getPoint("집", 129.0929115f, 129.0929115f));
//       list.add(getPoint("아오리의 행방불명", 37.519059f, 127.023776f));
//        list.add(getPoint("키친랩 가로수길점", 37.521601f, 127.021769f));
//        list.add(getPoint("C27 가로수길점", 37.520711f, 127.023231f));
         return list;
    }
@@ -57,4 +102,118 @@ private Point getPoint(String name, double latitude, double longitude){
         return p;
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
//            if (s.contains("success")) {
//                Toast.makeText(getContext(), "리스트 생성 성공", Toast.LENGTH_LONG).show();
//                //finish();
//            }
             StringTokenizer str = new StringTokenizer(s, "{\"\":\\/\",\"\":\\/\"}");
            int countTokens = str.countTokens();
            int latCnt = 0, lonCnt = 0;
            System.out.println("adapter token 수 : " + countTokens);
             String[] temp = new String[countTokens];
             for(int i = 0; i < countTokens; i++) {
                temp[i] = str.nextToken();
                System.out.println("**" + i +"번째 토큰 : " + temp[i]);
                 if(i % 3 == 0 && i != 0) {
                    latitudeArray[latCnt] = temp[i];
                    System.out.println(latCnt + "번째 lat : " + latitudeArray[latCnt]);
                    latCnt++;
                } else if(i % 3 == 2 && i != 0) {
                    longitudeArray[lonCnt] = temp[i];
                    System.out.println(lonCnt + "번째 lon : " + longitudeArray[lonCnt]);
                    lonCnt++;
                }
            }
            latlonCnt = latCnt;
 //            System.out.println("in function get count " + Umailcnt);
        }
    }
     public class RequestHttpURLConnection {
        public String request(String _url, ContentValues _params) {
            HttpURLConnection urlConn = null;
            StringBuffer sbParams = new StringBuffer();
            //StringBuffer에 파라미터 연결
            // 보낼 데이터가 없으면 파라미터를 비운다.
            if (_params == null) {
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
 }

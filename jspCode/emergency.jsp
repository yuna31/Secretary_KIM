<%@page import="java.net.URLEncoder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 
<%@ page import="java.sql.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.google.android.gcm.server.*"%>
 
<%
request.setCharacterEncoding("UTF-8");
String User_mail = request.getParameter("userID");
String msg = request.getParameter("message");
String place = request.getParameter("place");

    ArrayList<String> token = new ArrayList<String>();    //token값을 ArrayList에 저장
    String MESSAGE_ID = String.valueOf(Math.random() % 100 + 1);    //메시지 고유 ID
    boolean SHOW_ON_IDLE = false;    //옙 활성화 상태일때 보여줄것인지
    int LIVE_TIME = 1;    //옙 비활성화 상태일때 FCM가 메시지를 유효화하는 시간
    int RETRY = 2;    //메시지 전송실패시 재시도 횟수

    String simpleApiKey = "";
    String gcmURL = "https://android.googleapis.com/fcm/send";    
    
    Connection conn = null; 
    Statement stmt = null; 
    Statement stmt1 = null; 
    ResultSet rs = null;
        
    
    if(msg==null || msg.equals("")){
        msg=User_mail;
    }
    
    msg = new String(msg.getBytes("UTF-8"), "UTF-8");   //메시지 한글깨짐 처리
    
String DB_URL = "jdbc:mysql://rds-mysql-s0woo.ap-northeast-2.rds.amazonaws.com:3306/UserConnect?useUnicode=true&characterEncoding=utf8";
String DB_USER = "s0woo";
String DB_PASSWORD = "";

    try {
Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        stmt = conn.createStatement();
stmt1 = conn.createStatement();

String sql1 = "update ConnInfo set place_emer = '" + place + "' where U_mail = '" + User_mail + "'";
stmt1.executeUpdate(sql1); 

        String sql = "select S_UUID from ConnInfo where U_mail = \'" + User_mail + "\'";
        rs = stmt.executeQuery(sql);



        
        //모든 등록ID를 리스트로 묶음
        while(rs.next()){
            token.add(rs.getString("S_UUID"));
        }
//rs.close();
//rs1.close();
//rs2.close();
        conn.close();
//stmt.close();
        
        Sender sender = new Sender(simpleApiKey);
        Message message = new Message.Builder()
        .collapseKey(MESSAGE_ID)
        .delayWhileIdle(SHOW_ON_IDLE)
        .timeToLive(LIVE_TIME)
        .addData("message",msg)
        .build();
        MulticastResult result1 = sender.send(message,token,RETRY);
        if (result1 != null) {
            List<Result> resultList = result1.getResults();
            for (Result result : resultList) {
                //System.out.println(result.getErrorCodeName()); 
            }
        }
out.println("success");

    }catch (Exception e) {
        e.printStackTrace();
out.println(e + "fail");
    } finally { 

}
%>

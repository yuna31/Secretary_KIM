<%@page import="java.net.URLEncoder"%>
<%@ page language="java" contentType="text/html"
    pageEncoding="UTF-8"%>
 
<%@ page import="java.sql.*"%>
<%@ page import="java.util.*"%>
<%@page import="org.json.simple.*"%>

<%
request.setCharacterEncoding("UTF-8");
String Secretary_mail = request.getParameter("secretaryID");
String User_mail = request.getParameter("userID");
//out.println(Secretary_mail);
JSONArray jArray = new JSONArray(); // 배열
JSONObject jObject = new JSONObject(); // JSON내용을 담을 객체.

    Connection conn = null; 
    Statement stmt = null; 
    ResultSet rs = null;

String DB_URL = "jdbc:mysql://rds-mysql-s0woo.ap-northeast-2.rds.amazonaws.com:3306/UserConnect";
String DB_USER = "s0woo";
String DB_PASSWORD = "";

    try {
Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        stmt = conn.createStatement();

        String sql = "select new_fileName from ConnInfo where S_mail = \'" + Secretary_mail + "\' AND U_mail = '" + User_mail + "'";
        rs = stmt.executeQuery(sql);

while(rs.next()) {
jObject.put("fileName" , rs.getString("new_fileName"));
jArray.add(jObject);
}
//jsonMain.put("getFilename", jArray);

out.println(jObject);
//out.print(jsonMain);

//out.println("success");

    }catch (Exception e) {
        e.printStackTrace();
out.println("실패");
    }
%>


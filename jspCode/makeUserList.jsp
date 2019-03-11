<%@page import="java.net.URLEncoder"%>
<%@ page language="java" contentType="text/html"
    pageEncoding="UTF-8"%>
 
<%@ page import="java.sql.*"%>
<%@ page import="java.util.*"%>
<%@page import="org.json.simple.*"%>
<%@page import="org.json.simple.JSONObject"%>
<%@page import="org.json.simple.JSONArray"%>


<%
request.setCharacterEncoding("UTF-8");
String Secretary_mail = request.getParameter("secretaryID");
//out.println(Secretary_mail);
JSONArray jArray = new JSONArray(); // 배열
JSONObject jObject = new JSONObject(); // JSON내용을 담을 객체.
JSONObject jsonMain = new JSONObject(); 

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

String sql = "select U_name, new_fileName,U_mail from ConnInfo where S_mail = '" + Secretary_mail + "'";
        rs = stmt.executeQuery(sql);

while(rs.next()) {
 jObject.put("Uname" , rs.getString("U_name"));
 jObject.put("Umail" , rs.getString("U_mail"));
 jObject.put("newfileName" , rs.getString("new_fileName"));
 out.println(jObject);

 //jArray.add(i, jObject);
 //String jsonStr = jArray.toJSONString();
 //out.println(jsonStr);

}

//jsonMain.put("putFileName", jArray);
//out.println("object : ");
//out.println(jObject);
//String jsonStr = jArray.toJSONString();
//out.println(jsonStr);

//out.println(jsonMain);

out.println("success");

    }catch (Exception e) {
        e.printStackTrace();
out.println("fail");
    }
%>


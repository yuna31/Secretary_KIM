<%@ page language="java" import="java.sql.*" %>
<% request.setCharacterEncoding("utf-8"); %>
<%
String U_mail= request.getParameter("userID");
String S_mail = request.getParameter("secretaryID");
String S_UUID = request.getParameter("secretaryUUID");

String DB_URL = "jdbc:mysql://rds-mysql-s0woo.ap-northeast-2.rds.amazonaws.com:3306/UserConnect";
String DB_USER = "s0woo";
String DB_PASSWORD = "";

Connection con = null;
            
try {
Class.forName("com.mysql.jdbc.Driver");

con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
	Statement st = con.createStatement();
	String sql = "insert ignore into ConnInfo values (NULL, '" + U_mail + 
                    "','" + S_mail + 
                    "','" + S_UUID + 
                    "', NULL, NULL, NULL, NULL)";
            st.executeUpdate(sql);
            out.println("success"); 
            
        } catch (Exception e) {       
            out.println("DB 등록 실패");
        }    
%>

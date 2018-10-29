<%@ page language="java" import="java.sql.*" %>
<%

 String DB_URL = "jdbc:mysql://rds-mysql-s0woo.ap-northeast-2.rds.amazonaws.com:3306/UserConnect";
 String DB_USER = "s0woo";
 String DB_PASSWORD= "";    

 Connection con = null;
 PreparedStatement  pstmt   = null;
 ResultSet rs = null;
 String sql=null;

 try
 {
  Class.forName("com.mysql.jdbc.Driver");
  con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);    
 }
 catch(SQLException e){
  out.println(e);
 }
%>

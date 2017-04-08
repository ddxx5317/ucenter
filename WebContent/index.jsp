<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  
  <body>
    <a href="emps">List All Employees</a>
    
    <br><br> 
	<a href="i18n">I18N PAGE</a>
	
	<br><br> 
	<form action="testFileUpload" method="POST" enctype="multipart/form-data">
		File: <input type="file" name="file"/><br>
		Desc: <input type="text" name="desc"/>
		<input type="submit" value="Submit"/>
	</form>
	
	<a href="login/redirectAuthPage?oauth_provider=QQOAuthProvider">QQ登陆</a><br>
	<a href="login/redirectAuthPage?oauth_provider=WeChatOAuthProvider">微信登陆</a><br>
	<a href="login/redirectAuthPage?oauth_provider=SinaWeiboOAuthProvider">新浪微博登陆</a>
  </body>
</html>

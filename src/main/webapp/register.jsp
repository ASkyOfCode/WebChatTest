<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--<% String path = request.getContextPath();%>--%>
<!DOCTYPE html>
<html>
<head>
  <title>WebChat | 注册</title>
</head>
<body>

<h1>WebChat</h1>
  <form id="login-form2" action="/user/register" method="post">
      <input type="text" id="username" name="uname" placeholder="请输入用户名" >
      <input type="text" id="uemail" name="uemail" placeholder="请输入邮箱">
      <input type="text" id="sex" name="sex" placeholder="请输入性别">
      <input type="text" id="age" name="age" placeholder="请输入年龄">
      <input type="password" id="password" name="upassword" placeholder="请输入密码">
      <input type="submit" id="submit" value="Register" >
  </form>
</body>
</html>
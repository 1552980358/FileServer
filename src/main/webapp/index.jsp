<%--
  Created by IntelliJ IDEA.
  User: 1552980358
  Date: 2020/6/24
  Time: 8:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<html>
<head>
    <title>信息</title>
</head>
<body>
<%
    out.println("<p>服务器系统：" + System.getProperty("os.name") + "</p>");
    String remote = request.getHeader("x-forwarded-for");
    if (remote == null) {
        remote = request.getRemoteAddr();
    }
    out.println("<p>客户IP地址端口：" + remote + "</p>");
    out.println("<p>客户UA：" + request.getHeader("user-agent") + "</p>");
%>
</body>
</html>

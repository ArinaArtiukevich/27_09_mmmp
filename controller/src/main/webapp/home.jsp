<%--
Created by IntelliJ IDEA.
User: arina
Date: 10/1/21
Time: 13:49
To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
<h2>Home!</h2>
<hr/>
<!-- NAGATION -->
<a href="/main">[GET] go to main.jsp</a>
<p>Message from server (requestScope.serverMessage) : ${requestScope.serverMessage}</p>
<p>Message from server (requestScope.users) : ${requestScope.users}</p>
<p>Message from server (requestScope.users[0].login) : ${requestScope.users[0].login}</p>
<p>Message from server (requestScope.users[1].login) : ${requestScope.users[1].login}</p>
<p>Message from server (requestScope.users[2].login) : ${requestScope.users[2].login}</p>
</body>
</html>
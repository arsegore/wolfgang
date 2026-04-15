<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:include page="include/header.jsp">
  <jsp:param name="title" value="Se connecter"/>
</jsp:include>
<div class="container">
  <%@ include file="include/flash.jsp"%>

  <h1>Bienvenue sur Wolfgang</h1>
  <a href="/wolfgang/logout">Déconnexion</a>
</div>

<%@include file="include/footer.jsp"%>
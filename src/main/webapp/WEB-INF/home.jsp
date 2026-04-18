<%@ page import="wolfgang.models.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:include page="include/header.jsp">
    <jsp:param name="title" value="Se connecter"/>
</jsp:include>
<div class="container">
    <%@ include file="include/flash.jsp"%>

    <h1>Bienvenue sur Wolfgang</h1>

    <% if (session.getAttribute("user") != null) { %>
        <p>Connecté en tant que <%= ((User) session.getAttribute("user")).getUsername() %></p>
        <a href="${pageContext.request.contextPath}/logout">Déconnexion</a>
    <% } else { %>
        <a href="${pageContext.request.contextPath}/login">Connexion</a>
    <% } %>
</div>

<%@include file="include/footer.jsp"%>
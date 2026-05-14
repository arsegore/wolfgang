<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:include page="include/header.jsp">
    <jsp:param name="title" value="Accueil — Wolfgang"/>
    <jsp:param name="activePage" value="home"/>
</jsp:include>

<div class="container py-5">
    <%@ include file="include/flash.jsp"%>
    <h1>Bienvenue sur Wolfgang</h1>
</div>

<%@include file="include/footer.jsp"%>

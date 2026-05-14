<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:include page="include/header.jsp">
    <jsp:param name="title" value="Nouvelle composition — Wolfgang"/>
</jsp:include>

<div class="container py-5">
    <%@ include file="include/flash.jsp"%>
    <h1>Bienvenue sur la page de la composition</h1>
</div>

<div>
    <ul>
        <li>Titre = ${composition.title}</li>
        <li>tempo = ${composition.tempo}</li>
        <li>acces = ${composition.accessType}</li>
    </ul>
</div>

<%@include file="include/footer.jsp"%>
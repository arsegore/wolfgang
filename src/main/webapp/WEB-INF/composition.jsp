<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>


<jsp:include page="include/header.jsp">
    <jsp:param name="title" value="Nouvelle composition — Wolfgang"/>
</jsp:include>

<div class="container py-5">
    <%@ include file="include/flash.jsp"%>
    <h1>Bienvenue sur la page de la composition</h1>
</div>

<div>
    <ul>
        <li>identifiant : ${composition.id}</li>
        <li>Titre : ${composition.title}</li>
        <li>tempo : ${composition.tempo}</li>
        <li>acces : ${composition.accessType}</li>
        <li>date de création : ${composition.createdAt}</li>
        <li>dernière mise à jour : ${composition.updatedAt}</li>
        <li>propriétaire : ${composition.owner.username}</li>
        <li>
            <select class="form-select" name="accessType" id="accessType">
                <c:forEach var="track" items="${composition.tracks}">
                    <option value="${track.name}">${track.name}</option>
                </c:forEach>
            </select>
        </li>
        <li>
            collaborateurs :
            <ul>
            <c:forEach var="entry" items="${composition.members}">
                ${entry.key.username}
            </c:forEach>
            </ul>
        </li>
    </ul>
</div>

<%@include file="include/footer.jsp"%>
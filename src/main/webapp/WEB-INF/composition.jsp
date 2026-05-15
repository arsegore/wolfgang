<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>


<jsp:include page="include/header.jsp">
    <jsp:param name="title" value="${composition.title} — Wolfgang"/>
</jsp:include>

<div class="container py-5">
    <%@ include file="include/flash.jsp"%>
    <h1>${composition.title} (${composition.id})</h1>
</div>

<div class="card-body p-4">
    <h4>Description</h4>
    <p>${composition.description}</p>
</div>


<div>
    <ul>
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
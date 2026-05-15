<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="include/header.jsp">
    <jsp:param name="title" value="Profil — Wolfgang"/>
</jsp:include>

<div class="container py-5">
    <%@ include file="include/flash.jsp"%>

    <div class="row mb-5">
        <div class="col-12">
            <h1 class="h3 mb-4">Profil</h1>
        </div>

        <c:choose>
            <c:when test="${empty sessionScope.user}">
                <div class="col-12">
                    <div class="alert alert-info border shadow-sm">
                        <i class="bi bi-info-circle me-2"></i>
                        Connectez-vous pour accéder à votre profil.
                        <a href="${pageContext.request.contextPath}/login" class="alert-link ms-2">Se connecter</a>
                    </div>
                </div>
            </c:when>

            <c:otherwise>
                <c:forEach var="user" items="${user}">
                    <div class="col-md-4 mb-4">
                        <div class="card shadow-sm h-100">
                            <div class="card-body p-4">
                                <h5 class="card-title">Nom : ${user.username}</h5>
                                <p class="text-muted small">Adresse mail : ${user.email}</p>
                                <p class="text-muted small">Compte créé le : ${user.createdAt}</p>
                                <p class="text-muted small">Liste d'amis : </p>
                                <p class="text-muted small">Compositions : </p>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@include file="include/footer.jsp"%>
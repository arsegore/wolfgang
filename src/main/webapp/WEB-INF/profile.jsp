<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="include/header.jsp">
    <jsp:param name="title" value="${user.username} — Wolfgang"/>
</jsp:include>

<div class="container py-5">
    <%@ include file="include/flash.jsp"%>

    <div class="row mb-5">
        <div class="col-12">
            <h1 class="h3 mb-4">
                <c:choose>
                    <c:when test="${isOwnProfile}">
                        Mon profil
                    </c:when>
                    <c:otherwise>
                        Profil de ${user.username}
                        <a class="btn btn-outline-dark btn-sm mt-2" href="${pageContext.request.contextPath}/friends/add?id=${user.id}">
                            <i class="bi bi-person-plus me-1"></i>Ajouter en ami
                        </a>
                    </c:otherwise>
                </c:choose>
            </h1>
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
                <div class="col-md-4 mb-4">
                    <div class="card shadow-sm h-100">
                        <div class="card-body p-4">
                            <h5 class="card-title">${user.username}</h5>
                            <br>

                            <c:if test="${isOwnProfile}">
                                <p class="text-muted small">${user.email}</p>
                            </c:if>

                            <p class="text-muted small">Compte créé le : ${user.createdAt}</p>

                            <c:if test="${isOwnProfile}">
                                <p class="text-muted small">
                                    <a href="${pageContext.request.contextPath}/friends">Liste d'amis</a>
                                </p>

                                <p class="text-muted small">
                                    <a href="${pageContext.request.contextPath}/composition/list">Mes compositions</a>
                                </p>
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@include file="include/footer.jsp"%>
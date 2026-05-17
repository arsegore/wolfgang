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
        <c:choose>
            <c:when test="${!isOwnProfile}">
                <c:choose>
                    <%-- Déjà amis --%>
                    <c:when test="${isFriend}">
                        <form method="post" class="d-inline-block" action="${pageContext.request.contextPath}/friends/action">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="id" value="${user.id}">
                            <button class="btn btn-outline-success btn-sm mt-2" disabled>
                                <i class="bi bi-check2-circle me-1"></i>Déjà amis
                            </button>
                            <button class="btn btn-outline-danger btn-sm mt-2">
                                <i class="bi bi-trash me-1"></i>Supprimer
                            </button>
                        </form>
                    </c:when>

                    <%-- Demande envoyée --%>
                    <c:when test="${sentRequest}">
                        <form method="post" class="d-inline-block" action="${pageContext.request.contextPath}/friends/action">
                            <input type="hidden" name="action" value="cancel">
                            <input type="hidden" name="id" value="${user.id}">
                            <button class="btn btn-outline-secondary btn-sm mt-2" disabled>
                                <i class="bi bi-hourglass-split me-1"></i>Demande envoyée
                            </button>
                            <button class="btn btn-outline-danger btn-sm mt-2">
                                <i class="bi bi-person-dash me-1"></i>Annuler
                            </button>
                        </form>
                    </c:when>

                    <%-- Demande reçue --%>
                    <c:when test="${receivedRequest}">
                        <div class="d-flex gap-1 mt-2">
                            <form method="post" class="d-inline-block" action="${pageContext.request.contextPath}/friends/action">
                                <input type="hidden" name="action" value="accept">
                                <input type="hidden" name="id" value="${user.id}">
                                <button class="btn btn-outline-success btn-sm mt-2">
                                    <i class="bi bi-check-circle me-1"></i>Accepter
                                </button>
                            </form>

                            <form method="post" class="d-inline-block" action="${pageContext.request.contextPath}/friends/action">
                                <input type="hidden" name="action" value="refuse">
                                <input type="hidden" name="id" value="${user.id}">
                                <button class="btn btn-outline-danger btn-sm mt-2">
                                    <i class="bi bi-x-circle me-1"></i>Refuser
                                </button>
                            </form>
                        </div>
                    </c:when>

                    <%-- Aucun lien --%>
                    <c:otherwise>
                        <form method="post" class="d-inline-block" action="${pageContext.request.contextPath}/friends/action">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="id" value="${user.id}">
                            <button class="btn btn-outline-primary btn-sm">
                                <i class="bi bi-person-plus me-1"></i>Ajouter en ami
                            </button>
                        </form>
                    </c:otherwise>
                </c:choose>
            </c:when>
        </c:choose>
    </div>
</div>

<%@include file="include/footer.jsp"%>
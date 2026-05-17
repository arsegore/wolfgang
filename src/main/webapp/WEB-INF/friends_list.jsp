<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="include/header.jsp">
    <jsp:param name="title" value="Liste d'amis — Wolfgang"/>
    <jsp:param name="activePage" value="friends"/>
</jsp:include>

<div class="container py-5">
    <%@ include file="include/flash.jsp"%>

    <div class="row mb-5">
        <div class="col-12">
            <h1 class="h3 mb-4">Liste d'amis</h1>
        </div>

        <c:choose>
            <%-- Pas connecté --%>
            <c:when test="${empty sessionScope.user}">
                <div class="col-12">
                    <div class="alert alert-info border shadow-sm">
                        <i class="bi bi-info-circle me-2"></i>
                        Connectez-vous pour accéder à votre liste d'amis.
                        <a href="${pageContext.request.contextPath}/login" class="alert-link ms-2">Se connecter</a>
                    </div>
                </div>
            </c:when>
            <%-- Connecté --%>
            <c:otherwise>
                <div class="col-12">
                    <h2 class="h4 mb-4">Demandes envoyées</h2>
                </div>
                <c:choose>
                    <%-- Pas de demandes envoyées --%>
                    <c:when test="${empty mySentRequests}">
                        <div class="col-12">
                            <div class="alert alert-light border shadow-sm">
                                Vous n'avez envoyé aucune demande d'ami.
                            </div>
                        </div>
                    </c:when>
                    <%-- Demandes envoyées --%>
                    <c:otherwise>
                        <c:forEach var="request" items="${mySentRequests}">
                            <div class="col-md-4 mb-4">
                                <div class="card shadow-sm h-100">
                                    <div class="card-body p-4">
                                        <h5 class="card-title">${request.friend.username}</h5>
                                        <p class="text-muted small">Envoyée le : ${request.friendsSince}</p>
                                        <a class="btn btn-outline-primary btn-sm mt-2" href="${pageContext.request.contextPath}/profile?id=${request.friend.id}">
                                            <i class="bi bi-person me-1"></i>Profil
                                        </a>
                                        <form method="post" class="d-inline-block" action="${pageContext.request.contextPath}/friends/action">
                                            <input type="hidden" name="action" value="cancel">
                                            <input type="hidden" name="id" value="${request.friend.id}">
                                            <button class="btn btn-outline-danger btn-sm mt-2">
                                                <i class="bi bi-person-dash me-1"></i>Annuler
                                            </button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>

                <div class="col-12">
                    <h2 class="h4 mb-4">Demandes reçues</h2>
                </div>
                <c:choose>
                    <%-- Pas de demandes reçues --%>
                    <c:when test="${empty myReceivedRequests}">
                        <div class="col-12">
                            <div class="alert alert-light border shadow-sm">
                                Vous n'avez reçu aucune demande d'ami.
                            </div>
                        </div>
                    </c:when>
                    <%-- Demandes reçues --%>
                    <c:otherwise>
                        <c:forEach var="request" items="${myReceivedRequests}">
                            <div class="col-md-4 mb-4">
                                <div class="card shadow-sm h-100">
                                    <div class="card-body p-4">
                                        <h5 class="card-title">${request.friend.username}</h5>
                                        <p class="text-muted small">Reçue le : ${request.friendsSince}</p>
                                        <a class="btn btn-outline-primary btn-sm mt-2" href="${pageContext.request.contextPath}/profile?id=${request.friend.id}">
                                            <i class="bi bi-person me-1"></i>Profil
                                        </a>
                                        <form method="post" class="d-inline-block" action="${pageContext.request.contextPath}/friends/action">
                                            <input type="hidden" name="action" value="accept">
                                            <input type="hidden" name="id" value="${request.friend.id}">
                                            <button class="btn btn-outline-success btn-sm mt-2">
                                                <i class="bi bi-check-circle me-1"></i>Accepter
                                            </button>
                                        </form>
                                        <form method="post" class="d-inline-block" action="${pageContext.request.contextPath}/friends/action">
                                            <input type="hidden" name="action" value="refuse">
                                            <input type="hidden" name="id" value="${request.friend.id}">
                                            <button class="btn btn-outline-danger btn-sm mt-2">
                                                <i class="bi bi-x-circle me-1"></i>Refuser
                                            </button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>

                <div class="col-12">
                    <h2 class="h4 mb-4">Amis</h2>
                </div>
                <c:choose>
                    <%-- Pas d'amis --%>
                    <c:when test="${empty myFriends}">
                        <div class="col-12">
                            <div class="alert alert-light border shadow-sm">
                                Vous n'avez pas encore d'amis.
                            </div>
                        </div>
                    </c:when>
                    <%-- Amis --%>
                    <c:otherwise>
                        <c:forEach var="friendship" items="${myFriends}">
                            <div class="col-md-4 mb-4">
                                <div class="card shadow-sm h-100">
                                    <div class="card-body p-4">
                                        <h5 class="card-title">${friendship.friend.username}</h5>
                                        <p class="text-muted small">Amis depuis : ${friendship.friendsSince}</p>
                                        <a class="btn btn-outline-primary btn-sm mt-2" href="${pageContext.request.contextPath}/profile?id=${friendship.friend.id}">
                                            <i class="bi bi-person me-1"></i>Profil
                                        </a>
                                        <form method="post" class="d-inline-block" action="${pageContext.request.contextPath}/friends/action">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="id" value="${friendship.friend.id}">
                                            <button class="btn btn-outline-danger btn-sm mt-2">
                                                <i class="bi bi-trash me-1"></i>Supprimer
                                            </button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@include file="include/footer.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="include/header.jsp">
    <jsp:param name="title" value="Tableau de bord — Wolfgang"/>
    <jsp:param name="activePage" value="compositions"/>
</jsp:include>

<div class="container py-5">
    <%@ include file="include/flash.jsp"%>

    <%-- mes compositions (owner) --%>
    <div class="row mb-5">
        <div class="col-12 d-flex justify-content-between align-items-center mb-4">
            <h1 class="h3 mb-0">Mes compositions</h1>
            <c:if test="${not empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/composition/create" class="btn btn-primary btn-sm">
                    <i class="bi bi-plus-circle me-1"></i> Nouvelle
                </a>
            </c:if>
        </div>

        <c:choose>
            <c:when test="${empty sessionScope.user}">
                <div class="col-12">
                    <div class="alert alert-info border shadow-sm">
                        <i class="bi bi-info-circle me-2"></i>
                        Connectez-vous pour créer et retrouver vos compositions.
                        <a href="${pageContext.request.contextPath}/login" class="alert-link ms-2">Se connecter</a>
                    </div>
                </div>
            </c:when>
            <c:when test="${empty myCompositions}">
                <div class="col-12">
                    <div class="alert alert-light border shadow-sm">
                        Vous n'avez pas encore créé de composition.
                        <a href="${pageContext.request.contextPath}/composition/create">Commencer une nouvelle œuvre ?</a>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <c:forEach var="comp" items="${myCompositions}">
                    <div class="col-md-4 mb-4">
                        <div class="card shadow-sm h-100">
                            <div class="card-body p-4">
                                <h5 class="card-title">${comp.title}</h5>
                                <p class="text-muted small">Tempo : ${comp.tempo} BPM</p>
                                <p class="card-text text-truncate">${comp.description}</p>
                                <span class="badge bg-light text-dark border">${comp.accessType}</span>
                            </div>
                            <div class="card-footer bg-white border-0 p-3">
                                <a href="${pageContext.request.contextPath}/composition/view?id=${comp.id}"
                                   class="btn btn-primary w-100">Ouvrir</a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>

    <%-- mes collaborations (member) --%>
    <c:if test="${not empty memberCompositions}">
        <hr class="my-4">
        <div class="row mb-5">
            <div class="col-12 mb-4">
                <h2 class="h4 mb-0">Mes collaborations</h2>
            </div>
            <c:forEach var="entry" items="${memberCompositions}">
                <div class="col-md-4 mb-4">
                    <div class="card shadow-sm h-100 border-start border-3 ${entry.value == 'editor' ? 'border-primary' : 'border-secondary'}">
                        <div class="card-body p-4">
                            <h5 class="card-title">${entry.key.title}</h5>
                            <p class="small text-muted mb-2">
                                Par <strong>
                                    <a href="${pageContext.request.contextPath}/profile?id=${entry.key.owner.id}">
                                        ${entry.key.owner.username}
                                    </a>
                                </strong> · ${entry.key.tempo} BPM
                            </p>
                            <span class="badge ${entry.value == 'editor' ? 'bg-primary' : 'bg-secondary'}">
                                <c:choose>
                                    <c:when test="${entry.value == 'editor'}"><i class="bi bi-pencil me-1"></i>Éditeur</c:when>
                                    <c:otherwise><i class="bi bi-eye me-1"></i>Lecture seule</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div class="card-footer bg-white border-0 p-3">
                            <a href="${pageContext.request.contextPath}/composition/view?id=${entry.key.id}"
                               class="btn ${entry.value == 'editor' ? 'btn-primary' : 'btn-outline-secondary'} w-100">Ouvrir</a>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:if>

    <hr class="my-4">

    <%-- compositions publiques --%>
    <div class="row">
        <div class="col-12 mb-4">
            <h2 class="h4 mb-0">Compositions publiques</h2>
            <p class="text-muted small mb-0">En lecture seule, sauf si l'auteur a activé l'édition libre.</p>
        </div>

        <c:if test="${empty publicCompositions}">
            <p class="text-center text-muted">Aucune composition publique disponible pour le moment.</p>
        </c:if>

        <c:forEach var="pub" items="${publicCompositions}">
            <div class="col-md-4 mb-4">
                <div class="card shadow-sm border-0 bg-light h-100">
                    <div class="card-body p-4">
                        <h5 class="card-title">${pub.title}</h5>
                        <p class="small text-muted mb-2">
                            Par <strong>
                                <a href="${pageContext.request.contextPath}/profile?id=${pub.owner.id}">
                                    ${pub.owner.username}
                                </a>
                            </strong> · ${pub.tempo} BPM
                        </p>
                        <c:choose>
                            <c:when test="${pub.publicEditable}">
                                <span class="badge bg-success"><i class="bi bi-pencil me-1"></i>Édition libre</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-secondary"><i class="bi bi-eye me-1"></i>Lecture seule</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="card-footer bg-transparent border-0 p-3">
                        <a href="${pageContext.request.contextPath}/composition/view?id=${pub.id}"
                           class="btn ${pub.publicEditable ? 'btn-outline-primary' : 'btn-outline-secondary'} w-100">
                            ${pub.publicEditable ? 'Ouvrir' : 'Consulter'}
                        </a>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<%@include file="include/footer.jsp"%>

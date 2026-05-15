<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="include/header.jsp">
    <jsp:param name="title" value="${composition.title} — Wolfgang"/>
</jsp:include>

<div class="container py-5">
    <%@ include file="include/flash.jsp"%>

    <div class="d-flex align-items-center justify-content-between mb-4 border-bottom pb-3">
        <div>
            <h1 class="display-5 mb-0">${composition.title}</h1>
            <p class="text-muted">Référence : #${composition.id}</p>
        </div>
        <div class="text-end">
            <span class="badge ${composition.accessType == 'public' ? 'bg-success' : 'bg-warning'} fs-6">
                Accès : ${composition.accessType}
            </span>
        </div>
    </div>

    <div class="row">
        <div class="col-lg-8">
            <div class="card shadow-sm mb-4">
                <div class="card-body p-4">
                    <h4 class="card-title mb-3 text-primary">Description</h4>
                    <p class="card-text lead">
                        <c:choose>
                            <c:when test="${not empty composition.description}">
                                ${composition.description}
                            </c:when>
                            <c:otherwise>
                                <span class="text-muted italic">Aucune description fournie.</span>
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>
            </div>

            <div class="card shadow-sm mb-4">
                <div class="card-header bg-white py-3">
                    <h5 class="mb-0 text-primary">Pistes de l'œuvre</h5>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${empty composition.tracks}">
                            <p class="text-muted">Aucune piste enregistrée pour cette composition.</p>
                        </c:when>
                        <c:otherwise>
                            <ul class="list-group list-group-flush">
                                <c:forEach var="track" items="${composition.tracks}">
                                    <li class="list-group-item d-flex justify-content-between align-items-center">
                                        <span><i class="bi bi-music-note-beamed me-2"></i> ${track.name}</span>
                                        <span class="badge bg-light text-dark border">Instrument #${track.instrumentId}</span>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <div class="col-lg-4">
            <div class="card shadow-sm mb-4 border-primary">
                <div class="card-body">
                    <h5 class="card-title border-bottom pb-2 mb-3">Informations</h5>
                    <ul class="list-unstyled mb-0">
                        <li class="mb-2">
                            <strong>Propriétaire :</strong> <span class="text-primary">${composition.owner.username}</span>
                        </li>
                        <li class="mb-2">
                            <strong>Tempo :</strong> <span class="badge bg-info text-dark">${composition.tempo} BPM</span>
                        </li>
                        <li class="mb-2">
                            <small class="text-muted d-block">Créé le : ${composition.createdAt}</small>
                        </li>
                        <li class="mb-0">
                            <small class="text-muted d-block">Mise à jour : ${composition.updatedAt}</small>
                        </li>
                    </ul>
                </div>
            </div>

            <div class="card shadow-sm">
                <div class="card-header bg-light">
                    <h5 class="mb-0">Collaborateurs</h5>
                </div>
                <div class="card-body p-0">
                    <ul class="list-group list-group-flush">
                        <c:choose>
                            <c:when test="${empty composition.members}">
                                <li class="list-group-item text-muted small">Aucun collaborateur externe.</li>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="entry" items="${composition.members}">
                                    <li class="list-group-item">
                                        <div class="d-flex align-items-center">
                                            <div class="bg-primary text-white rounded-circle p-2 me-3" style="width: 35px; height: 35px; display: flex; align-items:center; justify-content:center;">
                                                ${entry.key.username.substring(0,1).toUpperCase()}
                                            </div>
                                            <div>
                                                <h6 class="mb-0">${entry.key.username}</h6>
                                                <small class="text-muted">${entry.value}</small>
                                            </div>
                                        </div>
                                    </li>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </ul>
                </div>
            </div>

            <div class="mt-4">
                <a href="${pageContext.request.contextPath}/composition/display" class="btn btn-outline-secondary w-100">
                    <i class="bi bi-arrow-left"></i> Retour à la liste
                </a>
            </div>
        </div>
    </div>
</div>

<%@include file="include/footer.jsp"%>
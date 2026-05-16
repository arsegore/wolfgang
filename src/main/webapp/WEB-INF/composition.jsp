<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="include/header.jsp">
    <jsp:param name="title" value="${composition.title} — Wolfgang"/>
</jsp:include>

<div class="container-fluid py-3 px-4">
    <%@ include file="include/flash.jsp"%>

    <div class="d-flex align-items-center justify-content-between mb-3">
        <div>
            <h2 class="mb-0 fw-bold">${composition.title}</h2>
            <small class="text-muted">${composition.tempo} BPM; #${composition.id}</small>
        </div>
        <div class="d-flex gap-2 align-items-center">
            <span class="badge ${composition.accessType == 'public' ? 'bg-success' : 'bg-warning text-dark'}">
                ${composition.accessType}
            </span>
            <a href="${pageContext.request.contextPath}/composition/list" class="btn btn-outline-secondary btn-sm">
                <i class="bi bi-arrow-left"></i> Retour
            </a>
        </div>
    </div>

    <div class="editor-wrapper mb-4">

        <div id="track-tabs" class="track-tabs-bar">
            <span class="text-muted small px-2">Chargement…</span>
        </div>

        <div class="canvas-row">
            <canvas id="midi-canvas" height="440"></canvas>
            <input type="range" id="scroll-y" orient="vertical" min="0" max="1000" value="0">
        </div>

        <input type="range" id="scroll-x" min="0" max="1000" value="0">

        <div class="editor-toolbar">
            <button id="tool-draw" class="btn btn-sm btn-primary active">
                <i class="bi bi-pencil-fill"></i> Crayon
            </button>
            <button id="tool-erase" class="btn btn-sm btn-outline-secondary">
                <i class="bi bi-eraser-fill"></i> Gomme
            </button>
            <span id="cursor-info"></span>
        </div>
    </div>

    <div class="row">
        <div class="col-md-6">
            <div class="card shadow-sm mb-4">
                <div class="card-body">
                    <h6 class="card-title text-primary mb-3">Informations</h6>
                    <ul class="list-unstyled mb-0 small">
                        <li class="mb-1"><strong>Propriétaire :</strong> ${composition.owner.username}</li>
                        <li class="mb-1"><strong>Tempo :</strong> ${composition.tempo} BPM</li>
                        <li class="mb-1"><strong>Accès :</strong> ${composition.accessType}</li>
                        <c:if test="${not empty composition.description}">
                            <li class="mb-1"><strong>Description :</strong> ${composition.description}</li>
                        </c:if>
                        <li class="text-muted">Créé le : ${composition.createdAt}</li>
                        <li class="text-muted">Mis à jour : ${composition.updatedAt}</li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="card shadow-sm mb-4">
                <div class="card-header bg-light py-2"><h6 class="mb-0">Collaborateurs</h6></div>
                <div class="card-body p-0">
                    <ul class="list-group list-group-flush">
                        <c:choose>
                            <c:when test="${empty composition.members}">
                                <li class="list-group-item text-muted small">Aucun collaborateur.</li>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="entry" items="${composition.members}">
                                    <li class="list-group-item small">
                                        <strong>${entry.key.username}</strong>
                                        <span class="text-muted ms-2">${entry.value}</span>
                                    </li>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    var COMPOSITION_DATA = {
        id: ${composition.id},
        tempo: ${composition.tempo},
        tracks: ${tracksJson}
    };
</script>
<script src="${pageContext.request.contextPath}/js/editor.js"></script>
<script>
    window.addEventListener('load', function () {
        initEditor(COMPOSITION_DATA.tracks, 4);
    });
</script>

<%@include file="include/footer.jsp"%>

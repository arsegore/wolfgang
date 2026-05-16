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
        <div id="chat-config"
            data-pseudo="${sessionScope.user.username}"
            data-context="${pageContext.request.contextPath}"
            class="d-none">
        </div>

        <div id="chat-container" class="card shadow-sm p-3">
            <h6 class="card-title text-primary mb-2"><i class="bi bi-chat-dots-fill me-1"></i> Discussion de groupe</h6>
            <div id="messages" style="height: 180px; overflow-y: scroll; border: 1px solid #ddd; padding: 10px; margin-bottom: 10px; background: #fafafa; font-size: 0.9rem; border-radius: 4px;">
            </div>
            <div class="input-group input-group-sm">
                <input type="text" id="messageInput" class="form-control" placeholder="Votre message...">
                <button class="btn btn-primary" onclick="send()">Envoyer</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="modal-new-track" tabindex="-1">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header py-2">
                <h6 class="modal-title">Nouvelle piste</h6>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="mb-2">
                    <label class="form-label small mb-1">Nom</label>
                    <input type="text" id="new-track-name" class="form-control form-control-sm" placeholder="Piano, Basse…">
                </div>
                <div class="mb-2">
                    <label class="form-label small mb-1">Instrument</label>
                    <select id="new-track-instrument" class="form-select form-select-sm"></select>
                </div>
                <div class="mb-2">
                    <label class="form-label small mb-1">Couleur</label>
                    <input type="color" id="new-track-color" class="form-control form-control-sm form-control-color w-100" value="#4a9eff">
                </div>
            </div>
            <div class="modal-footer py-2">
                <button type="button" class="btn btn-sm btn-secondary" data-bs-dismiss="modal">Annuler</button>
                <button type="button" id="btn-create-track" class="btn btn-sm btn-primary">Créer</button>
            </div>
        </div>
    </div>
</div>

<script>
    var COMPOSITION_DATA = {
        id: ${composition.id},
        tempo: ${composition.tempo},
        tracks: ${tracksJson},
        instruments: ${instrumentsJson},
        contextPath: '${pageContext.request.contextPath}'
    };
</script>
<script src="${pageContext.request.contextPath}/js/editor.js"></script>
<script src="${pageContext.request.contextPath}/js/Client.js"></script>
<script>
    window.addEventListener('load', function () {
        initEditor(COMPOSITION_DATA.tracks, 4);
    });
</script>

<%@include file="include/footer.jsp"%>

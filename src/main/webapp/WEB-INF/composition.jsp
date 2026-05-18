<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<jsp:include page="include/header.jsp">
    <jsp:param name="title" value="${composition.title} — Wolfgang"/>
</jsp:include>

<div class="container py-5">
    <%@ include file="include/flash.jsp"%>

    <%-- Haut de page --%>
    <div class="d-flex align-items-center justify-content-between mb-4 border-bottom pb-3">

        <%-- Titre --%>
        <div>
            <h1 class="display-5 mb-0">${composition.title}</h1>
            <p class="text-muted">Référence : #${composition.id}</p>
        </div>

        <%-- Actions à droite --%>
        <div class="text-end d-flex gap-2 align-items-center">

            <button type="button" class="btn btn-outline-primary" data-bs-toggle="modal" data-bs-target="#modal-import">
                <i class="bi bi-upload me-1"></i> Importer des données (.txt)
            </button>

            <a href="${pageContext.request.contextPath}/composition/export?id=${composition.id}" class="btn btn-outline-success">
                <i class="bi bi-download me-1"></i> Exporter les données (.txt)
            </a>

            <c:choose>
                <%-- Propriétaire --%>
                <c:when test="${sessionScope.user.id == composition.owner.id}">
                    <form method="post" action="${pageContext.request.contextPath}/composition/view?id=${composition.id}" class="d-inline">
                        <input type="hidden" name="id" value="${composition.id}" />
                        <input type="hidden" name="action" value="updateAccess"/>
                        <input type="hidden" name="accessType" value="${composition.accessType == 'public' ? 'private' : 'public'}" />
                        <button type="submit" class="btn badge ${composition.accessType == 'public' ? 'bg-success' : 'bg-danger'} fs-6 border-0">
                            Accès : ${composition.accessType}
                        </button>
                    </form>
                </c:when>

                <%-- Autres --%>
                <c:otherwise>
                    <span class="badge ${composition.accessType == 'public' ? 'bg-success' : 'bg-danger'} fs-6">
                        Accès : ${composition.accessType}
                    </span>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <%-- Editeur --%>
    <div class="editor-wrapper mb-4">

        <div id="track-tabs" class="track-tabs-bar">
            <span class="text-muted small px-2">Chargement…</span>
        </div>

        <div class="canvas-row">
            <canvas id="midi-canvas" height="440"></canvas>
            <input type="range" id="scroll-y" orient="vertical" min="0" max="1000" value="0">
        </div>

        <input type="range" id="scroll-x" min="0" max="1000" value="0">

        <div class="player-bar">
            <button id="player-play" class="player-btn" title="Lecture / Pause">
                <i class="bi bi-play-fill"></i>
            </button>
            <button id="player-stop" class="player-btn" title="Arrêt">
                <i class="bi bi-stop-fill"></i>
            </button>
            <button id="player-mode" class="player-btn" title="Mode de lecture">
                <i class="bi bi-music-note"></i> Piste active
            </button>
            <span id="player-time" class="player-time">M1 T1</span>
        </div>

        <div class="editor-toolbar">
            <c:if test="${canEdit}">
            <button id="tool-draw" class="btn btn-sm btn-primary active">
                <i class="bi bi-pencil-fill"></i> Crayon
            </button>
            <button id="tool-erase" class="btn btn-sm btn-outline-secondary">
                <i class="bi bi-eraser-fill"></i> Gomme
            </button>
            </c:if>
            <span id="cursor-info"></span>
        </div>
    </div>

    <%-- Partie Basse --%>
    <div class="row">
        <div class="col-lg-8">

            <%-- Description de la composition --%>
            <div class="card shadow-sm mb-4">
                <div class="card-body p-4">

                    <%-- Bouton d affichage de l éditeur de description --%>
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h4 class="card-title mb-3 text-primary">Description</h4>
                        <c:if test="${sessionScope.user.id == composition.owner.id}">
                            <button class="btn btn-outline-primary btn-sm" type="button" onclick="document.getElementById('editDescription').classList.toggle('d-none')">
                                Modifier
                            </button>
                        </c:if>
                    </div>

                    <%-- Affichage de la description --%>
                    <p class="card-text lead">
                        <c:choose>
                            <c:when test="${not empty composition.description}">
                                ${composition.description}
                            </c:when>
                            <c:otherwise>
                                <p class="text-muted">Aucune description fournie.</p>
                            </c:otherwise>
                        </c:choose>
                    </p>

                    <%-- Editeur de description --%>
                    <div id="editDescription" class="d-none mt-3">
                        <form method="post" action="${pageContext.request.contextPath}/composition/view?id=${composition.id}">
                            <input type="hidden" name="id" value="${composition.id}"/>
                            <input type="hidden" name="action" value="updateDescription"/>
                            <textarea class="form-control mb-3" name="description" rows="4">
                               ${composition.description}
                            </textarea>
                            <button type="submit" class="btn btn-primary">Enregistrer</button>
                        </form>
                    </div>
                </div>
            </div>

        <%-- chat --%>
            <div id="chat-config"
                data-pseudo="${sessionScope.user.username}"
                data-context="${pageContext.request.contextPath}"
                class="d-none">
            </div>

            <div id="chat-container" class="card shadow-sm p-3">
                <h6 class="card-title text-primary mb-2"><i class="bi bi-chat-dots-fill me-1"></i> Discussion de groupe</h6>
                <div id="messages" class="wf-chat-messages">
                </div>
                <div class="input-group input-group-sm">
                    <input type="text" id="messageInput" class="form-control" placeholder="Votre message...">
                    <button class="btn btn-primary" onclick="send()">Envoyer</button>
                </div>
            </div>
        </div>

        <%-- Partie Droite --%>
        <div class="col-lg-4">

            <%-- Informations --%>
            <div class="card shadow-sm mb-4 border-primary">
                <div class="card-body">
                    <h5 class="card-title border-bottom pb-2 mb-3">Informations</h5>
                    <ul class="list-unstyled mb-0">
                        <li class="mb-2">
                            <strong>Propriétaire :</strong> <span class="text-primary">
                                <a href="${pageContext.request.contextPath}/profile?id=${composition.owner.id}">
                                    ${composition.owner.username}
                                </a>
                            </span>
                        </li>
                        <li class="mb-2">
                            <div class="d-flex align-items-center">
                                <strong>Tempo : </strong>
                                <c:choose>
                                    <c:when test="${sessionScope.user.id == composition.owner.id}">

                                        <form method="post" action="${pageContext.request.contextPath}/composition/view?id=${composition.id}" class="d-flex align-items-center gap-2">
                                            <input type="hidden" name="action" value="updateTempo"/>
                                            <input type="number" name="tempo" value="${composition.tempo}" min="20" class="form-control form-control-sm" style="width: 120px;"/>

                                            <button type="submit" class="btn btn-success btn-sm">Valider</button>
                                        </form>
                                    </c:when>

                                    <c:otherwise>
                                        <span class="badge bg-info text-dark">${composition.tempo} BPM</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
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

            <%-- édition libre ou pas (toggle) --%>
            <c:if test="${sessionScope.user.id == composition.owner.id && composition.accessType == 'public'}">
                <div class="card shadow-sm mb-4">
                    <div class="card-body d-flex align-items-center justify-content-between gap-3">
                        <div>
                            <h6 class="mb-0">Édition libre</h6>
                            <small class="text-muted">Autoriser tous les visiteurs à modifier</small>
                        </div>
                        <form method="post" action="${pageContext.request.contextPath}/composition/view?id=${composition.id}" class="mb-0">
                            <input type="hidden" name="action" value="updatePublicEditable"/>
                            <input type="hidden" name="publicEditable" value="${composition.publicEditable ? '0' : '1'}"/>
                            <button type="submit" class="btn btn-sm ${composition.publicEditable ? 'btn-success' : 'btn-outline-secondary'}">
                                ${composition.publicEditable ? 'Activé' : 'Désactivé'}
                            </button>
                        </form>
                    </div>
                </div>
            </c:if>

            <%-- collaborateurs --%>
            <div class="card shadow-sm">
                <div class="card-header bg-light">
                    <h5 class="mb-0">Collaborateurs</h5>
                </div>

                <%-- formulaire d'ajt de collaborateur (owner seulement) --%>
                <c:if test="${sessionScope.user.id == composition.owner.id}">
                    <div class="card-body border-bottom">
                        <form method="post" action="${pageContext.request.contextPath}/composition/view?id=${composition.id}" class="d-flex flex-wrap gap-2 align-items-center">
                            <input type="hidden" name="action" value="addMember"/>
                            <input type="text" name="username" class="form-control form-control-sm flex-grow-1"
                                   placeholder="Pseudo du collaborateur" required style="min-width:140px">
                            <div class="btn-group btn-group-sm" role="group">
                                <input type="radio" class="btn-check" name="role" id="role-editor" value="editor" autocomplete="off" checked>
                                <label class="btn btn-outline-primary" for="role-editor">Éditeur</label>
                                <input type="radio" class="btn-check" name="role" id="role-viewer" value="viewer" autocomplete="off">
                                <label class="btn btn-outline-secondary" for="role-viewer">Lecture</label>
                            </div>
                            <button type="submit" class="btn btn-primary btn-sm">Ajouter</button>
                        </form>
                    </div>
                </c:if>

                <%-- liste des collaborateurs --%>
                <div class="card-body p-0">
                    <ul class="list-group list-group-flush">
                        <c:choose>
                            <c:when test="${empty composition.members}">
                                <li class="list-group-item text-muted small px-3 py-2">Aucun collaborateur.</li>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="entry" items="${composition.members}">
                                    <li class="list-group-item d-flex align-items-center justify-content-between px-3 py-2">
                                        <div class="d-flex align-items-center gap-2">
                                            <div class="bg-primary text-white rounded-circle d-flex align-items-center justify-content-center flex-shrink-0"
                                                 style="width:32px;height:32px;font-size:13px">
                                                ${entry.key.username.substring(0,1).toUpperCase()}
                                            </div>
                                            <div>
                                                <div class="fw-semibold small">
                                                    <a href="${pageContext.request.contextPath}/profile?id=${entry.key.id}">
                                                        ${entry.key.username}
                                                    </a>
                                                </div>
                                                <c:choose>
                                                    <c:when test="${entry.key.id == composition.owner.id}">
                                                        <span class="badge bg-warning text-dark py-1">Propriétaire</span>
                                                    </c:when>
                                                    <c:when test="${entry.value == 'editor'}">
                                                        <span class="badge bg-primary py-1">Éditeur</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary py-1">Lecture seule</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                        <c:if test="${sessionScope.user.id == composition.owner.id && entry.key.id != composition.owner.id}">
                                            <form method="post" action="${pageContext.request.contextPath}/composition/view?id=${composition.id}" class="mb-0">
                                                <input type="hidden" name="action" value="removeMember"/>
                                                <input type="hidden" name="userId" value="${entry.key.id}"/>
                                                <button type="submit" class="btn btn-sm btn-outline-danger py-0 px-2">×</button>
                                            </form>
                                        </c:if>
                                    </li>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </ul>
                </div>
            </div>

            <%-- Bouton Retour --%>
            <div class="mt-4">
                <a href="${pageContext.request.contextPath}/composition/list" class="btn btn-outline-secondary w-100">
                    <i class="bi bi-arrow-left"></i> Retour à la liste
                </a>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="modal-import" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title text-primary"><i class="bi bi-file-earmark-arrow-up me-1"></i> Importer des pistes</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <p class="text-muted small">Sélectionnez le fichier <code>.txt</code>. Le script lira son flux de texte brut.</p>
                <div class="mb-3">
                    <label class="form-label small fw-bold">Fichier source</label>
                    <input type="file" id="import-file-input" class="form-control form-control-sm" accept=".txt">
                </div>
            </div>
            <div class="modal-footer py-2">
                <button type="button" class="btn btn-sm btn-secondary" data-bs-dismiss="modal">Annuler</button>
                <button type="button" class="btn btn-sm btn-success" onclick="declencherImportationCours()">Lancer l'import</button>
            </div>
        </div>
    </div>
</div>

<%-- Modal création de piste --%>
<div class="modal fade" id="modal-new-track" tabindex="-1" aria-labelledby="modal-new-track-label" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modal-new-track-label">Nouvelle piste</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fermer"></button>
            </div>
            <div class="modal-body">
                <div class="mb-3">
                    <label for="new-track-name" class="form-label">Nom</label>
                    <input type="text" class="form-control" id="new-track-name" placeholder="Ex : Mélodie, Basse…">
                </div>
                <div class="mb-3">
                    <label for="new-track-instrument" class="form-label">Instrument</label>
                    <select class="form-select" id="new-track-instrument"></select>
                </div>
                <div class="mb-3">
                    <label for="new-track-color" class="form-label">Couleur</label>
                    <input type="color" class="form-control form-control-color" id="new-track-color" value="#4a9eff">
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Annuler</button>
                <button type="button" class="btn btn-primary" id="btn-create-track">Créer la piste</button>
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
        contextPath: '${pageContext.request.contextPath}',
        canEdit: ${canEdit}
    };
</script>
<script src="${pageContext.request.contextPath}/js/editor.js"></script>
<script src="${pageContext.request.contextPath}/js/import.js"></script>
<script src="${pageContext.request.contextPath}/js/chatComposition.js"></script>
<script>

    window.addEventListener('load', function () {
        initEditor(COMPOSITION_DATA.tracks, 4);
    });
</script>


<%@include file="include/footer.jsp"%>
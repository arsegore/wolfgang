<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="wolfgang.models.Composition" %>

<jsp:include page="../include/header.jsp">
    <jsp:param name="title" value="Admin — Modifier une composition — Wolfgang"/>
</jsp:include>

<div class="d-flex">
    <%@ include file="../include/admin_sidebar.jsp" %>

    <div class="flex-grow-1 p-4">
        <%@ include file="../include/flash.jsp" %>

        <% Composition composition = (Composition) request.getAttribute("composition"); %>

        <div class="d-flex align-items-center mb-4 gap-3">
            <a href="${pageContext.request.contextPath}/admin/compositions" class="btn btn-outline-secondary btn-sm">← Retour</a>
            <h1 class="h3 mb-0">Modifier « <%= composition.getTitle() %> »</h1>
        </div>

        <div class="card shadow-sm" style="max-width: 520px;">
            <div class="card-body p-4">
                <form method="post" action="${pageContext.request.contextPath}/admin/compositions/edit">
                    <input type="hidden" name="id" value="<%= composition.getId() %>">

                    <div class="mb-3">
                        <label for="title" class="form-label">Titre</label>
                        <input type="text" class="form-control" id="title" name="title"
                               value="<%= composition.getTitle() %>" required>
                    </div>

                    <div class="mb-3">
                        <label for="description" class="form-label">Description <span class="text-muted small">(optionnel)</span></label>
                        <textarea class="form-control" id="description" name="description" rows="3"><%= composition.getDescription() != null ? composition.getDescription() : "" %></textarea>
                    </div>

                    <div class="mb-3">
                        <label for="tempo" class="form-label">Tempo (BPM)</label>
                        <input type="number" class="form-control" id="tempo" name="tempo"
                               value="<%= composition.getTempo() %>" min="20" max="300" required>
                    </div>

                    <div class="mb-4">
                        <label for="accessType" class="form-label">Accès</label>
                        <select class="form-select" id="accessType" name="accessType">
                            <option value="private" <%= "private".equals(composition.getAccessType()) ? "selected" : "" %>>Privé</option>
                            <option value="link" <%= "link".equals(composition.getAccessType()) ? "selected" : "" %>>Lien</option>
                            <option value="public" <%= "public".equals(composition.getAccessType()) ? "selected" : "" %>>Public</option>
                        </select>
                    </div>

                    <button type="submit" class="btn btn-primary">Enregistrer</button>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="../include/footer.jsp" %>

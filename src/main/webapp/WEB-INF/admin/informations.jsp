<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="wolfgang.models.Information, java.util.List" %>

<jsp:include page="../include/header.jsp">
    <jsp:param name="title" value="Admin — Actualités — Wolfgang"/>
</jsp:include>

<div class="d-flex">
    <%@ include file="../include/admin_sidebar.jsp" %>

    <div class="flex-grow-1 p-4">
        <%@ include file="../include/flash.jsp" %>

        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="h3 mb-0">Actualités</h1>
        </div>

        <%-- Formulaire de création --%>
        <div class="card shadow-sm mb-4">
            <div class="card-header fw-semibold">Nouvelle actualité</div>
            <div class="card-body">
                <form method="post" action="${pageContext.request.contextPath}/admin/informations">
                    <input type="hidden" name="action" value="create">
                    <div class="mb-3">
                        <label for="title" class="form-label">Titre <span class="text-danger">*</span></label>
                        <input type="text" id="title" name="title" class="form-control" required maxlength="255">
                    </div>
                    <div class="mb-3">
                        <label for="description" class="form-label">Contenu</label>
                        <textarea id="description" name="description" class="form-control" rows="4"></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary">Publier</button>
                </form>
            </div>
        </div>

        <%-- Liste des actualités --%>
        <div class="card shadow-sm">
            <div class="table-responsive">
                <table class="table table-hover mb-0">
                    <thead class="table-dark">
                        <tr>
                            <th>#</th>
                            <th>Titre</th>
                            <th>Contenu</th>
                            <th>Publiée le</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% List<Information> informations = (List<Information>) request.getAttribute("informations"); %>
                        <% if (informations.isEmpty()) { %>
                        <tr>
                            <td colspan="5" class="text-center text-muted py-4">Aucune actualité.</td>
                        </tr>
                        <% } %>
                        <% for (Information info : informations) { %>
                        <tr>
                            <td class="text-muted"><%= info.getId() %></td>
                            <td><strong><%= info.getTitle() %></strong></td>
                            <td class="text-muted small" style="max-width: 400px;">
                                <%= info.getDescription() != null && info.getDescription().length() > 100
                                    ? info.getDescription().substring(0, 100) + "…"
                                    : info.getDescription() %>
                            </td>
                            <td class="text-muted small"><%= info.getCreatedAt() != null ? info.getCreatedAt().toLocalDate() : "" %></td>
                            <td>
                                <form method="post" action="${pageContext.request.contextPath}/admin/informations"
                                      class="d-inline"
                                      onsubmit="return confirm('Supprimer « <%= info.getTitle() %> » ?');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="id" value="<%= info.getId() %>">
                                    <button type="submit" class="btn btn-sm btn-outline-danger">Supprimer</button>
                                </form>
                            </td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<%@ include file="../include/footer.jsp" %>

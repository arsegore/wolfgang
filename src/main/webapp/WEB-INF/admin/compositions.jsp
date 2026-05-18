<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="wolfgang.models.Composition, java.util.List" %>

<jsp:include page="../include/header.jsp">
    <jsp:param name="title" value="Admin — Compositions — Wolfgang"/>
</jsp:include>

<div class="d-flex">
    <%@ include file="../include/admin_sidebar.jsp" %>

    <div class="flex-grow-1 p-4">
        <%@ include file="../include/flash.jsp" %>

        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="h3 mb-0">Compositions</h1>
        </div>

        <div class="card shadow-sm">
            <div class="table-responsive">
                <table class="table table-hover mb-0">
                    <thead class="table-dark">
                        <tr>
                            <th>#</th>
                            <th>Titre</th>
                            <th>Propriétaire</th>
                            <th>Tempo</th>
                            <th>Accès</th>
                            <th>Créée le</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% List<Composition> compositions = (List<Composition>) request.getAttribute("compositions"); %>
                        <% if (compositions.isEmpty()) { %>
                        <tr>
                            <td colspan="7" class="text-center text-muted py-4">Aucune composition.</td>
                        </tr>
                        <% } %>
                        <% for (Composition c : compositions) { %>
                        <tr>
                            <td class="text-muted"><%= c.getId() %></td>
                            <td><strong><%= c.getTitle() %></strong></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/profile?id=<%= c.getOwner().getId() %>">
                                    <%= c.getOwner().getUsername() %>
                                </a>
                            </td>
                            <td><%= c.getTempo() %> BPM</td>
                            <td>
                                <%
                                    String accessType = c.getAccessType();
                                    String badgeClass = "public".equals(accessType) ? "bg-success"
                                                      : "link".equals(accessType)   ? "bg-info text-dark"
                                                                                    : "bg-secondary";
                                    String accessLabel = "public".equals(accessType) ? "Public"
                                                       : "link".equals(accessType)   ? "Lien"
                                                                                     : "Privé";
                                %>
                                <span class="badge <%= badgeClass %>"><%= accessLabel %></span>
                            </td>
                            <td class="text-muted small"><%= c.getCreatedAt() != null ? c.getCreatedAt().toLocalDate() : "" %></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/admin/compositions/edit?id=<%= c.getId() %>"
                                   class="btn btn-sm btn-outline-primary me-1">Modifier</a>
                                <form method="post" action="${pageContext.request.contextPath}/admin/compositions"
                                      class="d-inline"
                                      onsubmit="return confirm('Supprimer « <%= c.getTitle() %> » ?');">
                                    <input type="hidden" name="id" value="<%= c.getId() %>">
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

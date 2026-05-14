<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="wolfgang.models.User, java.util.List" %>

<jsp:include page="../include/header.jsp">
    <jsp:param name="title" value="Admin — Utilisateurs — Wolfgang"/>
</jsp:include>

<div class="d-flex">
    <%@ include file="../include/admin_sidebar.jsp" %>

    <div class="flex-grow-1 p-4">
        <%@ include file="../include/flash.jsp" %>

        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="h3 mb-0">Utilisateurs</h1>
        </div>

        <div class="card shadow-sm">
            <div class="table-responsive">
                <table class="table table-hover mb-0">
                    <thead class="table-dark">
                        <tr>
                            <th>#</th>
                            <th>Nom d'utilisateur</th>
                            <th>Email</th>
                            <th>Admin</th>
                            <th>Créé le</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% List<User> users = (List<User>) request.getAttribute("users"); %>
                        <% if (users.isEmpty()) { %>
                        <tr>
                            <td colspan="6" class="text-center text-muted py-4">Aucun utilisateur.</td>
                        </tr>
                        <% } %>
                        <% for (User u : users) { %>
                        <tr>
                            <td class="text-muted"><%= u.getId() %></td>
                            <td><strong><%= u.getUsername() %></strong></td>
                            <td><%= u.getEmail() %></td>
                            <td>
                                <% if (u.isAdmin()) { %>
                                <span class="badge bg-warning text-dark">Admin</span>
                                <% } else { %>
                                <span class="badge bg-secondary">Utilisateur</span>
                                <% } %>
                            </td>
                            <td class="text-muted small"><%= u.getCreatedAt() != null ? u.getCreatedAt().toLocalDate() : "" %></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/admin/users/edit?id=<%= u.getId() %>"
                                   class="btn btn-sm btn-outline-primary me-1">Modifier</a>
                                <form method="post" action="${pageContext.request.contextPath}/admin/users"
                                      class="d-inline"
                                      onsubmit="return confirm('Supprimer <%= u.getUsername() %> ?');">
                                    <input type="hidden" name="id" value="<%= u.getId() %>">
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

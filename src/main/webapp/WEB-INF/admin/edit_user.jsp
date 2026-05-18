<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="wolfgang.models.User" %>

<jsp:include page="../include/header.jsp">
    <jsp:param name="title" value="Admin — Modifier un utilisateur — Wolfgang"/>
</jsp:include>

<div class="d-flex">
    <%@ include file="../include/admin_sidebar.jsp" %>

    <div class="flex-grow-1 p-4">
        <%@ include file="../include/flash.jsp" %>

        <% User editedUser = (User) request.getAttribute("editedUser"); %>

        <div class="d-flex align-items-center mb-4 gap-3">
            <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline-secondary btn-sm">← Retour</a>
            <h1 class="h3 mb-0">Modifier 
                <a href="${pageContext.request.contextPath}/profile?id=<%= editedUser.getId() %>">
                    <%= editedUser.getUsername() %>
                </a>
            </h1>
        </div>

        <div class="card shadow-sm" style="max-width: 520px;">
            <div class="card-body p-4">
                <form method="post" action="${pageContext.request.contextPath}/admin/users/edit">
                    <input type="hidden" name="id" value="<%= editedUser.getId() %>">

                    <div class="mb-3">
                        <label for="username" class="form-label">Nom d'utilisateur</label>
                        <input type="text" class="form-control" id="username" name="username"
                               value="<%= editedUser.getUsername() %>" required>
                    </div>

                    <div class="mb-3">
                        <label for="email" class="form-label">Email</label>
                        <input type="email" class="form-control" id="email" name="email"
                               value="<%= editedUser.getEmail() %>" required>
                    </div>

                    <div class="mb-3">
                        <label for="password" class="form-label">Nouveau mot de passe <span class="text-muted small">(laisser vide pour ne pas changer)</span></label>
                        <input type="password" class="form-control" id="password" name="password">
                    </div>

                    <div class="mb-4">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="isAdmin" name="isAdmin"
                                   <%= editedUser.isAdmin() ? "checked" : "" %>>
                            <label class="form-check-label" for="isAdmin">Administrateur</label>
                        </div>
                    </div>

                    <button type="submit" class="btn btn-primary">Enregistrer</button>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="../include/footer.jsp" %>

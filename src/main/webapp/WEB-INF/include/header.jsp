<%@ page import="wolfgang.models.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param['title']}</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/wolfgang.css">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/">Wolfgang</a>
        <div class="ms-auto">
            <% if (session.getAttribute("user") != null) { %>
                <span class="navbar-text me-3">
                    Connecté en tant que <strong><%= ((User) session.getAttribute("user")).getUsername() %></strong>
                </span>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-sm">Déconnexion</a>
            <% } else { %>
                <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-light btn-sm">Connexion</a>
            <% } %>
        </div>
    </div>
</nav>

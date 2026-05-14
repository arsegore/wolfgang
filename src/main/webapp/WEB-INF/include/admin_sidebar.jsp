<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String section = (String) request.getAttribute("adminSection");
%>
<nav class="d-flex flex-column bg-dark text-white p-3 flex-shrink-0" style="width: 220px; min-height: calc(100vh - 56px);">
    <span class="text-uppercase text-secondary small fw-semibold mb-3 ps-2">Administration</span>
    <ul class="nav nav-pills flex-column gap-1">
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/users"
               class="nav-link <%= "users".equals(section) ? "active" : "text-white-50" %>">
                Utilisateurs
            </a>
        </li>
        <li class="nav-item">
            <a href="${pageContext.request.contextPath}/admin/compositions"
               class="nav-link <%= "compositions".equals(section) ? "active" : "text-white-50" %>">
                Compositions
            </a>
        </li>
    </ul>
</nav>

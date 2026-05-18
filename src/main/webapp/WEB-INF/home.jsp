<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="wolfgang.models.Information, java.util.List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="include/header.jsp">
    <jsp:param name="title" value="Accueil — Wolfgang"/>
    <jsp:param name="activePage" value="home"/>
</jsp:include>

<div class="container py-5">
    <%@ include file="include/flash.jsp"%>
    <h1>Bienvenue sur Wolfgang</h1>
</div>

<div class="container py-5">
    <div class="row mb-5">

        <%-- Actualités --%>
        <%
            List<Information> informations = (List<Information>) request.getAttribute("informations");
            if (informations != null && !informations.isEmpty()) {
        %>
        <div class="col-12 mb-4">
            <h2 class="h3 mb-3">Actualités</h2>
            <div class="row">
                <% for (Information info : informations) { %>
                <div class="col-md-4 mb-3">
                    <div class="card border-0 shadow-sm h-100">
                        <div class="card-body">
                            <h5 class="card-title"><%= info.getTitle() %></h5>
                            <p class="card-text text-muted small" style="white-space: pre-line;"><%= info.getDescription() != null ? info.getDescription() : "" %></p>
                        </div>
                        <div class="card-footer bg-white border-0 text-muted small">
                            <%= info.getCreatedAt() != null ? info.getCreatedAt().toLocalDate() : "" %>
                        </div>
                    </div>
                </div>
                <% } %>
            </div>
            <hr class="my-4">
        </div>
        <% } %>

        <%-- Bloc dernière composition utilisateur/amis --%>
        <c:if test="${not empty sessionScope.user}">
            <div class="row align-items-stretch mb-4">

                <%-- Dernière composition de l utilisateur --%>
                <c:if test="${not empty myLastCompo}">
                    <div class="col-md-4 mb-4 border-end">
                        <h1 class="h3 mb-4">Reprendre ma composition</h1>
                        <div class="card shadow-sm">
                            <div class="card-body p-4">
                                <h5 class="card-title">${myLastCompo.title}</h5>
                                <p class="text-muted small">Tempo : ${myLastCompo.tempo} BPM</p>
                                <p class="card-text text-truncate">${myLastCompo.description}</p>
                            </div>
                            <div class="card-footer bg-white border-0 p-3">
                                <a href="${pageContext.request.contextPath}/composition/view?id=${myLastCompo.id}"
                                   class="btn btn-primary w-100">
                                    Reprendre
                                </a>
                            </div>
                        </div>

                    </div>
                </c:if>

                <%-- Dernières compositions des amis --%>
                <div class="col-md-8 mb-4">
                    <h1 class="h3 mb-4">Dernières compositions de mes amis</h1>
                    <c:choose>
                        <c:when test="${not empty sessionScope.user.friends && not empty friendsLastComp}">
                            <div class="row">
                                <c:forEach var="comp" items="${friendsLastComp}">
                                    <div class="col-md-6 mb-4">
                                        <div class="card shadow-sm border-0 bg-light">
                                            <div class="card-body p-4">
                                                <h5 class="card-title">${comp.title}</h5>
                                                <p class="small text-primary">
                                                    Par : 
                                                    <a href="${pageContext.request.contextPath}/profile?id=${comp.owner.id}">
                                                        ${comp.owner.username}
                                                    </a>
                                                </p>
                                            </div>
                                            <div class="card-footer bg-transparent border-0 p-3">
                                                <a href="${pageContext.request.contextPath}/composition/view?id=${comp.id}"
                                                   class="btn btn-outline-secondary w-100">
                                                    Consulter
                                                </a>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="d-flex justify-content-center align-items-center text-center text-muted opacity-75 p-4">
                                <p class="mb-0">
                                    Vous n’avez pas encore d’amis ou vos amis n’ont publié aucune composition.
                                </p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                <hr class="my-4">
            </div>
        </c:if>

        <%-- Dernières compositions publiques --%>
        <div class="col-12">
            <h1 class="h3 mb-4">Dernières compositions</h1>
        </div>

        <c:choose>
            <c:when test="${not empty publicLastCompo}">
                <c:forEach var="comp" items="${publicLastCompo}">
                    <div class="col-md-4 mb-4">
                        <div class="card shadow-sm border-0 bg-light h-100">
                            <div class="card-body p-4">
                                <h5 class="card-title">${comp.title}</h5>
                                <p class="small text-primary">
                                    Par : 
                                    <a href="${pageContext.request.contextPath}/profile?id=${comp.owner.id}">
                                        ${comp.owner.username}
                                    </a>
                                </p>
                            </div>
                            <div class="card-footer bg-transparent border-0 p-3">
                                <a href="${pageContext.request.contextPath}/composition/view?id=${comp.id}" class="btn btn-outline-secondary w-100">Consulter</a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <p class="text-center text-muted">Aucune composition publique disponible pour le moment.</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@include file="include/footer.jsp"%>

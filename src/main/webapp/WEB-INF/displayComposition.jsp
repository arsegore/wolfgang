<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="include/header.jsp">
    <jsp:param name="title" value="Tableau de bord — Wolfgang"/>
</jsp:include>

<div class="container py-5">
    <%@ include file="include/flash.jsp"%>

    <div class="row mb-5">
        <div class="col-12">
            <h1 class="h3 mb-4">Mes compositions</h1>
        </div>

        <c:choose>
            <c:when test="${empty sessionScope.user}">
                <div class="col-12">
                    <div class="alert alert-info border shadow-sm">
                        <i class="bi bi-info-circle me-2"></i>
                        Connectez-vous pour créer et retrouver vos compositions personnelles.
                        <a href="${pageContext.request.contextPath}/login" class="alert-link ms-2">Se connecter</a>
                    </div>
                </div>
            </c:when>
            <c:when test="${empty myCompositions}">
                <div class="col-12">
                    <div class="alert alert-light border shadow-sm">
                        Vous n'avez pas encore créé de partition.
                        <a href="create">Commencer une nouvelle œuvre ?</a>
                    </div>
                </div>
            </c:when>

            <c:otherwise>
                <c:forEach var="comp" items="${myCompositions}">
                    <div class="col-md-4 mb-4">
                        <%-- Le cadre (card) est BIEN à l'intérieur du forEach --%>
                        <div class="card shadow-sm h-100">
                            <div class="card-body p-4"> <%-- Classe p-4 inspirée de create_composition.jsp [cite: 2] --%>
                                <h5 class="card-title">${comp.title}</h5>
                                <p class="text-muted small">Tempo : ${comp.tempo} BPM</p>
                                <p class="card-text text-truncate">${comp.description}</p>
                            </div>
                            <div class="card-footer bg-white border-0 p-3">
                                <a href="Editor?id=${comp.id}" class="btn btn-primary w-100">Ouvrir</a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>

    <hr class="my-5">

    <div class="row">
        <div class="col-12">
            <h2 class="h4 mb-4">Découvrir des œuvres publiques</h2>
        </div>

        <c:if test="${empty publicCompositions}">
            <p class="text-center text-muted">Aucune composition publique disponible pour le moment.</p>
        </c:if>

        <c:forEach var="pub" items="${publicCompositions}">
            <div class="col-md-4 mb-4">
                <div class="card shadow-sm border-0 bg-light h-100">
                    <div class="card-body p-4">
                        <h5 class="card-title">${pub.title}</h5>
                        <p class="small text-primary">Par : ${pub.owner.username}</p>
                    </div>
                    <div class="card-footer bg-transparent border-0 p-3">
                        <a href="View?id=${pub.id}" class="btn btn-outline-secondary w-100">Consulter</a>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/Client.js"></script>

<%@include file="include/footer.jsp"%>
<%@ page import="wolfgang.models.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:include page="include/header.jsp">
    <jsp:param name="title" value="Se connecter"/>
</jsp:include>
<div class="container">
    <%@ include file="include/flash.jsp"%>

    <% if (session.getAttribute("user") != null) { %>
        <p>Connecté en tant que <%= ((User) session.getAttribute("user")).getUsername() %></p>
        <a href="${pageContext.request.contextPath}/logout">Déconnexion</a>
    <% } else { %>
            <a href="${pageContext.request.contextPath}/login">Connexion</a>
        <% } %>
    <h1>Crée votre composition.</h1>

    <form action="" method="POST" class="form-example">
        <label for="nameComp">Nom de votre Composition :</label>
        <input type="text" name="nameComp" id="nameComp" required/>
        <label for="tempo">>uel est votre temp ?</label>
        <input type="number" name="tempo" id="tempo" required min="20" max="2000"/>
        <label for="access">Type d\'acces</label>
        <select name="accessType" id="accessType">
            <option value="">--veuillez choisir une option--</option>
            <option value="ownerOnly">Vous uniquement</option>
            <option value="public">publique</option>
            <option value="link">Utilisateur disposant du lien</option>
        </select>

    <input value="soumettre" type="submit" name="buttonSub">Soumettre</button>
    </form>

</div>

<%@include file="include/footer.jsp"%>
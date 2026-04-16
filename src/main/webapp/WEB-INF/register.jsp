<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:include page="include/header.jsp">
    <jsp:param name="title" value="S'inscrire"/>
</jsp:include>

<div class="container">
    <%@ include file="include/flash.jsp"%>

    <div class="register-title">
        <h1>S'inscrire à Wolfgang</h1>
    </div>
    <div class="register-form">
        <form action="" method="post">
            <label for="username">Nom d'utilisateur</label>
            <input type="text" id="username" name="username" placeholder="Nom d'utilisateur" required>

            <label for="email">Adresse mail</label>
            <input type="text" id="email" name="email" placeholder="Adresse mail" required>

            <label for="password">Mot de passe</label>
            <input type="password" id="password" name="password" placeholder="Mot de passe" required>
            <%-- double check du mdp à ajouter --%>

            <button type="submit">S'inscrire</button>
        </form>
        <p>Vous possédez déjà un un compte ? <a href="/wolfgang/login">Connectez-vous</a> !</p>
    </div>
</div>

<%@include file="include/footer.jsp"%>

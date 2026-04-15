<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:include page="include/header.jsp">
    <jsp:param name="title" value="Se connecter"/>
</jsp:include>
<div class="container">
    <%@ include file="include/flash.jsp"%>

    <div class="register-title">
        <h1>Connexion</h1>
    </div>
    <div class="register-form">
        <form action="" method="post">
            <label for="username">Nom d'utilisateur</label>
            <input type="text" id="username" name="username" placeholder="Nom d'utilisateur" required>

            <label for="password">Mot de passe</label>
            <input type="password" id="password" name="password" placeholder="Mot de passe" required>

            <button type="submit">Se connecter</button>
        </form>
        <p>Vous n'avez pas encore de compte ? <a href="/wolfgang/register">Inscrivez-vous</a> !</p>
    </div>
</div>

<%@include file="include/footer.jsp"%>
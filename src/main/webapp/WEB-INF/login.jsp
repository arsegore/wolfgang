<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:include page="include/header.jsp">
    <jsp:param name="title" value="Se connecter — Wolfgang"/>
</jsp:include>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <%@ include file="include/flash.jsp"%>
            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <h1 class="card-title h3 mb-4">Connexion</h1>
                    <form action="" method="post">
                        <div class="mb-3">
                            <label for="username" class="form-label">Nom d'utilisateur</label>
                            <input type="text" class="form-control" id="username" name="username" placeholder="Nom d'utilisateur" required>
                        </div>
                        <div class="mb-3">
                            <label for="password" class="form-label">Mot de passe</label>
                            <input type="password" class="form-control" id="password" name="password" placeholder="Mot de passe" required>
                        </div>
                        <button type="submit" class="btn btn-primary w-100">Se connecter</button>
                    </form>
                    <p class="mt-3 text-center mb-0">
                        Vous n'avez pas encore de compte ? <a href="${pageContext.request.contextPath}/register">Inscrivez-vous</a> !
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>

<%@include file="include/footer.jsp"%>

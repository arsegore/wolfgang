<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:include page="include/header.jsp">
    <jsp:param name="title" value="Nouvelle composition — Wolfgang"/>
</jsp:include>

<div class="container py-5">
    <%@ include file="include/flash.jsp"%>
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <h1 class="card-title h3 mb-4">Créez votre composition</h1>
                    <form action="" method="POST">
                        <div class="mb-3">
                            <label for="nameComp" class="form-label">Nom</label>
                            <input type="text" class="form-control" name="nameComp" id="nameComp" required>
                        </div>
                        <div class="mb-3">
                            <label for="tempo" class="form-label">Tempo</label>
                            <input type="number" class="form-control" name="tempo" id="tempo" required min="20" max="2000">
                        </div>
                        <div class="mb-3">
                            <label for="accessType" class="form-label">Type d'accès</label>
                            <select class="form-select" name="accessType" id="accessType">
                                <option value="">-- Veuillez choisir une option --</option>
                                <option value="private">Vous uniquement</option>
                                <option value="public">Publique</option>
                                <option value="link">Utilisateurs disposant du lien</option>
                            </select>
                        </div>
                        <button type="submit" name="buttonSub" class="btn btn-primary w-100">Soumettre</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<%@include file="include/footer.jsp"%>

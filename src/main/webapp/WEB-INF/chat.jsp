<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<jsp:include page="include/header.jsp">
    <jsp:param name="title" value="Chat Wolfgang"/>
</jsp:include>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <h1 class="h3 mb-4">Espace de discussion</h1>

            <div id="chat-config"
                 data-pseudo="${sessionScope.user.username}"
                 data-context="${pageContext.request.contextPath}"
                 class="d-none">
            </div>

            <div class="card shadow-sm">
                <div class="card-body">
                    <div id="messages" class="wf-chat-messages wf-chat-messages--tall"></div>

                    <div class="input-group mt-3">
                        <input type="text" id="messageInput" class="form-control" placeholder="Tapez votre message...">
                        <button class="btn btn-primary" onclick="send()">Envoyer</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/Client.js"></script>

<%@include file="include/footer.jsp"%>
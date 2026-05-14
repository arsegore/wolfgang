<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="wolfgang.utils.FlashMessageUtils" %>
<%
    String flash = FlashMessageUtils.getMessage(request);
    String flashType = FlashMessageUtils.getType(request);
    if (flash != null) {
        String bsClass = "success".equals(flashType) ? "alert-success" : "alert-danger";
%>
<div class="alert <%= bsClass %> alert-dismissible fade show" role="alert">
    <%= flash %>
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Fermer"></button>
</div>
<% } %>

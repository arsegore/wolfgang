<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="wolfgang.utils.FlashMessageUtils" %>
<%
    String flash = FlashMessageUtils.getMessage(request);
    String flashType = FlashMessageUtils.getType(request);
    if (flash != null) {
%>
<div class="flash <%= flashType %>"><%= flash %></div>
<% } %>

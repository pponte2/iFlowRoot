<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%>
<%@ include file="inc/defs.jsp"%>
<%
  String src = fdFormData.getParameter("src");
  String param = URLEncoder.encode(fdFormData.getParameter("param"));
%>
<iframe onload="document.getElementById('open_proc_frame').contentWindow.document.body.scrollTop=0;calcFrameHeight('open_proc_frame');" id="open_proc_frame" name="open_proc_frame"
	frameborder="0" scrolling="auto" marginheight="0" marginwidth="0" width="100%" height="100%"
	src="<%=src%>?process_url=<%=param%>" class="open_proc_frame_colapsed" style="display: block;"> your
	browser does not support iframes or they are disabled at this time </iframe>

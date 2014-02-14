<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%>
<%@ include file="inc/defs.jsp"%>
<%
  String src = fdFormData.getParameter("src");
  String tab = fdFormData.getParameter("tab");
  String param = URLEncoder.encode(fdFormData.getParameter("param"));
%>
<div id="div_proc_menu_colapsed" class="proc_menu_colapsed" style="display:block;">
	
	<div class="pull-right" style="display:inline;width:15px;">&nbsp;</div>
	<!--img id="butclose" class="proc_menu_button pull-right" src="images/close.png" onclick="javascript:close_process(3);" title="Fechar::Fechar o form"/-->
	<div id="butclose" class="proc_menu_button btn btn-default btn-sm pull-right" src="images/close.png" onclick="javascript:close_process(3);changeColor(document.getElementById('ptc_<%=23%>'));" title="Fechar::Fechar o form"><if:message string="button.close"/></div>
	<!--
	<img id="butexpand" class="proc_menu_button"  src="images/detach.png" onclick="expand();"/>
	<img id="butinfocol" class="proc_menu_button" style="width:auto; display:none;" src="images/info.png" onclick="info();"/>
	-->
	<span id ="process_annotations_span_colapsed"></span>
</div>
<div id="div_proc_menu_expanded" class="proc_menu_expanded" style="display:none;">
	<!--
	<img id="butclose" class="proc_menu_button" src="images/close.png" onclick="javascript:close_process(3);"/>
	<img id="butcolapse" class="proc_menu_button" src="images/attach.png" onclick="colapse();"/>
	<img id="butinfoexp" class="proc_menu_button" style="width:auto; display:none;" src="images/info.png" onclick="info();"/>
	-->
	<span id ="process_annotations_span_expanded"></span>
</div>
<iframe onload="document.getElementById('open_proc_frame_<%=tab%>').contentWindow.document.body.scrollTop=0;calcFrameHeight('open_proc_frame_<%=tab%>');" id="open_proc_frame_<%=tab%>" name="open_proc_frame_<%=tab%>"
	frameborder="0" scrolling="auto" marginheight="0" marginwidth="0" width="100%" height="100%"
	src="<%=src%>?process_url=<%=param%>" class="open_proc_frame_colapsed" style="display: block;"> your
	browser does not support iframes or they are disabled at this time </iframe>

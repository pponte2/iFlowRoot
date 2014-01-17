<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ include file = "../inc/defs.jsp" %>
<%@ page import = "java.sql.*" %>
<%@ page import="pt.iflow.api.db.DatabaseInterface" %>
<%@page import="pt.iflow.core.ProcessManagerBean;"%>

<if:checkUserAdmin type="both">
	<div class="error_msg"><if:message string="admin.error.unauthorizedaccess"/></div>
</if:checkUserAdmin>

<%
String pidS = fdFormData.getParameter("pidSearch");
String subpidS = fdFormData.getParameter("subPidSearch");
String newProcdata = request.getParameter("newprocdata");
char procdatatype;
if(pidS==null)
  pidS="";
else{try{Integer.parseInt(pidS);} catch(Exception E){}}
if (subpidS == null || "".equals(subpidS)) 
  subpidS = "1";
try{Integer.parseInt(subpidS);}catch(Exception E){}
String oper = fdFormData.getParameter("oper");
String result="Sucess";
String procdata="";

if ("U".equals(oper)){ 
result=pm.setProcdataString(userInfo, pidS, subpidS, newProcdata);
}

if (pidS!=null && ! "".equals(pidS)) {
  procdata = pm.getProcdataString(userInfo, pidS, subpidS);
 
}

%>

<form name="formulario" id="formulario">
<h1 id="title_admin">Procurar Procdata</h1>
<%if(!result.equals("Sucess")){%>

<div class="error_msg">
Error

</div>

<%} %>
<fieldset>
<ol>
<li>
<if:formInput type="text" name="pidSearch" value="<%=pidS%>" labelkey="admin_nav.section.system.tooltip.pid" edit="true" required="true" maxlength="50"/>
</li>
<li>
<if:formInput type="text" name="subPidSearch" value="1" labelkey="admin_nav.section.system.tooltip.subpid" edit="true" required="true" maxlength="50"/>
</li>
<li>
<input class="regular_button_01" type="button" value="Procurar" name="search" align="middle" onclick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/alterProcdata.jsp")%>','&oper=S&' + get_params(document.formulario));">
</li>
<li>
<br>
<textarea name="newprocdata" id="newprocdata" cols="100" rows="20" class="textarea">
<%= procdata %>
</textarea><br>
<li>
<input class="regular_button_01" type="button" value="Guardar" name="save" align="middle" onclick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/alterProcdata.jsp")%>','&oper=U&newprocdata=' + encodeURIComponent(document.getElementById('newprocdata').value) + '&' + get_params(document.formulario));">
</li>
	
</li>

</ol>
</fieldset>



</form>


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
String begin=pm.getMinPidNonEncrypted(userInfo);
String end=pm.getMaxPidNonEncrypted(userInfo);
  %>

<%
String pidB = fdFormData.getParameter("pidBegin");
String pidE = fdFormData.getParameter("pidEnd");
String oper = fdFormData.getParameter("oper");
String result="";
int flag=0;
if("".equals(pidE) && "".equals(pidB))
  result="Incomplete";

if(pidE!=null && pidB!=null && !"".equals(pidE) && !"".equals(pidB)){
  try{
  if(Integer.parseInt(pidB)<Integer.parseInt(pidE))
    flag=1;
  else
   result="FailAtempt";
  }
  catch(Exception e){}
}

if(pidE!=null && pidB!=null && "".equals(pidE) && !"".equals(pidB)){
  try{
    Integer.parseInt(pidB);
    flag=1;
  }
  catch(Exception e){result="FailAtempt2";}
}

if(pidE!=null && pidB!=null && !"".equals(pidE) && "".equals(pidB)){
  try{
    Integer.parseInt(pidE);
    flag=1;
  }
  catch(Exception e){result="FailAtempt2";}
}

if(pidE!=null && pidB==null )
  if(!"".equals(pidE)){
    try{
      Integer.parseInt(pidE);
      flag=1;
    }
    catch(Exception e){result="FailAtempt2";}
   			}

if(pidE==null && pidB!=null )
  if(!"".equals(pidB)){
    try{
      Integer.parseInt(pidB);
      flag=1;
    }
    catch(Exception e){result="FailAtempt2";}
  }

if(pidE==null)
  pidE="";

if(pidB==null)
  pidB="";

if("E".equals(oper) && flag==1){
result = pm.ProcessEncryptEB(userInfo,pidB,pidE);
begin=pm.getMinPidNonEncrypted(userInfo);
end=pm.getMaxPidNonEncrypted(userInfo);
}

%>

<form name="formulario" id="formulario">
<h1 id="title_admin">Encriptar</h1>


<div>
Processos nÃ£o encriptados : PidMin : <%=begin%>  
							PidMax : <%=end%>
</div>

<%if("Sucess".equals(result)){ %>

<div>
Encriptado com sucesso

</div>

<%} %>

<%if("Incomplete".equals(result)){ %>

<div class="error_msg">
Ã‰ necessÃ¡rio preencher pelo menos um campo

</div>

<%} %>

<%if("FailAtempt".equals(result)){ %>

<div class="error_msg">
O Pid de inicio tem de ser menor que o de finalizaÃ§Ã£o

</div>

<%} %>

<%if("FailAtempt2".equals(result)){ %>

<div class="error_msg">
Todos os pids apenas sÃ£o constituidos por numeros inteiros

</div>

<%} %>


<fieldset>
<ol>
<li>
Desde o pid: &nbsp&nbsp&nbsp<input type="text" name="pidBegin" value="<%=pidB%>" labelkey="PidB" edit="true" required="false" maxlength="50"/>
</li>
<li>
AtÃ© ao pid:	&nbsp&nbsp&nbsp&nbsp	<input type="text" name="pidEnd" value="<%=pidE%>" labelkey="PidE" edit="true" required="false" maxlength="50"/>
</li>
<li>
<input class="regular_button_01" type="button" value="Encriptar" name="encrypt" align="middle" onclick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/encryptProcdata.jsp")%>','&oper=E&' + get_params(document.formulario));">
</li>
</ol>
</fieldset>

	

</form>


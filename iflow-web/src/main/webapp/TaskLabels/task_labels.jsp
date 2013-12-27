<%@page import="pt.iflow.api.folder.Folder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/errorhandler.jsp"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%>
<%@ include file="../inc/defs.jsp"%>

<%

	FolderManager fm = BeanFactory.getFolderManagerBean();

	//EDIT FOLDER
	String editfolder = fdFormData.getParameter("editfolder");
	if(editfolder!=null){
		  String editname = fdFormData.getParameter("editname");
		  String color = "#"+fdFormData.getParameter("color");
	
		  if(editfolder.equals("0"))
			  fm.createFolder(userInfo,editname,color);
		  else
		  	  fm.editFolder(userInfo,editfolder,editname,color);
	}

	List<Folder> folders = fm.getUserFolders(userInfo);
%>

<ul>
	<%for (Folder folder: folders) { %>
	<li class="droppable" style="height:34px;padding:2px;margin:2px;" valToAssign="<%= folder.getFolderid()%>">
		<a id="bt_edit_<%=folder.getFolderid()%>" href="javascript:getJSP('main_content.jsp?filterfolder=<%= folder.getFolderid()%>');" class="lmenu form-label" ><%=folder.getName()%></a>
		<a href="" onClick="editLabel('<%=folder.getFolderid()%>','1'); return false;" class="lmenu"><img id="cl_edit_bg_<%=folder.getFolderid()%>" title="Alterar" width="15" height="30" src="Themes/newflow/images/img_categ.png" style="float: right; background:<%=folder.getColor()%>;"/></a>
		<input class="form-control" type="text" value="" id="edit_<%=folder.getFolderid()%>" style="display:none;width:8em;height:25px;" onkeydown="if (event.keyCode == 13) { editLabel('<%=folder.getFolderid()%>','0');}"/>
		<input id="bt_pickColor_<%=folder.getFolderid()%>" class="color form-control" style="display:none;width:15px;height:25px;border: 1px solid #CCCCCC" maxlength="0" title="Escolha a cor"></input>
		<button id="bt_cancel_<%=folder.getFolderid()%>"  style="display:none;"  type="button" class="close pull-left" onclick="javascript:editLabel('<%=folder.getFolderid()%>','-1');">&times;</button>
	</li>
	<%}%>
</ul>
<script type="text/javascript">
</script>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="pt.iflow.api.utils.UserInfoInterface"%>
<%@ page import="pt.iflow.api.utils.Const"%>
<%@ page import="pt.iflow.api.core.UserManager"%>
<%@ page import="pt.iflow.api.userdata.views.*"%>
<%@ include file="../../inc/defs.jsp"%>

<if:checkUserAdmin type="org">
	<div class="error_msg"><if:message string="admin.error.unauthorizedaccess"/></div>
</if:checkUserAdmin>

<%
List<String[]> calendar = new ArrayList<String[]>(); 
String title = messages.getString("admin_nav.section.resources.tooltip.calend");
String sPage = "Admin/UserManagement/useradm";

boolean canModify = (!Const.INSTALL_LOCAL.equals(Const.INSTALL_TYPE)) || AccessControlManager.getUserDataAccess().canModifyUser();
boolean canPassword = (!Const.INSTALL_LOCAL.equals(Const.INSTALL_TYPE)) || AccessControlManager.getUserDataAccess().canModifyPassword();
boolean canDelete = (!Const.INSTALL_LOCAL.equals(Const.INSTALL_TYPE)) || AccessControlManager.getUserDataAccess().canDeleteUser();

StringBuffer sbError = new StringBuffer();
int flowid = -1;
String sOper = fdFormData.getParameter("oper");
String userId = fdFormData.getParameter("userid");
String name = fdFormData.getParameter("nome");
String id = fdFormData.getParameter("id");

UserInfoInterface ui = (UserInfoInterface) session.getAttribute(Const.USER_INFO);

UserManager manager = BeanFactory.getUserManagerBean();
String actionMsg = "";
if ("del".equals(sOper)) {
  if(manager.deleteCalendars(ui,id)) {
  	actionMsg = messages.getString("useradm.calend.deleted");
  } else { 
  	actionMsg = messages.getString("useradm.calend.not_deleted");
  }
} else if(Const.bUSE_EMAIL && "passreset".equals(sOper)) {
  if(manager.resetPassword(ui, userId)) {
  	actionMsg = messages.getString("useradm.info.password_reset");
  } else {
  	actionMsg = messages.getString("useradm.info.password_not_reset");
  }
}
//get calendars
try {
	calendar = manager.getCalendars(ui);
}
catch (Exception e) {
	e.printStackTrace();
}
      %>

<form method="post" name="formulario" id="formulario">

      <h1 id="title_admin"><%=title%></h1>
      <div class="table_inc">  
        <table class="item_list">
          <tr class="tab_header">
				
				<td>
					<%=messages.getString("admin_nav.section.resources.tooltip.calend")%>
				</td>
				<% if (Const.bUSE_EMAIL && canPassword) { %>
				<td/>
				<% } %>
				<% if (canDelete) { %>
				<td/>
				<% } %>
			</tr>
			<%
        for (int i = 0; i < calendar.size(); i++) {
%>

			<tr class="<%=i%2==0?"tab_row_even":"tab_row_odd"%>" style="color:grey;">				
				
				<td>
					<%=calendar.get(i)[1]%>
				</td>
				
				<td class="itemlist_icon">
					<a href="javascript:if (confirm('<%=messages.getString("useradm.confirm.delete")%>') ) tabber_right(4, '<%=response.encodeURL("Admin/Resources/iflowcalendar.jsp")%>','oper=del&id=<%=calendar.get(i)[0]%>' );">
					<img class="toolTipImg" src="images/icon_delete.png" border="0" title="<%=messages.getString("useradm.calendar.delete")%>">
					</a>
				</td>
				<td class="itemlist_icon">
					<a href="javascript:tabber_right(4, '<%=response.encodeURL("Admin/Resources/newcalendar.jsp")%>', 'oper=2&nome=<%=calendar.get(i)[1]%>&id=<%=calendar.get(i)[0]%>');"/>
					<img class="toolTipImg" src="images/icon_modify.png" width="16" height="16" border="0" title="<%=messages.getString("useradm.calendar.edit")%>">
					</a>
				</td>
			</tr>
			<%
		}
      

    %>
    
		</table>
	</div>
<% if (canModify) { %>
	<div class="button_box">
    	<input class="regular_button_01" type="button" name="add_user" value="<%=messages.getString("button.add")%>" onClick="javascript:tabber_right('admin', '<%=response.encodeURL("Admin/Resources/newcalendar.jsp")%>','operation=create&id=0');"/>
	</div>
<% } %>

<if:generateHelpBox context="useradm"/>

</form>

<%@page import="pt.iflow.api.delegations.DelegationInfoData"%>
<%@page import="pt.iflow.api.notification.Notification"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%>
<%@ include file="inc/defs.jsp"%>
<%@page import="pt.iflow.api.licensing.LicenseServiceFactory"%>
<%	
	//Collection<Notification> notifications = BeanFactory.getNotificationManagerBean().listNotifications(userInfo);
	//Collection<DelegationInfoData> delegations = BeanFactory.getDelegationInfoBean().getDeployedReceivedDelegations(userInfo);
	Collection<Notification> msgs = BeanFactory.getNotificationManagerBean().listAllNotifications(userInfo);
	//Integer nAlerts = (notifications == null ? 0 : notifications.size()) + (delegations == null ? 0 : delegations.size());
	
	for(Notification notification: msgs)
		if(notification.getSuspend()!=null && notification.getSuspend()!=null && notification.getSuspend().before(new Date())){
			BeanFactory.getNotificationManagerBean().markMessageNew(userInfo, notification.getId());
			BeanFactory.getNotificationManagerBean().suspendMessageNew(userInfo, notification.getId(), new Date(2100,12,12));
			BeanFactory.getNotificationManagerBean().updateCreated(userInfo, notification.getId(), new Date());
		}
	msgs = BeanFactory.getNotificationManagerBean().listAllNotifications(userInfo);
%>
<table class="item_list table" style="color:black;">
	<thead>
		<tr class="tab_header">
			<th>
				
			</th>
			<th><input class="btn btn-default btn-sm" style="margin: 0px;" onclick="javascript:showAlertClose();" value="Fechar" type="submit"></th>
			<th></th>
			<th>				
			</th>
			<% 
			int odd=1;
			String class_type,text_style,msg_icon,msg_action,msg_onclick;
			for(Notification notification: msgs){
				if(odd==0){
					class_type="even";
					odd=1;
				} else {
					class_type="odd";
					odd=0;
				}
				if (notification.isRead()){
					text_style="";
					msg_icon="read";
					msg_action="U";
					msg_onclick="document.getElementById(\'delegButtonCount\').text=1 +  Number(document.getElementById(\'delegButtonCount\').text);";
				} else {
					//if(notification.getSuspend()!=null && notification.getSuspend().before(new Date()))
						text_style="font-weight:bold; color: red";
					//else
					//	text_style="font-weight:bold;";
					msg_icon="unread";
					msg_action="R";
					msg_onclick="document.getElementById(\'delegButtonCount\').text= document.getElementById(\'delegButtonCount\').text-1;";
				}
				
				String href = "";
				String [] dadosproc = notification.getLink().split(",");
				int procid = -1;
				String flowid="0";
				String pid="0";
				String subpid="0";
				if(dadosproc.length > 1)
					procid = Integer.parseInt(dadosproc[1]);
				if(notification.getLink().equals("false") || procid<=0)
					href =  "false";
				else{
					href =  "8, \'user_proc_detail.jsp\'," + notification.getLink()+",-3";
					String[] procIdAux = notification.getLink().split(",");
					flowid = procIdAux[0];
					pid = procIdAux[1];
					subpid = procIdAux[2];
				}
				
				%>					
				<tr class="tab_row_<%= class_type%>" id="msg_tr_notification" style="<%=text_style %>;border-top:1px solid black">
					<td>
						<div id="notification_dropdown" class="dropdown">
							<div class="dropdown-toggle" data-toggle="dropdown" id="dropdownmenu_<%= notification.getId() %>" data-toggle="dropdown" onClick="cancelMenu=true;">
								<img src="Themes/newflow/images/V.png"/>
							</div>
							<div class="dropdown-menu topt-menu" role="menu" aria-labelledby="dropdownmenu_<%= notification.getId() %>" style="top:10px;left:30px">								
								<%if (pid!=null && !pid.equals("") && !pid.equals("0")) {%>
							
									<a role="menuitem" tabindex="-1" href="#" onClick="javascript:window.open('user_proc_detail.jsp?flowid=<%=flowid %>&pid=<%=pid %>&subpid=<%=subpid %>&procStatus=-5');;return false;">Ver detalhe</a>
									<br>
								<%if (notification.isPickTask()) {%>								
								
									<a role="menuitem" tabindex="-1" href="#" onClick="javascript:makeRequest('pickActivityFromNotification.jsp', 'flowid=<%=flowid %>&pid=<%=pid %>&subpid=<%=subpid %>', pickActivityFromNotificationCallback, 'text'); return false;"> Ficar com tarefa</a>
									<br>
								<%}} %>
								
									<a role="menuitem" tabindex="-1" href="#" onClick="javascript:markNotification_alert(<%= notification.getId() %>, '<%= msg_action %>'); launchBrowserNotificationCheckers();return false;">Marcar como lido/não lido</a>
								
							</div>
						</div>
					</td>
					<td style="padding: 8px;text-align: left;">
						<%= notification.getMessage() %> - <%= notification.getCreated() %></br>
						<%= notification.getSender().substring(notification.getSender().indexOf("/") + 1, notification.getSender().indexOf(","))%>
						<% if (notification.getExternalLink()!=null && notification.getExternalLink()!="") {
							String externalLinkAux = notification.getExternalLink();
							if(externalLinkAux.startsWith("www."))
								externalLinkAux = "http://" + externalLinkAux;%>
							<a target="_blank" href="<%= externalLinkAux%>"><%= notification.getExternalLink()%></a>
						<% }%>
					</td>
				
					<td class="itemlist_icon" style="padding: 8px;">
						<a onclick="showSchedule(); attributeValueSchedule(<%= notification.getId() %>);">
							<img class="toolTipImg" src="images/flowScheduling_new.png" id="msg_img_<%= notification.getId() %>" width="16" height="16" border="0" title=""></img>
						</a>
					</td>										
	
					<td class="itemlist_icon" style="padding: 8px;">
							<a href="javascript:markNotification_alert(<%= notification.getId() %>, 'D');">
								<img class="toolTipImg" src="images/icon_delete.png" id="msg_img_<%= notification.getId() %>" width="16" height="16" border="0" title=""></img>
							</a>
							<%if (notification.isRead() && notification.getSuspend()!=null && notification.getSuspend().before(new Date(2100,12,12))) {%>
								<img class="toolTipImg" src="images/box_warn.png" id="suspend_till_<%= notification.getId() %>" width="16" height="16" border="0" title="reagendamento: <%= notification.getSuspend()%>"></img>
							<%} %>
						</td>
					</tr>
				<%} %>												
		</tr>
	<thead>
	<tbody>
	</tbody>
</table>
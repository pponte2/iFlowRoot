<?xml version="1.0"?><result>
<%@page import="pt.iflow.api.delegations.DelegationInfoData"%>
<%@page import="pt.iflow.api.notification.Notification"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%>
<%@ include file="inc/defs.jsp"%>
<%	
	String showNotificationDetail = BeanFactory.getNotificationManagerBean().checkShowNotificationDetail(userInfo);	
	String [] dadosproc = showNotificationDetail.split(",");
	Integer procid = 0 ; 
	String href= "";
	if(dadosproc.length > 1)
		procid = Integer.parseInt(dadosproc[1]);
	if(showNotificationDetail.equals("false") || procid<=0)
		href =  "false";
	else
		href =  "START"+"8, \'user_proc_detail.jsp\'," + showNotificationDetail+",-3"+"END";
%><%=href%>
</result>
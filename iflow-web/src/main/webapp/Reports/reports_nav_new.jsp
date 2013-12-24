<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c"%>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if"%>
<%@ page import="pt.iflow.api.msg.IMessages"%>
<%@ page import="org.apache.commons.collections15.map.ListOrderedMap"
%><%@ page import="org.apache.commons.collections15.OrderedMap"
%><%@ include file="../inc/defs.jsp"%>

<%
  java.util.Date urldate = new java.util.Date();

  String sel = fdFormData.getParameter("sel");
  int nSel = AdminNavConsts.NONE;
  try {
    nSel = Integer.parseInt(sel);
  } catch (Exception e) {
  }

  UserDataAccess userDataAccess = AccessControlManager.getUserDataAccess();
  boolean canUserAdmin = userDataAccess.canUserAdmin();

  String theme = BeanFactory.getOrganizationThemeBean().getOrganizationTheme(userInfo).getThemeName();
  boolean isClassic = StringUtils.equals("classic", theme);
  boolean isNewflow = StringUtils.equals("newflow", theme);
%>

<%
  if (userInfo.isOrgAdmin() || userInfo.isProcSupervisor(-1)) {
%>
<%  boolean showOnlyFlowsToBePresentInMenu = true;
    if(BeanFactory.getFlowHolderBean().listFlowsOnline(userInfo, FlowType.REPORTS, showOnlyFlowsToBePresentInMenu).length > 0){//show reports_personalized%>
			<%
				//load flows
				FlowApplications appInfo = BeanFactory.getFlowApplicationsBean();
				//only loads search flows
				FlowMenu flows = BeanFactory.getFlowApplicationsBean().getAllApplicationOnlineMenu(userInfo,FlowType.REPORTS, showOnlyFlowsToBePresentInMenu);

				ArrayList<OrderedMap<Object,Object>> appFlows = new ArrayList<OrderedMap<Object,Object>>();
				Collection<FlowAppMenu> appMenuList = flows.getAppMenuList();
			  	Iterator<FlowAppMenu> iter = appMenuList.iterator();
			  	while(iter != null && iter.hasNext()) {
					FlowAppMenu appMenu = iter.next();
					String sAppName = appMenu.getAppDesc();
					FlowMenuItems menuPart = appMenu.getMenuItems();
					OrderedMap<Object,Object> hm = new ListOrderedMap<Object,Object>();
					if ("".equals(sAppName)) sAppName = messages.getString("main_content.processes.appname.misc");
					hm.put("appname", sAppName);
			  
					hm.put("appid", appMenu.getAppID());
			
					List<IFlowData> currAppflows = menuPart.getFlows(); 
			  
					hm.put("selected", false);
				        
			  		hm.put("flows", menuPart.getFlows());
			  		hm.put("links", menuPart.getLinks());
			  		hm.put("tooltip_flow", messages.getString("main_content.processes.tooltip.flows"));
			  		hm.put("tooltip_link", messages.getString("main_content.processes.tooltip.links"));
			  
			      appFlows.add(hm);
			  	}
			    String tabnr = (String) fdFormData.getParameter("navtabnr");
			    if (tabnr == null) tabnr ="10"; 
			    String pageContent = "proc_list";
			    java.util.Hashtable<String,Object> hsSubstLocal = new java.util.Hashtable<String,Object>();
			    hsSubstLocal.put("appflows", appFlows);
			    hsSubstLocal.put("processesMsg", "");
			    hsSubstLocal.put("tabnr", tabnr);
			    hsSubstLocal.put("title", "");
			    hsSubstLocal.put("ts", java.lang.Long.toString(ts));
			    hsSubstLocal.put("url_prefix", sURL_PREFIX.substring(0, sURL_PREFIX.length() - 1));
			    hsSubstLocal.put("css", css);
			    hsSubstLocal.put("procMenuVisible", "visible");
			    String html = PresentationManager.buildPage(response, userInfo, hsSubstLocal, pageContent);
			    out.println(html);
			%>
<%
  }//end reports_personalized
}
%>

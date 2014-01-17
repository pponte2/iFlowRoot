<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/taglibs/core" prefix="c" %>
<%@ taglib uri="http://www.iknow.pt/jsp/jstl/iflow" prefix="if" %>
<%@ page import="pt.iflow.api.core.UserManager"%>
<%@ include file = "../../inc/defs.jsp" %>
<%

UserManager manager = BeanFactory.getUserManagerBean();
String title = messages.getString("calendar.header.criteria");
StringBuffer sbError = new StringBuffer();
UserInfoInterface ui = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
String op = fdFormData.getParameter("oper");

String nome = "";
String mond = "";
String tues = "";
String wedn = "";
String thur = "";
String frid = "";
String satu = "";
String sund = "";
String[] feri = new String[40];
String[] peri = new String[40];
String id = fdFormData.getParameter("id");

if ("1".equals(op)) {
  	manager.deleteCalendars(ui,id);
  	id="0";
  	boolean b = false;
	String dias = fdFormData.getParameter("diasUteis");
	String feriados = fdFormData.getParameter("feriados");
	String periodos = fdFormData.getParameter("periodos");
	nome = fdFormData.getParameter("nome");
	b = manager.saveCalendar(ui,nome,dias,feriados,periodos,id);
	if(b)
	  title = messages.getString("calendar.field.success");
	else
	  title = messages.getString("calendar.field.notsuccess");
	nome = "";
}
else if("2".equals(op)) {
  nome = fdFormData.getParameter("nome");
  //DAYS
  List<String> days = manager.getCalendarDays(ui,id);
	if(days.contains("monday"))
	  mond = "checked";
	if(days.contains("tuesday"))
	  tues = "checked";
	if(days.contains("wednesday"))
	  wedn = "checked";
	if(days.contains("thursday"))
	  thur = "checked";
	if(days.contains("friday"))
	  frid = "checked";
	if(days.contains("saturday"))
	  satu = "checked";
	if(days.contains("sunday"))
	  sund = "checked";
  
  //HOLIDAYS
  List<String> holidays = manager.getHolidays(ui,id);
  
  for(int i = 0; i<holidays.size();i++){
    String[] valores = holidays.get(i).split(" ");
    String[] hol = valores[0].split("-");
    String temp = hol[2] + "/" + hol[1] + "/" + hol[0];
    feri[i] = temp;
  }
  
  //split " ", inverter data e inserir no holidayValue
  
  //PERIODS
  List<String> periods = manager.getPeriods(ui,id);
  
  for(int i = 0; i<periods.size();i++){
    peri[i] = periods.get(i);
  }
}
  else{
  mond = "checked";
  tues = "checked";
  wedn = "checked";
  thur = "checked";
  frid = "checked";
}

ArrayList<Integer> hour = new ArrayList<Integer>(24);
for (int i = 0; i < 24; i++) {
  hour.add(i);
}

ArrayList<Integer> min = new ArrayList<Integer>(61);
for (int i = 0; i < 61; i++) {
  
  min.add(i);
}


List<List<String>> alData = new ArrayList<List<String>>();

String stmp = null;
String stmp2 = null;
String stmp3 = null;

int ITEMS_PAGE = 20;
int oper = 0;

String sBeforeUser = fdFormData.getParameter("beforeuser");
String sActualUser = fdFormData.getParameter("actualuser");
String sPid = fdFormData.getParameter("pid");
String sShowFlowId = fdFormData.getParameter("showflowid");
try {
  sShowFlowId = "" + Integer.parseInt(sShowFlowId);
  session.setAttribute(Const.SESSION_ATTRIBUTE_FLOWID, Integer.parseInt(sShowFlowId));
} catch(Exception e) {
  try {
    sShowFlowId = "" + Integer.parseInt(session.getAttribute(Const.SESSION_ATTRIBUTE_FLOWID).toString());
  } catch(Exception ex) {
    session.setAttribute(Const.SESSION_ATTRIBUTE_FLOWID, -1);
  }
}
String sShowFlowHtml = "";
String sAfterHtml = "";
String sNavPrevHtml = "";
String sNavNextHtml = "";
int nShowFlowId = -1;
int nItems = ITEMS_PAGE;
int nPid = 0;
String name = "";
String h = "";
if (sBeforeUser == null) sBeforeUser = "";
if (sActualUser == null) sActualUser = "";


if (userflowid > 0) {
	sShowFlowId = String.valueOf(userflowid);
	nShowFlowId = userflowid;
} else {
if (sShowFlowId == null || sShowFlowId.equals("")) sShowFlowId = "-1";
try {
	nShowFlowId = Integer.parseInt(sShowFlowId);
}
catch (Exception e) {
}
}

if (sPid == null || sPid.equals("")) sPid = "";
try {
	nPid = Integer.parseInt(sPid);
}
catch (Exception e) {
}
try {
	stmp = fdFormData.getParameter("itemspage");
	nItems = Integer.parseInt(stmp);
}
catch (Exception e) {
}
try {
	oper = Integer.parseInt(fdFormData.getParameter("oper"));
}
catch (Exception e) {
}

HashMap<Integer,String> hmFlowInfo = new HashMap<Integer,String>();
List<Activity> alActivities = new ArrayList<Activity>();

boolean bAdminUser = userInfo.isOrgAdmin();
Flow flow = BeanFactory.getFlowBean();
IFlowData[] fda = null;
try {
	if (userInfo.isOrgAdmin()) {
		// iflow administrator has full access
		bAdminUser = true;
	}
	else {

		FlowRolesTO[] fra = flow.getAllUserFlowRoles(userInfo);
		for (int i = 0; i < fra.length; i++) {

	if (fra[i].hasPrivilege(FlowRolesTO.READ_PRIV)) {
		hmFlowInfo.put(fda[i].getId(),fda[i].getName());
	}
		}
	}

	fda = BeanFactory.getFlowHolderBean().listFlowsOnline(userInfo, null, new FlowType[] { FlowType.SEARCH, FlowType.REPORTS});

	for (int i=0; fda != null && i < fda.length; i++) {
		hmFlowInfo.put(fda[i].getId(),fda[i].getName());
	}
}
catch (Exception e) {
	Logger.errorJsp(login,"flows"," checking admin profile exception: " + e.getMessage());
	e.printStackTrace();
}

if (userflowid == -1) {
	List<String> alValues = new ArrayList<String>();
	List<String> alNames = new ArrayList<String>();
	alValues.add("-1");
	alNames.add(messages.getString("const.select"));
	for (int i=0; fda != null && i < fda.length; i++) {
		if (!bAdminUser) {
	continue;
		}
		alValues.add(String.valueOf(fda[i].getId()));
		alNames.add(fda[i].getName());
	}
	sShowFlowHtml = Utils.genHtmlSelect("showflowid","",sShowFlowId,alValues,alNames);
}


java.util.Date dtAfter = Utils.getFormDate(request, "dtafter");

sAfterHtml = Utils.genFormDate(response, userInfo, "dtafter", dtAfter, "f_date_c");
%>


<% Collection<UserData> users = BeanFactory.getAuthProfileBean().getAllUsers(userInfo.getOrganization());%>

<%@page import="pt.iflow.api.transition.FlowRolesTO"%>
<form name="f1" method="post">
  <script>
	 function addHoliday() {
	   if((document.getElementById('f_date_c').value) != '') {
	     var i = 0;
	     while (i<40 && document.getElementById('holidayItem_' + i) != null) i++;
	     document.getElementById('holidaysTable').innerHTML += 
	       '<tr class="tab_row_even" style="color:grey;" id="holidayItem_' + i + '"><td id="holidayDate_' + i + '">' + document.getElementById('f_date_c').value + '</td><td class="itemlist_icon"><a onclick="javascript:removeHoliday(' + i + '); return false;">' +
		'<img class="toolTipImg" src="images/icon_delete.png" border="0" title="<%=messages.getString("useradm.tooltip.deletedays")%>"></a></td></tr>';
	     document.getElementById('f_date_c').value = '';
	   }
	 }
	 
	 function removeHoliday(id) {
	   document.getElementById('holidayItem_' + id).parentNode.removeChild(document.getElementById('holidayItem_' + id));
	 }
	 
	 function getSelectedText(elementId) {
	    var elt = document.getElementById(elementId);

	    if (elt.selectedIndex == -1)
	        return null;

	    return elt.options[elt.selectedIndex].text;
	}
		
	function validateTime(){
		var hr = parseInt(getSelectedText('h'));
		var mn = parseInt(getSelectedText('m'));
		var hr1 = parseInt(getSelectedText('h1'));
		var mn1 = parseInt(getSelectedText('m1'));
		var per = hr + ':' + mn + ':00'+ '-' + hr1 + ':' + mn1+ ':00';
		if(isNaN(hr) || isNaN(mn) || isNaN(hr1) || isNaN(mn1) ){
			alert('Hora invalida');
		} else {
		  if(hr1<hr)
			  if(hr1==0 & mn1 ==0)
			  	addPeriod(per);
			  else
			  	alert('Hora de tÃ©rmino inferior Ã  hora de comeÃ§o.');
		  else if(hr1==hr && mn1<=mn)
			  alert('Hora invalida');
		  else {
			  addPeriod(per);
		  }
		  }		 
		}

	function addPeriod(per) {		
	     var i = 0;
	     while (i<40 && document.getElementById('periodItem_' + i) != null) i++;
	     document.getElementById('periodTable').innerHTML += 
	       '<tr class="tab_row_even" style="color:grey;" id="periodItem_' + i + '"><td id="periodVal_' + i + '">' + per + '</td><td class="itemlist_icon"><a onclick="javascript:removePeriod(' + i + '); return false;">' +
		'<img class="toolTipImg" src="images/icon_delete.png" border="0" title="<%=messages.getString("useradm.tooltip.deleteperiods")%>"></a></td></tr>';
	     per;	   
	 }
	 
	 function removePeriod(id) { 
	   document.getElementById('periodItem_' + id).parentNode.removeChild(document.getElementById('periodItem_' + id));
	 }

	function getCalendParamsForSave(op) {
	
			
		
		var name = document.getElementById('calendval').value;
		var days = "";
	   	var radios = document.getElementsByName('days');
	   	for (var i = 0, length = radios.length; i < length; i++) {
	       if (radios[i].checked) {
		       if(i==0)
			       days += radios[i].value ;
		       else
		       	   days += "," +radios[i].value ;
	       }
	   	}
	   	var holiday = "";
	   	for (var i = 0; i< 40; i++) {
		   	if(document.getElementById('holidayDate_' + i) != null){
		       if(i==0)
		         holiday += document.getElementById('holidayDate_' + i).innerHTML;
		       else
		         holiday += "," +document.getElementById('holidayDate_' + i).innerHTML ;
		   	}
	   	}

	   	var period = "";
	   	for (var i = 0; i< 40; i++) {
		   	if(document.getElementById('periodItem_' + i) != null){
		       if(i==0)
		         period += document.getElementById('periodVal_' + i).innerHTML;
		       else
		         period += "," +document.getElementById('periodVal_' + i).innerHTML ;
		   	}
	   	}
		return 'oper=1&diasUteis='+days+'&feriados='+holiday+'&periodos='+period+'&nome='+name+'&id='+<%=id%>+"";
	}
  </script>

  <h1 id="title_admin"><%=messages.getString("calendar.crit.title")%></h1>
                                              

  <fieldset>
    <legend align="left"><%=title %></legend>
    <ol>
      <li>
        <label for="itemspage"><%=messages.getString("calendar.crit.name") %>:</label>
        <input type="text" id = "calendval" name="calendval" value="<%=nome%>" class="txt" size="16" maxlength="16" />
      </li>
      <li>
                    <label for="itemspage"><%=messages.getString("calendar.field.days") %>:</label><br></br>
                    <input type="checkbox" name="days" value="monday"class="txt" <%=mond%>> <%=messages.getString("calendar.days.monday") %> <br></br>
                    <input type="checkbox" name="days" value="tuesday"class="txt" <%=tues%>> <%=messages.getString("calendar.days.tuesday") %><br></br>
                    <input type="checkbox" name="days" value="wednsday"class="txt" <%=wedn%>> <%=messages.getString("calendar.days.wednesday") %><br></br>
                    <input type="checkbox" name="days" value="thursday"class="txt" <%=thur%>> <%=messages.getString("calendar.days.thursday") %><br></br>
                    <input type="checkbox" name="days" value="friday"class="txt" <%=frid%>> <%=messages.getString("calendar.days.friday") %><br></br>
                    <input type="checkbox" name="days" value="saturday"class="txt"<%=satu%>> <%=messages.getString("calendar.days.saturday") %><br></br>
                    <input type="checkbox" name="days" value="sunday"class="txt"<%=sund%>> <%=messages.getString("calendar.days.sunday") %><br></br>
      </li>
      <li>
        <label for="holiday"><%=messages.getString("calendar.field.holidays") %>:</label>
        <%=sAfterHtml%>
        <input class="regular_button_00" type="button" name="next" value="<%=messages.getString("button.add")%>" 
        	onClick="javascrip:addHoliday()"/>
      </li>

	<div class="table_inc">  
	<table class="item_list" id = "holidaysTable">
			<% for (int i = 0; i < feri.length; i++) { %>
				<%if(feri[i]!=null){%>	
				<tr class="tab_row_even" style="color:grey;" id="holidayItem_<%=i%>">				
					<td id="holidayDate_<%=i%>"><%=feri[i]%></td>
					</td>
				<td>
					<a onclick="javascript:removeHoliday('<%=i%>'); return false;">
					<img class="toolTipImg" src="images/icon_delete.png" border="0" title="<%=messages.getString("useradm.tooltip.deletedays")%>">
					</a>
				</td>
			</tr>
			<% } %>
		<% } %>
	</table>
	</div>
	
	<li><label for="dtafter"><%=messages.getString("calendar.field.periods") %>:</label></li>
	<li>	
        <label for="dtafter"><%=messages.getString("calendar.field.from") %>: </label>
        <select name="h" id="h" >
        <option selected="selected">HH</option>
        <option value="0">00</option>
        <option value="1">01</option>
        <option value="2">02</option>
        <option value="3">03</option>
        <option value="4">04</option>
        <option value="5">05</option>
        <option value="6">06</option>
        <option value="7">07</option>
        <option value="8">08</option>
        <option value="9">09</option>
        <option value="10">10</option>
        <option value="11">11</option>
        <option value="12">12</option>
        <option value="13">13</option>
        <option value="14">14</option>
        <option value="15">15</option>
        <option value="16">16</option>
        <option value="17">17</option>
        <option value="18">18</option>
        <option value="19">19</option>
        <option value="20">20</option>
        <option value="21">21</option>
        <option value="22">22</option>
        <option value="23">23</option>
      </select>
      <select name="m" id="m">
        <option selected="selected">MM</option>
        <option value="0">00</option>
        <option value="15">15</option>
        <option value="30">30</option>
        <option value="45">45</option>
        </select>
	 </li>
      <li>
      <label for="dtafter"><%=messages.getString("calendar.field.to") %>: </label>
        <select name="h1" id="h1">
        <option selected="selected">HH</option>
        <option value="0">00</option>
        <option value="1">01</option>
        <option value="2">02</option>
        <option value="3">03</option>
        <option value="4">04</option>
        <option value="5">05</option>
        <option value="6">06</option>
        <option value="7">07</option>
        <option value="8">08</option>
        <option value="9">09</option>
        <option value="10">10</option>
        <option value="11">11</option>
        <option value="12">12</option>
        <option value="13">13</option>
        <option value="14">14</option>
        <option value="15">15</option>
        <option value="16">16</option>
        <option value="17">17</option>
        <option value="18">18</option>
        <option value="19">19</option>
        <option value="20">20</option>
        <option value="21">21</option>
        <option value="22">22</option>
        <option value="23">23</option>
      </select>
      <select name="m1" id="m1">
        <option selected="selected">MM</option>
        <option value="0">00</option>
        <option value="15">15</option>
        <option value="30">30</option>
        <option value="45">45</option>
        </select>
     <input class="regular_button_00" type="button" name="next" value="<%=messages.getString("button.add")%>" 
        onClick="javascrip:validateTime();"/>   
      </li>
      <li>
      	<div class="table_inc">  
		<table class="item_list" id = "periodTable">
			<% for (int i = 0; i < peri.length; i++) { %>
				<%if(peri[i]!=null){%>	
			<tr class="tab_row_even" style="color:grey;" id="periodItem_<%=i%>">			
				<td id="periodVal_<%=i%>"><%=peri[i]%></td>
				<td>
					<a onclick="javascript:removePeriod('<%=i%>'); return false;">
					<img class="toolTipImg" src="images/icon_delete.png" border="0" title="<%=messages.getString("useradm.calendar.deleteperiods")%>">
					</a>
				</td>
			</tr>
			<%} %>
		<% } %>
	</table>
	</div>
	</li>
    </ol>
  </fieldset>
  
  <fieldset class="submit">
  
    <input class="regular_button_01" type="button" onclick="javascript:tabber_right(4, 'Admin/Resources/iflowcalendar.jsp');" value="Voltar" name="back">
    
    <input class="regular_button_01" type="button" name="add" value="<%=messages.getString("button.add")%>" 
      onClick="javascript:tabber_right(4, '<%=response.encodeURL("Admin/Resources/newcalendar.jsp")%>', getCalendParamsForSave());"/>  
  </fieldset>
</li>

</form>

package pt.iflow.servlets;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.presentation.DateUtility;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessHeader;
import pt.iflow.api.processdata.ProcessSimpleVariable;
import pt.iflow.api.processtype.DateDataType;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.ServletUtils;
import pt.iflow.api.utils.UserInfoInterface;

import com.google.gson.Gson;

/**
 * Servlet implementation class ActionServlet
 */

public class AjaxFormServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public AjaxFormServlet() {
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String varNewValue  = request.getParameter("varNewValue");		
		String varName  = request.getParameter("varName");
		Integer flowid  = Integer.parseInt(request.getParameter("flowid"));
		Integer pid  = Integer.parseInt(request.getParameter("pid"));
		Integer subpid  = Integer.parseInt(request.getParameter("subpid"));
		
		HttpSession session = request.getSession();
		UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
		if (userInfo == null)
			throw new NullPointerException();
		
		ProcessManager pm = BeanFactory.getProcessManagerBean();
	    Flow flow = BeanFactory.getFlowBean();
	    ProcessData procData = null;
	    String flowExecType = "";
	    //get procdata from SESSION
	    if (pid == Const.nSESSION_PID) {
	        subpid = Const.nSESSION_SUBPID; // reset subpid to session subpid
	        procData = (ProcessData)session.getAttribute(Const.SESSION_PROCESS + flowExecType);
	    }
	    else {
	    	ProcessHeader procHeader = new ProcessHeader(flowid, pid, subpid);
	        procData = pm.getProcessData(userInfo, procHeader, true?Const.nALL_PROCS:Const.nOPENED_PROCS);
        }	    	  
	    Block bBlockJSP = flow.getBlock(userInfo, procData);	    
	    HashMap<String, String> hmHidden = new HashMap<String, String>();
        hmHidden.put("subpid", "" + subpid);
        hmHidden.put("pid", "" + pid);
        hmHidden.put("flowid", "" + flowid);
        hmHidden.put(Const.FLOWEXECTYPE, flowExecType);
    	//hmHidden.put(Const.sMID_ATTRIBUTE, currMid);
        try {
			procData.parseAndSet(varName, varNewValue);			
		} catch (ParseException e) {
			try {
				Object o = DateUtility.parseFormDate(userInfo, varNewValue);
				ProcessSimpleVariable psv = new ProcessSimpleVariable(new DateDataType(), varName);
				psv.setValue(o);
				procData.set(psv);
			} catch (ParseException e1) {
				;
			}			
		} 
        
	    Object[] oa = new Object[4];
        oa[0] = userInfo;
        oa[1] = procData;
        oa[2] = hmHidden;
        oa[3] = new ServletUtils(response);
        // 2: generateForm
        String sHtmlNew = (String) bBlockJSP.execute(2, oa);
        String result =extractUpdatedFieldDivsSimple(sHtmlNew);
        
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(result);
	}
	
	private String extractUpdatedFieldDivsSimple(String sHtmlNew){
		try{
			Document doc = Jsoup.parse(sHtmlNew);
			Element newMain = doc.getElementById("main");						
			
			Gson gson = new Gson();
			String result =gson.toJson(newMain.html());
			
			return result;			
		} catch (Exception e){
			return "";
		}
	}
	
}
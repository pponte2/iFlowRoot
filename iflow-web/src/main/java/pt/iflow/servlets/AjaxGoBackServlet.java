package pt.iflow.servlets;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
import pt.iflow.services.IFlowRemote;

import com.google.gson.Gson;

/**
 * Servlet implementation class ActionServlet
 * Martelada CMA P13064-16
 */

public class AjaxGoBackServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public AjaxGoBackServlet() {
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Integer flowid  = null;
		Integer pid  = null;
		Integer subpid  = null;
		Integer _button_clicked_id = null;
		try{
			flowid  = Integer.parseInt(request.getParameter("flowid"));
			pid  = Integer.parseInt(request.getParameter("pid"));
			subpid  = Integer.parseInt(request.getParameter("subpid"));
			_button_clicked_id  = Integer.parseInt(request.getParameter("_button_clicked_id"));
		} catch(Exception e)
		{
			return;
		}
		HttpSession session = request.getSession();
		UserInfoInterface userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
		if (userInfo == null)
			throw new NullPointerException();
		
		ProcessManager pm = BeanFactory.getProcessManagerBean();
	    Flow flow = BeanFactory.getFlowBean();
	    ProcessData procData = null;
	    String flowExecType = "";
	    //get procdata from SESSION
	    Boolean procInSession = Boolean.FALSE;
	    if (pid == Const.nSESSION_PID) {
	        subpid = Const.nSESSION_SUBPID; // reset subpid to session subpid
	        procData = (ProcessData)session.getAttribute(Const.SESSION_PROCESS + flowExecType);
	    }
	    else {
	    	ProcessHeader procHeader = new ProcessHeader(flowid, pid, subpid);
	        procData = pm.getProcessData(userInfo, procHeader, true?Const.nALL_PROCS:Const.nOPENED_PROCS);
	        procInSession = Boolean.TRUE;
        }	    	  
	    Block bBlockJSP = flow.getBlock(userInfo, procData);	    
	    HashMap<String, String> hmHidden = new HashMap<String, String>();
        hmHidden.put("subpid", "" + subpid);
        hmHidden.put("pid", "" + pid);
        hmHidden.put("flowid", "" + flowid);
        hmHidden.put(Const.FLOWEXECTYPE, flowExecType);
    	//hmHidden.put(Const.sMID_ATTRIBUTE, currMid);
        
        Enumeration parameterNames = request.getParameterNames();
		while(parameterNames.hasMoreElements()){
			String varName  = parameterNames.nextElement().toString();
			String varNewValue  = request.getParameter(varName);					
			try {
				procData.parseAndSet(varName, varNewValue);			
			} catch (Exception e) {
				try {
					Object o = DateUtility.parseFormDate(userInfo, varNewValue);
					ProcessSimpleVariable psv = new ProcessSimpleVariable(new DateDataType(), varName);
					psv.setValue(o);
					procData.set(psv);
				} catch (Exception e1) {
					;
				}			
			} 
		}
		if (procInSession)
        	bBlockJSP.saveDataSet(userInfo, procData);
		
		IFlowRemote ir = new IFlowRemote();
		String formXml;
		try {
			formXml = ir.generateXmlForm(userInfo.getUserData().getUsername(), userInfo.getPasswordString(), flowid, pid, subpid);
			ir.processXmlForm(userInfo.getUserData().getUsername(), userInfo.getPasswordString(), flowid, pid, subpid, formXml, ""+_button_clicked_id, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// processForm
//		Object[] oa = new Object[5];
//		FormData fd =  new FormData();
//		fd.setParameter("flowExecType", "");
//		fd.setParameter("curmid", "96");
//		fd.setParameter("_custom_6", "Voltar");
//		fd.setParameter("_serv_filed_", "-1");
//		fd.setParameter("pid", "635");
//		fd.setParameter("op", "3");
//		fd.setParameter("0_MAX_ROW", "0");
//		fd.setParameter("_button_clicked_id", "6");
//		fd.setParameter("buttonResult", "return");
//		fd.setParameter("flowid", "122");
//		fd.setParameter("subpid", "1");
//		
//        oa[0] = userInfo;
//        oa[1] = procData;
//        oa[2] = fd;
//        oa[3] = new ServletUtils(request, response);
//        oa[4] = Boolean.TRUE;		
//        bBlockJSP.execute(3, oa);
//        
        // generateForm
        Object[] oa = new Object[4];
        oa[0] = userInfo;
        oa[1] = procData;
        oa[2] = hmHidden;
        oa[3] = new ServletUtils(response);
        String sHtmlNew = (String) bBlockJSP.execute(2, oa);
        
        String result = extractUpdatedFieldDivsSimple(sHtmlNew);             
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
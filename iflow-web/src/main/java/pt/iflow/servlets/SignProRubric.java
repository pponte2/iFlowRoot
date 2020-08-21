package pt.iflow.servlets;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cmaRepos.ReposWSDEV_PortType;
import cmaRepos.ReposWSDEV_ServiceLocator;
//import cmaRepos.ReposWSDEV_PortType;
//import cmaRepos.ReposWSDEV_ServiceLocator;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;
import pt.iknow.utils.html.FormFile;
import pt.iknow.utils.html.FormUtils;

@SuppressWarnings("serial")
public class SignProRubric  extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserInfoInterface userInfo = (UserInfoInterface) request.getSession().getAttribute(Const.USER_INFO);
		try{						
			if(userInfo==null){
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			
			ReposWSDEV_ServiceLocator locator = new ReposWSDEV_ServiceLocator();
			ReposWSDEV_PortType port = locator.getReposWSDEVPort();
			port.instanceRepos("cmauser", "15375900");
			byte[] rubric = port.getRubric(userInfo.getUtilizador());
			
			OutputStream out = response.getOutputStream();
		    out.write(rubric);
		    out.flush();
		    out.close(); 
		} catch (Exception e){
			//Logger.error(userInfo.getUtilizador(), this, "GET", "Error Ocorred.", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserInfoInterface userInfo = (UserInfoInterface) request.getSession().getAttribute(Const.USER_INFO);
		try{						
			if(userInfo==null){
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			
			FormData fdFormData = FormUtils.parseRequest(request, Const.nUPLOAD_THRESHOLD_SIZE, Const.nUPLOAD_MAX_SIZE, Const.fUPLOAD_TEMP_DIR);
			FormFile file = fdFormData.getFileParameter("file");
			
			ReposWSDEV_ServiceLocator locator = new ReposWSDEV_ServiceLocator();
			ReposWSDEV_PortType port = locator.getReposWSDEVPort();
			port.instanceRepos("cmauser", "15375900");
			port.deleteRubric(userInfo.getUtilizador());
			port.insertRubric(file.getFileName(), file.getData(), userInfo.getUtilizador());
			
			response.sendError(HttpServletResponse.SC_OK);
		} catch (Exception e){
			Logger.error(userInfo.getUtilizador(), this, "POST", "Error Ocorred.", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
}

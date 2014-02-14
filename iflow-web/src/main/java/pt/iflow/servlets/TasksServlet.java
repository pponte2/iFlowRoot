package pt.iflow.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

/**
 * Servlet implementation class for Servlet: HelpDialogServlet
 * 
 * @web.servlet name="HelpDialog"
 * 
 * @web.servlet-mapping url-pattern="/HelpDialog"
 */
public class TasksServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
  static final long serialVersionUID = 1L;

  private UserInfoInterface userInfo = null;
  private String flowid = "-1";
  private String pid = "-1";
  private HttpServletRequest request = null;

  private static final String METHOD_MARK_AS_READ = "markRead";

  private static final String PARAM_FOWLID = "flowid";
  private static final String PARAM_PID = "pid";
  private static final String PARAM_READ_FLAG = "readFlag";
  private static final String PARAM_USERID = "userid";

  public TasksServlet() {
    super();
  }

  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setCharacterEncoding("UTF-8");
    response.setContentType("text/html; charset=UTF-8");
    PrintWriter out = response.getWriter();

    getProcessData(request);
    
    if(userInfo == null) {
      out.print("session-expired");
      return;
    }
    
    // find what to do
    String method = request.getPathInfo();
    if (method == null) method = "";
    else if (method.indexOf("/") == 0) method = method.substring(1);

    if (METHOD_MARK_AS_READ.equals(method)) {
      try {
        String readFlag = request.getParameter(PARAM_READ_FLAG);
        ProcessManager pm = BeanFactory.getProcessManagerBean();
        pm.markActivityReadFlag(userInfo, flowid, pid, readFlag);
        out.print("");
      } catch (Exception e) {
        out.print("Error!");
        Logger.error(userInfo.getUtilizador(), this, "service", "Error Ocorred.", e);
      }
    } else {
      out.print("Method Unknown");
    }
  }    
  
  private void getProcessData(HttpServletRequest request) {
    try {
      flowid = request.getParameter(PARAM_FOWLID);
    } catch (Exception e) {}
    try {
      pid = request.getParameter(PARAM_PID);
    } catch (Exception e) {}
    HttpSession session = request.getSession();
    userInfo = (UserInfoInterface) session.getAttribute(Const.USER_INFO);
    this.request = request;
  }

}
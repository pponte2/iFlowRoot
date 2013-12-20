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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.ProcessManager;
import pt.iflow.api.flows.Flow;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.processdata.ProcessHeader;
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
        
        Object[] oa = new Object[4];
        oa[0] = userInfo;
        oa[1] = procData;
        oa[2] = hmHidden;
        oa[3] = new ServletUtils(response);
        // 2: generateForm
        String sHtmlOld = (String) bBlockJSP.execute(2, oa);
        try {
            procData.parseAndSet(varName, varNewValue);
        } catch (ParseException e) {}
        String sHtmlNew = (String) bBlockJSP.execute(2, oa);
        String result = extractUpdatedFieldDivs2(sHtmlOld, sHtmlNew);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(result);
    }
    
    private String extractUpdatedFieldDivs(String sHtmlOld, String sHtmlNew){
        try{
            Document doc = Jsoup.parse(sHtmlNew);
            Elements elsNew = doc.select("div.fieldDiv");
            doc = Jsoup.parse(sHtmlOld);
            Elements elsOld = doc.select("div.fieldDiv");
            ArrayList<DivContainer> updatedDivs = new ArrayList<AjaxFormServlet.DivContainer>();
            
            for(Element eN: elsNew){
                //unchanged field, add it and ignore
                if ((elsOld.contains(eN))){
                    updatedDivs.add(new DivContainer(eN.id(), "", "1"));
                    ;
                }
                //changed or new field, add it
                else{
                    updatedDivs.add(new DivContainer(eN.id(), eN.html(), "0"));
                    ;
                }
            }
            Gson gson = new Gson();
            String result =gson.toJson(updatedDivs);
            
            return result;          
        } catch (Exception e){
            return "";
        }
    }
    
    private String extractUpdatedFieldDivs2(String sHtmlOld, String sHtmlNew){
        try{
            ArrayList<BlockDivisionDiv> blocks = new ArrayList<AjaxFormServlet.BlockDivisionDiv>();
            //get all the old field divs to be able to compare content changes
            Document doc = Jsoup.parse(sHtmlOld);
            Elements elsOld = doc.select("div.fieldDiv");           
            
            doc = Jsoup.parse(sHtmlNew);
            Elements blockDivision = doc.select("div.blockdivision");
            for(Element bd: blockDivision){
                if(bd.select("div.submit").size()!=0)
                    break;
                
                BlockDivisionDiv auxBlock = new BlockDivisionDiv();
                Elements columnDivision = bd.select("div.columndivision");              
                
                for(Element cd:columnDivision){
                    ColumnDivisionDiv auxColumn = new ColumnDivisionDiv();
                    auxColumn.setStyle(cd.attr("style"));
                    Elements fieldDivision = cd.select("div.fieldDiv");
                    DivContainer auxDiv = null;                     
                    for(Element fd: fieldDivision){
                        auxDiv = new DivContainer(fd.id(), fd.html(), "0");
                        auxColumn.getFields().add(auxDiv);
                    }
                    auxBlock.getColumns().add(auxColumn);
                }
                                
                blocks.add(auxBlock);
            }
            
            Gson gson = new Gson();
            String result =gson.toJson(blocks);
            
            return result;          
        } catch (Exception e){
            return "";
        }
    }

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {        
    }

    class BlockDivisionDiv{
        List<ColumnDivisionDiv> columns;

        public List<ColumnDivisionDiv> getColumns() {
            return columns;
        }

        public void setColumns(List<ColumnDivisionDiv> columns) {
            this.columns = columns;
        }

        public BlockDivisionDiv(List<ColumnDivisionDiv> columns) {
            super();
            this.columns = columns;
        }       
        
        public BlockDivisionDiv() {
            super();
            columns = new ArrayList<ColumnDivisionDiv>();
        }
    }
    
    class ColumnDivisionDiv{
        List<DivContainer> fields;
        String style;

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        public List<DivContainer> getFields() {
            return fields;
        }

        public void setFields(List<DivContainer> fields) {
            this.fields = fields;
        }

        public ColumnDivisionDiv(List<DivContainer> fields, String style) {
            super();
            this.fields = fields;
            this.style = style;
        }
        
        public ColumnDivisionDiv() {
            super();
            fields = new ArrayList<DivContainer>();
        }
    }
    
    class DivContainer{
        String id;
        String content;
        String ignore;
        
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getContent() {
            return content;
        }
        public void setContent(String content) {
            this.content = content;
        }
        public String getIgnore() {
            return ignore;
        }
        public void setIgnore(String ignore) {
            this.ignore = ignore;
        }
        public DivContainer(String id, String content, String ignore) {
            super();
            this.id = id;
            this.content = content;
            this.ignore = ignore;
        }
        public DivContainer() {
            super();
        }               
    }
}
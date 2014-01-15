package pt.iflow.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;

import pt.iflow.api.audit.AuditData;
import pt.iflow.api.calendar.Calendar;
import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.CalendarManager;
import pt.iflow.api.core.ReportManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.transition.ReportTO;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;

/**
 * @author Luis Cabral
 * @version 18.02.2009
 */
public class ReportManagerBean implements ReportManager {

  private static ReportManagerBean instance = null;

  /**
   * This is a singleton.
   */
  private ReportManagerBean() {
  }

  /**
   * Retrieve this Singleton's instance, or create one if it is the first
   * invocation.
   * 
   * @return An instance of this class.
   */
  public static ReportManagerBean getInstance() {
    if (instance == null) {
      instance = new ReportManagerBean();
    }
    return instance;
  }

  /**
   * @see pt.iflow.api.core.ReportManager#storeReport(pt.iflow.api.utils.UserInfoInterface,
   *      pt.iflow.api.processdata.ProcessData,
   *      pt.iflow.api.transition.ReportTO)
   */
  public void storeReport(UserInfoInterface userInfo, ProcessData procData, ReportTO report) {
    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      st = db.createStatement();
      StringBuffer sql = new StringBuffer();

      //Lidar com casos em que é para fazer insert de relatorios com o mesmo codigo
      if(report.getInsert() && report.getStopReporting() == null && !containsOpen(getProcessReports(userInfo, procData), report))
          sql = buildQuerySqlInsert(report);
      else{
          if (contains(getProcessReports(userInfo, procData), report)) {
            sql = buildQuerySqlUpdate(report);
          } else {
            sql = buildQuerySqlInsert(report);
          }
      }

      if (Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), this, "storeReport", "QUERY=" + sql);
      }

      if (StringUtils.isNotBlank(sql.toString())) {
        st.executeUpdate(sql.toString());
      }
      
      if(report.getStopReporting() != null)
        updateReportDifference(userInfo, procData.getFlowId(), procData.getPid(), procData.getSubPid(), report.getStartReporting(), report.getStopReporting(), report.getCodReporting());
         
    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "storeReport", "caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
  }

  
  public void updateReportDifference(UserInfoInterface userInfo, int flowid, int pid, int subpid, Timestamp start, Timestamp stop, String cod) {
    CalendarManager cm = BeanFactory.getCalendarManagerBean();
    Calendar cal = cm.getFlowCalendar(userInfo, flowid);
    int calendarid = 0;
    long total = 0;
    //String stotal = "";
    
    if(cal != null){
      calendarid = cal.getCalendar_id();
      total = Utils.getDurationCalendar(start, stop, cal, userInfo);      
    }else{
      total = stop.getTime() - start.getTime();
    }
    
    //stotal = Utils.getDuration(start, stop, flowid, userInfo);
    
    String sql = "update reporting set calendarid = "+calendarid+" , time_reporting = '"+total+"' where flowid = "+flowid;
    sql += " and pid = "+pid+" and subpid = "+subpid+" and stop_reporting is not null";
    sql += " and cod_reporting = '"+cod+"'";
    
    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    ResultSet rs = null;
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      st = db.createStatement();   
      if (Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), this, "updateReportDifference", "QUERY=" + sql);
      }
      if (StringUtils.isNotBlank(sql.toString())) {
        st.executeUpdate(sql);
      }
    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "updateReportDifference", "caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      DatabaseInterface.closeResources(db, st, rs);
    }
  }
  
  
  
  public long getLastDifference(int flowid, int pid, int subpid) {
    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    Timestamp startReporting = null;
    long total = 0;
    
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      StringBuffer sql = new StringBuffer();
      sql.append("SELECT start_reporting FROM reporting");
      sql.append(" WHERE flowid = " + flowid);
      sql.append(" AND pid = " + pid);
      sql.append(" AND subpid = " + subpid);
      sql.append(" AND stop_reporting is null ");

      if (Logger.isDebugEnabled()) {
        Logger.debug("iFlow Editor", this, "getLastDifference", "QUERY=" + sql);
      }

      pst = db.prepareStatement(sql.toString());
      rs = pst.executeQuery();
      while (rs.next()) {
        startReporting = rs.getTimestamp("start_reporting");
      }
    } catch (SQLException sqle) {
      Logger.error("iFlow Editor", this, "getLastDifference", "caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }
    
    if(startReporting != null){
        java.util.Calendar capturedMoment = java.util.Calendar.getInstance();
        Timestamp current = new Timestamp(capturedMoment.getTimeInMillis());
        
        UserInfoInterface userInfo = BeanFactory.getUserInfoFactory().newGuestUserInfo();
        
        CalendarManager cm = BeanFactory.getCalendarManagerBean();
        Calendar cal = cm.getFlowCalendar(userInfo, flowid);
       
        if(cal != null){
          total = Utils.getDurationCalendar(startReporting, current, cal, userInfo);      
        }else{
          total = current.getTime() - startReporting.getTime();
        }
    } 
    
    return total;
  }
  
  /**
   * @see pt.iflow.api.core.ReportManager#getProcessReports(pt.iflow.api.utils.UserInfoInterface,
   *      pt.iflow.api.processdata.ProcessData)
   */
  public List<ReportTO> getProcessReports(UserInfoInterface userInfo, ProcessData procData) {
    List<ReportTO> reports = new ArrayList<ReportTO>();
    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      StringBuffer sql = new StringBuffer();
      sql.append("SELECT * FROM " + ReportTO.TABLE_NAME);
      sql.append(" WHERE " + ReportTO.FLOWID + "=?");
      sql.append(" AND " + ReportTO.PID + "=?");
      sql.append(" AND " + ReportTO.SUBPID + "=?");
      sql.append(" ORDER BY " + ReportTO.START_REPORTING);

      if (Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), this, "getProcessReports", "QUERY=" + sql);
      }

      pst = db.prepareStatement(sql.toString());
      pst.setInt(1, procData.getFlowId());
      pst.setInt(2, procData.getPid());
      pst.setInt(3, procData.getSubPid());
      rs = pst.executeQuery();
      while (rs.next()) {
        int flowId = rs.getInt(ReportTO.FLOWID);
        int pid = rs.getInt(ReportTO.PID);
        int subpid = rs.getInt(ReportTO.SUBPID);
        String codReporting = rs.getString(ReportTO.COD_REPORTING);
        Timestamp startReporting = rs.getTimestamp(ReportTO.START_REPORTING);
        Timestamp stopReporting = rs.getTimestamp(ReportTO.STOP_REPORTING);
        Timestamp ttl = rs.getTimestamp(ReportTO.TTL);
        int active = rs.getInt(ReportTO.ACTIVE);
        reports.add(new ReportTO(flowId, pid, subpid, codReporting, startReporting, stopReporting, ttl, active));
      }
    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "getProcessReports", "caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }
    return reports;
  }

  /**
   * @see pt.iflow.api.core.ReportManager#getFlowReports(pt.iflow.api.utils.UserInfoInterface,
   *      int, boolean)
   */
  public AuditData[] getFlowReports(UserInfoInterface userInfo, int flowid, boolean includeOpen , Date[] interval) {
    if (Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, "getFlowReports", "entered with flowid=" + flowid);
    }

    List<AuditData> result = new ArrayList<AuditData>();
    for (String codReporting : getReportCodes(userInfo, flowid, includeOpen,interval)) {
      result.add(getReportAverage(userInfo, flowid, codReporting, includeOpen,interval));
    }

    if (Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, "getFlowReports", "leaving with result=" + result);
    }

    return result.toArray(new AuditData[result.size()]);
  }

  /**
   * @see pt.iflow.api.core.ReportManager#getFlowTTLReports(pt.iflow.api.utils.UserInfoInterface,
   *      int, boolean)
   */
  public OrderedMap<String, AuditData[]> getFlowTTLReports(UserInfoInterface userInfo, int flowid, boolean includeOpen, Date[] interval) {
    if (Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, "getFlowTTLReports", "entered with flowid=" + flowid);
    }

    OrderedMap<String, AuditData[]> result = new ListOrderedMap<String, AuditData[]>();
    for (String codReporting : getReportCodes(userInfo, flowid, includeOpen,interval)) {
      result.put(codReporting, getReportTTL(userInfo, flowid, codReporting, includeOpen ,  interval));
    }

    if (Logger.isDebugEnabled()) {
      Logger.debug(userInfo.getUtilizador(), this, "getFlowTTLReports", "leaving with result=" + result);
    }

    return result;
  }

  private List<String> getReportCodes(UserInfoInterface userInfo, int flowid, boolean includeOpen,Date[] interval) {
    List<String> codes = new ArrayList<String>();
    
    Date startDate = interval[0];
    Date endDate = interval[1];
    
    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();

      StringBuffer sql = new StringBuffer();
      sql.append("SELECT rep." + ReportTO.COD_REPORTING);
      sql.append(" FROM " + ReportTO.TABLE_NAME + " rep");
      sql.append(" WHERE rep." + ReportTO.FLOWID + "=?");
          if (includeOpen) 
            sql.append(" AND rep.start_reporting > ? AND ( rep.stop_reporting < ? or rep.active = 1)");
              else
                sql.append(" AND rep.start_reporting > ? AND rep.stop_reporting < ?"); 
      sql.append(" ORDER BY rep." + ReportTO.START_REPORTING);


      pst = db.prepareStatement(sql.toString());
      pst.setInt(1, flowid);
      pst.setTimestamp(2,  new Timestamp(startDate.getTime()));
      pst.setTimestamp(3,  new Timestamp(endDate.getTime()));
      rs = pst.executeQuery();
      while (rs.next()) {
        String code = rs.getString(ReportTO.COD_REPORTING);
        if (!codes.contains(code)) {
          codes.add(code);
        }
      }

    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "getFlowTTLReports", "caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }
    return codes;
  }

  private AuditData getReportAverage(UserInfoInterface userInfo, int flowid, String codReporting, boolean includeOpen,Date[] interval) {
    AuditData data = new AuditData(codReporting, "0");

    Date startDate = interval[0];
    Date endDate = interval[1];
    
    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();

      StringBuffer sql = new StringBuffer();
      sql.append("SELECT rep." + ReportTO.START_REPORTING);
      sql.append(", rep." + ReportTO.STOP_REPORTING);
      sql.append(" FROM " + ReportTO.TABLE_NAME + " rep");
      sql.append(" WHERE rep." + ReportTO.FLOWID + "=?");
      sql.append(" AND rep." + ReportTO.COD_REPORTING + " LIKE ?");
          if (includeOpen) 
            sql.append(" AND rep.start_reporting > ? AND ( rep.stop_reporting < ? or rep.active = 1)");
            else
              sql.append(" AND rep.start_reporting > ? AND rep.stop_reporting < ?");    
      sql.append(" ORDER BY rep." + ReportTO.START_REPORTING);

      if (Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), this, "getFlowReports", "QUERY=" + sql);
      }

      pst = db.prepareStatement(sql.toString());
      pst.setInt(1, flowid);
      pst.setString(2, codReporting);
      pst.setTimestamp(3,  new Timestamp(startDate.getTime()));
      pst.setTimestamp(4,  new Timestamp(endDate.getTime()));

      rs = pst.executeQuery();
      int counter = 0;
      while (rs.next()) {
        Timestamp start = rs.getTimestamp(ReportTO.START_REPORTING);
        Timestamp end = rs.getTimestamp(ReportTO.STOP_REPORTING);
       
        if (end == null) 
          end = new Timestamp(endDate.getTime());
        
        //long value = end.getTime() - start.getTime();
        long value = Utils.getDurationByCalendar(start, end, flowid, userInfo);
        data.setValue("" + (Long.parseLong(data.getValue()) + value));
        counter++;
      }
      data.setValue("" + (Long.parseLong(data.getValue()) / counter));
    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "getFlowReports", "caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }
    return data;
  }

  private AuditData[] getReportTTL(UserInfoInterface userInfo, int flowid, String codReporting, boolean includeOpen,Date[] interval) {
    List<AuditData> result = new ArrayList<AuditData>();

    Date startDate = interval[0];
    Date endDate = interval[1];
    
    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();

      StringBuffer sql = new StringBuffer();

      sql.append("SELECT rep." + ReportTO.STOP_REPORTING);
      sql.append(", rep." + ReportTO.TTL);
      sql.append(" FROM " + ReportTO.TABLE_NAME + " rep");
      sql.append(" WHERE rep." + ReportTO.FLOWID + "=?");
      sql.append(" AND rep." + ReportTO.COD_REPORTING + " LIKE ?");
            if (includeOpen) 
              sql.append(" AND rep.start_reporting > ? AND ( rep.stop_reporting < ? or rep.active = 1)");
                else
                  sql.append(" AND rep.start_reporting > ? AND rep.stop_reporting < ?");
      sql.append(" ORDER BY rep." + ReportTO.START_REPORTING);

      if (Logger.isDebugEnabled()) {
        Logger.debug(userInfo.getUtilizador(), this, "getFlowReports", "QUERY=" + sql);
      }

      pst = db.prepareStatement(sql.toString());
      pst.setInt(1, flowid);
      pst.setString(2, codReporting);
      pst.setTimestamp(3,  new Timestamp(startDate.getTime()));
      pst.setTimestamp(4,  new Timestamp(endDate.getTime()));
      rs = pst.executeQuery();

      AuditData green = new AuditData(codReporting, "0");
      AuditData red = new AuditData(codReporting, "0");
      while (rs.next()) {
        Timestamp end = rs.getTimestamp(ReportTO.STOP_REPORTING);
        Timestamp ttl = rs.getTimestamp(ReportTO.TTL);
        if (ttl != null) {
          if (end == null) {
            end = new Timestamp(endDate.getTime());
          }
          if (end.after(ttl)) {
            red.setValue("" + (Integer.parseInt(red.getValue()) + 1));
          } else {
            green.setValue("" + (Integer.parseInt(green.getValue()) + 1));
          }
        }
      }
      result.add(green);
      result.add(red);
    } catch (SQLException sqle) {
      Logger.error(userInfo.getUtilizador(), this, "getFlowReports", "caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }
    return result.toArray(new AuditData[result.size()]);
  }

  private boolean contains(List<ReportTO> reports, ReportTO report) {
    for (ReportTO item : reports) {
      if (item.getFlowId() == report.getFlowId() && item.getPid() == report.getPid() && item.getSubpid() == report.getSubpid()
          && StringUtils.equals(item.getCodReporting(), report.getCodReporting())) {
        return true;
      }
    }
    return false;
  }
  
  private boolean containsOpen(List<ReportTO> reports, ReportTO report) {
    for (ReportTO item : reports) {
      if (item.getFlowId() == report.getFlowId() && item.getPid() == report.getPid() && item.getSubpid() == report.getSubpid()
          && StringUtils.equals(item.getCodReporting(), report.getCodReporting()) && item.getStopReporting() == null) {
        return true;
      }
    }
    return false;
  }
  
  private StringBuffer buildQuerySqlUpdate(ReportTO report) {
    StringBuffer sql = new StringBuffer("UPDATE " + ReportTO.TABLE_NAME + " SET ");
    for (String column : ReportTO.TABLE_COLUMNS) {
      if (StringUtils.equals(column, ReportTO.TABLE_COLUMNS[0])) {
        sql.append(column + "=" + report.getValueOf(column));
        continue;
      }
      sql.append("," + column + "=" + report.getValueOf(column));
    }
    sql.append(" WHERE " + ReportTO.FLOWID + "=" + report.getFlowId());
    sql.append(" AND " + ReportTO.PID + "=" + report.getPid());
    sql.append(" AND " + ReportTO.SUBPID + "=" + report.getSubpid());
    sql.append(" AND " + ReportTO.COD_REPORTING + " LIKE " + report.getValueOf(ReportTO.COD_REPORTING));
    sql.append(" AND start_reporting = "+ report.getValueOf(ReportTO.START_REPORTING));
    return sql;
  }

  private StringBuffer buildQuerySqlInsert(ReportTO report) {
    StringBuffer sql = new StringBuffer("INSERT INTO " + ReportTO.TABLE_NAME + " (");
    for (String column : ReportTO.TABLE_COLUMNS) {
      if (StringUtils.equals(column, ReportTO.TABLE_COLUMNS[0])) {
        sql.append(column);
        continue;
      }
      sql.append("," + column);
    }
    sql.append(") VALUES (");
    for (String column : ReportTO.TABLE_COLUMNS) {
      if (StringUtils.equals(column, ReportTO.TABLE_COLUMNS[0])) {
        sql.append(report.getValueOf(column));
        continue;
      }
      sql.append("," + report.getValueOf(column));
    }
    sql.append(")");
    return sql;
  }



  public void updateReport(int flowid, int pid, int subpid, String codigo) {
    DataSource ds = null;
    Connection db = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    Timestamp startReporting = null;
    long total = 0;
    int calid = 0;
    UserInfoInterface userInfo = BeanFactory.getUserInfoFactory().newGuestUserInfo();
    StringBuffer sql = new StringBuffer();
    java.util.Calendar capturedMoment = java.util.Calendar.getInstance();
    Timestamp current = new Timestamp(capturedMoment.getTimeInMillis());
    
    //Calculate difference
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();

      sql.append("SELECT start_reporting FROM reporting");
      sql.append(" WHERE flowid = " + flowid);
      sql.append(" AND pid = " + pid);
      sql.append(" AND subpid = " + subpid);
      sql.append(" AND stop_reporting is null");

      if (Logger.isDebugEnabled()) {
        Logger.debug("iFlow Editor", this, "Block BeanShell Update Report", "QUERY=" + sql);
      }

      pst = db.prepareStatement(sql.toString());
      rs = pst.executeQuery();
      while (rs.next()) {
        startReporting = rs.getTimestamp("start_reporting");
      }
    } catch (SQLException sqle) {
      Logger.error("iFlow Editor", this, "Block BeanShell Update Report", "caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }
    
    if(startReporting != null){

        CalendarManager cm = BeanFactory.getCalendarManagerBean();
        Calendar cal = cm.getFlowCalendar(userInfo, flowid);
       
        if(cal != null){
          calid = cal.getCalendar_id();
          total = Utils.getDurationCalendar(startReporting, current, cal, userInfo);      
        }else{
          total = current.getTime() - startReporting.getTime();
        }
    } 
    
    //Update old report
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      
      sql = new StringBuffer();
      sql.append("UPDATE reporting");
      sql.append(" SET stop_reporting = '"+current+"'");
      sql.append(" , calendarid = "+calid);
      sql.append(" , time_reporting = "+total);
      sql.append(" WHERE flowid = " + flowid);
      sql.append(" AND pid = " + pid);
      sql.append(" AND subpid = " + subpid);
      sql.append(" AND stop_reporting is null ");
      

      if (Logger.isDebugEnabled()) {
        Logger.debug("iFlow Editor", this, "Block BeanShell Update Report", "QUERY=" + sql);
      }
      if (StringUtils.isNotBlank(sql.toString())) {
        pst = db.prepareStatement(sql.toString());
        pst.executeUpdate();
      }
    } catch (SQLException sqle) {
      Logger.error("iFlow Editor", this, "Block BeanShell Update Report", "caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }
    

    //Insert new Report
    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      
      sql = new StringBuffer();
      sql.append("insert into reporting (flowid,pid,subpid,cod_reporting,start_reporting,active) ");
      sql.append("values ("+flowid+","+pid+","+subpid+",'"+codigo+"','"+current+"',1) ");     

      if (Logger.isDebugEnabled()) {
        Logger.debug("iFlow Editor", this, "Block BeanShell Update Report", "QUERY=" + sql);
      }
      if (StringUtils.isNotBlank(sql.toString())) {
        pst = db.prepareStatement(sql.toString());
        pst.execute();
      }
    } catch (SQLException sqle) {
      Logger.error("iFlow Editor", this, "Block BeanShell Update Report", "caught sql exception: " + sqle.getMessage(), sqle);
    } finally {
      DatabaseInterface.closeResources(db, pst, rs);
    }
  }




}

package pt.iflow.blocks;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.blocks.Port;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.Utils;


/**
 * <p>Title: BlockSQLUpdate</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class BlockSQLUpdate extends BlockSQL {
	
  private static final String advancedQuery = "advancedQuery";	
  private static final String sTransform = "TRANSFORMTODB";
  
  public BlockSQLUpdate(int anFlowId,int id, int subflowblockid, String filename) {
    super(anFlowId,id, subflowblockid, filename);
    hasInteraction = false;
    saveFlowState = false;
  }

  /**
   * Executes the block main action
   *
   * @param dataSet a value of type 'DataSet'
   * @return the port to go to the next block
   */
  @SuppressWarnings("deprecation")
  public Port after(UserInfoInterface userInfo, ProcessData procData) {
    Port outPort = portSuccess;
    StringBuffer logMsg = new StringBuffer();
    String login = userInfo.getUtilizador();
    
    DataSource ds = null;
    Connection db = null;
    Statement st = null;
    StringBuffer sbQuery = null;

    String sDataSource = null;
    String sTable = null;
    String sSet = null;
    String sWhere = null;
    String sQuery = null;
    String transform = null;
    
    try{
      transform = this.getAttribute(sTransform);
    }catch (Exception e) {
      transform = "true";
    }
    
    try{
    	sQuery = this.getAttribute(advancedQuery);
    	 if (StringUtils.isNotEmpty(sQuery)) {
    	   if(!StringUtils.isNotEmpty(transform) || transform.equals("true")){
    		 sQuery = Utils.transformStringAndPrepareForDB(userInfo, sQuery, procData);
    	   }else{
    	     sQuery = procData.transform(userInfo, sQuery, true);
    	   }
    	 }
         if (StringUtils.isEmpty(sQuery)) sQuery = null;
    }
    catch (Exception e) {
    	sQuery = null;
    }
    
    try {
      sDataSource = this.getAttribute(BlockSQL.sDATASOURCE);
      if (StringUtils.isNotEmpty(sDataSource)) {
        sDataSource = procData.transform(userInfo, sDataSource, true);
      }
      if (StringUtils.isEmpty(sDataSource)) sDataSource = null;
    }
    catch (Exception e) {
      sDataSource = null;
    }
    
    if (StringUtils.isEmpty(sDataSource)) {
      Logger.warning(login,this,"after", 
          procData.getSignature(this.getId()) + "NO DATASOURCE SPECIFIED !!!!!!!!!!!");
    }
    
   
    if(sQuery == null) {    
    	try {
    		sTable = this.getAttribute(BlockSQL.sTABLE);
    		if (StringUtils.isNotEmpty(sTable)) {
    			sTable = procData.transform(userInfo, sTable, true);
    		}
    		if (sTable.equals("")) sTable = null;
    	}
    	catch (Exception e) {
    		sTable = null;
    	}
    	try {
    		sSet = this.getAttribute(BlockSQL.sSET);
    		
            if(!StringUtils.isNotEmpty(transform) || transform.equals("true")){
              sSet = Utils.transformStringAndPrepareForDB(userInfo, sSet, procData);
            }else{
              sSet = procData.transform(userInfo, sSet, true);
            }

    	}
    	catch (Exception e) {
    		sSet = null;
    	}
    	try {
    		sWhere = this.getAttribute(BlockSQL.sWHERE);
    		if (StringUtils.isNotEmpty(sWhere)) {   			
                if(!StringUtils.isNotEmpty(transform) || transform.equals("true")){
                  sWhere = Utils.transformStringAndPrepareForDB(userInfo, sWhere, procData);
                }else{
                  sWhere = procData.transform(userInfo, sWhere, true);
                }
    			
    		}
    		if (sWhere.equals("")) sWhere = null;
    	}
    	catch (Exception e) {
          Logger.error(login, this, "after", procData.getSignature(this.getId()) + "Error in where: '"+ sWhere +"'");
          outPort = portError;
          sWhere = null;
    	}

    	if (sTable == null || sSet == null) {
          Logger.error(login, this, "after", procData.getSignature(this.getId()) + "Empty table or set");            
          outPort = portError;
    	} else {
    		if (this.isSystemTable(sDataSource, sTable)) {
    		  Logger.error(login, this, "after", procData.getSignature(this.getId()) + "Table '"+ sTable +"'is a system table");
    			outPort = portError;
    		} else {
    			if (sWhere == null) {
    				Logger.warning(login,this,"after","Where clause is empty... updating all table!!!");
    			}

    			List<String> alSet = null;
    			alSet = tokenize(sSet);

    			sbQuery = new StringBuffer("update ");
    			sbQuery.append(sTable);
    			sbQuery.append(" set ");
    			for (int i=0; i < alSet.size(); i++) {
    			  if (i > 0) {
    			    sbQuery.append(",");
    			  }
    			  sbQuery.append((String)alSet.get(i));
    			}
    			if (sWhere != null) {
    			  sbQuery.append(" where ").append(sWhere);
    			}
    			
    			sQuery = sbQuery.toString();
    		}
    	}
    }
    if (outPort != portError) {
      if (StringUtils.isEmpty(sQuery)) {
        Logger.error(login, this, "after", procData.getSignature(this.getId()) + "Empty query");
        outPort = portError;
      }
      else {
        try {
          ds = Utils.getUserDataSource(sDataSource);
          if (null == ds) {
            Logger.error(login, this, "after", procData.getSignature(this.getId()) + "null datasource for " + sDataSource);
            outPort = portError;
          } else {
            db = ds.getConnection();
            st = db.createStatement();

            // insert
            Logger.debug(login,this,"after","Going to execute update: " + sQuery);
            int nCols = st.executeUpdate(sQuery);
            Logger.debug(login,this,"after","Number of updated columns = " + nCols);

            if (nCols == 0) {
              outPort = portEmpty;
            }
          }
        } catch (SQLException sqle) {
          Logger.error(login,this,"after","caught sql exception: " + sqle.getMessage(), sqle);
          outPort = portError;
        } catch (Exception e) {
          Logger.error(login,this,"after","caught exception: " + e.getMessage(), e);
          outPort = portError;
        } finally {
          DatabaseInterface.closeResources(db,st);
        }
      }
    }
    
 
    logMsg.append("Using '" + outPort.getName() + "';");
    Logger.logFlowState(userInfo, procData, this, logMsg.toString());
    return outPort;
  }

  public String getDescription (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, true, "SQL Update");
  }

  public String getResult (UserInfoInterface userInfo, ProcessData procData) {
    return this.getDesc(userInfo, procData, false, "SQL Update Efectuado");
  }
}

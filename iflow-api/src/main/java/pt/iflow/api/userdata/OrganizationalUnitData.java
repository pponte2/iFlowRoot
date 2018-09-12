/*
 *
 * Created on May 16, 2005 by iKnow
 *
  */

package pt.iflow.api.userdata;

import java.io.Serializable;


/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2005 iKnow</p>
 * 
 * @author iKnow
 */

public interface OrganizationalUnitData extends IMappedData , Serializable {
 
  public static final String UNITCODE = "UNITCODE";
  public static final String NAME = "NAME";
  public static final String ORGNAME = "ORGNAME";
  public static final String MANAGER = "MANAGER";
  
}

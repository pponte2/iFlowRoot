package pt.iflow.api.core;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

import pt.iflow.api.calendar.Calendar;
import pt.iflow.api.utils.UserInfoInterface;



/**
 * Remote interface for ProcessManager.
 * 
 */
public interface CalendarManager {

  public Calendar getFlowCalendar(UserInfoInterface userInfo, int flowid);
  public ArrayList<Timestamp> getHolidaysCalendar(UserInfoInterface userInfo, int calendar_id, Timestamp tsStart, Timestamp tsStop);
  public ArrayList<Time> getPeriodsCalendar(UserInfoInterface userInfo, int calendar_id);
}

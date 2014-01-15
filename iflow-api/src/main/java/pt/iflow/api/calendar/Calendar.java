package pt.iflow.api.calendar;

import java.util.ArrayList;



public class Calendar {

    int id;
    int version;
    String name;
    Boolean sunday;
    Boolean monday;
    Boolean tuesday;
    Boolean wednesday;
    Boolean thursday;
    Boolean friday;
    Boolean saturday;
    int day_hours;
  
    
    public Calendar(int id, int version, String name, Boolean sunday, Boolean monday, Boolean tuesday, Boolean wednesday, Boolean thursday, Boolean friday, Boolean saturday, int day_hours) {
          this.id = id;
          this.version = version;
          this.name = name;
          this.sunday = sunday;
          this.monday = monday;
          this.tuesday = tuesday;
          this.wednesday = wednesday;
          this.thursday = thursday;
          this.friday = friday;
          this.saturday = saturday;
          this.day_hours = day_hours;
    }
    
    public Calendar(){
      this.id = 0;
      this.version = 0;
      this.name = "";
      this.sunday = false;
      this.monday = false;
      this.tuesday = false;
      this.wednesday = false;
      this.thursday = false;
      this.friday = false;
      this.saturday = false;
      this.day_hours = 0;
    }

    //GET
    public int getCalendar_id(){
        return id;
    }
    
       public int getDay_hours(){
            return day_hours;
        }
    
    public ArrayList<Integer> getDaysNotToCount(){
      ArrayList<Integer> weekdays = new ArrayList<Integer>(); 
          if(!sunday)
              weekdays.add(1);
          if(!monday)
              weekdays.add(2);
          if(!tuesday)
              weekdays.add(3);
          if(!wednesday)
              weekdays.add(4);
          if(!thursday)
              weekdays.add(5);
          if(!friday)
              weekdays.add(6);
          if(!saturday)
              weekdays.add(7);    
      return weekdays;    
    }
    
    
    public Boolean isSunday(){
        return sunday;
    }
    public Boolean isMonday(){
      return monday;
    }
    public Boolean isTuesday(){
      return tuesday;
    }
    public Boolean isWednesday(){
      return wednesday;
    }
    public Boolean isThursday(){
      return thursday;
    }
    public Boolean isFriday(){
      return friday;
    }
    public Boolean isSaturday(){
      return saturday;
    }
    
    //SET
    
    public void setId(int id){
      this.id = id;
    }
    
    public void setDayHours(int day_hours){
      this.day_hours = day_hours;
    }
    
    public void setSunday(int flag){
      if(flag == 1) sunday = true;
      else          sunday = false;
    }   
    public void setMonday(int flag){
      if(flag == 1) monday = true;
      else          monday = false;
    }
    public void setTuesday(int flag){
      if(flag == 1) tuesday = true;
      else          tuesday = false;
    }
    public void setWednesday(int flag){
      if(flag == 1) wednesday = true;
      else          wednesday = false;
    }
    public void setThursday(int flag){
      if(flag == 1) thursday = true;
      else          thursday = false;
    }
    public void setFriday(int flag){
      if(flag == 1) friday = true;
      else          friday = false;
    }
    public void setSaturday(int flag){
      if(flag == 1) saturday = true;
      else          saturday = false;
    }
    
}

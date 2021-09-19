package com.example.tourmate.model_class;


import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ProjectUtils {

    public static String getDateTime(){

          Calendar calendar=Calendar.getInstance();
          // SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd:MMM:yyyy hh:mm a");
          // SimpleDateFormat simpleDateFormat2=new SimpleDateFormat("dd:MM:yyyy hh:mm a");
          SimpleDateFormat simpleDateFormat2=new SimpleDateFormat("dd-MM-yyyy hh:mm a");
          String dateTime=simpleDateFormat2.format(calendar.getTime());
          return dateTime;
      }




}

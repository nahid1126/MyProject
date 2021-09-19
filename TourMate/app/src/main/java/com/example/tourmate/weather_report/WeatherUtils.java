package com.example.tourmate.weather_report;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class WeatherUtils {

    public static final String API_KEY="dfcd19c76c02e3f667646b5ce3f3dd19";



    public static class Icon{

        public static final String ICON_PREFIX="https://openweathermap.org/img/wn/";
        public static final String ICON_SUFFIX="@2x.png";

    }


    public static String convertDtToDateString(Long dt) {

        long dtMills=dt*1000;
        Date date =new Date(dtMills);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(date);
    }


    public static String convertTimeToTimeString(Long time) {

        long dtMills=time*1000;
        Date date =new Date(dtMills);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm a");
        return simpleDateFormat.format(date);
    }
    public static String getDateTime(){

        Calendar calendar=Calendar.getInstance();
        // SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd:MMM:yyyy hh:mm a");
        // SimpleDateFormat simpleDateFormat2=new SimpleDateFormat("dd:MM:yyyy hh:mm a");
        SimpleDateFormat simpleDateFormat2=new SimpleDateFormat("EEEE  dd-MMM-yyyy hh:mm a");
        String dateTime=simpleDateFormat2.format(calendar.getTime());
        return dateTime;
    }

    public static String getForcastDate(Long dt) {

        long dtMills=dt*1000;
        Date date =new Date(dtMills);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("EEEE dd/MM/yyyy hh:mm a");
        return simpleDateFormat.format(date);
    }



}

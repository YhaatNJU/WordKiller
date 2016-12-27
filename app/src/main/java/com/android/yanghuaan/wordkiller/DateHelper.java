package com.android.yanghuaan.wordkiller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by YangHuaan on 2016/12/22.
 */

public class DateHelper {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date getDate(String dateString){
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            Date date = sdf.parse(dateString);
            return date;
        }catch (ParseException pe){
            pe.printStackTrace();
        }
        return null;
    }

    public static String getString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String resultString = sdf.format(date);
        return resultString;
    }

    public static int getPastDay(Date fromDate, Date toDate){

        long from = fromDate.getTime();
        long to = toDate.getTime();

        return (int) ((to - from) / (24 * 60 * 60 * 1000));
    }

    public static int getPastHour(Date fromDate, Date toDate){
        long from = fromDate.getTime();
        long to = toDate.getTime();

        return (int) ((to - from) / (60 * 60 * 1000));
    }
}

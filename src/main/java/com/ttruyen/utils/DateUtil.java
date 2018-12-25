package com.ttruyen.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static java.sql.Timestamp createDateTimestamp(java.util.Date date) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(date);
        return new java.sql.Timestamp((calendar.getTime()).getTime());
    }

    /**
     * Alert time with current date
     *
     * @param input HH:mm
     * @return true if time input = time current
     */
    public static boolean alertTime(String input) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return StringUtils.equals(dateFormat.format(new Date()), input);
    }

    public static void main(String[] args) {
        System.out.println(alertTime("15:37"));
    }
}

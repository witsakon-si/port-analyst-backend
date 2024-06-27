package com.mport.domain.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateTimeUtil {

    public static Date now() {
        return Date.from(Instant.now());
    }

    public static Date today() {
        return getOnlyDate(now());
    }

    public static Date datePlusMonth(Date date, int months) {
        date = getOnlyDate(date);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, months);
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    public static Date datePlusDay(Date date, int days) {
        date = getOnlyDate(date);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return c.getTime();
    }

    public static Date datePlusSecond(Date date, int seconds) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.SECOND, seconds);
        return c.getTime();
    }

    public static Date getOnlyDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date result = null;
        try {
            result = formatter.parse(formatter.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    public static int getWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    public static Date getFirstDateOfWeek(Date dateInWeek) {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        DayOfWeek weekStart = DayOfWeek.MONDAY;
        LocalDate date = LocalDate.ofInstant(dateInWeek.toInstant(), defaultZoneId);
        LocalDate firstDate = date.with(TemporalAdjusters.previousOrSame(weekStart));
        return Date.from(firstDate.atStartOfDay(defaultZoneId).toInstant());
    }

    public static int getDayBetween(Date startDate, Date endDate) {
        long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return (int) diff;
    }

    public static int[] getDayBetweenDMY(Date startDate, Date endDate) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(endDate.getTime() - startDate.getTime());
        int y = c.get(Calendar.YEAR) - 1970;
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH) - 1;
        return new int[]{d, m, y};
    }

    public static LocalDate convertToLocalDate(Date dateToConvert) {
        return new java.sql.Date(dateToConvert.getTime()).toLocalDate();
    }

}

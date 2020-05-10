package me.pixeldroid.plugins.serverrecord.utils;

import java.time.YearMonth;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class LongTIme {

    // This is the most precise way to calculate time
    public static long getTime(String time) {
        long out = 0;
        String[] args = time.split("_");

        Calendar calendar = Calendar.getInstance();

        int seconds = Integer.parseInt(args[0]);
        int minutes = Integer.parseInt(args[1]);
        int hours = Integer.parseInt(args[2]);
        int days = Integer.parseInt(args[3]);
        int months = Integer.parseInt(args[4]);
        int years = Integer.parseInt(args[5]);

        // Seconds
        out += seconds;

        // Minutes
        out += TimeUnit.MINUTES.toSeconds(minutes);

        // Hours
        out += TimeUnit.HOURS.toSeconds(hours);

        // Days
        out += TimeUnit.DAYS.toSeconds(days);

        // Months
        int monthOfRollback = calendar.get(Calendar.MONTH) - months;

        int yearOverflow = 0;
        if(monthOfRollback < 0) {
            monthOfRollback = 12 - monthOfRollback;
            yearOverflow += 1;
        }

        out += TimeUnit.DAYS.toSeconds(YearMonth.of(calendar.get(Calendar.YEAR) - years - yearOverflow, monthOfRollback).lengthOfMonth());

        // Years
        out += TimeUnit.DAYS.toSeconds(YearMonth.of(calendar.get(Calendar.YEAR) - years - yearOverflow, monthOfRollback).lengthOfYear());

        return out;
    }
}

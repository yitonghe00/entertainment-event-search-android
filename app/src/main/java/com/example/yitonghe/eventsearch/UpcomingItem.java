package com.example.yitonghe.eventsearch;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UpcomingItem {
    String name;
    String url;
    String artist;
    String time;
    String date;
    String type;

    public String getTime() {
        String printedTime;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try {
            d = simpleDateFormat.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: Handle parse error
        }
        simpleDateFormat.applyPattern("MMM dd, yyyy");
        printedTime = simpleDateFormat.format(d);

        if(!time.equals("N/A")) {
            printedTime +=" " + time;
        }

        return printedTime;
    }
}

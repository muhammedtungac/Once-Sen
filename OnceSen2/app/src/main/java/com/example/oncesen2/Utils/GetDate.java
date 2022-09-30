package com.example.oncesen2.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GetDate {
    public static String getDate() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date today = Calendar.getInstance().getTime();
        String date = df.format(today);
        return date;
    }
    public static String getNow() {
        DateFormat df = new SimpleDateFormat("HH:mm");
        Date today = Calendar.getInstance().getTime();
        String date = df.format(today);
        return date;
    }
}

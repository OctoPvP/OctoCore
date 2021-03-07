package net.octopvp.octocore.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String convertFromMills(long mills){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm");
        Date date = new Date(mills);
        return sdf.format(date);
    }
}

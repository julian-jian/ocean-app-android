package com.sky.lamp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.addapp.pickers.util.LogUtils;

public class TimeHelper {
    /**
     * @param dateStr HH:mm
     *
     * @return
     */
    public static Date parseHourDate(String dateStr) {
        Calendar calendar = Calendar.getInstance();
        String str =
                calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH)+" ";
        return parseDate(str+dateStr, "yyyy-MM-dd HH:mm");
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss字符串转换成日期<br/>
     *
     * @param dateStr    时间字符串
     * @param dataFormat 当前时间字符串的格式。
     * @return Date 日期 ,转换异常时返回null。
     */
    public static Date parseDate(String dateStr, String dataFormat) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(dataFormat, Locale.PRC);
            Date date = dateFormat.parse(dateStr);
            return new Date(date.getTime());
        } catch (ParseException e) {
            LogUtils.warn(e);
            return null;
        }
    }
}

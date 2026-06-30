package com.baidu.myshop.utils;
import java.text.SimpleDateFormat;
import java.util.Date;
public class MyDate {
    private static SimpleDateFormat format;
    static {
        format=new SimpleDateFormat("yyyy-MM-dd");
    }
    //将字符串转换成Date类型
    public static Date stringToDate(String str){
        Date date=null;
        try {
            date=format.parse(str);
        }catch (Exception e){
            e.printStackTrace();
        }
        return date;
    }
    //将Date对象转换成字符串
    public static String dateToString(Date date){
        return format.format(date);
    }
}
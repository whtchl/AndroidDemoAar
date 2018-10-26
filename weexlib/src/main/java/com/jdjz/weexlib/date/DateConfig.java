package com.jdjz.weexlib.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConfig {
    public static String mode2 = "yyyy-MM-zz";
    public static String mode3 = "HH:mm";
    public static String mode1 = "yyyy-MM-zz HH:mm";


    //String 装换成date
/*    public static String string2date(String str){
        String time="";
        try {
            String times = "2016-11-18";
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(times);
            System.out.println(date.getYear()+"   "+date.getDay()+"   "+date.getMonth()+"");
            time = date.toString();
            System.out.println(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return time;
    }*/

    //时间装换   yyyy-MM-dd

    /**
     * times的格式是1920-20-22，返回值是获取年 月 日
     *
     * @param times
     * @return
     */
    public static String[] string2YMD(String times) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//"yyyy-MM-dd"
        Date date = new Date();
        try {
            date = sdf.parse(times);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] strNow = sdf.format(date).toString().split("-");


        return strNow;
    }

    //HH:mm

    /**
     * times 输入时19:05，返回时获取时 分
     *
     * @param times
     * @return
     */
    public static String[] string2HourMin(String times) {
        times = "1970-12-22 " + times;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");//"yyyy-MM-dd"
        Date date = new Date();
        try {
            date = sdf.parse(times);
            System.out.print(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] strNow = sdf.format(date).toString().substring(11, 16).split(":");


        return strNow;
    }

    //yyyy-mm-dd  format yyyy-mm-dd HH:mm

    /**
     * times：1970-11-10 19:55
     *
     * @param times
     * @return
     */
    public static String[] string2YMDHM(String times) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");//"yyyy-MM-dd"
        Date date = new Date();
        try {
            date = sdf.parse(times);
            System.out.print(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] strNow = sdf.format(date).toString().substring(0, 10).split("-");
        String[] strNow2 = sdf.format(date).toString().substring(11, 16).split(":");
        String[] strNow3 = new String[5];
        for (int i = 0; i < strNow3.length; i++) {
            if (i <= 2) {
                strNow3[i] = strNow[i];
            } else {
                strNow3[i] = strNow2[i - 3];
            }
        }
        return strNow3;
    }


    /**
     * 转换时间日期格式字串为long型
     *
     * @param time 格式为：yyyy-MM-dd  HH:mm:ss的时间日期类型
     */
    public static Long convertTimeToLong(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//"yyyy-MM-dd"
        Date date = new Date();
        try {
            date = sdf.parse(time);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }


    public static boolean isValidDateYMDHM(String str) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            // e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }

    public static boolean isValidDateYMD(String str) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            // e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }

    public static boolean isValidDateHM(String str) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            // e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }

    public static void main(String[] args) {
        /*String[] strNow2 = string2YMD("2017-14-19");
        Integer year = Integer.parseInt(strNow2[0]);
        Integer month = Integer.parseInt(strNow2[1]);
        Integer day = Integer.parseInt(strNow2[2]);
        System.out.print(" "+ year+" "+month+" "+day);*/

    /*  String[] strNow3 = string2HourMin("15:62");
        System.out.print(" @@@@"+ strNow3[0]+" "+strNow3[1]);*/

        /*String[] strNow4 = string2YMDHM("1970-10-01 15:52");
        System.out.print(" @@@@"+ strNow4[0]+" "+strNow4[1]+" "+strNow4[2]+"  "+strNow4[3]+" "+strNow4[4]);*/
        // System.out.print(convertTimeToLong("1986-12-16 23:59:59"));


        long longTime = new Date().getTime();
        System.out.println("字符串类型的Long日期转换成日期:");
        String str = "1498457677473";
        Long dateLong = Long.valueOf(str);
        System.out.println("longToDate：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(dateLong)));
        System.out.println("============================");
        System.out.println("Date和Long互转:");
        System.out.println("日期转换成Long：" + longTime);
        System.out.println("Long转换成日期：" + new Date(longTime));


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//"yyyy-MM-dd"
        Date date = new Date();
        try {
            date = sdf.parse("1979-10-09 15:34:10");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("longToDate：" + date.getTime());


        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");//"yyyy-MM-dd"
        Date date1 = new Date();
        try {
            date1 = sdf1.parse("1979-10-09");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("longToDate：" + date1.getTime());


        System.out.println(isValidDateYMDHM("2018-1-12 15:00"));
        System.out.println(isValidDateYMD("2018-1-12 "));


    }
}

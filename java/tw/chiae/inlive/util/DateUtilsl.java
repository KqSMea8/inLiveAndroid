package tw.chiae.inlive.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lw on 2016/7/26.
 */

public class DateUtilsl {
    private SimpleDateFormat sf = null;
    /*获取系统时间 格式为："yyyy/MM/dd "*/
    public static String getCurrentDate() {
        Date d = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日");
        return sf.format(d);
        }
    /*时间戳转换成字符窜*/
    public static String getDateToString(String time) {
//        Date d = new Date(time);
//        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
//        return sf.format(d);
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        long lcc_time = Long.valueOf(time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }

    /*将字符串转为时间戳*/
    public static long getStringToDate(String time) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日");
        Date date = new Date();
        try{
            date = sf.parse(time);
            } catch(ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            }
        return date.getTime();
        }

    /*将字符串转为时间戳 推荐使用正则表达式 而不是像我一样偷工减料*/
    public static String getStringToDataMrl(String time) {
        SimpleDateFormat sf = null;
        Date date = new Date();
        if (time.indexOf("-") >= 0)
            sf=new SimpleDateFormat("yyyy-MM-dd");
        else if (time.indexOf("月") >= 0)
            sf=new SimpleDateFormat("yyyy年MM月dd日");
        else
            return time;
        try {
            date = sf.parse(time);
            return String.valueOf(date.getTime()/1000);
        }catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return time;
        }
    }
}

package tw.chiae.inlive.presentation.ui.chatting.utils;

import android.content.Context;

import tw.chiae.inlive.util.L;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * Created by Ken on 2015/2/26.
 */
public class TimeFormat {

    private long mTimeStamp;
    private Context mContext;

    public TimeFormat(Context context, long timeStamp) {
        this.mContext = context;
        this.mTimeStamp = timeStamp;
        SimpleDateFormat format = new SimpleDateFormat(mContext.getString(IdHelper.getString(mContext,
                "jmui_time_format_accuracy")), Locale.CHINA);
        String date = format.format(timeStamp);
    }

    //用于显示会话时间
    public String getTime() {
        //获取当前时间
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
        Date date1 = new Date(System.currentTimeMillis());
        String str = formatter.format(date1);

//        long currentTime = Configs.getReportTime();
//        Date date1 = new Date(currentTime);
        Date date2 = new Date(mTimeStamp);
        SimpleDateFormat format = new SimpleDateFormat(mContext.getString(IdHelper.getString(mContext,
                "jmui_time_format_hours")), Locale.CHINA);
        SimpleDateFormat format1 = new SimpleDateFormat(mContext.getString(IdHelper.getString(mContext,
                "jmui_time_format_accuracy")), Locale.CHINA);
        String date = format.format(mTimeStamp);
//        L.e("lw","时间 1  1 ="+date1+"+ 2 ="+date2+"+ 0 ="+date+"+ 0 ="+date1);
        int hour = Integer.parseInt(date.substring(0, 2));
//        L.e("lw","时间 2  1 ="+date1.getDate()+"+ 2 ="+date2.getDate()+"+ 0 ="+date+"+ h ="+hour);
//        L.e("lw","日期"+date1.getDay()+" + "+date2.getDay());
//        L.e("lw","日期"+format1.format(mTimeStamp));
        //今天
        if (date1.getDate() - date2.getDate() == 0) {
            if (hour < 6) {
                return mContext.getString(IdHelper.getString(mContext, "jmui_before_dawn")) + " " + date;
            } else if (hour < 12) {
                return mContext.getString(IdHelper.getString(mContext, "jmui_morning")) + " " + date;
            } else if (hour < 18) {
                return mContext.getString(IdHelper.getString(mContext, "jmui_afternoon")) + " " + date;
            } else {
                return mContext.getString(IdHelper.getString(mContext, "jmui_night")) + " " + date;
            }
            //昨天
        } else if (date1.getDate() - date2.getDate() == 1) {
            return mContext.getString(IdHelper.getString(mContext, "jmui_yesterday"));
        } else if (date1.getDay() - date2.getDay() > 0) {
            switch (date2.getDay()) {
                case 1:
                    return mContext.getString(IdHelper.getString(mContext, "jmui_monday"));
                case 2:
                    return mContext.getString(IdHelper.getString(mContext, "jmui_tuesday"));
                case 3:
                    return mContext.getString(IdHelper.getString(mContext, "jmui_wednesday"));
                case 4:
                    return mContext.getString(IdHelper.getString(mContext, "jmui_thursday"));
                case 5:
                    return mContext.getString(IdHelper.getString(mContext, "jmui_friday"));
                case 6:
                    return mContext.getString(IdHelper.getString(mContext, "jmui_saturday"));
                default:
                    return mContext.getString(IdHelper.getString(mContext, "jmui_sunday"));
            }
        } else if (date1.getYear() == date2.getYear()) {
            return date2.getMonth() + 1 + mContext.getString(IdHelper.getString(mContext, "jmui_month"))
                    + date2.getDate() + mContext.getString(IdHelper.getString(mContext, "jmui_day"));
        } else {
            return format1.format(mTimeStamp);
        }
    }

    //用于显示消息具体时间
    public String getDetailTime() {
        //获取当前时间
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm");
        Date date1 = new Date(System.currentTimeMillis());
        String str = formatter.format(date1);
//        long currentTime = Configs.getReportTime();
//        Date date1 = new Date(currentTime);
        Date date2 = new Date(mTimeStamp);
        SimpleDateFormat format = new SimpleDateFormat(mContext.getString(IdHelper.getString(mContext,
                "jmui_time_format_hours")), Locale.CHINA);
        String date = format.format(mTimeStamp);
        SimpleDateFormat format1 = new SimpleDateFormat(mContext.getString(IdHelper.getString(mContext,
                "jmui_time_format_year_month_day")), Locale.CHINA);
        String date3 = format1.format(mTimeStamp);
        int hour = Integer.parseInt(date.substring(0, 2));
        if (date1.getDate() - date2.getDate() == 0) {
            if (hour < 6) {
                return mContext.getString(IdHelper.getString(mContext, "jmui_before_dawn")) + date;
            } else if (hour < 12) {
                return mContext.getString(IdHelper.getString(mContext, "jmui_morning")) + date;
            } else if (hour < 18) {
                return mContext.getString(IdHelper.getString(mContext, "jmui_afternoon")) + date;
            } else {
                return mContext.getString(IdHelper.getString(mContext, "jmui_night")) + date;
            }
        } else if (date1.getDate() - date2.getDate() == 1) {
            if (hour < 6) {
                return mContext.getString(IdHelper.getString(mContext, "jmui_yesterday")) + " "
                        + mContext.getString(IdHelper.getString(mContext, "jmui_before_dawn")) + date;
            } else if (hour < 12) {
                return mContext.getString(IdHelper.getString(mContext, "jmui_yesterday")) + " "
                        + mContext.getString(IdHelper.getString(mContext, "jmui_morning")) + date;
            } else if (hour < 18) {
                return mContext.getString(IdHelper.getString(mContext, "jmui_yesterday")) + " "
                        + mContext.getString(IdHelper.getString(mContext, "jmui_afternoon")) + date;
            } else {
                return mContext.getString(IdHelper.getString(mContext, "jmui_yesterday")) + " "
                        + mContext.getString(IdHelper.getString(mContext, "jmui_night")) + date;
            }
        } else if (date1.getYear() == date2.getYear()) {
            if (hour < 6) {
                return date2.getMonth() + 1 + mContext.getString(IdHelper.getString(mContext, "jmui_month"))
                        + date2.getDate() + mContext.getString(IdHelper.getString(mContext, "jmui_day"))
                        + " " + mContext.getString(IdHelper.getString(mContext, "jmui_before_dawn")) + date;
            } else if (hour < 12) {
                return date2.getMonth() + 1 + mContext.getString(IdHelper.getString(mContext, "jmui_month"))
                        + date2.getDate() + mContext.getString(IdHelper.getString(mContext, "jmui_day"))
                        + " " + mContext.getString(IdHelper.getString(mContext, "jmui_morning")) + date;
            } else if (hour < 18) {
                return date2.getMonth() + 1 + mContext.getString(IdHelper.getString(mContext, "jmui_month"))
                        + date2.getDate() + mContext.getString(IdHelper.getString(mContext, "jmui_day"))
                        + " " + mContext.getString(IdHelper.getString(mContext, "jmui_afternoon")) + date;
            } else {
                return date2.getMonth() + 1 + mContext.getString(IdHelper.getString(mContext, "jmui_month"))
                        + date2.getDate() + mContext.getString(IdHelper.getString(mContext, "jmui_day"))
                        + " " + mContext.getString(IdHelper.getString(mContext, "jmui_night")) + date;
            }
        } else if (hour < 6) {
            return date3 + " " + mContext.getString(IdHelper.getString(mContext, "jmui_before_dawn")) + date;
        } else if (hour < 12) {
            return date3 + " " + mContext.getString(IdHelper.getString(mContext, "jmui_morning")) + date;
        } else if (hour < 18) {
            return date3 + " " + mContext.getString(IdHelper.getString(mContext, "jmui_afternoon")) + date;
        } else {
            return date3 + " " + mContext.getString(IdHelper.getString(mContext, "jmui_night")) + date;
        }
    }
}

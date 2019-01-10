package tw.chiae.inlive.util.upapk;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import tw.chiae.inlive.R;

/**
 * Created by Administrator on 2016/6/22 0022.
 */
public class NotificationManager {
    public static final int NOTIFICATION_FLAG=1;
    Context context;
    android.app.NotificationManager notificationManager;
    Notification myNotify;
    String appname;
    String tocker;
    int incon;
    public NotificationManager(Context context) {
        this.context = context;
        notificationManager=(android.app.NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        myNotify = new Notification();
    }

    public void setIncon(int incon) {
        this.incon = incon;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public void setTocker(String tocker) {
        this.tocker = tocker;
    }

    //    显示通知
    public void showNotification(){
        myNotify.icon = incon;
        myNotify.tickerText = tocker;
        myNotify.flags=Notification.FLAG_AUTO_CANCEL;
        myNotify.when = System.currentTimeMillis();
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.notf_up_layout);
        rv.setTextViewText(R.id.up_appName, appname);
        myNotify.contentView = rv;
        Intent intent = new Intent(Intent.ACTION_MAIN);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 1,
                intent, 0);
        myNotify.contentIntent = contentIntent;
        notificationManager.notify(NOTIFICATION_FLAG, myNotify);
    }
    //取消通知
    public void cancelNotification(int id){
//        去取消通知
        notificationManager.cancel(id);
    }
    //    刷新进度条
    public void updateNotification(int id,int progress){
        if (myNotify!=null){
//            修改进度条
            myNotify.contentView.setProgressBar(R.id.up_pb,100,progress,false);
            myNotify.contentView.setTextViewText(R.id.up_ps,String.valueOf(progress));
//            刷新进度条
            notificationManager.notify(id,myNotify);
        }
    }

}

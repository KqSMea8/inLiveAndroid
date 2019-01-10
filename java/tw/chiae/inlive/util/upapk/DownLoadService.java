package tw.chiae.inlive.util.upapk;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import tw.chiae.inlive.R;

public class DownLoadService extends Service {
//    handler返回
    public static final int MSG_BACK=1;
//    下载开始
    public static final String ACTION_START="ACTION_START";
    //    更新进度
    public static final String ACTION_UPDATA="ACTION_UPDATA";
//    下载完成
    public static final String ACTION_END="ACTION_END";
    //   网络请求错误
    public static final int DOWNLOAD_EEROW=0;
//    下载的网址
    public String downloadurl;
//    发出当前进度
    public static final String SEND_PROGRESS="SEND_PROGRESS";
//    通知栏
    public NotificationManager notificationManager;
    public DownLoadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter=new IntentFilter();
        filter.addAction(DownLoadService.ACTION_UPDATA);
        filter.addAction(DownLoadService.ACTION_END);
        filter.addAction(DownLoadService.ACTION_START);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        registerReceiver(broadcastReceiver,filter);
        notificationManager=new NotificationManager(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DownLoadUp downLoadUp=new DownLoadUp(intent.getStringExtra("downLoadurl"),intent.getStringExtra("appPath"),intent.getStringExtra("appName"),this);
        downLoadUp.startDownLoad();
        notificationManager.setAppname(intent.getStringExtra("appName"));
        notificationManager.setTocker("开始下载");
        notificationManager.setIncon(R.mipmap.no_icon_bg);
        notificationManager.showNotification();
        return super.onStartCommand(intent,Service.START_REDELIVER_INTENT, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //    更新进度条哦
    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //            更新进度
            if (intent.getAction()==DownLoadService.ACTION_START){
                notificationManager.updateNotification(NotificationManager.NOTIFICATION_FLAG, (int) intent.getLongExtra(DownLoadService.SEND_PROGRESS,0));
            }else if (intent.getAction()==DownLoadService.ACTION_END){
                notificationManager.updateNotification(NotificationManager.NOTIFICATION_FLAG, (int) intent.getLongExtra(String.valueOf(100),100));
            }
        }
    };
}

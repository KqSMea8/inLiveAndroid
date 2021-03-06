package tw.chiae.inlive.util.troubleshoot;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import tw.chiae.inlive.BeautyLiveApplication;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.L;
import tw.chiae.inlive.util.Networks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class NetworkDiagnosisService extends Service {

    private static final String LOG_TAG = "NetworkDiagnosisService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startDiagnosis(this);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 执行以下操作并写到日志：
     * 1. 检查是否有网络连接；
     * 2. 检查能否ping通WS和API的服务器；
     * 3. 检查能否ping通QQ或百度的服务器。
     */
    public void startDiagnosis(Context context){
        L.i(LOG_TAG, "------------- Start Diagnosis ---------------");
        L.i(LOG_TAG, "isNetworkConnected? %s", Networks.isNetworkConnected(context));
        L.i(LOG_TAG, "Network type = %s", Networks.getNetworkType(context));

        pingWithLog(Const.WS_HOST, 6);
        pingWithLog(Const.MAIN_HOST_FOR_PING, 6);
        pingWithLog("baidu.com", 3);
        pingWithLog("qq.com", 3);

        L.i(LOG_TAG, "REPEAT : isNetworkConnected? %s", Networks.isNetworkConnected(context));
        L.i(LOG_TAG, "REPEAT : Network type = %s", Networks.getNetworkType(context));
        L.i(LOG_TAG, "------------- Finish Diagnosis ---------------");

//        L.e(true, LOG_TAG, "Manual Diagnosis Finished", new ManualWsDiagnosisException());
        stopSelf();
    }

    private void pingWithLog(String host, int count){
        L.i(LOG_TAG, "Ping host %s", host);
        Log.i("RayTestPing","Ping host "+ host);
        if (Networks.isNetworkConnected(BeautyLiveApplication.getContextInstance())){
            ping(host, count);
        }
        else{
            L.e(LOG_TAG, "Network disconnected, cancel ping!");
        }
    }

    private void ping(String host, int count) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(String.format(Locale.US, "/system/bin/ping -c %d %s", count, host));

//            process = new ProcessBuilder()
//                    .command("/system/bin/ping -c 10 " + host)
//                    .redirectErrorStream(true)
//                    .start();
            InputStream in = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String string;
            while ((string = reader.readLine()) != null) {
                L.i(LOG_TAG, string);
                Log.i("RayTestPing","Ping :"+ string);
            }
            in.close();
        } catch (IOException e) {
            L.e(LOG_TAG, "Failed to ping host " + host, e);
        } finally {
            if (process != null)
                process.destroy();
        }
    }

    public class ServiceBinder extends Binder {
        public NetworkDiagnosisService getService() {
            return NetworkDiagnosisService.this;
        }
    }

}

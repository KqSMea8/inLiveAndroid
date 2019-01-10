package tw.chiae.inlive.presentation.ui.room.player;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.daniulive.smartplayer.SmartPlayerJni;
import com.eventhandle.SmartEventCallback;

/**
 * Created by rayyeh on 2017/4/28.
 */

public class PlayerService extends Service implements SmartEventCallback {

    public static final String LOG_TAG = "RayService";
    public static String playbackUrlTAG = "playerUrl";

    private SmartPlayerJni libPlayer;
    private long playerHandle;




    public class LocalBinder extends Binder //宣告一個繼承 Binder 的類別 LocalBinder
    {
        PlayerService getService()
        {
            return  PlayerService.this;
        }
    }
    private LocalBinder mLocBin = new LocalBinder();

    public void ServiceInit()
    {
        Log.d("RayService", "ServiceInit()");
        libPlayer = new SmartPlayerJni();

    }

    public void startNewPopWindow(){
        startActivity(new Intent(this, PopPlayerRoom.class));
    }
    public void startBackGroundPlay(String playbackUrl){
        Log.e(LOG_TAG, "playback URL with "+playbackUrl);
        playerHandle = libPlayer.SmartPlayerInit(this);
        if (playerHandle == 0) {
            Log.i("RayTest","背景執行失敗");
            return;
        }
        libPlayer.SetSmartPlayerEventCallback(playerHandle, this);
/*        libPlayer.SmartPlayerSetSurface(playerHandle, daNiuSurfaceView);*/
        libPlayer.SmartPlayerSetAudioOutputType(playerHandle, 0);
        libPlayer.SmartPlayerSetBuffer(playerHandle, 200); // 原本200;
        libPlayer.SmartPlayerSetMute(playerHandle, 0);
        if (playbackUrl == null) {
            Log.e(LOG_TAG, "playback URL with NULL...");
            return;
        }
        int iPlaybackRet = libPlayer.SmartPlayerStartPlayback(playerHandle, playbackUrl);
        if (iPlaybackRet != 0) {
            Log.i("RayTest","StartPlayback strem failed..");
            Log.e(LOG_TAG, "StartPlayback strem failed..");
            return;
        }
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        // TODO Auto-generated method stub
        ServiceInit();
        return mLocBin;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        // TODO Auto-generated method stub

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // TODO Auto-generated method stub

        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        // TODO Auto-generated method stub
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        // TODO Auto-generated method stub
    }

    public void StopPlayService(){
        libPlayer.SmartPlayerClose(playerHandle);
    }
    @Override
    public void onCallback(int code, long l, long l1, String s, String s1, Object o) {
        switch (code) {
            case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_STARTED:
                Log.i(LOG_TAG, "开始。。");
                break;
            case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_CONNECTING:
                Log.i(LOG_TAG, "连接中。。");

                break;
            case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_CONNECTION_FAILED:
                Log.i(LOG_TAG, "连接失败。。");

                break;
            case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_CONNECTED:
                Log.i(LOG_TAG, "连接成功。。");

                break;
            case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_DISCONNECTED:
                Log.i(LOG_TAG, "连接断开。。");
                if (playerHandle != 0) {
                    libPlayer.SmartPlayerClose(playerHandle);
                    playerHandle = 0;
                }
                break;
            case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_STOP:
                Log.i(LOG_TAG, "关闭。。");
                break;
            case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_RESOLUTION_INFO:

                break;
            case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_NO_MEDIADATA_RECEIVED:
                Log.i(LOG_TAG, "收不到媒体数据，可能是url错误。。");
        }
    }
}

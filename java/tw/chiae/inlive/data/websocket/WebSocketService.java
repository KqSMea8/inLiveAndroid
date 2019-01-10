package tw.chiae.inlive.data.websocket;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.google.gson.Gson;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import tw.baidu.location.service.LocationService;
import tw.chiae.inlive.BeautyLiveApplication;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.websocket.ErrorMsg;
import tw.chiae.inlive.data.bean.websocket.LightHeartMsg;
import tw.chiae.inlive.data.bean.websocket.LiveAudienceListMsg;
import tw.chiae.inlive.data.bean.websocket.SendGiftMsg;
import tw.chiae.inlive.data.bean.websocket.SystemMsg;
import tw.chiae.inlive.data.bean.websocket.SystemWelcome;
import tw.chiae.inlive.data.bean.websocket.UserPrvMsg;
import tw.chiae.inlive.data.bean.websocket.UserPublicMsg;
import tw.chiae.inlive.data.bean.websocket.WsLoginOutMsg;
import tw.chiae.inlive.data.bean.websocket.WsLoginRequest;
import tw.chiae.inlive.data.bean.websocket.WsLogoutRequest;
import tw.chiae.inlive.data.bean.websocket.WsRequest;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.widget.CustomToast;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.L;
import tw.chiae.inlive.util.troubleshoot.NetworkDiagnosisService;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class WebSocketService extends Service {

    private static final String LOG_TAG = WebSocketService.class.getSimpleName();

    private static final int PONG_INTERVAL_SECONDS = 10;
    private static final int SELF_CHECK_INTERVAL_SECONDS = 20;

    /**
     * 连续尝试连接次数，超过限制时触发手动诊断和上报过程。
     */
    private int connectionAttemptCount = 0;

    /**
     * 最高允许的连续失败次数。
     * 达到这个次数将立即触发上报操作。
     */
    private static final int ATTEMPT_TOLERANCE = 2;

    public static Intent createIntent(Context context) {
        return new Intent(context, WebSocketService.class);
    }

    private WebSocket webSocket;
    /**
     * 为避免用户空闲太久导致WebSocket连接被服务器断开，需要定期向服务器发送Pong请求。
     * <p>Pong请求不需要服务器回复，相应地，服务器下发的Ping请求也不需要处理。</p>
     *
     * @see #PONG_INTERVAL_SECONDS
     */
    private ScheduledExecutorService pongService;
    /**
     * 有时候会因为一些数据传输异常【如重复登录房间、或数据出现错误等】导致被服务器强行断开连接。
     * 为了避免这种情况下用户毫无察觉地不可用，在WebSocket初始化后创建一个定时自检的Service。
     *
     * 为什么不能单纯依靠OnClose方法来完成重连？
     * 因为OnClose方法里的重连可能连接失败！失败后就再也没有OnClose了！
     */
    private Subscription selfCheckSubscription;

    /**
     * 标记是否正在连接中，用于自检服务避免重复发起连接。
     */
    private boolean isAttemptConnecting;

    /**
     * 标识准备关闭Service。
     * 一旦这个标记为true,则onclose方法里不能再发起重连操作
     */
    private boolean preparedShutdown = false;

    /**
     * 标记是否需要自动重连。
     */
    private boolean shouldAutoReconnect;
    /**
     * 标记是否需要自动重新登录。
     * 这个标记主要用于被管理员踢出房间之后，要维持Ws连接但不能再次自动登录该房间。
     */
    private boolean shouldAutoRelogin;

//    private final Object lockForErrorHandle = new Object();

    /**
     * 由于断线后重连需要重新登录而不希望退出房间，所以这里缓存最新的登录请求。
     * 注意,即使在发起登录的时候没有登录成功，也要保存这个请求。
     * 下面两个时间点需要清除这个缓存的请求：1）发起登出请求的时候；2）准备关闭Service的时候。
     */
    private WsLoginRequest cachedLoginRequest;

    private HashMap<String, WsListener<?>> activeListener = new HashMap<>();
    private Gson gson = new Gson();
    private static boolean isSpeedCancel = false;

    @Override
    public void onCreate() {
        super.onCreate();
        L.i(LOG_TAG, "----- onCreate -----"+Thread.currentThread().getName());
        initSocketWrapper("InitialConnect", true);
        startSelfCheckService();
        //initLocation();
//        if (webSocket == null || (!webSocket.isOpen())) {
//            initSocket();
//        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Use this to force restart service
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        L.v(LOG_TAG, "----- onBind -----");
        return new ServiceBinder();
    }

    @Override
    public void onDestroy() {
        L.i(LOG_TAG, "----- onDestroy -----");
        /*if(SpeedRunnable!=null) {
            Log.i("RaySocket","stop SpeedTest");
            speedhandler.removeCallbacks(SpeedRunnable);
        }*/
        super.onDestroy();
    }

    /**
     * 准备关闭Service。这个方法调用会关闭所有自检和Pong服务，调用者应当尽快解除对Service的连接并调用stopService。
     */
    public void prepareShutdown() {
        L.i(LOG_TAG, "----- prepareShutdown -----");
        preparedShutdown = true;

        if (cachedLoginRequest != null) {
            cachedLoginRequest = null;
        }
        stopSelfCheckService();
        stopPongDaemonService();
        if (webSocket != null && webSocket.isOpen()) {
            webSocket.close();
        }
        if (activeListener.size() > 0) {
            L.w(LOG_TAG, "Force clear active listeners, count=%d", activeListener.size());
            activeListener.clear();
            L.w(LOG_TAG, "Force clear active listeners, count=%d", activeListener.size());
        }
    }

    private boolean checkSocketAvailable() {
        if (webSocket == null || (!webSocket.isOpen())) {
            L.e(LOG_TAG, "WebSocket not ready, ignore this operation!");
            return false;
        }
        return true;
    }

    /**
     * Register listener for specified type.
     *
     * @param event    Event name. see {@link SocketConstants}
     * @param listener see {@link WsListener}
     */
    public void registerListener(@NonNull String event, @NonNull WsListener listener) {
        activeListener.put(event, listener);
    }

    /**
     * Remove all listeners.
     */
    public void removeAllListeners() {
        L.i(LOG_TAG, "Removing all listeners, count=%d. ", activeListener.size());
        activeListener.clear();
    }

    /**
     * Send request to server.
     *
     * @param request see {@link WsRequest}
     */
    public void sendRequest(@NonNull WsRequest request) {
        if (request instanceof WsLoginRequest){
            //update value
            cachedLoginRequest = (WsLoginRequest)request;
        }
        else if (request instanceof WsLogoutRequest){
            //clear value
            cachedLoginRequest = null;
        }

        if (!checkSocketAvailable()) {
            return;
        }
        Gson gson = new Gson();
        String msg = gson.toJson(request);
        Log.i("RaySocket","webSocket request: "+msg);
        webSocket.send(msg);
    }

    private void initSocketWrapper(String forReason){
        initSocketWrapper(forReason, false);
    }

    private void initSocketWrapper(final String forReason, final boolean isFirstConnect){
        Observable.just(forReason)
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        //如果正在连接则屏蔽该次消息
                        if (isAttemptConnecting){
                            L.v(LOG_TAG, "%s: Should reconnect but already in process, skip.", forReason);
                            return Boolean.FALSE;
                        }
                        return Boolean.TRUE;
                    }
                })
                //强制跳转主线程做通知操作
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.i(LOG_TAG,"大神大神大神");
                        if ( (webSocket == null) && (!isFirstConnect) && (!isAttemptConnecting)){
                            //notifyUiWsStatus(getString(R.string.servers_disconnect_connection));
                        }
                    }
                })
                //跳转IO线程做操作
                .observeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        L.i(LOG_TAG, "Reconnect WebSocket from %s.", forReason);
                        initSocket();
                    }
                });
    }

    /**
     * Never call this method directly!
     * call {@link #initSocketWrapper(String)} instead.
     */
    private void initSocket() {
        Log.i("RayTest","initSocket : "+Const.SOCKET_URL);
        //0.12.0 开始在onClose里也会自动发起重连，因此将attemptConnecting状态的维护放在这个地方。
        if (isAttemptConnecting) {
            return;
        }
        isAttemptConnecting = true;
        L.i(LOG_TAG, "Set isAttemptConnecting flag to true");
        Observable.create(new Observable.OnSubscribe<WebSocket>() {
            @Override
            public void call(final Subscriber<? super WebSocket> subscriber) {
                connectionAttemptCount++;
                L.d(LOG_TAG, "Connection attempt:%d", connectionAttemptCount);
                AsyncHttpClient.getDefaultInstance().websocket(Const.SOCKET_URL, "ws", new AsyncHttpClient.WebSocketConnectCallback() {
                            @Override
                            public void onCompleted(Exception ex, WebSocket socket) {
                                L.i(LOG_TAG, "onCompleted, ex=%s, webSocket=%s", ex, socket);
                                isAttemptConnecting = false;

                                if (socket != null && socket.isOpen()) {
                                    //标记连接成功
                                    connectionAttemptCount = 0;

                                    webSocket = socket;
                                    webSocket.setStringCallback(new WebSocket.StringCallback() {
                                        public void onStringAvailable(String message) {
                                            L.v(false, LOG_TAG, "received msg:%s", message);
                                            dispatchMessage(message);
                                        }
                                    });
                                    webSocket.setClosedCallback(new CompletedCallback() {
                                        @Override
                                        public void onCompleted(Exception ex) {
                                            L.i(LOG_TAG, "ClosedCallback: WebSocket closed.");
                                            //不做任何操作，等待自检服务重启Socket，或者自然死掉
                                            //Update: 0.12.0 需要自动重连，不等待服务。
                                            if ((!preparedShutdown) && (shouldAutoReconnect)) {
                                                initSocketWrapper("onClose");
                                            }
                                        }
                                    });
                                    webSocket.setEndCallback(new CompletedCallback() {
                                        @Override
                                        public void onCompleted(Exception ex) {
                                            L.i(LOG_TAG, "EndCallback: WebSocket closed?");
                                        }
                                    });
                                    subscriber.onNext(webSocket);
                                    subscriber.onCompleted();
                                } else {
                                    subscriber.onError(ex != null ? ex : new ConnectException
                                            ("Cannot connect ws service!"));
                                }
                            }
                        }).tryGet();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WebSocket>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        L.w(LOG_TAG, "WebSocket init failed!");
                        e.printStackTrace();
                        //判断是否需要执行诊断服务
                        if (connectionAttemptCount >= ATTEMPT_TOLERANCE){
                            L.e(LOG_TAG, "Continuous connection error occurred for %d times!",
                                    connectionAttemptCount);
                            L.i(LOG_TAG, "Force starting diagnosis service");
//                            startService(new Intent(WebSocketService.this, NetworkDiagnosisService.class));
                            //重置标记
                            connectionAttemptCount = 0;
                        }

                        //被动等待自检服务重连
//                        notifyUiWsStatus("服务器连接失败，正在重连……");
                        //Auto re-connect
//                        initSocket();
                    }

                    @Override
                    public void onNext(WebSocket webSocket) {
//                        notifyUiWsStatus("服务器连接成功");
//                        startSelfCheckService();
                        if (pongService == null) {
                            startPongDaemonService();
                        }
                        //如果缓存的登录请求不为空，则应该是房间内异常断线后的重连，自动执行重新登录
                        if (/*shouldAutoRelogin&&*/cachedLoginRequest != null) {
                            L.i(LOG_TAG, "Performing auto re-login, wsRequest=%s",
                                    cachedLoginRequest);
                            sendRequest(cachedLoginRequest);
                        }
                    }
                });
    }

    private void notifyUiWsStatus(String msg) {
        Observable.just(msg)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        CustomToast.makeCustomText(BeautyLiveApplication.getContextInstance(), s,
                                Toast
                                .LENGTH_SHORT)
                                .show();
                    }
                });
//        CustomToast.makeCustomText(BeautyLiveApplication.getContextInstance(), msg, Toast
//                .LENGTH_SHORT)
//                .show();
    }

    /**
     * 启动自检服务。自检服务会立即执行，之后按周期执行。
     */
    private void startSelfCheckService() {
//        //为安全起见先解除之前的订阅
//        stopSelfCheckService();
        //订阅新的自检服务
        selfCheckSubscription = Observable.interval(SELF_CHECK_INTERVAL_SECONDS, SELF_CHECK_INTERVAL_SECONDS, TimeUnit.SECONDS)
                .filter(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        if (!shouldAutoReconnect){
                            L.w(LOG_TAG, "Auto reconnect has been disabled, maybe kicked?");
                        }
                        return shouldAutoReconnect;
                    }
                })
                .map(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        return (webSocket != null) && (webSocket.isOpen());
                    }
                })
                .subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        L.i(LOG_TAG, "Self check task has been scheduled per %d seconds.",
                                SELF_CHECK_INTERVAL_SECONDS);
                        shouldAutoReconnect = true;
                        L.i(LOG_TAG, "Auto reconnect feature has been enabled.");
                    }
                })
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean websocketAlive) {
                        if (websocketAlive) {
                            L.v(false, LOG_TAG, "WebSocket self check: is alive.");
                            return;
                        }
                        initSocketWrapper("SelfCheckService");
//
//                        if (isAttemptConnecting) {
//                            return;
//                        }
//                        isAttemptConnecting = true;
//                        if (isAttemptConnecting){
//                            L.v(LOG_TAG, "SelfCheck: Should reconnect but already in process, " +
//                                    "skip.");
//                        } else {
//                            L.i(LOG_TAG, "Reconnect WebSocket from SelfCheckService!");
////                        notifyUiWsStatus("你已断线，重新连接中……");
//                            initSocket();
//                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        L.e(true, LOG_TAG, "Error while executing self check!", throwable);
                    }
                });
    }

    private void stopSelfCheckService() {
        if (selfCheckSubscription != null && (!selfCheckSubscription.isUnsubscribed())) {
            selfCheckSubscription.unsubscribe();
            L.i(LOG_TAG, "Self check service has been unSubscribed.");
        }
    }

    private void startPongDaemonService() {
        pongService = Executors.newSingleThreadScheduledExecutor();
        pongService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (webSocket!=null && webSocket.isOpen()){
                    sendRequest(WsObjectPool.newPongRequest());
                }
                else{
                    L.v(LOG_TAG, "WebSocket is not ready, cancel sending pong msg.");
                }
            }
        }, PONG_INTERVAL_SECONDS, PONG_INTERVAL_SECONDS, TimeUnit.SECONDS);
        L.i(LOG_TAG, "Pong service has been scheduled at %s seconds delay.", PONG_INTERVAL_SECONDS);
    }

    private void stopPongDaemonService() {
        if (pongService != null && (!pongService.isShutdown())) {
            pongService.shutdownNow();
            L.i(LOG_TAG, "Shutdown pong service now.");
        }
    }

//    这里是得到信息
    private void dispatchMessage(String msg) {
        String type = null;

        try {
            JSONObject json = new JSONObject(msg);
            type = json.optString(SocketConstants.FIELD_TYPE);
        } catch (JSONException e) {
            L.e(LOG_TAG, "Message is not well-formed data!");
        }
        if (TextUtils.isEmpty(type)) {
            L.e(LOG_TAG, "Cannot parse type from msg!");
            return;
        }
            L.v(false, LOG_TAG, "Dispatching msg type : %s", type);
            //Log.i("RayTest","Chat Url : "+Const.SOCKET_URL);
        try {
            switch (type) {
                case SocketConstants.EVENT_LOGIN:
//                    notifyListener(msg, SocketConstants.EVENT_LOGIN, WsLoginMsg.class);
                    notifyListener(msg, SocketConstants.EVENT_LOGIN, SystemWelcome.class);
                    break;
                case SocketConstants.EVENT_PUB_MSG:
                    notifyListener(msg, SocketConstants.EVENT_PUB_MSG, UserPublicMsg.class);
                    break;
                case SocketConstants.EVENT_SEND_GIFT:
                    notifyListener(msg, SocketConstants.EVENT_SEND_GIFT, SendGiftMsg.class);
                    break;
                case SocketConstants.EVENT_ONLINE_CLIENT:
                    notifyListener(msg, SocketConstants.EVENT_ONLINE_CLIENT, LiveAudienceListMsg.class);
                    break;
                case SocketConstants.EVENT_LIGHT_HEART:
                    notifyListener(msg, SocketConstants.EVENT_LIGHT_HEART, LightHeartMsg.class);
                    break;
                case SocketConstants.EVENT_SYS_MSG:
                    notifyListener(msg, SocketConstants.EVENT_SYS_MSG, UserPublicMsg.class);
                    break;
                case SocketConstants.STSTEM_MSG:
                    notifyListener(msg, SocketConstants.STSTEM_MSG, SystemMsg.class);
                    break;
                case SocketConstants.EVENT_PRV_MSG:
                    notifyListener(msg, SocketConstants.EVENT_PRV_MSG, UserPrvMsg.class);
                    break;
                case SocketConstants.STSTEM_WELCOME:
                    break;
                case SocketConstants.EVENT_ADD_ADMIN:

                    break;
                case SocketConstants.EVENT_REMOVE_ADMIN:

                    break;
                case SocketConstants.EVENT_LOGOUT:

                    notifyListener(msg, SocketConstants.EVENT_LOGOUT, WsLoginOutMsg.class);
                    break;
                case SocketConstants.EVENT_PING:
                    //Do nothing
                    break;

//                case SocketConstants.EVENT_ERROR:
//                    break;
                default:
                    if (type.startsWith(SocketConstants.EVENT_ERROR)){
                        notifyListener(msg, SocketConstants.EVENT_ERROR, ErrorMsg.class);
                        boolean shouldShutDown = shouldShutdownOnError(type);
                        L.i(LOG_TAG, "should shutdown for this error type?%s", shouldShutDown);
                        if (shouldShutDown) {
                            shouldAutoReconnect = false;
                            L.i(LOG_TAG, "Auto reconnect feature has been disabled.");
                        }
//
//                        else {
//                            notifyUiWsStatus(type);
//                        }
                        shouldAutoRelogin = canAutoReloginOnError(type);
                        L.i(LOG_TAG, "should relogin for this error type?%s", shouldAutoRelogin);
                    } else if
                            (type.startsWith(SocketConstants.EVENT_SYS_MSG)){
                        //只是转发信息，不是实际的Error，所以不处理
                        notifyListener(msg, SocketConstants.EVENT_ERROR, ErrorMsg.class);
                    }
                     else{
                        L.e(LOG_TAG, "Unsupported msg type:%s", type);
                    }
            }
        } catch (Exception e) {
            L.e(LOG_TAG, "Unexpected exception while dispatching msg.", e);
        }
    }

    private boolean shouldShutdownOnError(String type){
//        if (SocketConstants.ERROR_KICKED.equalsIgnoreCase(type)){
//            notifyUiWsStatus("被踢出房间");
//            return true;
//        }
//        notifyUiWsStatus("发送太快，前方高能");
            return false;
    }

    private boolean canAutoReloginOnError(String type){
        if (SocketConstants.ERROR_KICKED.equalsIgnoreCase(type)){
            return false;
        }
        return true;
    }
    /**
     * Notify active listener to handle data, if no listener matches, just discard.
     */
    @SuppressWarnings("unchecked")
    private <T> void notifyListener(String msg, final String type, final Class<T> clzData) {
        //transfer this to main thread
        L.d("RaynotifyListener", "notifyListener "+ msg);
        Observable.just(msg)
                .map(new Func1<String, T>() {
                    @Override
                    public T call(String s) {
                        return gson.fromJson(s, clzData);
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<T>() {
                    @Override
                    public void onCompleted() {
                        //Empty...
                    }

                    @Override
                    public void onError(Throwable e) {
                        //Auto upload this error
                        L.e(true, LOG_TAG, "Ws Service has catch an error!", e);
                    }

                    @Override
                    public void onNext(T data) {
                        WsListener<T> listener = (WsListener<T>) activeListener.get(type);
                        if (listener == null) {
                            L.e(LOG_TAG, "No listener handle type %s, discard this.", type);
                            return;
                        }
                        L.d(LOG_TAG, "Msg entity:%s.", data);
                        listener.handleData(data);
                    }
                });
    }

    public class ServiceBinder extends Binder {
        public WebSocketService getService() {
            return WebSocketService.this;
        }
    }

    /**
     * 初始化位置信息 ---实时定位用的
     */
   /* private static Handler speedhandler = new Handler();
    private static SpeedTestRunnable SpeedRunnable;

    public void startSpeedTest(int RoomType) {
        if(RoomType<=0)
            return;
        Log.i("RaySocket","startSpeedTest"+RoomType);
        isSpeedCancel = false;
        if (speedhandler == null)
            speedhandler = new SpeedHandler();
        if (SpeedRunnable == null)
            SpeedRunnable = new SpeedTestRunnable();
        SpeedRunnable.setRoomType(RoomType);
        speedhandler.postDelayed(SpeedRunnable, 5000);
    }

    public void stopSpeedTest() {
        Log.i("RaySocket","stopSpeedTest");
        isSpeedCancel = true;
    }

    private static class SpeedHandler extends Handler {


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    static class SpeedTestRunnable implements Runnable {

        private int mRoomType;
        private final int TYPE_VIEW_LIVE = 1;
        private final int TYPE_PUBLISH_LIVE = 2;
        private int type;


        public void run() {
            String CdnResult;
            if (mRoomType != TYPE_VIEW_LIVE)
                CdnResult = executeCmd("ping -c1 push.inlive.tw", false);
            else
                CdnResult = executeCmd("ping -c1 pull.inlive.tw", false);
            String API1Result = executeCmd("ping -c1 api1.inlive.tw", false);
            //String response = parseCmdIP(CdnResult, API1Result);
            Log.i("RaySocket","speed:"+API1Result);
            if(!isSpeedCancel)
                speedhandler.postDelayed(this, 5000);
        }

        public void setRoomType(int roomType) {
            this.mRoomType = roomType;
        }
    }

    public static String executeCmd(String cmd, boolean sudo) {
        try {

            Process p;
            if (!sudo)
                p = Runtime.getRuntime().exec(cmd);
            else {
                p = Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
            }
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String s;
            String res = "";
            while ((s = stdInput.readLine()) != null) {
                res += s + "[@@]";
            }
            p.destroy();
            return res;
        } catch (UnknownHostException HostError) {
            Log.i("RayTest", "UnknownHostException");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }*/

    private OkHttpClient mOkHttpClient;
    public static double latitude,longitude;
    //百度定位服务，只使用一次，用后即焚
    //private LocationService locationService;
    //private MyLocationListenner mLocationListener = new MyLocationListenner();
    /*private void initLocation() {
        mOkHttpClient = new OkHttpClient();
        locationService = ((BeautyLiveApplication) getApplication()).locationService;
        locationService.registerListener(mLocationListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();
    }*/

    /**
     * 定位SDK监听函数
     */
    /*public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            //上报数据
            latitude=location.getLatitude();
            longitude=location.getLongitude();
            HashMap hashMap=new HashMap();
            hashMap.put("longitude",longitude);
            hashMap.put("latitude",latitude);
            Gson gson = new Gson();
            uploadJson(gson.toJson(hashMap));
        }
    }*/

    //    上传数据
    /*public void uploadJson(String longitude) {
        RequestBody requestBody =  new FormBody.Builder()
                .add("token", LocalDataManager.getInstance().getLoginInfo().getToken())
                .add("profile",longitude)
                .build();

        Request request = new Request.Builder()
                .url(Const.WEB_BASE_URL +"user/edit")
                .post(requestBody)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 请求成功
                *//*locationService.unregisterListener(mLocationListener);
                locationService.stop();*//*
            }
        });
    }*/

}

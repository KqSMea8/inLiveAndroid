package tw.chiae.inlive;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.google.gson.Gson;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.bugly.crashreport.CrashReport;
import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONObject;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.sharesdk.framework.ShareSDK;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.repository.ParamsRemoteResponse;
import tw.chiae.inlive.data.sharedpreference.PrefsHelper;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.domain.LoginManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.chatting.utils.ConfigSharePreference;
import tw.chiae.inlive.presentation.ui.chatting.utils.SharePreferenceManager;
import tw.chiae.inlive.presentation.ui.main.mergefilm.VCamera;
import tw.chiae.inlive.presentation.ui.main.mergefilm.model.DeviceUtils;
import tw.chiae.inlive.presentation.ui.main.setting.SmartAsyncPolicyHolder;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.ImagePipelineConfigUtils;

/**
 * @since 1.0.0
 */
public class BeautyLiveApplication extends MultiDexApplication {

    //    private static final String LOG_TAG = BeautyLiveApplication.class.getSimpleName();
    private static final String JCHAT_CONFIGS = "JChat_configs";
    public static final String TARGET_ID = "targetId";
    public static final String TARGET_APP_KEY = "targetAppKey";
    public static final String GROUP_ID = "groupId";
    public static final String MsgIDs = "msgIDs";
    public static final String DRAFT = "draft";
    public static String PICTURE_DIR = "sdcard/xingmoxiu/pictures/";
    public static final String POSITION = "position";
    public static final int REQUEST_CODE_CROP_PICTURE = 18;
    public static final int RESULT_CODE_SELECT_PICTURE = 8;
    public static final int RESULT_CODE_BROWSER_PICTURE = 13;
    private static Context applicationContext;
    //public LocationService locationService;
    private static RequestQueue requestQueue;
    public RefWatcher mRefWatcher;

    public static RequestQueue getRequestQueue(){
        return requestQueue;
    }

    /*public static RefWatcher getRefWatcher(Context context) {
        BeautyLiveApplication application = (BeautyLiveApplication) context.getApplicationContext();
        return application.mRefWatcher;
    }*/

    @Override
    public void onCreate() {
        super.onCreate();

        initApplication();

    }

    private void initApplication() {


        ConfigSharePreference.init(this,"env");
        Boolean b = ConfigSharePreference.readEnviroment();
        Log.i("RayTest","ConfigSharePreference:"+b);
        Const.setEnvironment(b);
       // Const.setEnvironment(ConfigSharePreference.readEnviroment());
        SmartAsyncPolicyHolder.INSTANCE.init(getApplicationContext());

        //mRefWatcher = LeakCanary.install(this);
//        MobclickAgent.setDebugMode(true);
//        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_GAME);
//        MobclickAgent.openActivityDurationTrack(false);

    /*    //environment
        Boolean TestEvnironmentSW = Const.TEST_ENVIROMENT_SW;
        if(TestEvnironmentSW)
            Const.HOST_DNS = Const.MAIN_HOST_TEST;
        else
            Const.HOST_DNS = Const.MAIN_HOST_RELEASE;*/
        //初始化JMessage-sdk
        JMessageClient.init(this);
        JPushInterface.init(this);
        //设置Notification的模式
        JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_NO_NOTIFICATION);
        //初始化SharePreference
        SharePreferenceManager.init(this, JCHAT_CONFIGS);

        NoHttp.init(this);
        Logger.setDebug(true);
        requestQueue= NoHttp.newRequestQueue();

        applicationContext = this;
        //初始化Bugly
        CrashReport.initCrashReport(getApplicationContext(), getString(R.string.bugly_app_key), true);
        //初始化Prefs
        PrefsHelper.init(this);

        //初始化shareSDK
        ShareSDK.initSDK(this);
//        LeakCanary.install(this);
        //初始化Fresco
        initFresco();
        if(Const.ModifyMode){
            DownloadRemoteData();
        }

        /***
         * 初始化定位sdk，建议在Application中创建
         */
        //locationService = new LocationService(getApplicationContext());
//        doAsyncInitWorks();

        // 设置拍摄视频缓存路径
        File dcim = getExternalCacheDir();
        if (DeviceUtils.isZte()) {
            if (dcim.exists()) {
                VCamera.setVideoCachePath(dcim + "/inlive/rec/");
            } else {
                VCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/", "/sdcard-ext/")+ "/inlive/rec/");
            }
        } else {
            VCamera.setVideoCachePath(dcim + "/inlive/rec/");
        }
        // 开启log输出,ffmpeg输出到logcat
        VCamera.setDebugMode(true);
        // 初始化拍摄SDK，必须
        VCamera.initialize(this);
    }

    private void DownloadRemoteData() {
            final int PAGER_JSON = 1;
            RequestQueue requestQueueAppcation = NoHttp.newRequestQueue();
            final Request<JSONObject> request = NoHttp.createJsonObjectRequest("https://script.google.com/macros/s/AKfycbz-h1b-37CXz9fgBaQZ8spcZccfPm3ygGgNeSdp9YJiNavZhg/exec", RequestMethod.GET);
            requestQueueAppcation.add(PAGER_JSON, request, new OnResponseListener<JSONObject>() {
                @Override
                public void onStart(int what) {
                    Log.i("RayTest", "onStart:");
                }

                @Override
                public void onSucceed(int what, Response<JSONObject> response) {
                    if (what == PAGER_JSON) {// 判断what是否是刚才指定的请求
                        JSONObject result = response.get();// 响应结果
                        Gson gson = new Gson();
                        ParamsRemoteResponse paramsResponse = gson.fromJson(result.toString(), ParamsRemoteResponse.class);
                        Log.i("RayTest", "auth:" + paramsResponse.getAuth());
                        LoginInfo userInfo = LocalDataManager.getInstance().getLoginInfo();
                        if (userInfo != null) {
                            Log.i("RayTest", "userInfo.getUserId():" + userInfo.getUserId());
                            if (userInfo.getUserId().equals(paramsResponse.getAuth())) {
                                if (paramsResponse.getEnv() == 1) {
                                    Const.setEnvironment(true);
                                    Log.i("RayTest", "設定TEST_ENVIROMENT_SWTEST_ENVIROMENT_SW 1 :" +  Const.getEnvironment() );
                                } else {
                                    Const.setEnvironment(false);
                                    Log.i("RayTest", "設定TEST_ENVIROMENT_SWTEST_ENVIROMENT_SW 2 :" +  Const.getEnvironment() );
                                }
                                if (paramsResponse.getGash() == 0) {
                                    Const.IsPayMode=false ;
                                } else {
                                    Const.IsPayMode=true ;
                                }
                                Const.MainOfficialAccount=paramsResponse.getMainOfficial();
                                String[] OfficialAccountArray = new String[paramsResponse.getOfficiList().size()];
                                Const.OfficialAccountListID=paramsResponse.getOfficiList().toArray(OfficialAccountArray) ;
                                Log.i("RayTest", "MainOfficialAuccont:" + Const.MainOfficialAccount + "env:" + paramsResponse.getEnv());
                                Const.setToast(paramsResponse.getToast());
                            }
                            Log.i("RayTest", "修改完畢");
                        }
                    }
                }

                @Override
                public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

                }

                @Override
                public void onFinish(int what) {

                }
            });


    }

    private void initFresco() {
       /* Set<RequestListener> listeners = new HashSet<>();
        listeners.add(new RequestLoggingListener());
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setRequestListeners(listeners)
                .setDownsampleEnabled(true)
                .setBitmapsConfig(Bitmap.Config.RGB_565)
                .build();*/
        //Phoenix.init(this, config);
        ImagePipelineConfig config = ImagePipelineConfigUtils.getDefaultImagePipelineConfig(getApplicationContext());
        ImagePipelineConfigUtils.showImagePipeInfo();
        Fresco.initialize(this, config);
    }
//
//    private void doAsyncInitWorks() {
//        Observable.just("stub").subscribeOn(Schedulers.computation())
//                .doOnNext(new Action1<String>() {
//                    @Override
//                    public void call(String s) {
//                        L.v(false, LOG_TAG, "executing async tasks....");
//                        executeAsyncTasks();
//                    }
//                }).subscribe();
//    }
//
//    //Put all async init task here
//    private void executeAsyncTasks() {
//        startService(WebSocketService.createIntent(this));
//    }

    /**
     * Only call this method when have to call this!
     */
    public static Context getContextInstance() {
        return applicationContext;
    }

    public static void setPicturePath(String appKey) {
        if (!SharePreferenceManager.getCachedAppKey().equals(appKey)) {
            SharePreferenceManager.setCachedAppKey(appKey);
            PICTURE_DIR = "sdcard/JChat/pictures/" + appKey + "/";
        }
    }
}

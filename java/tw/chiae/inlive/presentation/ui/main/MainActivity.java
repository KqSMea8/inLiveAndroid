package tw.chiae.inlive.presentation.ui.main;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.jakewharton.rxbinding.view.RxView;

import java.io.File;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import rx.functions.Action1;
import rx.functions.Func1;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.EventSummary;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.data.websocket.WebSocketService;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.chatting.utils.FileHelper;
import tw.chiae.inlive.presentation.ui.chatting.utils.SharePreferenceManager;
import tw.chiae.inlive.presentation.ui.login.splash.PermissionsActivity;
import tw.chiae.inlive.presentation.ui.login.splash.VersionChecker;
import tw.chiae.inlive.presentation.ui.main.me.MeFragment;
import tw.chiae.inlive.presentation.ui.main.me.OtherUserActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.EventActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.KSWebActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.KaraStar;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;
import tw.chiae.inlive.presentation.ui.main.mergefilm.MediaPlayerActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.MediaRecorderActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.VCamera;
import tw.chiae.inlive.presentation.ui.main.mergefilm.model.FileUtils;
import tw.chiae.inlive.presentation.ui.main.search.SearchActivity;
import tw.chiae.inlive.presentation.ui.main.setting.CreateViewDialogFragment;
import tw.chiae.inlive.presentation.ui.main.webview.SimpleWebViewActivity;
import tw.chiae.inlive.presentation.ui.room.create.CreateRoomActivity;
import tw.chiae.inlive.presentation.ui.widget.MessageDialog;
import tw.chiae.inlive.util.L;
import tw.chiae.inlive.util.PermissionsChecker;
import tw.inlive.paymentsdk.PaymentIAB;

public class MainActivity extends BaseActivity implements EventInterface, CreateViewDialogFragment.dialogCallback {

    private String ctiyBaiDu;
    private String provinceBaiDu;
    //private final MyHandler myHandler = new MyHandler(this);
    public static final int ACTIVITY_REQUEST_CODE			= 1901;
    public static final int ACTIVITY_PAYMENT_SUCCESS_CODE			= 10001;

    @Nullable
    private WebSocketService wsService;

    private ServiceConnection wsConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            L.d(LOG_TAG, "Service connected.");
            wsService = ((WebSocketService.ServiceBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            L.i(LOG_TAG, "Service disconnected.");
        }
    };

    //百度定位服务，只使用一次，用后即焚
    //private LocationService locationService;
    //private ReportLocationListener mLocationListener = new ReportLocationListener();

    @IdRes
    private static final int FRAG_CONTAINER = R.id.main_container2;

    private long lastTimeTapBack = 0;

    private static final String FRAG_TAG_INDEX = "fragIndex";
    private static final String FRAG_TAG_ME = "fragMe";

    private RelativeLayout rlIndex, rlMe;

    private IndexFragment indexFragment;
    private MeFragment meFragment;
    private String mMyName,mMyPassword;
    private Context mContext;
    private static final int REGISTER = 200;
    private boolean r=false;
    private boolean f=false;
    private RelativeLayout rlKstar;
    private RelativeLayout rlSearch;
    private int preIndex;
    private EventPresenter presenter;
    private CreateViewDialogFragment dialogFragment;
    private VersionChecker checker;
    //private RequestQueue rq;

    @SuppressWarnings("unused")
    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void init() {
        mContext = this;
        setSwipeBackEnable(false);

        //requestLocationAndReport();
       // rq = webRequestUtil.getVolleyIntence(this);
        mMyName = "user"+LocalDataManager.getInstance().getLoginInfo().getUserId();
        mMyPassword = "user"+LocalDataManager.getInstance().getLoginInfo().getUserId();

        if (JMessageClient.getMyInfo() == null) {

            for (int i = 0; i < 5; i++) {
                register();
                    if (r = true) {
                        break;
                    }
            }
            if (r=true){
                for (int j = 0; j < 5; j++) {
                    login();
                    if (f=true) {
                        break;
                    }
                }
            }
        }
        presenter = new EventPresenter(this);
    }
    private void initPaymentIAB(){

        PaymentIAB.getInstance().initialize(this, LocalDataManager.getInstance().getLoginInfo().getUserId(), "https://api2.inlive.tw/", getResources().getString(R.string.googleDevKey), new PaymentIAB.BillingCompletion()
        {
            @Override
            public void onResult(PaymentIAB.BillingResult billingResult)
            {
                // 後續處理...
                //

            }
        });

    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void findViews(Bundle savedInstance) {
        if(savedInstance!=null) {
            savedInstance.clear();
        }
        //预启动WebSocket服务
        startService(WebSocketService.createIntent(this));
        bindService(WebSocketService.createIntent(this), wsConnection, BIND_AUTO_CREATE);
        dialogFragment = CreateViewDialogFragment.newInstance();
        dialogFragment.setDialogCallback(this);
        rlIndex = $(R.id.main_rl_index);
        rlMe = $(R.id.main_rl_me);
        rlKstar = $(R.id.main_rl_K);
        rlSearch = $(R.id.main_rl_serch);
        ImageButton imgbtnPublishRoom = $(R.id.main_imgbtn_room);
        indexFragment = IndexFragment.newInstance();
        meFragment = MeFragment.newInstance();
     /*   getSupportFragmentManager().beginTransaction()
                .add(FRAG_CONTAINER, indexFragment, FRAG_TAG_INDEX)
                .hide(meFragment)
                .commit();*/

        getSupportFragmentManager().beginTransaction()
                .add(FRAG_CONTAINER, indexFragment, FRAG_TAG_INDEX)
                .add(FRAG_CONTAINER, meFragment, FRAG_TAG_ME)
                .hide(meFragment)
                .commit();
        checker = new VersionChecker(getApplicationContext(),getSupportFragmentManager());
        rlIndex.setSelected(true);
        preIndex= 1;
        RxView.clicks(rlIndex)
                .filter(new Func1<Void, Boolean>() {
                    @Override
                    public Boolean call(Void aVoid) {
                        //为当前页时直接不处理事件
                        return (!rlIndex.isSelected());
                    }
                })
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        preIndex = 1;
                        L.v(LOG_TAG, "Tab index : clicked!");
                        rlIndex.setSelected(true);
                        rlMe.setSelected(false);
                        rlSearch.setSelected(false);
                        rlKstar.setSelected(false);
                        getSupportFragmentManager().beginTransaction()
                                .hide(meFragment)
                                .show(indexFragment)
//                                .replace(FRAG_CONTAINER, fragments[FRAG_INDEX], FRAG_TAG_INDEX)
                                //use this to avoid exception while performing pause.
                                .commitAllowingStateLoss();
                        meFragment.onPause();
                        indexFragment.onResume();
                    }
                });

//        开直播的按钮
        subscribeClick(imgbtnPublishRoom, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
//                if (LocalDataManager.getInstance().getLoginInfo().getApproveid().equals(getString(R.string.authentication_no))){
//                    startActivity(AuthenticationActivity.createIntent(getBaseContext()));
//                    toastShort("請先認證");
//                }else {
                    startActivity(CreateRoomActivity.createIntent(MainActivity.this,ctiyBaiDu,provinceBaiDu));
//                }

//                publishauthority(LocalDataManager.getInstance().getLoginInfo().getToken(),LocalDataManager.getInstance().getLoginInfo().getUserId());
            }
        });
        RxView.clicks(rlMe)
                .filter(new Func1<Void, Boolean>() {
                    @Override
                    public Boolean call(Void aVoid) {
                        //为当前页时直接不处理事件
                        return (!rlMe.isSelected());
                    }
                })
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        preIndex = 3;
                        L.v(LOG_TAG, "Tab me : clicked!");
                        rlIndex.setSelected(false);
                        rlMe.setSelected(true);
                        rlSearch.setSelected(false);
                        rlKstar.setSelected(false);
                        getSupportFragmentManager().beginTransaction()
//                                .replace(FRAG_CONTAINER, fragments[FRAG_ME], FRAG_TAG_ME)
                                .hide(indexFragment)
                                .show(meFragment)
                                .commitAllowingStateLoss();
                        meFragment.onResume();
                        indexFragment.onPause();
                    }
                });

        RxView.clicks(rlSearch)
                .filter(new Func1<Void, Boolean>() {
                    @Override
                    public Boolean call(Void aVoid) {
                        //为当前页时直接不处理事件
                        return (!rlSearch.isSelected());
                    }
                })
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        //preIndex = 0;
                        L.v(LOG_TAG, "Tab me : rlSearch!");
                        rlIndex.setSelected(false);
                        rlMe.setSelected(false);
                        rlSearch.setSelected(true);
                        rlKstar.setSelected(false);
                        startActivity(SearchActivity.createIntent(getApplicationContext()));
                        overridePendingTransition(R.anim.fragment_slide_right_in, R
                                .anim.fragment_slide_right_out);
                        /*getActivity().overridePendingTransition(R.anim.fragment_slide_right_in, R
                                .anim.fragment_slide_right_out);*/

                    }
                });
        RxView.clicks(rlKstar)
                .filter(new Func1<Void, Boolean>() {
                    @Override
                    public Boolean call(Void aVoid) {
                        //为当前页时直接不处理事件
                        return (!rlKstar.isSelected());
                    }
                })
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        //preIndex = 2;
                        L.v(LOG_TAG, "Tab me : rlKstar!");
  /*                      rlIndex.setSelected(false);
                        rlMe.setSelected(false);
                        rlSearch.setSelected(false);
                        rlKstar.setSelected(true);*/
                        //checkActivateEvent();
                        startKsEvent();

                    }
                });
//        if (BuildConfig.FLAVOR.equalsIgnoreCase("meilibo")){
//            MessageDialog dialog = new MessageDialog(this);
//            dialog.setContent(R.string.login_brand_tip);
//            dialog.setCancelable(false);
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.show();
//            dialog.hideCancelOption();
//            dialog.setMessageDialogListener(new MessageDialog.MessageDialogListener() {
//                @Override
//                public void onCancelClick(MessageDialog dialog) {
//                    dialog.dismiss();
//                }
//
//                @Override
//                public void onCommitClick(MessageDialog dialog) {
//                    dialog.dismiss();
//                }
//            });
//        }
        initPaymentIAB();
    }

    public void checkActivateEvent() {
        Log.i("RayTest","checkActivateEvent");
        presenter.checkActivateEvent();
    }

    private void startKsEvent() {

        KaraStar.getInstance().open(this, LocalDataManager.getInstance().getLoginInfo().getUserId(), new KaraStar.ViewHandler() {
            @Override
            public void switchToRecordView(KSWebActivity act, int starId, String starVideoUrl, String name) {
                Log.i("RayTest","switchToRecordView1");
                String FilePath = VCamera.getVideoCachePath() + "K_star_rec_" + starId + "_" + LocalDataManager.getInstance().getLoginInfo().getUserId() + "/0.mp4";

                if (FileUtils.checkFile(FilePath)) {
                    Intent it = MediaPlayerActivity.createIntent(getApplicationContext(), FilePath, name, starVideoUrl, starId);
                    act.startActivityForResult(it, KaraStar.REQUEST_VIDEO_RECORD);
                } else {
                    Intent it = MediaRecorderActivity.createIntent(getApplicationContext(), name, starVideoUrl, starId, false);
                    act.startActivityForResult(it, KaraStar.REQUEST_VIDEO_RECORD);
                }

            }

            @Override
            public void switchToUserView(KSWebActivity act, String userId) {

                act.startActivity(OtherUserActivity.createIntent(getApplicationContext(),
                        Integer.valueOf(userId), false));
            }

        });
    }

    private static final int REQUEST_CODE = 0; // 请求码
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO,
//            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE

    };


    private boolean CheckPermissions() {
        PermissionsChecker mPermissionsChecker = new PermissionsChecker(getApplicationContext());
        if (mPermissionsChecker.getLacksPermissions(PERMISSIONS).size()>0) {
            //startPermissionsActivity();
            dialogFragment.showMsgDialog(getSupportFragmentManager(),"提示",getString(R.string.permissions_error),CreateViewDialogFragment.TYPE_MV_DOWNLOAD_ERROR,false);
            return false;
        }
        return true;
    }
    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    private boolean CheckSpaceAvailable() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        stat.restat(Environment.getDataDirectory().getPath());
        long bytesAvailable ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytesAvailable = stat.getBlockSize()*stat.getAvailableBlocksLong();
        }else
            bytesAvailable = (long)stat.getBlockSize()*(long)stat.getAvailableBlocks();
        long megAvailable = bytesAvailable / 1048576;
        tw.chiae.inlive.presentation.ui.main.mergefilm.Log.i("RayTest"," 空間剩餘約："+megAvailable);
        if(megAvailable<=getResources().getInteger(R.integer.max_ks_space)){
            return false;
        }
        return true;
    }

    /**
     * 自动请求定位并上报
     */
/*    private void requestLocationAndReport(){
        // -----------location config ------------
        locationService = ((BeautyLiveApplication) getApplication()).locationService;
        locationService.registerListener(mLocationListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();// 定位SDK
    }*/

    /**
     * 尝试关闭定位服务，如果已经关闭则什么也不做。
     */
 /*   private void stopLocationService() {
        if (locationService != null) {
            locationService.unregisterListener(mLocationListener);
            locationService.stop();
            locationService = null;
            mLocationListener = null;
        }
    }*/

    @Override
    protected void onDestroy() {
        JMessageClient.unRegisterEventReceiver(this);

        UserInfo info = JMessageClient.getMyInfo();
        if (null != info) {
            File file = info.getAvatarFile();
            if (file != null && file.isFile()) {
            } else {
                String path = FileHelper.getUserAvatarPath(info.getUserName());
                file = new File(path);
                if (file.exists()) {
                }
            }
            SharePreferenceManager.setCachedUsername(info.getUserName());
            SharePreferenceManager.setCachedAvatarPath(file.getAbsolutePath());
            JMessageClient.logout();
        }

        super.onDestroy();
        if (wsService != null) {
            wsService.prepareShutdown();
            unbindService(wsConnection);
               stopService(WebSocketService.createIntent(this));
        }
       // stopLocationService();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("RayTest","MainActivity onActivityResult");
        if(requestCode== ACTIVITY_REQUEST_CODE){
            if(meFragment!=null){
                meFragment.onActivityResult(requestCode, resultCode, data);
            }
        } else if(requestCode==ACTIVITY_PAYMENT_SUCCESS_CODE){
           if(meFragment!=null){
               meFragment.onActivityResult(requestCode, resultCode, data);
           }
        }else
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastTimeTapBack > 2000) {
            toastShort(R.string.main_tap_to_exit);
            lastTimeTapBack = currentTimeMillis;
        } else {
            UserInfo info = JMessageClient.getMyInfo();
            if (null != info) {
                File file = info.getAvatarFile();
                if (file != null && file.isFile()) {
                } else {
                    String path = FileHelper.getUserAvatarPath(info.getUserName());
                    file = new File(path);
                    if (file.exists()) {
                    }
                }
                SharePreferenceManager.setCachedUsername(info.getUserName());
                SharePreferenceManager.setCachedAvatarPath(file.getAbsolutePath());
                JMessageClient.logout();
            }
            finish();
        }
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    //    定位的那个回调接口
/*    private class ReportLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (null != bdLocation && bdLocation.getLocType() != BDLocation.TypeServerError) {
                double lng = bdLocation.getLongitude();//经度
                double lat = bdLocation.getLatitude(); //纬度
                ctiyBaiDu=bdLocation.getCity();
                provinceBaiDu=bdLocation.getProvince();
                L.i(LOG_TAG, "Reporting location:lat=%s, lng=%s", lat, lng);
                //直接关闭
               // stopLocationService();
            }
        }
    }*/

//    是否能直播的签约
    private int PAGER_JSON=1;
    /*public void publishauthority(String user_token,String user_id) {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest("http://meilibo.cxtv.kaduoxq.com/OpenAPI/v1/room/canLive", RequestMethod.GET);
        request.add("token",user_token);
        request.add("user_id", user_id);

        BeautyLiveApplication.getRequestQueue().add(PAGER_JSON, request, ViewPagerOnResponse);
    }*/

    /*private OnResponseListener<JSONObject> ViewPagerOnResponse = new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int i) {
        }

        @Override
        public void onSucceed(int i, Response<JSONObject> response) {
            if (i == PAGER_JSON) {// 判断what是否是刚才指定的请求
                //                {"code":0,"msg":"ok","data":"320200"}这个是什么
                // 请求成功
                JSONObject result = response.get();// 响应结果
                    handler.sendEmptyMessage(1);
                // 响应头
                Headers headers = response.getHeaders();
                headers.getResponseCode();// 响应码
                response.getNetworkMillis();// 请求花费的时间
            }
        }

        @Override
        public void onFailed(int i, String s, Object o, Exception e, int i1, long l) {
        }

        @Override
        public void onFinish(int i) {
        }
    };*/

    /*Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//             *  开启直播验证
//             *
//             *  @param reponse  token  ucid
//             *
//             *  @return 服务器返回 data:( 0 ：可直播；1：未签约；2：时间不对;3:其他错误)
            switch (msg.what){
                case 0:
                    break;
                case 1:
                    showFinishConfirmDialog();
                    break;
                case 2:
                    toastShort("直播时间为： 02:00 - 06:00");
                    break;
                case 3:
                    toastShort("其他错误");
                    break;
            }
        }
    };*/

//    提示
    public void showFinishConfirmDialog(){
        MessageDialog dialog = new MessageDialog(this);
        dialog.setContent(R.string.mian_sign_tip);
        dialog.setMessageDialogListener(new MessageDialog.MessageDialogListener() {
            @Override
            public void onCancelClick(MessageDialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onCommitClick(MessageDialog dialog) {
                if (dialog!=null && dialog.isShowing()){
                    dialog.dismiss();
                }
//                跳转到签约的url网页中
                startActivity(SimpleWebViewActivity.createIntent(MainActivity.this,
                        SourceFactory.wrapPath("http://meilibo.cxtv.kaduoxq.com/app/goodvoice"),""));
            }
        });
        dialog.show();
    }

    public void login() {
        JMessageClient.login(mMyName, mMyPassword, new BasicCallback() {
            @Override
            public void gotResult(int status, String desc) {
                if (status == 0) {
                    f=true;
                } else {
                }
            }
        });
    }

    public void register() {
        JMessageClient.register(mMyName, mMyPassword, new BasicCallback() {
            @Override
            public void gotResult(int status, String desc) {
                if (status == 0) {
                    r=true;
                } else {
                    if (desc.equals("user exist")) {
                        r=true;
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        JPushInterface.onPause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {

        JPushInterface.onResume(this);
        Log.i("RayTest","onResume:"+preIndex);
        showVersionInfo();
        switch (preIndex){
            case 1:
                rlIndex.callOnClick();
                break;
            case 2:
                rlKstar.callOnClick();
                break;
            case 3:
                rlMe.callOnClick();
                break;
            default:

                break;
        }
        super.onResume();
    }

    public void showVersionInfo() {



        checker.getVersionName(new VersionChecker.VersionCheckInterface() {
            @Override
            public void onVersionSucess(boolean onSucess) {
                android.util.Log.i("RayTest","onSucess: "+onSucess);
                if(onSucess) {
                    //LoginPrepare();
                }
            }
    
            @Override
            public void onVersionFail() {
        
            }
        });

    }
    @Override
    public void showData(List<EventSummary> list) {

    }

    @Override
    public void appendData(List<EventSummary> list) {

    }

    @Override
    public void UpdateActivateEvent(EventActivity eventActivity) {
        Log.i("RayTest","UpdateActivateEvent");
        for(EventActivity.EventItem eventItem :eventActivity.getEvents()){
            if(eventItem.getId()==1 && eventItem.isViewable()==1){
                startKsEvent();
            }
        }
    }

    @Override
    public void showUserInfo(tw.chiae.inlive.data.bean.me.UserInfo data) {

    }

    @Override
    public void onOKDialogcheck(Bundle bundle) {
        int type = bundle.getInt("type");
        switch (type){
            case CreateViewDialogFragment.TYPE_FINISH_MEDIA_RECORD:

                break;
            case CreateViewDialogFragment.TYPE_PAUSE_MEDIA_RECORD:

                dialogFragment.dismiss();
                break;


            default:
                break;
        }
    }

    @Override
    public void onCancelDialogcheck(Bundle mArgs) {

    }

/*    private static class MyHandler extends Handler {

        private WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity mainActivity = mActivity.get();
            if (mainActivity != null) {
                switch (msg.what) {
                    case REGISTER:
                        mainActivity.login();
                        break;
                }
            }
        }
    }*/
}
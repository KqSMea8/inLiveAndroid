package tw.chiae.inlive.presentation.ui.room.publish;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.daniulive.smartpublisher.SmartPublisherJni;
import com.eventhandle.SmartEventCallback;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;
import com.voiceengine.NTAudioRecord;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.bean.room.CreateRoomBean;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.login.LoginSelectActivity;
import tw.chiae.inlive.presentation.ui.main.currency.CurrencyActivity;
import tw.chiae.inlive.presentation.ui.main.setting.CreateViewDialogFragment;
import tw.chiae.inlive.presentation.ui.main.webview.SimpleWebViewActivity;
import tw.chiae.inlive.presentation.ui.room.RoomActivity;
import tw.chiae.inlive.presentation.ui.room.RoomFragment;
import tw.chiae.inlive.presentation.ui.room.RoomInfoTmp;
import tw.chiae.inlive.presentation.ui.room.player.SimpleWebDialog;
import tw.chiae.inlive.presentation.ui.widget.SmartCameraView;
import tw.chiae.inlive.presentation.ui.widget.magicfilter.utils.MagicFilterType;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.EventUtil;
import tw.chiae.inlive.util.L;
import tw.chiae.inlive.util.PermissionsChecker;
import tw.chiae.inlive.util.TimingLogger;

/**
 * @author Muyangmin
 * @since 1.0.0
 * 开直播
 */
public class PublishFragment extends RoomFragment implements PublishFragmentUiInterface, SurfaceHolder.Callback, SmartCameraView.SmartCameraCallback, View.OnTouchListener, PublishFragmentPresenter.PublishFragmentCallback, SimpleWebDialog.dialogListener {

    private static final String ARG_PUSH_ADDRESS = "addr";

    private PopupWindow popupWindowCamera;
    private String myPlayPath;

    private boolean isStart = false;
    private int PAGER_JSON;
    private View mRoomOwner; //房间主人控件
    public String user_id = null;
    public String user_token = null;

    //是否可以反转摄像头，通常是由于用户缺少摄像头或被占用造成的。
    private boolean canReverseCamera = true;

    //记录闪光灯是否开启，在切换和退出相机的时候应该关闭闪光灯。
    private boolean mIsTorchOn = false;

    //    用来记录是否在 生命周期里面对流进行重启和暂停操作 设置一个全局变量
    public static boolean isCameraManagerPause = true;
    // 区分主播和副主播
    private static final String ARG_PUSH_AHCHOR = "anchor";
    //连麦的房间号 其实就是roomid
    private static final String ARG_ROOM_ID = "roomidcon";
    public static final int RTC_ROLE_ANCHOR = 0x01;
    private static final String CAMERA_WIDTH = "camera_width";
    private static final String CAMERA_HEIGHT = "camera_height";
    private static final String ARG_TITLE = "title";
    public static final int RTC_ROLE_VICE_ANCHOR = 0x02;
    private int publishType;
    private String publishRoomId;
    //推流的json地址
    private String streamJsonStrFromServer;
    //h关于重连handel的
    private static final int MESSAGE_ID_RECONNECTING = 0x01;
    //是否暂停界面
    private boolean mIsActivityPaused = true;
    //是否推流中
    private boolean mIsPublishStreamStarted = false;
    private PublishFragmentPresenter presenter;
    //创建房间的返回对象
    private CreateRoomBean createRoomBean;
    private static final String CREATE_ROOM_BEAN = "createRoomBean";
    //大牛
    private SmartPublisherJni libPublisher = null;
    //private SurfaceView mSurfaceView = null;
    //private SurfaceHolder mSurfaceHolder = null;
    //private Camera mCamera = null;
    private Switch swSound;

    private static final int FRONT = 1;        //前置摄像头标记
    private static final int BACK = 2;        //后置摄像头标记
    private int currentCameraType = FRONT;    //当前打开的摄像头标记
    private static final int PORTRAIT = 1;    //竖屏
    private static final int LANDSCAPE = 2;    //横屏
    private int currentOrigentation = PORTRAIT;
    //找個玩意應該是攝像頭的一個什麼 是否在運行 我記得要記得關關閉  如果重複啟動會錯誤
    private boolean mPreviewRunning = false;
    //攝像頭分辨率的尺寸
    private int videoWidth = 480;
    private int videoHeight = 640;
    private boolean isDefaultSize = true;

    private int PublisherVideoWidth = 640;
    private int PublisherVideoHight = 360;

    //不太清楚
    private int curCameraIndex = -1;
    //找個也不太清楚
    private int frameCount = 0;
    //自動變焦回調
    private Camera.AutoFocusCallback myAutoFocusCallback = null;

    NTAudioRecord audioRecord_ = null;    //for audio capture
    //這裡設置不錄製 但是我要保留一下這個功能
    private boolean is_need_local_recorder = false;
    //錄製地址
    private String recDir = "";    //for recorder path

    /* 推送类型选择
     * 0: 音视频
	 * 1: 纯音频
	 * 2: 纯视频
	 * */
    private int pushType = 0;
    /* 水印类型选择
     * 0: 图片水印
	 * 1: 全部水印
	 * 2: 文字水印
	 * 3: 不加水印
	 * */
    private int watemarkType = 0;

    private String txt = "當前狀態";

    static {
        System.loadLibrary("SmartPublisher");
    }

    private SmartCameraView mSmartCameraView;
    private int mDegree;
    private int mCurOrg = Configuration.ORIENTATION_PORTRAIT;
    private View mScrollView;
    private View mSetting;
    private SettingDialogView mSettingDialog;
    private SharedPreferences settings;
    private int config_kbps;
    private int config_beauty;
    private String setting_flag;
    private boolean config_support_HW_Encode;
    private int DefaultFps = 18  ;
    private int DefaultGop = 36;

    private String publishRoomTitle;
    private String CreatRoomStreamID;

    SimpleDateFormat formatter_old = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat formatter_new = new SimpleDateFormat("HH:mm:ss");
    private boolean SettingShowing = false;
    private String StartTime="";
    private PermissionsChecker mPermissionsChecker;
    private CreateViewDialogFragment dialogFragment;
    private boolean OpenRank = false;
    private SimpleWebDialog simpleWebDialog;

    //

    public static PublishFragment newInstance(@NonNull Bundle bundle) {
        PublishFragment fragment = new PublishFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static Bundle createArgs(@NonNull String pushAddress, int publishType, String roomid, CreateRoomBean createRoomBean, ArrayList<Integer> arrayListForSize, String title) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PUSH_ADDRESS, pushAddress);
        bundle.putInt(ARG_PUSH_AHCHOR, publishType);
        bundle.putString(ARG_ROOM_ID, roomid);
        bundle.putParcelable(CREATE_ROOM_BEAN, createRoomBean);
        bundle.putInt(CAMERA_WIDTH, arrayListForSize.get(0));
        bundle.putInt(CAMERA_HEIGHT, arrayListForSize.get(1));
        bundle.putString(ARG_TITLE, title);
        return bundle;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_room_publish;
    }

    @Override
    public void setBalamceValue(String value){
        int charmCount = getBalamceValue();
        tvCharmCount.setText("" + charmCount);
        tvGold.setText(new java.text.DecimalFormat("#").format(Double.valueOf(value)));

        if (!isFirstVBalance) {
            vFirstVBalance = Double.valueOf(value);
            isFirstVBalance = true;
        }
    }

    @Override
    public void setupStartPublishTime(String starttime) {

        try {
            Date date = formatter_old.parse(starttime);
            StartTime = "開播時間 "+formatter_new.format(date)+" ";
            getStartTimeView().setVisibility(View.VISIBLE);
            getStartTimeView().setText(StartTime);
        } catch (ParseException e) {
            e.printStackTrace();
            getStartTimeView().setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public void sendLiveMsg(String msg) {
        getStartTimeView().setText(StartTime + msg);
    }


    @Override
    protected void initViews(View view) {
        super.initViews(view);

        readPublishConfig();
        timingLogger.reset(TIMING_LOG_TAG, "PublishFragment.initViews");
        presenter = new PublishFragmentPresenter(this);
        presenter.setPublishCallback(this);
        if (toptabstart != null) {
            toptabstart.setVisibility(View.GONE);
        }

        simpleWebDialog  = SimpleWebDialog.newInstance();
        simpleWebDialog.setDialogListener(this);
        mRoomOwner = $(view, R.id.room_owner);
        mScrollView  = $(view,R.id.room_scroll);
        mSetting = $(view,R.id.room_imgbtn_setting);
        mSetting.setVisibility(View.VISIBLE);
        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showSettingDialog();
            }
        });
        initSettingDialog();

        //showCameraSize();
        setApproveidIcon(ivApproveId,LocalDataManager.getInstance().getLoginInfo().getApproveid());
        setCrownIcon(LocalDataManager.getInstance().getLoginInfo().getApproveid());

        mPermissionsChecker = new PermissionsChecker(getContext().getApplicationContext());
        boolean camera_permission = mPermissionsChecker.lacksPermission(Manifest.permission.CAMERA);
        boolean audio_permission = mPermissionsChecker.lacksPermission(Manifest.permission.RECORD_AUDIO);

        if(camera_permission && audio_permission  ){

        }else {
            if (dialogFragment == null) {
                dialogFragment = CreateViewDialogFragment.newInstance();
            }
            dialogFragment.setDialogCallback(new CreateViewDialogFragment.dialogCallback() {
                @Override
                public void onOKDialogcheck(Bundle bundle) {
                    dialogFragment.dismiss();

                }

                @Override
                public void onCancelDialogcheck(Bundle mArgs) {

                }
            });

            if(!camera_permission && !audio_permission){
                dialogFragment.showMsgDialog(getActivity().getSupportFragmentManager(), "提示", getString(R.string.permissions_error_audio_camera), CreateViewDialogFragment.TYPE_SHOW_ERROR, false);
                return;
            }else{
                if (!camera_permission) {
                    dialogFragment.showMsgDialog(getActivity().getSupportFragmentManager(), "提示", getString(R.string.permissions_error_storage_camera), CreateViewDialogFragment.TYPE_SHOW_ERROR, false);
                    return;
                }
                if (!audio_permission  ) {
                    dialogFragment.showMsgDialog(getActivity().getSupportFragmentManager(), "提示", getString(R.string.permissions_error_audio), CreateViewDialogFragment.TYPE_SHOW_ERROR, false);
                    return;
                }
            }

       /* mPermissionsChecker = new PermissionsChecker(getContext().getApplicationContext());
        if(mPermissionsChecker.lacksPermission(Manifest.permission.CAMERA)){

        }else{
            if(dialogFragment==null) {
                dialogFragment = CreateViewDialogFragment.newInstance();
            }
            dialogFragment.setDialogCallback(new CreateViewDialogFragment.dialogCallback() {
                @Override
                public void onOKDialogcheck(Bundle bundle) {
                    dialogFragment.dismiss();
                    ((RoomActivity) getActivity()).exitLiveRoom(false);
                }

                @Override
                public void onCancelDialogcheck(Bundle mArgs) {

                }
            }).showMsgDialog(getActivity().getSupportFragmentManager(),"提示",getString(R.string.permissions_error_camera),CreateViewDialogFragment.TYPE_SHOW_ERROR,false);*/
            return;
        }
        Log.i("RayTest","通過 檢查");
        mSmartCameraView = (SmartCameraView) view.findViewById(R.id.glsurfaceview_camera);
        setSmartCameraView();
        view.findViewById(R.id.room_move_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){

                    Point point = new Point();
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    point.set(x,y);
                    mSmartCameraView.onFocus(point, new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            Log.i("RatTest","success:"+success);
                        }
                    });
                }
                return true;
            }
        });

        RxView.clicks(mRoomOwner).throttleFirst(Const.VIEW_THROTTLE_TIME,
                TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {

                UserInfo info = new UserInfo();
                info.setId(LocalDataManager.getInstance().getLoginInfo().getUserId());
                info.setNickname(LocalDataManager.getInstance().getLoginInfo().getNickname());
                info.setAvatar(LocalDataManager.getInstance().getLoginInfo().getAvatar());
                info.setLevel(LocalDataManager.getInstance().getLoginInfo().getLevel());
                info.setSnap(LocalDataManager.getInstance().getLoginInfo().getSnap());
                info.setCity(LocalDataManager.getInstance().getLoginInfo().getCity());
                showUserInfoDialog(info);
            }
        });

        //显示主播信息
        SimpleDraweeView draweeAnchor = $(view, R.id.img_user_avatar);
        String myAvatar = LocalDataManager.getInstance().getLoginInfo().getAvatar();
        if (!TextUtils.isEmpty(myAvatar)) {
            draweeAnchor.setImageURI(SourceFactory.wrapPathToUri(myAvatar));
        }
        //swSound = $(view, R.id.sw_live_menu_sound);

        user_id = LocalDataManager.getInstance().getLoginInfo().getUserId();
        user_token = LocalDataManager.getInstance().getLoginInfo().getToken();
        presenter.getViewPagerJson(user_id, user_token);

        timingLogger.addSplit("init presenter, drawee & SurfaceView");

        initOperationBars(view);
        timingLogger.addSplit("initOperationBars");

//        initPreview();
        timingLogger.addSplit("initPreview");
        timingLogger.dumpToLog();
        if (mRankLay != null) {
            RxView.clicks(mRankLay)
                    .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            if(!OpenRank){
                                OpenRank = true;
                                showRank();
                            }

                        }
                    });
        }

        //获取穿过来的推流地址
        streamJsonStrFromServer = getArguments().getString(ARG_PUSH_ADDRESS);

        //初始化SDK内部推流配置
        publishType = getArguments().getInt(ARG_PUSH_AHCHOR);
        //连麦房间号传过来的
        publishRoomId = getArguments().getString(ARG_ROOM_ID, LocalDataManager.getInstance().getLoginInfo().getCurrentRoomNum());
        publishRoomTitle = getArguments().getString(ARG_TITLE, "");
        //创建直播返回的bean
        this.createRoomBean = getArguments().getParcelable(CREATE_ROOM_BEAN);
        int width = getArguments().getInt(CAMERA_WIDTH,0);
        int height = getArguments().getInt(CAMERA_HEIGHT,0);
        Log.i("RayTest","parse width:" +width+"parse height:" +height);
        if(width!=0 && height!=0){
            if(width > height){
                videoWidth = height;
                videoHeight = width;
            }else{
                videoWidth = width;
                videoHeight = height;
            }
        }
        libPublisher = new SmartPublisherJni();
        RxView.clicks($(view, R.id.room_imgbtn_conference))
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                    }
                });
        setupCreatRoomTime();
    }

    private void setupCreatRoomTime() {
        presenter.setupCreateRequest(getWsUserId(),getWsRoomId(),publishRoomTitle);
    }

    private void setupEndRoomTime() {
        if(presenter==null)
            return;
        presenter.setupEndRoomRequest(getWsUserId(),getWsRoomId(),CreatRoomStreamID);
        presenter.getHotPointRequest(getmAnchorId());
    }





    private void readPublishConfig() {
        setting_flag = "setting_publish_config_"+LocalDataManager.getInstance().getLoginInfo().getUserId();
        settings = getContext().getSharedPreferences(setting_flag,0);

        config_kbps = settings.getInt("kbps",-1);
        config_beauty = settings.getInt("beauty",-1);


    }

    private void showSettingDialog() {
        mSettingDialog.setConfig(getContext(),mSmartCameraView,PublishFragment.this,settings,config_support_HW_Encode,setting_flag);
        mSettingDialog.show();

    }

    private void initSettingDialog() {
        Log.i("RayTest","initSettingDialog");
        mSettingDialog = new SettingDialogView(getContext());
        Window win = mSettingDialog.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        win.setAttributes(lp);
        mSettingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                SettingShowing = false;
                Log.i("RayTest","mSettingDialog onCancel");
            }
        });
    }

    private void setSmartCameraView() {

        mSmartCameraView.setCameraCallbcak(this);

        mSmartCameraView.setPreviewCallback(new SmartCameraView.PreviewCallback() {
            @Override
            public void onGetRgbaFrame(byte[] data, int width, int height) {

                if (!(pushType == 1) && isStart) {

                    ByteBuffer bbuffer = ByteBuffer.allocateDirect(data.length);

                    bbuffer.put(data, 0, data.length);

                    libPublisher.SmartPublisherOnCaptureVideoABGRFlipVerticalData(bbuffer, width*4,  width, height);
                }
            }
        });

        setPreviewResolution(videoWidth, videoHeight);
        FixSurfaceView();
        setMagicFilter();

        if (!mSmartCameraView.startCamera()) {
            return;
        }
    }

    private void setMagicFilter() {

        MagicFilterType MagicFilter ;
        if(config_beauty == -1) {
            MagicFilter = MagicFilterType.NONE;
            settings.edit().putInt("beauty",0).commit();
        }else{
            switch (config_beauty){
                case 0:
                    MagicFilter = MagicFilterType.NONE;
                    break;
                case 1:
                    MagicFilter = MagicFilterType.BEAUTY;
                    break;
                case 2:
                    MagicFilter =MagicFilterType.COOL;
                    break;
                case 3:
                    MagicFilter =MagicFilterType.SAKURA;
                    break;
                case 4:
                    MagicFilter =MagicFilterType.ROMANCE;
                    break;
                case 5:
                    MagicFilter =MagicFilterType.NONE;
                    break;
                case 6:
                    MagicFilter =MagicFilterType.NONE;
                    break;
                case 7:
                    MagicFilter =MagicFilterType.ANTIQUE;
                    break;
                case 8:
                    MagicFilter =MagicFilterType.NONE;
                    break;
                default:
                    MagicFilter = MagicFilterType.NONE;
                    break;
            }

        }

        switchCameraFilter(MagicFilter);

    }

    private boolean switchCameraFilter(MagicFilterType type) {

        return mSmartCameraView.setFilter(type);
    }

    public void setPreviewResolution(int width, int height) {
        int resolution[] = mSmartCameraView.setPreviewResolution(width, height);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        try {
            super.onConfigurationChanged(newConfig);

            setCameraDisplayOrientation(getActivity(), getCamraId());

            mSmartCameraView.setPreviewOrientation(newConfig.orientation, mDegree);

            mCurOrg = newConfig.orientation;


        } catch (Exception ex) {
        }
    }

    public int getCamraId() {
        return mSmartCameraView.getCameraId();
    }

    private void FixSurfaceView() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //lp.height = getSurfaceViewHeight();
        //lp.width = (int) (((float) lp.height / (float)videoHight)*(float)videoWidth)+500;
        if(!isDefaultSize){
            //lp.height = videoHight;
            //lp.width = videoWidth;
        }else{

        }
        float ratio =(float) getScreenMetrics().heightPixels/(float)videoWidth;
        lp.height = (int) (videoWidth * ratio);
        lp.width = (int) (videoHeight * ratio);

        int offset = (lp.width - getScreenMetrics().widthPixels)/2 ;
        //lp.setMargins(-1*offset,0,0,0);
        mSmartCameraView.setLayoutParams(lp);
        mSmartCameraView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
            }
        });
    }

    @Override
    public void upDataLoginBalance(String coinbalance) {
        LoginInfo loginInfo = LocalDataManager.getInstance().getLoginInfo();
        loginInfo.setTotalBalance(Double.valueOf(coinbalance));
        //更新到永存
        LocalDataManager.getInstance().saveLoginInfo(loginInfo);
        UpdateCoinBlance(coinbalance);
    }

    @Override
    public void upDataRoomStreamID(String streamID) {
        CreatRoomStreamID = streamID;
        Log.i("RayTest","CreatRoomStreamID:"+CreatRoomStreamID);
    }

    @Override
    public void EndHotPoint(long coin) {
        ((RoomActivity) getActivity()).EndHotPoint(coin);
    }

    private boolean stopPublishStreaming() {
        if (!mIsPublishStreamStarted) {
            return true;
        }
        //在请求删除房间
        presenter.deletConferenceRoom(getWsRoomId());
        mIsPublishStreamStarted = false;
        presenter.stopLive();
        return false;
    }

    /**
     * 展示秀币排行榜
     */
    private void showRank() {
        isCameraManagerPause = false;
        if (LocalDataManager.getInstance().getLoginInfo() != null) {
            /*//TODO 需要添加目前的秀币
            *//*startActivity(CurrencyActivity.createIntent(getActivity(), LocalDataManager
                    .getInstance().getLoginInfo().getUserId()));*//*
            startActivity(SimpleWebViewActivity.createIntent(getActivity(), Const.RankPageUrl+getmAnchorId(),""));*/
            /*CheckEventSwitch(EventUtil.RankID, new EventCheckCallback() {
                @Override
                public void eventSW(boolean sw) {
                    Log.i("RayTest",getClass().getSimpleName()+" CheckEventSwitch: "+sw);
                    if(sw){
                        startActivity(SimpleWebViewActivity.createIntent(getActivity(), Const.RankPageUrl+getmAnchorId(),""));
                    }else{
                        startActivity(CurrencyActivity.createIntent(getActivity(), getmAnchorId()));
                    }
                }
            });*/
            CheckEventSwitch(EventUtil.RankID, new EventCheckCallback() {
                @Override
                public void eventSW(boolean sw) {
                    Log.i("RayTest",getClass().getSimpleName()+" CheckEventSwitch: "+sw);
                    if(sw){

                        simpleWebDialog.showWebContent(getActivity().getSupportFragmentManager(),Const.RankPageUrl+getmAnchorId(),SimpleWebDialog.TYPE_WEB_NORMAL);
                        //startActivity(SimpleWebViewActivity.createIntent(getActivity(), Const.RankPageUrl+getmAnchorId(),""));
                    }else{
                        startActivity(CurrencyActivity.createIntent(getActivity(), getmAnchorId()));
                    }
                }
            });
        }
    }

    @Override
    protected int getRoomType() {
        return RoomActivity.TYPE_PUBLISH_LIVE;
    }

    @Override
    protected void parseArguments(Bundle bundle) {
        //Empty
    }

    @Override
    protected String getWsRoomId() {
        return LocalDataManager.getInstance().getLoginInfo().getCurrentRoomNum();
    }

    @Override
    protected String getWsUserId() {
        return LocalDataManager.getInstance().getLoginInfo().getUserId();
    }

    //刷新余额
    @Override
    protected void updateBalance(double coinbalance) {
        LoginInfo loginInfo = LocalDataManager.getInstance().getLoginInfo();
        loginInfo.setTotalBalance(coinbalance);
        LocalDataManager.getInstance().saveLoginInfo(loginInfo);
    }


    @Override
    protected void setMute(boolean closeMute) {
//        mRTCStreamingManager.mute(closeMute);
        libPublisher.SmartPublisherSetMute(closeMute ? 1 : 0);
    }

    @Override
    protected void sendDanmu(String roomid, String content) {
        presenter.sendFlyDanMuMsg(roomid, content);
    }

    @Override
    protected void stopPublishLive() {
        Log.i("RayTest",getClass().getSimpleName()+" stopPublishLive ....");
        StopPublish();
    }

    @Override
    public void onDestroyView() {
        if (isStart) {
            isStart = false;
            StopPublish();
            Log.i(LOG_TAG, "onDestroy StopPublish");
        }
        //handler = null;
        presenter.unsubscribeTasks();
        //ViewPagerOnResponse = null;

        //mEncoder.close();
        /*        SetupCreateRommRequest = null;
        responseListen = null;
        responseErrorListen = null;*/
        super.onDestroyView();
    }

    @Override
    protected void setupLiveContent(String liveStatus, String liveMsg) {
        getStartTimeView().setText(StartTime+liveMsg);
        if(liveStatus==null || liveStatus.equals("n")){
            presenter.sendLiveCheck();
        }
    }

    @Override
    public void finishRoom(int roomType) {
        if(roomType==RoomActivity.TYPE_PUBLISH_LIVE){
            setupEndRoomTime();
        }
    }

    private void initOperationBars(@NonNull final View view) {
        //相机和闪光
        final ImageButton imgbtnCamera = $(view, R.id.room_imgbtn_flash);
        RxView.clicks(imgbtnCamera)
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (popupWindowCamera == null) {
                            View view = LayoutInflater.from(getActivity())
                                    .inflate(R.layout.popup_room_camera, null);
//                            闪光灯
                            RxView.clicks($(view, R.id.popup_menu_flash))
                                    .subscribeOn(AndroidSchedulers.mainThread())
                                    .observeOn(Schedulers.io())
                                    .subscribe(new Action1<Void>() {
                                        @Override
                                        public void call(Void aVoid) {
                                            if (!mIsTorchOn) {
                                                openLightOn();
                                            } else {
                                                closeLightOff();
                                            }
                                        }
                                    });
//                            美颜
                            RxView.clicks($(view, R.id.popup_menu_beautiful))
                                    .subscribeOn(AndroidSchedulers.mainThread())
                                    .observeOn(Schedulers.io())
                                    .subscribe(new Action1<Void>() {
                                        @Override
                                        public void call(Void aVoid) {

                                        }
                                    });

//                             聲音開關
                            if (swSound == null)
                                swSound = (Switch) view.findViewById(R.id.sw_live_menu_sound);
                            swSound.setTextOn("開啟");
                            swSound.setTextOff("關閉");
                            swSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (buttonView.isChecked()) {

                                        libPublisher.SmartPublisherSetMute(0);
                                        toastShort("聲音開啟");

                                        //startDaNiuPublish(streamJsonStrFromServer);

                                    } else {

                                        libPublisher.SmartPublisherSetMute(1);
                                        toastShort("聲音關閉");
                                    }

                                }
                            });
//                            摄像头翻转的哦哦哦哦哦
                            RxView.clicks($(view, R.id.popup_menu_reverse_camera))
                                    .subscribe(new Action1<Void>() {
                                        @Override
                                        public void call(Void aVoid) {
                                            if (canReverseCamera) {
                                                try {
//                                                    翻转摄像头
                                                    changeCamera();
                                                } catch (IOException e) {
                                                    L.e(LOG_TAG, "Unable to reverse camera", e);
                                                }
                                            }
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            L.e(LOG_TAG, "Reverse camera error!", throwable);
                                        }
                                    });
//                            关闭私密哦~
                            if (createRoomBean.getPrivatemsg().equals(getString(R.string.room_create_isprivate))) {
                                RxView.clicks($(view, R.id.popup_menu_closeprivate))
                                        .subscribe(new Action1<Void>() {
                                            @Override
                                            public void call(Void aVoid) {
                                            }
                                        }, new Action1<Throwable>() {
                                            @Override
                                            public void call(Throwable throwable) {
                                                L.e(LOG_TAG, "Reverse camera error!", throwable);
                                            }
                                        });
                            } else {
                                $(view, R.id.popup_menu_closeprivate).setVisibility(View.GONE);
                            }

                            popupWindowCamera = new PopupWindow(view, ViewGroup.LayoutParams
                                    .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                        }
                        Log.i("RayTest","SettingShowing "+SettingShowing);
                       if (!popupWindowCamera.isShowing() ) {
//                            popupWindowCamera.showAsDropDown(imgbtnCamera);
                            showPopupWindowAboveButton(popupWindowCamera, imgbtnCamera , view);
                        }
                    }
                });
    }

    private void changeCamera() throws IOException {
//        停止推流
//        mHandler.removeCallbacks(mSwitcher);
//        100毫秒之后开启
//        mHandler.postDelayed(mSwitcher, 100);
        // mSmartCameraView.setPreviewCallback(null);
        if (Camera.getNumberOfCameras() > 0) {
            switchCameraFace((getCamraId() + 1) % Camera.getNumberOfCameras());
        }
    }

    private void switchCameraFace(int id) {
        mSmartCameraView.setCameraId(id);
        mSmartCameraView.stopCamera();
        mSmartCameraView.startCamera();

    }

    /**
     * 用于接收Activity发来的消息，终止直播。
     * 由于调用这个方法即表示不再推流，所以可以在这里直接关闭相机、闪光灯等，可以提高退出Activity的延迟。
     */
    public void prepareExit() {
        timingLogger.reset(TIMING_LOG_TAG, "PublishFragment.onDestroyView");
        Observable.just(isStart)
                .filter(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        return isStart;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        TimingLogger logger = new TimingLogger(TIMING_LOG_TAG,
                                "SmartPublisherClose");
//                        mRTCStreamingManager.stopStreaming();
                        logger.addSplit("StopPublish");
                        logger.dumpToLog();
                    }
                });

        timingLogger.addSplit("stop native publish");
        //Camera的操作放到主线程，保证下次进入时相机不是被占用的状态
        timingLogger.addSplit("release camera");
        timingLogger.dumpToLog();
    }

    @Override
    protected boolean shouldSendHeartRequest() {
        //直播页面永远不发送
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsActivityPaused = false;


    }

    @Override
    public void onPause() {
        super.onPause();
        mIsActivityPaused = true;
    }

    public void restartPublish(){
        readPublishConfig();
        if (isStart) {
            isStart = false;
            StopPublish();
            Log.i(LOG_TAG, "onDestroy StopPublish");
        }
        //presenter.unsubscribeTasks();
        startDaNiuPublish(streamJsonStrFromServer);
        setSmartCameraView();
       /* libPublisher.SmartPublisherStop();
        isStart = false;
        startDaNiuPublish(streamJsonStrFromServer);*/
        setMagicFilter();

    }
    //suface發生變化？ camera也要重新init
    /*private void initCamera(SurfaceHolder holder) {
        Log.i(LOG_TAG, "initCamera..");
        if (mPreviewRunning)
            mSmartCameraView.stopCamera();

        Camera.Parameters parameters;

        //mGPUImage = new GPUImage(getActivity());

        int bufferSize = (((videoWidth | 0xf) + 1) * videoHight * ImageFormat.getBitsPerPixel(parameters.getPreviewFormat())) / 8;

        mCamera.addCallbackBuffer(new byte[bufferSize]);
        //mCamera.addCallbackBuffer(buffer);
        mCamera.setPreviewCallbackWithBuffer(this);
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (Exception ex) {
            // TODO Auto-generated catch block
            if (null != mCamera) {
                mCamera.release();
                mCamera = null;
            }
            ex.printStackTrace();
        }
        mCamera.startPreview();
        mCamera.autoFocus(myAutoFocusCallback);

        mPreviewRunning = true;
    }*/


    //設置攝像頭的fps
    /*private void SetCameraFPS(Camera.Parameters parameters) {
        if (parameters == null)
            return;

        int[] findRange = null;

        int defFPS = 20 * 1000;

        List<int[]> fpsList = parameters.getSupportedPreviewFpsRange();
        if (fpsList != null && fpsList.size() > 0) {
            for (int i = 0; i < fpsList.size(); ++i) {
                int[] range = fpsList.get(i);
                if (range != null
                        && Camera.Parameters.PREVIEW_FPS_MIN_INDEX < range.length
                        && Camera.Parameters.PREVIEW_FPS_MAX_INDEX < range.length) {
                    Log.i(LOG_TAG, "Camera index:" + i + " support min fps:" + range[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]);

                    Log.i(LOG_TAG, "Camera index:" + i + " support max fps:" + range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
                    Log.i("RayTest", "Camera index:" + i + " support min fps:" + range[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]);

                    Log.i("RayTest", "Camera index:" + i + " support max fps:" + range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
                    if (findRange == null) {
                        if (defFPS <= range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]) {
                            findRange = range;

                            Log.i(LOG_TAG, "Camera found appropriate fps, min fps:" + range[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]
                                    + " ,max fps:" + range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
                        }
                    }
                }
            }
        }

        if (findRange != null) {
            parameters.setPreviewFpsRange(findRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX], findRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
        }
    }*/

    public void Logout() {
        toastShort("您的帳號在別處登錄了");
        getActivity().startActivity(LoginSelectActivity.createIntent(getActivity()));
        ((RoomActivity)getActivity()).sendFinishBroadcast(LoginSelectActivity.class.getSimpleName());
        /*cn.jpush.im.android.api.model.UserInfo info = JMessageClient.getMyInfo();
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
        }*/
    }

    private void setCameraDisplayOrientation(Activity activity, int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo (cameraId , info);
        int rotation = activity.getWindowManager ().getDefaultDisplay ().getRotation ();
        int degrees = 0;
        switch (rotation) {
            case 0:
                degrees = 0;
                break;
            case 1:
                degrees = 90;
                break;
            case 2:
                degrees = 180;
                break;
            case 3:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            // back-facing
            result = ( info.orientation - degrees + 360) % 360;
        }

        mDegree = result;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(LOG_TAG, "surfaceCreated..");

    }

   /* private void initEncode() {
        //int framerate = DEFAULT_FRAME_RATE;
        //int bitrate = 1250000;
        //mEncoder = new AvcEncoder(getContext(),videoWidth,videoHight,framerate,bitrate);
        //mEncoder.setEncodecallback(this);
    }*/

    private void preparePublish(){

        try {

            int CammeraIndex = findBackCamera();
            Log.i(LOG_TAG, "BackCamera: " + CammeraIndex);

            if (CammeraIndex == -1) {
                CammeraIndex = findFrontCamera();
                currentCameraType = BACK;
                if (CammeraIndex == -1) {
                    Log.i(LOG_TAG, "NO camera!!");
                    return;
                }
            } else {
                currentCameraType = FRONT;
            }

            Camera.Size size = selectCameraSize(false, mSmartCameraView.getCamera());
            if (!isDefaultSize) {
                videoHeight = size.height;
                videoWidth = size.width;
            }
            //initEncode();

            if (!isStart) {
                startDaNiuPublish(streamJsonStrFromServer);
                initSettingDialog();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  /*  @SuppressLint("NewApi")
    private boolean SupportAvcCodec() {
        if (Build.VERSION.SDK_INT >= 18) {
            for (int j = MediaCodecList.getCodecCount() - 1; j >= 0; j--) {
                MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(j);

                String[] types = codecInfo.getSupportedTypes();
                for (int i = 0; i < types.length; i++) {
                    if (types[i].equalsIgnoreCase("video/avc")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }*/

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(LOG_TAG, "surfaceChanged..");
        /*initCamera(holder);*/
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(LOG_TAG, "Surface Destroyed");
    }

    //是否有前置攝像頭
    private int findFrontCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();

        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return camIdx;
            }
        }
        return -1;
    }

    //是否有後置攝像頭
    private int findBackCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();

        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return camIdx;
            }
        }
        return -1;
    }

    //打開相應的攝像頭
    @SuppressLint("NewApi")
    private Camera openCamera(int type) {
        int frontIndex = -1;
        int backIndex = -1;
        int cameraCount = Camera.getNumberOfCameras();
        Log.i(LOG_TAG, "cameraCount: " + cameraCount);

        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int cameraIndex = 0; cameraIndex < cameraCount; cameraIndex++) {
            Camera.getCameraInfo(cameraIndex, info);

            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                frontIndex = cameraIndex;
            } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                backIndex = cameraIndex;
            }
        }

        currentCameraType = type;
        if (type == FRONT && frontIndex != -1) {
            curCameraIndex = frontIndex;
            return Camera.open(frontIndex);
        } else if (type == BACK && backIndex != -1) {
            curCameraIndex = backIndex;
            return Camera.open(backIndex);
        }
        return null;
    }

   /* @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        frameCount++;
        if (frameCount % 3000 == 0) {
            Log.i("OnPre", "gc+");
            System.gc();
            Log.i("OnPre", "gc-");
        }
        //Log.i("RayTest","data: " + data.length);
        if (data == null) {
            Camera.Parameters params = camera.getParameters();
            Camera.Size size = params.getPreviewSize();
            //int bufferSize = (((size.width | 0x1f) + 1) * size.height * ImageFormat.getBitsPerPixel(params.getPreviewFormat())) / 8;
            int bufferSize = (((size.width | 0x1f) + 1) * size.height * ImageFormat.getBitsPerPixel(params.getPreviewFormat())) / 8;
            camera.addCallbackBuffer(new byte[bufferSize]);
        } else {
            if (isStart) {
                //Camera.Parameters params = camera.getParameters();
                //Camera.Size size = params.getPreviewSize();
                //int bufferSize = (((size.width | 0x1f) + 1) * size.height * ImageFormat.getBitsPerPixel(params.getPreviewFormat())) / 8;
                libPublisher.SmartPublisherOnCaptureVideoData(data, data.length, currentCameraType, currentOrigentation);
                //mEncoder.EnCodePreviewFrame( data,  camera );
                //avcEncoder.offerEncoder(data);
            }
            camera.addCallbackBuffer(data);
        }
    }*/




    /**
     * 是否有網絡on
     *
     * @param context
     * @return
     */
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void StopPublish() {

        if (audioRecord_ != null) {
            Log.i(LOG_TAG, "surfaceDestroyed, call StopRecording..");
            audioRecord_.StopRecording();
            audioRecord_ = null;
        }

        if (libPublisher != null) {
            libPublisher.SmartPublisherStop();
        }
        if(presenter!=null)
            presenter.stopLive();
        /* if (mCamera == null)
            return;

        try {
            mCamera.setPreviewCallback(null);
            mCamera.cancelAutoFocus();
            mCamera.stopPreview();
            mCamera.setPreviewDisplay(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mCamera.release();
        mCamera = null;*/
        //  mSmartCameraView.setPreviewCallback(null);
        if(mSmartCameraView != null)
        {
            mSmartCameraView.stopCamera();
        }
    }

    public void startDaNiuPublish(String publishurl) {
        //swSound.setChecked(false);
        isStart = true;
        Log.i("RayTest","publishurl:"+publishurl);
        String hostName = getHostName(publishurl);
        String  pingVal  = executeCmd(publishurl,false);
        Log.i("RayTest","ping value: "+pingVal);
        getSpeed(hostName,pingVal);
        if (libPublisher != null) {
            //判斷是否錄製
//            ConfigRecorderFuntion();
            //這裡萎了擴展還是加上吧
            int audio_opt = 1;
            int video_opt = 1;
            if (pushType == 1) {
                video_opt = 0;
            } else if (pushType == 2) {
                audio_opt = 0;
            }
            //showCameraSize();
            if(config_kbps != -1) {
                switch (config_kbps){
                    case 1200:
                        DefaultFps = 15;
                        DefaultGop = 30;
                        break;
                    case 1700:
                        DefaultFps = 15;
                        DefaultGop = 30;
                        break;
                    case 2000:
                        DefaultFps = 18;
                        DefaultGop = 36;
                        break;
                    case 2200:
                        DefaultFps = 18;
                        DefaultGop = 36;
                        break;
                    default:
                        DefaultFps = 15;
                        DefaultGop = 30;
                        break;
                }

            }

             libPublisher.SmartPublisherSetFPS(DefaultFps);
            libPublisher.SmartPublisherSetGopInterval(DefaultGop);
            //libPublisher.SmartPublisherSetClippingMode(1);
            //libPublisher.SmartPublisherSetSpeexEncoderQuality(10);
            libPublisher.SmartPublisherSetAGC(1);
            libPublisher.SmartPublisherSetNoiseSuppression(1);
            if(!config_support_HW_Encode) {

               /* libPublisher.SmartPublisherSetSWVideoBitRate(1200,1200);
                libPublisher.SmartPublisherSetSWVideoEncoderProfile(1);
                libPublisher.SmartPublisherSetFPS(DefaultFps);
                libPublisher.SmartPublisherSetGopInterval(60);*/
            }

            libPublisher.SmartPublisherInit(getActivity(), audio_opt, video_opt, videoWidth, videoHeight);
            int hwHWKbps;
            if(config_kbps == -1) {
                hwHWKbps = setHardwareEncoderKbps(videoWidth, videoHeight);
                settings.edit().putInt("kbps",hwHWKbps).commit();

            }
            else
                hwHWKbps = config_kbps;

            int isSupportHWEncoder = libPublisher.SetSmartPublisherVideoHWEncoder(hwHWKbps);
            if (isSupportHWEncoder == 0) {
                config_support_HW_Encode = true;
                Log.i(LOG_TAG, "Great, it supports hardware encoder!");
            }else
                config_support_HW_Encode = false;
            EventHander eventHandle = new EventHander();
            libPublisher.SetSmartPublisherEventCallback(eventHandle);
            // IF not set url or url is empty, it will not publish stream
            // if ( libPublisher.SmartPublisherSetURL("") != 0 )
            Log.i("mrl", LocalDataManager.getInstance().getLoginInfo().getCurrentRoomNum());
//            9811481163712
            if (libPublisher.SmartPublisherSetURL(publishurl) != 0) {
                Log.e(LOG_TAG, "Failed to set publish stream URL..");
            }

            int isStarted = libPublisher.SmartPublisherStart();
            if (isStarted != 0) {
                Log.e(LOG_TAG, "Failed to publish stream..");
            }
        }
        if (pushType == 0 || pushType == 1) {
            CheckInitAudioRecorder();    //enable pure video publisher..
        }
    }

    //Configure recorder related function.

    private int setHardwareEncoderKbps(int width, int height) {
        int hwEncoderKpbs = 0;
        Log.i("RayTest","setHardwareEncoderKbps width:"+ width);
        switch (width) {
            case 176:
                hwEncoderKpbs = 300;
                break;
            case 320:
                hwEncoderKpbs = 500;
                break;
            case 640:
                hwEncoderKpbs = 2000;
                break;
            case 1080:
                hwEncoderKpbs = 2000;
                break;
            case 1280:
                hwEncoderKpbs = 2000;
                break;
            default:
                hwEncoderKpbs = 2000;
        }

        return hwEncoderKpbs;
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    @Override
    public void SmartCameraSurfaceCreated(GL10 gl, EGLConfig config) {

        preparePublish();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return false;
    }

    public TextView getStartTimeView() {
        return (TextView) getView().findViewById(R.id.tv_start_time);
    }

    @Override
    public void WebDialogDismiss() {
        if(OpenRank)
            OpenRank = false;
    }


    public class EventHander implements SmartEventCallback {
        @Override
        public void onCallback(int code, long param1, long param2, String param3, String param4, Object param5) {

            switch (code) {

                case EVENTID.EVENT_DANIULIVE_ERC_PUBLISHER_STARTED:
                    txt = "開始。。";
                    Log.i("mrlpu", "開始。。");
                    Log.i("RayTest","開始推流...");
                    RoomInfoTmp.RtmpEventLog = "StartPublish";
                    setMagicFilter();
                    presenter.getStartPublishTime(getWsUserId());
                    break;
                case EVENTID.EVENT_DANIULIVE_ERC_PUBLISHER_CONNECTING:
                    txt = "連接中。。";
                    Log.i("mrlpu", "連接中。。");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RoomInfoTmp.RtmpEventLog = "Connecting";
                        }
                    });
                    break;
                case EVENTID.EVENT_DANIULIVE_ERC_PUBLISHER_CONNECTION_FAILED:
                    txt = "連接失敗。。";
                    Log.i("mrlpu", "連接失敗。。");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RoomInfoTmp.RtmpEventLog = "ConnectionFailed";
                        }
                    });
                    break;
                case EVENTID.EVENT_DANIULIVE_ERC_PUBLISHER_CONNECTED:
                    txt = "连接成功。。";
                    Log.i("mrlpu", "連接成功。。");
                    toastShort("開始直播啦");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RoomInfoTmp.RtmpEventLog = "ConnectionSucceeded";
                        }
                    });
                    presenter.startLive();
                    break;
                case EVENTID.EVENT_DANIULIVE_ERC_PUBLISHER_DISCONNECTED:
                    txt = "連接斷開。。";
                    Log.i("mrlpu", "連接斷開。。");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RoomInfoTmp.RtmpEventLog = "Disconnect";
                        }
                    });
                    break;
                case EVENTID.EVENT_DANIULIVE_ERC_PUBLISHER_STOP:
                    txt = "關閉。。";
                    Log.i("mrlpu", "關閉。。");
                    Log.i("RayTest", "EVENT_DANIULIVE_ERC_PUBLISHER_STOP。。");
                    Log.i("RayTest","Token:  "+LocalDataManager.getInstance().getLoginInfo().getToken());
                    presenter.stopLive();
                    break;
                case EVENTID.EVENT_DANIULIVE_ERC_PUBLISHER_RECORDER_START_NEW_FILE:
                    Log.i(LOG_TAG, "開始一個新的錄像文件 : " + param3);
                    txt = "開始一個新的錄像文件。。";
                    break;
                case EVENTID.EVENT_DANIULIVE_ERC_PUBLISHER_ONE_RECORDER_FILE_FINISHED:
                    Log.i(LOG_TAG, "已生成一個錄像文件 : " + param3);
                    txt = "已生成一個錄像文件。。";
                    break;
            }
            String str = "當前回調狀態：" + txt;
            Log.i("mrlp", str);
        }
    }

    void CheckInitAudioRecorder() {
        if (audioRecord_ == null) {
            audioRecord_ = new NTAudioRecord(getActivity().getApplicationContext(), 1);
        }
        if (audioRecord_ != null) {
            Log.i(LOG_TAG, "onCreate, call executeAudioRecordMethod..");
            audioRecord_.executeAudioRecordMethod();

        }

    }

    /**
     * 開啟閃光燈
     */
    private void openLightOn() {
        /*if (mCamera == null) {
            mCamera = openCamera(currentCameraType);
        }
        Camera.Parameters parameters;
        try {
            parameters = mCamera.getParameters();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        Log.i("mrl", "openLightOn" + parameters.getFlashMode());
        if (parameters.getFlashMode() == null || !parameters.getFlashMode().equals(FLASH_MODE_OFF))
            return;
        if (mPreviewRunning)
            mCamera.stopPreview();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
        mIsTorchOn = true;*/
    }

    /**
     * 關閉閃光燈
     */
    private void closeLightOff() {
       /* if (null == mCamera) {
            mCamera = Camera.open();
        }
        if (mPreviewRunning)
            mCamera.stopPreview();
        Camera.Parameters parameters;
        try {
            parameters = mCamera.getParameters();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        parameters.setFlashMode(FLASH_MODE_OFF);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
        mIsTorchOn = false;*/
    }

    /**
     * 连续自动对焦
     */
    private String getAutoFocusMode(Camera.Parameters mParameters) {
        if (mParameters != null) {
            //持续对焦是指当场景发生变化时，相机会主动去调节焦距来达到被拍摄的物体始终是清晰的状态。
            List<String> focusModes = mParameters.getSupportedFocusModes();
            if ((Build.MODEL.startsWith("GT-I950") || Build.MODEL.endsWith("SCH-I959") || Build.MODEL.endsWith("MEIZU MX3")) && isSupported(focusModes, "continuous-picture")) {
                return "continuous-picture";
            } else if (isSupported(focusModes, "continuous-video")) {
                return "continuous-video";
            } else if (isSupported(focusModes, "auto")) {
                return "auto";
            }
        }
        return null;
    }

    /**
     * 检测是否支持指定特性
     */
    private boolean isSupported(List<String> list, String key) {
        return list != null && list.contains(key);
    }

    public void showCameraSize() {
        List<Camera.Size> sizes = mSmartCameraView.getCamera().getParameters().getSupportedPictureSizes();
        for (Camera.Size size : sizes) {
            Log.i("mrlheight", "PictureSizes: " + size.width + "  " + size.height);
            Log.i("RayTest", "PictureSizes" + size.width + "  " + size.height);
        }
        List<Camera.Size> sizess = mSmartCameraView.getCamera().getParameters().getSupportedPreviewSizes();
        for (Camera.Size size : sizess) {
            Log.i("mrlheight", "PreviewSizes: " + size.width + "  " + size.height);
            Log.i("RayTest", "PreviewSizes" + size.width + "  " + size.height);
        }
    }

    /**
     * 是否是4:3
     *
     * @param is43
     * @return 可以用來錄製的攝像頭分辨率
     */
    public Camera.Size selectCameraSize(boolean is43, Camera camera) {
        List<Camera.Size> sizess = camera.getParameters().getSupportedPreviewSizes();
        List<Camera.Size> newSize = new ArrayList<>();
        for (Camera.Size size : sizess) {

            if (is43) {
                if (size.width * 3 == size.height * 4) {
                    newSize.add(size);
                    return size;
                }
            } else {
                if (size.width * 9 == size.height * 16) {
                    newSize.add(size);

                    return size;
                }
            }
        }

        return sizess.get(0);
    }


}

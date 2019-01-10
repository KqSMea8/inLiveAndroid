package tw.chiae.inlive.presentation.ui.room;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.magiepooh.recycleritemdecoration.ItemDecorations;
import com.google.gson.Gson;
import com.jakewharton.rxbinding.view.RxView;
import com.ms_square.etsyblur.BlurConfig;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.api.BasicCallback;*/
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.api.BasicCallback;
import de.greenrobot.event.EventBus;
import io.jchat.android.tools.BitmapLoader;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;
import rx.functions.Action1;
import rx.functions.Func1;
import tw.chiae.inlive.BeautyLiveApplication;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.AnchoBean;
import tw.chiae.inlive.data.bean.Danmu;
import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.data.bean.gift.SendGiftAction;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.bean.websocket.AudienceInfo;
import tw.chiae.inlive.data.bean.websocket.ErrorMsg;
import tw.chiae.inlive.data.bean.websocket.LightHeartMsg;
import tw.chiae.inlive.data.bean.websocket.LiveAudienceListMsg;
import tw.chiae.inlive.data.bean.websocket.RoomPublicMsg;
import tw.chiae.inlive.data.bean.websocket.SendGiftMsg;
import tw.chiae.inlive.data.bean.websocket.SystemMsg;
import tw.chiae.inlive.data.bean.websocket.SystemWelcome;
import tw.chiae.inlive.data.bean.websocket.UserPrvMsg;
import tw.chiae.inlive.data.bean.websocket.UserPublicMsg;
import tw.chiae.inlive.data.bean.websocket.WsLoginMsg;
import tw.chiae.inlive.data.bean.websocket.WsLoginOutMsg;
import tw.chiae.inlive.data.bean.websocket.WsRoomManageRequest;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.data.websocket.SocketConstants;
import tw.chiae.inlive.data.websocket.WebSocketService;
import tw.chiae.inlive.data.websocket.WsListener;
import tw.chiae.inlive.data.websocket.WsObjectPool;
import tw.chiae.inlive.domain.BlackList;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.nohttp.HttpListener;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.base.BaseFragment;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerAdapter;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerHolder;
import tw.chiae.inlive.presentation.ui.chatting.DropDownListView;
import tw.chiae.inlive.presentation.ui.chatting.RecordVoiceButton;
import tw.chiae.inlive.presentation.ui.chatting.utils.DialogCreator;
import tw.chiae.inlive.presentation.ui.chatting.utils.Event;
import tw.chiae.inlive.presentation.ui.chatting.utils.IdHelper;
import tw.chiae.inlive.presentation.ui.login.LoginSelectActivity;
import tw.chiae.inlive.presentation.ui.main.me.OtherUserActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.EventActivity;
import tw.chiae.inlive.presentation.ui.main.setting.CreateViewDialogFragment;
import tw.chiae.inlive.presentation.ui.main.setting.SmartAsyncPolicyHolder;
import tw.chiae.inlive.presentation.ui.room.gift.IAnimController;
import tw.chiae.inlive.presentation.ui.room.gift.IGiftAnimPlayer;
import tw.chiae.inlive.presentation.ui.room.gift.LocalAnimQueue;
import tw.chiae.inlive.presentation.ui.room.player.SimpleWebDialog;
import tw.chiae.inlive.presentation.ui.room.prvmsg.MsgListRoomAdapter;
import tw.chiae.inlive.presentation.ui.room.prvmsg.PrvListAdapter;
import tw.chiae.inlive.presentation.ui.room.pubmsg.MsgUtils;
import tw.chiae.inlive.presentation.ui.room.pubmsg.PublicChatAdapter;
import tw.chiae.inlive.presentation.ui.room.pubmsg.PublicChatHolder;
import tw.chiae.inlive.presentation.ui.widget.AdaptiveTextView;
import tw.chiae.inlive.presentation.ui.widget.UserInfoDialog;
import tw.chiae.inlive.presentation.ui.widget.roomanim.CarView;
import tw.chiae.inlive.presentation.ui.widget.roomanim.FireworksView;
import tw.chiae.inlive.presentation.ui.widget.roomanim.GenView;
import tw.chiae.inlive.presentation.ui.widget.roomanim.GitfSpecialsStop;
import tw.chiae.inlive.presentation.ui.widget.roomanim.HeartLoopView;
import tw.chiae.inlive.presentation.ui.widget.roomanim.PlaneImagerView;
import tw.chiae.inlive.presentation.ui.widget.roomanim.ShipView;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.EventChecker;
import tw.chiae.inlive.util.FrescoUtil;
import tw.chiae.inlive.util.L;
import tw.chiae.inlive.util.PicUtil;
import tw.chiae.inlive.util.TimingLogger;
import tw.chiae.inlive.util.danmu.DanmuControl;
import tw.inlive.cewebkit.CEWebActivity;
import tw.inlive.cewebkit.CEWebKit;

/**
 * 房间页面的基类，用于抽象三种情况下的公共操作。
 *
 * @author Muyangmin
 * @since 1.0.0
 */
public abstract class RoomFragment extends BaseFragment implements RoomActivity.HasInputLayout, HttpListener<Bitmap>, BaseUiInterface, GitfSpecialsStop, RoomFragmentInterface, PrvListAdapter.PrvListCallback, PrvListAdapter.resetCallback, CreateViewDialogFragment.dialogCallback, SimpleWebDialog.dialogListener {

    private static final String MsgIDs = "msgIDs";
    private static final String NICKNAME = "nickname";
    private static final int REQUEST_CODE_TAKE_PHOTO = 4;
    private static final int RESULT_CODE_SELECT_PICTURE = 8;
    private static final int RESULT_CODE_FRIEND_INFO = 17;
    //private static float publicCDNSpeedFloat = 0;
    //private static float publicServerSpeedFloat = 0;

    private UIHandler mUIHandler = new UIHandler(this);
    private boolean mIsSingle = true;
    private boolean mShowSoftInput = false;
    private MsgListRoomAdapter mChatAdapter;
    private Conversation mConv;
    private MyReceiver receiver;
    private cn.jpush.im.android.api.model.UserInfo mMyInfo;
    //private String draft;
    private String mTargetId;
    private String mTargetAppKey;
    private String mPhotoPath = null;

    Window mWindow;
    InputMethodManager mImm;

    private static String TAG = RoomFragment.class.getSimpleName();
    private NetworkReceiver mReceiver;
    private Activity mcontext;
    private BackgroundHandler mBackgroundHandler;
    private HandlerThread mThread;
    private static final int REFRESH_CONVERSATION_LIST = 0x3000;
    private static final int REFRESH_LAST_PAGE = 0x1023;
    private static final int UNREAD = 0x99999;
    private static final int READ = 0x99997;
    private String user_id = null;
    protected final String TIMING_LOG_TAG = "timing";
    /**
     * 用于监测启动和退出时的执行时间，为避免多次创建对象，一个Fragment实例仅使用一个对象。
     * 父类和子类都需要在启动和退出方法中调用reset和dump，以确保每次打点的消息一定能被dump。
     */
    protected final TimingLogger timingLogger = new TimingLogger(TIMING_LOG_TAG, "Not Initialized");

    protected WebSocketService wsService;
    private String mRoomId;
    protected AdaptiveTextView tvGold;
    protected TextView  tvChatName, mChatTitle, unRead, newMsg;
    private ImageButton ibBack, ibToBack, ibSysBack;
    private AnchoBean mAnchoBean;
    private Button mSendMsgBtn;

    protected boolean isFirstVBalance = false;
    protected Double vFirstVBalance = Double.valueOf(0);


    private IAnimController localGiftController;

    public LinearLayout llOperationBar, mRankLay;

    public LinearLayout  llIn;
    public VisLinearLayout  llSys;
    public VisRelativeLayout  rlPrvChat;
    public RelativeLayout llHeader,rlChatBar;
    //public RelativeLayout rlPrvChat;
    public NestedScrollView mRoomScroll;
    public EditText edtChatContent, mChatInputEt;
    private PublicChatAdapter publicChatAdapter;

    private DropDownListView mChatListView;

    private ListView roomShow;
    //private List<Conversation> mDatas = new ArrayList<Conversation>();
    //private PrvListAdapter mListAdapter;
    private Dialog mDialog;

    protected RecyclerView recyclerPublicChat;
    protected RelativeLayout mRoot;
    /**
     * 实时在线观看人数。
     */
    //protected TextView tvOnlineCount;
    private RecyclerView recyclerAudienceList,recyclerAudienceSideList;
    private AudienceAdapter audienceAdapter;

    protected int[] heartColorArray;
    private int defaultColorIndex;

    private boolean isKicked;

    private boolean isRoomAdmin = false;
    //    创建一个用户发送信息对象用来直接显示
    private UserPublicMsg userPublicMsg;
    //    创建一个布尔值判断只对userPublicMsg进行一次设置
    private boolean initUserPublicMsg = false;
    //    等级
    private int publicMsgLevel;
    //    id
    private String publicMsgId;
    //    name
    private String publicMsgName;
    // 主播的id
    private String mAnchorId;

    private TextView mWaterId, mWaterDate;
    //    弹幕开关
    private boolean danmuopenis = false;
    private boolean danmuMode = true;
    //      根据弹幕开关显示对应的弹幕开关样式
    private LinearLayout danmu_layout_close, danmu_layot_open;
    //      弹幕view
    public DanmakuView mDanmakuView;
    //   弹幕分析器
    public BaseDanmakuParser mParser;
    //    弹幕context
    public DanmakuContext mContext;

/*    private static int MaxDiff = 20;
    private static int MaxAllowSpeed = 100;*/

    //    弹幕对象
    //public Danmu danmu;
    //  头像请求
    Request<JSONObject> request;

    //    可能在上一个红包还没打开，下一个红包就来了，所以为了防止下一个红包还能使用，我们这里讲红包储存到一个集合里
    private List<SendGiftMsg> hongbaolist;

    //    通过socket监听主播退出
    public boolean isloginout = false;
    //    头像右下角的小角角
    private int[] starticon;

    //    头像右下角的星星
    private ImageView iconstart;

    //    头像栏的关注
    public ImageView toptabstart;
    //    礼物动画
    private RelativeLayout animLayout;
    //    烟花
    private FireworksView fireworks;
    //    灰机
    private PlaneImagerView plane;
    //    汽车
    private CarView car;
    //    船
    private ShipView ship;
    //    移动
    public RelativeLayout moveLayout;
    //    记录大礼物
    private List<Integer> giftSpecials;
    private List<String> giftFromUserName;
    private List<String> giftNameList;
    //    大礼物动画结束
    private boolean isgiftend = true;
    //    记录死亡
    private boolean isdeas = false;
    //    砖石
    private GenView gen;
    private boolean r = false;
    private boolean f = false;
    private String mMyName, mMyPassword;
    public UserPrvMsg mUserPrvMsg;
    public TextView tvUserCountType, tvCharmCount;
    public ImageView ivApproveId;
    //是否靜音
    public boolean closeMute;
    //    计算便宜量
    private float touchMoveX = -1, touchMoveY = -1;
    //    记录
    private boolean isHideMove;

    private ServiceConnection wsConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            wsService = ((WebSocketService.ServiceBinder) service).getService();
            RoomInfoTmp.webService = wsService;
            wsService.sendRequest(WsObjectPool.newLoginRequest(mRoomId, LocalDataManager.getInstance().getLoginInfo().getApproveid()));
            initWsListeners();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private final int MaxInputLength = 50;
    private int mSoundId;
    private boolean isPlayAudio = false;
    private SoundPool mSoundPool;
    private boolean KsEventActivate = false;
    //private View mKStarPortal;
    private TextView mMsgMore;
    private int noReadCount = 0;
    private boolean isManualScrollMode = false;
    //private LottieAnimationView lottie_anim;
    //private MvVideoView surface;


    //for 520 big
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mMediaPlayer;
    private ImageView big520_bg;
/*    private LottieAnimationView big520_car;
    private LottieAnimationView big520_star;*/
    private TextView big520_text;
    //private ValueAnimator valueAnimator;
    private ImageView mActiviteEvent;

    //private int HeartDurationTime=1000;
    private String publicMsgApproveid;
    private String[] sArtistType_cn = {"", "星級藝人", "普通藝人", "金牌藝人", "官方", "特約藝人"};
    private String[] sArtistType_tw = {"无", "星级艺人", "普通艺人", "金牌艺人", "官方", "特约艺人"};
    //private SimpleDraweeView big520_car_2;
    private HeartLoopView giftView, giftView2;
    //private RequestQueue requestQueue;
    private int durationValue;
    private String mRoomUserUid;
    private ImageView ksBigGift;
    //private ArrayList<Drawable> KsBigGiftdrawables;
    private boolean bigAnimStatus = false;
    private DecimalFormat mDecimalFormat = new DecimalFormat("#,###");
    ;
    //private ScheduledExecutorService autoRefreshService;
    private static final long AUTO_REFRESH_TIME = 10;
    private final String ROOM_FRAGMENT_REQ = "roomfragment_request";
    private com.yolanda.nohttp.rest.RequestQueue requestQueueAppcation;
    //private MyTimerTask task;
    private Timer timer;
    private int mDurationTime = 10000;
    private String BigGiftFolder = "ksing512";
    private TextView ksBigGiftText;
    private int HideUiOptions;
    private View decorView;
    private int ShowUiOption;
    //private static ImageView iv_speedtest_event;
    //private sStringRequestListener HotPointLinsten;
    //private sStringRequestErrorListener HotPointErrorLinsten;
    Date d = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM.dd/HH:mm:ss/z");
    private Thread threadIp;

    //private int getAnchorBalanceValue = 0;

    private Gson gson;
    private static UserInfo mTestInfo;
    private HashMap<String, String> mFuImgUrl;
    private int count;
    private mResponseListener OnResponse;
    //private static List<Bundle> EventRequests ;
    private RoomFragmentPresenter roomFragmentPresenter;
    private EventActivity eventsList;
    private MsgHandler msghandler ;
    private ImageView mMoreOption;
    private boolean isInputByKeyBoard = true;
    private PrvListAdapter mPriListAdapter;
    private Point point_screen;
    private ConvenientBanner mEventBanner;
    private ImageView ivUserList;
    private RelativeLayout rlAudiencelistLayout;
    private boolean isAudienceOpen = false;
    private AudienceSiderAdapter audienceSidebarAdapter;
    private ImageView  danmuSw;
    private ImageView imgbtnlist;
    private CreateViewDialogFragment dialogFragment;
    private InputMethodManager imm;
    private boolean isPriMode = false;
    private int edtChatContentText = 0 ;
    private ValueAnimator TextvalueAnimator;
/*    private DanmuAnimView mDanmuAnimTmpView;
    private DanmuAnimView mDanmuAnimView;*/
    private EnterView mEnterView;
    private SystemWelcome testSystemWelcome;
    private DanmuAnimView mDanmuAnimView;
    private UserPublicMsg testUserPublicMsg;
    private int mDanmuAnimViewWidth = 0;
    private int mDanmuAnimViewHeight = 0;
    private DanmuAnimView mDanmuAnimTmpView;
    private TextView tv_default_text;
    private int DanmuMaxInputLength = 30;
    private boolean isRunningUpdateBlackList = false;
    private RelativeLayout roomDanMu;
    private LinearLayout MainLayout ;
    private ImageView DanmuOff ;
    private ImageView Img_Danmu ;
    private ImageView ivCrown;
    private boolean UserInfoDialogShowing = false;
    private long lastClickTime;
    private static long diff = 2000;
    private ValueAnimator valueAnimator;
    private ValueAnimator.AnimatorUpdateListener animListener;
    private SimpleWebDialog simpleWebDialog;
    private boolean isCacheProfile = false;
    private int recyclerPublicChatHeight;


    @Override
    protected void initViews(View view) {
        mcontext = this.getActivity();
        imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        startPingIP();
        if (roomFragmentPresenter == null)
            roomFragmentPresenter = new RoomFragmentPresenter(this);
        initDrawView();
        HideUiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        ShowUiOption =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView = getActivity().getWindow().getDecorView();
        /*requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());*/
        requestQueueAppcation = NoHttp.newRequestQueue();
        checkActivateEvent();
        EventBus.getDefault().register(this);
        mThread = new HandlerThread("Work on RoomActivity");
        mThread.start();
        RoomInfoTmp.getAnchorBalanceValue = 0;
        RoomInfoTmp.coinValue = 0;
        mBackgroundHandler = new BackgroundHandler(mThread.getLooper());

        userPublicMsg = new UserPublicMsg();
//        红包集合实例化
        hongbaolist = new ArrayList<SendGiftMsg>();
//        特殊礼物集合
        giftSpecials = new ArrayList<>();
        giftFromUserName = new ArrayList<>();
        giftNameList = new ArrayList<>();
//        小星星
        starticon = new int[]{0, R.drawable.id_star, R.drawable.id_vip, R.drawable.id_gold, R.drawable.id_in, R.drawable.id_specialicon};
        iconstart = (ImageView) view.findViewById(R.id.img_user_star_type);
        if (iconstart != null) {
            iconstart.setImageResource(starticon[0]);
        }
        if (getRoomType() == RoomActivity.TYPE_PUBLISH_LIVE) {
            publicMsgLevel = Integer.parseInt(LocalDataManager.getInstance().getLoginInfo().getLevel());
            publicMsgId = LocalDataManager.getInstance().getLoginInfo().getUserId();
            publicMsgName = LocalDataManager.getInstance().getLoginInfo().getNickname();
        }
        publicMsgApproveid = LocalDataManager.getInstance().getLoginInfo().getApproveid();
        timingLogger.reset(TIMING_LOG_TAG, "RoomFragment#initViews");
        parseArguments(getArguments());
        timingLogger.addSplit("parseArguments");
        simpleWebDialog  = SimpleWebDialog.newInstance();
        simpleWebDialog.setDialogListener(this);
        heartColorArray = getResources().getIntArray(R.array.room_heart_colors);
//        heartColorArray = new int[colorRefs.length];
//        for (int i=0; i<colorRefs.length; i++){
//            heartColorArray[i] = ContextCompat.getColor(getActivity(), colorRefs[i]);
//        }
        L.v(false, LOG_TAG, "Heart colors:%s", Arrays.toString(heartColorArray));
        defaultColorIndex = (int) (Math.random() * heartColorArray.length);
        L.d(LOG_TAG, "Default color for this room : %s", defaultColorIndex);
        timingLogger.addSplit("init heart color.");
        mRoomId = getWsRoomId();
        mRoomUserUid = getWsUserId();
        //getHotPoint(mRoomUserUid);
        //mPrvChat = $(view, R.id.dialog_user_info_prv_chat);
        //llChatBar = $(view, R.id.room_ll_chat_bar);
        llOperationBar = $(view, R.id.room_ll_operation_bar);

        //mHeartAnim = $(view, R.id.room_heart_view);
        llHeader = $(view, R.id.room_header);

        mRankLay = $(view, R.id.room_coin_rank_lay);
        mRoot = $(view, R.id.room_fragment_root);
        mRoomScroll = $(view, R.id.room_scroll);
        tvGold = $(view, R.id.txt_gold_count);
        mWaterId = $(view, R.id.water_room_uid);
        mWaterId.setText("ID号: " + mAnchorId);
        mWaterDate = $(view, R.id.water_room_date);
        mWaterDate.setText(refFormatNowDate(System.currentTimeMillis()));
        ibBack = $(view, R.id.chat_back);
        tv_default_text = $(view,R.id.tv_default_chat_text);
        ibToBack = $(view, R.id.chat_toback);
        llIn = $(view, R.id.input);
        llSys = $(view, R.id.room_chat);
        llSys.setVisibility(View.GONE);
        ibSysBack = $(view, R.id.chat_sysback);
        toptabstart = $(view, R.id.room_top_bar_start_tv);
        roomShow = $(view, R.id.room_chat_show);
        unRead = $(view, R.id.unread);
        newMsg = $(view, R.id.some_msg);
        mMoreOption = $(view , R.id.jmui_add_file_btn);
        mChatListView = $(view, R.id.room_chatlist);
        mChatInputEt = $(view, R.id.room_input);
        mSendMsgBtn = $(view, R.id.room_chat_send);
        mChatTitle = $(view, R.id.tv_chat_roomName);
        //mKStarPortal = $(view, R.id.iv_kstar_portal);
        mActiviteEvent = $(view, R.id.iv_activate_event);
        mChatInputEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
        mChatInputEt.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mMsgMore = $(view, R.id.tv_msg_more);
        mChatInputEt.setSingleLine(false);
        dialogFragment = CreateViewDialogFragment.newInstance();
        dialogFragment.setDialogCallback(this);
        BlurConfig mBlurConfig = new BlurConfig.Builder()
                .overlayColor(Color.argb(136, 0, 0, 0))  // semi-transparent white color
                .asyncPolicy(SmartAsyncPolicyHolder.INSTANCE.smartAsyncPolicy())
                .debug(true)
                .allowFallback(false)
                .build();
        dialogFragment.setBlurConfig(mBlurConfig);
        roomDanMu = $(view,R.id.room_danmu);
        $(view, R.id.tv_msg_more);
        /*giftView = (HeartLoopView) view.findViewById(R.id.heartview);
        giftView2 = (HeartLoopView) view.findViewById(R.id.heartview2);
        giftView.initView();
        giftView2.initView();
*/
        mChatInputEt.setMaxLines(4);
        setNewUIView(view);
        llSys.setVisibilityListener(new VisibilitChangeListener() {
            @Override
            public void isVisibilityChange(int visibility) {
                if(visibility==View.GONE){
                    getUnReadMsgCount();
                    //showPriInputLayout(false);
                    showPriInputLayout(false);
                }
                if(visibility==View.VISIBLE){
                    showPriInputLayout(true);
                }
            }
        });
        rlPrvChat = $(view, R.id.layout_prv_chat);
        rlPrvChat.setVisibilityListener(new VisibilitChangeListener() {
            @Override
            public void isVisibilityChange(int visibility) {
                if(visibility==View.GONE && isPriMode ){
                    getUnReadMsgCount();
                    //showPriInputLayout(false);
                    showPriInputLayout(false);
                    isPriMode = false;
                }
            }
        });
         MainLayout = $(view,R.id.anim_main_layout);
         DanmuOff = $(view,R.id.room_img_danmu_off);
         Img_Danmu = $(view,R.id.room_img_danmu);

        rlPrvChat.setVisibility(View.GONE);

/*        llSys.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int newVis = llSys.getVisibility();
                if(newVis==View.GONE){
                    getUnReadMsgCount();
                    //showPriInputLayout(false);
                    Log.i("RayTest","llSys getViewTreeObserver addOnGlobalLayoutListener");
                }
            }
        });*/
        mMoreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (!isInputByKeyBoard) {
                    mChatView.isKeyBoard();
                    isInputByKeyBoard = true;
                    mChatView.showMoreMenu();
                } else {
                    //如果弹出软键盘 则隐藏软键盘
                    if (mChatView.getMoreMenu().getVisibility() != View.VISIBLE) {
                        dismissSoftInputAndShowMenu();
                        mChatView.focusToInput(false);
                        //如果弹出了更多选项菜单，则隐藏菜单并显示软键盘
                    } else {
                        showSoftInputAndDismissMenu();
                    }
                }*/
            }
        });

        mMsgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerPublicChat != null)
                    recyclerPublicChat.scrollToPosition(publicChatAdapter.getItemCount() - 1);
                mMsgMore.setVisibility(View.GONE);
                noReadCount = 0;
            }
        });
        mMyName = "user" + LocalDataManager.getInstance().getLoginInfo().getUserId();
        mMyPassword = "user" + LocalDataManager.getInstance().getLoginInfo().getUserId();
        if (JMessageClient.getMyInfo() == null) {
            for (int i = 0; i < 5; i++) {
                register();
                if (r = true) {
                    break;
                }
            }
            if (r = true) {
                for (int j = 0; j < 5; j++) {
                    login();
                    if (f = true) {
                        break;
                    }
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .build();
        } else {
            mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 1);
        }
        mActiviteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //starActivityEvent(6,LocalDataManager.getInstance().getLoginInfo().getUserId());
                startEvent6Event();
            }
        });
        mSoundId = mSoundPool.load(getContext(), R.raw.applause2, 1);
        getUnReadMsgCount();
        //roomFragmentPresenter.getUserInoList(mDatas);
        this.mWindow = getActivity().getWindow();
        this.mImm = (InputMethodManager) mcontext.getSystemService(Context.INPUT_METHOD_SERVICE);


        moveLayout = $(view, R.id.room_move_layout);
     /*   mKStarPortal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startKsEvent();
            }
        });*/
        $(view, R.id.play_click_view).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touchMoveX = event.getX();
                    touchMoveY = event.getY();
                    if (!getWsRoomId().equals(LocalDataManager.getInstance().getLoginInfo().getCurrentRoomNum()))
                        onRootClickAction();
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    if (touchMoveX > event.getX() + 122 && isHideMove) {
//                        显示
                        ObjectAnimator.ofFloat(moveLayout, "X", moveLayout.getWidth(), 0).setDuration(1000).start();
                        isHideMove = false;
                        return true;
                    } else if (touchMoveX < event.getX() - 122 && !isHideMove) {
//                        隐藏
                        ObjectAnimator.ofFloat(moveLayout, "X", 0, moveLayout.getWidth()).setDuration(1000).start();
                        isHideMove = true;
                        return true;
                    }
                    return false;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (touchMoveX > event.getX() + 122 && isHideMove) {
//                        显示
                        ObjectAnimator.ofFloat(moveLayout, "X", moveLayout.getWidth(), 0).setDuration(1000).start();
                        isHideMove = false;
                        return true;
                    } else if (touchMoveX < event.getX() - 122 && !isHideMove) {
//                        隐藏
                        ObjectAnimator.ofFloat(moveLayout, "X", 0, moveLayout.getWidth()).setDuration(1000).start();
                        isHideMove = true;
                        return true;
                    }
                    return false;
                }
                return false;
            }
        });
        //iv_speedtest_event = (ImageView) view.findViewById(R.id.iv_speed_event);
        big520_bg = (ImageView) view.findViewById(R.id.big520_bg);
        ksBigGift = (ImageView) view.findViewById(R.id.big_ks);
        //initBigGift();
        //big520_car_2 = (SimpleDraweeView) view.findViewById(R.id.big520_car2);
/*        big520_car = (LottieAnimationView) view.findViewById(R.id.big520_car);
        big520_star = (LottieAnimationView) view.findViewById(R.id.big520_star);*/
        big520_text = (TextView) view.findViewById(R.id.big_ks_text);
/*        big520_star.setImageAssetsFolder("images");
        big520_star.setImageAssetsFolder("images");
        big520_star.setAnimation("lottiejson/star.json");
        big520_car.setAnimation("lottiejson/data_0601.json");*/
        ksBigGiftText = (TextView) view.findViewById(R.id.big_ks_text);

        //big520_star.loop(true);
  /*      TextvalueAnimator = ValueAnimator.ofFloat(0, 1);
        TextvalueAnimator.setDuration(500);
        TextvalueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                big520_text.setAlpha(value);
            }
        });*/
        mEventBanner = $(view,R.id.banner_event);
        danmu_layout_close = $(view, R.id.room_danmu_close);
        danmu_layot_open = $(view, R.id.room_danmu_open);

        //roomFragmentPresenter.getUserInoList(mDatas);
        //mListAdapter = new PrvListAdapter(mcontext, mDatas, mFuImgUrl,this ,ConverToUserInf(mDatas));
/*        lottie_anim = (LottieAnimationView)view.findViewById(R.id.room_live_show_anim_layout_lottie);

        surface = (MvVideoView) view.findViewById(R.id.anim_surface);*/
        /*try {
        AssetFileDescriptor descriptor = getContext().getAssets().openFd("big520_5.mp4");
            long start = descriptor.getStartOffset();
            long end = descriptor.getLength();
            surface.setDataSource(descriptor.getFileDescriptor(), start, end);

        surface.setVolume(1.0f, 1.0f);
        surface.setLooping(false);

            surface.prepare(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    surface.seekTo(0);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        surface.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                surface.seekTo(0);
                surface.setVisibility(View.GONE);
                animend();
            }
        });*/


        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mConv.resetUnreadCount();
                    dismissSoftInput();
                    JMessageClient.exitConversation();
//                    //发送保存为草稿事件到会话列表
                    if (mIsSingle) {
                        EventBus.getDefault().post(new Event.DraftEvent(mTargetId, mTargetAppKey,
                                mChatInputEt.getText().toString()));
                    }
                } catch (Exception e) {
                }
                View view = getActivity().getWindow().peekDecorView();
                if (view != null) {
                    InputMethodManager inputmanger = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                showAnimOut(rlPrvChat);
                rlPrvChat.setVisibility(View.GONE);
                llOperationBar.setVisibility(View.VISIBLE);
                recyclerPublicChat.setVisibility(View.VISIBLE);
                showAnimIn(llSys);
                llSys.setVisibility(View.VISIBLE);
            }
        });
        ibToBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mConv.resetUnreadCount();
                    dismissSoftInput();
                    JMessageClient.exitConversation();
//                    //发送保存为草稿事件到会话列表
                    if (mIsSingle) {
                        EventBus.getDefault().post(new Event.DraftEvent(mTargetId, mTargetAppKey,
                                mChatInputEt.getText().toString()));
                    }
                } catch (Exception e) {
                }
                View view = getActivity().getWindow().peekDecorView();
                if (view != null) {
                    InputMethodManager inputmanger = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                showAnimOut(rlPrvChat);
                rlPrvChat.setVisibility(View.GONE);
//                llOperationBar.setVisibility(View.VISIBLE);
//                recyclerPublicChat.setVisibility(View.VISIBLE);
                showAnimIn(llSys);
                llSys.setVisibility(View.VISIBLE);
            }
        });
        ibSysBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAnimOut(llSys);
                Log.i("RayTest","setVisibility 2");
                llSys.setVisibility(View.GONE);
                llOperationBar.setVisibility(View.VISIBLE);
                recyclerPublicChat.setVisibility(View.VISIBLE);
                //initPrivateChatConversation();

            }
        });
        if(mRoot==null)
        Log.i("RayTest","mRoot null");
        if(mRoomScroll==null)
            Log.i("RayTest","mRoomScroll null");
        if (mRoot != null && mRoomScroll != null) {
            RxView.clicks(mRoomScroll)
                    .throttleFirst(Const.LIVE_ROOM_HEART_THROTTLE, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            Log.i("RayTest","onRootClickAction");
                            onRootClickAction();
                        }
                    });
        }

        mRoomScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
            }
        });

        mRoomScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        if (rlChatBar != null) {
            edtChatContent = $(view, R.id.room_edt_chat);
            ImageView btnSendChat = $(view, R.id.room_btn_send);
            edtChatContent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    edtChatContentText = s.length();
                    if(edtChatContentText>=MaxInputLength)
                        toastShort("聊天最多可輸入50字");
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            RxView.clicks(btnSendChat).throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                    .filter(new Func1<Void, Boolean>() {
                        @Override
                        public Boolean call(Void aVoid) {
                            userPublicMsg = new UserPublicMsg();
                            userPublicMsg.setLevel(publicMsgLevel);
                            userPublicMsg.setUserId(publicMsgId);
                            userPublicMsg.setFromClientName(publicMsgName);
                            userPublicMsg.setContent(edtChatContent.getText().toString());
//                            publicChatAdapter.appendData(userPublicMsg);
                            //Auto scroll to last
                            Log.i("RayTest","edtChatContent length: "+edtChatContent.getText().toString().trim().length());
                            if(edtChatContent.getText().toString().trim().length()<=0)
                                return false;
                            recyclerPublicChat.scrollToPosition(publicChatAdapter.getItemCount() - 1);
                            //CheckScrollVertically();
                            //CheckScroll();
                            return !TextUtils.isEmpty(edtChatContent.getText().toString());
                        }
                    })
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            String msg = edtChatContent.getText().toString();
                            if (Const.isDebugMode) {
                                getDebugData(new SystemWelcome());
                                getDebugData(new UserPublicMsg());
                                getDebugData(new LightHeartMsg());
                                getDebugData(new SendGiftMsg());
                                getDebugData(new SystemMsg());

                            }

                            if (edtChatContentText > MaxInputLength) {
                                toastShort("你輸入的字數為：" + edtChatContentText + " 已經超過了限制！(" + MaxInputLength + "字元)");
                                return;
                            }
                            if (danmuopenis && danmuMode) {
                                Log.i("RayTest","sendDanmu");
                                if (edtChatContentText > DanmuMaxInputLength) {
                                    toastShort( "彈幕最多可輸入" + DanmuMaxInputLength + "字元");
                                    return;
                                }
                                if (edtChatContent.getText().toString().trim().length() <=0 ) {
                                    toastShort("您不可發送空白內容");
                                    return;
                                }
                                sendDanmu(mRoomId, msg);
                                //showInputLayout(false);
                            } else {
                                Log.i("RayTest","newPublicMsgRequest1");
                                wsService.sendRequest(WsObjectPool.newPublicMsgRequest(msg, LocalDataManager.getInstance().getLoginInfo().getApproveid()));
                            }
                            edtChatContent.setText("");
                            //showInputLayout(false);

                            LoginInfo userinfo = LocalDataManager.getInstance().getLoginInfo();
                            //DisplayDanmuAnim(userinfo.getAvatar(),userinfo.getNickname(),msg);
                        }
                    });
        }

        RxView.clicks($(view, R.id.room_danmu))
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (danmuopenis) {
                            danmu_layout_close.setVisibility(View.VISIBLE);
                            danmu_layot_open.setVisibility(View.INVISIBLE);
                            danmuSw.setImageResource(R.drawable.ammobutton_off2);
                            tv_default_text.setHint(getResources().getString(R.string.tv_default_Room_chat_text));
                            if(edtChatContent!=null){
                                edtChatContent.setHint(getResources().getString(R.string.tv_default_Room_chat_text));
                            }
                            danmuopenis = false;
                        } else {
                            danmu_layot_open.setVisibility(View.VISIBLE);
                            danmu_layout_close.setVisibility(View.INVISIBLE);
                            danmuSw.setImageResource(R.drawable.ammobutton_on2);
                            tv_default_text.setHint(getResources().getString(R.string.tv_default_Room_danmu_text,1));
                            if(edtChatContent!=null){
                                edtChatContent.setHint(getResources().getString(R.string.tv_default_Room_danmu_text,1));
                            }
                            danmuopenis = true;
                        }

                    }
                });
        ImageButton imgbtn = $(view, R.id.room_imgbtn_public_chat);
        if (imgbtn != null) {
            RxView.clicks(imgbtn).throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            showInputLayout(true);
                        }
                    });
        }

        LinearLayout giftLayout = $(view, R.id.room_ll_gift_bar);
        if (giftLayout != null) {
            List<IGiftAnimPlayer> playerViews = new ArrayList<>();
            int childCount = giftLayout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                IGiftAnimPlayer player = (IGiftAnimPlayer) giftLayout.getChildAt(i);
                playerViews.add(player);
            }
            localGiftController = new LocalAnimQueue(playerViews);
        }


        recyclerPublicChat = $(view, R.id.room_recycler_chat);
        setRecyclerParams();
        if (recyclerPublicChat != null) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setStackFromEnd(false);
            recyclerPublicChat.setLayoutManager(linearLayoutManager);
            publicChatAdapter = new PublicChatAdapter(new ArrayList<RoomPublicMsg>(), getContext(), recyclerPublicChat);
            publicChatAdapter.setCallback(new PublicChatAdapter.appendDataCallback() {
                @Override
                public void CheckScrollVertically() {
                    CheckScroll();
                }
            });
            recyclerPublicChat.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    NotifyScrollVertically();
                }
            });
            recyclerPublicChat.setAdapter(publicChatAdapter);
        }

        animLayout = $(view, R.id.room_live_show_anim_layout);


        initAudienceBar(view);
        subscribeCloseBtn(view);
        subscribeShowBtn(view);
        subscribeMuteBtn(view);
        initReceiver();
        initreceiver();

        timingLogger.addSplit("init view & set listener");

        //TODO 添加网络连接状况检测
        initWebSocket();
        //tvCharmCount.setText("0");
        setCharmCount(0);
        timingLogger.addSplit("initWebSocket");
        L.d(LOG_TAG, "Timing log is enabled?%s", Log.isLoggable(TIMING_LOG_TAG, Log.VERBOSE));
        timingLogger.dumpToLog();

        initDanmuku(view);

//      设置红包头像的size
        size = (int) getActivity().getResources().getDimension(R.dimen.item_gift_icon_size);
        msghandler = new MsgHandler(this, getContext());
        Log.i("RayTest","mListAdapter 1 ");
        /*try {

            //mDatas = JMessageClient.getConversationList();
            if (mDatas != null && mDatas.size() > 1) {
                SortConvList sortList = new SortConvList();
                Collections.sort(mDatas, sortList);
//                mListAdapter.notifyDataSetChanged();
            }
            initDatas(mDatas);
            roomFragmentPresenter.getUserInoList(mDatas);
            //mListAdapter = new PrvListAdapter(mcontext, mDatas, this);
            //roomShow.setAdapter(mListAdapter);
            if (mListAdapter != null) {
                mListAdapter.sortConvList();
            }

            unRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListAdapter.resetConversation();
                }
            });
        } catch (NullPointerException e) {
        }*/
        unRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPriListAdapter!=null)
                     mPriListAdapter.resetConversation();
            }
        });
        roomShow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("RayTest","roomShow onItem Click");
                Log.i("RayTest","UpdateConversationUserInfo isCacheProfile:"+isCacheProfile);
                if(!isCacheProfile){
                    checkProfile();
                    return;
                }
                if (position > -1) {
                    //showPriInputLayout(true);
                    Conversation conv = mPriListAdapter.getmDatas().get(position).getJConversion();
                    if (null != conv) {
                        mTargetId = ((cn.jpush.im.android.api.model.UserInfo) conv.getTargetInfo()).getUserName();
                        mTargetAppKey = conv.getTargetAppKey();
                        String some= mTargetId;
                        some=some.replace("user","");
                        String draft = mPriListAdapter.getDraft(some);
                        showAnimIn(rlPrvChat);
                        llOperationBar.setVisibility(View.GONE);
                        recyclerPublicChat.setVisibility(View.GONE);
                        llSys.setVisibility(View.INVISIBLE);
                        rlPrvChat.setVisibility(View.VISIBLE);
                        try {
                            mMyInfo = JMessageClient.getMyInfo();
                            Log.i("RayTest","mMyName1:"+mMyInfo.getDisplayName()+" :"+mMyInfo.getAvatar()+" mTargetId:"+mTargetId);
                            if (!TextUtils.isEmpty(mTargetId)) {
                                mIsSingle = true;
                                mConv = JMessageClient.getSingleConversation(mTargetId, mTargetAppKey);
                                Log.i("RayTest","getSingleConversation ");
                                if (mConv != null) {
                                    cn.jpush.im.android.api.model.UserInfo userInfo = (cn.jpush.im.android.api.model.UserInfo) mConv.getTargetInfo();
                                    if (TextUtils.isEmpty(userInfo.getNickname())) {
                                        mChatTitle.setText(userInfo.getUserName());
                                    } else {
                                        mChatTitle.setText(userInfo.getNickname());
                                    }
                                } else {
                                    mConv = Conversation.createSingleConversation(mTargetId, mTargetAppKey);
                                    Log.i("RayTest","createSingleConversation ");
                                    cn.jpush.im.android.api.model.UserInfo userInfo = (cn.jpush.im.android.api.model.UserInfo) mConv.getTargetInfo();
                                    if (TextUtils.isEmpty(userInfo.getNickname())) {
                                        mChatTitle.setText(userInfo.getUserName());
                                    } else {
                                        mChatTitle.setText(userInfo.getNickname());
                                    }
                                }
                                if(mConv == null){
                                    Log.i("RayTest","Conversation fail");
                                    toastShort("目前伺服器發生問題 請稍後再試!");
                                }else {
                                    mChatAdapter = new MsgListRoomAdapter(mcontext, mTargetId, mTargetAppKey, longClickListener);
                                    Log.i("RayTest","mConv unused count: "+ mConv.getUnReadMsgCnt());
                                    if(mConv.getUnReadMsgCnt()>0)
                                        mPriListAdapter.notifyDataSetChanged();
                                }
                            }
                            if (draft != null && !TextUtils.isEmpty(draft)) {
                                Log.i("RayTest","draft:"+draft);
                                mChatInputEt.setText(draft);
                            }else{
                                mChatInputEt.setText("");
                            }

                        } catch (NullPointerException e) {
                        }
                    }else{
                        mTargetId= "user"+ mPriListAdapter.getmDatas().get(position).getUserID();
                        mTargetAppKey= null;
                        mConv = Conversation.createSingleConversation(mTargetId, mTargetAppKey);
                        showAnimIn(rlPrvChat);
                        llOperationBar.setVisibility(View.GONE);
                        recyclerPublicChat.setVisibility(View.GONE);
                        Log.i("RayTest","setVisibility 4");
                        llSys.setVisibility(View.INVISIBLE);
                        rlPrvChat.setVisibility(View.VISIBLE);

                        if (TextUtils.isEmpty( mPriListAdapter.getmDatas().get(position).getNickName())) {
                            mChatTitle.setText("");
                        } else {
                            mChatTitle.setText(mPriListAdapter.getmDatas().get(position).getNickName());
                        }
                        mChatAdapter = new MsgListRoomAdapter(mcontext, mTargetId, mTargetAppKey, longClickListener);

                    }

                    mChatListView.setAdapter(mChatAdapter);
                    if(mChatAdapter==null){
                        Log.i("RayTest","mChatAdapter null");
                    }else
                     mChatAdapter.initMediaPlayer();
                    // 监听下拉刷新
                    mChatListView.setOnDropDownListener(new DropDownListView.OnDropDownListener() {
                        @Override
                        public void onDropDown() {
                            mUIHandler.sendEmptyMessageDelayed(REFRESH_LAST_PAGE, 1000);
                        }
                    });
                    // 滑动到底部
                    mChatListView.clearFocus();
                    mChatListView.post(new Runnable() {
                        @Override
                        public void run() {
                            mChatListView.setSelection(mChatListView.getAdapter().getCount() - 1);
                            //mChatListView.smoothScrollToPosition(mChatListView.getAdapter().getCount() - 1);
                        }
                    });
                    mSendMsgBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ibBack.setFocusable(true);
                            ibBack.requestFocus();
                            String msgContent = mChatInputEt.getText().toString();
                            mChatInputEt.setText("");

                            mChatListView.clearFocus();
                            mChatListView.post(new Runnable() {
                                @Override
                                public void run() {
                                    mChatListView.setSelection(mChatListView.getAdapter().getCount() - 1);
                                }
                            });

                            if (msgContent.equals("")) {
                                return;
                            }
                            TextContent content = new TextContent(msgContent);
                            cn.jpush.im.android.api.model.Message msg = mConv.createSendMessage(content);

                            Log.i("RayTest","createSendMessage2:"+msg.getFromUser().getAvatar()+" mTargetId:"+mTargetId);
                            mChatAdapter.addMsgToList(msg);
                            JMessageClient.sendMessage(msg);

                        }
                    });
                }
            }

        });

        roomShow.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.i("RayTest","setOnItemLongClickListener "+position);
                if (position >= 0) {
                    List<PriConversation> mDatas = mPriListAdapter.getmDatas();
                    Log.i("RayTest","data2 size"+mDatas.size());
                    final PriConversation conv = mDatas.get(position);
                    if (conv != null) {
                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.i("RayTest","data2 onClick "+conv.getConversationID() );
                                //若會話不存在
                                if(conv.getConversationID()!=null || !(conv.getConversationID().equals(""))){
                                    Log.i("RayTest","data3 onClick " );
                                    conv.getJConversion().resetUnreadCount();

                                    PriConversation Conversation = new PriConversation();
                                    Conversation.setUserID(conv.getUserID());
                                    Conversation.setLastMsg(null);
                                    Conversation.setConversationID("");
                                    Conversation.setTime(0);
                                    Conversation.setAvt(conv.getAvt());
                                    Conversation.setToken("");
                                    Conversation.setApproveid(conv.getApproveid());
                                    Conversation.setNickName(conv.getNickName());
                                    JMessageClient.deleteSingleConversation("user"+conv.getUserID(),conv.getJConversion().getTargetAppKey());
                                    //mPriListAdapter.getmDatas().remove(position);
                                    mPriListAdapter.removeItem(position,Conversation);
                                    //mPriListAdapter.notifyDataSetChanged();

                                   /* if(LocalDataManager.getInstance().isOfficialAccount(Conversation.getUserID()))
                                        mPriListAdapter.addPriConversation(Conversation);*/
                                    mDialog.dismiss();
                                }
                            }
                        };

                        if(conv.getJConversion()!=null){
                            mDialog = DialogCreator.createDelConversationDialog(mcontext, conv.getJConversion().getTitle(),
                                    listener);
                            mDialog.show();
                            mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
                        }

                    }
                }
                return true;
            }
        });

        initPrivateChatConversation();
    }

    private void startPingIP() {
        //Log.i("RayTest","startPingIP: "+ executeCmd("",false));
    }

    /*private void RunCar520Car() {
        Log.i("RayTest","RunCar520Car...");
        big520_car.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.i("RayTest","onAnimationStart...");
                big520_text.setAlpha(0);
                big520_car.setVisibility(View.VISIBLE);
                big520_bg.setVisibility(View.VISIBLE);
                //big520_star.setVisibility(View.VISIBLE);
                big520_text.setVisibility(View.VISIBLE);
                //TextvalueAnimator.start();
                //big520_star.playAnimation();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.i("RayTest","onAnimationEnd...");
                big520_car.removeAnimatorListener(this);
                big520_text.setText("");
                big520_text.setAlpha(0);
                big520_bg.setVisibility(View.GONE);
                big520_car.setVisibility(View.GONE);
                //big520_star.setVisibility(View.GONE);
                big520_text.setVisibility(View.GONE);
                big520_car.cancelAnimation();
                animend();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        big520_car.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                big520_text.setAlpha(value);
            }
        });

        big520_car.playAnimation();
    }*/

    private void setCharmCount(long iCount) {
        RoomInfoTmp.HotpointValue = iCount;
        tvCharmCount.setText(mDecimalFormat.format(iCount) + "");
        //UpdateGifAnim(iCount);
    }

    private void setNewUIView(View view) {
        rlAudiencelistLayout = $(view, R.id.rl_audiencelist_layout);
         imgbtnlist = (ImageView) view.findViewById(R.id.room_imgbtn_list);
       RxView.clicks($(view, R.id.room_imgbtn_list))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                        if(isAudienceOpen) {
                            isAudienceOpen = false;
                            showAnimRightOut(rlAudiencelistLayout);
                            Log.i("RayTest","Close...");
                        }
                        else {
                            isAudienceOpen = true;
                            showAnimRightIn(rlAudiencelistLayout);
                            Log.i("RayTest","Open...");
                        }
                    }
                });
        rlChatBar = $(view, R.id.room_rl_chat_bar);
        if (rlChatBar != null) {
            edtChatContent = $(view, R.id.room_edt_chat);
            ImageView btnSendChat = $(view, R.id.room_btn_send);
            RxView.clicks(btnSendChat).throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                    .filter(new Func1<Void, Boolean>() {
                        @Override
                        public Boolean call(Void aVoid) {
                            userPublicMsg = new UserPublicMsg();
                            userPublicMsg.setLevel(publicMsgLevel);
                            userPublicMsg.setUserId(publicMsgId);
                            userPublicMsg.setFromClientName(publicMsgName);
                            userPublicMsg.setContent(edtChatContent.getText().toString());
//                            publicChatAdapter.appendData(userPublicMsg);
                            //Auto scroll to last
                            recyclerPublicChat.scrollToPosition(publicChatAdapter.getItemCount() - 1);
                            //CheckScrollVertically();
                            //CheckScroll();
                            return !TextUtils.isEmpty(edtChatContent.getText().toString());
                        }
                    })
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            String msg = edtChatContent.getText().toString();

                            if (edtChatContentText > MaxInputLength) {
                                toastShort("你輸入的字數為：" + edtChatContent.getText().length() + " 已經超過了限制！(" + MaxInputLength + "字元)");
                                return;
                            }
                            if (danmuopenis && danmuMode) {
                                sendDanmu(mRoomId, msg);
                            } else {
                                Log.i("RayTest","newPublicMsgRequest2");
                                wsService.sendRequest(WsObjectPool.newPublicMsgRequest(msg, LocalDataManager.getInstance().getLoginInfo().getApproveid()));
                            }
                            edtChatContent.setText("");
                            showInputLayout(false);
                        }
                    });
        }

        audienceSidebarAdapter = new AudienceSiderAdapter(this, new ArrayList<AudienceInfo>());
        recyclerAudienceSideList = $(view, R.id.recycle_audiencelist);
        recyclerAudienceSideList.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        recyclerAudienceSideList.addItemDecoration(ItemDecorations.vertical(getActivity())
                .type(0, R.drawable.divider_decoration_transparent_h1)
                .create());
        recyclerAudienceSideList.setAdapter(audienceSidebarAdapter);


        danmu_layout_close = $(view, R.id.room_danmu_close);
        danmu_layot_open = $(view, R.id.room_danmu_open);
        Log.i("RayTest","get danmuSW1: "+ Const.dammuSW);


        RxView.clicks($(view, R.id.room_danmu))
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (danmuopenis) {
                            danmu_layout_close.setVisibility(View.VISIBLE);
                            danmu_layot_open.setVisibility(View.INVISIBLE);
                            danmuopenis = false;
                            tv_default_text.setHint(getResources().getString(R.string.tv_default_Room_chat_text));
                            if(edtChatContent!=null){
                                edtChatContent.setHint(getResources().getString(R.string.tv_default_Room_chat_text));
                            }
                        } else {
                            danmu_layot_open.setVisibility(View.VISIBLE);
                            danmu_layout_close.setVisibility(View.INVISIBLE);
                            danmuopenis = true;
                            tv_default_text.setHint(getResources().getString(R.string.tv_default_Room_danmu_text,1));
                            if(edtChatContent!=null){
                                edtChatContent.setHint(getResources().getString(R.string.tv_default_Room_danmu_text,1));
                            }
                        }
                    }
                });
        LinearLayout ll_chat_view =  $(view, R.id.room_linear_public_chat);
        if (ll_chat_view != null) {
            RxView.clicks(ll_chat_view).throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            showInputLayout(true);
                        }
                    });
        }

        danmuSw = $(view,R.id.room_img_danmu);
        if(danmuSw==null)
            Log.i("RayTest","danmuSw null");


        showDanMU();
        if (danmuSw != null) {
            RxView.clicks(danmuSw).throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            if(danmuopenis) {
                                danmuopenis = false;
                                danmuSw.setImageResource(R.drawable.ammobutton_off2);
                                danmu_layout_close.setVisibility(View.VISIBLE);
                                danmu_layot_open.setVisibility(View.INVISIBLE);

                                tv_default_text.setHint(getResources().getString(R.string.tv_default_Room_chat_text));
                                if(edtChatContent!=null){
                                    edtChatContent.setHint(getResources().getString(R.string.tv_default_Room_chat_text));
                                }
                            }else{
                                danmuopenis=true;
                                danmu_layot_open.setVisibility(View.VISIBLE);
                                danmu_layout_close.setVisibility(View.INVISIBLE);
                                danmuSw.setImageResource(R.drawable.ammobutton_on2);
                                tv_default_text.setHint(getResources().getString(R.string.tv_default_Room_danmu_text,1));
                                if(edtChatContent!=null){
                                    edtChatContent.setHint(getResources().getString(R.string.tv_default_Room_danmu_text,1));
                                }
                            }
                        }
                    });
        }
    }

    private void showDanMU() {
        if(danmuopenis) {
            Log.i("RayTest","danmuopenis true...");
            danmuSw.setImageResource(R.drawable.ammobutton_on2);
            danmu_layot_open.setVisibility(View.VISIBLE);
            danmu_layout_close.setVisibility(View.INVISIBLE);
            tv_default_text.setHint(getResources().getString(R.string.tv_default_Room_danmu_text,1));
            if(edtChatContent!=null){
                edtChatContent.setHint(getResources().getString(R.string.tv_default_Room_danmu_text,1));
            }

        }else{
            Log.i("RayTest","danmuopenis false...");
            danmuSw.setImageResource(R.drawable.ammobutton_off2);
            danmu_layout_close.setVisibility(View.VISIBLE);
            danmu_layot_open.setVisibility(View.INVISIBLE);
            tv_default_text.setHint(getResources().getString(R.string.tv_default_Room_chat_text));
            if(edtChatContent!=null){
                edtChatContent.setHint(getResources().getString(R.string.tv_default_Room_chat_text));
            }
        }
    }

    private void showAnimRightOut(final View view) {
        Animation headerAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.room_right_out);
        headerAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rlAudiencelistLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(headerAnim);
    }

    private void showAnimRightIn(final View view) {
        rlAudiencelistLayout.setVisibility(View.VISIBLE);
        Animation headerAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.room_right_in);
        view.startAnimation(headerAnim);
        /*Animation headerAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.room_right_in);
        headerAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                rlAudiencelistLayout.setVisibility(View.VISIBLE);
                Log.i("RayTest","anim start In start");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.i("RayTest","anim start In end");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(headerAnim);*/
    }

    private void getUnReadMsgCount() {

        List<Conversation> mDatas = JMessageClient.getConversationList();
        int unreadCount = 0;
        if(mDatas==null || mDatas.size()==0){
            mDatas = new ArrayList<>();
        }
        for(Conversation con : mDatas){
            unreadCount += con.getUnReadMsgCnt();
        }
        if(unreadCount>0){
            newMsg.setVisibility(View.VISIBLE);
        }else
            newMsg.setVisibility(View.INVISIBLE);
    }
    private void checkProfile(){
        Log.i("RayTest","img path:"+LocalDataManager.getInstance().getLoginInfo().getAvatar());
        if(!isCacheProfile){
            FrescoUtil.CacheImgToDisk(LocalDataManager.getInstance().getLoginInfo().getAvatar(), new FrescoUtil.CacheCallbacek() {
                @Override
                public void cachePath(String path) {
                    Log.i("RayTest","cache:"+path);

                    JMessageClient.updateUserAvatar(new File(String.valueOf(path)), new BasicCallback() {
                        @Override
                        public void gotResult(int status, final String desc) {
                            if (status == 0) {
                                isCacheProfile = true;
                                Log.i("RayTest","isCacheProfile 更新成功");
                            }
                        }
                    });
                }
            },true);
        }
    }

    private void initPrivateChatConversation() {

        checkProfile();
        List<Conversation> mDatas = JMessageClient.getConversationList();
        if(mDatas==null || mDatas.size()==0){
            mDatas = new ArrayList<>();
        }
        for(Conversation con : mDatas){
            String some= ((cn.jpush.im.android.api.model.UserInfo) con.getTargetInfo()).getUserName();
        }
        if(mPriListAdapter!=null)
            roomFragmentPresenter.getUserInfoList(mDatas,mPriListAdapter.getDraftMap());
        else
            roomFragmentPresenter.getUserInfoList(mDatas,null);
    }

    @Override
    public void UpdateConversationUserInfo(List<PriConversation> userInfoList) {

        mPriListAdapter = new PrvListAdapter(mcontext, userInfoList, mFuImgUrl,this);
        mPriListAdapter.setAdapterCallback(this);
        roomShow.setAdapter(mPriListAdapter);
        for (int i = 0; i < userInfoList.size(); i++) {
            Conversation conv = userInfoList.get(i).getJConversion();
            if(conv==null)
                return;
            Log.i("RayTest","UpdateConversationUserInfo id: "+conv.getId()+" getUnReadMsgCnt:"+conv.getUnReadMsgCnt());
            if (conv.getUnReadMsgCnt() > 0) {
                newMsg.setVisibility(View.VISIBLE);
                break;
            } else
                newMsg.setVisibility(View.GONE);
        }
    }


    private void initDrawView() {
        /*drawView = new mDrawView(getActivity());
        int height = drawView.getWidth();
        drawView.setScreenScale(1024.0f / drawView.getRealScreenSize().y);
        FrameLayout.LayoutParams Layoutparam = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.NO_GRAVITY);
        getActivity().addContentView(drawView, Layoutparam);
        drawView.setZOrderOnTop(true);
        initAllScript();*/
}


    /*private void initBigGift() {
        try {
            List<String> files = getImage(getContext());
            KsBigGiftdrawables = new ArrayList<>();
            for(String str : files){
                InputStream ims = getContext().getAssets().open(BigGiftFolder+"/"+str);
                // load image as Drawable
                Drawable d = Drawable.createFromStream(ims, null);
                // set image to ImageView
                KsBigGiftdrawables.add(d);

            }
            Log.i("RayTest","ok "+ KsBigGiftdrawables.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
  /*  private static SpeedHandler speedhandler;
    private static SpeedTestRunnable SpeedRunnable;

    private void startSpeedTest() {
        if (speedhandler == null)
            speedhandler = new SpeedHandler(this);
        if (SpeedRunnable == null)
            SpeedRunnable = new SpeedTestRunnable(getRoomType());
        speedhandler.postDelayed(SpeedRunnable, 5000);
    }

    private void stopSpeedTest() {

        msghandler = null;
        if(SpeedRunnable!=null)
        speedhandler.removeCallbacks(SpeedRunnable);
        SpeedRunnable = null;
        speedhandler = null;
    }
*/
    @Override
    public void setCoinData(int currencyItemSum) {

    }

    @Override
    public void UpdateActivateEvent(EventActivity eventActivity) {
        this.eventsList = eventActivity;
        Log.i("RayTest","UpdateActivateEvent");
        for(EventActivity.EventItem item : eventActivity.getEvents()){

            switch (item.getId()){

                case 16:
                    Const.dammuSW = item.getStatus();
                    break;
                case 17:
                    Log.i("RayTest"," set VipEnterSW:"+Const.VipEnterSW+" id:"+item.getId());
                    Const.VipEnterSW = item.getStatus();
                    break;
                case  18:
                    Log.i("RayTest"," set LevelEnterSW:"+Const.LevelEnterSW+" id:"+item.getId());
                    Const.LevelEnterSW = item.getStatus();
                    break;
                default:
                    break;

            }
        }

        if(Const.dammuSW==0) {
            danmuMode = false;
        } else {
            danmuMode = true;
        }
        Log.i("RayTest"," dammuSW:"+Const.dammuSW);
        Log.i("RayTest"," VipEnterSW:"+Const.VipEnterSW);
        Log.i("RayTest"," LevelEnterSW:"+Const.LevelEnterSW);
        setDanmuMode();
        upDateEventBanner(getIsViewableEvent(eventsList));

/*        for (EventActivity.EventItem event : eventActivity.getEvents()) {
            switch (event.getId()) {

                case 1:
                    //Check_event(event.getId(),mKStarPortal,event.isViewable());
                    break;
                case 6:
                    Check_event(event.getId(), mActiviteEvent, event.isViewable());
                    break;
            }

            if ((event.getId() == 1)) {
                if (event.isViewable() == 1 && getRoomType() != RoomActivity.TYPE_PUBLISH_LIVE) {
                    KsEventActivate = true;
                    mKStarPortal.setVisibility(View.VISIBLE);
                } else {

                    KsEventActivate = false;
                    mKStarPortal.setVisibility(View.INVISIBLE);
                }
            }
            if (event.getId() == 2) {
                if(event.isViewable() == 1)
                    mActiviteEvent.setVisibility(View.VISIBLE);
                else {
                    mActiviteEvent.setVisibility(View.INVISIBLE);
                }
            }
        }*/
    }

    private void setDanmuMode() {
        if(danmuMode){
            Log.i("RayTest","彈幕開");
            //MainLayout.setVisibility(View.VISIBLE);
            mDanmuAnimView.setVisibility(View.VISIBLE);
            roomDanMu.setVisibility(View.VISIBLE);
            DanmuOff.setVisibility(View.GONE);
            Img_Danmu.setVisibility(View.VISIBLE);
        }else{
            //彈幕關
            Log.i("RayTest","彈幕關");
            //MainLayout.setVisibility(View.GONE);
            mDanmuAnimView.setVisibility(View.GONE);
            roomDanMu.setVisibility(View.GONE);
            DanmuOff.setVisibility(View.VISIBLE);
            Img_Danmu.setVisibility(View.GONE);
        }
        if(Const.LevelEnterSW==1||Const.VipEnterSW==1){
            mEnterView.setVisibility(View.VISIBLE);
        }else{
            mEnterView.setVisibility(View.VISIBLE);
        }
    }

    private void upDateEventBanner(final List<EventActivity.EventItem> EventItems) {
        if(EventItems.size()>1){
            mEventBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {

                @Override
                public NetworkImageHolderView createHolder() {
                    return new NetworkImageHolderView();
                }
            },EventItems).setPointViewVisible(true)    //设置指示器是否可见
                    .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R
                            .drawable.ic_page_indicator_focused})   //设置指示器圆点
                    .startTurning(5000)     //设置自动切换（同时设置了切换时间间隔）
                    .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                    .setManualPageable(true);
        }else{

            mEventBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {

                @Override
                public NetworkImageHolderView createHolder() {
                    return new NetworkImageHolderView();
                }
            },EventItems).setPointViewVisible(false)    //设置指示器是否可见
                    .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R
                            .drawable.ic_page_indicator_focused})   //设置指示器圆点
                    .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                    .setManualPageable(true);


        }


        mEventBanner.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                EventActivity.EventItem eventitem = EventItems.get(position);
                Log.i("RayTest","on click:"+eventitem.getId()+ " img url:"+eventitem.getImgUrl());
                gotoEvent(eventitem);

            }
        });
    }

    private void stopAnim() {
        Log.i("RayTest","強制停止 動畫");




        if(valueAnimator!=null){
            valueAnimator.cancel();
            bigAnimStatus = false;
            isgiftend = true;
            ksBigGift.setVisibility(View.GONE);
            ksBigGiftText.setVisibility(View.GONE);
            if(animListener!=null)
                valueAnimator.removeUpdateListener(animListener);
            decorView.setSystemUiVisibility(ShowUiOption);
           // animend();
            isgiftend = true;
            try {
                giftSpecials.clear();
                giftFromUserName.clear();
                giftNameList.clear();
            }catch (Exception e){
                Log.i("RayTest","no anim clear");
            }

            if (giftSpecials.size() > 0 && isgiftend) {
                isgiftend = false;
                showAnim(giftSpecials.get(0), giftFromUserName.get(0), giftNameList.get(0));
                return;
            }
            VisiblellHeader();
        }
    }

    private void gotoEvent(EventActivity.EventItem eventitem) {
        if(eventitem.getId()==1){
            startKsEvent(true);
        }else{
            ///startEventById(eventitem.getId());
            startEventById2(eventitem.getId());
        }
    }

    @Override
    public void resetConversationComplete() {
        getUnReadMsgCount();
    }

    @Override
    public void initPriChatInput() {
        mChatInputEt.setText("");
    }

    @Override
    public void onOKDialogcheck(Bundle bundle) {
        int itype = bundle.getInt("type");
        if(dialogFragment.getDialog()!=null){
            if(dialogFragment.getDialog().isShowing())
                dialogFragment.dismiss();
        }
        String blackId = bundle.getString("blackUserId");
        switch (itype){
            case CreateViewDialogFragment.TYPE_ADD_BLACKLIST:
                String uid = LocalDataManager.getInstance().getLoginInfo().getUserId();
                Log.i("RayTest","uid:"+uid+" blackid: "+blackId);
                if(LocalDataManager.getInstance().getLoginInfo().getUserId().equals(blackId)) {
                    toastShort("不可以加入自己黑名單");
                }else
                    roomFragmentPresenter.addBlackList(blackId);
                break;
            case CreateViewDialogFragment.TYPE_CANCEL_BLACKLIST:
                roomFragmentPresenter.delBlackList(blackId,0);
                break;
            default:
                break;
        }


    }

    @Override
    public void WebDialogDismiss() {

    }


    public class NetworkImageHolderView implements Holder<EventActivity.EventItem> {
        private View view;
        @Override
        public View createView(Context context) {
            view = LayoutInflater.from(context).inflate(R.layout.event_banner_item, null, false);
            return view;
        }

        @Override
        public void UpdateUI(Context context, int position, EventActivity.EventItem data) {
            Uri uri = Uri.parse(data.getImgUrl());
            ((SimpleDraweeView)view.findViewById(R.id.sdv_background)).setImageURI(uri);
        }
    }

    private List<EventActivity.EventItem> getIsViewableEvent(EventActivity eventsList) {
        ArrayList<EventActivity.EventItem> eventItems = new ArrayList<>();
        for(EventActivity.EventItem item : eventsList.getEvents()){
            String imgUrl = item.getImgUrl();
            if(imgUrl!=null &&(imgUrl.contains("http://")||imgUrl.contains("https://")) &&item.isViewable()==1){
                if(!( getRoomType()==RoomActivity.TYPE_PUBLISH_LIVE))
                    eventItems.add(item);
            }
        }
        return eventItems;
    }

    @Override
    public void SendReportComplete(String s) {
       /* if(EventRequests!=null)
            EventRequests.remove(0);*/
    }



    @Override
    public void notifChange(UserInfo data) {
        //mListAdapter.notifyDataSetChanged();
        Log.i("RayTest","mListAdapter 3 ");
    }


    private void printEvent(UserInfo event) {
        Log.i("RayTest", "===================");
        Log.i("RayTest", "event id: " + event.getId());
        Log.i("RayTest", "===================");
    }

    @Override
    public void upDatePriList(List<Conversation> newDataList) {
        Log.i("RayTest","mListAdapter 5 ");
       /* mDatas = newDataList;
        if(mListAdapter!=null)
            mListAdapter.notifyDataSetChanged();*/
    }

    @Override
    public void isUpdate() {
        Log.i("RayTest","mListAdapter 6 ");
        //mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void needDownloadInfo(String some) {
       // roomFragmentPresenter.DownLoadUsereInfo(some);
    }

   /* static class SpeedTestRunnable implements Runnable {

        private final int mRoomType;

        public SpeedTestRunnable(int roomType) {
            this.mRoomType = roomType;
        }

        public void run() {
            String CdnResult;
            if (mRoomType != RoomActivity.TYPE_VIEW_LIVE)
                CdnResult = executeCmd("ping -c1 push.inlive.tw", false);
            else
                CdnResult = executeCmd("ping -c1 pull.inlive.tw", false);
            String API1Result = executeCmd("ping -c1 api1.inlive.tw", false);
            String response = parseCmdIP(CdnResult, API1Result);
            //Log.i("RayTest","speed:"+response);、
            speedhandler.postDelayed(this, 5000);
        }
    }*/
    public String getHostName(String cmd){
        String prefix = "rtmp://";
        if(cmd.contains(prefix)){
            cmd = cmd.substring(prefix.length(),cmd.length());
            return cmd.substring(0,cmd.indexOf("/"));
        }
        return "";
    }
    public static String executeCmd(String cmd, boolean sudo) {
        String prefix = "rtmp://";
        Log.i("RayTest","cmd string1:"+cmd);
        if(cmd.contains(prefix)){
            cmd = cmd.substring(prefix.length(),cmd.length());
            cmd = "ping -c1 "+cmd.substring(0,cmd.indexOf("/"));
        }
        Log.i("RayTest","cmd string2:"+cmd);
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
                res += s ;
            }
            p.destroy();
            return res;
        } catch (UnknownHostException HostError) {
            Log.i("RayTest", "UnknownHostException");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("RayTest", "Exception");
        }
        return "";

    }

    public String getSpeed (String ip,String str){
        Log.i("RayTest","getSpeed："+str);
        String pattern_ttl = "time=(\\d{0,10}.\\d{0,10})";
        Matcher mMatcher = Pattern.compile(pattern_ttl).matcher(str);
        if (mMatcher.find() && mMatcher.groupCount()>=1) {
            String strTTL = mMatcher.group(1);
            Log.i("RayTest","strTTL: "+strTTL);
            sendSpeedReport(LocalDataManager.getInstance().getLoginInfo().getUserId(),ip,strTTL);
            return strTTL;
        }

        return "";
    }

   /* private static String parseCmdIP(String cdnResult, String serverResult) {

        String strIP = "";
        int IPstart = cdnResult.indexOf("(");
        int IPend = cdnResult.indexOf(")");
        if (IPstart > 1 && IPend > 1) {
            strIP = cdnResult.substring(IPstart + 1, IPend);
        }

        float cdnSpeed = getSpeedValue(cdnResult);
        float serverSpeed = getSpeedValue(serverResult);

        if (cdnSpeed < MaxAllowSpeed && serverSpeed < MaxAllowSpeed) {

            iv_speedtest_event.setVisibility(View.INVISIBLE);
            showWarningEvent(null);
        } else {
            Bundle bundle = new Bundle();
            if (cdnSpeed > MaxAllowSpeed && serverSpeed > MaxAllowSpeed) {
                bundle.putInt("status", 3);
            } else if (cdnSpeed > MaxAllowSpeed) {
                bundle.putInt("status", 1);
            } else if (serverSpeed > MaxAllowSpeed) {
                bundle.putInt("status", 2);
            }

            bundle.putString("ip", strIP);
            bundle.putString("speed", String.valueOf(cdnSpeed));
            bundle.putString("serverspeed", String.valueOf(serverSpeed));
            showWarningEvent(bundle);


        }

        if (strIP.equals(""))
            strIP = " 無法獲取IP";

        return "節點IP:" + strIP + "  Speed:" + cdnSpeed + " ms";
    }

    private static float getSpeedValue(String str) {
        String strSpeed = "";
        String strIP = "";
        int IPstart = str.indexOf("(");
        int IPend = str.indexOf(")");
        if (IPstart > 1 && IPend > 1) {
            strIP = str.substring(IPstart + 1, IPend);
        }


        int SpeedStart = str.indexOf("time=");
        int SpeedEnd = str.indexOf(" ms[@@][@@]");
        if (SpeedStart > 1 && SpeedEnd > 1) {
            strSpeed = str.substring(SpeedStart + 5, SpeedEnd);

        } else
            strSpeed = "0";
        float speedfloat = Float.parseFloat(strSpeed);
        return speedfloat;
    }

    private static void showWarningEvent(Bundle bundle) {
        Message msg = new Message();
        msg.setData(bundle);
        speedhandler.sendMessage(msg);
    }

    private static class SpeedHandler extends Handler {
        private WeakReference<RoomFragment> roomfragment;

        public SpeedHandler(RoomFragment fragment) {
            roomfragment = new WeakReference<RoomFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            if (bundle != null && roomfragment.get() != null) {
                if (bundle.getInt("status", 0) == 0) {
                    //roomfragment.get().toastShort("網路("+bundle.getString("cdn_ip")+")不穩 速度為:"+bundle.getString("cdn_speed"));

                    roomfragment.get().showSpeedEvent(false, bundle, roomfragment.get());
                } else {
                    roomfragment.get().showSpeedEvent(true, bundle, roomfragment.get());
                }

            }else{
                roomfragment.get().showSpeedEvent(false, bundle, roomfragment.get());
            }
        }
    }

    public String getSystemTime(){

        String currentDateTimeString = sdf.format(d);
        return currentDateTimeString;
    }

    private void showSpeedEvent(boolean status, final Bundle bundle, final RoomFragment fragment) {
        if (status) {


            final int SpeedStatus = bundle.getInt("status", 0);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (SpeedStatus == 1) {
                        iv_speedtest_event.setImageResource(R.drawable.tip_cdn_error);
                    }
                    if (SpeedStatus == 2) {
                        iv_speedtest_event.setImageResource(R.drawable.tip_server_error);
                    }
                    if (SpeedStatus == 3) {
                        iv_speedtest_event.setImageResource(R.drawable.tip_cdn_server_error);
                    }
                    iv_speedtest_event.setVisibility(View.VISIBLE);
                }
            });

            threadIp = new Thread(new Runnable() {
                //String currentDateTimeString = getSystemTime();
                @Override
                public void run() {
                    String ip = getPublicIP();
                    String sRemak = "";
                    //serverSpeed
                    //sRemak +="[Device Time] "+currentDateTimeString;
                    if (getRoomType() == RoomActivity.TYPE_PUBLISH_LIVE)
                        sRemak += "[Anchor]";
                    if (getRoomType() == RoomActivity.TYPE_VIEW_LIVE)
                        sRemak += "[Fans]";
                    if (bundle.getInt("status") >= 1)
                        sRemak += "[CDN_ResponseTime:" + bundle.getString("speed") + "/ms]" + "[SERVER_ResponseTime" + bundle.getString("serverspeed") + "/ms]" + "[PushPullState:" + RoomInfoTmp.RtmpEventLog + "]";

                    try {
                        String server_speed = bundle.getString("serverspeed");
                        String cdn_speed = bundle.getString("speed");
                        fragment.sendSpeedReport(mRoomUserUid, ip, bundle.getString("ip"), sRemak, cdn_speed, server_speed);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            });
            threadIp.start();
        } else {
            if(getActivity()!=null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv_speedtest_event.setVisibility(View.INVISIBLE);
                    }
                });
            }


        }
    }

    public static String getPublicIP() {
        String publicIp = "";
        try {
            Document doc = Jsoup.connect("http://www.checkip.org").get();
            publicIp = doc.getElementById("yourip").select("h1").first().select("span").text();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return publicIp;
    }

    private void sendSpeedReport(String uid, String uip, String rip, String remark, String cdn_speed, String server_speed) throws UnsupportedEncodingException {
        String remark_tmp = remark;
        if (Math.abs(publicCDNSpeedFloat - Float.parseFloat(cdn_speed)) >= MaxDiff) {

            roomFragmentPresenter.sendSpeedReport(uid, uip, rip, remark_tmp);
            publicCDNSpeedFloat = Float.parseFloat(cdn_speed);
        }
        if (Math.abs(publicServerSpeedFloat - Float.parseFloat(server_speed)) >= MaxDiff) {

            roomFragmentPresenter.sendSpeedReport(uid, uip, rip, remark_tmp);
            publicServerSpeedFloat = Float.parseFloat(server_speed);
        }
    }*/
   private void sendSpeedReport(String uid, String uip, String server_speed)  {
       roomFragmentPresenter.sendSpeedReport(uid, uip, "", "Ping time= "+server_speed +" ms");
   }
    private void startKsAnim(final String sName, final String GiftName) {

        final int ksAnimSize = 104;
        if (bigAnimStatus)
            return;
        if (ksAnimSize <= 0)
            return;
        bigAnimStatus = true;
        ksBigGift.setVisibility(View.VISIBLE);
        //big520_Test.setAlpha(0.8f);
        ksBigGift.setScaleType(ImageView.ScaleType.CENTER_CROP);
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(1, ksAnimSize);
        valueAnimator.setDuration(10000);
/*        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);*/
        if (Build.VERSION.SDK_INT < 19) {
            decorView.setSystemUiVisibility(View.GONE);
        } else {
            //for higher api versions.
            decorView.setSystemUiVisibility(HideUiOptions);
        }

        animListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (int) animation.getAnimatedValue();
                float alphaVal = getKsTextAlphaValue(val);
                ksBigGiftText.setAlpha(alphaVal);
                ksBigGiftText.setText(sName + " " + GiftName);
                ksBigGiftText.setAlpha(0.87f);
                ksBigGiftText.setVisibility(View.VISIBLE);
                if (ksAnimSize == 0)
                    ksBigGift.setImageDrawable(null);
                else
                    ksBigGift.setImageResource(getGifImageId(getContext(), val));
                //big520_Test.setImageResource(R.drawable.test1);
                if (val == ksAnimSize - 1) {
                    bigAnimStatus = false;
                    isgiftend = true;
                    ksBigGift.setVisibility(View.GONE);
                    ksBigGiftText.setVisibility(View.GONE);
                    valueAnimator.removeUpdateListener(this);
                    decorView.setSystemUiVisibility(ShowUiOption);
                    animend();
                }
            }
        };

        valueAnimator.addUpdateListener(animListener);
        valueAnimator.start();
    }

    private float getKsTextAlphaValue(int val) {
           /*     int startValue = 1 ;
                int endValue = KsBigGiftdrawables.size()-13;
                if(val<startValue || KsBigGiftdrawables.size()<startValue ||val > endValue)
                    return  0;
                else*/
            return 1;
               /* float RatioValue = 1/ ((float)(KsBigGiftdrawables.size())-20);
                Log.i("RayTest","anim Ratio:"+RatioValue+" val:"+val*RatioValue);
                return val*RatioValue;*/
    }

    public static int getGifImageId(Context context, int index) {

        return context.getResources().getIdentifier("sing_" + index, "drawable",
                context.getPackageName());
    }

    public static int getNewCarGifImageId(Context context, int index) {

        return context.getResources().getIdentifier("rain_car_" + index, "drawable",
                context.getPackageName());
    }

    private List<String> getImage(Context context) throws IOException {
        AssetManager assetManager = context.getAssets();
        String[] files = assetManager.list(BigGiftFolder);
        List<String> it = Arrays.asList(files);
        return it;
    }


    private void CheckScroll() {

        //NotifyScrollVertically();
        RecyclerView.LayoutManager LayoutManager = recyclerPublicChat.getLayoutManager();
        if (LayoutManager instanceof LinearLayoutManager) {

            int itheVisibleItemPos = ((LinearLayoutManager) LayoutManager).findLastVisibleItemPosition();
            int count = publicChatAdapter.getItemCount() - itheVisibleItemPos;
            if (count > 5) {
                noReadCount++;
                mMsgMore.setText(noReadCount + "條新訊息");
                mMsgMore.setVisibility(View.VISIBLE);
            } else {
                mMsgMore.setVisibility(View.GONE);
                recyclerPublicChat.scrollToPosition(publicChatAdapter.getItemCount() - 1);
                noReadCount = 0;
            }



                /*if(!isManualScrollMode)
                    recyclerPublicChat.scrollToPosition(publicChatAdapter.getItemCount() - 1);
                else {
                    int itheVisibleItemPos = ((LinearLayoutManager) LayoutManager).findLastVisibleItemPosition();
                    int count = publicChatAdapter.getItemCount()  - itheVisibleItemPos;
                    if(count>5){
                        noReadCount++;
                        mMsgMore.setText(noReadCount+"條新訊息");
                        mMsgMore.setVisibility(View.VISIBLE);
                    }else {
                        mMsgMore.setVisibility(View.GONE);
                        recyclerPublicChat.scrollToPosition(publicChatAdapter.getItemCount() - 1);
                    }

                }*/


        }


    }

    private void NotifyScrollVertically() {
        if (recyclerPublicChat.canScrollVertically(1)) {

            //mMsgMore.setVisibility(View.VISIBLE);
        } else {
            noReadCount = 0;
            mMsgMore.setVisibility(View.GONE);
        }
    }

    private void setRecyclerParams() {
        int height = getSurfaceViewHeight();


       /* ViewGroup.LayoutParams lp = recyclerPublicChat.getLayoutParams();
        lp.height = 500;
        recyclerPublicChat.setLayoutParams(lp);*/
    }

    private void getDebugData(RoomPublicMsg data) {
        if (data instanceof SystemWelcome) {
            SystemWelcome msg = (SystemWelcome) data;
            msg.setLevelid(10);
            msg.setType("login");
            msg.setClient_name("wel測試員");
            publicChatAdapter.appendData(msg);

        }
        if (data instanceof UserPublicMsg) {
            UserPublicMsg msg = (UserPublicMsg) data;
            msg.setType("SendPubMsg");
            msg.setFromClientId("7f00000108ff00000150");
            msg.setFromClientName("Public測試員");
            msg.setToClientId("all");
            msg.setContent("我是歌手");
            msg.setTime("21:15:16");
            msg.setLevel(20);
            msg.setVipLevel(20);
            publicChatAdapter.appendData(msg);
        }
        if (data instanceof LightHeartMsg) {
            LightHeartMsg msg = (LightHeartMsg) data;
            msg.setType("LightHeart");
            msg.setLevel(20);
            msg.setTime("23:19");
            //msg.setColor(Color.GRAY);
            msg.setFromClientId("8b8113be08ff00055f65");
            msg.setFromClientName("light測試員");
            msg.setFromUserId("8b8113be08ff00055f65");
            msg.setVip(0);
            publicChatAdapter.appendData(msg);
        }
        if (data instanceof SendGiftMsg) {
            SendGiftMsg msg = (SendGiftMsg) data;

        }
        if (data instanceof SystemMsg) {
            SystemMsg msg = (SystemMsg) data;
            msg.setContent("系統訊息系統訊息系統訊息系統訊息系統訊息系統訊息");
            publicChatAdapter.appendData(msg);

        }

    }

    //    "adminer_list" =     (
//            );
//    "client_id" = 0a0b20df08fc00000004;
//    "client_list" =     (
//            );
//    "client_name" = "\U6d4b\U8bd5";
//    levelid = 1;
//    time = "18:27";
//    type = login;
//    ucuid = 1190;
//    "user_id" = 1245;
//    vip = 0;

    public void openGiftWindows()
    {
/*        ViewGroup.LayoutParams lp = recyclerPublicChat.getLayoutParams();
        recyclerPublicChatHeight = lp.height;
        lp.height = 0 ;
        recyclerPublicChat.setLayoutParams(lp);*/
        recyclerPublicChat.setVisibility(View.GONE);
        mMsgMore.setVisibility(View.GONE);
    }

    public void closeGiftWindows()
    {
        recyclerPublicChat.setVisibility(View.VISIBLE);
        mMsgMore.setVisibility(View.VISIBLE);
        /*ViewGroup.LayoutParams lp = recyclerPublicChat.getLayoutParams();
        lp.height = recyclerPublicChatHeight ;
        recyclerPublicChat.setLayoutParams(lp);*/
    }

    protected void onRootClickAction() {
        Log.i("RayTeset","isgifend:"+isgiftend);
        if(!isgiftend)
            return;
        //giftView.playAllAnim();
       /* if(Const.TEST_ENVIROMENT_SW) {
            rundefaultEffect();
            rundefaultDanmu();
        }*/

       Log.i("RayTest","onRootClickAction");
        boolean shouldSend = shouldSendHeartRequest();
        if (shouldSend && wsService != null) {
            wsService.sendRequest(WsObjectPool.newLightHeartRequest(defaultColorIndex, LocalDataManager.getInstance().getLoginInfo().getApproveid()));
        }
        rlPrvChat.setVisibility(View.GONE);

        try {
            List<Conversation> mDatas = JMessageClient.getConversationList();
            initDatas(mDatas);

            for (int i = 0; i < mDatas.size(); i++) {
                Conversation conv = mDatas.get(i);
                if (conv.getUnReadMsgCnt() > 0) {
                    newMsg.setVisibility(View.VISIBLE);
                    break;
                }
                else
                    newMsg.setVisibility(View.GONE);
            }
        } catch (NullPointerException e) {
        }

        if(isAudienceOpen) {
            isAudienceOpen = false;
            showAnimRightOut(rlAudiencelistLayout);
            Log.i("RayTest","Close...");
        }

    }
    Runtime runtime;
    long usedMemInMB;
    long maxHeapSizeInMB;
    long availHeapSizeInMB;

    private void Cachecheck() {
        runtime = Runtime.getRuntime();
        usedMemInMB=(runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
        maxHeapSizeInMB=runtime.maxMemory() / 1048576L;
        availHeapSizeInMB = maxHeapSizeInMB - usedMemInMB;
        if(availHeapSizeInMB<50) {
            Log.i("RayTest","內存不足 開始清緩存 : " +availHeapSizeInMB);
            //toastShort("內存不足 開始清緩存!");
            System.gc();
        }
        Log.i("RayHeapSize","availHeapSizeInMB: "+availHeapSizeInMB);
    }

    @Override
    public void onDestroyView() {
        Log.i("RayTest",getClass().getSimpleName()+" onDestroyView");
        roomShow.setAdapter(null);

        //KsBigGiftdrawables.clear();
        //KsBigGiftdrawables = null;
        timingLogger.reset(TIMING_LOG_TAG, "RoomFragment#onDestroyView");
        //requestQueue.cancelAll(ROOM_FRAGMENT_REQ);
        super.onDestroyView();
        timingLogger.addSplit("super.onDestroyView");
        cancelWsService();
        //清除动画
        if (localGiftController != null) {
            localGiftController.removeAll();
        }

        //Release ws service binding

        timingLogger.addSplit("reset webSocket");
        timingLogger.dumpToLog();
        threadIp = null;
    }

    public void cancelWsService(){
        if (wsConnection != null) {
            getActivity().unbindService(wsConnection);
        }

        if (wsService == null) {
            L.e(LOG_TAG, "Ws service reference has been null, logout action cannot perform!");
            return;
        }
//        2016年8月2日17:47:54 鹏哥说的 发送两次logout不会出事
        if (!isKicked) {
            wsService.sendRequest(WsObjectPool.newLogoutRequest(mRoomId));
        }
        //autoRefreshService = null;
        wsService.removeAllListeners();
        RoomInfoTmp.webService=null;
    }

    //    初始化webSocket
    private void initWebSocket() {
        Log.i("RayTest","initWebSocket");
        getActivity().bindService(WebSocketService.createIntent(getActivity()),
                wsConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 因为WebSocket能连接的时间具有不确定性，所以必须在ServiceConnection里初始化。
     */
    protected void initWsListeners() {
       /* //wsService.startSpeedTest(getRoomType());*/
        WsListener<WsLoginMsg> loginListener = new WsListener<WsLoginMsg>() {

            @Override
            public void handleData(WsLoginMsg wsLoginMsg) {
                Log.i("RayTest","WsLoginMsg");
                isRoomAdmin = !TextUtils.isEmpty(wsLoginMsg.getRole());

                Log.i("RayTest","getHotpoint:"+wsLoginMsg.getHotpoint()+"");
            }
        };
        wsService.registerListener(SocketConstants.EVENT_LOGIN, loginListener);
//
        WsListener<WsLoginOutMsg> loginOutMsgWsListener = new WsListener<WsLoginOutMsg>() {
            @Override
            public void handleData(WsLoginOutMsg wsLoginOutMsg) {
                if (wsLoginOutMsg.getUser_id() != null && mAnchorId.equals(wsLoginOutMsg.getUser_id())) {
                    isloginout = true;
                    Log.i("RayTest","showRoomEndInfoDialog1");
                    ((RoomActivity) getActivity()).showRoomEndInfoDialog();
                }
            }
        };
        wsService.registerListener(SocketConstants.EVENT_LOGOUT, loginOutMsgWsListener);

        final WsListener<ErrorMsg> errorListener = new WsListener<ErrorMsg>() {
            private AlertDialog alertDialog;

            @Override
            public void handleData(ErrorMsg errorMsg) {
                if (SocketConstants.ERROR_KICKED.equalsIgnoreCase(errorMsg.getType())) {
                    if (alertDialog == null) {
                        final BaseActivity activity = (BaseActivity) getActivity();
                        String sysmsg = TextUtils.isEmpty(errorMsg.getContent())
                                ? getString(R.string.room_live_eliminate_user)
                                : errorMsg.getContent();
                        sysmsg = sysmsg + getString(R.string.room_live_eliminate_outtime);
                        new Thread(new ThreadShow(RoomFragment.this, getString(R.string.room_live_immediately_errorout), msghandler)).start();
                        alertDialog = new AlertDialog.Builder(activity)
                                .setCancelable(false)
                                .setMessage(sysmsg)
                                .setPositiveButton(getString(R.string.room_live_immediately_outroom), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//                                        是否被踢出 设为true
                                        isKicked = true;
                                        FINISH_ROOM = true;
                                        activity.startActivity(LoginSelectActivity.createIntent(activity));
                                        activity.sendFinishBroadcast(LoginSelectActivity.class.getSimpleName());
                                        //((RoomActivity) getActivity()).finishRoomActivity();
                                    }
                                })
                                .create();
                        alertDialog.show();
                    }
                } else {
                    String msg = TextUtils.isEmpty(errorMsg.getContent()) ? errorMsg.getType()
                            : errorMsg.getContent();
                    toastShort(msg);
                    L.e(LOG_TAG, "Unsupported error type:%s", errorMsg.getType());
                }
            }
        };
        wsService.registerListener(SocketConstants.EVENT_ERROR, errorListener);

//        接受用户发出的消息
        WsListener<UserPublicMsg> chatListener = new WsListener<UserPublicMsg>() {
            @Override
            public void handleData(final UserPublicMsg msg) {
//                Log.i("sendmsg","这个是handlerdata"+msg.getUserId()+"   "+userPublicMsg.getUserId());
//                addDanmaKuShowTextAndImage(false,msg.getContent());
//                Bitmap bitmap=Glide.with(RoomFragment.this).load("http://www.bilibili.com/favicon.ico").diskCacheStrategy(DiskCacheStrategy.ALL);

//                request = NoHttp.createImageRequest("http://www.bilibili.com/favicon.ico");
//                if (request != null)
//                    CallServer.getRequestInstance().add(getActivity(), 0, request, RoomFragment.this, false, true);
                if (msg.getFly() != null && msg.getFly().equals("FlyMsg")) {
                  final Danmu danmu = new Danmu(0, (int) (Math.random() * (3)), "Comment", msg.getContent(),msg);
                    //final Danmu danmu = new Danmu(0, (int) (Math.random() * (3)), "Comment", msg.getFromClientName() + ": " + msg.getContent());
                    Glide.with(RoomFragment.this).load(Const.MAIN_HOST_URL + msg.getAvatar()).asBitmap().into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            danmu.setAvatarUrl(resource);

                            mDanmuControl.addDanmu(danmu, 1);

                            Log.i("RayTest","addDanmu!!!! "+danmu.content);
                        }
                    });
               /*     DisplayDanmuAnim(msg);
                    switch (msg.getflyType()){
                        case 1:
                            DisplayDanmuAnim(msg);
                        case 2:
                            DisplayDanmuAnim(msg);
                        case 3:
                            DisplayDanmuAnim(msg);
                        default:
                            DisplayDanmuAnim(msg);
                    }*/
                }
//                如果不是用户本人的信息则添加到Recycleview
//                if (!msg.getUserId().equals(userPublicMsg.getUserId())) {
                publicChatAdapter.appendData(msg);
                String context = msg.getContent();
                //CheckScroll();
                if (context.indexOf("\uD83D\uDC4F") > -1) {
                    //有
                    //Log.i("RayTest", "msg: yes" + context);
                    //mSoundPool.play(mSoundId, 0.3f, 0.3f, 1, 0, 1);

                } else {
                    //Log.i("RayTest", "msg: 無" + context);
//無
                }
                //Auto scroll to last
                //recyclerPublicChat.scrollToPosition(publicChatAdapter.getItemCount() - 1);
//                }

            }
        };
        wsService.registerListener(SocketConstants.EVENT_PUB_MSG, chatListener);

//        接受系统发出的警告
        WsListener<SystemMsg> msgListenet = new WsListener<SystemMsg>() {
            @Override
            public void handleData(SystemMsg systemMsg) {
                publicChatAdapter.appendData(systemMsg);
                //Auto scroll to last
                //recyclerPublicChat.scrollToPosition(publicChatAdapter.getItemCount() - 1);
                //CheckScroll();
            }
        };
        wsService.registerListener(SocketConstants.STSTEM_MSG, msgListenet);

        //        接受系统发出的欢迎
        WsListener<SystemWelcome> welcomeListenet = new WsListener<SystemWelcome>() {
            @Override
            public void handleData(SystemWelcome systemMsg) {

                publicChatAdapter.appendData(systemMsg);
                setCharmCount(systemMsg.getHotpoint());
                if(systemMsg.getUser_id().equals(mAnchorId))
                    return;


                switch (systemMsg.getflyType()){
                    case 1:
                        runApproachEffects(1,systemMsg);
                        break;
                    case 2:
                        runApproachEffects(2,systemMsg);
                        break;
                    case 3:
                        runApproachEffects(3,systemMsg);
                        break;
                    default:
                        /*runApproachEffects(0,systemMsg);*/
                        break;
                }
                //Auto scroll to last
                //recyclerPublicChat.scrollToPosition(publicChatAdapter.getItemCount() - 1);
                //CheckScrollVertically();
                //CheckScroll();
            }
        };
        wsService.registerListener(SocketConstants.EVENT_LOGIN, welcomeListenet);


        WsListener<LightHeartMsg> heartListener = new WsListener<LightHeartMsg>() {
            @Override
            public void handleData(LightHeartMsg lightHeartMsg) {
                //要插入一条聊天数据
                publicChatAdapter.appendData(lightHeartMsg);
                //Auto scroll to last
                //recyclerPublicChat.scrollToPosition(publicChatAdapter.getItemCount() - 1);
                //CheckScrollVertically();
                //CheckScroll();
                //画心，如果不是来自自己的点亮
                String myUserId = LocalDataManager.getInstance().getLoginInfo().getUserId();
                if ((!TextUtils.isEmpty(myUserId)) && (!myUserId.equalsIgnoreCase(lightHeartMsg
                        .getFromUserId()))) {
                    int colorIndex = lightHeartMsg.getColor();
                    //设置安全的默认值
                    if (colorIndex < 0 || colorIndex >= heartColorArray.length) {
                        colorIndex = 0;
                    }
                    //mHeartAnim.addLove(heartColorArray[colorIndex]);
                }
            }
        };
        wsService.registerListener(SocketConstants.EVENT_LIGHT_HEART, heartListener);

        if (localGiftController != null) {
            WsListener<SendGiftMsg> giftAnimListener = new WsListener<SendGiftMsg>() {
                @Override
                public void handleData(SendGiftMsg sendGiftMsg) {

                    try {
                        int giftis = Integer.parseInt(sendGiftMsg.getIsred());
                        int giftCount = sendGiftMsg.getGiftCount();
                        String formName = sendGiftMsg.getFromUserName();
                        String giftName = sendGiftMsg.getGiftName();
                        //2是烟花表示烟花
                        if (giftis > 1) {
                            for (int i = 0; i < giftCount; i++) {
                                Log.i("RayTest","add gift "+giftis);
                                giftSpecials.add(giftis);
                                giftFromUserName.add(formName);
                                giftNameList.add(giftName);
                            }

                            if (isgiftend) {

                                showAnim(giftSpecials.get(0), giftFromUserName.get(0), giftNameList.get(0));
                            }
                        }
                    } catch (NumberFormatException e) {
                        toastShort(getString(R.string.paihpian_error));
                    }
//                    判断是否是红包
                    if (sendGiftMsg.getIsred().equals("1")) {
                        hongbaolist.add(sendGiftMsg);
                        if (mPopupWindow != null && !mPopupWindow.isShowing()) {
                            statusPopupWindow(hongbaolist.get(0).getRed_Id(), hongbaolist.get(0).getFromUserName(), hongbaolist.get(0).getFromUserAvatar());
                        } else if (mPopupWindow == null) {
                            statusPopupWindow(hongbaolist.get(0).getRed_Id(), hongbaolist.get(0).getFromUserName(), hongbaolist.get(0).getFromUserAvatar());
                        }
                    }
                    for (int i = 0; i < sendGiftMsg.getGiftCount(); i++) {
                        publicChatAdapter.appendData(sendGiftMsg);
                        //CheckScroll();
                    }
                    //Auto scroll to last
//                    if (!sendGiftMsg.getIsred().equals("1")) {
                    Log.i("RayTest","getAnchorBalance : "+sendGiftMsg.getAnchorBalance());
                    tvGold.setText(new DecimalFormat("#").format(Double.valueOf(sendGiftMsg.getAnchorBalance())));
//                    }

                    //getHotPoint(mRoomUserUid);
                    RoomInfoTmp.getAnchorBalanceValue = (int) ((sendGiftMsg.getAnchorBalance() - vFirstVBalance) * 3.14f);
                    //RoomInfoTmp.coinValue = (int) (sendGiftMsg.getAnchorBalance() - vFirstVBalance);
                    setCharmCount((long)sendGiftMsg.getHotpoint());
                    //recyclerPublicChat.scrollToPosition(publicChatAdapter.getItemCount() - 1);
                    //CheckScroll();
                    localGiftController.enqueue(adapter(sendGiftMsg));
                }


                private SendGiftAction adapter(SendGiftMsg msg) {
                    SendGiftAction action = new SendGiftAction();
                    action.setAvatar(msg.getFromUserAvatar());
                    action.setFromUid(msg.getFromUserId());
                    action.setCombo(msg.getGiftCount());
                    action.setNickname(msg.getFromUserName());
                    action.setGiftIcon(msg.getGiftIcon());
                    action.setGiftName(msg.getGiftName());
                    action.setIntcombe(msg.getComboHit());
                    return action;
                }
            };
            wsService.registerListener(SocketConstants.EVENT_SEND_GIFT, giftAnimListener);
        }

        if (recyclerAudienceList != null) {
            WsListener<LiveAudienceListMsg> liveListener = new WsListener<LiveAudienceListMsg>() {
                @Override
                public void handleData(LiveAudienceListMsg msg) {
                    if (recyclerAudienceList != null && audienceAdapter != null && audienceSidebarAdapter != null) {
                        List<AudienceInfo> total = new ArrayList<>();
                        List<AudienceInfo> clients = msg.getClientList();
                        List<AudienceInfo> admins = msg.getAdminList();
                        total.addAll(admins);
                        //Collections.copy cause lots of bugs.
                        total.addAll(clients);
                        Cachecheck();
                        L.v(false, LOG_TAG, "clients=%d, admin=%d, total=%d", clients.size(),
                                admins.size
                                        (), total.size());
//                        audienceAdapter.setDataList(total);
                        String liveStatus = msg.getLiveStatus();
                        String liveMsg = msg.getLiveMsg();

                        screenAudienList(audienceAdapter.getDataList(), total, audienceAdapter);
                        screenAudienList(audienceSidebarAdapter.getDataList(), total, audienceSidebarAdapter);
                        setupLiveContent(liveStatus,liveMsg);
                    }
                    //float getAnchorBalanceValue =  ((float)msg.getTotalCount())*3.14f;
                    //Log.i("RayTest","Total Count:"+msg.getTotalCount() );
                    //tvCharmCount.setText(""+getAnchorBalanceValue);

                }
            };
            wsService.registerListener(SocketConstants.EVENT_ONLINE_CLIENT, liveListener);
        }
//        聊天的msg的点击事件

        publicChatAdapter.setOnItemClickListener(new PublicChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, RoomPublicMsg data, PublicChatHolder holder) {
                //UserPublicMsg msg;
                Log.i("RayTest","on Item click msg type:"+holder.getMsgType()+" pos:"+position +"  "+ holder.getTextViewContent());
                if (holder == null || holder.getUserid() == null && UserInfoDialogShowing)
                    return;
                if (!isFastDoubleClick()) {
                    //写你相关操作即可
                    try {
                        if (!holder.getUserid().equals(userPublicMsg.getUserId()) && holder.getMsgType()==1) {
                            UserInfo mInfo = new UserInfo();
                            mInfo.setId(holder.getUserid());
                            mInfo.setAvatar("");
                            mInfo.setNickname(holder.getUserName());
                            mInfo.setVip(holder.getUserVip() + " ");
                            mInfo.setLevel(holder.getUserLevel() + "");
                            mInfo.setCurrentRoomNum("");
                            showUserInfoDialog(mInfo);
                        }
                    }catch (Exception e){

                    }

                }



               /* try {
                    msg = (UserPublicMsg) msgItem.getMsgData();
                } catch (Exception e) {
                    return;
                }


                if (msg.getUserId() != null && !msg.getUserId().equals(userPublicMsg.getUserId())) {
                    UserInfo mInfo = new UserInfo();
                    mInfo.setId(msg.getUserId());
                    mInfo.setAvatar("");
                    mInfo.setNickname(msg.getFromClientName());
                    mInfo.setVip(msg.getVipLevel() + " ");
                    mInfo.setLevel(msg.getLevel() + "");
                    mInfo.setCurrentRoomNum("");
                    showUserInfoDialog(mInfo);
                }*/

            }


        });
    }

    private boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;

        Log.i("RayTest","time: "+ time+ " lastclick time: "+lastClickTime + " timeD: "+timeD);
        if (lastClickTime > 0 && timeD < diff) {
            Log.v("RayTest", "短时间内按钮多次触发");
            return true;
        }
        lastClickTime = time;
        return false;
    }

    protected abstract void setupLiveContent(String liveStatus, String liveMsg);

    private void DisplayDanmuAnim(UserPublicMsg msg) {
      /*  Log.i("RayTest","跑種類 彈幕特效");
        MsgUtils utils = MsgUtils.getInstance();
        if(Const.TEST_ENVIROMENT_SW){
            msg.setLevel(61);
            mDanmuAnimView.RunDanmuAnim(msg,"跑種類 "+1+" 進場特效");
            *//*mDanmuAnimView.RunDanmuAnim(msg,"跑種類 "+2+" 進場特效");
            mDanmuAnimView.RunDanmuAnim(msg,"跑種類 "+3+" 進場特效");*//*
            this.testUserPublicMsg = msg;
            //mEnterView.RunEnterAnim(i,systemMsg,"跑種類 "+i+" 進場特效");
        }else{runApproachEffects
            mDanmuAnimView.RunDanmuAnim(msg,"跑種類 彈幕特效");

        }*/
    }

    private void runApproachEffects(int i, SystemWelcome systemMsg) {

        MsgUtils utils = MsgUtils.getInstance();
        String userinfoID = LocalDataManager.getInstance().getLoginInfo().getUserId();
        if(Const.TEST_ENVIROMENT_SW ){
  /*          mEnterView.RunEnterAnim(1,systemMsg,"RunEnterAnim "+1+" 進場特效");
            mEnterView.RunEnterAnim(2,systemMsg,"RunEnterAnim "+2+" 進場特效");
            mEnterView.RunEnterAnim(3,systemMsg,"RunEnterAnim "+3+" 進場特效");
            mEnterView.RunEnterAnim(4,systemMsg,"RunEnterAnim "+4+" 進場特效");
            this.testSystemWelcome = systemMsg;*/
            if(systemMsg.getFlyshow()>0){
                Log.i("RayTest","跑種類 "+i+" 進場特效");
                mEnterView.RunEnterAnim(i,systemMsg,"跑種類 "+i+" 進場特效");
            }
        }else{
            if(systemMsg.getFlyshow()>0){
                Log.i("RayTest","跑種類 "+i+" 進場特效");
                mEnterView.RunEnterAnim(i,systemMsg,"跑種類 "+i+" 進場特效");
            }
        }

    }

   /* private void rundefaultEffect(){
       if(testSystemWelcome==null)
           return;
        runApproachEffects(1,testSystemWelcome);
    }

    private void rundefaultDanmu(){
        if(testUserPublicMsg==null)
            return;
        DisplayDanmuAnim(testUserPublicMsg);
    }*/
    /*private static class sStringRequestListener implements com.android.volley.Response.Listener<String> {


        private final String mRoomUserUid;
        private final RoomFragment mfragment;

        public sStringRequestListener(RoomFragment fragment, String uid) {
            this.mfragment = fragment;
            this.mRoomUserUid = uid;
        }

        @Override
        public void onResponse(String sResponse) {
            try {
                JSONArray jsonArray = new JSONArray(sResponse);
                for(int index = 0 ;index<= jsonArray.length() ; index++){
                    String hotItem = jsonArray.getJSONObject(index).toString();
                    Log.i("RayTest","===============");
                    Log.i("RayTest","hotItem3 :"+hotItem);
                    HotPointInfo hotpointInfo = new Gson().fromJson(hotItem, HotPointInfo.class);
                    Log.i("RayTest","getUid :"+hotpointInfo.getUid()+" uid id"+mRoomUserUid);
                    if(Long.toString(hotpointInfo.getUid()).equals(mRoomUserUid) ){
                        Log.i("RayTest","HotVal:"+hotpointInfo.getHotpoint());
                        //tvCharmCount.setText("" + hotpointInfo.getHotpoint());

                        mfragment.startAnimNumber(hotpointInfo.getHotpoint());
                        mfragment.UpdateGifAnim(hotpointInfo.getHotpoint());

                    }
                    Log.i("RayTest","===============");
                }

            } catch (JSONException e) {


            }
        }


    }
*/


 /*   private static class sStringRequestErrorListener implements com.android.volley.Response.ErrorListener {


        @Override
        public void onErrorResponse(VolleyError volleyError) {

        }
    }*/
    //private static com.android.volley.Response.Listener sLinsten  = new sStringRequestListener();
    //private  static  com.android.volley.Response.ErrorListener sErrorLinsten  = new sStringRequestErrorListener();


   /* public void getHotPoint(String uid) {

        String RequestUrl = Const.HotPointAPI+"?uid="+uid;
        Log.i("RayTest","Request Url:"+RequestUrl);
        HotPointLinsten = new sStringRequestListener(this, mRoomUserUid);
        HotPointErrorLinsten  = new sStringRequestErrorListener();
        StringRequest stringRequest2 = new StringRequest(RequestUrl,HotPointLinsten,HotPointErrorLinsten);
        // time out 10 sec.
        stringRequest2.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest2.setTag(ROOM_FRAGMENT_REQ);
        requestQueue.add(stringRequest2);
    }*/

    /*private void UpdateGifAnim(long hotpoint) {
        //Level 1
        if (hotpoint < 25000) {
            if (durationValue != 1500) {
                durationValue = 1500;
                giftView.LoopAllAnim(1500);
            }
        }
        //Level 2
        if (hotpoint > 25000 && hotpoint < 75000) {
            if (durationValue != 1000) {
                durationValue = 1000;
                giftView.LoopAllAnim(1000);
            }
        }

        //Level 3
        if (hotpoint > 75001 && hotpoint < 250000) {
            if (durationValue != 800) {
                durationValue = 800;
                giftView.LoopAllAnim(800);
            }
        }

        //Level 4
        if (hotpoint > 250001 && hotpoint < 500000) {
            if (durationValue != 600) {
                durationValue = 600;
                giftView.LoopAllAnim(600);
            }
        }

        //Level 5
        if (hotpoint > 500001 && hotpoint < 1000000) {
            if (durationValue != 400) {
                durationValue = 400;
                giftView.LoopAllAnim(400);
            }
        }

        //Level 6
        if (hotpoint > 1000001) {
            if (durationValue != 200) {
                durationValue = 200;
                giftView.LoopAllAnim(200);
            }
        }

    }*/

    private void subscribeCloseBtn(View view) {
        RxView.clicks($(view, R.id.room_imgbtn_close))
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        //观众点击叉叉时不需要提示
                        ((RoomActivity) getActivity()).exitLiveRoom(getRoomType() != RoomActivity.TYPE_VIEW_LIVE);
                    }
                });
    }

    public abstract void finishRoom(int roomType);

    private void subscribeShowBtn(View view) {
        RxView.clicks($(view, R.id.room_imgbtn_talk))
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        llOperationBar.setVisibility(View.GONE);
                        recyclerPublicChat.setVisibility(View.GONE);
                        llSys.setVisibility(View.VISIBLE);
                        showAnimIn(llSys);
                    }
                });
    }

    /**
     * 靜音相關
     *
     * @param view
     */
    private void subscribeMuteBtn(View view) {
        RxView.clicks($(view, R.id.room_imgbtn_mute))
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        closeMute = !closeMute;
                        setMute(closeMute);
                    }
                });
    }


    private void initReceiver() {
        mReceiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mcontext.registerReceiver(mReceiver, filter);
    }

    //监听网络状态的广播
    private class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                ConnectivityManager manager = (ConnectivityManager) mcontext.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeInfo = manager.getActiveNetworkInfo();
                if (null == activeInfo) {
                }
            }
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

    }

    private void initAudienceBar(View view) {
        //tvOnlineCount = $(view, R.id.room_tv_live_user_count);
        tvCharmCount = $(view, R.id.room_tv_live_charm_count);
        tvUserCountType = $(view, R.id.room_tv_label_user_count);
        ivApproveId = $(view, R.id.iv_approveid_type);
        ivCrown = $(view,R.id.iv_crown);
        recyclerAudienceList = $(view, R.id.room_recycler_audience);
        recyclerAudienceList.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));
/*        recyclerAudienceList.addItemDecoration(ItemDecorations.horizontal(getActivity())
                .type(0, R.drawable.divider_decoration_transparent_w5)
                .create());*/

        audienceAdapter = new AudienceAdapter(this, new ArrayList<AudienceInfo>());
        recyclerAudienceList.setAdapter(audienceAdapter);
        if (getRoomType() == RoomActivity.TYPE_PUBLISH_LIVE) {
            tvUserCountType.setText(LocalDataManager.getInstance().getLoginInfo().getNickname());
        }

    }

    /**
     * 返回子类的房间类型，用于直接处理一些简单公用的属性。
     */
    @RoomActivity.RoomType
    protected abstract int getRoomType();

    /**
     * 解析参数。
     * 该方法会在initView之前调用。
     */
    protected abstract void parseArguments(Bundle bundle);

    /**
     * WebSocket聊天房间号。
     */
    protected abstract String getWsRoomId();

    protected abstract String getWsUserId();

    protected abstract boolean shouldSendHeartRequest();

    /**
     * 刷新抢到红包后的金额
     *
     * @param coinbalance
     */
    protected abstract void updateBalance(double coinbalance);

    /**
     * 靜音
     *
     * @param closeMute
     */
    protected abstract void setMute(boolean closeMute);

    /**
     * 發送彈幕
     *
     * @param roomid
     * @param content
     */
    protected abstract void sendDanmu(String roomid, String content);

    /**
     * 停止直播了
     */
    protected abstract void stopPublishLive();

    @Override
    public void showInputLayout(boolean show) {

        //showDanMU();
        Log.i("RayTest","danmu sw:"+ danmuMode+" "+ roomDanMu.getVisibility());
        Log.i("RayTest","showInputLayout "+show);
        if (show) {
            //llChatBar.setVisibility(View.VISIBLE);

            rlChatBar.setVisibility(View.VISIBLE);
//            llHeader.setVisibility(View.GONE);
//            new Handler().postDelayed(new Runnable(){
//
//                @Override
//                public void run() {

            hidellHeader();

//                }
//
//            }, 100);


            edtChatContent.requestFocus();
            imm.showSoftInput(edtChatContent, InputMethodManager.SHOW_IMPLICIT);

            llOperationBar.setVisibility(View.INVISIBLE);
        } else {

            llOperationBar.setVisibility(View.VISIBLE);
            /**
             * 使用handler来处理
             */
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    VisiblellHeader();
                }

            }, 50);

            rlChatBar.setVisibility(View.INVISIBLE);

            imm.hideSoftInputFromWindow(edtChatContent.getWindowToken(), InputMethodManager
                    .HIDE_NOT_ALWAYS);
        }
    }

    private void hidellHeader() {
        Animation headerAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.top_out);
        llHeader.startAnimation(headerAnim);
        llHeader.setVisibility(View.INVISIBLE);

    }


    private void VisiblellHeader() {
        if(isgiftend){
            llHeader.setVisibility(View.VISIBLE);
            Animation headerAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.top_in);
            llHeader.startAnimation(headerAnim);
        }
    }

    public void showPriInputLayout(boolean show) {
        L.v(false, LOG_TAG, "showInputLayout:%s", show);
        Log.i("RayTest","showPriInput "+show);
        if (show) {
            //llSys.setVisibility(View.VISIBLE);
//            llHeader.setVisibility(View.GONE);
//            new Handler().postDelayed(new Runnable(){
//
//                @Override
//                public void run() {
            hidellHeader();



//                }
//
//            }, 100);


            mChatInputEt.requestFocus();
            //imm.showSoftInput(mChatInputEt, InputMethodManager.SHOW_IMPLICIT);

            llOperationBar.setVisibility(View.INVISIBLE);
        } else {
            edtChatContent.requestFocus();
            llOperationBar.setVisibility(View.VISIBLE);
            /**
             * 使用handler来处理
             */
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    VisiblellHeader();
                }



            }, 50);

            //llSys.setVisibility(View.INVISIBLE);

            //imm.hideSoftInputFromWindow(edtChatContent.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        //showInputLayout(false);
    }

    protected void UpdateCoinBlance(String coinbalance){

    }

    @Override
    public ViewGroup getInputLayout() {
        return rlChatBar;
    }

    @Override
    public ViewGroup getPriInputLayout() {
        return rlPrvChat;
    }
    /**
     * 辅助方法， 用于将PopupWindow显示在图标上方。
     */
    protected final void showPopupWindowAboveButton(@NonNull PopupWindow window, @NonNull View
            anchor , View contentview) {
        View popupView = window.getContentView();
        //执行一次measure，避免第一次无法获取位置的问题。
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = popupView.getMeasuredWidth();
        int popupHeight = popupView.getMeasuredHeight();
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        //To fix the bug of inability for automatically dismiss pop window when touch outside
        window.setBackgroundDrawable(new BitmapDrawable());
        window.setOutsideTouchable(true);
        window.showAtLocation(anchor, Gravity.NO_GRAVITY, (location[0] + anchor.getWidth() / 2)
                        - popupWidth / 2,
                location[1] - popupHeight);
    }

    UserInfoDialog dialog;
    boolean m = false;

    /**
     * 展示用户信息的弹出框
     */
    protected void showUserInfoDialog(@NonNull final UserInfo info) {

        if (info == null) {
            toastShort(getString(R.string.userinfo_dialog_errorload));
            return;
        }
        if (TextUtils.isEmpty(info.getId())) {
            toastShort(getString(R.string.userinfo_dialog_errorid));
            return;
        }


//        dialog=new UserInfoDialog(this.getActivity(), wsService, mAnchorId);
        dialog = new UserInfoDialog(this.getActivity(), wsService, mAnchorId, mRoomId, new UserInfoDialog.ChatListener() {
            @Override
            public void prvChatListener() {
                mTargetId = info.getId();
                if (!info.getId().isEmpty()) {
                    String id = "user" + info.getId();
                    mTargetId = id;
                    for (int i = 0; i < 5; i++) {
                        JMessageClient.register(id, id, new BasicCallback() {
                            @Override
                            public void gotResult(int status, String desc) {
                                if (status == 0) {
                                    m = true;
                                } else {
                                    if (desc.equals("user exist")) {
                                        m = true;
                                    }
                                }
                            }
                        });
                        if (m = true) {
                            break;
                        }
                    }
                }

                showAnimIn(rlPrvChat);
                llOperationBar.setVisibility(View.GONE);
                recyclerPublicChat.setVisibility(View.GONE);
                Log.i("RayTest","setVisibility 5");
                llSys.setVisibility(View.GONE);
                rlPrvChat.setVisibility(View.VISIBLE);
                try {
                    mMyInfo = JMessageClient.getMyInfo();
                    if (!TextUtils.isEmpty(mTargetId)) {
                        mIsSingle = true;
                        mConv = JMessageClient.getSingleConversation(mTargetId);
                        if (mConv != null) {
                            cn.jpush.im.android.api.model.UserInfo userInfo = (cn.jpush.im.android.api.model.UserInfo) mConv.getTargetInfo();
                            if (!TextUtils.isEmpty(info.getNickname())) {
                                mChatTitle.setText(info.getNickname());
                            } else {
                                if (TextUtils.isEmpty(userInfo.getNickname())) {
                                    mChatTitle.setText(userInfo.getUserName());
                                } else {
                                    mChatTitle.setText(userInfo.getNickname());
                                }
                            }
                        } else {
                            mConv = Conversation.createSingleConversation(mTargetId);
                            final cn.jpush.im.android.api.model.UserInfo userInfo = (cn.jpush.im.android.api.model.UserInfo) mConv.getTargetInfo();
                            if (!TextUtils.isEmpty(info.getNickname())) {
                                mChatTitle.setText(info.getNickname());
                            } else {
                                JMessageClient.getUserInfo(mTargetId, new GetUserInfoCallback() {
                                    @Override
                                    public void gotResult(int i, String s, cn.jpush.im.android.api.model.UserInfo userInfo) {
                                        cn.jpush.im.android.api.model.UserInfo serInfo = userInfo;
                                        mChatTitle.setText(serInfo.getNickname());
                                    }
                                });
//                            if (TextUtils.isEmpty(userInfo.getNickname())) {
//                                mChatTitle.setText(userInfo.getUserName());
//                            }else {
//                                mChatTitle.setText(userInfo.getNickname());
//                            }
                            }
                            getConversationInfo(mConv, new RoomFragmentPresenter.RoomCallback() {
                                @Override
                                public void onSuccessInfo(UserInfo data) {
                                    PriConversation conversation = CreatePriConversation(data,mConv);
                                    mPriListAdapter.addPriConversation(conversation);
                                }
                            });
                        }
                        mChatAdapter = new MsgListRoomAdapter(mcontext, mTargetId, mTargetAppKey, longClickListener);
                    }


                    mChatListView.setAdapter(mChatAdapter);
                    // 滑动到底部
                    mChatListView.clearFocus();
                    mChatListView.post(new Runnable() {
                        @Override
                        public void run() {
                            mChatListView.setSelection(mChatListView.getAdapter().getCount() - 1);
                            mChatListView.smoothScrollToPosition(mChatListView.getAdapter().getCount() - 1);
                        }
                    });
                    mChatListView.setSelection(mChatListView.getAdapter().getCount() - 1);
                } catch (NullPointerException e) {
                }
                mSendMsgBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ibBack.setFocusable(true);
                        ibBack.requestFocus();
                        String msgContent = mChatInputEt.getText().toString();
                        mChatInputEt.setText("");

                        mChatListView.clearFocus();
                        mChatListView.post(new Runnable() {
                            @Override
                            public void run() {
                                mChatListView.setSelection(mChatListView.getAdapter().getCount() - 1);
                            }
                        });

                        if (msgContent.equals("")) {
                            return;
                        }
                        TextContent content = new TextContent(msgContent);

                        cn.jpush.im.android.api.model.Message msg = mConv.createSendMessage(content);
                        Log.i("RayTest","createSendMessage1:"+msg.getFromUser().getAvatar());
                        mChatAdapter.addMsgToList(msg);
                        JMessageClient.sendMessage(msg);

                        mChatAdapter.initMediaPlayer();
                        // 监听下拉刷新
                        mChatListView.setOnDropDownListener(new DropDownListView.OnDropDownListener() {
                            @Override
                            public void onDropDown() {
                                mUIHandler.sendEmptyMessageDelayed(REFRESH_LAST_PAGE, 1000);
                            }
                        });
                        // 滑动到底部
                        mChatListView.clearFocus();
                        mChatListView.post(new Runnable() {
                            @Override
                            public void run() {
                                mChatListView.setSelection(mChatListView.getAdapter().getCount() - 1);
                            }
                        });
                    }
                });
            }

            @Override
            public void dialogConference(UserInfo minfo) {

            }

            @Override
            public void sendReplyAt(String nickName) {
//                edtChatContent.setText("@"+nickName+" ");
//                edtChatContent.setSelection(edtChatContent.getText().toString().length());
//                showInputLayout(true);
                edtChatContent.setText("@" + nickName + " ");
                edtChatContent.setSelection(edtChatContent.getText().length());
                edtChatContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showInputLayout(true);
                    }
                }, 200);
            }

            @Override
            public void updateBlackList(String uid) {
                Log.i("RayTest","uid:"+LocalDataManager.getInstance().getLoginInfo().getUserId()+" blackid: "+uid);
                if(LocalDataManager.getInstance().getLoginInfo().getUserId().equals(uid)) {
                    toastShort("不可以加入自己黑名單");
                }else{
                    if (!LocalDataManager.getInstance().getIsHit(uid)) {
                        Log.i("RayTest"," 没被拉黑");
                        Activity ac = (Activity) getContext();
                        isRunningUpdateBlackList = true;
                        dialogFragment.showCheckDelDialog(getActivity().getSupportFragmentManager(),uid, CreateViewDialogFragment.TYPE_ADD_BLACKLIST);
                        //mPresenter.addBlackList(mUserInfo.getId());
                        //mPresenter.setHit(LocalDataManager.getInstance().getLoginInfo().getToken(), mUserInfo.getId());
                    } else {
                        Log.i("RayTest"," 已被拉黑");
                        isRunningUpdateBlackList = true;
                        dialogFragment.showCheckDelDialog(getActivity().getSupportFragmentManager(),uid,CreateViewDialogFragment.TYPE_CANCEL_BLACKLIST);
                        //mPresenter.delBlackList(mUserInfo.getId(),0);
                        //mPresenter.removeHit(LocalDataManager.getInstance().getLoginInfo().getToken(), mUserInfo.getId());

                    }
                }

            }

            @Override
            public void showDialogFragment(String id, int itype) {
                switch (itype){
                    case CreateViewDialogFragment.TYPE_ADD_FAVORITE_ALREADY_BLACK:
                        dialogFragment.showCheckDelDialog(getActivity().getSupportFragmentManager(),id,CreateViewDialogFragment.TYPE_ADD_FAVORITE_ALREADY_BLACK);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void updateFollowStatus(boolean b, String uid) {
                Log.i("RayTest","關注更新："+b+ " uid :"+uid+ "  主播: "+mAnchorId);
                if(uid.equals(mAnchorId)){
                   if(b)
                       toptabstart.setVisibility(View.INVISIBLE);
                    else
                       toptabstart.setVisibility(View.VISIBLE);
                }
            }
        });
        //dialog.setSupportFragmentManager(getActivity().getSupportFragmentManager());
        dialog.setUserInofo(info);
        dialog.setUserId(info.getId());
        if ((getRoomType() == RoomActivity.TYPE_PUBLISH_LIVE)) {
            dialog.setKickListener(new UserInfoDialog.UserClickKickListener() {
                @Override
                public void clickKick(String userId, String username) {
                    wsService.sendRequest(WsObjectPool.newRoomManageRequest(WsRoomManageRequest.TYPE_KICK,
                            userId,
                            username));
                }
            });
            dialog.showKick(true);
        }
        Window win = dialog.getWindow();
        int margin = (int) this.getContext().getResources().getDimension(R.dimen.dialog_margin);
        win.getDecorView().setPadding(margin, margin, margin, margin);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);
        dialog.show();
    }

    private PriConversation CreatePriConversation(UserInfo info,Conversation con) {
        PriConversation Conversation = new PriConversation();
        Conversation.setUserID(info.getId());

        Conversation.setAvt(info.getAvatar());
        Conversation.setApproveid(info.getApproveid());
        Conversation.setNickName(info.getNickname());
        if(con!=null){
            Conversation.setLastMsg(con.getLatestMessage());
            Conversation.setConversationID(con.getId());
            Conversation.setTime(con.getLastMsgDate());
            Conversation.setToken(con.getTargetAppKey());
        }else{
            Conversation.setLastMsg(null);
            Conversation.setConversationID("");
            Conversation.setTime(0);
            Conversation.setToken("");
        }
        return Conversation;
    }

    private void getConversationInfo(Conversation mConv,RoomFragmentPresenter.RoomCallback roomCallback) {
        roomFragmentPresenter.getUserInfo(mConv,roomCallback);
    }
    public void setmAnchorId(String mAnchorId) {
        this.mAnchorId = mAnchorId;
    }

    public String getmAnchorId() {
        return this.mAnchorId;
    }

    private static class AudienceAdapter extends SimpleRecyclerAdapter<AudienceInfo, AudienceHolder> {

        private final RoomFragment mFragment;

        public AudienceAdapter(RoomFragment fragment, List<AudienceInfo> audienceInfoList) {
            super(audienceInfoList);
            Log.i("RayTest","AudienceAdapter size:"+audienceInfoList.size());
            this.mFragment = fragment;
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_online_audience;
        }

        @NonNull
        @Override
        protected AudienceHolder createHolder(View view) {
            return new AudienceHolder(mFragment, view);
        }
    }

    private static class AudienceSiderAdapter extends SimpleRecyclerAdapter<AudienceInfo, AudienceSideBarHolder> {

        private final RoomFragment mFragment;

        public AudienceSiderAdapter(RoomFragment fragment, List<AudienceInfo> audienceInfoList) {
            super(audienceInfoList);
            this.mFragment = fragment;
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_online_audience_sidebar;
        }

        @NonNull
        @Override
        protected AudienceSideBarHolder createHolder(View view) {
            return new AudienceSideBarHolder(mFragment, view);
        }
    }

    private static class AudienceHolder extends SimpleRecyclerHolder<AudienceInfo> {

        private final View itemview;
        private final RoomFragment mfragment;
        private final ImageView crown;
        private SimpleDraweeView draweeAvatar;
        private ImageView icon;
        private UserInfo mInfo;

        public AudienceHolder(RoomFragment fragment, View itemView) {

            super(itemView);
            this.mfragment = fragment;
            this.itemview = itemView;
            draweeAvatar = (SimpleDraweeView) itemview.findViewById(R.id.img_user_avatar);
            icon = (ImageView) itemview.findViewById(R.id.img_user_star_type);
            crown = (ImageView)itemview.findViewById(R.id.user_crown);

        }

        @Override
        public void displayData(final AudienceInfo data) {
//            判断当前用户是哪个观众，然后设置一下userPublicMsg的信息
            if(LocalDataManager.getInstance().getLoginInfo()==null)
                return;
            if (LocalDataManager.getInstance().getLoginInfo().getUserId().equals(data.getUserId()) && !mfragment.initUserPublicMsg) {
                mfragment.publicMsgLevel = Integer.parseInt(data.getLevelId());
                mfragment.publicMsgId = data.getUserId();
                mfragment.publicMsgName = data.getClientName();
                mfragment.initUserPublicMsg = true;
            }
            if(data.getApproveid().contains("貴賓"))
                crown.setVisibility(View.VISIBLE);
            else
                crown.setVisibility(View.GONE);
            String avatar = data.getAvatar();
            mInfo = new UserInfo();
            mInfo.setId(data.getUserId());
            mInfo.setAvatar(data.getAvatar());
            mInfo.setNickname(data.getClientName());
            mInfo.setVip(data.getVip() + "");
            mInfo.setLevel(data.getLevelId());
            mInfo.setCurrentRoomNum(data.getRoomId());
            mInfo.setApproveid(data.getApproveid());
            if(draweeAvatar==null){

            }
            if (!TextUtils.isEmpty(avatar)) {
                draweeAvatar.setImageURI(SourceFactory.wrapPathToUri(avatar));
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if(!mfragment.isFastDoubleClick()){
                    if (mfragment.dialog == null) {
                        mfragment.showUserInfoDialog(mInfo);
                    } else if (!mfragment.dialog.isShowing()) {
                        mfragment.showUserInfoDialog(mInfo);
                    }
                }

                }
            });

            String role = data.getRole();
            if(role!=null && role.equals("adminer")){
                RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
                roundingParams.setBorder(Color.parseColor("#B94FFF"), 5.5f);
                roundingParams.setRoundAsCircle(true);
                draweeAvatar.getHierarchy().setRoundingParams(roundingParams);
            }else{
                RoundingParams roundingParams = RoundingParams.fromCornersRadius(0);
                roundingParams.setBorder(Color.parseColor("#FFFFFF"), 0f);
                roundingParams.setRoundAsCircle(true);
                draweeAvatar.getHierarchy().setRoundingParams(roundingParams);
            }
            String approveid = mInfo.getApproveid();
            mfragment.setApproveidIcon(icon, approveid);


        }
    }
    private static class AudienceSideBarHolder extends SimpleRecyclerHolder<AudienceInfo> {

        private final View itemview;
        private final RoomFragment mfragment;
        private final ImageView crown;
        private SimpleDraweeView draweeAvatar;
        private ImageView icon;
        private AdaptiveTextView tv_name;
        private ImageView iv_level;
        private UserInfo mInfo;

        public AudienceSideBarHolder(RoomFragment fragment, View itemView) {

            super(itemView);
            this.mfragment = fragment;
            this.itemview = itemView;
            draweeAvatar = (SimpleDraweeView) itemview.findViewById(R.id.sd_sidebar_head_icon);
            icon = (ImageView) itemview.findViewById(R.id.iv_sidebar_head_approveid);
            tv_name = (AdaptiveTextView) itemView.findViewById(R.id.tv_audience_side_name);
            iv_level = (ImageView) itemview.findViewById(R.id.iv_audience_side_level);
            crown = (ImageView)itemview.findViewById(R.id.iv_sidebar_user_crown);
        }

        @Override
        public void displayData(final AudienceInfo data) {
//            判断当前用户是哪个观众，然后设置一下userPublicMsg的信息
            if (LocalDataManager.getInstance().getLoginInfo().getUserId().equals(data.getUserId()) && !mfragment.initUserPublicMsg) {
                mfragment.publicMsgLevel = Integer.parseInt(data.getLevelId());
                mfragment.publicMsgId = data.getUserId();
                mfragment.publicMsgName = data.getClientName();
                mfragment.initUserPublicMsg = true;
            }
            Log.i("RayTest", "=================="+data.getClientName());
            String avatar = data.getAvatar();
            mInfo = new UserInfo();
            mInfo.setId(data.getUserId());
            mInfo.setAvatar(data.getAvatar());
            mInfo.setNickname(data.getClientName());
            mInfo.setVip(data.getVip() + "");
            mInfo.setLevel(data.getLevelId());
            mInfo.setCurrentRoomNum(data.getRoomId());
            mInfo.setApproveid(data.getApproveid());
            if (!TextUtils.isEmpty(avatar)) {
                draweeAvatar.setImageURI(SourceFactory.wrapPathToUri(avatar));
            }
            if(data.getApproveid().contains("貴賓"))
                crown.setVisibility(View.VISIBLE);
            else
                crown.setVisibility(View.GONE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mfragment.dialog == null) {
                        mfragment.showUserInfoDialog(mInfo);
                    } else if (!mfragment.dialog.isShowing()) {
                        mfragment.showUserInfoDialog(mInfo);
                    }
                }
            });

            String role = data.getRole();
            if(role!=null && role.equals("adminer")){
                RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
                roundingParams.setBorder(Color.parseColor("#B94FFF"), 5.5f);
                roundingParams.setRoundAsCircle(true);
                draweeAvatar.getHierarchy().setRoundingParams(roundingParams);
            }else{
                RoundingParams roundingParams = RoundingParams.fromCornersRadius(0);
                roundingParams.setBorder(Color.parseColor("#FFFFFF"), 0f);
                roundingParams.setRoundAsCircle(true);
                draweeAvatar.getHierarchy().setRoundingParams(roundingParams);
            }
            String approveid = mInfo.getApproveid();
            mfragment.setApproveidIcon(icon, approveid);
            tv_name.setText(data.getClientName());
            iv_level.setImageResource(PicUtil.getLevelImageId(itemview.getContext(),Integer.parseInt(data.getLevelId())));
        }
    }

    public void setCrownIcon(String approveid){
        if(approveid.contains("貴賓")){
            ivCrown.setVisibility(View.VISIBLE);
        }else{
            ivCrown.setVisibility(View.INVISIBLE);
        }
    }


    public void setApproveidIcon(ImageView icon, String approveid) {
        int approveidflag = -1;
        if (approveid != null) {
            if (approveid.equals(sArtistType_cn[0]) || approveid.equals(sArtistType_tw[0]))
                approveidflag = 0;
            if (approveid.equals(sArtistType_cn[1]) || approveid.equals(sArtistType_tw[1]))
                approveidflag = 1;
            if (approveid.equals(sArtistType_cn[2]) || approveid.equals(sArtistType_tw[2]))
                approveidflag = 2;
            if (approveid.equals(sArtistType_cn[3]) || approveid.equals(sArtistType_tw[3]))
                approveidflag = 3;
            if (approveid.contains(sArtistType_cn[4]) || approveid.contains(sArtistType_tw[4]))
                approveidflag = 4;
            if (approveid.contains(sArtistType_cn[5]) || approveid.contains(sArtistType_tw[5]))
                approveidflag = 5;
        }

        switch (approveidflag) {
            case 0:
                icon.setVisibility(View.GONE);
                break;
            case 1:
                icon.setVisibility(View.VISIBLE);
                icon.setImageResource(starticon[1]);
                break;
            case 2:
                icon.setVisibility(View.VISIBLE);
                icon.setImageResource(starticon[2]);
                break;
            case 3:
                icon.setVisibility(View.VISIBLE);
                icon.setImageResource(starticon[3]);
                break;
            case 4:
                icon.setVisibility(View.VISIBLE);
                icon.setImageResource(starticon[4]);
                break;
            case 5:
                icon.setVisibility(View.VISIBLE);
                icon.setImageResource(starticon[5]);
                break;
            default:
                icon.setVisibility(View.INVISIBLE);
                break;

        }
    }

    protected void showAnimIn(View v) {
        if(v.getId()==R.id.layout_prv_chat)
            isPriMode = true;
        Animation headerAnim  = AnimationUtils.loadAnimation(getActivity(), R.anim.room_buttom_in);
        v.startAnimation(headerAnim);
    }

    protected void showAnimOut(View v) {
        Animation headerAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.room_buttom_out);
        v.startAnimation(headerAnim);
    }

    protected Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    protected int getSurfaceViewHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        //因为部分（乐视）手机的系统栏高度不是标准的25/24dp，所以首选获得系统内置的高度，出现异常时则用预定义的高度。
        int statusBarHeight;
        try {
            //取得窗口属性
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            statusBarHeight = getResources().getDimensionPixelSize(getResources().getIdentifier
                    ("status_bar_height", "dimen", "android"));
            L.i(LOG_TAG, "System status bar height:%s", statusBarHeight);
        } catch (Exception e) {
            L.w(LOG_TAG, "Cannot get system status bar height, using default!");
            statusBarHeight = (int) getResources().getDimension(R.dimen.status_bar_height);
        }
        //窗口的宽度
/*        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        int  res = 0;
        if (resourceId > 0) {
            res = getResources().getDimensionPixelSize(resourceId);
        }*/

        return dm.heightPixels - statusBarHeight;
    }

    protected int getSurfaceTotalHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        //因为部分（乐视）手机的系统栏高度不是标准的25/24dp，所以首选获得系统内置的高度，出现异常时则用预定义的高度。
        int statusBarHeight;
        try {
            //取得窗口属性
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            statusBarHeight = getResources().getDimensionPixelSize(getResources().getIdentifier
                    ("status_bar_height", "dimen", "android"));
            L.i(LOG_TAG, "System status bar height:%s", statusBarHeight);
        } catch (Exception e) {
            L.w(LOG_TAG, "Cannot get system status bar height, using default!");
            statusBarHeight = (int) getResources().getDimension(R.dimen.status_bar_height);
        }
        //窗口的宽度
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        int res = 0;
        if (resourceId > 0) {
            res = getResources().getDimensionPixelSize(resourceId);
        }

        return dm.heightPixels + statusBarHeight + res;
    }

    protected DisplayMetrics getScreenMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    protected int getSurfaceViewwidth() {
        DisplayMetrics dm = new DisplayMetrics();
        //因为部分（乐视）手机的系统栏高度不是标准的25/24dp，所以首选获得系统内置的高度，出现异常时则用预定义的高度。
        int statusBarWidth;
        try {
            //取得窗口属性
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            statusBarWidth = getResources().getDimensionPixelSize(getResources().getIdentifier
                    ("status_bar_height", "dimen", "android"));
            L.i(LOG_TAG, "System status bar height:%s", statusBarWidth);
        } catch (Exception e) {
            L.w(LOG_TAG, "Cannot get system status bar height, using default!");
            statusBarWidth = (int) getResources().getDimension(R.dimen.status_bar_height);
        }
        //窗口的宽度
        return dm.heightPixels - statusBarWidth;
    }

    //    是否执行
    private boolean FINISH_ROOM = false;
   /* Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                ((RoomActivity) getActivity()).finishRoomActivity();
            } else if (msg.what == 21) {
                for (String value : mFuImgUrl.values()) {
                }
                mListAdapter = new PrvListAdapter(mcontext, mDatas, mFuImgUrl);
                roomShow.setAdapter(mListAdapter);
            }
            super.handleMessage(msg);
        }

    };*/



    private static class MsgHandler extends Handler {


        private final RoomFragment mFragment;
        private final Context mcontext;
        //private final List<Conversation> mDatas;
        private final HashMap<String, String> mFuImgUrl;
        //private final WeakReference<RoomFragment> mFragment;

        public MsgHandler(RoomFragment fragment, Context context) {
            WeakReference<RoomFragment> WeakReferenceFragment = new WeakReference<RoomFragment>(fragment);
            this.mFragment = WeakReferenceFragment.get();
            this.mcontext = context;
            //this.mDatas = fragment.mDatas;
            this.mFuImgUrl = fragment.mFuImgUrl;
        }

        @Override
        public void handleMessage(Message msg) {
            Log.i("RayTest","handleMessage"+msg.what);
            if (msg.what == 139 ) { //原本1
                ((RoomActivity) mFragment.getActivity()).finishRoomActivity();
                Log.i("RayTest","收到關播Msg");
            } else if (msg.what == 21) {
                for (String value : mFragment.mFuImgUrl.values()) {
                }
                //mFragment.roomFragmentPresenter.getUserInoList(mDatas);
                /*mFragment.mListAdapter = new PrvListAdapter(mcontext, mDatas, mFuImgUrl,mFragment);
                mFragment.roomShow.setAdapter(mFragment.mListAdapter);*/
            }
            super.handleMessage(msg);
        }
    }

    // d计时器
    static class ThreadShow implements Runnable {

        private final RoomFragment mfragment;
        private final MsgHandler mhandler;
        private final String sText;

        public ThreadShow(RoomFragment fragment, String str, MsgHandler handler) {
            this.mfragment = fragment;
            this.mhandler = handler;
            this.sText = str;
        }

        @Override
        public void run() {
            int i = 0;
            Log.i("RayTest","FINISH_ROOM:"+mfragment.FINISH_ROOM);
            while (i < 5 && !mfragment.FINISH_ROOM) {
                try {
                    i++;
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    //mfragment.toastShort(getString(R.string.room_live_immediately_errorout));
                    mfragment.toastShort(sText);
                }
            }
            if (!mfragment.FINISH_ROOM) {
                Message msg = new Message();
                msg.what = 139; //原本1
                mhandler.sendMessage(msg);
            }


        }
    }

    private DanmuControl mDanmuControl;

    private void initDanmuku(View view) {
        mDanmuControl = new DanmuControl(getActivity().getApplicationContext());
        //弹幕实力话
        mDanmakuView = (DanmakuView) view.findViewById(R.id.mrl_danmaku2);
        mEnterView = (EnterView) view.findViewById(R.id.enter_main_layout);
        Log.i("RayTest","get danmuSW2: "+ Const.dammuSW);
        if(danmuMode) {
            mDanmakuView.setVisibility(View.VISIBLE);
            mEnterView.setVisibility(View.VISIBLE);
            mDanmuControl.setDanmakuView(mDanmakuView);

            mDanmuAnimTmpView = (DanmuAnimView) view.findViewById(R.id.danmu_main_layout);
            mDanmuAnimView = (DanmuAnimView) view.findViewById(R.id.danmu_main_layout2);
            final LinearLayout ll_anim_layout = (LinearLayout) view.findViewById(R.id.anim_main_layout);
            mDanmuAnimTmpView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mDanmuAnimTmpView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mDanmuAnimViewWidth = mDanmuAnimTmpView.getWidth();
                    mDanmuAnimViewHeight = mDanmuAnimTmpView.getHeight();
                    mEnterView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            mEnterView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            mDanmuAnimViewHeight = mDanmuAnimViewHeight - mEnterView.getHeight();
                            Log.i("RayTest","mDanmuAnimViewWidth:"+mDanmuAnimViewWidth);
                            Log.i("RayTest","mDanmuAnimViewHeight:"+mDanmuAnimViewHeight);
                            RelativeLayout.LayoutParams animLP = new RelativeLayout.LayoutParams(mDanmuAnimViewWidth,mDanmuAnimViewHeight);
                            mDanmakuView.setLayoutParams(animLP);
                            mDanmuControl.setDanmuViewHeigh(mDanmuAnimViewHeight);
                        }
                    });


                    //ll_anim_layout.setLayoutParams(animLP);
                    mEnterView.setViewParams(mDanmuAnimViewWidth,mDanmuAnimViewHeight);
                    //mDanmakuView.setViewParams(mDanmuAnimViewWidth,mDanmuAnimViewHeight);

                }
            });

        }else{
            mDanmakuView.setVisibility(View.GONE);
        }
        if(Const.LevelEnterSW>=1||Const.VipEnterSW>=1)
            mEnterView.setVisibility(View.VISIBLE);
        else
            mEnterView.setVisibility(View.INVISIBLE);
    }

    //    成功的请求
    @Override
    public void onSucceed(int what, Response<Bitmap> response) {
        /*danmu.setAvatarUrl(response.get());
        mDanmuControl.addDanmu(danmu, 1);*/
        Log.i("RayTest",getClass().getSimpleName()+":onSucceed");
    }

    //  失败
    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

    }

    View view;
    PopupWindow mPopupWindow;
    ImageView get;
    ImageView close;
    String giftid;
    SimpleDraweeView hbfromicon;
    TextView hbfromnikname, hongbaoamount;
    RelativeLayout hongbao;
    int size;

    //    红包弹窗                      红包id        发送的人名字  发送人的头像
    private void statusPopupWindow(String gifid, String nikname, String icon) {
        giftid = gifid;
        if (mPopupWindow == null) {
            LayoutInflater relativeLayout = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = relativeLayout.inflate(R.layout.layout_hongbao_popu, null);
            hongbao = (RelativeLayout) view.findViewById(R.id.hongbao);
            get = (ImageView) view.findViewById(R.id.get);
            close = (ImageView) view.findViewById(R.id.close);
            hbfromnikname = (TextView) view.findViewById(R.id.hongbao_name);
            hongbaoamount = (TextView) view.findViewById(R.id.hongbao_amount);
            hbfromicon = (SimpleDraweeView) view.findViewById(R.id.hongbao_head);

            hbfromnikname.setText(nikname);
            hbfromicon.setImageURI(Uri.parse(Const.MAIN_HOST_URL + icon));
            get.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRoomId != null) {
                        getViewPagerJson(LocalDataManager.getInstance().getLoginInfo().getToken(), mRoomId, giftid);
                    }
                }
            });
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hongbaolist.size() > 1) {
                        mPopupWindow.dismiss();
                        hongbaolist.remove(0);
                        statusPopupWindow(hongbaolist.get(0).getRed_Id(), hongbaolist.get(0).getFromUserName(), hongbaolist.get(0).getFromUserAvatar());
                    } else {
                        hongbaolist.remove(0);
                        mPopupWindow.dismiss();
                    }
                }
            });
            mPopupWindow = new PopupWindow(view,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            // 使其聚集
            mPopupWindow.setFocusable(true);
            // 设置允许在外点击消失
            mPopupWindow.setOutsideTouchable(true);
            // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.showAtLocation(tvGold, Gravity.BOTTOM, 0, 0);
            aniPopupWindowValuesHolder(hongbao);
            mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                }
            });
        } else {
            hbfromnikname.setText(nikname);
            get.setVisibility(View.VISIBLE);
            hongbaoamount.setText(getString(R.string.room_live_redpacket_title));
            hbfromicon.setImageURI(Uri.parse(Const.MAIN_HOST_URL + icon));
            mPopupWindow.showAtLocation(tvGold, Gravity.BOTTOM, 0, 0);
            aniPopupWindowValuesHolder(hongbao);
        }
    }


    int PAGER_JSON = 1;

    public void getViewPagerJson(String token, String roomuid, String gifid) {
        request = NoHttp.createJsonObjectRequest(Const.MAIN_HOST_URL + "/OpenAPI/V1/Gift/robredgift", RequestMethod.GET);
        request.add("token", token);
        request.add("roomid", roomuid);
        request.add("red_id", gifid);
        requestQueueAppcation.add(PAGER_JSON, request, ViewPagerOnResponse);
    }

    private OnResponseListener<JSONObject> ViewPagerOnResponse = new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int i) {
        }

        @Override
        public void onSucceed(int i, Response<JSONObject> response) {
            if (i == PAGER_JSON) {// 判断what是否是刚才指定的请求
                //                {"code":0,"msg":"ok","data":"320200"}这个是什么
                // 请求成功
                JSONObject result = response.get();// 响应结果
                try {
                    JSONObject data = result.getJSONObject("data");
                    final int code = result.getInt("code");
                    final String amount = data.getString("amount");
                    final double coinbalance = data.getDouble("coinbalance");
                    if (code == 1) {
                        hongbaoamount.setText(getString(R.string.room_live_redpacket_over));
                        get.setVisibility(View.INVISIBLE);
                    } else {
                        hongbaoamount.setText(getString(R.string.room_live_redpacket_get) + amount);
                        //更新余额
                        updateBalance(coinbalance);
                        get.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
    };

    public void aniPopupWindowValuesHolder(View view) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha",
                0f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX",
                0, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY",
                0, 1f);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ).setDuration(1000).start();
    }

    //  时间转换类哦
    public String refFormatNowDate(long time) {
        Date nowTime = new Date(time);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy.MM.dd");
        String retStrFormatNowDate = sdFormatter.format(nowTime);
        return retStrFormatNowDate;
    }

    /**
     * 在会话列表中接收消息
     *
     * @param event 消息事件
     */
    public void onEvent(MessageEvent event) {

        Log.i("RayTest","onEvent:"+event.getResponseCode());
        /**
         * 接收消息类事件
         *
         * @param event 消息事件
         */
        try {
            final cn.jpush.im.android.api.model.Message msg = event.getMessage();
            //若为群聊相关事件，如添加、删除群成员
            Log.i(TAG, event.getMessage().toString());
            //刷新消息
            mcontext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //收到消息的类型为单聊
                    if (msg.getTargetType() == ConversationType.single) {
                        cn.jpush.im.android.api.model.UserInfo userInfo = (cn.jpush.im.android.api.model.UserInfo) msg.getTargetInfo();
                        String targetId = userInfo.getUserName();
                        String appKey = userInfo.getAppKey();
                        //判断消息是否在当前会话中
                        if (mIsSingle && targetId.equals(mTargetId)) {
                            cn.jpush.im.android.api.model.Message lastMsg = mChatAdapter.getLastMsg();
                            //收到的消息和Adapter中最后一条消息比较，如果最后一条为空或者不相同，则加入到MsgList
                            if (lastMsg == null || msg.getId() != lastMsg.getId()) {
                                mChatAdapter.addMsgToList(msg);
                            } else {
                                mChatAdapter.notifyDataSetChanged();

                            }
                        }
                    }
                }
            });

            /**
             * 在会话列表中接收消息
             *
             * @param event 消息事件
             */
//        cn.jpush.im.android.api.model.Message msg = event.getMessage();
            ConversationType convType = msg.getTargetType();
            if (convType == ConversationType.group) {
                long groupID = ((GroupInfo) msg.getTargetInfo()).getGroupID();
                Conversation conv = JMessageClient.getGroupConversation(groupID);
                if (conv != null) {

                    mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST,
                            conv));
                }
            } else {
                Log.i("RayTest","在会话列表中接收消息:"+event.getResponseCode());
                final cn.jpush.im.android.api.model.UserInfo userInfo = (cn.jpush.im.android.api.model.UserInfo) msg.getTargetInfo();
                final String targetID = userInfo.getUserName();
                final Conversation conv = JMessageClient.getSingleConversation(targetID, userInfo.getAppKey());
                if (conv != null && conv.getUnReadMsgCnt() > 0) {
                    mUIHandler.sendEmptyMessage(UNREAD);
                }
                if (conv != null) {
                    /*mcontext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //如果设置了头像
                            if (!TextUtils.isEmpty(userInfo.getAvatar())) {
                                //如果本地不存在头像
                                userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                                    @Override
                                    public void gotResult(int status, String desc, Bitmap bitmap) {
                                        if (status == 0) {
                                            mPriListAdapter.addmFuImg(userInfo.getUserName().replaceAll("user", ""));
                                        } else {
                                            HandleResponseCode.onHandle(mcontext, status, false);
                                        }
                                    }
                                });
                            }
                        }
                    });*/
                    mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST,
                            conv));
                }
            }
        } catch (Exception e) {
            if (JMessageClient.getMyInfo() == null) {

                for (int i = 0; i < 5; i++) {
                    register();
                    if (r = true) {
                        break;
                    }
                }
                if (r = true) {
                    for (int j = 0; j < 5; j++) {
                        login();
                        if (f = true) {
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 收到创建单聊的消息
     *
     * @param event 可以从event中得到targetID
     */
    public void onEventMainThread(Event.StringEvent event) {
        Log.i("RayTest","可以从event中得到targetID:"+event.getTargetId());
        try {
            String targetId = event.getTargetId();
            String appKey = event.getAppKey();
            final Conversation conv = JMessageClient.getSingleConversation(targetId, appKey);
            if (conv != null) {
                getConversationInfo(conv, new RoomFragmentPresenter.RoomCallback() {
                    @Override
                    public void onSuccessInfo(UserInfo data) {
                        PriConversation conversation = CreatePriConversation(data,conv);
                        mPriListAdapter.addPriConversation(conversation);
                    }
                });
            }
        } catch (Exception e) {
            if (JMessageClient.getMyInfo() == null) {

                for (int i = 0; i < 5; i++) {
                    register();
                    if (r = true) {
                        break;
                    }
                }
                if (r = true) {
                    for (int j = 0; j < 5; j++) {
                        login();
                        if (f = true) {
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * 收到创建或者删除群聊的消息
     *
     * @param event 从event中得到groupID以及flag
     */
    public void onEventMainThread(Event.LongEvent event) {
        Log.i("RayTest","从event中得到groupID以及flag:"+event.getFlag());
        long groupId = event.getGroupId();
        final Conversation conv = JMessageClient.getGroupConversation(groupId);
        if (conv != null && event.getFlag()) {
            getConversationInfo(conv, new RoomFragmentPresenter.RoomCallback() {
                @Override
                public void onSuccessInfo(UserInfo data) {
                    PriConversation conversation = CreatePriConversation(data,conv);
                    mPriListAdapter.addPriConversation(conversation);
                }
            });
            Log.i("RayTest","addNewConversation");
        } else {

            Log.i("RayTest","deleteConversation");
        }
    }

    /**
     * 收到保存为草稿事件
     *
     * @param event 从event中得到Conversation Id及草稿内容
     */
    public void onEventMainThread(Event.DraftEvent event) {
        Log.i("RayTest","从event中得到Conversation Id及草稿内容:"+event.getDraft());
        String draft = event.getDraft();
        String targetId = event.getTargetId();
        String targetAppKey = event.getTargetAppKey();
        Conversation conv;
        if (targetId != null) {
            conv = JMessageClient.getSingleConversation(targetId, targetAppKey);
        } else {
            long groupId = event.getGroupId();
            conv = JMessageClient.getGroupConversation(groupId);
        }
        //如果草稿内容不为空，保存，并且置顶该会话
        if (!TextUtils.isEmpty(draft)) {
            Log.i("RayTest","如果草稿内容不为空，保存，并且置顶该会话:"+conv.getId());
            String some= event.getTargetId();
            some=some.replace("user","");
            mPriListAdapter.putDraftToMap(some, draft);
            setToTop(conv);


            //否则删除
        } else {
            Log.i("RayTest","否则删除");
            String some= ((cn.jpush.im.android.api.model.UserInfo) conv.getTargetInfo()).getUserName();
            some=some.replace("user","");
            mPriListAdapter.delDraftFromMap(some);
        }
        //initPrivateChatConversation();
    }

    @Override
    public void setToTop(Conversation conversation) {
        Log.i("RayTest","setToTop ");
        if(conversation==null)
            return;
        String some= ((cn.jpush.im.android.api.model.UserInfo) conversation.getTargetInfo()).getUserName();
        some=some.replace("user","");
        Log.i("RayTest", "setToTop :"+some);
        tw.chiae.inlive.data.bean.me.UserInfo info = LocalDataManager.getInstance().getUserInfo(some);
        if(info!=null){
            mPriListAdapter.setToTop(conversation);
        } else{
            roomFragmentPresenter.seToTop(conversation);
        }

    }

    @Override
    public void onDestroy() {
        Log.i("RayTest", "RoomFragment onDestroy");
        isdeas = true;
        EventBus.getDefault().unregister(this);
        mcontext.unregisterReceiver(mReceiver);
        mBackgroundHandler.removeCallbacksAndMessages(null);
        mThread.getLooper().quit();

        removeMsgItem();
        mChatListView.setAdapter(null);
        mChatListView = null;
        audienceAdapter = null;
        audienceSidebarAdapter = null;
        mcontext.unregisterReceiver(receiver);
        try {
            mChatAdapter.releaseMediaPlayer();
            mChatAdapter = null;
        } catch (NullPointerException e) {
        }
        mUIHandler.removeCallbacksAndMessages(null);
        //requecQueueAppcation.cancelAll();
        //HotPointLinsten = null;
        //HotPointErrorLinsten = null;
       /* giftView.stop();
        giftView2.stop();*/
        getActivity().finish();
        mUIHandler = null;
        OnResponse = null;
        //ClearCache();
        //stopSpeedTest();
        mDanmuControl.destroy();
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

/*    private void ClearCache() {
        try {
            Fresco.getImagePipeline().clearCaches();
            DataCleanManager.clearAllCache(getActivity());
        } catch (Exception e) {
            L.w(LOG_TAG, "Error while clearing cache!", e);
        }

    }*/

    private void removeMsgItem() {

        for (int index = 0; index < recyclerPublicChat.getChildCount(); index++) {
            View vChild = recyclerPublicChat.getChildAt(index);
            PublicChatHolder viewholder = (PublicChatHolder) recyclerPublicChat.getChildViewHolder(vChild);
            viewholder.itemView.setOnClickListener(null);
            if (viewholder.getItem() != null)
                viewholder.getItem().removeItem();
        }

        publicChatAdapter.setOnItemClickListener(null);
        publicChatAdapter.StopAllRequest();

        recyclerPublicChat.setAdapter(null);
        mPriListAdapter = null;
        publicChatAdapter = null;

    }


    private class BackgroundHandler extends Handler {
        public BackgroundHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_CONVERSATION_LIST:
                    Log.i("RayTest","REFRESH_CONVERSATION_LIST");
                    Conversation conv = (Conversation) msg.obj;
                    setToTop(conv);
                    //mPriListAdapter.setToTop(conv);
                    break;
            }
        }
    }

    //    对比适配器里面的集合，和传过来的集合
    public void screenAudienList(List<AudienceInfo> oldList, List<AudienceInfo> newList, SimpleRecyclerAdapter adapter) {
        //        如果观众出去了
        if(imgbtnlist!=null){
            if(newList.size()<=0 )
                imgbtnlist.setVisibility(View.INVISIBLE);
            else
                imgbtnlist.setVisibility(View.VISIBLE);
        }
        if (oldList.size() > newList.size()) {
            int i;
            for (i = 0; i < newList.size(); i++) {
                String newRole =newList.get(i).getRole();
                String oldRole =oldList.get(i).getRole();
                if ((!newList.get(i).getUserId().equals(oldList.get(i).getUserId()))||(!newList.get(i).getRole().equals(oldList.get(i).getRole()))) {
                    oldList.set(i, newList.get(i));
                    adapter.notifyItemChanged(i);
                }
            }
            while (oldList.size() > i) {
                //adapter.notifyItemRemoved(i);
                oldList.remove(i);
                adapter.notifyItemRemoved(i);
                i++;
            }
        } else {
            int i;
            for (i = 0; i < oldList.size(); i++) {
                String newRole =newList.get(i).getRole();
                String oldRole =oldList.get(i).getRole();
                if ((!newList.get(i).getUserId().equals(oldList.get(i).getUserId()))||(!newList.get(i).getRole().equals(oldList.get(i).getRole())) ) {
                    oldList.set(i, newList.get(i));
                    adapter.notifyItemChanged(i);
                }
            }
            while (newList.size() > i) {
                oldList.add(newList.get(i));
                adapter.notifyItemChanged(i);
                i++;
            }
        }
    }

    public void requesetRoomLoginOut() {
        wsService.sendRequest(WsObjectPool.newLogoutRequest(mRoomId));
        stopPublishLive();
    }

    private class UIHandler extends Handler {
        private final WeakReference<RoomFragment> mActivity;

        public UIHandler(RoomFragment activity) {
            mActivity = new WeakReference<RoomFragment>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RoomFragment activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case REFRESH_LAST_PAGE:
                        activity.mChatAdapter.dropDownToRefresh();
                        activity.mChatListView.onDropDownComplete();
                        if (activity.mChatAdapter.isHasLastPage()) {
                            if (Build.VERSION.SDK_INT >= 21) {
                                activity.mChatListView
                                        .setSelectionFromTop(activity.mChatAdapter.getOffset(),
                                                activity.mChatListView.getHeaderHeight());
                            } else {
                                activity.mChatListView.setSelection(activity.mChatAdapter
                                        .getOffset());
                            }
                            activity.mChatAdapter.refreshStartPosition();
                        } else {
                            activity.mChatListView.setSelection(0);
                        }
                        activity.mChatListView
                                .setOffset(activity.mChatAdapter.getOffset());
                        break;
                    case UNREAD:
                        activity.newMsg.setVisibility(View.VISIBLE);
                        break;
                    case READ:
                        activity.newMsg.setVisibility(View.GONE);
                        break;
                }
            }
        }
    }

    // 监听耳机插入
    private void initreceiver() {
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        mcontext.registerReceiver(receiver, filter);
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent data) {
            try {
                if (data != null) {
                    //插入了耳机
                    if (data.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                        mChatAdapter.setAudioPlayByEarPhone(data.getIntExtra("state", 0));
                    }
                }
            } catch (NullPointerException e) {
            }
        }
    }

    private MsgListRoomAdapter.ContentLongClickListener longClickListener = new MsgListRoomAdapter.ContentLongClickListener() {
        @Override
        public void onContentLongClick(final int position, View view) {
            Log.i(TAG, "long click position" + position);
            final cn.jpush.im.android.api.model.Message msg = mChatAdapter.getMessage(position);
            cn.jpush.im.android.api.model.UserInfo userInfo = msg.getFromUser();
            if (msg.getContentType() != ContentType.image) {
                // 长按文本弹出菜单
                String name = userInfo.getNickname();
                View.OnClickListener listener = new View.OnClickListener() {

                    @SuppressLint("NewApi")
                    @Override
                    public void onClick(View v) {
                        if (v.getId() == IdHelper.getViewID(mcontext, "jmui_copy_msg_btn")) {
                            if (msg.getContentType() == ContentType.text) {
                                final String content = ((TextContent) msg.getContent()).getText();
                                if (Build.VERSION.SDK_INT > 11) {
                                    ClipboardManager clipboard = (ClipboardManager) mcontext
                                            .getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("Simple text", content);
                                    clipboard.setPrimaryClip(clip);
                                } else {
                                    android.text.ClipboardManager clip = (android.text.ClipboardManager) mcontext
                                            .getSystemService(Context.CLIPBOARD_SERVICE);
                                    if (clip.hasText()) {
                                        clip.getText();
                                    }
                                }

                                Toast.makeText(mcontext, IdHelper.getString(mcontext, "jmui_copy_toast"),
                                        Toast.LENGTH_SHORT).show();
                                mDialog.dismiss();
                            }
                        } else if (v.getId() == IdHelper.getViewID(mcontext, "jmui_forward_msg_btn")) {
                            mDialog.dismiss();
                        } else {
                            mConv.deleteMessage(msg.getId());
                            mChatAdapter.removeMessage(position);
                            mDialog.dismiss();
                        }
                    }
                };
                boolean hide = msg.getContentType() == ContentType.voice;
                mDialog = DialogCreator.createLongPressMessageDialog(mcontext, name, hide, listener);
                mDialog.show();
                mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
            } else {
                String name = msg.getFromUser().getNickname();
                View.OnClickListener listener = new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (v.getId() == IdHelper.getViewID(mcontext, "jmui_delete_msg_btn")) {
                            mConv.deleteMessage(msg.getId());
                            mChatAdapter.removeMessage(position);
                            mDialog.dismiss();
                        } else if (v.getId() == IdHelper.getViewID(mcontext, "jmui_forward_msg_btn")) {
                            mDialog.dismiss();
                        }
                    }
                };
                mDialog = DialogCreator.createLongPressMessageDialog(mcontext, name, true, listener);
                mDialog.show();
                mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
            }
        }
    };

    private void dismissSoftInput() {
        if (mShowSoftInput) {
            if (mImm != null) {
                mImm.hideSoftInputFromWindow(mChatInputEt.getWindowToken(), 0);
                mShowSoftInput = false;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理发送图片，刷新界面
     *
     * @param data intent
     */
    private void handleImgRefresh(Intent data) {
        mChatAdapter.setSendImg(data.getIntArrayExtra(MsgIDs));
        mChatListView.clearFocus();
        mChatListView.post(new Runnable() {
            @Override
            public void run() {
                mChatListView.setSelection(mChatListView.getAdapter().getCount() - 1);
            }
        });
    }

    @Override
    public void onPause() {
        stopAnim();
        isdeas = true;
        RecordVoiceButton.mIsPressed = false;
        JMessageClient.exitConversation();
        Log.i(TAG, "[Life cycle] - onPause");
        durationValue = 0;
        /*if(wsService!=null)
            wsService.stopSpeedTest();*/
        mDanmuControl.pause();
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("RayTest", "RoomFragment onStop");
        isdeas = true;
        stopAnim();
        try {
            mChatAdapter.stopMediaPlayer();
        } catch (NullPointerException e) {
        }
        if (mConv != null) {
            mConv.resetUnreadCount();
        }
        super.onStop();
    }
    @Override
    public void onResume() {
        isdeas = false;
        String targetId = mTargetId;
        if (null != targetId) {
            String appKey = mTargetAppKey;
            JMessageClient.enterSingleConversation(targetId, appKey);
        }
        Log.i(TAG, "[Life cycle] - onResume");
        if(wsService!=null){
           // wsService.startSpeedTest(getRoomType());
        }
        mDanmuControl.resume();
        super.onResume();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
            final Conversation conv = mConv;
            try {
                String originPath = mPhotoPath;
                Bitmap bitmap = BitmapLoader.getBitmapFromFile(originPath, 720, 1280);
                ImageContent.createImageContentAsync(bitmap, new ImageContent.CreateImageContentCallback() {
                    @Override
                    public void gotResult(int status, String desc, ImageContent imageContent) {
                        if (status == 0) {
                            cn.jpush.im.android.api.model.Message msg = conv.createSendMessage(imageContent);
                            Intent intent = new Intent();
                            intent.putExtra(MsgIDs, new int[]{msg.getId()});
                            handleImgRefresh(intent);
                        }
                    }
                });
            } catch (NullPointerException e) {
                Log.i(TAG, "onActivityResult unexpected result");
            }
        } else if (resultCode == RESULT_CODE_SELECT_PICTURE) {
            handleImgRefresh(data);
            //如果作为UIKit使用,去掉以下几段代码
        } else if (resultCode == RESULT_CODE_FRIEND_INFO) {
            if (mIsSingle) {
                String nickname = data.getStringExtra(NICKNAME);
                if (!TextUtils.isEmpty(nickname)) {
                    mChatTitle.setText(nickname);
                }
            }
        }
    }

    public void showAnim(int isread, String name, String sGiftName) {
        hidellHeader();
        imm.hideSoftInputFromWindow(edtChatContent.getWindowToken(), InputMethodManager
                .HIDE_NOT_ALWAYS);
        Log.i("RayTest","start showAnim  "+isread+"  "+sGiftName);
        if (animLayout == null)
            animLayout = $(view, R.id.room_live_show_anim_layout);
        if (isdeas) {
            return;
        }
        switch (isread) {
//            烟花
            case 2:
                isgiftend = false;
                startNewSakuraAnim(name, sGiftName);
                break;
//            砖石
            case 3:
                isgiftend = false;
                startKsAnim(name, sGiftName);
                break;
//            汽车
            case 4:
                isgiftend = false;
                startNewCarAnim(name, sGiftName);
                break;
//            飞机
            case 5:
                isgiftend = false;
                /*plane = new PlaneImagerView(getActivity());
                plane.setGitfSpecialsStop(this);
                animLayout.addView(plane);
                plane.initAnim(getActivity().getWindowManager().getDefaultDisplay().getWidth());*/
                startNewAnim("only",1,118,10000,name, sGiftName);
                break;
//            游艇
            case 6:
                isgiftend = false;
                startNewAnim("star",0,90,8000,name, sGiftName);
                break;
            case 7:
                isgiftend = false;
                startNewAnim("balloon",1,105,10000,name, sGiftName);
                break;

            case 8:
                isgiftend = false;
                startNewAnim("moon",1,120,10000,name, sGiftName);
                break;

            case 9:
                isgiftend = false;
                startNewAnim("hallo",0,144,9100,name, sGiftName);
                break;

            case 10:
                isgiftend = false;
                startNewAnim("xmas",0,134,9100,name, sGiftName);
                break;
            case 11:
                isgiftend = false;
                startNewAnim("elec",0,134,9100,name, sGiftName);
                break;
            case 12:
                isgiftend = false;
                startNewAnim("cheers",0,44,3000,name, sGiftName);
                break;
            case 13:
                isgiftend = false;
                startNewAnim("bravo",0,59,4000,name, sGiftName);
                break;
            case 14:
                isgiftend = false;
                startNewAnim("angel",0,119,8000,name, sGiftName);
                break;
            case 15:
                isgiftend = false;
                startNewAnim("babu",0,37,2500,name, sGiftName);
                break;
            case 16:
                isgiftend = false;
                startNewAnim("white_eyes",0,31,2000,name, sGiftName);
                break;
            case 17:
                isgiftend = false;
                startNewAnim("ring",0,47,4000,name, sGiftName);
                break;
            case 18:
                isgiftend = false;
                startNewAnim("lian",0,47,4000,name, sGiftName);
                break;
            case 19:
                isgiftend = false;
                startNewAnim("chocolate",0,47,4000,name, sGiftName);
                break;
            case 20:
                isgiftend = false;
                startNewAnim("egg",0,35,3000,name, sGiftName);
                break;
            case 21:
                isgiftend = false;
                startNewAnim("new_years",0,133,9000,name, sGiftName);
                break;
            case 22:
                isgiftend = false;
                startNewAnim("firecracker",0,59,4000,name, sGiftName);
                break;
            case 23:
                isgiftend = false;
                startNewAnim("redenvelope",0,56,4000,name, sGiftName);
                break;


        }
    }

    private void startNewAnim(final String fileName, int minVal, int maxVal, int sec, final String name, final String sGiftName) {
        Log.i("RayTest", "fileName:"+fileName);
        final Resources resource = BeautyLiveApplication.getContextInstance().getResources();
        final String PackName = getContext().getPackageName();
        final int ksAnimSize = maxVal;
        if (bigAnimStatus)
            return;
        if (ksAnimSize <= 0)
            return;
        bigAnimStatus = true;
        ksBigGift.setVisibility(View.VISIBLE);
        //big520_Test.setAlpha(0.8f);
        ksBigGift.setScaleType(ImageView.ScaleType.CENTER_CROP);
        valueAnimator = ValueAnimator.ofInt(minVal, maxVal);
        valueAnimator.setDuration(sec);
/*        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);*/
        if (Build.VERSION.SDK_INT < 19) {
            decorView.setSystemUiVisibility(View.GONE);
        } else {
            //for higher api versions.
            decorView.setSystemUiVisibility(HideUiOptions);
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (int) animation.getAnimatedValue();
                float alphaVal = getKsTextAlphaValue(val);
                ksBigGiftText.setAlpha(alphaVal);
                ksBigGiftText.setText(name + " " + sGiftName);
                ksBigGiftText.setAlpha(0.87f);
                ksBigGiftText.setVisibility(View.VISIBLE);
                if (ksAnimSize == 0)
                    ksBigGift.setImageDrawable(null);
                else{
                    int resId = startNewAnimImgId(resource,PackName, val,fileName);

                    if(resId>0)
                        ksBigGift.setImageResource(resId);
                }

                //big520_Test.setImageResource(R.drawable.test1);
                if (val == ksAnimSize - 1) {
                    bigAnimStatus = false;
                    isgiftend = true;
                    ksBigGift.setVisibility(View.GONE);
                    ksBigGiftText.setVisibility(View.GONE);
                    valueAnimator.removeUpdateListener(this);
                    decorView.setSystemUiVisibility(ShowUiOption);
                    animend();
                }
            }

            private float getKsTextAlphaValue(int val) {
           /*     int startValue = 1 ;
                int endValue = KsBigGiftdrawables.size()-13;
                if(val<startValue || KsBigGiftdrawables.size()<startValue ||val > endValue)
                    return  0;
                else*/
                return 1;
               /* float RatioValue = 1/ ((float)(KsBigGiftdrawables.size())-20);
                Log.i("RayTest","anim Ratio:"+RatioValue+" val:"+val*RatioValue);
                return val*RatioValue;*/
            }
        });
        valueAnimator.start();
    }

    private int startNewAnimImgId(Resources res , String PackageName, int val,String filename) {
        return res.getIdentifier(filename+"_" + val, "drawable",
                PackageName);
    }

    private void startNewSakuraAnim(final String name, final String sGiftName) {
        Log.i("RayTest", "startNewCarAnim");
        final int ksAnimSize = 103;
        if (bigAnimStatus)
            return;
        if (ksAnimSize <= 0)
            return;
        bigAnimStatus = true;
        ksBigGift.setVisibility(View.VISIBLE);
        //big520_Test.setAlpha(0.8f);
        ksBigGift.setScaleType(ImageView.ScaleType.CENTER_CROP);
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(1, ksAnimSize);
        valueAnimator.setDuration(10000);
/*        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);*/
        if (Build.VERSION.SDK_INT < 19) {
            decorView.setSystemUiVisibility(View.GONE);
        } else {
            //for higher api versions.
            decorView.setSystemUiVisibility(HideUiOptions);
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (int) animation.getAnimatedValue();
                float alphaVal = getKsTextAlphaValue(val);
                ksBigGiftText.setAlpha(alphaVal);
                ksBigGiftText.setText(name + " " + sGiftName);
                ksBigGiftText.setAlpha(0.87f);
                ksBigGiftText.setVisibility(View.VISIBLE);
                if (ksAnimSize == 0)
                    ksBigGift.setImageDrawable(null);
                else{
                    int resId = getNewSakuraImageId(getContext(), val);

                    if(resId>0)
                        ksBigGift.setImageResource(getNewSakuraImageId(getContext(), val));
                }

                //big520_Test.setImageResource(R.drawable.test1);
                if (val == ksAnimSize - 1) {
                    bigAnimStatus = false;
                    isgiftend = true;
                    ksBigGift.setVisibility(View.GONE);
                    ksBigGiftText.setVisibility(View.GONE);
                    valueAnimator.removeUpdateListener(this);
                    decorView.setSystemUiVisibility(ShowUiOption);
                    animend();
                }
            }

            private float getKsTextAlphaValue(int val) {
           /*     int startValue = 1 ;
                int endValue = KsBigGiftdrawables.size()-13;
                if(val<startValue || KsBigGiftdrawables.size()<startValue ||val > endValue)
                    return  0;
                else*/
                return 1;
               /* float RatioValue = 1/ ((float)(KsBigGiftdrawables.size())-20);
                Log.i("RayTest","anim Ratio:"+RatioValue+" val:"+val*RatioValue);
                return val*RatioValue;*/
            }
        });
        valueAnimator.start();
    }

    private int getNewSakuraImageId (Context context, int val) {
        return context.getResources().getIdentifier("s1_" + val, "drawable",
                context.getPackageName());
    }

    private void startNewCarAnim(final String sName, final String GiftName) {

        Log.i("RayTest", "startNewCarAnim");
        final int ksAnimSize = 126;
        if (bigAnimStatus)
            return;
        if (ksAnimSize <= 0)
            return;
        bigAnimStatus = true;
        ksBigGift.setVisibility(View.VISIBLE);
        //big520_Test.setAlpha(0.8f);
        ksBigGift.setScaleType(ImageView.ScaleType.CENTER_CROP);
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(1, ksAnimSize);
        valueAnimator.setDuration(13000);
/*        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);*/
        if (Build.VERSION.SDK_INT < 19) {
            decorView.setSystemUiVisibility(View.GONE);
        } else {
            //for higher api versions.
            decorView.setSystemUiVisibility(HideUiOptions);
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (int) animation.getAnimatedValue();
                float alphaVal = getKsTextAlphaValue(val);
                ksBigGiftText.setAlpha(alphaVal);
                ksBigGiftText.setText(sName + " " + GiftName);
                ksBigGiftText.setAlpha(0.87f);
                ksBigGiftText.setVisibility(View.VISIBLE);
                if (ksAnimSize == 0)
                    ksBigGift.setImageDrawable(null);
                else
                    ksBigGift.setImageResource(getNewCarGifImageId(getContext(), val));
                //big520_Test.setImageResource(R.drawable.test1);
                if (val == ksAnimSize - 1) {
                    bigAnimStatus = false;
                    isgiftend = true;
                    ksBigGift.setVisibility(View.GONE);
                    ksBigGiftText.setVisibility(View.GONE);
                    valueAnimator.removeUpdateListener(this);
                    decorView.setSystemUiVisibility(ShowUiOption);
                    animend();
                }
            }

            private float getKsTextAlphaValue(int val) {
           /*     int startValue = 1 ;
                int endValue = KsBigGiftdrawables.size()-13;
                if(val<startValue || KsBigGiftdrawables.size()<startValue ||val > endValue)
                    return  0;
                else*/
                return 1;
               /* float RatioValue = 1/ ((float)(KsBigGiftdrawables.size())-20);
                Log.i("RayTest","anim Ratio:"+RatioValue+" val:"+val*RatioValue);
                return val*RatioValue;*/
            }
        });
        valueAnimator.start();
    }

    private void startBigAnim(String sName) {
       /* surface.setVisibility(View.VISIBLE);
        surface.start();*/


        //Uri imageUri = Uri.parse("https://lindalue.github.io/webp/star_p.webp");
        /*Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                .path(String.valueOf(R.drawable.star_p309_550_15min))
                .build();*/
 /*       big520_car.setVisibility(View.VISIBLE);*/
        big520_text.setVisibility(View.VISIBLE);
        big520_bg.setVisibility(View.VISIBLE);
        //RunCar520Car();
        //AbstractDraweeController controller = Fresco.newDraweeControllerBuilder().setUri(uri).setControllerListener(controllerListener).build();

        //big520_car_2.setController(controller);
        big520_text.setText(sName + "\r\n陪你去看流星雨 ");

    }


/*    ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {

        @Override
        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
            //super.onFinalImageSet(id, imageInfo, animatable);
            if (animatable != null) {
                Field field = null;
                try {
                    field = AbstractAnimatedDrawable.class.getDeclaredField("mLoopCount");
                    field.setAccessible(true);
                    field.set(animatable, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                animatable.start();
            }
            if (animatable instanceof AbstractAnimatedDrawable) {
                int duration = ((AbstractAnimatedDrawable) animatable).getDuration();
                getView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("RayTest", "end");
                        big520_car_2.setVisibility(View.GONE);
                        big520_text.setVisibility(View.GONE);
                        big520_bg.setVisibility(View.GONE);
                        animend();
                    }
                }, duration);
            }
        }
    };*/

    @Override
    public void animend() {
        Log.i("RayTest", "animend");

        isgiftend = true;
        try {
            clearAnim(giftSpecials.get(0));
            giftSpecials.remove(0);
            giftFromUserName.remove(0);
            giftNameList.remove(0);
        }catch (Exception e){
            Log.i("RayTest","no anim clear");
        }

        if (giftSpecials.size() > 0 && isgiftend) {
            isgiftend = false;
            showAnim(giftSpecials.get(0), giftFromUserName.get(0), giftNameList.get(0));
            return;
        }
        VisiblellHeader();
    }

    public void clearAnim(int giftisread) {
        animLayout.removeAllViews();
        switch (giftisread) {
            case 2:
                fireworks = null;
                break;
            case 3:
                gen = null;
                break;
            case 4:
                car = null;
                break;
            case 5:
                plane = null;
                break;
            case 6:
                ship = null;
                break;
        }
    }


    public void login() {
        JMessageClient.login(mMyName, mMyPassword, new BasicCallback() {
            @Override
            public void gotResult(int status, String desc) {
                if (status == 0) {
                    f = true;
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
                    r = true;
                } else {
                    if (desc.equals("user exist")) {
                        r = true;
                    }
                }
            }
        });
    }

    final int DATA_JSON = 11;

    public void getDatas(String user_id) {
       /* OnResponse = new mResponseListener(this, mDatas, msghandler);
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(Const.WEB_BASE_URL + "user/profile", RequestMethod.GET);
        request.add("uid", user_id);
        request.add("token", LocalDataManager.getInstance().getLoginInfo().getToken());
        requestQueueAppcation.add(DATA_JSON, request, OnResponse);*/
    }

    /*private OnResponseListener<JSONObject> OnResponse = new mResponseListener(this, mDatas, handler);*/
    private static class mResponseListener implements OnResponseListener<JSONObject> {

        private final List<Conversation> mDatas;
        private final Gson gson;
        private final int DATA_JSON;
        private final MsgHandler handler;
        private RoomFragment mfragment;

        public mResponseListener(RoomFragment fragment, List<Conversation> datas, MsgHandler handler) {
            this.mfragment = fragment;
            this.mDatas = datas;
            this.gson = fragment.gson;
            this.DATA_JSON = fragment.DATA_JSON;
            this.handler = handler;
        }


        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<JSONObject> response) {
            if (what == DATA_JSON) {// 判断what是否是刚才指定的请求
                //                {"code":0,"msg":"ok","data":"320200"}这个是什么
                // 请求成功
                if (mfragment != null) {
                    JSONObject result = response.get();// 响应结果
                    try {
                        Log.i("RayTest", "result:" + result);
                        JSONObject json = result.getJSONObject("data");
                        if (json == null) {
                            mfragment.toastShort("資料取得失敗 請重新登入房間");
                            mfragment.onDestroyView();
                            return;
                        }

                        mTestInfo = gson.fromJson(json.toString(), UserInfo.class);
                        cn.jpush.im.android.api.model.UserInfo userInfo;
                        for (int j = 0; j < mDatas.size(); j++) {
                            userInfo = ((cn.jpush.im.android.api.model.UserInfo) mDatas.get(j).getTargetInfo());
                            if (userInfo.getUserName().replaceAll("user", "").equals(mfragment.mTestInfo.getId())) {
                                mfragment.mFuImgUrl.put(userInfo.getUserName().replaceAll("user", ""), mfragment.mTestInfo.getAvatar());
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // 响应头
                    Headers headers = response.getHeaders();
                    headers.getResponseCode();// 响应码
                    response.getNetworkMillis();// 请求花费的时间
                }

            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

        }

        @Override
        public void onFinish(int what) {
            mfragment.count++;
            if (mfragment.count == mDatas.size()) {
                handler.sendEmptyMessage(21);
            }
        }
    }


  /*  private OnResponseListener<JSONObject> OnResponse = new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int i) {
        }

        @Override
        public void onSucceed(int i, Response<JSONObject> response) {
            if (i == DATA_JSON) {// 判断what是否是刚才指定的请求
                //                {"code":0,"msg":"ok","data":"320200"}这个是什么
                // 请求成功
                JSONObject result = response.get();// 响应结果
                try {
                    JSONObject json = result.getJSONObject("data");
                    mTestInfo = gson.fromJson(json.toString(), UserInfo.class);
                    cn.jpush.im.android.api.model.UserInfo userInfo;
                    for (int j = 0; j < mDatas.size(); j++) {
                        userInfo = ((cn.jpush.im.android.api.model.UserInfo) mDatas.get(j).getTargetInfo());
                        if (userInfo.getUserName().replaceAll("user", "").equals(mTestInfo.getId())) {
                            mFuImgUrl.put(userInfo.getUserName().replaceAll("user", ""), mTestInfo.getAvatar());
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
            count++;
            if (count == mDatas.size()) {
                handler.sendEmptyMessage(21);
            }
        }
    };*/


    //    拉去服务器得
    public void initDatas(List<Conversation> data) {
        mFuImgUrl = new HashMap<>();
        cn.jpush.im.android.api.model.UserInfo info;
        if (gson == null)
            gson = new Gson();
        for (int i = 0; i < data.size(); i++) {
            info = (cn.jpush.im.android.api.model.UserInfo) data.get(i).getTargetInfo();
            getDatas(info.getUserName().replaceAll("user", ""));
        }

    }

    public int getBalamceValue() {
        //getHotPoint(mRoomId);
        return RoomInfoTmp.getAnchorBalanceValue;
    }

    public void checkActivateEvent() {
        roomFragmentPresenter.checkActivateEvent();
    }

    private void Check_event(int id, View view, int viewable) {
        if ((viewable == 1)) {
            if (getRoomType() == RoomActivity.TYPE_PUBLISH_LIVE) {
                view.setVisibility(View.INVISIBLE);
            } else
                view.setVisibility(View.VISIBLE);
        } else {

            view.setVisibility(View.INVISIBLE);
        }
    }



    public void startEvent6Event() {
        CEWebKit.getInstance().open(getActivity(), 6, LocalDataManager.getInstance().getLoginInfo().getUserId(), new CEWebKit.EventHandler() {
            @Override
            public void openUserView(CEWebActivity activity, String userId) {
                // 切換到 userId 指定的使用者頁面
                Log.i("RayTest", "openUserView");
                activity.startActivity(OtherUserActivity.createIntent(getActivity(),
                        Integer.valueOf(userId), false));
            }

            @Override
            public void openRecordView(CEWebActivity activity, String uploadPage, String videoUrl, String lyricsUrl, String extraInfo) {
                Log.i("RayTest", "openRecordView");

            }

            @Override
            public void onUploadComplete(String filePath) {
                Log.i("RayTest", "onUploadComplete");
                // 上傳完成處理, 刪除檔案...
            }

            @Override
            public void onUploadError() {
                toastShort("上傳失敗..請重新上傳");
                // 上傳錯誤處理, 顯示訊息...
            }
        });

    }

    public void startEventById(int eventid) {
        Log.i("RayTest","startEventById:"+eventid);
        CEWebKit.getInstance().open(getActivity(), eventid, LocalDataManager.getInstance().getLoginInfo().getUserId(), null);

    }

    public void startEventById2(int eventid){

        EventChecker eventChecker = new EventChecker(getContext().getApplicationContext());
        String sEventUrl = eventChecker.getUrlbyId(eventid, new EventChecker.EventCallback() {
            @Override
            public void getUrl(int id, String name, String fullUrl) {
                simpleWebDialog.showWebContent(getFragmentManager(),fullUrl,SimpleWebDialog.TYPE_WEB_EVENT);
            }
        });
        Log.i("RayTest","sEventUrl:"+sEventUrl);
    }

    private void showErrorMsgDiaLog(Activity currentActivity, String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity)
                .setTitle("公告")
                .setMessage(str)
                .setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }
    @Override
    public void CompleteDelBlackList(List<BlackList> blackUids, int code, String hitid) {
        if(dialog!=null)
            dialog.getRemoveHitCode(code, hitid);
        Log.i("RayTest","刪除成功");
    }

    @Override
    public void CompleteAddBlackList(List<BlackList> blackUserIds, String hitid) {
        if(dialog!=null)
            dialog.getHitCode(0, hitid);
        Log.i("RayTest","新增成功");
    }

    public boolean CheckBlackList() {

        return LocalDataManager.getInstance().getIsHit(mAnchorId);
    }

    public void showAlreadyDialog(String id){
        if(dialogFragment!=null)
            dialogFragment.showCheckDelDialog(getActivity().getSupportFragmentManager(),id,CreateViewDialogFragment.TYPE_ADD_FAVORITE_ALREADY_BLACK);
    }
}

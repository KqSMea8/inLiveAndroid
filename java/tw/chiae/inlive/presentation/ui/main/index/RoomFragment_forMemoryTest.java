package tw.chiae.inlive.presentation.ui.main.index;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.HandlerThread;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.model.Conversation;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;
import tw.chiae.inlive.data.bean.AnchoBean;
import tw.chiae.inlive.data.bean.Danmu;
import tw.chiae.inlive.data.bean.websocket.SendGiftMsg;
import tw.chiae.inlive.data.bean.websocket.UserPrvMsg;
import tw.chiae.inlive.data.bean.websocket.UserPublicMsg;
import tw.chiae.inlive.data.websocket.WebSocketService;
import tw.chiae.inlive.nohttp.HttpListener;
import tw.chiae.inlive.presentation.ui.base.BaseFragment;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;
import tw.chiae.inlive.presentation.ui.chatting.DropDownListView;
import tw.chiae.inlive.presentation.ui.room.RoomActivity;
import tw.chiae.inlive.presentation.ui.room.RoomFragment;
import tw.chiae.inlive.presentation.ui.room.gift.IAnimController;
import tw.chiae.inlive.presentation.ui.room.prvmsg.MsgListRoomAdapter;
import tw.chiae.inlive.presentation.ui.room.prvmsg.PrvListAdapter;
import tw.chiae.inlive.presentation.ui.room.pubmsg.PublicChatAdapter;
import tw.chiae.inlive.presentation.ui.widget.roomanim.CarView;
import tw.chiae.inlive.presentation.ui.widget.roomanim.FireworksView;
import tw.chiae.inlive.presentation.ui.widget.roomanim.GenView;
import tw.chiae.inlive.presentation.ui.widget.roomanim.GitfSpecialsStop;
import tw.chiae.inlive.presentation.ui.widget.roomanim.PlaneImagerView;
import tw.chiae.inlive.presentation.ui.widget.roomanim.ShipView;
import tw.chiae.inlive.util.TimingLogger;

/**
 * Created by rayyeh on 2017/6/8.
 */

public abstract class RoomFragment_forMemoryTest extends BaseFragment implements RoomActivity.HasInputLayout, HttpListener<Bitmap>, BaseUiInterface, GitfSpecialsStop {
    private static final String MsgIDs = "msgIDs";
    private static final String NICKNAME = "nickname";
    private static final int REQUEST_CODE_TAKE_PHOTO = 4;
    private static final int RESULT_CODE_SELECT_PICTURE = 8;
    private static final int RESULT_CODE_FRIEND_INFO = 17;
    private static final int REFRESH_LAST_PAGE = 0x1023;


    private boolean mIsSingle = true;
    private boolean mShowSoftInput = false;
    private MsgListRoomAdapter mChatAdapter;
    private Conversation mConv;

    private cn.jpush.im.android.api.model.UserInfo mMyInfo;
    private String draft;
    private String mTargetId;
    private String mTargetAppKey;
    private String mPhotoPath = null;

    Window mWindow;
    InputMethodManager mImm;

    private static String TAG = RoomFragment.class.getSimpleName();
    private Activity mcontext;

    private HandlerThread mThread;
    private static final int REFRESH_CONVERSATION_LIST = 0x3000;
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

    protected TextView tvGold, mPrvChat, tvChatName, mChatTitle, unRead, newMsg;
    private ImageButton ibBack, ibToBack, ibSysBack;
    private AnchoBean mAnchoBean;
    private Button mSendMsgBtn;

    protected boolean isFirstVBalance = false;
    protected Double vFirstVBalance = Double.valueOf(0);


    private IAnimController localGiftController;

    public LinearLayout llOperationBar, mRankLay;

    public LinearLayout llChatBar, llIn, llSys;
    public RelativeLayout llHeader;
    public RelativeLayout rlPrvChat;
    public NestedScrollView mRoomScroll;
    public EditText edtChatContent, mChatInputEt;
    private PublicChatAdapter publicChatAdapter;

    private DropDownListView mChatListView;

    private ListView roomShow;
    private List<Conversation> mDatas = new ArrayList<Conversation>();
    private PrvListAdapter mListAdapter;
    private Dialog mDialog;
    /**
     * 实时在线观看人数。
     */
    //protected TextView tvOnlineCount;
    private RecyclerView recyclerAudienceList;

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
    //      根据弹幕开关显示对应的弹幕开关样式
    private LinearLayout danmu_layout_close, danmu_layot_open;
    //      弹幕view
    public DanmakuView mDanmakuView;
    //   弹幕分析器
    public BaseDanmakuParser mParser;
    //    弹幕context
    public DanmakuContext mContext;


    //    弹幕对象
    public Danmu danmu;
    //  头像请求
    Request<Bitmap> request;

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
    private List<Integer> gitfSpecials;
    private List<String> gitfName;
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
    @Override
    public void animend() {

    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    @Override
    public void onSucceed(int what, Response<Bitmap> response) {

    }

    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

    }

    @Override
    protected void initViews(View view) {

    }
    public void setmAnchorId(String mAnchorId) {
        this.mAnchorId = mAnchorId;
    }

    @Override
    public void showInputLayout(boolean show) {

    }

    @Override
    public ViewGroup getInputLayout() {
        return null;
    }

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

    protected abstract void stopPublishLive();
}

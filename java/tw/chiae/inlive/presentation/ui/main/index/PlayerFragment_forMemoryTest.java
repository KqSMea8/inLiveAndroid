package tw.chiae.inlive.presentation.ui.main.index;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daniulive.smartplayer.SmartPlayerJni;
import com.yqritc.scalablevideoview.ScalableVideoView;

import java.util.List;

import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.LiveSummary;
import tw.chiae.inlive.data.bean.gift.Gift;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseFragment;
import tw.chiae.inlive.presentation.ui.room.RoomActivity;
import tw.chiae.inlive.presentation.ui.room.RoomFragment;
import tw.chiae.inlive.presentation.ui.room.player.PlayerFragment;
import tw.chiae.inlive.presentation.ui.room.player.PlayerPresenter;
import tw.chiae.inlive.presentation.ui.room.player.PlayerUiInterface;
import tw.chiae.inlive.presentation.ui.room.player.RoomShareHelper;
import tw.chiae.inlive.presentation.ui.widget.giftView.GiftLayoutView;
import tw.chiae.inlive.util.L;

/**
 * Created by rayyeh on 2017/6/8.
 */

public class PlayerFragment_forMemoryTest  extends RoomFragment_forMemoryTest implements PlayerUiInterface {
    private static final String ARG_ANCHOR_SUMMARY = "anchor";
    private static final String ARG_ANCHOR_PLAYURL = "backurl";
    private static final String ARG_ANCHOR_ISBACK = "isback";

    private LiveSummary mSummary;
    private PlayerPresenter presenter;
    private RoomShareHelper shareHelper;

    private int PAGER_JSON;
    //播放源地址
    private String playbackUrl;

    private View mRoomOwner; //房间主人控件
    private AlertDialog alertDialog;

    private GiftLayoutView mGiftView;
    //                          礼物弹出窗
    private View mChargeLay, mGiftLay;
    private TextView mChargeTv;
    private ImageView mGiftSentBtn,mchargeinforBtn;

    private boolean hasSendHeartRequest = false;

    private int giftComboCount;

    private ScalableVideoView mMp4Video;

    //    是否是回播
    private boolean isBackPlay = false;
    //    回播得到的数据源房间号
    private String roomidmsg;
    //    回播地址
    private String backplayurl;
    //    回播视频的开始时间
    private String backstarttime;

    RelativeLayout surfaceFrame;
    //  记录视频变化
    private int oldWidth = -1, oldHeight = -1;
    //    记录获得的信息是否是退出了的
    private boolean isgetTopContributeUsers;

    //我的推流地址
    private String myPushAddress;
    //推流的json地址
    private String streamJsonStrFromServer;
    //h关于重连handel的
    private static final int MESSAGE_ID_RECONNECTING = 0x01;
    //是否暂停界面
    private boolean mIsActivityPaused = true;
    //是否推流中
    private boolean mIsPublishStreamStarted = false;

    //大牛的管理文
    private SmartPlayerJni libPlayer = null;
    //大牛的隨便一個surface
    private SurfaceView daNiuSurfaceView = null;
    //大牛player初始化的一個結果，如果失敗則為0，所有的操作都圍繞這這個值
    private long playerHandle = 0;
    @Override
    public void showGiftList(List<Gift> giftList) {

    }

    @Override
    public void onPlaybackReady(String playbackUrl) {

    }

    @Override
    public void getStartCode(int code) {

    }

    @Override
    public void getRemoveStartCode(int code) {

    }

    @Override
    public void showUserInfo(UserInfo userInfo) {

    }

    @Override
    public void onMyPushReady(String address) {

    }

    @Override
    public void upDataLoginBalance(String coinbalance) {

    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_room_player;
    }

    @Override
    protected int getRoomType() {
        return RoomActivity.TYPE_VIEW_LIVE;
    }

    @Override
    protected void parseArguments(Bundle bundle) {
        isBackPlay = bundle.getBoolean(ARG_ANCHOR_ISBACK);
        roomidmsg = bundle.getString(ARG_ANCHOR_PLAYURL);
        mSummary = bundle.getParcelable(ARG_ANCHOR_SUMMARY);
        if (mSummary == null) {
            L.e(LOG_TAG, "Argument is null!");
            toastShort(R.string.msg_argument_error);
        }
        String selfUid = LocalDataManager.getInstance().getLoginInfo().getUserId();
        if (selfUid.equals(mSummary.getId()) && !isBackPlay) {
            toastShort(getString(R.string.player_live_cannotme));
            ((RoomActivity) getActivity()).finishRoomActivity();
        }
    }

    @Override
    protected String getWsRoomId() {
        if (isBackPlay) {
            return mSummary.getCurroomnum() + backstarttime;
        } else {
            return mSummary.getCurroomnum();
        }
    }

    @Override
    protected String getWsUserId() {
        if (isBackPlay) {
            return mSummary.getId() + backstarttime;
        } else {
            return mSummary.getId();
        }
    }

    @Override
    protected boolean shouldSendHeartRequest() {
        return false;
    }

    @Override
    protected void updateBalance(double coinbalance) {

    }

    @Override
    protected void setMute(boolean closeMute) {

    }

    @Override
    protected void sendDanmu(String roomid, String content) {

    }

    @Override
    protected void stopPublishLive() {

    }

    public static BaseFragment newInstance(@NonNull Bundle bundle, String roomidmsg, boolean isPlay) {
        PlayerFragment_forMemoryTest fragment = new PlayerFragment_forMemoryTest();
        bundle.putString(ARG_ANCHOR_PLAYURL, roomidmsg);
        bundle.putBoolean(ARG_ANCHOR_ISBACK, isPlay);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public ViewGroup getPriInputLayout() {
        return null;
    }

    @Override
    public void onCancelDialogcheck(Bundle mArgs) {

    }
}

package tw.chiae.inlive.presentation.ui.room.player;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daniulive.smartplayer.SmartPlayerJni;
import com.eventhandle.SmartEventCallback;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;
import com.videoengine.NTRenderer;

import tw.chiae.inlive.BeautyLiveApplication;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.LiveSummary;
import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.data.bean.gift.Gift;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.main.MainActivity;
import tw.chiae.inlive.presentation.ui.main.currency.CurrencyActivity;
import tw.chiae.inlive.presentation.ui.main.webview.SimpleWebViewActivity;
import tw.chiae.inlive.presentation.ui.room.RoomActivity;
import tw.chiae.inlive.presentation.ui.room.RoomFragment;
import tw.chiae.inlive.presentation.ui.room.RoomInfoTmp;
import tw.chiae.inlive.presentation.ui.widget.giftView.GiftClickListener;
import tw.chiae.inlive.presentation.ui.widget.giftView.GiftLayoutView;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.EventUtil;
import tw.chiae.inlive.util.L;

import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;
import com.yqritc.scalablevideoview.ScalableType;
import com.yqritc.scalablevideoview.ScalableVideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;
import rx.functions.Func1;
import tw.inlive.paymentsdk.PaymentIAB;

/**
 * @author Muyangmin
 * @since 1.0.0
 * 观看直播
 */
public class PlayerFragment extends RoomFragment implements PlayerUiInterface, SimpleWebDialog.dialogListener {

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

    static {
        System.loadLibrary("SmartPlayer");
    }

    private DecimalFormat decimalFormat;
    private Dialog hud;
    private boolean isUpdateCoin = false;
    private Request<JSONObject> request;
    private boolean OpenRank = false;
    private SimpleWebDialog simpleWebDialog;

    public static PlayerFragment newInstance(@NonNull Bundle bundle, String roomidmsg, boolean isPlay) {

        PlayerFragment fragment = new PlayerFragment();
        bundle.putString(ARG_ANCHOR_PLAYURL, roomidmsg);
        bundle.putBoolean(ARG_ANCHOR_ISBACK, isPlay);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static Bundle createArgs(@NonNull LiveSummary summary) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_ANCHOR_SUMMARY, summary);
        return bundle;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_room_player;
    }

    public void getViewPagerJson(String user_id, String user_token) {
        if(request==null)
            request = NoHttp.createJsonObjectRequest(Const.MAIN_HOST_URL + "/OpenAPI/V1/Anchor/getAnchorBean", RequestMethod.GET);
        request.add("token", user_token);
        request.add("user_id", user_id);
        BeautyLiveApplication.getRequestQueue().add(PAGER_JSON, request, ViewPagerOnResponse);
    }

    private OnResponseListener<JSONObject> ViewPagerOnResponse = new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int i) {
        }

        @Override
        public void onSucceed(int i, Response<JSONObject> response) {
            if (i == PAGER_JSON) {// 判断what是否是刚才指定的请求R
                //                {"code":0,"msg":"ok","data":"320200"}这个是什么
                // 请求成功
                JSONObject result = response.get();// 响应结果
                try {
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("douzi", result.getString("data"));
                    msg.what = 1;
                    msg.setData(data);
                    handler.sendMessage(msg);
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
            handler.sendEmptyMessage(2);
        }
    };

/*    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                tvGold.setText(new java.text.DecimalFormat("#").format(Double.valueOf(msg.getData().getString("douzi"))));
                if(!isFirstVBalance){
                    vFirstVBalance = Double.valueOf(msg.getData().getString("douzi"));
                    isFirstVBalance = true;
                }
            } else if (msg.what == 2) {
            }
        }
    };*/

    private mHandler handler = new mHandler(this);

    @Override
    public void WebDialogDismiss() {
        if(OpenRank)
            OpenRank = false;
    }

    private static class mHandler extends Handler {

        private PlayerFragment mFragment;

        public mHandler(PlayerFragment fragment) {
            this.mFragment = fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            if(mFragment==null)
                return;
            if (msg.what == 1) {
                mFragment.tvGold.setText(new java.text.DecimalFormat("#").format(Double.valueOf(msg.getData().getString("douzi"))));
                if(!mFragment.isFirstVBalance){
                    mFragment.vFirstVBalance = Double.valueOf(msg.getData().getString("douzi"));
                    mFragment.isFirstVBalance = true;
                }
            } else if (msg.what == 2) {
            }
        }
    }



    @Override
    protected void initViews(View view) {
        super.initViews(view);
        timingLogger.reset(TIMING_LOG_TAG, "PlayerFragment#initViews");
        presenter = new PlayerPresenter(this);
        simpleWebDialog  = SimpleWebDialog.newInstance();
        simpleWebDialog.setDialogListener(this);
        shareHelper = new RoomShareHelper($(view, R.id.room_ll_share), mSummary.getCurroomnum(), mSummary.getNickname(), mSummary.getAvatar());

        decimalFormat = new DecimalFormat("#");

        if (toptabstart != null) {

            toptabstart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(CheckBlackList()){
                        // 已經是黑名單
                        showAlreadyDialog(getmAnchorId());
                    }else{
                        presenter.starUser(LocalDataManager.getInstance().getLoginInfo().getToken(), mSummary.getId(), mSummary.getCurroomnum());
                    }
//                    if (toptabstart.getText().equals(getResources().getString(R.string.room_top_tab_start))) {

//                    } else if (toptabstart.getText().equals(getResources().getString(R.string.room_top_tab_unstart))) {
//                        presenter.unStarUser(LocalDataManager.getInstance().getLoginInfo().getToken(), mSummary.getId(), mSummary.getCurroomnum());
//                    }
                }
            });
        }
        //显示主播信息
        SimpleDraweeView draweeAnchor = $(view, R.id.img_user_avatar);
        draweeAnchor.setImageURI(SourceFactory.wrapPathToUri(mSummary.getAvatar()));
        if (mSummary.getIs_attention() == 1) {
            toptabstart.setVisibility(View.GONE);
        }

        getViewPagerJson(mSummary.getId(), LocalDataManager.getInstance().getLoginInfo().getToken());

        mRoomOwner = $(view, R.id.room_owner);
        RxView.clicks(mRoomOwner).throttleFirst(Const.VIEW_THROTTLE_TIME,
                TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                UserInfo info = new UserInfo();
                info.setId(mSummary.getId());
                info.setNickname(mSummary.getNickname());
                info.setAvatar(mSummary.getAvatar());
                info.setLevel(1 + "");
                info.setSnap(mSummary.getSnap());
                info.setCity(mSummary.getCity());
                showUserInfoDialog(info);
            }
        });


        RxView.clicks($(view, R.id.room_imgbtn_share))
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        shareHelper.showShareLayout(getActivity());
                    }
                });

        RxView.clicks($(view, R.id.room_imgbtn_gift))
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        llOperationBar.setVisibility(View.GONE);
//                        showAnimOut(llOperationBar);
                        mGiftLay.setVisibility(View.VISIBLE);
                        showAnimIn(mGiftLay);
                        /*recyclerPublicChat.setVisibility(View.GONE);*/
                        openGiftWindows();
                    }
                });

        mGiftView = $(view, R.id.gift);
        mChargeLay = $(view, R.id.layout_gift_btn_charge);
        mChargeTv = $(view, R.id.layout_gift_charge_tv);
        mchargeinforBtn = $(view, R.id.room_gif_gotoadd_money);
        mGiftSentBtn = $(view, R.id.iv_gift_btn_send);

        mGiftLay = $(view, R.id.layout_gift);
        mGiftLay.setVisibility(View.GONE);

        double balance = LocalDataManager.getInstance().getLoginInfo().getTotalBalance();
        mChargeTv.setText(decimalFormat.format(balance));
        mGiftSentBtn.setImageDrawable(getResources().getDrawable(R.drawable.sendgifts_2));
        presenter.loadGiftList();
        timingLogger.addSplit("this.initView");

//        得到地址
        presenter.loadPlaybackUrl(mSummary.getCurroomnum());
        //获取推流地址
        presenter.generatePushStreaming();


        surfaceFrame = $(view, R.id.room_player_frame);

        mMp4Video = $(surfaceFrame, R.id.video_view);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        lp.height = getScreenMetrics().heightPixels;
        lp.width = getScreenMetrics().widthPixels;
        //lp.width = getScreenMetrics().widthPixels  ;

        //大牛
        libPlayer = new SmartPlayerJni();
        daNiuSurfaceView = $(view, R.id.daniuplaysurfaceview);
        daNiuSurfaceView.setLayoutParams(lp);

        RxView.clicks(mchargeinforBtn)
                .throttleFirst(Const.LIVE_ROOM_HEART_THROTTLE, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                    }
                });
        mchargeinforBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(getActivity(), RechargeActivity.class);
                //startActivity(i);
                initPaymentPage();
            }
        });


        timingLogger.addSplit("init url");
        timingLogger.addSplit("startPlay");
        timingLogger.dumpToLog();

        if (mRankLay != null) {
            RxView.clicks(mRankLay)
                    .throttleFirst(1000, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            if(!OpenRank) {
                                OpenRank = true;
                                showRank();
                            }
                        }
                    });
        }


        RxView.clicks($(view, R.id.play_click_view)).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                onRootClickAction();
            }
        });

    }




    /**
     * 展示秀币排行榜
     */
    private void showRank() {
       /* if (mSummary != null) {
            //TODO 需要添加目前的秀币
            //startActivity(CurrencyActivity.createIntent(getActivity(), mSummary.getId()));
            startActivity(SimpleWebViewActivity.createIntent(getActivity(), Const.RankPageUrl+getmAnchorId(),""));
        }*/
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

    @Override
    public void showGiftList(final List<Gift> giftList) {
        mGiftView.setGiftDatas(giftList);
        mGiftView.setGiftSelectChangeListener(new GiftClickListener() {
            @Override
            public void onEmotionSelected(boolean isSelect) {
                mGiftSentBtn.setEnabled(isSelect);
                if(isSelect)
                    mGiftSentBtn.setImageDrawable(getResources().getDrawable(R.drawable.sendgifts));
                else
                    mGiftSentBtn.setImageDrawable(getResources().getDrawable(R.drawable.sendgifts_2));
            }
        });
        RxView.clicks(mGiftSentBtn)
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                //为避免连发过程中取消礼物的选中等导致的数据不一致的异常，这里先做一次Map和filter操作
                //保证最后发送的时候一定会成功。
                .map(new Func1<Void, Gift>() {
                    @Override
                    public Gift call(Void aVoid) {
                        return mGiftView.getSelectedGift();
                    }
                })
                .filter(new Func1<Gift, Boolean>() {
                    @Override
                    public Boolean call(Gift gift) {
                        //没有选中任何礼物则返回false
                        if (gift == null) {
                            return Boolean.FALSE;
                        }
                        double balance = LocalDataManager.getInstance().getLoginInfo().getTotalBalance();
                        //如果一个都买不起，则返回false并提示充值
                        if (balance < gift.getPrice()) {
                            toastShort(getString(R.string.player_live_please_recharge));
                            return Boolean.FALSE;
                        }
                        return Boolean.TRUE;
                    }
                })
                .doOnNext(new Action1<Gift>() {
                    @Override
                    public void call(Gift gift) {
                        //至少为1
                        giftComboCount = 1;
                    }
                })
                .subscribe(new Action1<Gift>() {
                    @Override
                    public void call(final Gift selectedGift) {
                        mGiftSentBtn.setVisibility(View.INVISIBLE);
//                        判断是否是红包
                        if (selectedGift.getIsred().equals("1")) {
                            //计算最大合法Combo总数
                            LoginInfo loginInfo = LocalDataManager.getInstance().getLoginInfo();
                            double balance = loginInfo.getTotalBalance();
                            int maxCombo = Integer.MAX_VALUE;
                            if (selectedGift.getPrice() != 0)
                                maxCombo = (int) (balance / selectedGift.getPrice());
                            int finalCombo = giftComboCount > maxCombo ? (int) maxCombo :
                                    giftComboCount;
                            balance -= (finalCombo * selectedGift.getPrice());
                            //直接扣除余额
                            loginInfo.setTotalBalance(balance);
                            //更新到永存
                            LocalDataManager.getInstance().saveLoginInfo(loginInfo);
                            //更新显示
                            mChargeTv.setText(String.valueOf(balance));
                            presenter.sendHongBaoGift(LocalDataManager.getInstance().getLoginInfo().getToken(), mSummary.getId(), selectedGift.getId());
                            mGiftSentBtn.setVisibility(View.VISIBLE);
                            shareHelper.hideShareLayout();
                            llOperationBar.setVisibility(View.VISIBLE);
                            mGiftLay.setVisibility(View.GONE);
                            recyclerPublicChat.setVisibility(View.VISIBLE);
                        } else if (Integer.parseInt(selectedGift.getIsred()) > 1) {
                            //计算最大合法Combo总数
                            LoginInfo loginInfo = LocalDataManager.getInstance().getLoginInfo();
                            double balance = loginInfo.getTotalBalance();
                            int maxCombo = Integer.MAX_VALUE;
                            if (selectedGift.getPrice() != 0)
                                maxCombo = (int) (balance / selectedGift.getPrice());
                            int finalCombo = giftComboCount > maxCombo ? (int) maxCombo :
                                    giftComboCount;
                            balance -= (finalCombo * selectedGift.getPrice());
                            //直接扣除余额
                            loginInfo.setTotalBalance(balance);
                            //更新到永存
                            LocalDataManager.getInstance().saveLoginInfo(loginInfo);
                            //更新显示
                            mChargeTv.setText(String.valueOf(balance));
                            presenter.sendGift(mSummary.getId(), selectedGift.getId(), finalCombo);
                            mGiftSentBtn.setVisibility(View.VISIBLE);
                            shareHelper.hideShareLayout();
                            llOperationBar.setVisibility(View.VISIBLE);
                            mGiftLay.setVisibility(View.GONE);
                            recyclerPublicChat.setVisibility(View.VISIBLE);
                        } else {
                            //计算最大合法Combo总数
                            LoginInfo loginInfo = LocalDataManager.getInstance().getLoginInfo();
                            double balance = loginInfo.getTotalBalance();
                            int maxCombo = Integer.MAX_VALUE;
                            if (selectedGift.getPrice() != 0)
                                maxCombo = (int) (balance / selectedGift.getPrice());
                            int finalCombo = giftComboCount > maxCombo ? (int) maxCombo :
                                    giftComboCount;
                            balance -= (finalCombo * selectedGift.getPrice());
                            //直接扣除余额
                            loginInfo.setTotalBalance(balance);
                            //更新到永存
                            LocalDataManager.getInstance().saveLoginInfo(loginInfo);
                            //更新显示
                            mChargeTv.setText(String.valueOf(balance));
                            presenter.sendGift(mSummary.getId(), selectedGift.getId(), finalCombo);
                            mGiftSentBtn.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    //    play地址的回调
    @Override
    public void onPlaybackReady(String playbackUrl) {
        this.playbackUrl = playbackUrl;
        Log.i("RayTest","onPlaybackReady:"+playbackUrl);
//        是否是mp4
        if (ismp4(playbackUrl)) {
            initMp4Video(playbackUrl);
        } else {
            if (backplayurl != null && isBackPlay) {
                this.playbackUrl = backplayurl;
            }
            mMp4Video.setVisibility(View.GONE);
            startDaNiuLive();
        }
        shareHelper.setUserPlayUrl("http://www.inlive.tw/");
    }

    @Override
    public void getStartCode(int code) {
        if (code == 0) {
//            toastShort(getString(R.string.follow_user_compelet));
            toptabstart.setVisibility(View.GONE);
        }
    }

    @Override
    public void getRemoveStartCode(int code) {
        if (code == 0) {
//            toastShort(getString(R.string.unfollow_user_compelet));
        }
    }

    //    得到主播信息的
    @Override
    public void showUserInfo(UserInfo userInfo) {
        String AnchorInfo = userInfo.getApproveid();
        if(isUpdateCoin){
            isUpdateCoin = false;

            mChargeTv.setText(decimalFormat.format(userInfo.getCoinBalance()));
            LocalDataManager.getInstance().getLoginInfo().setTotalBalance(userInfo.getCoinBalance());
        }else{
            if (userInfo.getId().equals(getWsUserId()) &&userInfo.getBroadcasting().equals("n") && !isBackPlay) {
                isgetTopContributeUsers = true;
                isloginout = true;
                Log.i("RayTest","showRoomEndInfoDialog4");
                ((RoomActivity) getActivity()).showRoomEndInfoDialog();
            }
            if (userInfo.getIsAttention() == 1) {
                toptabstart.setVisibility(View.GONE);
            } else {
                toptabstart.setVisibility(View.VISIBLE);
            }

            tvUserCountType.setText(userInfo.getNickname());
            setApproveidIcon(ivApproveId,AnchorInfo);
            setCrownIcon(AnchorInfo);
        }


    }


    @Override
    public void onMyPushReady(String address) {
        myPushAddress = address;
    }

    @Override
    protected void onRootClickAction() {
        super.onRootClickAction();
        shareHelper.hideShareLayout();
        llOperationBar.setVisibility(View.VISIBLE);
        View view = getActivity().getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if(llSys.getVisibility()==View.VISIBLE)
            llSys.setVisibility(View.GONE);
        mGiftLay.setVisibility(View.GONE);
        //recyclerPublicChat.setVisibility(View.VISIBLE);
        closeGiftWindows();
//        try{
//            mConv.resetUnreadCount();
//            dismissSoftInput();
//            JMessageClient.exitConversation();
////                    //发送保存为草稿事件到会话列表
//            if (mIsSingle) {
//                EventBus.getDefault().post(new Event.DraftEvent(mTargetId, mTargetAppKey,
//                        mChatInputEt.getText().toString()));
//            }
//        }catch (Exception e){
//        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mIsActivityPaused = false;
        //每次都调用详细信息接口
        if (mSummary != null) {
            presenter.loadUserInfo(mSummary.getId());
        }
        //        如果弹幕不喂空，弹幕是准备的，弹幕之前是暂停的
        /*if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused()) {
            mDanmakuView.resume();
        }*/
        //mChargeTv.setText(String.valueOf(LocalDataManager.getInstance().getLoginInfo().getTotalBalance()));
        if (!TextUtils.isEmpty(playbackUrl)) {
            if (ismp4(playbackUrl)) {
            } else {
                startDaNiuLive();
            }
        }
    }



    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, "Run into activity destory++");
        if (playerHandle != 0) {
            Log.i("RayTest", "Run into activity destory++");
            libPlayer.SmartPlayerInit(null);
            libPlayer.SmartPlayerClose(playerHandle);
            libPlayer.SetSmartPlayerEventCallback(playerHandle,null);
            request = null;
            BeautyLiveApplication.getRequestQueue().cancelAll();
            ViewPagerOnResponse= null;
            playerHandle = 0;
        }
        if(hud!=null) {
            hud.cancel();
            hud.dismiss();
            hud=null;
        }
        super.onDestroy();
       /* if (mDanmakuView != null) {
            // dont forget release!
            mDanmakuView.release();
            mDanmakuView = null;
        }*/
        if (mMp4Video != null && ismp4(playbackUrl)) {
            mMp4Video.stop();
            mMp4Video.release();
            mMp4Video = null;
        }
        shareHelper = null;
        unClickListener();
        presenter.unsubscribeTasks();
    }

    private void unClickListener() {
        toptabstart.setOnClickListener(null);
    }


    @Override
    public void onPause() {
        mIsActivityPaused = true;

        //弹幕清楚掉
        /*if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.clear();
        }*/
        //播放器挺吊
        if (ismp4(playbackUrl)) {
        } else {
            //播放器暫停
        }
        libPlayer.SmartPlayerClose(playerHandle);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (mMp4Video != null && ismp4(playbackUrl)) {
            mMp4Video.stop();
            mMp4Video.release();
            mMp4Video = null;
        }
        super.onDestroyView();
        timingLogger.reset(TIMING_LOG_TAG, "PlayerFragment#onDestroyView");
        shareHelper.unsubscribeAll();
        presenter.unsubscribeTasks();
        L.i(LOG_TAG, "Run into activity destroy++");

        timingLogger.addSplit("unSubscribe tasks");
        timingLogger.addSplit("close native player");
        timingLogger.dumpToLog();
    }

    @Override
    protected void setupLiveContent(String liveStatus, String liveMsg) {

    }

    @Override
    public void finishRoom(int roomType) {

    }

    @Override
    protected int getRoomType() {
        return RoomActivity.TYPE_VIEW_LIVE;
    }

    //    这里得到传过来的参数
    @Override
    protected void parseArguments(Bundle bundle) {
        isBackPlay = bundle.getBoolean(ARG_ANCHOR_ISBACK);
        roomidmsg = bundle.getString(ARG_ANCHOR_PLAYURL);
        geturldata(roomidmsg);
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

    //  试图分解 得到的数据源里面的url地址和start时间
    public void geturldata(String roomidmsg) {
        Log.i("RayTest","geturldata:"+roomidmsg);
        String[] list = roomidmsg.split("_");
        if (list.length >= 2) {
            this.roomidmsg = list[0];
            backstarttime = list[1];
            backplayurl = list[2];
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
        if (!hasSendHeartRequest) {
            //第一次的时候直接设置为true
            hasSendHeartRequest = true;
            return true;
        }
        return false;
    }

    //刷新余额
    @Override
    protected void updateBalance(double coinbalance) {
        LoginInfo loginInfo = LocalDataManager.getInstance().getLoginInfo();
        loginInfo.setTotalBalance(coinbalance);
        LocalDataManager.getInstance().saveLoginInfo(loginInfo);
        mChargeTv.setText(decimalFormat.format(coinbalance));
    }


    @Override
    protected void setMute(boolean closeMute) {
//        mRTCStreamingManager.mute(closeMute);
    }

    @Override
    protected void sendDanmu(String roomid, String content) {
        presenter.sendFlyDanMuMsg(roomid, content);
    }

    @Override
    protected void stopPublishLive() {
        //什麼也不做 我這裡沒涉及好
    }

    @Override
    public void showInputLayout(boolean show) {
        super.showInputLayout(show);
        Log.i("RayTest",getClass().getSimpleName().toString()+" showInputLayout:"+show );
        if(show && mGiftLay.getVisibility()==View.VISIBLE) {
            //mGiftSentBtn.setVisibility(View.VISIBLE);
            shareHelper.hideShareLayout();
            //llOperationBar.setVisibility(View.VISIBLE);
            mGiftLay.setVisibility(View.GONE);
            recyclerPublicChat.setVisibility(View.VISIBLE);

        }
    }

    private boolean isLiveStreaming(String url) {
        if (url.startsWith("rtmp://")
                || (url.startsWith("http://") && url.endsWith(".m3u8"))
                || (url.startsWith("http://") && url.endsWith(".flv"))) {
            return true;
        }
        return false;
    }

    public boolean ismp4(String url) {
        if (url != null) {
            String suffix = url.substring(url.lastIndexOf(".") + 1);
            if (suffix.equals("mp4")) {
                return true;
            }
        }
        return false;
    }

    private void initMp4Video(String url) {
        try {
            mMp4Video.setDataSource(url);
            mMp4Video.setVolume(100, 100);
            mMp4Video.setScalableType(ScalableType.FIT_XY);
            mMp4Video.setLooping(true);

            mMp4Video.prepareAsync(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });

            mMp4Video.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return false;
                }
            });
            mMp4Video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                }
            });

            mMp4Video.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    switch (what) {
                        case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                            break;
                        case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                            break;
                        case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                            break;
                        case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                            break;
                    }
                    return false;
                }
            });
        } catch (IOException ioe) {
        }
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }


    //网络
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void upDataLoginBalance(String coinbalance) {
        LoginInfo loginInfo = LocalDataManager.getInstance().getLoginInfo();
        loginInfo.setTotalBalance(Double.valueOf(coinbalance));
        //更新到永存

        LocalDataManager.getInstance().saveLoginInfo(loginInfo);
        UpdateCoinBlance(coinbalance);
        Log.i("RayTest","coinbalance:"+coinbalance);
        mChargeTv.setText(coinbalance);
    }


    /* Create rendering */
    private boolean CreateView() {

        if (daNiuSurfaceView == null) {
             /*
             *  useOpenGLES2:
             *  If with true: Check if system supports openGLES, if supported, it will choose openGLES.
             *  If with false: it will set with default surfaceView;
             */
            daNiuSurfaceView = NTRenderer.CreateRenderer(getActivity(), true);
        }

        if (daNiuSurfaceView == null) {
            Log.i(LOG_TAG, "Create render failed..");
            return false;
        }

        return true;
    }
    public void startDaNiuLive() {
        playerHandle = libPlayer.SmartPlayerInit(getActivity());
        if (playerHandle == 0) {
            toastShort("初始化數據失敗");
            return;
        }
        libPlayer.SetSmartPlayerEventCallback(playerHandle, new EventHande());
        libPlayer.SmartPlayerSetSurface(playerHandle, daNiuSurfaceView);
        libPlayer.SmartPlayerSetAudioOutputType(playerHandle, 0);
        libPlayer.SmartPlayerSetBuffer(playerHandle, 2000); // 原本200;
        libPlayer.SmartPlayerSetMute(playerHandle, 0);
        int hwChecking = libPlayer.SetSmartPlayerVideoHWDecoder(playerHandle, 0);
        if (playbackUrl == null) {
            Log.e(LOG_TAG, "playback URL with NULL...");
            return;
        }
        Log.i("RayTest","playbackUrl:"+playbackUrl);
        int iPlaybackRet = libPlayer.SmartPlayerStartPlayback(playerHandle, playbackUrl);
        if (iPlaybackRet != 0) {
            Log.e(LOG_TAG, "StartPlayback strem failed..");
            return;
        }
        String pingVal = executeCmd(playbackUrl,false);
        String hostName = getHostName(playbackUrl);
        String speed = getSpeed(hostName,pingVal);
        SendReportComplete(speed);
        Log.i("RayTest","ping value: "+pingVal);
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    private class EventHande implements SmartEventCallback {
        @Override
        public void onCallback(int code, long param1, long param2, String param3, String param4, Object param5) {
            switch (code) {
                case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_STARTED:
                    Log.i(LOG_TAG, "开始。。");
                    Log.i("RayTest","SmartEventCallback: " +
                            " param1:"+param1+
                            " param2:"+param2+
                            " param3:"+param3+
                            " param4:"+param4
                    );
                    RoomInfoTmp.RtmpEventLog = "StartPublish";
                    break;
                case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_CONNECTING:
                    Log.i(LOG_TAG, "连接中。。");

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //toastShort("連接失敗了，請重試一下呢");
                            //showLoadingDialog();

                        }
                    });
                    break;
                case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_CONNECTION_FAILED:
                    Log.i(LOG_TAG, "连接失败。。");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //toastShort("連接失敗了，請重試一下呢");
                            RoomInfoTmp.RtmpEventLog = "ConnectionFailed";
                        }
                    });
                    break;
                case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_CONNECTED:
                    Log.i(LOG_TAG, "连接成功。。");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //toastShort("連接失敗了，請重試一下呢");
                            //dismissLoadingDialog();
                            RoomInfoTmp.RtmpEventLog = "ConnectionSucceeded";
                        }
                    });
                    break;
                case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_DISCONNECTED:
                    Log.i(LOG_TAG, "连接断开。。");
                    Log.i("RayTest", "连接断开。。");
                    if (isloginout) {
                        if (playerHandle != 0) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    libPlayer.SmartPlayerClose(playerHandle);
                                    playerHandle = 0;
                                    RoomInfoTmp.RtmpEventLog = "Disconnect";
                                }
                            });
                        }
                    }
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (playerHandle!=0) {
//                                toastShort("主播已經退出了");
//                                ((RoomActivity) getActivity()).showRoomEndInfoDialog();
//                            }
//                        }
//                    });
                    break;
                case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_STOP:
                    Log.i(LOG_TAG, "关闭。。");
                    break;
                case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_RESOLUTION_INFO:
                    Log.i(LOG_TAG, "分辨率信息: width: " + param1 + ", height: " + param2);
                    break;
                case EVENTID.EVENT_DANIULIVE_ERC_PLAYER_NO_MEDIADATA_RECEIVED:
                    Log.i(LOG_TAG, "收不到媒体数据，可能是url错误。。");
                    showLineErrorDialog();
            }
        }
    }

    private void showLineErrorDialog() {

    }

    private void initPaymentPage() {

        String userid = LocalDataManager.getInstance().getLoginInfo().getUserId();

        hud = showLoadingDialog();
        hud.setCancelable(false);

        PaymentIAB.getInstance().initialize(getActivity(), userid, "http://api2.inlive.tw/", getResources().getString(R.string.googleDevKey), new PaymentIAB.BillingCompletion()
        {
            @Override
            public void onResult(PaymentIAB.BillingResult billingResult)
            {
                // 後續處理...
                //

                if(billingResult== PaymentIAB.BillingResult.GooglePlayServiceNotAvailable) {
                    toastShort("儲值前，請先在裝置上新增Google帳戶，謝謝！");
                    if(hud!=null && hud.isShowing())
                        hud.dismiss();
                }else if(billingResult== PaymentIAB.BillingResult.Success)
                    openPaymentPage((int) LocalDataManager.getInstance().getLoginInfo().getTotalBalance());
                else{
                    toastShort("未知錯誤！"+billingResult.toString());
                    if(hud!=null && hud.isShowing())
                    hud.dismiss();
                }
            }
        });
    }

    private void openPaymentPage(int coinBalance) {
        PaymentIAB.BillingCompletion billingCompletion = new PaymentIAB.BillingCompletion() {
            @Override
            public void onResult(PaymentIAB.BillingResult billingResult) {
                Log.i("RayTest","openPaymentPage onResult:"+billingResult.toString());
            }
        };
        if(hud!=null)
            dismissLoadingDialog();
        if(!Const.IsPayMode)
            PaymentIAB.getInstance().openProductsView(coinBalance,billingCompletion);
        else
            PaymentIAB.getInstance().openWebView(billingCompletion);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== MainActivity.ACTIVITY_REQUEST_CODE){

            PaymentIAB.getInstance().onActivityResult(requestCode, resultCode, data);
        }
        if(requestCode == MainActivity.ACTIVITY_PAYMENT_SUCCESS_CODE) {

            hud = showLoadingDialog();
            hud.setCancelable(false);
            PaymentIAB.getInstance().initialize(getActivity(), LocalDataManager.getInstance().getLoginInfo().getUserId(), "https://api2.inlive.tw/", getResources().getString(R.string.googleDevKey), new PaymentIAB.BillingCompletion() {
                @Override
                public void onResult(PaymentIAB.BillingResult billingResult) {

                    isUpdateCoin = true;
                    presenter.loadUserInfo(LocalDataManager.getInstance().getLoginInfo().getUserId());
                    if(hud!=null)
                        dismissLoadingDialog();
                }
            });
        }
    }

}

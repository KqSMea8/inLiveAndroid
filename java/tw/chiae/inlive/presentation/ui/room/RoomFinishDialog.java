package tw.chiae.inlive.presentation.ui.room;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.room.LiveRoomEndInfo;
import tw.chiae.inlive.domain.AnchorManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;
import tw.chiae.inlive.presentation.ui.room.create.CreateRoomShareHelper;
import tw.chiae.inlive.util.Spans;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 直播间关闭时的显示页面，提供直播间信息的显示、秀币和人数的显示，及关注主播的功能。
 * Created by huanzhang on 2016/5/5.
 */
public class RoomFinishDialog extends Dialog implements View.OnClickListener {

    private final long mHotPointVal;
    private final long mCoinValue;
    private RoomActivity activity;
    private AnchorManager am;
    //    房间号
    private String mRoomId;
    //    直播者的nicename
    private String mNickname;
    //    直播者头像
    private String mAvatar;
    //   平台
    private Platform mPlatformQQ;
    private Platform mPlatformQzone;
    private Platform mPlatformWeChat;
    private Platform mPlatformWechatCircle;
    private Platform mPlatformWeibo;
    private Subscription subscription;

    //    分享按钮
    private ImageButton imgbtnWeibo, imgbtnWechatCircle, imgbtnWeChat, imgbtnQQ, imgbtnQzone;
    // 这里直接借了 创建房间的分享
    private CreateRoomShareHelper shareHelper;
    @RoomActivity.RoomType
    private int mRoomType;

    private TextView mSeePeople, mCoin,mCharm;
    private Button mStar, mBack;
    private FinishDialogListener mListener;
    private LinearLayout getliner,charmliner;
    //private RequestQueue requestQueue;

    public RoomFinishDialog(RoomActivity context, String roomId, int roomType, long coinValue, long hotPointVal) {
        super(context, R.style.DialogStyle);
        this.activity = context;
        this.mRoomId = roomId;
        this.mRoomType = roomType;
        this.am = new AnchorManager();
        this.mCoinValue  = coinValue;
        this.mHotPointVal  = hotPointVal;

        Log.i("RayTest","mCoinValue: "+mCoinValue + " hotpoint:"+hotPointVal);
    }

    public void setListener(FinishDialogListener listener) {
        mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_room_publish_finish);
        //requestQueue = webRequestUtil.getVolleyIntence(activity);
        findView();
        initView();

    }

    private void findView() {
        mSeePeople = (TextView) findViewById(R.id.dialog_room_finish_users_num);
        mCharm = (TextView) findViewById(R.id.dialog_room_finish_my_charm);
        mCoin = (TextView) findViewById(R.id.dialog_room_finish_my_gain);
        mStar = (Button) findViewById(R.id.dialog_room_finish_btn_follow);
        mBack = (Button) findViewById(R.id.dialog_room_finish_btn_finish);
        getliner = (LinearLayout) findViewById(R.id.finish_get);
        charmliner = (LinearLayout) findViewById(R.id.finish_get_charm);

//        这里根据room的Type来选择隐藏 该finishdialog的收货
        if (mRoomType == RoomActivity.TYPE_VIEW_LIVE) {
            getliner.setVisibility(View.INVISIBLE);
        }
//        分享
        imgbtnQQ = (ImageButton) findViewById(R.id.room_finish_imgbtn_share_qq);
        imgbtnQQ.setOnClickListener(this);
        imgbtnQzone = (ImageButton) findViewById(R.id.room_finish_imgbtn_share_qzone);
        imgbtnQzone.setOnClickListener(this);
        imgbtnWeChat = (ImageButton) findViewById(R.id.room_finish_imgbtn_share_wechat);
        imgbtnWeChat.setOnClickListener(this);
        imgbtnWechatCircle = (ImageButton) findViewById(R.id.room_finish_imgbtn_share_wechat_circle);
        imgbtnWechatCircle.setOnClickListener(this);
        imgbtnWeibo = (ImageButton) findViewById(R.id.room_finish_imgbtn_share_weibo);
        imgbtnWeibo.setOnClickListener(this);


    }

    private void initView() {
        setCancelable(false);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (subscription != null) {
                    subscription.unsubscribe();
                }
            }
        });

        mBack.setOnClickListener(this);
        //主播不能关注自己
        if (mRoomType == RoomActivity.TYPE_PUBLISH_LIVE) {
            mStar.setVisibility(View.GONE);
        } else {
            mStar.setOnClickListener(this);
        }

        queryRoomInfo(mCoinValue,mHotPointVal);
    }

    @Override
    public void onClick(View v) {
        if (mListener == null) {
            return;
        }
      /* if (v == imgbtnWeChat) {
            if (mPlatformWeChat == null) {
                mPlatformWeChat = new Wechat(activity);
            }
            checkAndStartPublish(2, mPlatformWeChat);
        } *//*else if (v == imgbtnWechatCircle) {
            if (mPlatformWechatCircle == null) {
                mPlatformWechatCircle = new WechatMoments(activity);
            }
            checkAndStartPublish(3, mPlatformWechatCircle);
        }*/
        if (v == mBack) {
            dismiss();
            mListener.onFinish();
        } else if (v == mStar) {
            mListener.onClickFollow();
        }
    }

    @Override
    public void onBackPressed() {
        if (mListener == null) {
            super.onBackPressed();
        } else {
            mListener.onFinish();
        }
    }

    private void queryRoomInfo(final long CoinValue , final long HotValue) {
        subscription = am.getLiveRoomEndInfo(mRoomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<LiveRoomEndInfo>>(activity) {
                    @Override
                    public void onSuccess(BaseResponse<LiveRoomEndInfo> response) {
                        LiveRoomEndInfo info = response.getData();
                        mNickname = info.getNickname();
                        mAvatar = info.getAvatar();
                       /* mSeePeople.setText(Spans.createSpan("", info.getAudienceCount(),
                                "",
                                new ForegroundColorSpan(ContextCompat.getColor(getContext(), R
                                        .color.yunkacolor))
                        ));*/
                       if(mRoomType == RoomActivity.TYPE_VIEW_LIVE){
                           mSeePeople.setText(getContext().getString(R.string.finish_dialog_thanks));
                           mSeePeople.setTextColor(getContext().getResources().getColor(R.color.yunkacolor_bule));
                           getliner.setVisibility(View.GONE);
                           charmliner.setVisibility(View.GONE);
                       }else {
                           getliner.setVisibility(View.VISIBLE);
                           charmliner.setVisibility(View.VISIBLE);
                       }
                        //仅主播可见收获多少秀币
                        if (mRoomType == RoomActivity.TYPE_PUBLISH_LIVE) {
                            //因为是String字段解析的，所以可能为null

                            mSeePeople.setVisibility(View.GONE);

                            String coinStr = ""+CoinValue;
                            String CharmStr = ""+HotValue;
                            //为null或空时，使用0来代替
                            if (TextUtils.isEmpty(coinStr)) {
                                coinStr = "0";
                            }
                            if (TextUtils.isEmpty(CharmStr)) {
                                CharmStr = "0";
                            }
                            mCharm.setText(
                                    Spans.createSpan("",
//                                            info.getCoinIncome() ,
                                            CharmStr,
                                            "",
                                            new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.yunkacolor_bule))
                                    ));
                            Log.i("RayTest","set coinStr:"+coinStr);
                            mCoin.setText(
                                    Spans.createSpan("",
//                                            info.getCoinIncome() ,
                                            coinStr,
                                            "",
                                            new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.yunkacolor_bule))
                                    ));
                        }
                        shareHelper = new CreateRoomShareHelper(activity, mRoomId, mNickname, mAvatar, new PlatformActionListener() {
                            @Override
                            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

                            }

                            @Override
                            public void onError(Platform platform, int i, Throwable throwable) {

                            }

                            @Override
                            public void onCancel(Platform platform, int i) {

                            }
                        });
                    }
                });
    }

    public interface FinishDialogListener {
        void onFinish();

        void onClickFollow();
    }

  /*  private void checkAndStartPublish(int sharindex, Platform platform) {
//            有开启某个平台的分享
        if (sharindex == 2) {
            String shareTitleUrl = activity.getString(R.string.share_room_url, Const.MAIN_HOST_TEST, mRoomId);
            String shareText = activity.getString(R.string.share_room_text, mNickname);
            Wechat.ShareParams params = new Wechat.ShareParams();
            params.setTitleUrl(shareTitleUrl);
            params.setTitle(activity.getString(R.string.share_titles,mNickname));
            params.setText(shareText);
            params.setImageUrl(SourceFactory.wrapPath(mAvatar));
            params.setShareType(Platform.SHARE_WEBPAGE);
            params.setUrl(shareTitleUrl);
            Platform qzone = ShareSDK.getPlatform(Wechat.NAME);
            qzone.setPlatformActionListener(new ShareHelper.DefaultShareListener() {
                @Override
                public void onCancel(Platform platform, int i) {
                    super.onCancel(platform, i);
                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {
                    super.onError(platform, i, throwable);
                }

                @Override
                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    super.onComplete(platform, i, hashMap);
                }
            }); // 设置分享事件回调
            qzone.share(params);
        }
        if (sharindex != -1 && shareHelper != null) {
            shareHelper.share(platform);
        }
    }*/



}


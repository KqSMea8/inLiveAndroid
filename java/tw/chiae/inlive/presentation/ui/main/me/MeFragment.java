package tw.chiae.inlive.presentation.ui.main.me;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/*import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;*/
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.google.gson.Gson;
import com.jakewharton.rxbinding.view.RxView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import rx.functions.Action1;
import tw.chiae.inlive.BeautyLiveApplication;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.EventSummary;
import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.data.bean.me.PlayBackInfo;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.domain.BlackList;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.base.BaseFragment;
import tw.chiae.inlive.presentation.ui.base.ptr.BasePtr;
import tw.chiae.inlive.presentation.ui.chatting.ChatActivity;
import tw.chiae.inlive.presentation.ui.chatting.DemoActivity;
import tw.chiae.inlive.presentation.ui.chatting.utils.SharePreferenceManager;
import tw.chiae.inlive.presentation.ui.login.LoginSelectActivity;
import tw.chiae.inlive.presentation.ui.main.EventInterface;
import tw.chiae.inlive.presentation.ui.main.EventPresenter;
import tw.chiae.inlive.presentation.ui.main.MainActivity;
import tw.chiae.inlive.presentation.ui.main.currency.CurrencyActivity;
import tw.chiae.inlive.presentation.ui.main.index.MyLinearLayout;
import tw.chiae.inlive.presentation.ui.main.me.profile.EditProfileActivity;
import tw.chiae.inlive.presentation.ui.main.me.sublist.SubListActivity;
import tw.chiae.inlive.presentation.ui.main.me.transaction.IncomeActivity;
import tw.chiae.inlive.presentation.ui.main.me.transaction.RechargeActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.EventActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.KSWebActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.KaraStar;
import tw.chiae.inlive.presentation.ui.main.mergefilm.MediaPlayerActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.MediaRecorderActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.VCamera;
import tw.chiae.inlive.presentation.ui.main.mergefilm.model.FileUtils;
import tw.chiae.inlive.presentation.ui.main.search.SearchActivity;
import tw.chiae.inlive.presentation.ui.main.setting.BlacklistActivity;
import tw.chiae.inlive.presentation.ui.main.setting.CreateViewDialogFragment;
import tw.chiae.inlive.presentation.ui.main.setting.SettingActivity;
import tw.chiae.inlive.presentation.ui.main.webview.SimpleWebViewActivity;
import tw.chiae.inlive.util.CETracking;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.ConversionUtil;
import tw.chiae.inlive.util.EventUtil;
import tw.chiae.inlive.util.FrescoUtil;
import tw.chiae.inlive.util.L;
import tw.chiae.inlive.util.LocaleFormats;
import tw.chiae.inlive.util.PermissionsChecker;
import tw.chiae.inlive.util.PicUtil;

import tw.inlive.paymentsdk.PaymentIAB;

import static android.R.attr.path;
import static java.util.ResourceBundle.clearCache;
import static tw.chiae.inlive.BeautyLiveApplication.TARGET_ID;


/**
 * 个人中心。
 *
 * @author huanzhang
 * @since 1.0.0
 */
public class MeFragment extends BaseFragment implements IMe, EventInterface {

    private static final int CODE_EDIT_PROFILE = 0x1001;
    private static final int CODE_EDIT_AVATER = 1200;

    private UserInfo mUserInfo;
    private MyLinearLayout me_snap;


    private PtrFrameLayout mPtr;
    private ImageButton mBack, mMsg;
    private TextView mOnline;
    //                            等级      送出的秀碧
    private TextView mName, mId, mSent;
    private ImageView mlevel, mChat ,main_snap_mask,mAuthenticationClick;
    private SimpleDraweeView mPhoto;
    private ImageView mSex, mEdit, mLevelImg;
    //              收益    等级   我的钻石  设置     认证                  贡献   mv
    private View mGet, mLevel, mDiamonds, mSetting, mAuthentication, mContribution,mMvPlay;
    private TextView mplay_back_number, muser_level, muser_get, muser_money;
    private TextView mGetTv, mLevelTv, mDiamondsTv, mSettingTv, mAuthenticationTv, mContributionTv,mMvPlayTv,mBlackListTv;
    private TextView mGetTip, mLevelTip, mDiamondsTip, mSettingTip, mAuthenticationTip, mContributionTip,mMvPlayTip,mBlackListTip;
    //     关注 粉丝
    private TextView mStar, mFans;
    private TextView mRank;
    private TextView mSign;
    //    退出登录
    private TextView mLogout;
    //    2016年7月15日01:17:32 这个方法的loadUserInfo 请求参数从null 改为了 Integer.parseInt(LocalDataManager.getInstance().getLoginInfo().getUserId())
    private MePresenter mePresenter;
    //    回播item
    private LinearLayout item_playback, item_level, item_get, item_money,item_kstar;
    //    使用列表存放，方便遍历
    private List<SimpleDraweeView> topContributeDrawees;

    private final UIHandler mUIHandler = new UIHandler(this);
    private static final int UNREAD = 0x99999;
    private static final int READ = 0x99998;
    private TextView newMsg;
    private List<Conversation> mDatas = new ArrayList<Conversation>();

    private Bitmap bitmap;
    private final static String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/mypic_data/";
    private Handler handler = new Handler();
    private SimpleDraweeView main_snap;
    private Dialog hud;
    private boolean isUpdateCoin =false;
    private ImageView mSearch;
    private EventPresenter presenter;

    private boolean r = false;
    private boolean f = false;
    private boolean m = false;
    private List<String> newDefaultlist;
    private FrameLayout mMsgMainLayout;
    private View mBlackList;
    private String SnapUrl= "";
    private String ProfilePath;
    private PermissionsChecker mPermissionsChecker;
    private CreateViewDialogFragment dialogFragment;

    //private List<String> Officiallist;
    //private UserInfo OfficialInfo;
    //private String ServiceID ="user1000518";

    public static MeFragment newInstance() {
        return new MeFragment();
    }

    @Override
    protected int getLayoutId() {

        return R.layout.frag_main_me;
    }

    @Override
    public void onResume() {
//        MobclickAgent.onPageStart("个人中心");

        super.onResume();
//        mPtr.autoRefresh();

        if(LocalDataManager.getInstance().isOfficialAccount(LocalDataManager.getInstance().getLoginInfo().getUserId())) {
            mMsgMainLayout.setVisibility(View.VISIBLE);
            mChat.setVisibility(View.VISIBLE);
            try {
                mDatas = JMessageClient.getConversationList();

                if(mDatas.size()==0)
                    newMsg.setVisibility(View.GONE);
                for (int i = 0; i < mDatas.size(); i++) {
                    Conversation conv = mDatas.get(i);

                    if (conv.getUnReadMsgCnt() > 0) {
                        newMsg.setVisibility(View.VISIBLE);
                        break;
                    } else
                        newMsg.setVisibility(View.GONE);
                }
            } catch (NullPointerException e) {
            }
            //newMsg.setVisibility(View.VISIBLE);
        }else {
            mMsgMainLayout.setVisibility(View.INVISIBLE);
            mChat.setVisibility(View.INVISIBLE);
            newMsg.setVisibility(View.INVISIBLE);
        }


        if(mePresenter!=null)
            mePresenter.loadUserInfo(Integer.parseInt(LocalDataManager.getInstance().getLoginInfo().getUserId()));
        BaseActivity mActivity = (BaseActivity) getActivity();
        //mActivity.setTaskBarColored(R.color.transparent);
        //getView().setFitsSystemWindows(false);
    }

    @Override
    public void onPause() {
//        MobclickAgent.onPageEnd("个人中心");
        super.onPause();

    }

    @Override
    protected void initViews(View view) {

        newMsg = $(view, R.id.new_msg);
        mMsgMainLayout = (FrameLayout) view.findViewById(R.id.fl_msg);
        mBack = (ImageButton) view.findViewById(R.id.imgbtn_toolbar_back);
        mOnline = (TextView) view.findViewById(R.id.me_massage);
        mPtr = (PtrFrameLayout) view.findViewById(R.id.fragment_me_ptr);
        mPhoto = $(view, R.id.my_me_photo);
        mChat = $(view, R.id.me_chat);
        main_snap = $(view, R.id.layout_main_info_bg);
        main_snap_mask = $(view, R.id.layout_main_info_bg_color);
        me_snap = $(view, R.id.me_snap);
        mName = $(view, R.id.me_name);
        mlevel = $(view, R.id.me_level);
        mSent = $(view, R.id.me_send);
        mSex = $(view, R.id.me_sex);
        mEdit = $(view, R.id.me_edit);
        mId = $(view, R.id.me_id);
        BasePtr.setPagedPtrStyle(mPtr);
        mSign = $(view, R.id.me_sign);
        mPtr.setPtrHandler(new PtrDefaultHandler() {

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mePresenter.loadUserInfo(Integer.parseInt(LocalDataManager.getInstance().getLoginInfo().getUserId()));
            }
        });
        String[] accountLists = Const.OfficialAccountListID;
        /*Officiallist = Arrays.asList(accountLists);
        presenter.loadOfficialInfo(accountLists[0]);*/
        topContributeDrawees = new ArrayList<>();
        SimpleDraweeView draweeTop1 = $(view, R.id.me_coin_top1);
        SimpleDraweeView draweeTop2 = $(view, R.id.me_coin_top2);
        SimpleDraweeView draweeTop3 = $(view, R.id.me_coin_top3);
        topContributeDrawees.add(draweeTop1);
        topContributeDrawees.add(draweeTop2);
        topContributeDrawees.add(draweeTop3);

        mGet = $(view, R.id.fragment_me_item_myget);
        mGet.setVisibility(View.GONE);
        mLevel = $(view, R.id.fragment_me_item_level);
        mLevel.setVisibility(View.GONE);
        mDiamonds = $(view, R.id.fragment_me_item_diamonds);
        mDiamonds.setVisibility(View.GONE);
        mSetting = $(view, R.id.fragment_me_item_setting);
        mAuthentication = $(view, R.id.fragment_me_item_authentication);
        mContribution = $(view, R.id.fragment_me_item_contribution);
        mMvPlay=$(view, R.id.fragment_me_item_mvplay);
        mBlackList=$(view, R.id.fragment_me_item_blacklist);
        mGetTv = $(mGet, R.id.item_me_txt);
        mLevelTv = $(mLevel, R.id.item_me_txt);
        mDiamondsTv = $(mDiamonds, R.id.item_me_txt);
        mSettingTv = $(mSetting, R.id.item_me_txt);
        mAuthenticationTv = $(mAuthentication, R.id.item_me_txt);
        mAuthenticationClick =  $(mAuthentication,R.id.item_me_tip_click);
        mContributionTv = $(mContribution, R.id.item_me_txt);
        mMvPlayTv=$(mMvPlay, R.id.item_me_txt);

        mBlackListTip =$(mBlackList, R.id.item_me_tip);
        mBlackListTv = $(mBlackList, R.id.item_me_txt);
        mGetTip = $(mGet, R.id.item_me_tip);
        mLevelTip = $(mLevel, R.id.item_me_tip);
        mDiamondsTip = $(mDiamonds, R.id.item_me_tip);
        mSettingTip = $(mSetting, R.id.item_me_tip);
        mAuthenticationTip = $(mAuthentication, R.id.item_me_tip);
        mContributionTip = $(mContribution, R.id.item_me_tip);
        mMvPlayTip = $(mMvPlay, R.id.item_me_tip);

        muser_level = $(view, R.id.user_level);
        mplay_back_number = $(view, R.id.play_back_number);
        muser_get = $(view, R.id.user_get);
        muser_money = $(view, R.id.user_money);

        mStar = $(view, R.id.me_user_info_star);
        mFans = $(view, R.id.me_user_info_fans);

        mRank = $(view, R.id.me_coin_rank_tv);

        mLevelImg = $(mLevel, R.id.item_me_img);

        mLogout = $(view, R.id.fragment_me_logout);
        item_playback = $(view, R.id.item_playback);
        item_level = $(view, R.id.item_level);
        item_get = $(view, R.id.item_get);
        item_kstar = $(view, R.id.item_Kstar);
        item_money = $(view, R.id.item_money);

        mSearch = $(view, R.id.sel_me_btn_serach);
        mSearch.setVisibility(View.GONE);
        initData();

////        设置
//        RxView.clicks(mSetting).throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
//                .subscribe(new Action1<Void>() {
//                    @Override
//                    public void call(Void aVoid) {
//                        startActivity(SettingActivity.createIntent(getActivity(),));
//                    }
//                });
        // 关注列表页
        RxView.clicks(mStar).throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (mUserInfo != null) {
                            startActivity(SubListActivity.createIntent(getActivity(), mUserInfo.getId(), SubListActivity.KEY_STAR));
                        }
                    }
                });
        //粉丝列表
        RxView.clicks(mFans).throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (mUserInfo != null) {
                            startActivity(SubListActivity.createIntent(getActivity(), mUserInfo.getId(), SubListActivity.KEY_FANS));
                        }
                    }
                });
        RxView.clicks($(view, R.id.me_user_info_fans_tv)).throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (mUserInfo != null) {
                            startActivity(SubListActivity.createIntent(getActivity(), mUserInfo.getId(), SubListActivity.KEY_FANS));
                        }
                    }
                });
        RxView.clicks($(view, R.id.me_user_info_star_tv)).throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (mUserInfo != null) {
                            startActivity(SubListActivity.createIntent(getActivity(), mUserInfo.getId(), SubListActivity.KEY_STAR));
                        }
                    }
                });

//        老板的贡献榜
        RxView.clicks(mRank).throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (mUserInfo != null) {
                            //startActivity(CurrencyActivity.createIntent(getActivity(), mUserInfo.getId()));
                            CheckRankEvent();
                        }
                    }
                });
//        回播记录
     /*   RxView.clicks(item_playback).throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (mUserInfo != null) {
                            //startActivity(PlayBackListActivity.createIntent(getActivity(), mUserInfo));
                            Log.i("RayTest","Relaod");

                        }
                    }
                });*/
//                等级
        RxView.clicks(item_level).throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (mUserInfo != null) {
                            startActivity(SimpleWebViewActivity.createIntent(getActivity(), SourceFactory.wrapPath(getString(R.string.me_level_description_url, mUserInfo.getId())),""));
                        }
                    }
                });
//         K歌
        RxView.clicks(item_kstar).throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (mUserInfo != null) {
                            //startKsEvent();
                           // startIncomePage(mUserInfo);
                        }
                    }
                });


//        收益
        RxView.clicks(item_get).throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (mUserInfo != null) {
                    Log.i("RayTest","go to 收益頁");
                            startIncomePage(mUserInfo);
                        }
                    }
                });

//        账户
        RxView.clicks(item_money).throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (mUserInfo != null) {
                            /*startActivity(RechargeActivity.createIntent(getActivity()));*/
                            initPaymentPage(mUserInfo);
                        }
                    }
                });
//    左上角的搜索

        RxView.clicks($(view, R.id.sel_me_btn_serach)).throttleFirst(Const
                .VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        startActivity(SearchActivity.createIntent(getActivity()));
                        getActivity().overridePendingTransition(R.anim.fragment_slide_right_in, R
                                .anim.fragment_slide_right_out);
                    }
                });
//      修改用户信息
        subscribeClick(mEdit, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (mUserInfo != null) {
                    startActivityForResult(EditProfileActivity.createIntent(getActivity(),
                            mUserInfo), CODE_EDIT_PROFILE);
                }
            }
        });
//       充值
        subscribeClick(mDiamonds, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(RechargeActivity.createIntent(getActivity()));
            }
        });

        subscribeClick(mGet, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (mUserInfo != null) {
                    startActivity(IncomeActivity.createIntent(getActivity()));
                }
            }
        });

        subscribeClick(mLevel, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (mUserInfo != null) {
                    startActivity(SimpleWebViewActivity.createIntent(getActivity(),
                            SourceFactory.wrapPath(getString(R.string.me_level_description_url, mUserInfo.getLevel())),""));
                }
            }
        });

        subscribeClick(mPhoto, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (mUserInfo != null) {
                    startActivityForResult(EditProfileActivity.createIntent(getActivity(),
                            mUserInfo), CODE_EDIT_PROFILE);
                }
            }
        });
        subscribeClick(mlevel, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (mUserInfo != null) {
                    startActivityForResult(EditProfileActivity.createIntent(getActivity(),
                            mUserInfo), CODE_EDIT_PROFILE);
                }
            }
        });
        //      投票贡献榜
        subscribeClick(mContribution, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (mUserInfo != null) {
                    //startActivity(CurrencyActivity.createIntent(getActivity(), mUserInfo.getId()));
                    CheckRankEvent();
                }
            }
        });


        subscribeClick(mBlackList, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (mUserInfo != null) {
                    startActivity(BlacklistActivity.createIntent(getActivity(), mUserInfo.getId()));
                }
            }
        });


//      认证的点击事件注册放到我们的用户获取的信息里面

        //消息列表


        subscribeClick(mChat, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Log.i("RayTest","Test");
                if (mUserInfo != null) {
                    startActivity(DemoActivity.createIntent(getActivity()));
                    getActivity().overridePendingTransition(R.anim.fragment_slide_left_in, R
                            .anim.fragment_slide_left_out);
                }
            }
        });

    }

    private void CheckRankEvent() {
        CheckEventSwitch(EventUtil.RankID, new EventCheckCallback() {
            @Override
            public void eventSW(boolean sw) {
                if(sw){
                    startActivity(SimpleWebViewActivity.createIntent(getActivity(), Const.RankPageUrl+mUserInfo.getId(),""));
                }else{
                    startActivity(CurrencyActivity.createIntent(getActivity(), mUserInfo.getId()));
                }
            }
        });
    }


    private void startIncomePage(UserInfo mUserInfo) {
        String approveidStr = mUserInfo.getApproveid();
        if(approveidStr.contains("藝人")){
            startActivity(IncomeActivity.createIntent(getActivity()));
        }


    }

    private void initPaymentPage(final UserInfo mUserInfo) {
        Log.i("RayTest","id:"+mUserInfo.getId());
        String userid = LocalDataManager.getInstance().getLoginInfo().getUserId();

        hud = showLoadingDialog();
        hud.setCancelable(true);

        PaymentIAB.getInstance().initialize(getActivity(), userid, "http://api2.inlive.tw/", getResources().getString(R.string.googleDevKey), new PaymentIAB.BillingCompletion()
        {
            @Override
            public void onResult(PaymentIAB.BillingResult billingResult)
            {
                // 後續處理...
                //
                if(billingResult== PaymentIAB.BillingResult.GooglePlayServiceNotAvailable) {
                    toastShort("儲值前，請先在裝置上新增Google帳戶，謝謝！");
                    hud.dismiss();
                }else if(billingResult== PaymentIAB.BillingResult.Success)
                    openPaymentPage((int) LocalDataManager.getInstance().getLoginInfo().getTotalBalance());
                else{
                    toastShort("未知錯誤！"+billingResult.toString());
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

    private void initData() {
        mePresenter = new MePresenter(this);
        presenter = new EventPresenter(this);
        mBack.setVisibility(View.GONE);
        mGetTv.setText(getResources().getString(R.string.me_get));
        mLevelTv.setText(getResources().getString(R.string.me_level));
        mDiamondsTv.setText(getResources().getString(R.string.me_diamonds, getString(R.string.app_currency)));
        mSettingTv.setText(getResources().getString(R.string.me_setting));
        mAuthenticationTv.setText(getResources().getString(R.string.me_authentication_email));
        mAuthenticationClick.setVisibility(View.VISIBLE);
        mContributionTv.setText(getResources().getString(R.string.me_contribution));
        mMvPlayTv.setText("合唱");
        mMvPlay.setVisibility(View.GONE);
        mRank.setText(getString(R.string.coin_rank, getString(R.string.app_currency)));
        mBlackListTv.setText("查看黑名單");
        mMvPlayTip.setVisibility(View.GONE);
        mSettingTip.setVisibility(View.GONE);
        mLevelTip.setVisibility(View.GONE);
        mLevelImg.setVisibility(View.VISIBLE);
        mAuthenticationTip.setVisibility(View.GONE);
        mContributionTip.setVisibility(View.GONE);
        mMvPlayTip.setVisibility(View.GONE);
        RxView.clicks(mLogout)
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        //Move these operation into LoginSelectActivity
//                        LocalDataManager.getInstance().clearLoginInfo();
//                        //Clear all third authorize
//                        new QQ(getActivity()).removeAccount(true);
//                        new Wechat(getActivity()).removeAccount(true);
//                        new SinaWeibo(getActivity()).removeAccount(true);
                        CETracking.getInstance().onUserLogout(getActivity());
                        startActivity(LoginSelectActivity.createIntent(getActivity()));
                        ((BaseActivity) getActivity()).sendFinishBroadcast(LoginSelectActivity
                                .class.getSimpleName());
                    }
                });
    }

    //    这里是用来给wsloginrequest 设置信息的，为了不进行一次网络请求，所以这里写了个全局。。。不要问我之前那里如何写的，我也不知道
    public static UserInfo mrlinfo;

    @Override
    public void showInfo(final UserInfo info) {

        Log.i("RayTest","show info ");
        mrlinfo = info;
        if (info == null) {
            return;
        }
        mUserInfo = info;
        /*if (info.ㄕ() != null) {
            Log.i("RayTest","info.frescoResize: "+SourceFactory.wrapPathToUri(info.getSnap()));
            FrescoUtil.frescoResize(
                    SourceFactory.wrapPathToUri(info.getSnap()),
                    getWidth(this.getActivity()),
                    getWidth(this.getActivity()),
                    mPhoto
            );
        }*/
//            Const.MAIN_HOST_URL + mUserInfo.getSnap()
        new Thread(new Runnable() {

            @Override
            public void run() {
                SnapUrl = Const.MAIN_HOST_URL + mUserInfo.getSnap()+"&time="+getSystemTime();
                Log.i("RayTest","Url: "+SnapUrl);
                clearMainSnapCache(SnapUrl);
                boolean isSnapDefault = false;
                if(mUserInfo.getSnap().equals(Const.SNAP_DEFAULT_NAME)){
                    isSnapDefault = true;
                }
                /*try {

                    url = new URL(Const.MAIN_HOST_URL + mUserInfo.getSnap());
                    Log.i("RayTest","show getSnap "+Const.MAIN_HOST_URL + mUserInfo.getSnap());
                    if(mUserInfo.getSnap().equals(Const.SNAP_DEFAULT_NAME)){
                         isSnapDefault = true;
                    }
                    InputStream is = url.openStream();
                    bitmap = BitmapFactory.decodeStream(is);

                    saveFile(bitmap, "yangmi.jpg");
                    is.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                final boolean finalIsSnapDefault = isSnapDefault;
                handler.post(new Runnable() {
                    @Override
                    public void run() {


                        if (info.getSex() == 0) {
                            if(finalIsSnapDefault) {
                                main_snap.setImageResource(R.drawable.snap_default);
                            }else{
                                if(ProfilePath==null || ProfilePath.isEmpty()) {
                                    main_snap.setImageURI(SnapUrl);
                                }else{
                                    loadLocalImage(ProfilePath);
                                }
                                Log.i("RayTest","info.getSnap: "+Const.MAIN_HOST_URL +info.getAvatar());
                                mPhoto.setImageURI(Const.MAIN_HOST_URL +info.getAvatar());
                                 // main_snap_mask.setBackgroundColor(0xB4A6A3A4);
                            //me_snap.setColor(0xB4A6A3A4);
                                }
                        }
                        if (info.getSex() == 1) {
                            if(finalIsSnapDefault) {
                                main_snap.setImageResource(R.drawable.snap_default);
                            }else{
                                Log.i("RayTest","info.getSnap: "+Const.MAIN_HOST_URL +info.getAvatar());
                                mPhoto.setImageURI(Const.MAIN_HOST_URL +info.getAvatar());
                                if(ProfilePath==null ||ProfilePath.isEmpty()) {
                                    main_snap.setImageURI(SnapUrl);
                                }else{
                                    loadLocalImage(ProfilePath);
                                }
                                //main_snap_mask.setBackgroundColor(0xB59B5B7B);
                            //me_snap.setColor(0xB59B5B7B);
                            //B27B5367 B2F0C8DC
                                }
                            Log.i("Thread", "結束時間");
                        }
                    }
                });
            }
        }).start();


        //TODO add min photo

        if (info.getNickname() != null) {
            mName.setText(info.getNickname());

            //IM的nickname
            String nickName = mName.getText().toString().trim();
            if (nickName != null && !nickName.equals("")) {
                cn.jpush.im.android.api.model.UserInfo myUserInfo = JMessageClient.getMyInfo();
                try {
                    myUserInfo.setNickname(nickName);
                    JMessageClient.updateMyInfo(cn.jpush.im.android.api.model.UserInfo.Field.nickname, myUserInfo, new BasicCallback() {
                        @Override
                        public void gotResult(final int status, String desc) {
                            //更新跳转标志
                            SharePreferenceManager.setCachedFixProfileFlag(false);
                        }
                    });
                } catch (NullPointerException e) {
                }
            }
        }
        mSex.setImageResource(info.getSex() == 0 ? R.drawable.ic_male : R.drawable.ic_female);

        if (info.getSex() == 0 || info.getSex() == 1) {
            //IM的gender
            final cn.jpush.im.android.api.model.UserInfo myUserInfo = JMessageClient.getMyInfo();
            try {
                myUserInfo.setGender(info.getSex() == 0 ? cn.jpush.im.android.api.model.UserInfo.Gender.male : cn.jpush.im.android.api.model.UserInfo.Gender.female);
                JMessageClient.updateMyInfo(cn.jpush.im.android.api.model.UserInfo.Field.gender, myUserInfo, new BasicCallback() {
                    @Override
                    public void gotResult(final int status, String desc) {
                        //更新跳转标志
                        SharePreferenceManager.setCachedFixProfileFlag(false);
                    }
                });
            } catch (NullPointerException e) {
            }
        }

        if (info.getId() != null) {
            mId.setText(getResources().getString(R.string.me_id) + info.getId());
        }
        if (info.getLevel() != null) {
            mlevel.setImageResource(PicUtil.getLevelImageId(getActivity(), Integer.parseInt(info.getLevel())));

            //IM的level
            final cn.jpush.im.android.api.model.UserInfo myUserInfo = JMessageClient.getMyInfo();
            String level = info.getLevel();
            try {
                myUserInfo.setRegion(level);
                JMessageClient.updateMyInfo(cn.jpush.im.android.api.model.UserInfo.Field.region, myUserInfo, new BasicCallback() {
                    @Override
                    public void gotResult(final int status, String desc) {
                        //更新跳转标志
                        SharePreferenceManager.setCachedFixProfileFlag(false);
                    }
                });
            } catch (NullPointerException e) {
            }
        }
        if (info.getTotalContribution() != 0) {
            mSent.setText(getString(R.string.me_send_currency) + info.getTotalContribution());
//            PicUtil.TextViewSpandImg(this.getActivity(), mSent, R.drawable
//                    .ic_me_myaccount_reddiamond);
        }

        //贡献榜前三的头像
        List<String> topAvatars = info.getTopContributeUsers();
        if (topAvatars != null) {
            int avatarSize = (int) getResources().getDimension(R.dimen.avatar_size_default);
            //取可显示的头像个数和返回的头像个数的较小值执行遍历
            for (int i = 0; i < Math.min(topContributeDrawees.size(), topAvatars.size()); i++) {
                FrescoUtil.frescoResize(SourceFactory.wrapPathToUri(topAvatars.get(i)),
                        avatarSize, avatarSize, topContributeDrawees.get(i));
            }
        }

        mStar.setText(getString(R.string.my_info_follower_count, info.getFolloweesCount()));
        mFans.setText(getString(R.string.my_info_followee_count, info.getFollowersCount()));
        if (info.getIntro() != null) {
            mSign.setText(info.getIntro());
        } else {
            mSign.setText(getString(R.string.me_sign_null));
        }

        mDiamondsTip.setText(ConversionUtil.conversionNumberDoubel(info.getCoinBalance()));
        mGetTip.setText(ConversionUtil.conversionNumberDoubel(info.getEarnbean()));

        //      认证
        subscribeClick(mAuthentication, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (info.getApproveid().equals(getString(R.string.authentication_no))) {
                    //startActivity(AuthenticationActivity.createIntent(getActivity()));
                } else {
                    //toastShort(getString(R.string.authentication_complete));
                }
                if(LocalDataManager.getInstance().getOfficialList()!=null)
                    showPrivateService();

            }
        });

        //合唱
        subscribeClick(mMvPlay, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {


                //startActivity(MediaRecorderActivity.createIntent(getActivity(),"https://api2.inlive.tw/assets/events/karastar/video/stars-001.mp4","http://kalastar1.inlive.tw/songvideo/s001.lrc"));



                    //startActivity(MediaRecorderActivity.createIntent(getActivity(),"http://kalastar.inlive.tw/songvideo/s001.mp4","http://kalastar.inlive.tw/songvideo/s001.lrc"));
            }
        });

        //        设置
        RxView.clicks(mSetting).throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        startActivity(SettingActivity.createIntent(getActivity(), info.getRecommendation()));
                    }
                });

        if (info.getApproveid() != null) {


            String Approveid = info.getApproveid();
            Log.i("RayTest","getApproveid:"+Approveid);
            if(Approveid.equals("无")||Approveid.equals("無")){
                item_kstar.setVisibility(View.VISIBLE);
                item_get.setVisibility(View.GONE);
            }else{
                item_kstar.setVisibility(View.GONE);
                item_get.setVisibility(View.VISIBLE);
            }
            //mAuthenticationTip.setText(info.getApproveid());
            //mAuthenticationTip.setTextColor(getResources().getColor(R.color.yunkacolor));
        }

//        直播
        if (info.getPlayBackCount() != null) {
            mplay_back_number.setText(info.getPlayBackCount() + getString(R.string.unit_several));
        }

//        我的账户

        muser_money.setText(ConversionUtil.conversionNumberDoubel(info.getCoinBalance()));
        //PicUtil.TextViewSpandImg(this.getActivity(), muser_money, R.drawable.ic_me_myaccount_reddiamond);

        muser_get.setText((int)info.getEarnbean()+ " " + getString(R.string.app_currency));

        if (info.getLevel() != null) {
            muser_level.setText(LocaleFormats.formatMoney(getActivity(), info.getLevel()) + getString(R.string.unit_level));
        }
        // mLevelTip.setText(info.getLevel());
        //TODO setlevel icon
        Context context = this.getActivity();

        int resId = context.getResources().getIdentifier("ic_level_" + info.getLevel(), "drawable", context.getPackageName());
        mLevelImg.setImageResource(resId);
        savaMeInfo(info);

        if(isUpdateCoin){
            Log.i("RayTest","Coin: "+ info.getCoinBalance());
            isUpdateCoin = false;
            initPaymentPage(info);
        }
    }

    private void clearMainSnapCache(String url) {
        Uri uri = Uri.parse(url);
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromCache(uri);
        imagePipeline.clearCaches();
    }

    private long getSystemTime() {
        return System.currentTimeMillis();
    }

    private void showPrivateService() {
        Log.i("RayTest","showPrivateService");
        //id = "user" + mUserInfo.getId();
        if (mUserInfo != null) {
            /*for (int i = 0; i < 5; i++) {
                JMessageClient.register(ServiceID, ServiceID, new BasicCallback() {
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
            }*/

            final String targetId = "user"+Const.MainOfficialAccount;
            Log.i("RayTest","MainOfficialAuccont:"+Const.MainOfficialAccount);
            if (JMessageClient.getMyInfo() == null) {
                Log.i("RayTest","on Click2");
                final String mMyName = "user" + LocalDataManager.getInstance().getLoginInfo().getUserId();
                final String mMyPassword = "user" + LocalDataManager.getInstance().getLoginInfo().getUserId();
                for (int i = 0; i < 5; i++) {
                    JMessageClient.register(mMyName, mMyPassword, new BasicCallback() {
                        @Override
                        public void gotResult(int status, String desc) {
                            Log.i("RayTest","desc:"+desc +" status:"+status);
                            if (status == 0) {
                                r = true;
                            } else {
                                if (desc.equals("user exist")) {
                                    r = true;
                                }
                            }
                        }
                    });
                    if (r = true) {
                        break;
                    }
                }
                if (r = true) {
                    for (int j = 0; j < 5; j++) {
                        JMessageClient.login(mMyName, mMyPassword, new BasicCallback() {
                            @Override
                            public void gotResult(int status, String desc) {
                                Log.i("RayTest","desc2:"+desc +" status2:"+status);
                                if (status == 0) {
                                    f = true;
                                    Intent intent = new Intent();

                                    intent.putExtra(TARGET_ID, targetId);
                                    intent.putExtra("name", LocalDataManager.getInstance().getUserInfo(Const.MainOfficialAccount).getNickname());
//                                            intent.putExtra("fromimg",bitmap);
                                    intent.putExtra("t", "2");
                                    intent.setClass(getActivity(), ChatActivity.class);
                                    startActivity(intent);
                                } else {
                                    toastShort(getString(R.string.msg_network_error));
                                }
                            }
                        });
                        if (f = true) {
                            break;
                        }
                    }
                }
            } else {






                Log.i("RayTest","on Click1");
                Intent intent = new Intent();
//                        intent.putExtra("fromimg",bitmap);
                intent.putExtra(TARGET_ID, targetId);
                intent.putExtra("t", "2");
                UserInfo info = LocalDataManager.getInstance().getOfficialUserInfo(Const.MainOfficialAccount);
                if(info==null){
                    toastShort("目前客服小天使正在休息唷！ 請稍候再試試吧！");
                    presenter.loadOfficialInfo(Const.MainOfficialAccount);
                }else{

                    mPermissionsChecker = new PermissionsChecker(getContext().getApplicationContext());
                    boolean camera_permission = mPermissionsChecker.lacksPermission(Manifest.permission.CAMERA);
                    boolean camera_photo_read = mPermissionsChecker.lacksPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                    boolean camera_photo_write = mPermissionsChecker.lacksPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if(camera_permission && camera_photo_read && camera_photo_write ){
                        intent.putExtra("name",  info.getNickname());
                        intent.setClass(getActivity(), ChatActivity.class);
                        startActivity(intent);
                    }else{
                        if(dialogFragment==null) {
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
                        if(!camera_permission ) {
                            dialogFragment.showMsgDialog(getActivity().getSupportFragmentManager(), "提示", getString(R.string.permissions_error_storage_camera), CreateViewDialogFragment.TYPE_SHOW_ERROR, false);
                            return;
                        }
                        if (!camera_photo_read ||!camera_photo_write ) {
                            dialogFragment.showMsgDialog(getActivity().getSupportFragmentManager(), "提示", getString(R.string.permissions_error_storage), CreateViewDialogFragment.TYPE_SHOW_ERROR, false);
                            return;
                        }

                    }

                }

            }
        }
    }

    @Override
    public void getPlayLists(List<PlayBackInfo> playBackList) {

    }

    @Override
    public void getPlayUrl(String url) {

    }

    @Override
    public void getHitCode(int code) {

    }

    @Override
    public void getRemoveHitCode(int code) {

    }

    @Override
     public void getStartCode(int code) {

    }

    @Override
    public void getRemoveStartCode(int code) {

    }

    @Override
    public void showPrivateLimit(PrivateLimitBean bean) {

    }

    @Override
    public void startGoPlayFragment() {

    }

    @Override
    public void FailDelBlackList(String blackUid) {
        Log.i("RayTest","刪除失敗");
    }

    @Override
    public void CompleteDelBlackList(List<BlackList> blackUid,int code) {
        Log.i("RayTest","刪除成功");
    }

    @Override
    public void CompleteAddBlackList(List<BlackList> blackUserId) {
        Log.i("RayTest","新增成功");
    }

    @Override
    public void FailAddBlackList(String blackUserId) {
        Log.i("RayTest","新增失敗");
    }


    private void savaMeInfo(UserInfo info) {
        LoginInfo loginInfo = LocalDataManager.getInstance().getLoginInfo();
        loginInfo.setLevel(info.getLevel());
        loginInfo.setAvatar(info.getAvatar());
        loginInfo.setCurrentRoomNum(info.getCurrentRoomNum());
        loginInfo.setNickname(info.getNickname());
        loginInfo.setTotalBalance(Long.valueOf(new java.text.DecimalFormat("#").format(Double.valueOf(info.getCoinBalance()))));
        loginInfo.setSnap(info.getSnap());
        LocalDataManager.getInstance().saveLoginInfo(loginInfo);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mePresenter.unsubscribeTasks();
        presenter.unsubscribeTasks();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_EDIT_PROFILE && resultCode == Activity.RESULT_OK) {
            if(data!=null){
                ProfilePath = data.getExtras().getString("path");
                Log.i("RayTest","path _ CODE_EDIT_PROFILE: "+ProfilePath);
                if(ProfilePath!=null && !ProfilePath.isEmpty())
                    loadLocalImage(ProfilePath);
            }
            if (mePresenter != null) {
                mePresenter.loadUserInfo(Integer.parseInt(LocalDataManager.getInstance().getLoginInfo().getUserId()));
            } else {
                L.e(LOG_TAG, "What, presenter is already null?");
            }
        }
        if (requestCode == CODE_EDIT_AVATER && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            mPhoto.setImageURI(uri);


        }
        if(requestCode== MainActivity.ACTIVITY_REQUEST_CODE){
            Log.i("RayTest","開啟金流頁面");
            PaymentIAB.getInstance().onActivityResult(requestCode, resultCode, data);
        }
        if(requestCode == MainActivity.ACTIVITY_PAYMENT_SUCCESS_CODE){
            Log.i("RayTest","結束金流頁面");
            hud = showLoadingDialog();
            hud.setCancelable(false);

            PaymentIAB.getInstance().initialize(getActivity(), mUserInfo.getId(), "http://api2.inlive.tw/", getResources().getString(R.string.googleDevKey), new PaymentIAB.BillingCompletion()
            {
                @Override
                public void onResult(PaymentIAB.BillingResult billingResult)
                {
                    if(hud.isShowing())
                        hud.dismiss();
                    mePresenter.loadUserInfo(Integer.parseInt(LocalDataManager.getInstance().getLoginInfo().getUserId()));
                    isUpdateCoin = true;

                }
            });


        }
    }

    private void loadLocalImage(String profilePath) {
        final File file = new File(profilePath);
        final Uri uri = Uri.parse("file://"+file.getAbsolutePath());
        Log.i("RayTest","loadLocalImage: "+"file:/"+file.getAbsolutePath());
        main_snap.setImageURI(uri);
    }

    @Override
    public void showLoadingComplete() {
        super.showLoadingComplete();
        mPtr.refreshComplete();
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    /**
     * 在会话列表中接收消息
     *
     * @param event 消息事件
     */
    public void onEvent(MessageEvent event) {
        Message msg = event.getMessage();
        ConversationType convType = msg.getTargetType();
        if (convType == ConversationType.single) {
            final cn.jpush.im.android.api.model.UserInfo userInfo = (cn.jpush.im.android.api.model.UserInfo) msg.getTargetInfo();
            final String targetID = userInfo.getUserName();
            final Conversation conv = JMessageClient.getSingleConversation(targetID, userInfo.getAppKey());
            if (conv != null && conv.getUnReadMsgCnt() > 0) {
                mUIHandler.sendEmptyMessage(UNREAD);
            }
        }
    }



    public String getServiceName() {
        return "客服服務";
    }

    @Override
    public void showData(List<EventSummary> list) {

    }

    @Override
    public void appendData(List<EventSummary> list) {

    }

    @Override
    public void UpdateActivateEvent(EventActivity eventActivity) {

    }

    @Override
    public void showUserInfo(UserInfo data) {

    }

    @Override
    public void onCancelDialogcheck(Bundle mArgs) {

    }

    private static class UIHandler extends Handler {
        private final WeakReference<MeFragment> mActivity;

        public UIHandler(MeFragment activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            MeFragment activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case UNREAD:
                        activity.newMsg.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
    }

    // 指定保存的路径：
    public void saveFile(Bitmap bm, String fileName) throws IOException {
        File dirFile = new File(ALBUM_PATH);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File myCaptureFile = new File(ALBUM_PATH + fileName);
        Log.i("RayTest","Save File : "+ myCaptureFile.getPath());
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(myCaptureFile));
        if (bm!=null){
            bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        }

        bos.flush();
        bos.close();

        final File path = myCaptureFile;
        if (!path.equals("") && path != null) {

            try {
                cn.jpush.im.android.api.model.UserInfo myInfo = JMessageClient.getMyInfo();
                BeautyLiveApplication.setPicturePath(myInfo.getAppKey());
                JMessageClient.updateUserAvatar(new File(String.valueOf(path)), new BasicCallback() {
                    @Override
                    public void gotResult(int status, final String desc) {
                        if (status == 0) {
                            Log.i("RayTest","updateUserAvatar ok2!!"+path );
                        }
                    }
                });
            } catch (NullPointerException e) {

            }
        }
    }

    /*public void startKsEvent() {
        KaraStar.getInstance().open(getActivity(), LocalDataManager.getInstance().getLoginInfo().getUserId(), new KaraStar.ViewHandler() {
            @Override
            public void switchToRecordView(KSWebActivity act, int starId, String starVideoUrl, String name) {
                String FilePath = VCamera.getVideoCachePath()+"K_star_rec_"+starId+"_"+LocalDataManager.getInstance().getLoginInfo().getUserId()+"/0.mp4";
                Log.i("RayTest","path3 "+FilePath);
                if(FileUtils.checkFile(FilePath)){
                    Intent it = MediaPlayerActivity.createIntent(getActivity(), FilePath, name,starVideoUrl, starId);
                    act.startActivityForResult(it, KaraStar.REQUEST_VIDEO_RECORD);
                }else {
                    Intent it = MediaRecorderActivity.createIntent(getActivity(), name, starVideoUrl, starId);
                    act.startActivityForResult(it, KaraStar.REQUEST_VIDEO_RECORD);
                }
            }

            @Override
            public void switchToUserView(KSWebActivity act, String userId) {
                Log.i("RayTest","switchToUserView: "+userId);
                act.startActivity(OtherUserActivity.createIntent(getActivity(),
                        Integer.valueOf(userId), false));
            }

        });

    }*/

    /*public void checkActivateEvent() {
        presenter.checkActivateEvent();
    }*/

    private void Check_event(int id, View view, int viewable) {
        if ((viewable == 1 )) {
            view.setVisibility(View.VISIBLE);
            item_kstar.setVisibility(View.GONE);
        } else {
            item_kstar.setVisibility(View.VISIBLE);
            view.setVisibility(View.INVISIBLE);
        }
    }
}
package tw.chiae.inlive.presentation.ui.widget;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.bean.room.RoomAdminInfo;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.data.websocket.WebSocketService;
import tw.chiae.inlive.domain.BlackList;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.main.me.OtherUserActivity;
import tw.chiae.inlive.presentation.ui.main.setting.CreateViewDialogFragment;
import tw.chiae.inlive.presentation.ui.room.RoomManageDialog;
import tw.chiae.inlive.presentation.ui.room.publish.PublishFragment;
import tw.chiae.inlive.util.DownLoadUtil;
import tw.chiae.inlive.util.L;
import tw.chiae.inlive.util.PicUtil;

import java.util.List;

/**
 * Created by huanzhang on 2016/4/10.
 */
public class UserInfoDialog extends Dialog implements View.OnClickListener, IUserInfoDialog {
    private SimpleDraweeView mPhoto, mMin;
    private Context context;
    private TextView mNickName, mId, mAddr, mCertification , mStarNum, mFansNum, mSentNum, mGetNum;
    //               关注                    管理按钮                     回復
    private TextView mStarBtn, mCompaint, mConference, mReply;
    //    关注私信和回复主页上面的那个分割线
    private View line;
    //  关注私信和回复主页的父容器 用于管理隐藏和显示
    private LinearLayout mTabBottom;
    private ImageView mCancel;
    private ProgressDialog mProgressDialog;
    public String nicName;

    private UserInfoDialogPresenter mPresenter;
    private String mUserId = null;
    private UserInfo minfo;
    private UserClickKickListener mKickListener;
    private boolean isShowKick = false;
    private ChatListener listener;
    private WebSocketService webService;

    private RoomManageDialog roomManageDialog;
    //    管理员列表
    private List<RoomAdminInfo> adminInfoList;
    //    是否是管理员
    private boolean isadmin = false;
    //    当前登录的id
    private String token;
    //    当前房间的主播id
    private String uid;

    private String roomid;
    private ImageView mSex;
    private ImageView mLevel;
    private ImageView mAppIcon;
    private int[] starticon = {0, R.drawable.id_star, R.drawable.id_vip, R.drawable.id_gold,R.drawable.id_in,R.drawable.id_specialicon};
    private AdaptiveTextView mBlackListBtn , mBrief;
    private boolean minfoRole = false;
    private ImageView crown;
    //private CreateViewDialogFragment dialogFragment;
    //private FragmentManager mFragmentManager;

    public UserInfoDialog(Context context) {
        super(context, R.style.DialogStyle);
        this.context = context;
    }

    public UserInfoDialog(Context context, WebSocketService webService, String mAnchorId, String mRoomId, ChatListener listener) {
        super(context, R.style.DialogStyle);

        this.context = context;
        this.webService = webService;
        this.uid = mAnchorId;
        this.roomid = mRoomId;
        this.listener = listener;
        setCanceledOnTouchOutside(true);
    }

    public UserInfoDialog(Context context, WebSocketService webService, String mAnchorId) {
        super(context, R.style.DialogStyle);
        this.context = context;
        this.webService = webService;
        this.uid = mAnchorId;
    }
    public UserInfoDialog(Context context, WebSocketService webService, String mAnchorId, int themeResId) {
        super(context, R.style.DialogStyle);
        this.context = context;
        this.webService = webService;
        this.uid = mAnchorId;
    }

    public UserInfoDialog(Context context, int themeResId) {
        super(context, R.style.DialogStyle);
        this.context = context;
    }

    @Override
    public void show() {
        if(isShowing())
            return;
        else
            super.show();
    }

    protected UserInfoDialog(Context context, boolean themeResId, ChatListener listener) {
        super(context, R.style.DialogStyle);
        this.context = context;
        this.listener = listener;
    }

    /*@Override
    public void onOKDialogcheck(Bundle bundle) {
        int itype = bundle.getInt("type");
        if(dialogFragment.getDialog()!=null){
            if(dialogFragment.getDialog().isShowing())
                dialogFragment.dismiss();
        }
        switch (itype){
            case CreateViewDialogFragment.TYPE_ADD_BLACKLIST:
                mPresenter.addBlackList(uid);
                break;
            case CreateViewDialogFragment.TYPE_CANCEL_BLACKLIST:
                mPresenter.delBlackList(uid,0);
                break;
        }
    }*/

    /*public void setSupportFragmentManager(FragmentManager supportFragmentManager) {
        this.mFragmentManager = supportFragmentManager;
    }*/

    public interface ChatListener {
        /**
         * 回调函数，用于在Dialog的监听事件触发后刷新Activity的UI显示
         */
        void prvChatListener();

        /**
         * 發起連麥的回調
         *
         * @param minfo
         */
        void dialogConference(UserInfo minfo);

        /**
         * 回復
         *
         * @param nickName
         */
        void sendReplyAt(String nickName);

        void updateBlackList(String uid);

        void showDialogFragment(String id, int itype);

        void updateFollowStatus(boolean b,String id );
    }

    public void setUserInofo(UserInfo info) {
        this.minfo = info;
    }

    public void setKickListener(UserClickKickListener listener) {
        mKickListener = listener;
    }

    public void showKick(boolean isShow) {
        isShowKick = isShow;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_userinfo);
        if (context == null) {
            context = getContext();
        }
        findView();
        mPresenter = new UserInfoDialogPresenter(this);
        token = LocalDataManager.getInstance().getLoginInfo().getToken();
        /*mPresenter = new UserInfoDialogPresenter(this);
        token = LocalDataManager.getInstance().getLoginInfo().getToken();
        mPresenter.getAdminList(token, uid);*/
        if(LocalDataManager.getInstance().getLoginInfo().getUserId().equals(minfo.getId())) {
            mTabBottom.setVisibility(View.GONE);
            line.setVisibility(View.INVISIBLE);
        }else {
            mTabBottom.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
        }
        mPresenter.getAdminList(token, uid);
        init();


    }

    private void findView() {
        mPhoto = (SimpleDraweeView) findViewById(R.id.dialog_user_info_photo);
        mMin = (SimpleDraweeView) findViewById(R.id.dialog_user_info_photo_min);
        mMin.setVisibility(View.GONE);
        mId = (TextView) findViewById(R.id.dialog_user_info_userid);
        mNickName = (TextView) findViewById(R.id.dialog_user_info_name);
        mAddr = (TextView) findViewById(R.id.dialog_user_info_addr);
        mCertification = (TextView) findViewById(R.id.dialog_user_info_certification);
        mBrief = (AdaptiveTextView) findViewById(R.id.dialog_user_info_signature);
        mGetNum = (TextView) findViewById(R.id.dialog_user_info_poll_num);
        mStarNum = (TextView) findViewById(R.id.dialog_user_info_star_num);
        mFansNum = (TextView) findViewById(R.id.dialog_user_info_fans_num);
        mSentNum = (TextView) findViewById(R.id.dialog_user_info_sent_num);
        mStarBtn = (TextView) findViewById(R.id.dialog_user_info_star);
        mBlackListBtn = (AdaptiveTextView) findViewById(R.id.dialog_user_info_blacklist);
        //mMainPageBtn = (TextView) findViewById(R.id.dialog_user_info_mainpage);
        mCancel = (ImageView) findViewById(R.id.dialog_user_info_cancel);
        mCompaint = (TextView) findViewById(R.id.dialog_user_info_complaint);
        //mPrvChat = (TextView) findViewById(R.id.dialog_user_info_prv_chat);
        line = findViewById(R.id.dialog_user_info_line);
        mTabBottom = (LinearLayout) findViewById(R.id.dialog_user_tab_bottom);
        mConference = (TextView) findViewById(R.id.dialog_user_info_conference);
        mReply = (TextView) findViewById(R.id.dialog_user_info_reply);

        mSex = (ImageView) findViewById(R.id.dialog_user_info_sex);
        mLevel = (ImageView) findViewById(R.id.dialog_user_info_level);
        mAppIcon = (ImageView) findViewById(R.id.iv_app_icon);

        crown = (ImageView) findViewById(R.id.iv_crown);
    }

    private void initShowManager() {
        if (LocalDataManager.getInstance().getIsHit(minfo.getId())) {
            mBlackListBtn.setTextColor(getContext().getResources().getColor(R.color.yunkacolor_h));
            mBlackListBtn.setText(context.getString(R.string.pull_black_compelet));
        } else {
            mBlackListBtn.setTextColor(getContext().getResources().getColor(R.color.black));
            mBlackListBtn.setText(context.getString(R.string.pull_black));

        }
        //如果自己是直播
        if (!LocalDataManager.getInstance().getLoginInfo().getUserId().equals(uid)) {
            /*mConference.setVisibility(View.GONE);
            mCompaint.setVisibility(View.INVISIBLE);*/
            Log.i("RayTest","isAdmin:"+isadmin);
            Log.i("RayTest","uid:"+uid);
            Log.i("RayTest","minfo:"+minfo.getId());
            if (!isadmin) {
                mCompaint.setVisibility(View.INVISIBLE);
            } else {

                if (uid.equals(minfo.getId()) || minfo.getId().equals(LocalDataManager.getInstance().getLoginInfo().getUserId())) {
                    mCompaint.setVisibility(View.INVISIBLE);
                } else {
                    if (mUserId.equals(LocalDataManager.getInstance().getLoginInfo().getUserId()) || minfo.getId().equals(LocalDataManager.getInstance().getLoginInfo().getUserId())) {

                        line.setVisibility(View.GONE);
                        mTabBottom.setVisibility(View.GONE);
                    }
                    if(minfoRole)
                        mCompaint.setVisibility(View.INVISIBLE);
                    else
                        mCompaint.setVisibility(View.VISIBLE);
                }

            }
   /*         if(minfo.getId().equals(LocalDataManager.getInstance().getLoginInfo().getUserId())){
                mPrvChat.setVisibility(View.GONE);
            }else{
                mPrvChat.setVisibility(View.GONE);
            }
*/
        } else {
            if (minfo.getId().equals(LocalDataManager.getInstance().getLoginInfo().getUserId())) {
                mCompaint.setVisibility(View.INVISIBLE);
            } else {
                mCompaint.setVisibility(View.VISIBLE);

            }
        }
        if(LocalDataManager.getInstance().getLoginInfo().getUserId().equals(minfo.getId())) {
            mTabBottom.setVisibility(View.GONE);
            line.setVisibility(View.INVISIBLE);
        }else {
            mTabBottom.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
        }


    }

    private void init() {
        if (minfo != null) {
//            if (isadmin || info.getId().equals(uid)) {
//                mCompaint.setVisibility(View.VISIBLE);
//            } else {
//                mCompaint.setText(R.string.jubao);
//            }
            mCancel.setOnClickListener(this);
            mStarBtn.setOnClickListener(this);
            //mMainPageBtn.setOnClickListener(this);
            mCompaint.setOnClickListener(this);
            //mPrvChat.setOnClickListener(this);
            mConference.setOnClickListener(this);
            mReply.setOnClickListener(this);
            mBlackListBtn.setOnClickListener(this);
            showUserInfo(minfo);
            mPresenter.loadUserInfo(mUserId);
            /*dialogFragment = CreateViewDialogFragment.newInstance();
            dialogFragment.setDialogCallback(this);*/
        }
    }

    public void setUserId(String id) {
        mUserId = id;
    }

    private void setListener() {

    }

    @Override
    public void onClick(View v) {
        //        拉黑或者是主页
  /*      if (v == mMainPageBtn && mMainPageBtn.getText().toString().equals(context.getString(R.string.main_page))) {
            goUserMainPage();
        } else if (v == mMainPageBtn && mMainPageBtn.getText().toString().equals(context.getString(R.string.pull_black))) {
            if (minfo.getId() != null) {
//                拉黑
                mPresenter.setHit(token, minfo.getId());
                List<String> blackList = new ArrayList<>();
                blackList.add("user" + minfo.getId());
                JMessageClient.addUsersToBlacklist(blackList, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        Log.i("mrl", "拉黑" + i);
                    }
                });
            }
        } else if (v == mMainPageBtn && mMainPageBtn.getText().toString().equals(context.getString(R.string.pull_black_compelet))) {
//            接触拉黑
            mPresenter.removeHit(token, minfo.getId());
            List<String> blackList = new ArrayList<>();
            blackList.add("user" + minfo.getId());
            JMessageClient.delUsersFromBlacklist(blackList, new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    Log.i("mrl", "解除拉黑" + i);
                }
            });
        }*/
        switch (v.getId()) {
            case R.id.dialog_user_info_cancel:
                dismiss();
                break;
            case R.id.dialog_user_info_star:
                if (mStarBtn.getTag() == null) {
                    CustomToast.makeCustomText(this.getContext(), context.getString(R.string.userinfo_dialog_errorload), CustomToast.LENGTH_SHORT).show();
                    return;
                }
                if (Integer.parseInt(mStarBtn.getTag().toString()) == 1) {
                    unStarUser();
                } else {
                    starUser();
                }
                break;
            case R.id.dialog_user_info_complaint:
//                CustomToast.makeCustomText(this.getContext(), getContext().getString(R.string.room_live_manage_report_compelet), CustomToast.LENGTH_SHORT).show();
                Log.i("mrl", "這尼瑪");
                if (roomManageDialog == null) {

                    roomManageDialog = new RoomManageDialog(context, webService, minfo, token, uid);
                    Window win = roomManageDialog.getWindow();
                    win.getDecorView().setPadding(0, 0, 0, 0);
                    WindowManager.LayoutParams lp = win.getAttributes();
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    win.setAttributes(lp);
                }
                roomManageDialog.show();
                break;
            case R.id.dialog_user_info_prv_chat:
                dismiss();
                listener.prvChatListener();
                break;
            case R.id.dialog_user_info_conference:
                dismiss();
                listener.dialogConference(minfo);
                break;
            case R.id.dialog_user_info_reply:
                dismiss();
                listener.sendReplyAt(minfo.getNickname());
                break;
            case R.id.dialog_user_info_blacklist:
                listener.updateBlackList(minfo.getId());
                break;
        }
    }

/*    private void updateBlackList() {
        if(mFragmentManager!=null){
            if (!LocalDataManager.getInstance().getIsHit(uid)) {
                Log.i("RayTest"," 没被拉黑");
                Activity ac = (Activity) getContext();

                dialogFragment.showCheckDelDialog(mFragmentManager,uid, CreateViewDialogFragment.TYPE_ADD_BLACKLIST);
                //mPresenter.addBlackList(mUserInfo.getId());
                //mPresenter.setHit(LocalDataManager.getInstance().getLoginInfo().getToken(), mUserInfo.getId());
            } else {
                Log.i("RayTest"," 已被拉黑");
                dialogFragment.showCheckDelDialog(mFragmentManager,uid,CreateViewDialogFragment.TYPE_CANCEL_BLACKLIST);
                //mPresenter.delBlackList(mUserInfo.getId(),0);
                //mPresenter.removeHit(LocalDataManager.getInstance().getLoginInfo().getToken(), mUserInfo.getId());
            }
        }else
            Log.i("RayTest","mFragmentManager null");

    }*/

    private void starUser() {

        if (uid.equals(minfo.getId())) {
            if(LocalDataManager.getInstance().getIsHit(minfo.getId())){
                listener.showDialogFragment(minfo.getId(),CreateViewDialogFragment.TYPE_ADD_FAVORITE_ALREADY_BLACK);
            }else
                mPresenter.starUser(LocalDataManager.getInstance().getLoginInfo().getToken(), mUserId, String.valueOf(roomid));
        } else {
            if(LocalDataManager.getInstance().getIsHit(minfo.getId())){
                listener.showDialogFragment(minfo.getId(),CreateViewDialogFragment.TYPE_ADD_FAVORITE_ALREADY_BLACK);
            }else
                mPresenter.starUser(LocalDataManager.getInstance().getLoginInfo().getToken(), mUserId, "");
        }
//        mStarBtn.setTextColor(getContext().getResources().getColor(R.color.colorSecondaryText));
//        mStarBtn.setText(getContext().getResources().getString(R.string.is_star));
//        mStarBtn.setTag(1);
    }

    private void unStarUser() {
        if (uid.equals(minfo.getId())) {
            mPresenter.unStarUser(LocalDataManager.getInstance().getLoginInfo().getToken(), mUserId, String.valueOf(roomid));
        } else {
            mPresenter.unStarUser(LocalDataManager.getInstance().getLoginInfo().getToken(), mUserId, "");
        }
//        mStarBtn.setTextColor(getContext().getResources().getColor(R.color.txt_color));
//        mStarBtn.setText(getContext().getResources().getString(R.string.star));
//        mStarBtn.setTag(0);
    }

    private void goUserMainPage() {
        PublishFragment.isCameraManagerPause = false;
        this.getContext().startActivity(OtherUserActivity.createIntent(getContext(), Integer.parseInt(mUserId), false));
        this.dismiss();
    }

    @Override
    public void showUserInfo(UserInfo info) {
        if (info == null) {
            CustomToast.makeCustomText(this.getContext(), context.getString(R.string.userinfo_dialog_errorload), CustomToast.LENGTH_SHORT).show();
            dismiss();
        } else {
            this.minfo = info;

            if (info.getSnap() != null) {
               /* FrescoUtil.frescoResize(
                        SourceFactory.wrapPathToUri(info.getAvatar()),
                        (int) this.getContext().getResources().getDimension(R.dimen.user_photo_size),
                        (int) this.getContext().getResources().getDimension(R.dimen.user_photo_size),
                        mPhoto
                );*/
                mPhoto.setImageURI(SourceFactory.wrapPathToUri(info.getSnap()));
            }
            if (info.getNickname() != null) {
                mNickName.setText(info.getNickname() + " ");
            }
            if (info.getApproveid() != null) {
                if (info.getApproveid().equals("无")) {
                    mCertification.setText("無");
                    mAppIcon.setVisibility(View.GONE);
                } else {
                    mAppIcon.setImageResource(starticon[0]);
                    if (info.getApproveid().equals("金牌藝人")) {
                        mAppIcon.setImageResource(starticon[3]);
                    }
                    if (info.getApproveid().equals("星級藝人")) {
                        mAppIcon.setImageResource(starticon[1]);
                    }
                    if (info.getApproveid().contains("官方")) {
                        mAppIcon.setImageResource(starticon[4]);
                    }
                    if (info.getApproveid().equals("特約藝人")) {
                        mAppIcon.setImageResource(starticon[5]);
                    }

                    if (info.getApproveid().contains("貴賓")){
                        crown.setVisibility(View.VISIBLE);
                    }else{
                        crown.setVisibility(View.GONE);
                    }
                    mAppIcon.setVisibility(View.VISIBLE);
                    mCertification.setText(info.getApproveid());
                }
            }


            /*PicUtil.TextViewSpandImg(this.getContext(),
                    mNickName,
                    info.getSex() == 0 ? R.drawable.ic_male : R.drawable.ic_female
                    , R.dimen.sex_icon_size, R.dimen.sex_icon_size);*/
            int SexRes = info.getSex() == 0 ? R.drawable.ic_male : R.drawable.ic_female;
            mSex.setImageResource(SexRes);

            if (info.getId() != null) {
                mId.setText(context.getString(R.string.userinfo_dialog_id) + info.getId());
            }

            if (info.getIntro() != null ) {
                if( info.getIntro().length() != 0)
                mBrief.setText(info.getIntro());
            }
//            if (info.getTotalContribution() != 0) {
            mSentNum.setText(getContext().getResources().getString(R.string.userinfo_dialog_sent));
            mSentNum.append(DownLoadUtil.conversionNumber(info.getTotalContribution()) + " IN鈔");
//            PicUtil.TextViewSpandImg(this.getContext(), mSentNum, R.drawable.ic_me_myaccount_reddiamond);
//            }

            mGetNum.setText(getContext().getResources().getString(R.string.app_currencyearn2));
            mGetNum.append(DownLoadUtil.conversionNumberDoubel(Double.valueOf(info.getCoinBalance())));

            mStarNum.setText(getContext().getResources().getString(R.string.userinfo_dialog_star));
            mStarNum.append(DownLoadUtil.conversionNumber(Integer.parseInt(info.getFolloweesCount())));
            mFansNum.setText(getContext().getResources().getString(R.string.userinfo_dialog_fans));
            mFansNum.append(info.getFollowersCount());


            // mLevelTip.setText(info.getLevel());
            Context context = this.getContext();

            int resId = context.getResources().getIdentifier("ic_level_" + info.getLevel(), "drawable", context.getPackageName());
            //mNickName.append(" ");
            //PicUtil.TextViewSpandImg(context, mNickName, resId, R.dimen.level_width, R.dimen.level_height);
            mLevel.setImageResource(resId);

            mAddr.setText("");
            PicUtil.TextViewSpandImg(context, mAddr, R.drawable.ic_room_pop_up_location
                    , R.dimen.city_width, R.dimen.city_height);
            mAddr.append(info.getCity() == null ? this.getContext().getResources().getString(R.string.city_tip) : info.getCity());

//            mCertification.setText("");
//            PicUtil.TextViewSpandImg(context,mCertification,R.drawable.ic_me_renzheng
//            ,R.dimen.city_height,R.dimen.city_height);
//            mCertification.append(context.getResources().getString(R.string.userinfo_dialog_certification));
            if (info.getIsAttention() == 1) {
                mStarBtn.setTextColor(context.getResources().getColor(R.color.colorSecondaryText));
                mStarBtn.setText(context.getResources().getString(R.string.is_star));
                mStarBtn.setTag(1);
            } else {
                mStarBtn.setTextColor(context.getResources().getColor(R.color.txt_color));
                mStarBtn.setText(context.getResources().getString(R.string.star));
                mStarBtn.setTag(0);
            }

            if (LocalDataManager.getInstance().getIsHit(minfo.getId())) {
                mBlackListBtn.setText(context.getString(R.string.pull_black_compelet));
            } else  {
                mBlackListBtn.setText(context.getString(R.string.pull_black));
            }
//            排行榜第一个的
//            if (info.getTopContributeUsers() != null && info.getTopContributeUsers().size() > 0) {
//                mMin.setVisibility(View.VISIBLE);
////                int avatarSize = (int) context.getResources().getDimension(R.dimen.avatar_size_default);
////                FrescoUtil.frescoResize(SourceFactory.wrapPathToUri(info.getTopContributeUsers().get(0)),
////                        avatarSize, avatarSize, topContributeDrawees.get(i));
//                mMin.setImageURI(SourceFactory.wrapPathToUri(info.getTopContributeUsers().get(0)));
//            } else {
//                mMin.setVisibility(View.INVISIBLE);
//            }
        }
    }

    @Override
    public void getAdminLists(List<RoomAdminInfo> adminList) {
        if (uid.equals(LocalDataManager.getInstance().getLoginInfo().getUserId())) {
            isadmin = true;
        }

        if (adminList != null) {
            for (int i = 0; i < adminList.size(); i++) {
//                判断当前用户是否是该房间的管理员
                if (uid.equals(adminList.get(i).getId())) {
                    isadmin = true;
                    //initShowManager();
                    //return;
                }

                if (minfo.getId().equals(adminList.get(i).getId())) {
                    minfoRole = true;
                    //initShowManager();
                    //return;
                }
                if (LocalDataManager.getInstance().getLoginInfo().getUserId().equals(adminList.get(i).getId())) {
                    isadmin = true;
                }
            }
        } else {

        }


        initShowManager();
        //        判断当前登录用户是否是房主
    }

    @Override
    public void adminnullgoinit() {
        mCompaint.setEnabled(true);
    }

    @Override
    public void getHitCode(int code, String actionUid) {
    /*    if (code == 0 && uid.equals(LocalDataManager.getInstance().getLoginInfo().getUserId())) {
            minfo.setIsHit(1);
            CustomToast.makeCustomText(context, context.getString(R.string.pull_black_success), Toast.LENGTH_SHORT).show();
            mMainPageBtn.setText(context.getString(R.string.pull_black_compelet));
            mStarBtn.setTextColor(getContext().getResources().getColor(R.color.txt_color));
            mStarBtn.setText(getContext().getResources().getString(R.string.star));
            mStarBtn.setTag(0);
        }*/

        if (code == 0 || code ==3) {
            minfo.setIsHit(1);
            mBlackListBtn.setTextColor(getContext().getResources().getColor(R.color.yunkacolor_h));
            mBlackListBtn.setText(getContext().getString(R.string.pull_black_compelet));
            mStarBtn.setTextColor(getContext().getResources().getColor(R.color.black));
            mStarBtn.setText(getContext().getResources().getString(R.string.star));
            mStarBtn.setTag(0);
//            unStarUser(3);
            if(listener!=null){
                listener.updateFollowStatus(false,actionUid);
            }
        }
    }

    @Override
    public void getRemoveHitCode(int code, String actionUid) {
       /* if (code == 0 && uid.equals(LocalDataManager.getInstance().getLoginInfo().getUserId())) {
            minfo.setIsHit(0);
            mMainPageBtn.setText(context.getString(R.string.pull_black));
            CustomToast.makeCustomText(context, context.getString(R.string.pull_black_remove_success), Toast.LENGTH_SHORT).show();
        }
*/
        if (code == 0 || code ==3) {
            minfo.setIsHit(0);
            mBlackListBtn.setTextColor(getContext().getResources().getColor(R.color.black));
            mBlackListBtn.setText(context.getString(R.string.pull_black));
            CustomToast.makeCustomText(context, context.getString(R.string.pull_black_remove_success), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void getStartCode(int code, String actionUid) {
        if (code == 0) {
//            CustomToast.makeCustomText(context, context.getString(R.string.star_success), Toast.LENGTH_SHORT).show();
            mStarBtn.setTextColor(getContext().getResources().getColor(R.color.colorSecondaryText));
            mStarBtn.setText(getContext().getResources().getString(R.string.is_star));
            mStarBtn.setTag(1);
            if (LocalDataManager.getInstance().getIsHit(minfo.getId())) {
                minfo.setIsHit(0);
                mBlackListBtn.setText(context.getString(R.string.pull_black));
            }
            if(listener!=null){
                listener.updateFollowStatus(true,actionUid);
            }
        } else {
            CustomToast.makeCustomText(context, context.getString(R.string.star_error), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void getRemoveStartCode(int code, String actionUid) {
        if (code == 0) {
//            CustomToast.makeCustomText(context, context.getString(R.string.star_remove_success), Toast.LENGTH_SHORT).show();
            mStarBtn.setTextColor(getContext().getResources().getColor(R.color.txt_color));
            mStarBtn.setText(getContext().getResources().getString(R.string.star));
            mStarBtn.setTag(0);
            if(listener!=null){
                listener.updateFollowStatus(false,actionUid);
            }
        } else {
            CustomToast.makeCustomText(context, context.getString(R.string.star_remove_error), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void CompleteDelBlackList(List<BlackList> blackUid,int code,String hitid) {
        getRemoveHitCode(code, hitid);
        Log.i("RayTest","刪除成功");
    }

    @Override
    public void CompleteAddBlackList(List<BlackList> blackUserId,String hitid) {
        getHitCode(0, hitid);
        Log.i("RayTest","新增成功");
    }




    @Override
    public void showDataException(String msg) {
        CustomToast.makeCustomText(this.getContext(), msg, CustomToast.LENGTH_SHORT).show();
    }

    @Override
    public void showNetworkException() {
        CustomToast.makeCustomText(this.getContext(), R.string.msg_network_error, CustomToast.LENGTH_SHORT).show();
    }

    @Override
    public void showUnknownException() {
        CustomToast.makeCustomText(this.getContext(), R.string.msg_unknown_error, CustomToast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoadingComplete() {
        //Empty implementation
    }

    @Override
    public Dialog showLoadingDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            L.e("UserInfoDialog", "Call show loading dialog while dialog is still showing, is there a bug?");
            mProgressDialog.dismiss();
        }
        mProgressDialog = ProgressDialog.show(getContext(), null, context.getString(R.string.loading_dialog_text), true, false);
        return mProgressDialog;
    }

    @Override
    public void dismissLoadingDialog() {
        if (mProgressDialog == null || (!mProgressDialog.isShowing())) {
            L.e("UserInfoDialog", "Try to dismiss a dialog but dialog is null or already dismiss!");
            return;
        }
        mProgressDialog.dismiss();
        mProgressDialog = null;
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    @Override
    public void dismiss() {
        mPresenter.unsubscribeTasks();
        super.dismiss();
    }


    /**
     * 踢人按钮点击监听
     */
    public interface UserClickKickListener {
        void clickKick(String userId, String username);
    }

}

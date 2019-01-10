package tw.chiae.inlive.presentation.ui.main.me;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;

import tw.chiae.inlive.BeautyLiveApplication;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.HotAnchorSummary;
import tw.chiae.inlive.data.bean.me.PlayBackInfo;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.domain.BlackList;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.base.BaseFragment;
import tw.chiae.inlive.presentation.ui.chatting.ChatActivity;
import tw.chiae.inlive.presentation.ui.main.currency.CurrencyActivity;
import tw.chiae.inlive.presentation.ui.main.index.privateutil.GoPrivateRoom;
import tw.chiae.inlive.presentation.ui.main.index.privateutil.GoPrivateRoomInterface;
import tw.chiae.inlive.presentation.ui.main.me.sublist.SubListActivity;
import tw.chiae.inlive.presentation.ui.main.setting.CreateViewDialogFragment;
import tw.chiae.inlive.presentation.ui.main.webview.SimpleWebViewActivity;
import tw.chiae.inlive.presentation.ui.room.RoomActivity;
import tw.chiae.inlive.presentation.ui.room.create.PrivateSetStringDialog;
import tw.chiae.inlive.presentation.ui.room.player.PlayerFragment;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.EventUtil;
import tw.chiae.inlive.util.FrescoUtil;
import tw.chiae.inlive.util.PicUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import rx.functions.Action1;

//用户主页
public class OtherUserActivity extends BaseActivity implements IMe, GoPrivateRoomInterface, CreateViewDialogFragment.dialogCallback {
    private static String KEY_ID = "id";
    //這個應該是判斷是否在直播吧
    private static String KEY_SHOW_ONLINE = "showOnline";
    private String mUid = "";
    private static final String TARGET_ID = "targetId";
    private boolean isCanShowOnline = false;
    private ViewPager mViewPager;
    private ImageButton mBack;
    private TextView mOnline;
    private TextView mName, mId, mSign, mSent;
    private SimpleDraweeView mPhoto;
    private ImageView mSex, mEdit;
    //    下面三个按钮  关注私聊 拉黑
    private LinearLayout mStarBtn, mChatBtn, mPullBackBtn;
    //    拉黑的文字
    private TextView mPullBackTv;
    private TextView mStarTv;
    private TextView mStar, mFans, mDataTab, mBackPlayTab;
    private ImageView mlevel;
    //    下面的是关注  粉丝
    private View mStarLine, mFansLine;
    private MePresenter mPresenter;
    private TextView mRank;
    private UserInfo mUserInfo;
    private Context mContext;
    private boolean r = false;
    private boolean f = false;
    private boolean m = false;
    private String id;
    private Bitmap bitmap;
    private ImageView mPhotoBG;
    private Handler handler = new Handler();
    private HotAnchorSummary hotAnchorSummary;

    //使用列表存放，方便遍历
    private List<SimpleDraweeView> topContributeDrawees;
    // 私密房间的dialog
    private GoPrivateRoom goPrivateRoom;
    private TextView mStarView,mFansView;
    private CreateViewDialogFragment dialogFragment;

    public static Intent createIntent(Context context, int uid, boolean showOnline) {
        Intent intent = new Intent(context, OtherUserActivity.class);
        intent.putExtra(KEY_ID, uid);
        intent.putExtra(KEY_SHOW_ONLINE, showOnline);
        return intent;
    }

    @Override
    protected void onResume() {
//        MobclickAgent.onPageStart("用户主页");
        super.onResume();
        mPresenter.loadUserInfo(Integer.parseInt(mUid));
    }

    @Override
    protected void onPause() {
//        MobclickAgent.onPageEnd("用户主页");
        super.onPause();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_otheruser;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        mUid = String.valueOf(this.getIntent().getIntExtra(KEY_ID, 0));
        mContext = this;
        isCanShowOnline = this.getIntent().getBooleanExtra(KEY_SHOW_ONLINE, false);
        mViewPager = $(R.id.other_user_viewpager);
        mBack = (ImageButton) findViewById(R.id.imgbtn_toolbar_back);
        mOnline = (TextView) findViewById(R.id.me_massage);

        mPhoto = $(R.id.me_photo);
        mPhotoBG = $(R.id.iv_other_snap_bg);
//        mMinPhoto = $(R.id.me_photo_min);

        mName = $(R.id.me_name);
        mSign = $(R.id.me_sign);
        mSent = $(R.id.me_send);
        mSex = $(R.id.me_sex);
        mEdit = $(R.id.me_edit);
        mId = $(R.id.me_id);
        mStar = $(R.id.me_user_info_star);
        mDataTab = $(R.id.me_user_info_data_tab);
        mFans = $(R.id.me_user_info_fans);
        mBackPlayTab = $(R.id.me_user_info_backplay_tab);
        mlevel = $(R.id.other_level);



        mStarBtn = $(R.id.other_user_starly);
        mChatBtn = $(R.id.other_user_letter);
        mPullBackBtn = $(R.id.other_user_pull_black);
        mStarTv = $(R.id.other_user_star);
        mPullBackTv = $(R.id.other_user_pull_black_tv);

        mStarLine = $(R.id.other_user_star_line);
        mFansLine = $(R.id.other_user_fans_line);

        mRank = $(R.id.me_coin_rank_tv);

        mStarView = $(R.id.me_other_info_star_tv);
        mFansView = $(R.id.me_other_info_fans_tv);

        topContributeDrawees = new ArrayList<>();
        SimpleDraweeView draweeTop1 = $(R.id.me_coin_top1);
        SimpleDraweeView draweeTop2 = $(R.id.me_coin_top2);
        SimpleDraweeView draweeTop3 = $(R.id.me_coin_top3);
        topContributeDrawees.add(draweeTop1);
        topContributeDrawees.add(draweeTop2);
        topContributeDrawees.add(draweeTop3);

    }

    @Override
    protected void init() {
        mPresenter = new MePresenter(this);
        mStarBtn.setVisibility(View.VISIBLE);
        TextView tvRankLabel = $(R.id.me_coin_rank_tv);
        tvRankLabel.setText(getString(R.string.coin_rank, getString(R.string.app_currency)));

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mEdit.setVisibility(View.GONE);
        mDataTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });
        mBackPlayTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1);
            }
        });

        RxView.clicks(mRank).throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (mUserInfo != null) {
                            CheckEvent();
                        }
                    }
                });

        mViewPager.setCurrentItem(0);
        dialogFragment = CreateViewDialogFragment.newInstance();
        dialogFragment.setDialogCallback(this);
        setCheck(0);
    }

    private void CheckEvent() {

        CheckEventSwitch(EventUtil.RankID, new BaseFragment.EventCheckCallback() {
            @Override
            public void eventSW(boolean sw) {
                Log.i("RayTest","CheckEventSwitch: "+sw);
                if(sw){
                    startActivity(SimpleWebViewActivity.createIntent(OtherUserActivity.this, Const.RankPageUrl+mUserInfo.getId(),""));
                }else{
                    startActivity(CurrencyActivity.createIntent(OtherUserActivity.this, mUserInfo.getId()));
                }
            }
        });
    }

    private void setCheck(int position) {
        if (position == 0) {
//            mStar.setTextColor(getResources().getColor(R.color.yunkacolor));
            mStarLine.setVisibility(View.VISIBLE);
//            mFans.setTextColor(getResources().getColor(R.color.rgb_AAAAAA));
            mFansLine.setVisibility(View.INVISIBLE);
        } else {
//            mStar.setTextColor(getResources().getColor(R.color.rgb_AAAAAA));
            mStarLine.setVisibility(View.INVISIBLE);
//            mFans.setTextColor(getResources().getColor(R.color.yunkacolor));
            mFansLine.setVisibility(View.VISIBLE);
        }
    }

    private void starUser(int code) {
        if (mUid != null && dialogFragment!=null) {

            if(LocalDataManager.getInstance().getIsHit(mUserInfo.getId()))
                dialogFragment.showCheckDelDialog(getSupportFragmentManager(),mUserInfo.getId(),CreateViewDialogFragment.TYPE_ADD_FAVORITE_ALREADY_BLACK);
            else
                mPresenter.starUser(LocalDataManager.getInstance().getLoginInfo().getToken(), mUid, "", code);
        }
//        mStarTv.setText(getResources().getString(R.string.is_star));
//        mStarBtn.setTag(1);
    }

    private void unStarUser(int code) {
        if (mUid != null) {
            mPresenter.unStarUser(LocalDataManager.getInstance().getLoginInfo().getToken(), mUid, "",code);
        }
//        mStarTv.setText(getResources().getString(R.string.star));
//        mStarBtn.setTag(0);
    }

    @Override
    public void showInfo(UserInfo info) {
        if (info == null) {
            return;
        }
        mUserInfo = info;
        if (info.getSnap() != null) {
            FrescoUtil.frescoResize(
                    SourceFactory.wrapPathToUri(info.getSnap()),
                    getWidth(),
                    getWidth(),
                    mPhoto
            );
        }
        //获取当前头像bitmap值
//        Glide.with(mContext).load(SourceFactory.wrapPathToUri(info.getSnap())).asBitmap().into(new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                bitmap=resource;
//            }
//        });
//        bitmap =returnBitmap(SourceFactory.wrapPathToUri(info.getSnap()));

        new Thread(new Runnable() {

            @Override
            public void run() {
                URL url;
                boolean isSnapDefault = false;
                try {

                    url = new URL(Const.MAIN_HOST_URL + mUserInfo.getSnap());
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
                }
                final boolean finalIsSnapDefault = isSnapDefault;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(finalIsSnapDefault) {
                            mPhotoBG.setImageResource(R.drawable.snap_default);
                        }else{

                            mPhotoBG.setImageBitmap(new BitmapDrawable(getResources(),bitmap).getBitmap());
                            // main_snap_mask.setBackgroundColor(0xB4A6A3A4);
                            //me_snap.setColor(0xB4A6A3A4);
                        }

                    }
                });
            }
        }).start();





        //TODO add min photo
        if (info.getNickname() != null) {
            mName.setText(info.getNickname());
        }
        mSex.setImageResource(info.getSex() == 0 ? R.drawable.ic_male : R.drawable.ic_female);

        if (info.getIntro() != null) {
            mSign.setText(info.getIntro());
        }
        if (info.getLevel() != null) {
            mlevel.setImageResource(PicUtil.getLevelImageId(this, Integer.parseInt(info.getLevel())));
        }
//        mId.setText("");
//        PicUtil.TextViewSpandImg(this,mId,R.drawable.ic_me_renzheng
//                ,R.dimen.city_height,R.dimen.city_height);
//        mId.append(this.getResources().getString(R.string.userinfo_dialog_certification));

        if (info.getTotalContribution() != 0) {
            mSent.setText(getString(R.string.me_send_currency) + info.getTotalContribution());
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
        mStarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserInfo != null) {
                    startActivity(SubListActivity.createIntent(OtherUserActivity.this, mUserInfo.getId(), SubListActivity.KEY_STAR));
                }
            }
        });
        mFansView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserInfo != null) {
                    startActivity(SubListActivity.createIntent(OtherUserActivity.this, mUserInfo.getId(), SubListActivity.KEY_FANS));
                }
            }
        });
        if (info.getIsAttention() == 1) {
            mStarTv.setText(getResources().getString(R.string.is_star));
            mStarBtn.setTag(1);
            mStarTv.setTextColor(getResources().getColor(R.color.yunkacolor_h));
        } else {
            mStarTv.setText(getResources().getString(R.string.star));
            mStarBtn.setTag(0);
            mStarTv.setTextColor(getResources().getColor(R.color.black));
        }

//        关注
        mStarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(mStarBtn.getTag().toString()) == 1) {
                    unStarUser(0);
                } else {
                    starUser(0);
                }
            }
        });

//        拉黑
        mPullBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                没被拉黑
                Log.i("RayTest","uid:"+LocalDataManager.getInstance().getLoginInfo().getUserId()+" blackid: "+mUserInfo.getId());
                if(LocalDataManager.getInstance().getLoginInfo().getUserId().equals(mUserInfo.getId())) {
                    toastShort("不可以加入自己黑名單");
                }else{
                    if (!LocalDataManager.getInstance().getIsHit(mUserInfo.getId())) {
                        Log.i("RayTest"," 没被拉黑");
                        dialogFragment.showCheckDelDialog(getSupportFragmentManager(),mUserInfo.getId(),CreateViewDialogFragment.TYPE_ADD_BLACKLIST);
                        //mPresenter.addBlackList(mUserInfo.getId());
                        //mPresenter.setHit(LocalDataManager.getInstance().getLoginInfo().getToken(), mUserInfo.getId());
                    } else {
                        Log.i("RayTest"," 已被拉黑");
                        dialogFragment.showCheckDelDialog(getSupportFragmentManager(),mUserInfo.getId(),CreateViewDialogFragment.TYPE_CANCEL_BLACKLIST);
                        //mPresenter.delBlackList(mUserInfo.getId(),0);
                        //mPresenter.removeHit(LocalDataManager.getInstance().getLoginInfo().getToken(), mUserInfo.getId());
                    }
                }

            }
        });

//        私信
        mChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id = "user" + mUserInfo.getId();
                if (mUserInfo != null) {
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

                    if (JMessageClient.getMyInfo() == null) {
                        final String mMyName = "user" + LocalDataManager.getInstance().getLoginInfo().getUserId();
                        final String mMyPassword = "user" + LocalDataManager.getInstance().getLoginInfo().getUserId();
                        for (int i = 0; i < 5; i++) {
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
                            if (r = true) {
                                break;
                            }
                        }
                        if (r = true) {
                            for (int j = 0; j < 5; j++) {
                                JMessageClient.login(mMyName, mMyPassword, new BasicCallback() {
                                    @Override
                                    public void gotResult(int status, String desc) {
                                        if (status == 0) {
                                            f = true;
                                            Intent intent = new Intent();
                                            intent.putExtra(TARGET_ID, id);
                                            intent.putExtra("name", mUserInfo.getNickname());
//                                            intent.putExtra("fromimg",bitmap);
                                            intent.putExtra("t", "2");
                                            intent.setClass(mContext, ChatActivity.class);
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
                        Intent intent = new Intent();
//                        intent.putExtra("fromimg",bitmap);
                        intent.putExtra(TARGET_ID, id);
                        intent.putExtra("t", "2");
                        intent.putExtra("name", mUserInfo.getNickname());
                        intent.setClass(mContext, ChatActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
        if (LocalDataManager.getInstance().getIsHit(mUserInfo.getId())) {
            mPullBackTv.setTextColor(getResources().getColor(R.color.yunkacolor_h));
            mPullBackTv.setText(getString(R.string.pull_black_compelet));

        } else {
            //yunkacolor_h
            mPullBackTv.setTextColor(getResources().getColor(R.color.black));
            mPullBackTv.setText(getString(R.string.pull_black));
        }

        if (isCanShowOnline && (!TextUtils.isEmpty(info.getBroadcasting())) && info.getBroadcasting().equals("y")) {
            mOnline.setVisibility(View.VISIBLE);
            hotAnchorSummary = new HotAnchorSummary();
            hotAnchorSummary.setAvatar(info.getAvatar());
            hotAnchorSummary.setSnap(info.getSnap());
            hotAnchorSummary.setCurrentRoomNum(info.getCurrentRoomNum());
            hotAnchorSummary.setId(info.getId());
            hotAnchorSummary.setNickname(info.getNickname());
            hotAnchorSummary.setOnlineCount(0);
            mOnline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intentRoom();
                }
            });
        }

        //OtherUserBackPlayFragment otherUserBackPlayFragment = OtherUserBackPlayFragment.newInstance(mUserInfo);
        //otherUserBackPlayFragment.setOtherUserActivity(this);

        final Fragment[] fragments = new Fragment[]{OtherUserDataFragment.newInstance(mUserInfo)};
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setCheck(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void getPlayLists(List<PlayBackInfo> playBackList) {

    }

    @Override
    public void getPlayUrl(String url) {

    }

    //    拉黑回调
    @Override
    public void getHitCode(int code) {
        if (code == 0 || code ==3) {
            if(code==0)
                toastShort(getString(R.string.pull_black_success));
            mUserInfo.setIsHit(1);
            mPullBackTv.setTextColor(getResources().getColor(R.color.yunkacolor_h));
            mPullBackTv.setText(getString(R.string.pull_black_compelet));
            mStarTv.setTextColor(getResources().getColor(R.color.black));
            mStarTv.setText(getResources().getString(R.string.star));
            mStarBtn.setTag(0);
            unStarUser(3);
        }
    }

    @Override
    public void getRemoveHitCode(int code) {
        if (code == 0 || code ==3) {
            if(code==0)
                toastShort(getString(R.string.pull_black_remove_success));
            mUserInfo.setIsHit(0);
            mPullBackTv.setTextColor(getResources().getColor(R.color.black));
            mPullBackTv.setText(getString(R.string.pull_black));
        }
    }


    @Override
    public void getStartCode(int code) {
        if (code == 0 || code ==3) {
            if(code==0)
                toastShort(getString(R.string.star_success));
            mStarTv.setText(getResources().getString(R.string.is_star));
            mStarTv.setTextColor(getResources().getColor(R.color.yunkacolor_h));
            mStarBtn.setTag(1);
            mUserInfo.setIsHit(0);
            mPresenter.delBlackList(mUserInfo.getId(),3);
            mPullBackTv.setTextColor(getResources().getColor(R.color.black));
            mPullBackTv.setText(getString(R.string.pull_black));
        }


    }

    @Override
    public void getRemoveStartCode(int code) {
        if (code == 0|| code ==3) {
            if(code==0)
                toastShort(getString(R.string.star_remove_success));
            mStarTv.setTextColor(getResources().getColor(R.color.black));
            mStarTv.setText(getResources().getString(R.string.star));
            mStarBtn.setTag(0);
        }
    }

    @Override
    public void showPrivateLimit(PrivateLimitBean bean) {
        dismissLoadingDialog();
        if (bean.getCome() == 0 && bean.getPtid() != 0) {
            if (bean.getPtid() == Integer.valueOf(PrivateSetStringDialog.PRIVTE_ROOM_LEVEL)) {
                //如果等级够了，直接调用验证接口
                if (Integer.valueOf(bean.getPrerequisite()) <= Integer.valueOf(LocalDataManager.getInstance().getLoginInfo().getLevel())) {
                    mPresenter.checkPrivatePass(PrivateSetStringDialog.PRIVTE_ROOM_LEVEL, bean.getId(), "", LocalDataManager.getInstance().getLoginInfo().getUserId(), hotAnchorSummary.getId());
                    return;
                }
            }
            if (goPrivateRoom == null)
                goPrivateRoom = new GoPrivateRoom();
            goPrivateRoom.setGoPrivateRoomInterface(this);
            Bundle bundle = new Bundle();
            bundle.putInt(GoPrivateRoom.GO_PLID, bean.getId());
            bundle.putString(GoPrivateRoom.GO_PRIVATE_TYPE, String.valueOf(bean.getPtid()));
            bundle.putString(GoPrivateRoom.GO_PRIVATE_CONTE, bean.getPrerequisite());
            bundle.putString(GoPrivateRoom.GO_NAME, hotAnchorSummary.getNickname());
            bundle.putString(GoPrivateRoom.GO_PHOTO, hotAnchorSummary.getAvatar());
            bundle.putString(GoPrivateRoom.GO_USER_ID, hotAnchorSummary.getId());
            bundle.putString(GoPrivateRoom.GO_LAYOU_BG, hotAnchorSummary.getSnap());
            goPrivateRoom.setArguments(bundle);
            goPrivateRoom.show(getFragmentManager(), "dasdas");
        } else
            startPlayFragment();
    }

    @Override
    public void startGoPlayFragment() {
        startPlayFragment();
    }

    private void intentRoom() {
        showLoadingDialog();
        mPresenter.loadPrivateLimit(hotAnchorSummary.getId());
    }

    private Bitmap returnBitmap(Uri uri) {

        Bitmap bitmap = null;
        FileBinaryResource resource = (FileBinaryResource) Fresco.getImagePipelineFactory().getMainDiskStorageCache().getResource(new SimpleCacheKey(uri.toString()));
        File file = resource.getFile();
        bitmap = BitmapFactory.decodeFile(file.getPath());
        return bitmap;
    }

    public void startPlayFragment() {
        startActivity(RoomActivity.createIntent(this,
                RoomActivity.TYPE_VIEW_LIVE,
                hotAnchorSummary.getCurrentRoomNum(),
                hotAnchorSummary.getId(),
                PlayerFragment.createArgs(hotAnchorSummary)));
        this.overridePendingTransition(R.anim.fragment_slide_left_in, R.anim
                .fragment_slide_left_out);
    }

    @Override
    public void questGoPrivateRoom(String type, int msg, String userid, String pwd) {
        mPresenter.checkPrivatePass(type, msg, pwd, LocalDataManager.getInstance().getLoginInfo().getUserId(), userid);
    }

    public void saveFile(Bitmap bm, String fileName) throws IOException {
        final String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/mypic_data/";
        File dirFile = new File(ALBUM_PATH);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File myCaptureFile = new File(ALBUM_PATH + fileName);
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
                        }
                    }
                });
            } catch (NullPointerException e) {
            }
        }
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    @Override
    public void FailDelBlackList(String blackUid) {
        Log.i("RayTest","刪除失敗");
    }

    @Override
    public void CompleteDelBlackList(List<BlackList> blackUid,int code) {
        getRemoveHitCode(code);
        Log.i("RayTest","刪除成功");
    }

    @Override
    public void CompleteAddBlackList(List<BlackList> blackUserId) {
        getHitCode(0);
        Log.i("RayTest","新增成功");
    }

    @Override
    public void FailAddBlackList(String blackUserId) {

        Log.i("RayTest","新增失敗");
    }

    @Override
    public void onOKDialogcheck(Bundle bundle) {
        int itype = bundle.getInt("type");
        if(dialogFragment.getDialog()!=null){
            if(dialogFragment.getDialog().isShowing())
                dialogFragment.dismiss();
        }
        switch (itype){
            case CreateViewDialogFragment.TYPE_ADD_BLACKLIST:
                mPresenter.addBlackList(mUserInfo.getId());
                break;
            case CreateViewDialogFragment.TYPE_CANCEL_BLACKLIST:
                mPresenter.delBlackList(mUserInfo.getId(),0);
                break;
        }

    }

    @Override
    public void onCancelDialogcheck(Bundle mArgs) {

    }
}

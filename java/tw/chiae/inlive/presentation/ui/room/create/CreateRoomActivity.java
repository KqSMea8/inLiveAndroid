package tw.chiae.inlive.presentation.ui.room.create;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import tw.chiae.inlive.BeautyLiveApplication;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.data.bean.ThemBean;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.bean.room.CreateRoomBean;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerAdapter;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerHolder;
import tw.chiae.inlive.presentation.ui.main.index.MyLinearLayout;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;
import tw.chiae.inlive.presentation.ui.main.setting.CreateViewDialogFragment;
import tw.chiae.inlive.presentation.ui.room.RoomActivity;
import tw.chiae.inlive.presentation.ui.room.publish.PublishFragment;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.L;
import tw.chiae.inlive.util.PermissionsChecker;
import tw.chiae.inlive.util.share.ShareHelper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.google.GooglePlus;

import cn.sharesdk.line.Line;
import rx.functions.Action1;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class CreateRoomActivity extends BaseActivity implements CreateRoomInterface, PrivateTypeCommit {

    //    标题
    private EditText edtLiveTitle;
    //    分享按钮
    private ImageButton imgbtnFacebook, imgbtnGoogle, imgbtnLine, imgbtnInstagram;
    private List<ImageButton> listInitShare;
    private PopupWindow popupWindowInitShare;
    private TextView tvInitShareTips;
    private int mInitShareIndex = -1;        //初始创建时的分享平台
    private Platform mPlatform;
    private TextView changes;

    private MyLinearLayout create_img;
    private ImageView iv_create_img;

    private static final String GETCTIY = "city";
    private static final String GETPROVINCE = "province";
    private String mCity;
    private String mProvince;
    private CreateRoomShareHelper shareHelper;

    private ImageButton imgbtnLocking, imgbtnUnlocking;
    private Button btnStartLive;

    private CreateRoomPresenter presenter;
    private String mPushAddress;
    private Dialog mLoadingDialog;
    private int change;

    //    城市
    private TextView mCityTv;
    //    话题输入框的值
    private String mTitleThem;
    //    话题列表
    private List<ThemBean.Topic> topicsList;
    //    文本监听的
    private CharSequence temp;//监听前的文本
    private int editStart;//光标开始位置
    private int editEnd;//光标结束位置
    private final int charMaxNum = 20;
    //    屏幕高度 状态栏高度
    private int stateh, xunih;
    //    记录输入前的标题字数
    private int editbefor;
    private TextView roomToptx;
    //    boolean startok = false;
    //私密按钮
    private LinearLayout startPrivate;
    //私密的字段
    private String privateString;
    //私密的类型
    private int privateType;
    //创建房间的返回对象
    private CreateRoomBean createRoomBean;
    //那个私密文字
    private TextView privateStartTitle;

    private Bitmap bitmap;
    ArrayList<Integer> arrayListForSize = new ArrayList<Integer>();

    private final static String ALBUM_PATH = Environment.getExternalStorageDirectory() + "/mypic_data/";

    private Handler handler = new Handler();
    private boolean isLoading = false;
    private Handler delayTimeHandler;
    private PermissionsChecker mPermissionsChecker;
    private CreateViewDialogFragment dialogFragment;

    public static Intent createIntent(Context context, String city, String province) {

        Intent intent = new Intent(context, CreateRoomActivity.class);
        intent.putExtra(GETCTIY, city);
        intent.putExtra(GETPROVINCE, province);
        return intent;
    }

    @Override
    protected void parseIntentData(Intent intent, boolean isFromNewIntent) {
        super.parseIntentData(intent, isFromNewIntent);
        mCity = intent.getStringExtra(GETCTIY);
        mProvince = intent.getStringExtra(GETPROVINCE);
        String[] permissions = {""};


        mPermissionsChecker = new PermissionsChecker(getApplicationContext());
        boolean camera_permission = mPermissionsChecker.lacksPermission(Manifest.permission.CAMERA);
        boolean audio_permission = mPermissionsChecker.lacksPermission(Manifest.permission.RECORD_AUDIO);

        if(camera_permission && audio_permission ){
            showCameraSize();
        }else{
            if (dialogFragment == null) {
                dialogFragment = CreateViewDialogFragment.newInstance();
            }
            dialogFragment.setDialogCallback(new CreateViewDialogFragment.dialogCallback() {
                @Override
                public void onOKDialogcheck(Bundle bundle) {
                    dialogFragment.dismiss();
                    onBackPressed();

                }

                @Override
                public void onCancelDialogcheck(Bundle mArgs) {

                }
            });

            if(!camera_permission && !audio_permission){
                dialogFragment.showMsgDialog(getSupportFragmentManager(), "提示", getString(R.string.permissions_error_audio_camera), CreateViewDialogFragment.TYPE_SHOW_ERROR, false);
                return;
            }else {
                if (!camera_permission) {
                    dialogFragment.showMsgDialog(getSupportFragmentManager(), "提示", getString(R.string.permissions_error_camera), CreateViewDialogFragment.TYPE_SHOW_ERROR, false);
                    return;
                }
                if (!audio_permission) {
                    dialogFragment.showMsgDialog(getSupportFragmentManager(), "提示", getString(R.string.permissions_error_audio), CreateViewDialogFragment.TYPE_SHOW_ERROR, false);
                    return;
                }
            }
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_room;
    }

    public static int changed;


    @Override
    protected void findViews(Bundle savedInstanceState) {

        presenter = new CreateRoomPresenter(this);
        create_img = $(R.id.create_img);
        iv_create_img = $(R.id.iv_create_img);
        roomToptx = $(R.id.create_room_top_tx);
        imgbtnLocking = $(R.id.room_create_imgbtn_locking);
        imgbtnUnlocking = $(R.id.room_create_imgbtn_unlocking);
        mCityTv = $(R.id.create_room_top_city);
        imgbtnFacebook = $(R.id.room_create_imgbtn_share_facebook);
        imgbtnGoogle = $(R.id.room_create_imgbtn_share_google);
        imgbtnLine = $(R.id.room_create_imgbtn_share_line);
        imgbtnInstagram = $(R.id.room_create_imgbtn_share_instagram);
        changes = $(R.id.change);
        startPrivate = $(R.id.room_private);
        privateStartTitle = $(R.id.room_private_starttitle);
        delayTimeHandler  = new Handler();
        listInitShare = new ArrayList<>(4);
        Collections.addAll(listInitShare, imgbtnFacebook, imgbtnGoogle, imgbtnLine, imgbtnInstagram);

        if (mCity != null) {
            mCityTv.setText(mCity);
        } else {
            mCityTv.setText(getString(R.string.city_tip));
        }
        changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changes.getText().toString().equals(getString(R.string.room_live_create_land))) {
                    changes.setText(getString(R.string.room_live_create_pront));
                    change = 1;
                } else if (changes.getText().toString().equals(getString(R.string.room_live_create_pront))) {
                    changes.setText(getString(R.string.room_live_create_land));
                    change = 2;
                }
                changed = change;
            }
        });

        final LoginInfo loginInfo = LocalDataManager.getInstance().getLoginInfo();
//        mInitShareIndex=1;
//        mPlatform=new Wechat(this);
//        listInitShare.get(mInitShareIndex).setSelected(true);
        shareHelper = new CreateRoomShareHelper(this, loginInfo.getCurrentRoomNum(), loginInfo
                .getNickname(), loginInfo.getAvatar(), new ShareHelper.DefaultShareListener() {
            @Override
            public void onCancel(Platform platform, int i) {
                super.onCancel(platform, i);
                toastShort("分享取消咯");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                super.onError(platform, i, throwable);
                toastShort("分享失敗咯");
            }

            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                super.onComplete(platform, i, hashMap);
                toastShort("分享成功咯");
            }
        });

        subscribeInitShare(imgbtnFacebook, 0, new Facebook(this), getString(R.string.share_channel_facebook));
        subscribeInitShare(imgbtnGoogle, 1, new GooglePlus(this), getString(R.string.share_channel_google));
        /*subscribeInitShare(imgbtnLine, 2, new Line(this), getString(R.string.share_channel_line));*/
/*        subscribeInitShare(imgbtnInstagram, 3, new Instagram(this), getString(R.string.share_channel_instagram));*/
        edtLiveTitle = $(R.id.room_edt_title);
        edtLiveTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
                editbefor = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                /** 得到光标开始和结束位置 ,超过最大数后记录刚超出的数字索引进行控制 */
                editStart = edtLiveTitle.getSelectionStart();
                editEnd = edtLiveTitle.getSelectionEnd();
                //字数限制
                if (temp.length() > charMaxNum) {
                    toastShort("你輸入的字數已經超過了限制！");
                    s.delete(editStart - 1, editEnd);
                    int tempSelection = editStart;
                    edtLiveTitle.setText(s);
                    edtLiveTitle.setSelection(tempSelection);
                }
//      这里判断是删除操作还是输入操作
                if (editbefor < s.length()) {
                    //                防止删除到最后一个的时候里面判断出现数组越界
                    if (s.length() != 0) {
//                    如果只有最后一位
                        if (s.length() == 1) {
                            if (s.charAt(s.length() - 1) == '#') {
                                s.delete(0, editEnd);
                                startThemPopupWindow();
                            }
                        } else if (editStart == s.length()) {
                            if (s.charAt(s.length() - 1) == '#') {
                                s.delete(editStart - 1, editEnd);
                                startThemPopupWindow();
                            }
                        }
                    }
                }
            }
        });
        edtLiveTitle.setSelection(edtLiveTitle.getText().length());
        btnStartLive = $(R.id.room_create_btn_start_live);

        //开启私密
        RxView.clicks(startPrivate).throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        showPrivateTypeDialog();
                    }
                });

//        开始直播
        subscribeClick(btnStartLive, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (TextUtils.isEmpty(mPushAddress)) {
                    toastShort(getString(R.string.createroom_not_publishadr));
                } else {
                    checkAndStartPublish();
                }
            }
        });

        subscribeClick($(R.id.room_create_btn_close), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                finish();
            }
        });

        subscribeClick($(R.id.room_live_create_theme), new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startThemPopupWindow();
            }
        });

        presenter.generatePushStreaming();
        mLoadingDialog = showLoadingDialog();
        presenter.getThemBean();
    }

    private void checkAndStartPublish() {
        if(isLoading){
            toastShort("直播間建立中,五秒後再重試");
            return;
        }
        if(Const.isDebugMode){
            enterLiveRoom();
        }else {
            if ((!TextUtils.isEmpty(mPushAddress)) && (mLoadingDialog == null || (!mLoadingDialog.isShowing()))) {
                isLoading = true;
                enterLiveRoom();
                delayTimeHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isLoading = false;
                    }
                },5000);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.loadUserInfo(Integer.parseInt(LocalDataManager.getInstance().getLoginInfo().getUserId()));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //   开启直播
    private void enterLiveRoom() {
        if(Const.isDebugMode) {

            showLoadingDialog();
            LoginInfo loginInfo = LocalDataManager.getInstance().getLoginInfo();
            Intent intent = RoomActivity.createIntent(this, RoomActivity.TYPE_PUBLISH_LIVE, loginInfo.getCurrentRoomNum(), loginInfo.getUserId(),
                    PublishFragmentTest.createArgs(mPushAddress, PublishFragment.RTC_ROLE_ANCHOR, loginInfo.getCurrentRoomNum(), this.createRoomBean, arrayListForSize));
            startActivity(intent);
            finish();
            return;
        }
        if (TextUtils.isEmpty(edtLiveTitle.getText().toString().trim())) {
            toastShort("給個標題吧");
        } else {
            LoginInfo loginInfo = LocalDataManager.getInstance().getLoginInfo();
            char scren = 'v';
            if (changed == 2) {
                scren = 'h';
            }
            if (mCity == null) {
                presenter.postCreatRoom(loginInfo.getToken(), edtLiveTitle.getText().toString(), loginInfo.getCurrentRoomNum(), getString(R.string.unknown_city), getString(R.string.unknown_city), scren, privateString, privateType,loginInfo.getApproveid());
            } else {
                mProvince = mProvince.replaceAll("省", "");
                presenter.postCreatRoom(loginInfo.getToken(), edtLiveTitle.getText().toString(), loginInfo.getCurrentRoomNum(), mCity, mProvince, scren, privateString, privateType,loginInfo.getApproveid());
            }

        }
    }

    @Override
    protected void init() {

    }


    @Override
    public void showInfo(final UserInfo info) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    options.inPurgeable = true;// 允许可清除
                    options.inInputShareable = true;// 以上options的两个属性必须联合使用才会有效果
                    url = new URL(Const.MAIN_HOST_URL + info.getSnap());
                    InputStream is = url.openStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //iv_create_img.setImageResource(R.drawable.bkg_create_room);
                        //create_img.setBackgroundDrawable(new BitmapDrawable(bitmap));
                    }
                });
            }
        }).start();
    }

    @Override
    public void onPushStreamReady(String address) {
        //mPushAddress = address;

        mPushAddress = address;
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    //    得到话题列表对象
    @Override
    public void onThemBean(ThemBean themBean) {
        topicsList = themBean.getTopic();
    }

    //连麦房间
    @Override
    public void onCreateConferenceRoom(String roomName) {


    }

    @Override
    public void onCreateRoom(CreateRoomBean createRoomBean, String title) {
        showLoadingDialog();
        this.createRoomBean = createRoomBean;
        Log.i("RayTest","createRoomBean:"+createRoomBean.toString());
        LoginInfo loginInfo = LocalDataManager.getInstance().getLoginInfo();
        Log.i("RayTest","arrayListForSize:"+arrayListForSize.get(0));
        Log.i("RayTest","arrayListForSize:"+arrayListForSize.get(1));
        Intent intent = RoomActivity.createIntent(this, RoomActivity.TYPE_PUBLISH_LIVE, loginInfo.getCurrentRoomNum(), loginInfo.getUserId(),
                PublishFragment.createArgs(mPushAddress, PublishFragment.RTC_ROLE_ANCHOR, loginInfo.getCurrentRoomNum(), this.createRoomBean, arrayListForSize,title));
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        presenter.unsubscribeTasks();
    }

    private void subscribeInitShare(View view,
                                    final int index,
                                    final Platform platform,
                                    final String shareChannel) {
        subscribeClick(view, new Action1<Void>() {

            @Override
            public void call(Void aVoid) {
                if (popupWindowInitShare == null) {
                    @SuppressLint("InflateParams")
                    View contentView = LayoutInflater.from(CreateRoomActivity.this).inflate(R.layout
                            .popup_room_create_share, null);
                    tvInitShareTips = $(contentView, R.id.room_create_tv_share_tip);
                    popupWindowInitShare = new PopupWindow(contentView, ViewGroup.LayoutParams
                            .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                if (popupWindowInitShare.isShowing()) {
                    popupWindowInitShare.dismiss();
                }
                /**
                 * 1.所有的都没被选中：设置点击项selected为true。
                 * 2.有一项选中，现在点击的是另一个：设置之前的为false,现在的为true。
                 * 3.有一项选中，现在又点了这个：设置为false。
                 *
                 * 所以：
                 * Step1 只要有一项选中，直接设置为false。
                 * Step2 接下来判断点击项，如果是自己且之前为选中状态则将选中标记置为-1，表示没有选中任何项；
                 * 否则将点击项置为true且展示popWindow。
                 */
                boolean isSelfSelected = false;

                //Step1
                if (mInitShareIndex != -1) {
//                    listInitShare.get(mInitShareIndex).setSelected(false);
                    isSelfSelected = mInitShareIndex == index;
                }

                //Step2
                // 仅在不相等时才赋值和设置选中，因可以取消所有选项
                if (isSelfSelected) {
                    mInitShareIndex = -1;
                } else {
                    mInitShareIndex = index;
                    mPlatform = platform;

//                    listInitShare.get(mInitShareIndex).setSelected(true);
                    tvInitShareTips.setText(getString(R.string.room_live_create_share_tips,
                            shareChannel));
                    L.i(LOG_TAG, "Showing pop window!");
//                    popupWindowInitShare.showAtLocation(listInitShare.get(mInitShareIndex),
//                            Gravity.TOP, 0, 0);
                    if (mInitShareIndex != -1) {
                        shareHelper.share(mPlatform);
                    }
                }
            }
        });
    }

    protected final void showPopupWindowAboveButton(@NonNull PopupWindow window, @NonNull View
            anchor) {
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


    //  话题
    private PopupWindow mPopupWindow;
    private View popuView;
    private TextView popuClose, popuListNull;
    private RecyclerView popuList;
    private EditText popuET;

    private void startThemPopupWindow() {
        if (mPopupWindow == null) {
            getXuNiDpi();
//            getWindowKeyBrodH();
            LayoutInflater relativeLayout = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            popuView = relativeLayout.inflate(R.layout.start_theme_pop_layout, null);
            popuET = (EditText) popuView.findViewById(R.id.start_theme_et);
            popuClose = (TextView) popuView.findViewById(R.id.start_theme_close);
            popuList = (RecyclerView) popuView.findViewById(R.id.them_list);
            popuListNull = (TextView) popuView.findViewById(R.id.them_empty);
            popuList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            if (topicsList == null) {
                popuList.setVisibility(View.GONE);
                popuListNull.setVisibility(View.VISIBLE);
            } else {
                ThemAdapter adapter = new ThemAdapter(topicsList);
                popuList.setAdapter(adapter);
            }
            mPopupWindow = new PopupWindow(popuView,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            popuClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    closepopwindow(popuView);
                    mPopupWindow.dismiss();
                }
            });
            popuET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (event != null) {
                    }
                    if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                        edtLiveTitle.getText().append("#" + v.getText() + "##");
                        popuET.getText().clear();
                        mPopupWindow.dismiss();
                        return true;
                    }
                    return false;
                }
            });
            // 使其聚集
            mPopupWindow.setFocusable(true);
            // 设置允许在外点击消失
            mPopupWindow.setOutsideTouchable(true);
            // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.setAnimationStyle(R.style.popwin_anim_style);
            if (xunih == 0) {
                mPopupWindow.showAsDropDown(roomToptx, 0, -stateh);
            } else {
                mPopupWindow.showAsDropDown(roomToptx, 0, -(xunih - stateh));
            }
//            startpopwindow(popuView);
        } else {
            if (xunih == 0) {
                mPopupWindow.showAsDropDown(roomToptx, 0, -stateh);
            } else {
                mPopupWindow.showAsDropDown(roomToptx, 0, -(xunih - stateh));
            }
//            startpopwindow(popuView);
        }
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }


    // 话题列表适配器
    private class ThemAdapter extends SimpleRecyclerAdapter<ThemBean.Topic, RecommendHolder> {
        public ThemAdapter(List<ThemBean.Topic> anchorSummaries) {
            super(anchorSummaries);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.start_them_list_item;
        }

        @NonNull
        @Override
        protected RecommendHolder createHolder(View view) {
            return new RecommendHolder(view);
        }
    }

    private class RecommendHolder extends SimpleRecyclerHolder<ThemBean.Topic> {

        private TextView mChatTitile, mChartNumber;

        public RecommendHolder(View itemView) {
            super(itemView);
            mChatTitile = (TextView) itemView.findViewById(R.id.item_them_tv);
            mChartNumber = (TextView) itemView.findViewById(R.id.item_them_number);
        }

        @Override
        public void displayData(final ThemBean.Topic data) {
            mChatTitile.setText(data.getTopic_title());
            mChartNumber.setText(data.getTopic_num() + getString(R.string.unit_live));
            RxView.clicks(itemView)
                    .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            edtLiveTitle.getText().append("#" + data.getTopic_title() + "##");
                            mPopupWindow.dismiss();
                        }
                    });
        }
    }

    private void getXuNiDpi() {
        int dpi = 0;
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            dpi = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        xunih = dpi - getWindowManager().getDefaultDisplay().getHeight();
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        stateh = rect.top;

        Display disp = this.getWindowManager().getDefaultDisplay();
        Point outP = new Point();
        disp.getSize(outP);
    }

    /**
     * 获取私密dialog
     */
    private PrivateTypeDialog privateTypeDialog;

    private void showPrivateTypeDialog() {
        if (privateTypeDialog == null)
            privateTypeDialog = new PrivateTypeDialog();
        privateTypeDialog.show(getFragmentManager(), "privatetype");
    }

    @Override
    public void recoveryCommit() {
        privateString = null;
        privateType = 0;
        privateStartTitle.setText(getString(R.string.room_live_create_private));
    }

    @Override
    public void privateStringSet(String str, int type) {
        privateString = str;
        privateType = type;
        if (type == Integer.valueOf(PrivateSetStringDialog.PRIVTE_ROOM_PWD)) {
            privateStartTitle.setText(getString(R.string.goprivate_room_dialog_pwd));
        } else if (type == Integer.valueOf(PrivateSetStringDialog.PRIVTE_ROOM_LEVEL)) {
            privateStartTitle.setText(getString(R.string.goprivate_room_dialog_level));
        } else if (type == Integer.valueOf(PrivateSetStringDialog.PRIVTE_ROOM_TICKET)) {
            privateStartTitle.setText(getString(R.string.goprivate_room_dialog_ticket));
        } else {
            privateStartTitle.setText(getString(R.string.room_live_create_private));
        }
    }

    // 指定保存的路径：
    public void saveFile(Bitmap bm, String fileName) throws IOException {
        File dirFile = new File(ALBUM_PATH);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        File myCaptureFile = new File(ALBUM_PATH + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(myCaptureFile));
        if (bm != null) {
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

    public void showCameraSize() {
        Camera camera = Camera.open(1);        //  For Front Camera

        android.hardware.Camera.Parameters params1 = camera.getParameters();
        List sizes1 = params1.getSupportedPictureSizes();
        Camera.Size  result1 = null;
        for (int i=0;i<sizes1.size();i++){
            result1 = (Camera.Size) sizes1.get(i);
            if(result1.width>320 && result1.width <800){
                arrayListForSize.add(result1.width);
                arrayListForSize.add(result1.height);
            }
        }
        if(arrayListForSize.size()==0){
            arrayListForSize.add(640);
            arrayListForSize.add(360);
        }
        camera.release();
    }
}

package tw.chiae.inlive.presentation.ui.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

/*import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;*/
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.jakewharton.rxbinding.view.RxView;
import com.mob.tools.utils.UIHandler;
import com.tencent.bugly.crashreport.CrashReport;

import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.Banner;
import tw.chiae.inlive.data.repository.ServerEventResponse;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.login.splash.SplashActivity;
import tw.chiae.inlive.presentation.ui.main.MainActivity;
import tw.chiae.inlive.presentation.ui.main.webview.SimpleWebViewActivity;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.FrescoUtil;
import tw.chiae.inlive.util.L;
import tw.chiae.inlive.util.troubleshoot.AuthException;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.google.GooglePlus;

import rx.functions.Action1;

/**
 * 注意：这里使用了Handler。由于ShareSdk授权方法的回调执行在非UI线程，所以需要使用Handler回调到这边才能处理UI操作。
 * 登录页面
 */
public class LoginSelectActivity extends BaseActivity implements Handler.Callback,
        PlatformActionListener, LoginUiInterface ,GoogleSignInInterface  {

    private ThirdLoginPresenter presenter;

    public static final int MSG_USERID_FOUND = 1;
    public static final int MSG_LOGIN = 2;
    public static final int MSG_AUTH_CANCEL = 3;
    public static final int MSG_AUTH_ERROR = 4;
    public static final int MSG_AUTH_COMPLETE = 5;
    private GoogleSignInPlatform GooglePlatform;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, LoginSelectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    private ImageView mSina, mWechart, mQQ, mPhone, mFaceBook, mTwitter, mLine, mInstagram, mGoogle;
    private TextView mPrivacy, mPrivacys ,mVersion;
    private static int PHONE_REQUEST_CODE = 101;



    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_select_v2;
    }

    @Override
    public void init() {
//        sendFinishBroadcast(LOG_TAG);
        Log.i("RayTest","init");


    }

    private void initparams(){
        presenter = new ThirdLoginPresenter(this);
        setSwipeBackEnable(false);
        LocalDataManager.getInstance().clearLoginInfo();
        //Clear all third authorize
        new Facebook(this).removeAccount(true);
        new GooglePlus(this).removeAccount(true);
        GooglePlatform = new GoogleSignInPlatform(this);
        GooglePlatform.init();
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        Log.i("RayTest","LoginSelectActivity");
//        mSina = (ImageView)findViewById(R.id.login_select_sina);
        mWechart = (ImageView) findViewById(R.id.login_select_wechart);
        mGoogle = (ImageView) findViewById(R.id.login_select_g);
        mQQ = (ImageView) findViewById(R.id.login_select_qq);
        mPhone = (ImageView) findViewById(R.id.login_select_phone);
        mFaceBook = (ImageView) findViewById(R.id.login_select_facebook);
        mTwitter = (ImageView) findViewById(R.id.login_select_twitter);
        mPrivacy = (TextView) findViewById(R.id.login_select_privacy);
        mPrivacys = (TextView) findViewById(R.id.login_select_privacys);
        mPrivacy.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mPrivacys.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mLine = (ImageView) findViewById(R.id.login_select_line);
        mInstagram = (ImageView) findViewById(R.id.login_select_instagram);
        mVersion = (TextView) findViewById(R.id.tv_version_no);

        mPrivacys.setText(getString(R.string.login_select_privacy, getString(R.string.login_name)));
        mPrivacy.setText(getString(R.string.login_select_privacys));

        try {
            String version = getPackageManager().getPackageInfo(getPackageName(),0).versionName;
            mVersion.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void setListeners() {
        initparams();
        super.setListeners();
//        subscribeClick(mWechart, new Action1<Void>() {
//            @Override
//            public void call(Void aVoid) {
//                authorize(new Wechat(LoginSelectActivity.this));
//            }
//        });
        subscribeClick(mGoogle, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                //Platform platform = new GooglePlus(LoginSelectActivity.this);
                GooglePlatform.getUserInfo();

//                platform.removeAccount(true);
                //authorize(GooglePlatform);


            }
        });
        subscribeClick(mFaceBook, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Platform platform = new Facebook(LoginSelectActivity.this);
//                platform.removeAccount(true);
                authorize(platform);
            }
        });

        subscribeClick(mLine, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
            }
        });

/*        subscribeClick(mInstagram, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                authorize(new Instagram(LoginSelectActivity.this));
            }
        });*/
        RxView.clicks(mPhone).throttleFirst(Const.VIEW_THROTTLE_TIME,
                TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent i = LoginActivity.createIntent(LoginSelectActivity.this);
                startActivity(i);
//                startActivity(MediaRecorderActivity.createIntent(LoginSelectActivity.this,"http://kalastar.inlive.tw/songvideo/s001.mp4","http://kalastar.inlive.tw/songvideo/s001.lrc"));
            }
        });

        RxView.clicks(mPrivacy).throttleFirst(Const.VIEW_THROTTLE_TIME,
                TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(SimpleWebViewActivity.createIntent(LoginSelectActivity.this, SourceFactory.wrapPath(getString(R.string.me_login_privacy)),getString(R.string.login_select_privacys)));
            }
        });
        RxView.clicks(mPrivacys).throttleFirst(Const.VIEW_THROTTLE_TIME,
                TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(SimpleWebViewActivity.createIntent(LoginSelectActivity.this, SourceFactory.wrapPath(getString(R.string.me_login_privacys)),getString(R.string.login_select_privacy,"inLive")));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unsubscribeTasks();
    }



    @Override
    public void startActivityAndFinishOthers() {
        presenter.checkServerStat();
    }

    @Override
    public void smsSendsSccess(String s) {

    }

    @Override
    public void onResponseServerEvent(ServerEventResponse<String> response) {

        if(response.getStatus()!=1){
            new AlertDialog.Builder(LoginSelectActivity.this)
                    .setTitle("公告")
                    .setMessage(response.getContent())
                    .setPositiveButton("確定",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Login();
                            if(Const.TEST_ENVIROMENT_SW){
                               presenter.downloadData();
                            }
                            else
                                finish();
                        }
                    }).show();

        }else if(response.getStatus()==1)
        {
            presenter.downloadData();
        }
    }

    private void Login() {
        startActivity(MainActivity.createIntent(LoginSelectActivity.this));
        sendFinishBroadcast(MainActivity.class.getSimpleName());
        //finish();
    }

    @Override
    public void storeBannerImg(List<Banner> banners) {
       /* Log.i("RayTest","banner size:"+ banners.size());
        LocalDataManager.getInstance().saveBanners(banners);
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        for(Banner banner : banners){
            String imgURL = Const.MAIN_HOST_URL+banner.getImageUrl();
            FrescoUtil.CacheImgToDisk(imgURL);
        }
        presenter.getOfficialList();*/
    }

    @Override
    public void CompleteOfficialList() {
        Login();
    }

    @Override
    public void CompleteDownloadBanner(List<String> paths) {
        presenter.getOfficialList();
    }

    private void authorize(final Platform plat) {
        showLoadingDialog();
//        if(plat.isValid()) {
//            String userId = plat.getDb().getUserId();
//            if (!TextUtils.isEmpty(userId)) {
//                UIHandler.sendEmptyMessage(MSG_USERID_FOUND, this);
//                return;
//            }
//        }
        //CheckServerStatus(plat);
        plat.setPlatformActionListener(LoginSelectActivity.this);
        plat.SSOSetting(false);
        plat.showUser(null);

    }

/*    public void CheckServerStatus(final Platform plat) {
        RequestQueue rq = webRequestUtil.getVolleyIntence(this);

        StringRequest sr = new StringRequest(Const.Server_Stat_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response!=null) {
                    try {
                        JSONObject jObj = new JSONObject(response);
                        int status = jObj.getInt("status");
                        Log.i("RayTest","status:"+status);
                        if(status==0){
                            plat.setPlatformActionListener(LoginSelectActivity.this);
                            plat.SSOSetting(false);
                            plat.showUser(null);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        rq.add(sr);
    }*/
    // -----------------  PlatformActionListener BEGIN ----------------
//授权成功
    @Override
    public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
//        if (action==Platform.ACTION_USER_INFOR){
        Log.i("mrl", "MSG_AUTH_COMPLETE" + action);
        //UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, LoginSelectActivity.this);
        presenter.thirdLogin(platform, res);
//        UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, LoginSelectActivity.this);
//        presenter.thirdLogin(platform, res);
//        Log.i("mrl", "MSG_AUTH_COMPLETE" + action);
//        UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, LoginSelectActivity.this);
//        presenter.thirdLogin(platform, res);
//        }
    }

    //登陆错误的状态
    @Override
    public void onError(Platform platform, int action, Throwable throwable) {
//        if (action==Platform.ACTION_USER_INFOR){
        Log.i("mrl", "MSG_AUTH_ERROR" + action);
        UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, LoginSelectActivity.this);
        CrashReport.postCatchedException(new AuthException("Third login exception", throwable));
//        Log.i("mrl", "MSG_AUTH_ERROR" + action);
//        UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, LoginSelectActivity.this);
//        CrashReport.postCatchedException(new AuthException("Third login exception", throwable));
//        UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, LoginSelectActivity.this);
//        CrashReport.postCatchedException(new AuthException("Third login exception", throwable));
//        }
    }

    //取消登陆的状态
    @Override
    public void onCancel(Platform platform, int action) {
//        if (action==Platform.ACTION_USER_INFOR){
        Log.i("mrl", "MSG_AUTH_CANCEL" + action);
        UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, LoginSelectActivity.this);
//        UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, LoginSelectActivity.this);
//        Log.i("mrl", "MSG_AUTH_CANCEL" + action);
//        UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, LoginSelectActivity.this);
//        }
    }

    // -----------------  PlatformActionListener END ----------------

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_USERID_FOUND:
                //native userId information
                Log.i("mrl", "MSG_USERID_FOUND");
                L.e(LOG_TAG, "This branch should never be executed!");
                toastShort(getString(R.string.third_login_local_complete));
                break;
            case MSG_AUTH_COMPLETE:
                dismissLoadingDialog();
                toastShort(getString(R.string.third_login_complete));
                break;
            case MSG_AUTH_CANCEL:
                dismissLoadingDialog();
                toastShort(getString(R.string.third_login_cancel));
                break;
            case MSG_AUTH_ERROR:
                dismissLoadingDialog();
                toastShort(getString(R.string.third_login_error));
                break;
            default:
                break;
        }
        return false;
    }


    @Override
    public void onGoogleLoginComplete(HashMap<String, Object> res) {
        Log.i("mrl", "MSG_AUTH_COMPLETE" );
        //UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, LoginSelectActivity.this);
        GooglePlatform.thirdLogin(res);
    }

    @Override
    public void onGoogleLoginError() {
        Log.i("mrl", "MSG_AUTH_ERROR" );
        UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, LoginSelectActivity.this);
        CrashReport.postCatchedException(new AuthException("Third login exception"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
     /*   if(requestCode==GoogleSignInPresenter.RC_SIGN_IN){
            // pGoogleSignIn.handleSignInResult(data);
            GooglePlatform.handleSignInResult(data);
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(GooglePlatform!=null)
            GooglePlatform.onStart();
    }

    @Override
    public void onGoogleLoginWait() {
        showLoadingDialog();
    }


    @Override
    public void setCoinData(int currencyItemSum) {

    }
}

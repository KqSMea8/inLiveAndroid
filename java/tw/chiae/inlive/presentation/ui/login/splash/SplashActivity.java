package tw.chiae.inlive.presentation.ui.login.splash;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

/*import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;*/
import com.androidquery.AQuery;
import com.androidquery.service.MarketService;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import me.relex.circleindicator.CircleIndicator;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.Banner;
import tw.chiae.inlive.data.repository.ParamsRemoteResponse;
import tw.chiae.inlive.data.repository.ServerEventResponse;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.login.LoginSelectActivity;
import tw.chiae.inlive.presentation.ui.main.MainActivity;
import tw.chiae.inlive.presentation.ui.main.setting.CreateViewDialogFragment;
import tw.chiae.inlive.presentation.ui.room.RoomInfoTmp;
import tw.chiae.inlive.util.CETracking;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.PermissionsChecker;
import tw.chiae.inlive.util.Spans;

import static android.R.attr.version;


/**
 * @author Muyangmin
 * @since 1.0.0
 * 欢迎页面
 */
public class SplashActivity extends BaseActivity implements SplashUiInterface {

    private static final int MAX_SHOW_TIME = 3000;
    private final ArrayList<Integer> mImgIds = new ArrayList<>();
    private static final int REQUEST_CODE = 0; // 请求码
    private int DebugModeClick = 0;

    private final Runnable mRunnableOnTimeout = new Runnable() {
        @Override
        public void run() {
            Log.i("RayTest","login timeout");
            startLoginSelectActivity();
        }
    };

    private SplashPresenter presenter;

    //    第一次登陆哒
    private View rlFistTime;
    private View rlNormal;
    private ViewPager mViewPager;
    private CircleIndicator mPageIndicator;
    private static Handler mHandler;
    private SimpleDraweeView mSimpleDraweeView;
    /*private RequestQueue rq*/
            ;
    private String SERVER_CHECK="ServerCheck";
    private TextView tvMsg;
    private CreateViewDialogFragment dialogFragment;
    private ModifyDialogFragment DebugdialogFragment;
    //private boolean onPauseGotoStore = false;
    private String mDebugVersion = "";
    private VersionChecker checker;
    private boolean isEnableDebugMode = false;
    private String version_local;
    private boolean isDialogCheck = false;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {

        rlFistTime = $(R.id.splash_rl_first_time);
        rlNormal = $(R.id.splash_rl_normal);
        mViewPager = $(R.id.splash_view_pager);
        mPageIndicator = $(R.id.splash_circle_indicator);
        mSimpleDraweeView = $(R.id.splash_img_normal);
        tvMsg = $(R.id.tv_login_msg);

    }

    @Override
    protected void init() {
        setSwipeBackEnable(false);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        mImgIds.add(R.drawable.login_bg);

        CETracking.getInstance().onAppStart(getApplication(), "tw.inlive", "http://api2.inlive.tw/api2/");
        mHandler = new Handler();
        presenter = new SplashPresenter(getApplicationContext(),this);
        showServerMsg();
        presenter.initParams();
    /*    dialogFragment = CreateViewDialogFragment.newInstance();
        dialogFragment.setDialogCallback(this);
        DebugdialogFragment = ModifyDialogFragment.newInstance();
        DebugdialogFragment.setDebugCallback(this);*/
        //presenter.downloadImg();
        initPresenterComplete();
        mSimpleDraweeView.setImageURI(Uri.parse(("res:///" + R.drawable.login_bg)));
        mSimpleDraweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!RoomInfoTmp.isDialogShow && isDialogCheck)
                    showVersionInfo();
            }
        });
        //CheckPermissions();

    }



    private void showDebugMode(final int MaxNumber) {
        tvMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEnableDebugMode){
                    Log.i("RayTest","isEnableDebugMode");
                    DebugdialogFragment.showDebugMsg(getSupportFragmentManager(),"修改模式","目前版本為"+version_local,ModifyDialogFragment.TYPE_DEBUG_NORMAL);
                }else{
                    if(DebugModeClick>3){
                        toastShort("你已經點擊"+DebugModeClick+"次 , 請再點擊"+(MaxNumber-DebugModeClick)+"次進入修改模式");
                    }

                    if(DebugModeClick>5) {
                        isEnableDebugMode = true;
                        DebugModeClick = 5;
                        PackageManager manager = getPackageManager();

                        PackageInfo info = null;
                        try {
                            info = manager.getPackageInfo(getPackageName(), 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        String version="null";
                        if(info!=null)
                            version = info.versionName;


                        DebugdialogFragment.showDebugMsg(getSupportFragmentManager(),"修改模式","目前版本為"+version,ModifyDialogFragment.TYPE_DEBUG_NORMAL);
                    }
                    else
                        DebugModeClick+=1;
                }



            }
        });
    }

    private int getRandomNumber() {
        return (int)Math.random()*1000+10000;
    }


    private void showServerMsg() {
        if(Const.getToast()==1){
            if(Const.TEST_ENVIROMENT_SW)
                toastShort("已登入測試伺服器");
            else
                toastShort("已登入正式伺服器");
        }

    }


    private void Login(){
        showVersionInfo();
    }
    private void completeDataLogin(){
        if(LocalDataManager.getInstance().getLoginInfo()==null)
            Log.i("RayTest","getLoginInfo null");
        mHandler.removeCallbacks(mRunnableOnTimeout);
        if(LocalDataManager.getInstance().getLoginInfo()==null)
            startLoginSelectActivity();
        else{

            Log.i("RayTest","user id ====== "+LocalDataManager.getInstance().getLoginInfo().getUserId());
            if(Const.isNewUIMode)
                startActivity(MainActivity.createIntent(this));
            else
                startActivity(MainActivity.createIntent(this));
            finish();
        }

    }

    @Override
    public void startLoginSelectActivity() {
        Log.i("RayTest","startLoginSelectActivity");
        mHandler.removeCallbacks(mRunnableOnTimeout);
        startActivity(LoginSelectActivity.createIntent(this));
        finish();
    }

    @Override
    public void startMainActivity() {
        presenter.downloadImg();
        //completeDataLogin();
    }

    @Override
    public void onResponseServerEvent(ServerEventResponse<String> response) {
        if(response.getStatus()!=1){
            new AlertDialog.Builder(SplashActivity.this)
                    .setTitle("公告")
                    .setMessage(response.getContent())
                    .setPositiveButton("確定",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Login();
                            if(Const.TEST_ENVIROMENT_SW){
                                Login();
                                }
                            else
                                finish();
                        }
                    }).show();

        }else if(response.getStatus()==1)
            {
                Login();
            }
    }

    @Override
    public void storeBannerImg(List<Banner> banners) {
        /*Log.i("RayTest","banner size:"+ banners.size());
        LocalDataManager.getInstance().saveBanners(banners);
        for(Banner banner : banners){
            String imgURL = Const.MAIN_HOST_URL+banner.getImageUrl();
            FrescoUtil.CacheImgToDisk(imgURL);
        }*/

    }

    @Override
    public void CompleteOfficialList() {
        //presenter.checkServerStat();
       // completeDataLogin();
        presenter.getMainAccountInfo();

    }

    @Override
    public void showLoadingText(String s) {
        Log.i("RayTest",s);
        tvMsg.setText(s+" ");
    }

    @Override
    public void CompleteDownloadBanner(List<String> paths) {
        presenter.getOfficialList();
    }

    @Override
    public void isModifyParams() {
        //presenter.checkServerStat();
        initPresenterComplete();
        Log.i("RayTest","Cost:"+Const.MAIN_HOST_URL);
    }

    @Override
    public void showMsg(String str) {
        toastShort(str);
    }

    @Override
    public void initPresenterComplete() {

//        mImgIds.add(R.drawable.img_splash_02);


       /* rq = Volley.newRequestQueue(this);*/


        //Login();
        //Login();
        Log.i("RayTest",""+Const.Server_Stat_URL);
        if(presenter==null){
            Log.i("RayTest","presenter null");
        }
        presenter.checkServerStat();
    }

    @Override
    public void CompleteMainAccountList() {
        mHandler.postDelayed(mRunnableOnTimeout, MAX_SHOW_TIME);
        presenter.getblacklist();

    }

    @Override
    public void failLogin() {
        startLoginSelectActivity();
    }

    @Override
    public void CompleteBlackList() {
        completeDataLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RoomInfoTmp.SHOW_UPDATE_SW = false;
        Log.i("RayTest",getClass().getSimpleName()+ " onResume ");

        if(checker!=null && !RoomInfoTmp.isDialogShow){
            Log.i("RayTest","isCheckingVersion2: "+RoomInfoTmp.isDialogShow);
            showVersionInfo();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unsubscribeTasks();
        cancelRequest();
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }
    public void showVersionInfo() {

        checker = new VersionChecker(getApplicationContext(),getSupportFragmentManager());

        checker.getVersionName(new VersionChecker.VersionCheckInterface() {
            @Override
            public void onVersionSucess(boolean onSucess) {
                Log.i("RayTest","onSucess: "+onSucess);
                isDialogCheck = true;
                if(onSucess)
                    LoginPrepare();
            }
    
            @Override
            public void onVersionFail() {
        
            }
        });

    }


    public  void  cancelRequest(){
        try {
  /*          rq.cancelAll(SERVER_CHECK);

            //webRequestUtil.cancelRequestQueue();
            rq = null;*/

        }catch (Exception e){

        }
    }

/*
    @Override
    public void onOKDialogcheck(Bundle bundle) {
        int itype = bundle.getInt("type");
        switch (itype){
            case CreateViewDialogFragment.TYPE_SHOW_UPDATE:
            case CreateViewDialogFragment.TYPE_NEED_FOUCSE_UPDATE:
                gotoStoreDownload();
                break;


            default:
                LoginPrepare();
                break;


        }

    }*/

/*    private void gotoStoreDownload() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        onPauseGotoStore = true;
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }*/

/*    @Override
    public void onCancelDialogcheck(Bundle bundle) {
        int itype = bundle.getInt("type");
        switch (itype){
            default:
                LoginPrepare();
                break;


        }
    }*/

    private void LoginPrepare() {
        Log.i("RayTest",getClass().getSimpleName()+" Login...");
        if (presenter.isFirstRun()) {
            rlFistTime.setVisibility(View.VISIBLE);
            rlNormal.setVisibility(View.GONE);
            if (mImgIds.size()<2){
                mPageIndicator.setVisibility(View.GONE);
            }

            mViewPager.setAdapter(new SplashPagerAdapter(mImgIds,
                    new SplashPagerAdapter.SplashBtnListener() {
                        @Override
                        public void onStartBtnClicked() {

                            presenter.performFirstStartNavigation(SplashActivity.this);
                        }
                    }));
            mPageIndicator.setViewPager(mViewPager);
        } else {
            rlNormal.setVisibility(View.VISIBLE);
            rlFistTime.setVisibility(View.GONE);
            //mSimpleDraweeView.setImageURI(Uri.parse(("res:///" + R.drawable.login_bg)));
            /*mHandler.postDelayed(mRunnableOnTimeout, MAX_SHOW_TIME);*/
            presenter.autoLogin();
        }
    }
/*
    @Override
    public void onClickOK(String s) {

    }

    @Override
    public void onCancel() {
        showVersionInfo();
    }*/
}

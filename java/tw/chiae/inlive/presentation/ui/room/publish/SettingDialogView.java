package tw.chiae.inlive.presentation.ui.room.publish;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.room.LiveRoomEndInfo;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.domain.AnchorManager;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;
import tw.chiae.inlive.presentation.ui.room.RoomActivity;
import tw.chiae.inlive.presentation.ui.room.RoomInfoTmp;
import tw.chiae.inlive.presentation.ui.room.create.CreateRoomShareHelper;
import tw.chiae.inlive.presentation.ui.widget.SmartCameraView;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.Spans;
import tw.chiae.inlive.util.share.ShareHelper;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 直播间关闭时的显示页面，提供直播间信息的显示、秀币和人数的显示，及关注主播的功能。
 * Created by huanzhang on 2016/5/5.
 */
public class SettingDialogView extends Dialog implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {


    private SmartCameraView mMainView;
    private Context mContext;
    private SharedPreferences mSetting;
    private boolean mEncode;
    private PublishFragment mFragment;
    private String mflag;
    private ImageView img_bg;
    private Button bt_save;
    private RadioGroup group_kbps;
    private RadioGroup group_beautiful;
    private int config_kbps;
    private int config_beauty;
    private TextView tv_encode;
    private RadioGroup group_beautiful_1,group_beautiful_2,group_beautiful_3;
    private Button bt_cancel;
    private Button bt_speed;
    //private WebView web_trace;
    private java.lang.String url="http://www.wangsutong.com/wstCeba/cdnupload/detect!test.action?taskId=e813e77b34814fc28eeacbae64542a89";

    public SettingDialogView(Context context) {
        super(context, R.style.DialogStyle);
        this.mContext = context;

    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    private void setView() {
        bt_save.setOnClickListener(this);
        bt_cancel.setOnClickListener(this);
        bt_speed.setOnClickListener(this);

       /* web_trace.getSettings().setJavaScriptEnabled(true);
        web_trace.getSettings().setSupportZoom(true);       //Zoom Control on web (You don't need this
        //if ROM supports Multi-Touch
        web_trace.getSettings().setBuiltInZoomControls(true);
        web_trace.setWebViewClient(new MyWebViewClient());
        WebSettings settings = web_trace.getSettings();
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        if(width > 650)
        {
            this.web_trace.setInitialScale(190);
        }else if(width > 520)
        {
            this.web_trace.setInitialScale(160);
        }else if(width > 450)
        {
            this.web_trace.setInitialScale(140);
        }else if(width > 300)
        {
            this.web_trace.setInitialScale(120);
        }else
        {
            this.web_trace.setInitialScale(100);
        }
*/


        RadioButton mKbpsButton,mBeautyButton;
        Log.i("RayTest","config_kbps:"+config_kbps);
        switch (config_kbps){
            case 1200:
                mKbpsButton = (RadioButton) findViewById(R.id.kbps_1200);
                break;
            case 1700:
                mKbpsButton = (RadioButton) findViewById(R.id.kbps_1700);
                break;
            case 2000:
                mKbpsButton = (RadioButton) findViewById(R.id.kbps_2000);
                break;
            case 2400:
                mKbpsButton = (RadioButton) findViewById(R.id.kbps_2400);
                break;
            default:
                mKbpsButton = (RadioButton) findViewById(R.id.kbps_1200);
                break;
        }
        switch (config_beauty){
            case 0:
                mBeautyButton = (RadioButton) findViewById(R.id.beautiful_none);
                break;
            case 1:
                mBeautyButton = (RadioButton) findViewById(R.id.beautiful_normal);
                break;
            case 2:
                mBeautyButton = (RadioButton) findViewById(R.id.beautiful_cool);
                break;
            case 3:
                mBeautyButton = (RadioButton) findViewById(R.id.beautiful_sakura);
                break;
            case 4:
                mBeautyButton = (RadioButton) findViewById(R.id.beautiful_romance);
                break;
            case 5:
                mBeautyButton = (RadioButton) findViewById(R.id.beautiful_warm);
                break;
            case 6:
                mBeautyButton = (RadioButton) findViewById(R.id.beautiful_healthy);
                break;
            case 7:
                mBeautyButton = (RadioButton) findViewById(R.id.beautiful_antique);
                break;
            case 8:
                mBeautyButton = (RadioButton) findViewById(R.id.beautiful_fairytale);
                break;

            default:
                mBeautyButton = (RadioButton) findViewById(R.id.beautiful_none);
                break;
        }
        String str ;
        if(mEncode){
            str = "此手機支援 硬編碼！";
            group_beautiful.setEnabled(true);
            group_kbps.setEnabled(true);
        }else {
            str = "此手機支援 不支援硬編碼 , 使用軟編碼！";
            group_beautiful.setEnabled(false);
            group_kbps.setEnabled(false);
        }
        tv_encode.setText(str);
        mKbpsButton.setChecked(true);
        mBeautyButton.setChecked(true);
        group_beautiful_1.setOnCheckedChangeListener(this);
        group_beautiful_2.setOnCheckedChangeListener(this);
        group_beautiful_3.setOnCheckedChangeListener(this);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_room_publish_setting);
        initView();
    }

    private void initView() {
        //setCancelable(false);
        tv_encode = (TextView)findViewById(R.id.setting_encode_support);
        img_bg=(ImageView)findViewById(R.id.setting_dialog_bg);
        bt_save = (Button)findViewById(R.id.setting_save_finish);
        bt_cancel = (Button)findViewById(R.id.bt_dialog_cancel);
        bt_speed = (Button)findViewById(R.id.bt_goto_trace_speed);

        //web_trace = (WebView)findViewById(R.id.setting_web_view);
        group_kbps = (RadioGroup)findViewById(R.id.kbps_group);
        group_beautiful = (RadioGroup) findViewById(R.id.beautiful_group);
        group_beautiful_1 = (RadioGroup) findViewById(R.id.beautiful_group_1);
        group_beautiful_2 = (RadioGroup) findViewById(R.id.beautiful_group_2);
        group_beautiful_3 = (RadioGroup) findViewById(R.id.beautiful_group_3);
        config_kbps = mSetting.getInt("kbps",-1);
        config_beauty = mSetting.getInt("beauty",-1);
        setView();
    }

    @Override
    public void onClick(View v) {
        Log.i("RayTest","onClick");
        switch (v.getId()){

            case R.id.setting_save_finish:
                saveData();
                //web_trace.loadUrl(null);
                this.dismiss();
                break;

            case R.id.bt_dialog_cancel:
                this.dismiss();
                break;
            case R.id.bt_goto_trace_speed:
                //web_trace.loadUrl(url);
                break;
            default:
                break;
        }
    }

    private void saveData() {
        int kbpsIndex = getCheckVal(group_kbps);
        int Index_1 = getCheckVal(group_beautiful_1);
        int Index_2 = getCheckVal(group_beautiful_2);
        int Index_3 = getCheckVal(group_beautiful_3);

        Log.i("RayTest","Kbps:"+kbpsIndex);

        int beautifulIndex = -1;
        if(Index_1>=0)
            beautifulIndex = Index_1;
        if(Index_2>=0)
            beautifulIndex = Index_2+3;
        if(Index_3>=0)
            beautifulIndex = Index_3+6;
        Log.i("RayTest","beautiful:"+beautifulIndex);
        Log.i("RayTest","flag:"+mflag);

        switch (kbpsIndex){
            case 0:
                config_kbps = 1200;
                break;
            case 1:
                config_kbps = 1700;
                break;
            case 2:
                config_kbps = 2000;
                break;
            case 3:
                config_kbps = 2400;
                break;
            default:
                config_kbps = 1200;
                break;
        }

        switch (beautifulIndex){
            case 0:
                config_beauty = 0;
                break;
            case 1:
                config_beauty = 1;
                break;
            case 2:
                config_beauty = 2;
                break;
            case 3:
                config_beauty = 3;
                break;
            case 4:
                config_beauty = 4;
                break;
            case 5:
                config_beauty = 5;
                break;
            case 6:
                config_beauty = 6;
                break;
            case 7:
                config_beauty = 7;
                break;
            case 8:
                config_beauty = 8;
                break;
            default:
                config_beauty = 0;
                break;
        }

        Log.i("RayTest","kbps save:"+config_kbps);
        Log.i("RayTest","beauty save:"+config_beauty);
        mSetting = mContext.getSharedPreferences(mflag,0);
        SharedPreferences.Editor oEdit = mSetting.edit();
        oEdit.putInt("beauty",config_beauty);
        oEdit.putInt("kbps",config_kbps);
        oEdit.commit();
        readPublishConfig();
        mFragment.restartPublish();

    }

    private void readPublishConfig() {
        String setting_flag = "setting_publish_config_" + LocalDataManager.getInstance().getLoginInfo().getUserId();
        SharedPreferences settings = getContext().getSharedPreferences(setting_flag, 0);
        config_kbps = settings.getInt("kbps",-1);
        config_beauty = settings.getInt("beauty",-1);

        android.util.Log.i("RayTest","config_kbps:"+config_kbps);
        android.util.Log.i("RayTest","config_beauty:"+config_beauty);

    }

    private int getCheckVal(RadioGroup group) {

        int checkedId = group.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(checkedId);
        return group.indexOfChild(radioButton);

    }


    public void setConfig(Context context, SmartCameraView mSmartCameraView, PublishFragment fragment, SharedPreferences settings, boolean config_support_hw_encode, String setting_flag) {
        this.mMainView = mSmartCameraView;
        this.mContext = context;
        this.mSetting = settings;
        this.mEncode = config_support_hw_encode;
        this.mFragment = fragment;
        this.mflag = setting_flag;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        Log.i("RayTest","onCheckedChanged ");
        if(group==group_beautiful_1) {
            Log.i("RayTest","group_beautiful_1 change");
            group_beautiful_2.setOnCheckedChangeListener(null);
            group_beautiful_3.setOnCheckedChangeListener(null);
            group_beautiful_2.clearCheck();
            group_beautiful_3.clearCheck();
            group_beautiful_2.setOnCheckedChangeListener(this);
            group_beautiful_3.setOnCheckedChangeListener(this);
        }
        if(group==group_beautiful_2) {
            Log.i("RayTest","group_beautiful_2 change");
            group_beautiful_1.setOnCheckedChangeListener(null);
            group_beautiful_3.setOnCheckedChangeListener(null);
            group_beautiful_1.clearCheck();
            group_beautiful_3.clearCheck();
            group_beautiful_1.setOnCheckedChangeListener(this);
            group_beautiful_3.setOnCheckedChangeListener(this);
        }
        if(group==group_beautiful_3) {
            Log.i("RayTest","group_beautiful_3 change");
            group_beautiful_1.setOnCheckedChangeListener(null);
            group_beautiful_2.setOnCheckedChangeListener(null);
            group_beautiful_1.clearCheck();
            group_beautiful_2.clearCheck();
            group_beautiful_1.setOnCheckedChangeListener(this);
            group_beautiful_2.setOnCheckedChangeListener(this);
        }

    }
}
package tw.chiae.inlive.presentation.ui.login;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.jakewharton.rxbinding.view.RxView;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.Banner;
import tw.chiae.inlive.data.repository.ServerEventResponse;
import tw.chiae.inlive.domain.BlackList;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.base.MessageReceiver;
import tw.chiae.inlive.presentation.ui.login.country.CountryActivity;
import tw.chiae.inlive.presentation.ui.login.splash.SplashActivity;
import tw.chiae.inlive.presentation.ui.main.MainActivity;
import tw.chiae.inlive.util.Const;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.functions.Action1;
import rx.functions.Func1;
import tw.chiae.inlive.util.FrescoUtil;

//手机登录页面
public class LoginActivity extends BaseActivity implements LoginUiInterface {
    private EditText mName, mPass, mCaptcha,mCountryCode;
//                                        获取验证码的按钮
    private Button mLogin, mRegister, mGetCaptcha;
    private CountDownTimer countDownTimer;

    private LoginPresenter presenter;
    private RelativeLayout mCountryLayout;
    private TextView mCountryName;
    public final static int COUNTRY_CODE_REQUEST=0x86;
    public final static String COUNTRY_CODE="countryCode";
    public final static String COUNTRY_NAME="countryName";
    private CharSequence oldCode;
    private MessageReceiver SMSCapture;
    private static final String SMS_Action="android.provider.Telephony.SMS_RECEIVED";
    private boolean isAutoCapture = false;
    private ComponentName componentName;
    private PackageManager pm;
    private boolean isAutoMode = false;

    public static Intent createIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {

        mName = (EditText) findViewById(R.id.login_name);
        mPass = (EditText) findViewById(R.id.login_pass);
        mCaptcha = (EditText) findViewById(R.id.login_captcha);
        mGetCaptcha = (Button) findViewById(R.id.login_captcha_countdown);
        mLogin = (Button) findViewById(R.id.login_btn);
        mRegister = (Button) findViewById(R.id.login_register_btn);
        mCountryLayout= (RelativeLayout) findViewById(R.id.login_country_layout);
        mCountryName= (TextView) findViewById(R.id.login_country_tx);
        mCountryCode= (EditText) findViewById(R.id.login_country_code);
        mName.setInputType(InputType.TYPE_CLASS_NUMBER);
        mCountryName.setText(getString(R.string.login_phone_default));
        mCountryCode.setText("886");
//        listenInput();

    }

    @Override
    protected void setListeners() {
        super.setListeners();
        //AutoCaptureSMS();
//        listenInput();
//        mName.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                listenInput();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//        mPass.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                listenInput();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
        RxView.clicks(mGetCaptcha)
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .filter(new Func1<Void, Boolean>() {
                    @Override
                    public Boolean call(Void aVoid) {
                        if (TextUtils.isEmpty(mCountryCode.getText())){
                            toastShort(getString(R.string.login_country_errorcode));
                            return Boolean.FALSE;
                        } else if (TextUtils.isEmpty(mName.getText())){
                            toastShort(getString(R.string.phone_login_nullnumber));
                            return Boolean.FALSE;
                        }
//                        else if (!isMobileNO(mName.getText().toString())){
//                            toastShort(getString(R.string.phone_login_badnumber));
//                            return Boolean.FALSE;
//                        }
                        return countDownTimer == null;
                    }
                })
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        String NameTmp = mName.getText().toString().substring(0,4);
                        if(NameTmp.contains("9999")) {
                            isAutoMode = true;
                            mName.setText(mName.getText().toString().trim().substring(4));
                        }else{
                            isAutoMode = false;
                        }
                        String phone = mName.getText().toString().trim();
                        String code=mCountryCode.getText().toString().trim();
                        presenter.sendCaptcha(code+phone);
                        mCaptcha.requestFocus();
                        startListenerSMS();
                        countDownTimer = new CountDownTimer(60000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                mGetCaptcha.setText(getString(R.string.login_captcha_countdown,
                                        millisUntilFinished / 1000));

                            }

                            @Override
                            public void onFinish() {
                                mGetCaptcha.setText(R.string.login_captcha_get);
                                countDownTimer = null;
                                removeReceiver();

                            }
                        }.start();
                    }
                });


        RxView.clicks(mLogin).throttleFirst(Const.VIEW_THROTTLE_TIME,
                TimeUnit.MILLISECONDS)
                .filter(new Func1<Void, Boolean>() {
                    @Override
                    public Boolean call(Void aVoid) {
                        if (TextUtils.isEmpty(mName.getText())||TextUtils.isEmpty(mCountryCode.getText().toString())) {
                            toastShort(getString(R.string.phone_login_badnumber));
                            return Boolean.FALSE;
                        }
                        if (TextUtils.isEmpty(mCaptcha.getText())) {
                            toastShort(getString(R.string.phone_login_nullcode));
                            return Boolean.FALSE;
                        }
                        return Boolean.TRUE;
                    }
                })
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        String name = mName.getText().toString().trim();
//                        String pwd = mPass.getText().toString().trim();
                        String captcha = mCaptcha.getText().toString().trim();
                        presenter.loginByCaptcha(mCountryCode.getText().toString().trim()+mName.getText().toString().trim(), captcha);
                    }
                });
        //老旧的登录已经飞起
        RxView.clicks(mRegister).throttleFirst(Const.VIEW_THROTTLE_TIME,
                TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent i = RegisterActivity.createIntent(LoginActivity.this);
                startActivity(i);
            }
        });

        RxView.clicks(mCountryLayout).throttleFirst(Const.VIEW_THROTTLE_TIME,
                TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                //国家筛选
                startActivityForResult(CountryActivity.createIntent(LoginActivity.this),COUNTRY_CODE_REQUEST);
            }
        });

        mCountryCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldCode=s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void startListenerSMS() {
        Log.i("RayTest","startListenerSMS");
        if(isAutoMode)
            AutoCaptureSMS();
        /*pm = this.getPackageManager();
        componentName = new ComponentName(this, MessageReceiver.class);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);*/
    }

    private void removeReceiver() {
        Log.i("RayTest","removeReceiver");
        if(SMSCapture!=null ){
            isAutoCapture = false;
            try {
                LoginActivity.this.unregisterReceiver(SMSCapture);
            }catch (IllegalArgumentException e){

            }
            SMSCapture = null;
        }

        if(componentName!=null){
            Log.i("RayTest","setComponentEnabledSetting");
            pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
    }

    private void AutoCaptureSMS() {
        Log.i("RayTest","AutoCaptureSMS");
        SMSCapture = new MessageReceiver();
        SMSCapture.setSMSListener(new MessageReceiver.SMSListener() {
            @Override
            public void sendMsg(String newMsg) {
                SMSCapture.removeListener(this);
                if(Integer.parseInt(newMsg)>0){
                    mCaptcha.setText(newMsg);
                    toastShort("驗證碼："+newMsg);
                }
            }
        });
        IntentFilter intentFilter= new IntentFilter();
        intentFilter.addAction(SMS_Action);
        if(!isAutoCapture){
            isAutoCapture = true;
            Log.i("RayTest","registerReceiver");
            this.registerReceiver(SMSCapture, intentFilter);
        }

    }

    @Override
    protected void init() {

        setSwipeBackEnable(false);
        presenter = new LoginPresenter(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeReceiver();
        presenter.unsubscribeTasks();
    }

    //    private void listenInput() {
//        if (TextUtils.isEmpty(mName.getText()) || TextUtils.isEmpty(mPass.getText())) {
//            mLogin.setEnabled(false);
//        } else {
//            mLogin.setEnabled(true);
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case COUNTRY_CODE_REQUEST:
                if (data==null){
                    break;
                }
                if (data.getStringExtra(COUNTRY_CODE)!=null){
                    mCountryCode.setText(data.getStringExtra(COUNTRY_CODE));
                }
                if (data.getStringExtra(COUNTRY_NAME)!=null) {
                    mCountryName.setText(data.getStringExtra(COUNTRY_NAME));
                }
                mName.setFocusable(true);
                mName.setFocusableInTouchMode(true);
                mName.requestFocus();
                mName.findFocus();
                break;
        }
    }

    @Override
    public void startActivityAndFinishOthers() {
        presenter.checkServerStat();

    }

    @Override
    public void smsSendsSccess(String s) {
        toastShort(s);
    }

    @Override
    public void onResponseServerEvent(ServerEventResponse<String> response) {
        if(response.getStatus()!=1){
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("公告")
                    .setMessage(response.getContent())
                    .setPositiveButton("確定",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Login();
                            if(Const.TEST_ENVIROMENT_SW){
                               presenter.downloadImg();
                            }
                            else
                                finish();
                        }
                    }).show();

        }else if(response.getStatus()==1)
        {
            presenter.downloadImg();
        }
    }

    private void Login() {
        startActivity(MainActivity.createIntent(LoginActivity.this));
        sendFinishBroadcast(MainActivity.class.getSimpleName());
    }

    @Override
    public void storeBannerImg(List<Banner> banners) {
     /*   Log.i("RayTest","banner size:"+ banners.size());
        LocalDataManager.getInstance().saveBanners(banners);
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        for(Banner banner : banners){
            String imgURL = Const.MAIN_HOST_URL+banner.getImageUrl();
            FrescoUtil.CacheImgToDisk(imgURL);
        }*/
        //presenter.getOfficialList();
    }

    @Override
    public void CompleteOfficialList() {
        Login();
    }

    @Override
    public void CompleteDownloadBanner(List<String> paths) {
        presenter.getOfficialList();
    }

    public boolean isMobileNO(String mobiles){
        Pattern p = Pattern.compile("^1[3|4|5|7|8]\\d{9}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }



}

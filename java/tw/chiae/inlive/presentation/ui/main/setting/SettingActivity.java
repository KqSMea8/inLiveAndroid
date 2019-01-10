package tw.chiae.inlive.presentation.ui.main.setting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;

import tw.chiae.inlive.BeautyLiveApplication;
import tw.chiae.inlive.BuildConfig;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.data.bean.UpDataBean;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.chatting.utils.ConfigSharePreference;
import tw.chiae.inlive.presentation.ui.chatting.utils.FileHelper;
import tw.chiae.inlive.presentation.ui.chatting.utils.SharePreferenceManager;
import tw.chiae.inlive.presentation.ui.login.LoginSelectActivity;
import tw.chiae.inlive.presentation.ui.login.splash.SplashActivity;
import tw.chiae.inlive.presentation.ui.main.me.AuthenticationActivity;
import tw.chiae.inlive.presentation.ui.main.webview.SimpleWebViewActivity;
import tw.chiae.inlive.presentation.ui.widget.MessageDialog;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.DataCleanManager;
import tw.chiae.inlive.util.DownLoadUtil;
import tw.chiae.inlive.util.L;
import tw.chiae.inlive.util.Packages;


import tw.chiae.inlive.util.PixelUtil;

import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import rx.functions.Action1;

/**
 * @author Muyangmin
 * @since 1.0.0
 * 设置
 */
public class SettingActivity extends BaseActivity implements SettingInterface {

    public static final String RECOMMEND = "tuijianren";
    private RelativeLayout rl_evn_switch;

    public static Intent createIntent(Context context, String recommendation) {
        return new Intent(context, SettingActivity.class).putExtra(RECOMMEND, recommendation);
    }

    private TextView tvAbout;
    private TextView tvVersion;
    private TextView tvCacheSize;
    private TextView tvUpVersion;
    private ProgressBar prgClearCache;
    private TextView tvOutLogin;
    private String myPlayPath;
    private SettingPresenter presenter;
    private TextView mRecommendation;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        presenter = new SettingPresenter(this);
        presenter.loadMyAddress(LocalDataManager.getInstance().getLoginInfo().getCurrentRoomNum());
        subscribeFeatureStub(R.id.setting_rl_account);
        subscribeFeatureStub(R.id.setting_rl_black_list);
//        subscribeFeatureStub(R.id.setting_rl_clear_cache);
        subscribeFeatureStub(R.id.setting_rl_push_manage);

        tvAbout = $(R.id.setting_tv_about);
        tvVersion = $(R.id.setting_tv_version);
        tvUpVersion = (TextView) findViewById(R.id.setting_tv_upversion);
        tvCacheSize = $(R.id.setting_tv_cache_size);
        prgClearCache = $(R.id.setting_prg_clear_cache);
        tvOutLogin = $(R.id.setting_tv_logout);
        rl_evn_switch = $(R.id.setting_rl_evn_switch);
        LoginInfo info = LocalDataManager.getInstance().getLoginInfo();
        if((info.getApproveid().contains("官方"))||(info.getUserId().equals("1000518"))||(info.getUserId().equals("1000787"))){
            rl_evn_switch.setVisibility(View.VISIBLE);
        }else
            rl_evn_switch.setVisibility(View.GONE);
        mRecommendation = $(R.id.setting_invitation_name);

        subscribeClick(R.id.setting_rl_feedback, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(SimpleWebViewActivity.createIntent(SettingActivity.this,
                        getString(R.string.setting_feedback_url),""));
            }
        });
        subscribeClick(R.id.setting_rl_about, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startActivity(SimpleWebViewActivity.createIntent(SettingActivity.this,
                        getString(R.string.setting_about_url),""));
            }
        });
        subscribeClick(R.id.setting_rl_black_list, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                Intent i = new Intent(SettingActivity.this, BlacklistActivity.class);
                startActivity(i);
            }
        });
        subscribeClick(R.id.setting_rl_upversion, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
//                更新
//                toastShort(getString(R.string.setting_updata_download));
                presenter.upNewAppVersion("1");
            }
        });

        subscribeClick(R.id.tv_env_test, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                UserInfo info = JMessageClient.getMyInfo();
                if (null != info) {
                    File file = info.getAvatarFile();
                    if (file != null && file.isFile()) {
                    } else {
                        String path = FileHelper.getUserAvatarPath(info.getUserName());
                        file = new File(path);
                        if (file.exists()) {
                        }
                        SharePreferenceManager.setCachedUsername(info.getUserName());
                        SharePreferenceManager.setCachedAvatarPath(file.getAbsolutePath());
                    }
                }
                JMessageClient.logout();
                ConfigSharePreference.saveEnviroment(true);
                toastShort("切換成測試環境 , 請完全關閉APP重新啟動");

            }
        });

        subscribeClick(R.id.tv_env_release, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                UserInfo info = JMessageClient.getMyInfo();
                if (null != info) {
                    File file = info.getAvatarFile();
                    if (file != null && file.isFile()) {
                    } else {
                        String path = FileHelper.getUserAvatarPath(info.getUserName());
                        file = new File(path);
                        if (file.exists()) {
                        }
                        SharePreferenceManager.setCachedUsername(info.getUserName());
                        SharePreferenceManager.setCachedAvatarPath(file.getAbsolutePath());
                    }
                }
                JMessageClient.logout();
                Log.i("RayTest","Env:"+ Const.TEST_ENVIROMENT_SW);
                ConfigSharePreference.saveEnviroment(false);
                toastShort("切換成正式環境 , 請完全關閉APP 重新啟動");
                Log.i("RayTest","Env:"+ Const.TEST_ENVIROMENT_SW);
                //startActivity(LoginSelectActivity.createIntent(SettingActivity.this));
               /* Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);*/

            }
        });

//        我的直播地址
        subscribeClick(R.id.setting_my_pullpath, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (LocalDataManager.getInstance().getLoginInfo().getApproveid().equals(getString(R.string.authentication_no))) {
                    startActivity(AuthenticationActivity.createIntent(getBaseContext()));
                    toastShort("請先認證");
                } else {
                    showDialog();
                }
            }
        });

        //推荐人
        if (getIntent().getStringExtra(RECOMMEND) != null && !getIntent().getStringExtra(RECOMMEND).equals("")) {
            mRecommendation.setText(getIntent().getStringExtra(RECOMMEND));
        } else {
            subscribeClick(R.id.setting_my_invitation, new Action1<Void>() {
                @Override
                public void call(Void aVoid) {
                    showDialogInvitation();
                }
            });
        }

        subscribeClick(R.id.setting_tv_logout, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {

                UserInfo info = JMessageClient.getMyInfo();
                if (null != info) {
                    File file = info.getAvatarFile();
                    if (file != null && file.isFile()) {
                    } else {
                        String path = FileHelper.getUserAvatarPath(info.getUserName());
                        file = new File(path);
                        if (file.exists()) {
                        }
                        SharePreferenceManager.setCachedUsername(info.getUserName());
                        SharePreferenceManager.setCachedAvatarPath(file.getAbsolutePath());
                    }
                }
                JMessageClient.logout();

                startActivity(LoginSelectActivity.createIntent(SettingActivity.this));
                (SettingActivity.this).sendFinishBroadcast(LoginSelectActivity
                        .class.getSimpleName());

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void init() {
        tvAbout.setText(getString(R.string.setting_about, "我們"));
        tvVersion.setText(getString(R.string.setting_version, Packages.getVersionName(this)));
        tvUpVersion.setText(getString(R.string.setting_up_version));
        //Color egg
        tvVersion.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(SettingActivity.this,
                        String.format("Flavor : %1$s\nBuildType : %2$s\nVersionCode : %3$s",
                                BuildConfig.FLAVOR,
                                BuildConfig.BUILD_TYPE,
                                BuildConfig.VERSION_CODE),
                        Toast.LENGTH_LONG).show();
                return false;
            }
        });

        String size;
        try {
            size = DataCleanManager.getTotalCacheSize(this);
            final String finalSize = size;
            subscribeClick(R.id.setting_rl_clear_cache, new Action1<Void>() {
                @Override
                public void call(Void aVoid) {
                    tvCacheSize.setVisibility(View.INVISIBLE);
                    prgClearCache.setVisibility(View.VISIBLE);
                    try {
                        Fresco.getImagePipeline().clearCaches();
                        DataCleanManager.clearAllCache(SettingActivity.this);
                        Log.i("RayTest","after clear cache:"+DataCleanManager.getTotalCacheSize(SettingActivity.this));
                        tvCacheSize.setText(R.string.setting_cache_empty);
                        tvCacheSize.setVisibility(View.VISIBLE);
                        prgClearCache.setVisibility(View.INVISIBLE);
                    } catch (Exception e) {
                        L.w(LOG_TAG, "Error while clearing cache!", e);
                        tvCacheSize.setText(finalSize);
                        toastShort("清除暫存失敗");
                    }

                }
            });
        } catch (Exception e) {
            size = getString(R.string.setting_cache_empty);
        }
        tvCacheSize.setText(size);
    }


    //  判断是否更新
    public void isUpData(UpDataBean updata) {
        if (upDataState(Packages.getVersionName(this), updata.getApkversion()) < 0) {
            showUpDataDialog(updata);
        } else {
            toastShort(getString(R.string.setting_updata_newest));
        }
    }

    public void showUpDataDialog(final UpDataBean updata) {
        MessageDialog dialog = new MessageDialog(this);
        dialog.setContent(R.string.mian_updata_tip);
        dialog.setMessageDialogListener(new MessageDialog.MessageDialogListener() {
            @Override
            public void onCancelClick(MessageDialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onCommitClick(MessageDialog dialog) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
//                进行下载
                downLoadApk(updata);
            }
        });
        dialog.show();
    }


    /*
     * 从服务器中下载APK
	 */
    protected void downLoadApk(final UpDataBean updata) {
        final ProgressDialog pd;    //进度条对话框
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage(getString(R.string.main_updata_isdownload));
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = DownLoadUtil.getFileFromServer(updata.getApkaddress(), pd);
                    sleep(3000);
                    installApk(file);
                    pd.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                    handler.sendEmptyMessage(5);
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //安装apk
    protected void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//             *  开启直播验证
//             *
//             *  @param reponse  token  ucid
//             *
//             *  @return 服务器返回 data:( 0 ：可直播；1：未签约；2：时间不对;3:其他错误)
            switch (msg.what) {
                case 5:
                    toastShort(getString(R.string.main_updata_errordownload));
                    break;
            }
        }
    };

    public int upDataState(String oldVersion, String newVersion) {
//        old大于new则不进行更新  负数更新
        return oldVersion.compareTo(newVersion);
    }

    /**
     * 这是兼容的 AlertDialog
     */
    private void showDialog() {
        if (myPlayPath == null) {
            toastShort(getString(R.string.userinfo_dialog_errorload));
            return;
        }
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.my_publish_path_title));
        builder.setMessage(myPlayPath);
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.commit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) SettingActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setText(myPlayPath);
                toastShort(getString(R.string.my_publish_path_copy));
            }
        });
        builder.show();
    }

    private void showDialogInvitation() {
  /*
  这里使用了 android.support.v7.app.AlertDialog.Builder
  可以直接在头部写 import android.support.v7.app.AlertDialog
  那么下面就可以写成 AlertDialog.Builder
  */
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_invitation_layout, null);
        final EditText editText = (EditText) layout.findViewById(R.id.dialog_invitation_edit);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.setView(layout);
        builder.setPositiveButton(getString(R.string.commit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.upLoadMyRecommen(editText.getText().toString());
            }
        });
        builder.show();
    }

    @Override
    public void getMyAddress(String address) {
        myPlayPath = address;
    }

    @Override
    public void upLoadMyRecommen(int code) {
        if (code == 0)
            toastShort(getString(R.string.setting_recommen_compelet));
        else
            toastShort(getString(R.string.setting_recommen_error));
    }

    @Override
    public void getNewAppVersion(UpDataBean upData) {
        isUpData(upData);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unsubscribeTasks();
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }
}

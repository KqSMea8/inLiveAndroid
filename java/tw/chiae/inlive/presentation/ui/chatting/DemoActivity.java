package tw.chiae.inlive.presentation.ui.chatting;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;
import tw.chiae.inlive.R;

import java.lang.ref.WeakReference;

import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.chatting.utils.DialogCreator;
import tw.chiae.inlive.presentation.ui.chatting.utils.HandleResponseCode;
import tw.chiae.inlive.util.L;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.UserLogoutEvent;
import cn.jpush.im.api.BasicCallback;

/**
 * Chatting入口Activity, 可以选择单聊或群聊,并且设置聊天相关的用户信息(通过Intent的方式)
 */

public class DemoActivity extends FragmentActivity {

    private Dialog mDialog;
    private String mMyName;
    private String mMyPassword;
    private static final int REGISTER = 200;
    private Context mContext;
    private boolean r=false;
    private boolean f=false;

    private MainController mMainController;
    private MainView mMainView;

    public static Intent createIntent(Context context) {
        return new Intent(context, DemoActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jmui_activity_main);

        mMainView = (MainView) findViewById(R.id.main_view);
        mMainView.initModule();
        mMainController = new MainController(mMainView, this);

        mMainView.setOnClickListener(mMainController);
        mMainView.setOnPageChangeListener(mMainController);

        mContext = this;
        //注册接收消息(成为订阅者), 注册后可以直接重写onEvent方法接收消息
        JMessageClient.registerEventReceiver(this);
        LinearLayout mSingleChatLl;
//        LinearLayout mGroupChatLl;
        mSingleChatLl = (LinearLayout) findViewById(R.id.jmui_single_chat_ll);
//        mGroupChatLl = (LinearLayout) findViewById(R.id.jmui_group_chat_ll);

        mMyPassword = "user"+LocalDataManager.getInstance().getLoginInfo().getUserId();
        if (JMessageClient.getMyInfo() == null) {
            mMyName ="user"+LocalDataManager.getInstance().getLoginInfo().getUserId();

            for (int i = 0; i < 5; i++) {
                register();
                if (r = true) {
                    break;
                }
            }
            if (r=true){
                for (int j = 0; j < 5; j++) {
                    login();
                    if (f=true) {
                        break;
                    }
                }
            }
        }
    }

    public void login() {
        JMessageClient.login(mMyName, mMyPassword, new BasicCallback() {
            @Override
            public void gotResult(int status, String desc) {
                if (status == 0) {
                    f=true;
                } else {
                }
            }
        });
    }

    public void register() {
        JMessageClient.register(mMyName, mMyPassword, new BasicCallback() {
            @Override
            public void gotResult(int status, String desc) {
                if (status == 0) {
                    r=true;
                } else {
                    if (desc.equals("user exist")) {
                        r=true;
                    }
                }
            }
        });
    }
    protected float mDensity;
    protected int mDensityDpi;
    protected int mAvatarSize;
    protected int mWidth;
    protected int mHeight;
    public void onEventMainThread(UserLogoutEvent event) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mDensityDpi = dm.densityDpi;
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;
        mAvatarSize = (int) (50 * mDensity);
        String title = mContext.getString(R.string.jmui_user_logout_dialog_title);
        String msg = mContext.getString(R.string.jmui_user_logout_dialog_message);
        Log.i("RayTest","DemoActivity");
        mDialog = DialogCreator.createBaseCustomDialog(mContext, title, msg, onClickListener);
        mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.show();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mDialog.dismiss();
        }
    };


    @Override
    protected void onPause() {
        JPushInterface.onPause(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        JPushInterface.onResume(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        JMessageClient.unRegisterEventReceiver(this);
        super.onDestroy();
    }
    public FragmentManager getSupportFragmentManger() {
        // TODO Auto-generated method stub
        return getSupportFragmentManager();
    }

    private static class MyHandler extends Handler {

        private WeakReference<DemoActivity> mActivity;

        public MyHandler(DemoActivity activity) {
            mActivity = new WeakReference<DemoActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DemoActivity demoActivity = mActivity.get();
            if (demoActivity != null) {
                switch (msg.what) {
                    case REGISTER:
                        demoActivity.login();
                        break;
                }
            }
        }
    }
    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

}
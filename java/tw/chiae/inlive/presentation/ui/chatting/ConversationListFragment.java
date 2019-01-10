package tw.chiae.inlive.presentation.ui.chatting;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import tw.chiae.inlive.R;
import tw.chiae.inlive.presentation.ui.chatting.utils.Event;
import tw.chiae.inlive.presentation.ui.chatting.utils.HandleResponseCode;
import tw.chiae.inlive.util.L;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import de.greenrobot.event.EventBus;

/*
 * 会话列表界面
 */
public class ConversationListFragment extends BaseFragment {

    private static String TAG = ConversationListFragment.class.getSimpleName();
    private View mRootView;
    private ConversationListView mConvListView;
    private ConversationListController mConvListController;
    private NetworkReceiver mReceiver;
    private Activity mContext;
    private BackgroundHandler mBackgroundHandler;
    private HandlerThread mThread;
    private static final int REFRESH_CONVERSATION_LIST = 0x3000;
    private ImageButton mBack;
    private Button reset;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        EventBus.getDefault().register(this);
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        mRootView = layoutInflater.inflate(R.layout.fragment_conv_list,
                (ViewGroup) getActivity().findViewById(R.id.main_view), false);
        mConvListView = new ConversationListView(mRootView, this.getActivity());
        mConvListView.initModule();

        mThread = new HandlerThread("Work on DemoActivity");
        mThread.start();
        mBackgroundHandler = new BackgroundHandler(mThread.getLooper());
        mConvListController = new ConversationListController(mConvListView, this, mWidth);
        mConvListView.setItemListeners(mConvListController);
        mConvListView.setLongClickListener(mConvListController);
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        if (null == activeInfo) {
            mConvListView.showHeaderView();
        } else {
            mConvListView.dismissHeaderView();
        }
        initReceiver();

    }

    @Override
    public void onStart() {
        super.onStart();
        try{
            mBack = (ImageButton) getActivity().findViewById(R.id.jmui_return_btn_m);
            reset = (Button) getActivity().findViewById(R.id.reset);

            reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                    mConvListController.getAdapter().resetConversation();
                    }catch (NullPointerException e){
                    }
                }
            });
        }catch (NullPointerException e){
        }
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private void initReceiver() {
        mReceiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mContext.registerReceiver(mReceiver, filter);
    }

    //监听网络状态的广播
    private class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeInfo = manager.getActiveNetworkInfo();
                if (null == activeInfo) {
                    mConvListView.showHeaderView();
                } else {
                    mConvListView.dismissHeaderView();
                }
            }
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

    }

    /**
     * 在会话列表中接收消息
     *
     * @param event 消息事件
     */
    public void onEvent(MessageEvent event) {
        Message msg = event.getMessage();
        L.i("RayTest", "收到消息：msg = " + msg.toString());
        Log.d(TAG, "收到消息：msg = " + msg.toString());
        ConversationType convType = msg.getTargetType();
        if (convType == ConversationType.group) {
            long groupID = ((GroupInfo) msg.getTargetInfo()).getGroupID();
            Conversation conv = JMessageClient.getGroupConversation(groupID);
            if (conv != null && mConvListController != null) {
                mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST,
                        conv));
            }
        } else {
            final UserInfo userInfo = (UserInfo) msg.getTargetInfo();
            final String targetID = userInfo.getUserName();
            final Conversation conv = JMessageClient.getSingleConversation(targetID, userInfo.getAppKey());
            if (conv != null && mConvListController != null) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //如果设置了头像
                        if (!TextUtils.isEmpty(userInfo.getAvatar())) {
                            //如果本地不存在头像
                            userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                                @Override
                                public void gotResult(int status, String desc, Bitmap bitmap) {
                                    if (status == 0) {
//                                        mConvListController.getAdapter().notifyDataSetChanged();
                                        mConvListController.getAdapter().addmFuImg(userInfo.getUserName().replaceAll("user",""));
                                    } else {
                                        HandleResponseCode.onHandle(mContext, status, false);
                                    }
                                }
                            });
                        }
                    }
                });
                mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(REFRESH_CONVERSATION_LIST,
                        conv));
            }
        }
    }

    private class BackgroundHandler extends Handler {
        public BackgroundHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_CONVERSATION_LIST:
                    Conversation conv = (Conversation) msg.obj;
                    mConvListController.getAdapter().setToTop(conv);
                    break;
            }
        }
    }

    /**
     * 收到创建单聊的消息
     *
     * @param event 可以从event中得到targetID
     */
    public void onEventMainThread(Event.StringEvent event) {
        L.i("RayTest", "StringEvent execute");
        String targetId = event.getTargetId();
        String appKey = event.getAppKey();
        Conversation conv = JMessageClient.getSingleConversation(targetId, appKey);
        if (conv != null) {
            mConvListController.getAdapter().addNewConversation(conv);
        }
    }

    /**
     * 收到创建或者删除群聊的消息
     *
     * @param event 从event中得到groupID以及flag
     */
    public void onEventMainThread(Event.LongEvent event) {
        long groupId = event.getGroupId();
        Conversation conv = JMessageClient.getGroupConversation(groupId);
        if (conv != null && event.getFlag()) {
            mConvListController.getAdapter().addNewConversation(conv);
        } else {
            mConvListController.getAdapter().deleteConversation(groupId);
        }
    }

    /**
     * 收到保存为草稿事件
     * @param event 从event中得到Conversation Id及草稿内容
     */
    public void onEventMainThread(Event.DraftEvent event) {
        String draft = event.getDraft();
        String targetId = event.getTargetId();
        String targetAppKey = event.getTargetAppKey();
        Conversation conv;
        if (targetId != null) {
            conv = JMessageClient.getSingleConversation(targetId, targetAppKey);
        } else {
            long groupId = event.getGroupId();
            conv = JMessageClient.getGroupConversation(groupId);
        }
        //如果草稿内容不为空，保存，并且置顶该会话
        if (!TextUtils.isEmpty(draft)) {
            mConvListController.getAdapter().putDraftToMap(conv.getId(), draft);
            mConvListController.getAdapter().setToTop(conv);
        //否则删除
        } else {
            mConvListController.getAdapter().delDraftFromMap(conv.getId());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        ViewGroup p = (ViewGroup) mRootView.getParent();
        if (p != null) {
            p.removeAllViewsInLayout();
        }
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mContext.unregisterReceiver(mReceiver);
        mBackgroundHandler.removeCallbacksAndMessages(null);
        mThread.getLooper().quit();
        super.onDestroy();
    }

    public void sortConvList() {
        if (mConvListController != null) {
            mConvListController.getAdapter().sortConvList();
        }
    }
}

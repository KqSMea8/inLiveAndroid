package tw.chiae.inlive.presentation.ui.room;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.facebook.drawee.backends.pipeline.Fresco;

import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.domain.AnchorManager;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.main.MainActivity;
import tw.chiae.inlive.presentation.ui.room.player.PlayerFragment;
import tw.chiae.inlive.presentation.ui.room.publish.PublishFragment;
import tw.chiae.inlive.presentation.ui.widget.MessageDialog;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.DataCleanManager;
import tw.chiae.inlive.util.L;
import tw.chiae.inlive.util.TimingLogger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class RoomActivity extends BaseActivity {

    public static final int ACTIVITY_REQUEST_CODE = 1901;
    public static final int ACTIVITY_PAYMENT_SUCCESS_CODE = 10001;
    private static final String CHECK_HOTPOINT = "check_hotpoint" ;
    private static int ID = 0;
    protected final String INSTANCE_TAG = "RoomActivity-" + ++ID;

    /**
     * 观看直播。
     */
    public static final int TYPE_VIEW_LIVE = 1;
    /**
     * 发布直播。
     */
    public static final int TYPE_PUBLISH_LIVE = 2;
    /**
     * 观看回放。
     */
    public static final int TYPE_VIEW_REPLAY = 3;

    private int changes;
    //private RequestQueue requestQueue;
    private MessageDialog dialog;

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    public void EndHotPoint(long coin) {
        RoomInfoTmp.coinValue = coin;
        Log.i("RayTest","showRoomEndInfoDialog2");
        showRoomEndInfoDialog();
    }

    @IntDef({TYPE_VIEW_LIVE, TYPE_PUBLISH_LIVE, TYPE_VIEW_REPLAY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RoomType {
    }

    private static final String EXTRA_ROOM_TYPE = "rt";
    private static final String EXTRA_ROOM_ID = "ri";
    private static final String EXTRA_ANCHOR_ID = "ai";
    private static final int FRAG_CONTAINER = R.id.room_container;

    @RoomActivity.RoomType
    private int mRoomType;
    /**
     * 房间号，用于获取直播间结束后的信息。
     */
    private String mRoomId;
    /**
     * 主播ID号，用于观众关注该主播，因此只有类型为观众时才会使用到。
     */
    private String mAnchorId;
    private Bundle mBundleArgs;
    private RoomFinishDialog mFinishInfoDialog;

    private Subscription subscription;

    //    这个是直播的时候的roomFragmnet；
    private RoomFragment roomPFragment;

    /**
     * 进入直播房间页面。
     *
     * @param context  上下文信息
     * @param roomType 房间类型，必须是{@link tw.chiae.inlive.presentation.ui.room.RoomActivity
     *                 .RoomType}中的一个
     * @param args     传递给指定Fragment的额外参数。
     * @return 返回房间页的Intent。
     */
    @SuppressWarnings("unused")
    public static Intent createIntent(Context context,
                                      @RoomType int roomType,
                                      @NonNull String roomId,
                                      @NonNull String anchorId,
                                      Bundle args) {
        Intent intent = new Intent(context, RoomActivity.class);
        intent.putExtra(EXTRA_ROOM_TYPE, roomType);
        intent.putExtra(EXTRA_ROOM_ID, roomId);
        intent.putExtra(EXTRA_ANCHOR_ID, anchorId);
        Bitmap bitmap;
        if (args != null) {
            intent.putExtras(args);
        }
        return intent;
    }

    @Override
    protected void init() {
        setSwipeBackEnable(false);
    }

    @Override
    protected void parseIntentData(Intent intent, boolean isFromNewIntent) {
        super.parseIntentData(intent, isFromNewIntent);
        L.i(LOG_TAG, INSTANCE_TAG + ", " + isFromNewIntent);
        @RoomType int roomType = intent.getIntExtra(EXTRA_ROOM_TYPE, -1);
        mRoomType = roomType;
        mRoomId = intent.getStringExtra(EXTRA_ROOM_ID);
        mAnchorId = intent.getStringExtra(EXTRA_ANCHOR_ID);
        mBundleArgs = intent.getExtras();
        if (isFromNewIntent) {
            getSupportFragmentManager().beginTransaction()
                    .replace(FRAG_CONTAINER, createFragmentByType())
                    .commit();
        }
        /*requestQueue = Volley.newRequestQueue(getApplicationContext());*/
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_room;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        TimingLogger logger = new TimingLogger("timing", "RoomActivity");
        getSupportFragmentManager().beginTransaction()
                .add(FRAG_CONTAINER, createFragmentByType())
                .commit();
        logger.addSplit("add fragment");
        logger.dumpToLog();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
                dialog.cancel();
            }
            dialog = null;
        }

        if (mFinishInfoDialog != null) {
            if (mFinishInfoDialog.isShowing()) {
                mFinishInfoDialog.dismiss();
            }
            mFinishInfoDialog = null;
        }
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(FRAG_CONTAINER);
        currentFragment.onActivityResult(requestCode, resultCode, data);
    }

    private Fragment createFragmentByType() {
        Fragment fragment;
        switch (mRoomType) {
            case TYPE_VIEW_LIVE:
                fragment = PlayerFragment.newInstance(mBundleArgs, mRoomId, false);
                PlayerFragment roomFragment = (PlayerFragment) fragment;
                roomFragment.setmAnchorId(mAnchorId);
                break;
//            直播的
            case TYPE_PUBLISH_LIVE:
                //Not implemented yet
                fragment = PublishFragment.newInstance(mBundleArgs);
                roomPFragment = (RoomFragment) fragment;
                roomPFragment.setmAnchorId(mAnchorId);
                break;
            case TYPE_VIEW_REPLAY:
                //Not implemented yet
//                throw new UnsupportedOperationException("unsupported room type: " + mRoomType);
//                这个roomid里隐藏了我们的playurl   roomid_starttime_url
                fragment = PlayerFragment.newInstance(mBundleArgs, mRoomId, true);
                RoomFragment roomFragmentRePlay = (RoomFragment) fragment;
                roomFragmentRePlay.setmAnchorId(mAnchorId);
                break;
            default:
                throw new IllegalArgumentException("Wrong room type: " + mRoomType);
        }
        return fragment;
    }

    @Override
    public void onBackPressed() {
        //默认情况下需要提示，这里先判断键盘是否被隐藏掉 如果被隐藏了才提示退出
        Fragment fragment = getSupportFragmentManager().findFragmentById(FRAG_CONTAINER);
        if ((fragment instanceof HasInputLayout)) {
            ViewGroup inputLayout = ((HasInputLayout) fragment).getInputLayout();
            if (inputLayout != null && inputLayout.isShown()) {
                ((HasInputLayout) fragment).showInputLayout(false);
                return;
            }
        }
        exitLiveRoom(true);
    }

    public void exitLiveRoom(boolean needConfirm) {
        ClearCacheEvent();
        if (needConfirm) {
            showFinishConfirmDialog();
            return;
        }
        finishRoomActivity();
    }

    //弹出退出dialog
    public void showFinishConfirmDialog() {
        if(dialog==null)
            dialog = new MessageDialog(this);
        dialog.setContent(R.string.back_room_tip);
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
                performExitAction();
            }
        });
        dialog.show();
    }

    /**
     * 这个方法是在点击退出确认后的操作，对于观众来说应该什么都不做，而对于主播来说可以看到收入的信息框。
     */
    private void performExitAction() {
        if (mRoomType == TYPE_PUBLISH_LIVE) {
            Log.i("RayTest","showRoomEndInfoDialog3");
            //showRoomEndInfoDialog();
            Fragment fragment = getSupportFragmentManager().findFragmentById(FRAG_CONTAINER);
            if (!(fragment instanceof PublishFragment)) {
                L.e(LOG_TAG, "Fragment %s is not instance of PublishFragment!");
            } else {
                Log.i("RayTest","finishRoom!");
                PublishFragment publishFragment = (PublishFragment) fragment;
                publishFragment.finishRoom(TYPE_PUBLISH_LIVE);
            }




            return;
        }
        finishRoomActivity();
    }

    public void finishRoomActivity() {
        Log.i("RayTest","finishRoomActivity!");
        //不是通过Finish，而是start，保持本Activity不被销毁  -----------这里严重怀疑会出现无法退出的bug
        startActivity(MainActivity.createIntent(this));
        overridePendingTransition(R.anim.fragment_slide_right_in, R.anim.fragment_slide_right_out);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //获取fragment
        Fragment fragment = getSupportFragmentManager().findFragmentById(FRAG_CONTAINER);
        //如果没有获取到该对象则吧
        if (!(fragment instanceof HasInputLayout)) {
            return super.dispatchTouchEvent(ev);
        }
        //转型为接口
        HasInputLayout inputFragment = (HasInputLayout) fragment;

        //For safety   通过回调得到输入框对象
        ViewGroup inputLayout = inputFragment.getInputLayout();
        //如果输入框没获取到，或者输入框是隐藏了的责直接向下传递
        if (inputLayout == null || (!inputLayout.isShown())) {
//            L.d(LOG_TAG, "InputLayout is%s null and not shown.", inputLayout == null ? "" : " not");
            return super.dispatchTouchEvent(ev);
        }
        //获取down
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isHideInput(inputLayout, ev) && v != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
//                    switchSoftInputStatus();
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    inputFragment.showInputLayout(false);
                }
            }
            return super.dispatchTouchEvent(ev);
        }

        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            View view = getCurrentFocus();
            int aaa = view.findFocus().getId();
            int[] leftTop = {0, 0};
            view.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + view.getHeight();
            int right = left + view.getWidth();
        }
        //直接向下传递
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }


    //如果焦点在ViewGroup内责返回false
    private boolean isHideInput(ViewGroup viewGroup, MotionEvent event) {
        int[] leftTop = {0, 0};
        //获取输入框当前的location位置
        viewGroup.getLocationInWindow(leftTop);
        Log.e(LOG_TAG, "view gettop" + viewGroup.getTop());
        int left = leftTop[0];
        int top = leftTop[1];
        //bottom感觉没啥用 不过感觉还是可以加上event.getY()<bottom 。。。只是感觉但不知道前人为啥没加
        int bottom = top + viewGroup.getHeight();
        int right = left + viewGroup.getWidth();
        Log.e(LOG_TAG, "left:" + left + " top:" + top + " bottom:" + bottom + "right:" + right);
        Log.e(LOG_TAG, "x:" + event.getX() + " y:" + event.getY());
//        这里判断的是否是 触摸了viewGrop的 触摸了返回false  没有责返回true 不要问我问什么最后 只需要event.getY() > top 请动脑 毕竟不是我的代码
        return !(event.getX() > left && event.getX() < right
                && event.getY() > top);
    }

    public interface HasInputLayout {
        void showInputLayout(boolean show);

        ViewGroup getInputLayout();

        ViewGroup getPriInputLayout();
    }

    public void showRoomEndInfoDialog() {
        Log.i("RayTest", "showRoomEndInfoDialog"+RoomInfoTmp.coinValue);
        openFinishDialog(RoomInfoTmp.coinValue, RoomInfoTmp.HotpointValue);
     /*   String RequestUrl = Const.HotPointAPI +  mAnchorId;
        //getHotPointApi();
        android.util.Log.i("RayTest", "Request Url:" + RequestUrl);
        StringRequest stringRequest = new StringRequest(RequestUrl, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String sResponse) {
                try {
                    JSONArray jsonArray = new JSONArray(sResponse);
                    String hotItem = jsonArray.getJSONObject(0).toString();
                    android.util.Log.i("RayTest", "hotItem1 :" + hotItem);
                    HotPointInfo hotpointInfo = new Gson().fromJson(hotItem, HotPointInfo.class);
                    //android.util.Log.i("RayTest", "HotVal:" + hotpointInfo.getHotpoint());

                } catch (JSONException e) {


                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        stringRequest.setTag(CHECK_HOTPOINT);
        requestQueue.add(stringRequest);*/


    }




    private void openFinishDialog(long CoinValue, long HotPointVal) {

        if (mFinishInfoDialog != null && mFinishInfoDialog.isShowing()) {
            return;
        }
        //先停止Preview
        Fragment fragment = getSupportFragmentManager().findFragmentById(FRAG_CONTAINER);
        if (!(fragment instanceof PublishFragment)) {
            L.e(LOG_TAG, "Fragment %s is not instance of PublishFragment!");
        } else {
            PublishFragment publishFragment = (PublishFragment) fragment;
            publishFragment.prepareExit();
        }
//        关闭窗口的时候 dialog
        mFinishInfoDialog = new RoomFinishDialog(this, mRoomId, mRoomType, CoinValue, HotPointVal);
        Window win = mFinishInfoDialog.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(lp);

        mFinishInfoDialog.setListener(new RoomFinishDialog.FinishDialogListener() {
            @Override
            public void onFinish() {
                finishRoomActivity();
            }

            @Override
            public void onClickFollow() {
                subscription = new AnchorManager().followAnchor(mAnchorId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseObserver<BaseResponse<Object>>(RoomActivity.this) {
                            @Override
                            public void onSuccess(BaseResponse<Object> response) {
                                toastShort(response.getMsg());
                                finishRoomActivity();
                            }
                        });
            }
        });

        mFinishInfoDialog.show();
//        如果是直播的才发出logoOut其他人发送不走这里
        if (mRoomType == TYPE_PUBLISH_LIVE) {

            roomPFragment.requesetRoomLoginOut();

        }
    }

    private void ClearCacheEvent() {
        Log.i("RayTest","ClearCacheEvent......");
        Fresco.getImagePipeline().clearMemoryCaches();
        DataCleanManager.clearAllCache(this);
    }

}

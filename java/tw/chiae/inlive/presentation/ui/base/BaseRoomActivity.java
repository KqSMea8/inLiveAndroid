package tw.chiae.inlive.presentation.ui.base;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.jakewharton.rxbinding.view.RxView;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.model.UserInfo;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import rx.functions.Action1;
import tw.chiae.inlive.R;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.ProgressHUD.ProgressHUD;
import tw.chiae.inlive.presentation.ui.chatting.utils.FileHelper;
import tw.chiae.inlive.presentation.ui.chatting.utils.SharePreferenceManager;
import tw.chiae.inlive.presentation.ui.login.LoginSelectActivity;
import tw.chiae.inlive.presentation.ui.main.me.popup.city.model.CityModel;
import tw.chiae.inlive.presentation.ui.main.me.popup.city.model.DistrictModel;
import tw.chiae.inlive.presentation.ui.main.me.popup.city.model.ProvinceModel;
import tw.chiae.inlive.presentation.ui.main.me.popup.city.service.XmlParserHandler;
import tw.chiae.inlive.presentation.ui.widget.CustomToast;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.L;

/**
 * This is a fast-dev and common base activity.
 *
 * @author Muyangmin
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class BaseRoomActivity extends SwipeBackActivity implements BaseUiInterface {

    /**
     * Using activity class name as the log tag.
     */
    protected final String LOG_TAG = getClass().getSimpleName();
    //private FinishedBroadcastReceiver mFinishReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;
    //private ProgressHUD hud;
    //private ProgressDialog mProgressDialog;

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseIntentData(getIntent(), false);
        setContentView(getLayoutId());
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        registerFinishReceiver();
        //设置默认的按钮等
        processCommonWidgets();
        //调用子类的各项初始化操作
        JsonObject jsonObject = new JsonObject();
        findViews(savedInstanceState);
        //setListeners();
        //init();
        //JMessageClient.registerEventReceiver(this);

    }



    public void onEvent(LoginStateChangeEvent event){

    }
    @Override
    protected void onResume() {
        L.v(LOG_TAG, "----- onResume -----");
        super.onResume();
//        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        L.v(LOG_TAG, "----- onPause -----");
        super.onPause();
//        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        L.v(LOG_TAG, "----- onDestroy -----");

       /* try {
            if (hud != null && hud.isShowing()) {
                hud.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (mFinishReceiver != null) {
            mLocalBroadcastManager.unregisterReceiver(mFinishReceiver);
        }*/
        JMessageClient.unRegisterEventReceiver(this);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("RayTest","onKeyDown");
        return super.onKeyDown(keyCode, event);
    }

    private void registerFinishReceiver() {

    }
    private String CLASS_NAME = "className";


    public void sendFinishBroadcast(String className){

    }
    public void sendFinishBroadcast(){
        sendFinishBroadcast(null);
    }
    /**
     * 设置沉浸式状态栏，仅支持Lollipop 以上版本！
     */
    protected final void requestImmersiveLayoutIfSupported(){

    }

    @Override
    protected void onNewIntent(Intent intent) {
        L.v(LOG_TAG, "----- onNewIntent -----");
        super.onNewIntent(intent);
        //refresh intent data!
        parseIntentData(intent, true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        L.v(LOG_TAG, "----- onSaveInstanceState -----");
       /* super.onSaveInstanceState(outState);*/
        //为了解决activity被系统销毁后，fragment重叠的问题
        return ;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        L.v(LOG_TAG, "----- onRestoreInstanceState -----");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        L.v(LOG_TAG, "--- onActivityResult --- requestCode=" + requestCode + ",resultCode=" +
                resultCode + ", data=" + data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Specify the layout file this activity should display by calling {@link #setContentView(int)}.
     */
    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * Core method: retrieve views from {@link #getLayoutId()}.
     * Also all set listener code can put here.
     * <p>Tips: if too much listeners need to be set, consider override method {@link
     * #setListeners()} .</p>
     *
     * @param savedInstanceState see {@link #onCreate(Bundle)}
     */
    protected abstract void findViews(Bundle savedInstanceState);

    /**
     * Set listeners of retrieved widgets.
     * Note: this method always be called after {@link #findViews(Bundle)}.
     */
    protected void setListeners() {
        //empty implementation
    }

    /**
     * Parse and process the extra data from the intent.
     * <p>Note: this method will be called before {@link #findViews(Bundle)}!</p>
     *
     * @param intent          The intent which start this activity.
     * @param isFromNewIntent Indicates whether this method is called by {@link #onNewIntent
     *                        (Intent)}.
     */
    protected void parseIntentData(Intent intent, boolean isFromNewIntent) {
        //empty implementation
    }

    /**
     * Init presenters, data, etc.
     */
    protected abstract void init();

    @AnimRes
    protected int getTransitionEnterAnim(){
        return R.anim.fragment_slide_left_in;
    }

    @AnimRes
    protected int getTransitionOutAnim(){
        return R.anim.fragment_slide_left_out;
    }

    private void processCommonWidgets(){
        View view = $(R.id.imgbtn_toolbar_back);
        if (view!=null){
            RxView.clicks(view)
                    .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            onTitleBackButtonClicked();
                        }
                    });
        }
    }

    /**
     * 点击标题栏返回键时调用。
     */
    protected void onTitleBackButtonClicked(){
        onBackPressed();
    }

    @Override
    public void showDataException(String msg) {
       // toastShort(msg);
       // dismissLoadingDialog();
    }

    @Override
    public void showNetworkException() {
       // toastShort(R.string.msg_network_error);
       // dismissLoadingDialog();
    }

    @Override
    public void showUnknownException() {
       // toastShort(R.string.msg_unknown_error);
        //dismissLoadingDialog();
    }

    @Override
    public void showLoadingComplete() {
        //Empty implementation
    }

    @Override
    public Dialog showLoadingDialog() {
      /*  if (hud!=null && hud.isShowing()){
            L.e(LOG_TAG, "Call show loading dialog while dialog is still showing, is there a bug?");
            hud.dismiss();
        }
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
            hud = new ProgressHUD(this);
        hud.setSize(metrics.widthPixels/10,metrics.widthPixels/10);
            hud.setCornerRadius(10);
            hud.setText(R.string.loading_dialog_text);
            hud.show();
            return hud.getDialogView();*/

        return null;

    }

    @Override
    public void dismissLoadingDialog() {

    }

    /**
     * Convenient call of {@link #findViewById(int)}, automatically cast the result object.
     *
     * @param id  The aapt-generated unique id.
     * @param <T> The declared type of this widget.
     * @return The view object, or null if not found.
     */
    @SuppressWarnings("unchecked")
    protected final <T extends View> T $(@IdRes int id) {
        return (T) (findViewById(id));
    }

    /**
     * Convenient call of {@link View#findViewById(int)}, automatically cast the result object.
     *
     * @param view The view object which contains target object.
     * @param id   The aapt-generated unique id.
     * @param <T>  The declared type of this widget.
     * @return The view object, or null if not found.
     */
    @SuppressWarnings("unchecked")
    protected final <T extends View> T $(@NonNull View view, @IdRes int id) {
        return (T) (view.findViewById(id));
    }

    protected void toastShort(@StringRes int msg){
        CustomToast.makeCustomText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    protected void toastShort(@NonNull String msg){
        CustomToast.makeCustomText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Only for development usage!
     */
    protected void toastForNotImplementedFeature(){
        toastShort(R.string.commen_notmake);
    }

    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(enterAnim, exitAnim);
    }


    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(getTransitionEnterAnim(), getTransitionOutAnim());
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(getTransitionEnterAnim(), getTransitionOutAnim());
    }

    protected void subscribeFeatureStub(@IdRes int viewId){
        subscribeFeatureStub($(viewId));
    }

    protected void subscribeFeatureStub(@NonNull View view){
        subscribeClick(view, new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        toastForNotImplementedFeature();
                    }
                });
    }

    /**
     * 使用默认的throttle设置来注册点击事件。
     * @see #subscribeClick(View, Action1)
     * @see #$(int)
     */
    protected void subscribeClick(@IdRes int id, Action1<Void> action1){
        subscribeClick($(id), action1);
    }

    /**
     * 使用默认的throttle设置来注册点击事件。
     * @param view 要注册的View
     * @param action1 点击后执行的事件
     */
    protected void subscribeClick(View view, Action1<Void> action1){
        RxView.clicks(view)
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(action1);
    }

    /**
     * 注册点击事件，不允许throttle。
     * @param view 要注册的View
     * @param action1 点击后执行的事件
     */
    protected void subscribeClickWithoutThrottle(View view, Action1<Void> action1){
        RxView.clicks(view)
                .subscribe(action1);
    }
    protected int getAndoirdHeight(){
        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        //窗口的宽度
        return dm.heightPixels;
    }
    protected int getWidth(){
        WindowManager wm = (WindowManager)this
                .getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }




}
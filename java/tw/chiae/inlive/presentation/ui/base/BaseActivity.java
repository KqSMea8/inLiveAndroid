package tw.chiae.inlive.presentation.ui.base;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

/*import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;*/
import com.google.gson.JsonObject;
import com.jakewharton.rxbinding.view.RxView;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.leakcanary.RefWatcher;

import org.json.JSONException;
import org.json.JSONObject;

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
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import tw.chiae.inlive.BeautyLiveApplication;
import tw.chiae.inlive.R;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.ProgressHUD.ProgressHUD;
import tw.chiae.inlive.presentation.ui.chatting.utils.FileHelper;
import tw.chiae.inlive.presentation.ui.chatting.utils.SharePreferenceManager;
import tw.chiae.inlive.presentation.ui.login.LoginSelectActivity;
import tw.chiae.inlive.presentation.ui.login.splash.ModifyDialogFragment;
import tw.chiae.inlive.presentation.ui.login.splash.VersionChecker;
import tw.chiae.inlive.presentation.ui.main.EventManager;
import tw.chiae.inlive.presentation.ui.main.me.popup.city.model.CityModel;
import tw.chiae.inlive.presentation.ui.main.me.popup.city.model.DistrictModel;
import tw.chiae.inlive.presentation.ui.main.me.popup.city.model.ProvinceModel;
import tw.chiae.inlive.presentation.ui.main.me.popup.city.service.XmlParserHandler;
import tw.chiae.inlive.presentation.ui.main.mergefilm.EventActivity;
import tw.chiae.inlive.presentation.ui.main.setting.CreateViewDialogFragment;
import tw.chiae.inlive.presentation.ui.widget.CustomToast;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.L;
import tw.chiae.inlive.util.Spans;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;

/**
 * This is a fast-dev and common base activity.
 *
 * @author Muyangmin
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class BaseActivity extends SwipeBackActivity implements BaseUiInterface, CreateViewDialogFragment.dialogCallback, ModifyDialogFragment.DebugDialogCallback {

    /**
     * Using activity class name as the log tag.
     */
    protected final String LOG_TAG = getClass().getSimpleName();
    private FinishedBroadcastReceiver mFinishReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;
    private ProgressHUD hud;
    private EventManager eventManager;
    private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();;
    private CreateViewDialogFragment dialogFragment;
    private ModifyDialogFragment DebugdialogFragment;
    private VersionChecker checker;
    private String mDebugVersion = "";
    private String version_local;
    private boolean isEnableDebugMode = false;
    //private ProgressDialog mProgressDialog;
/*

    public void setTaskBarColored(int color) {

            Window window = this.getWindow();

            // Followed by google doc.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Log.i("RayTest","set status color1");
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(this,color));
                window.getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                //setTaskBarColoredLow();
            }else{
                Log.i("RayTest","set status color2");
                setTaskBarColoredLow();
            }

            // For not opaque(transparent) color.

            int offsetLayout = getStatusBarHeight();
        Log.i("RayTest","offset height"+offsetLayout);

    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void setTaskBarColoredLow() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.transparent);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

*/


    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        L.v(LOG_TAG, "----- onCreate ----- Bundle=" + savedInstanceState);
       /* if (savedInstanceState != null) {
            savedInstanceState.putParcelable("android:support:fragments", null);
        }*/
        super.onCreate(savedInstanceState);
        parseIntentData(getIntent(), false);
        setContentView(getLayoutId());

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        eventManager = new EventManager();
        dialogFragment = CreateViewDialogFragment.newInstance();
        dialogFragment.setDialogCallback(this);
        DebugdialogFragment = ModifyDialogFragment.newInstance();
        DebugdialogFragment.setDebugCallback(this);
        //registerFinishReceiver();
        //设置默认的按钮等
        processCommonWidgets();
        //调用子类的各项初始化操作
        JsonObject jsonObject = new JsonObject();
        findViews(savedInstanceState);
        setListeners();
        init();

        JMessageClient.registerEventReceiver(this);
           //requestImmersiveLayoutIfSupported();
    }



    public void onEvent(LoginStateChangeEvent event){
        Log.i("mrl",event.getReason()+"這尼瑪");
        toastShort("您的帳號在別處登錄了");
        Log.i("RayTest","token:"+ LocalDataManager.getInstance().getLoginInfo().getToken());
        UserInfo info = JMessageClient.getMyInfo();
        if (null != info) {
            File file = info.getAvatarFile();
            if (file != null && file.isFile()) {
            } else {
                String path = FileHelper.getUserAvatarPath(info.getUserName());
                file = new File(path);
                if (file.exists()) {
                }
            }
            SharePreferenceManager.setCachedUsername(info.getUserName());
            SharePreferenceManager.setCachedAvatarPath(file.getAbsolutePath());
            JMessageClient.logout();
        }

        startActivity(LoginSelectActivity.createIntent(this));
        (this).sendFinishBroadcast(LoginSelectActivity
                .class.getSimpleName());
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

        try {
            if (hud != null && hud.isShowing()) {
                hud.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (mFinishReceiver != null) {
            mLocalBroadcastManager.unregisterReceiver(mFinishReceiver);
        }
        JMessageClient.unRegisterEventReceiver(this);
        unsubscribeTasks();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("RayTest","onKeyDown");
        return super.onKeyDown(keyCode, event);
    }

    private void registerFinishReceiver() {

        mFinishReceiver = new FinishedBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.LIVE_FINISH_BROADCAST_ACTION);
        mLocalBroadcastManager.registerReceiver(mFinishReceiver,filter);
    }
    private String CLASS_NAME = "className";

    @Override
    public void onOKDialogcheck(Bundle bundle) {

    }

    @Override
    public void onCancelDialogcheck(Bundle mArgs) {

    }

    @Override
    public void onClickOK(String s) {
        if(checker.checkFormat(s)){
            mDebugVersion = s;
            DebugdialogFragment.dismiss();
        }
    }

    @Override
    public void onCancel() {

    }

    class FinishedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String className = intent.getStringExtra(CLASS_NAME);
            try{
                if(className!=null && LOG_TAG.equals(className)){
                    L.v(false, LOG_TAG,"keep class "+className);
                }else{
                    L.v(false, LOG_TAG,"finish class");
                    //API 17以下没有 isDestroyed 方法！
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1
                            || (!isDestroyed())) {
                        finish();
                    }
                }
            }catch (Throwable th){
                L.e(LOG_TAG, "Error while performing finish ACTION!", th);
            }
        }
    }

    public void sendFinishBroadcast(String className){
        Intent i = new Intent();
        i.setAction(Const.LIVE_FINISH_BROADCAST_ACTION);
        if(className !=null) {
            i.putExtra(CLASS_NAME, className);
        }
        mLocalBroadcastManager.sendBroadcast(i);
    }
    public void sendFinishBroadcast(){
        sendFinishBroadcast(null);
    }
    /**
     * 设置沉浸式状态栏，仅支持Lollipop 以上版本！
     */
    protected final void requestImmersiveLayoutIfSupported(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
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
        toastShort(msg);
        dismissLoadingDialog();
    }

    @Override
    public void showNetworkException() {
        toastShort(R.string.msg_network_error);
        dismissLoadingDialog();
    }

    @Override
    public void showUnknownException() {
        toastShort(R.string.msg_unknown_error);
        dismissLoadingDialog();
    }

    @Override
    public void showLoadingComplete() {
        //Empty implementation
    }

    @Override
    public Dialog showLoadingDialog() {
        if (hud!=null && hud.isShowing()){
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
            return hud.getDialogView();



    }

    @Override
    public void dismissLoadingDialog() {
        if (hud==null || (!hud.isShowing())){
            L.e(LOG_TAG, "Try to dismiss a dialog but dialog is null or already dismiss!");
            return ;
        }
        hud.dismiss();
        hud = null;
    }

    @Override
    public void setCoinData(int currencyItemSum) {

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
    /**
     * 所有省
     */
    protected String[] mProvinceDatas;
    /**
     * key - 省 value - 市
     */
    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    /**
     * key - 市 values - 区
     */
    protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();

    /**
     * key - 区 values - 邮编
     */
    protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();

    /**
     * 当前省的名称
     */
    protected String mCurrentProviceName;
    /**
     * 当前市的名称
     */
    protected String mCurrentCityName;
    /**
     * 当前区的名称
     */
    protected String mCurrentDistrictName ="";

    /**
     * 当前区的邮政编码
     */
    protected String mCurrentZipCode ="";
    /**
     * 解析省市区的XML数据
     */

    public void CheckEventSwitch(final int ID, final BaseFragment.EventCheckCallback callback) {
        Subscription subscription = eventManager.checkActivateEvent()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EventActivity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(EventActivity eventActivity) {
                        for(EventActivity.EventItem eventItem :eventActivity.getEvents()){
                            if(eventItem.getId()==ID ){
                                switch (eventItem.getStatus()){
                                    case 1:
                                        callback.eventSW(true);
                                        break;
                                    case 0:
                                        callback.eventSW(false);
                                        break;
                                    default:
                                        callback.eventSW(false);
                                        break;
                                }
                            }
                        }
                    }
                });
        addSubscription(subscription);
        //startActivity(CurrencyActivity.createIntent(getActivity(), mUserInfo.getId()));
    }

    protected void addSubscription(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }
    public void unsubscribeTasks() {
        mCompositeSubscription.unsubscribe();

    }

    public interface EventCheckCallback{

        void eventSW(boolean sw);
    }

    /*protected void initProvinceDatas()
    {
        List<ProvinceModel> provinceList = null;
        AssetManager asset = getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            // 获取解析出来的数据
            provinceList = handler.getDataList();
            /*//*//* 初始化默认选中的省、市、区
            if (provinceList!= null && !provinceList.isEmpty()) {
                mCurrentProviceName = provinceList.get(0).getName();
                List<CityModel> cityList = provinceList.get(0).getCityList();
                if (cityList!= null && !cityList.isEmpty()) {
                    mCurrentCityName = cityList.get(0).getName();
                    List<DistrictModel> districtList = cityList.get(0).getDistrictList();
                    mCurrentDistrictName = districtList.get(0).getName();
                    mCurrentZipCode = districtList.get(0).getZipcode();
                }
            }
            /*//*//*
            mProvinceDatas = new String[provinceList.size()];
            for (int i=0; i< provinceList.size(); i++) {
                // 遍历所有省的数据
                mProvinceDatas[i] = provinceList.get(i).getName();
                List<CityModel> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];
                for (int j=0; j< cityList.size(); j++) {
                    // 遍历省下面的所有市的数据
                    cityNames[j] = cityList.get(j).getName();
                    List<DistrictModel> districtList = cityList.get(j).getDistrictList();
                    String[] distrinctNameArray = new String[districtList.size()];
                    DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
                    for (int k=0; k<districtList.size(); k++) {
                        // 遍历市下面所有区/县的数据
                        DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        // 区/县对于的邮编，保存到mZipcodeDatasMap
                        mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        distrinctArray[k] = districtModel;
                        distrinctNameArray[k] = districtModel.getName();
                    }
                    // 市-区/县的数据，保存到mDistrictDatasMap
                    mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
                }
                // 省-市的数据，保存到mCitisDatasMap
                mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }
    }*/



    private int getRandomNumber() {
        return (int)Math.random()*1000+10000;
    }
}
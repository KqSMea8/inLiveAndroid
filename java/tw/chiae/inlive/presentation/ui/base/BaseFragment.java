package tw.chiae.inlive.presentation.ui.base;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.jakewharton.rxbinding.view.RxView;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.NotificationClickEvent;
import cn.jpush.im.android.api.model.UserInfo;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import tw.chiae.inlive.R;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.ProgressHUD.ProgressHUD;
import tw.chiae.inlive.presentation.ui.chatting.utils.FileHelper;
import tw.chiae.inlive.presentation.ui.chatting.utils.SharePreferenceManager;
import tw.chiae.inlive.presentation.ui.login.splash.PermissionsActivity;
import tw.chiae.inlive.presentation.ui.main.EventManager;
import tw.chiae.inlive.presentation.ui.main.me.OtherUserActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.DaYu2MediaRecorderActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.DaYuMediaPlayerActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.EventActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.KSWebActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.KaraStar;
import tw.chiae.inlive.presentation.ui.main.mergefilm.MediaPlayerActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.MediaRecorderActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.VCamera;
import tw.chiae.inlive.presentation.ui.main.mergefilm.model.FileUtils;
import tw.chiae.inlive.presentation.ui.main.setting.CreateViewDialogFragment;
import tw.chiae.inlive.presentation.ui.widget.CustomToast;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.L;
import tw.chiae.inlive.util.PermissionsChecker;
import tw.inlive.cewebkit.CEWebActivity;
import tw.inlive.cewebkit.CEWebKit;

/**
 * This is a fast-dev and common base fragment.
 * @author Muyangmin
 */
@SuppressWarnings("unused")
public abstract class BaseFragment extends Fragment implements BaseUiInterface, CreateViewDialogFragment.dialogCallback {

    private static final String TAG = "BaseFragment";
    /**
     * Using fragment class name as the log tag.
     */
    protected final String LOG_TAG = getClass().getSimpleName();
    private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    //private ProgressDialog mProgressDialog;
    private Context mContext;
    protected float mDensity;
    protected int mDensityDpi;
    protected int mWidth;
    protected int mAvatarSize;
    private Dialog dialog;
    private UserInfo myInfo;
    private ProgressHUD hud;
    private EventManager eventManager;
    private CreateViewDialogFragment dialogFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        //订阅接收消息,子类只要重写onEvent就能收到消息
        L.v(LOG_TAG, "----- registerEventReceiver ----- " );
        JMessageClient.registerEventReceiver(this);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        dialogFragment = CreateViewDialogFragment.newInstance();
        dialogFragment.setDialogCallback(this);
        mDensity = dm.density;
        mDensityDpi = dm.densityDpi;
        mWidth = dm.widthPixels;
        mAvatarSize = (int) (50 * mDensity);

        eventManager = new EventManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        L.v(LOG_TAG, "----- onCreateView ----- Bundle=" + savedInstanceState);
        int id = getLayoutId();
        View view = inflater.inflate(getLayoutId(), container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        L.v(LOG_TAG, "----- onViewCreated -----");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        L.v(LOG_TAG, "----- onResume -----");
        super.onResume();
    }

    @Override
    public void onPause() {
        L.v(LOG_TAG, "----- onPause -----");
        super.onPause();

    }

    @Override
    public void onDestroyView() {
        L.v(LOG_TAG, "----- onDestroyView -----");
        super.onDestroyView();
        unsubscribeTasks();
    }

    @Override
    public void onDestroy() {
        L.v(LOG_TAG, "----- onDestroy -----");
        //注销消息接收
        L.v("RayTest", "----- unRegisterEventReceiver -----");
        JMessageClient.unRegisterEventReceiver(this);
        if (dialog != null) {
            dialog.dismiss();
        }

        try {
            if (hud != null && hud.isShowing()) {
                hud.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    /**
     * 接收登录状态相关事件:登出事件,修改密码事件及被删除事件
     * @param event 登录状态相关事件
     */
    public void onEventMainThread(LoginStateChangeEvent event) {
        L.v(LOG_TAG, "----- LoginStateChangeEvent -----" );
        LoginStateChangeEvent.Reason reason = event.getReason();
        myInfo = event.getMyInfo();
        if (null != myInfo) {
            String path;
            File avatar = myInfo.getAvatarFile();
            if (avatar != null && avatar.exists()) {
                path = avatar.getAbsolutePath();
            } else {
                path = FileHelper.getUserAvatarPath(myInfo.getUserName());
            }
            Log.i(TAG, "userName " + myInfo.getUserName());
            SharePreferenceManager.setCachedUsername(myInfo.getUserName());
            SharePreferenceManager.setCachedAvatarPath(path);
            JMessageClient.logout();
        }
        if(dialog!=null) {
            dialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.show();
        }
    }

    public void onEvent(MessageEvent event){
        L.v(LOG_TAG, "----- LoginStateChangeEvent -----" );
    }

    public void onEvent(NotificationClickEvent event){
        L.v(LOG_TAG, "----- NotificationClickEvent -----" );
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        L.v(LOG_TAG, "----- onHiddenChanged -----" + hidden);
        super.onHiddenChanged(hidden);
    }

    /**
     * Returns the view layout which should be inflated on creating view.
     */
    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void initViews(View view);

    @Override
    public void showDataException(String msg) {
        toastShort(msg);
    }

    @Override
    public void showNetworkException() {
        toastShort(R.string.msg_network_error);
    }

    @Override
    public void showUnknownException() {
        toastShort(R.string.msg_unknown_error);
    }

    @Override
    public void showLoadingComplete() {
        //Empty implementation
    }

    public Dialog showLoadingDialog() {
        if (hud!=null && hud.isShowing()){
            L.e(LOG_TAG, "Call show loading dialog while dialog is still showing, is there a bug?");
            hud.dismiss();
        }
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //mProgressDialog = ProgressDialog.show(getActivity(), null, "请稍候", true, false);
        hud = new ProgressHUD(getContext());
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
        if (getActivity()!=null) {
            CustomToast.makeCustomText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    protected void toastShort(@NonNull String msg){
        if (getActivity()!=null) {
            CustomToast.makeCustomText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Only for development usage!
     */
    protected void toastForNotImplementedFeature(){
        toastShort(R.string.commen_notmake);
    }

    /**
     * @see BaseActivity#getTransitionEnterAnim()
     */
    @AnimRes
    protected int getTransitionEnterAnim(){
        return R.anim.fragment_slide_left_in;
    }

    /**
     * @see BaseActivity#getTransitionOutAnim()
     */
    @AnimRes
    protected int getTransitionOutAnim(){
        return R.anim.fragment_slide_left_out;
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        getActivity().overridePendingTransition(getTransitionEnterAnim(), getTransitionOutAnim());
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(getTransitionEnterAnim(), getTransitionOutAnim());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("RayTest","BaseFragment onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
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

    protected int getWidth(Context context){

        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    public void starActivityEvent(final int eventId, final String userId){
        Log.i("RayTest","event id:"+eventId);

        CEWebKit.getInstance().open(getActivity(), eventId, userId, new CEWebKit.EventHandler()
        {
            CEWebActivity mActivity;

            @Override
            public void openUserView(CEWebActivity activity, String userId)
            {
                // 切換到 userId 指定的使用者頁面
                Log.i("RayTest","openUserView");
                activity.startActivity(OtherUserActivity.createIntent(getActivity(),
                        Integer.valueOf(userId), false));
            }
            @Override
            public void openRecordView(CEWebActivity activity, String uploadPage, String videoUrl, String lyricsUrl, String extraInfo)
            {
                Log.i("RayTest","openRecordView");
                mActivity = activity;

                // 切換到錄影 Activity
                //
                // 切換範例:
                //  Intent intent = new Intent(activity, some_activity.class);
                //  intent.putExtra(...);
                //  activity.startActivityForResult(intent, CEWebKit.REQUEST_VIDEO_RECORD);
                //
                // 返回範例:
                //  Intent intent = new Intent();
                //  intent.putExtra("uploadPage", uploadPage);
                //  intent.putExtra("extraInfo", extraInfo);
                //  intent.putExtra("filePath", 存檔路徑);
                //  setResult(RESULT_OK, intent);
                //  finish();
                //
                Log.i("RayTest","uploadPage"+uploadPage);
                Log.i("RayTest","videoUrl"+videoUrl);
                Log.i("RayTest","lyricsUrl"+lyricsUrl);
                if(eventId==5)
                    startDaYuEvent(activity,Integer.parseInt(userId),videoUrl,lyricsUrl,uploadPage,extraInfo);
            }
            @Override
            public void onUploadComplete(String filePath)
            {
                showCompleteUpLoad(filePath);
                Log.i("RayTest","onUploadComplete"+filePath);
               /* starActivityEvent(eventId, userId);
                mActivity.finish();*/

                // 上傳完成處理, 刪除檔案...
            }
            @Override
            public void onUploadError()
            {
                toastShort("上傳失敗..請重新上傳");
                // 上傳錯誤處理, 顯示訊息...
            }
        });
    }
    private void showCompleteUpLoad(String videoFile) {
        if (FileUtils.checkFile(videoFile)) {
            File f = new File(videoFile);
            if (f != null) {
                if (f.exists()) {
                    //已经存在，删除
                    if (f.isDirectory())
                        FileUtils.deleteDir(f);
                    else
                        FileUtils.deleteFile(f);
                }
            }
        }
		/*if(uploadProgressView!=null && uploadProgressView.isShowing())
			uploadProgressView.dismiss();*/
        toastShort("恭喜您影片上傳成功!審核過後將會顯示在排行榜中。您可至您的參賽頁查看審核進度");
    }

    public void startDaYuEvent(Activity act, int userid, String starVideoUrl, String starlyricsUrl, String uploadUrl, String extraInfo) {
        String prefix = "";
        String[] fileName = starVideoUrl.split("/");
        //return VCamera.getVideoCachePath()+itData.getStringExtra(MV_NAME)+".mp4";
        String[] filePath =  fileName[fileName.length - 1].split(".mp4");

        String FilePath = VCamera.getVideoCachePath() + "DaYu_rec_" + userid  + "/"+ filePath[filePath.length - 1]+"/0.mp4";
        Log.i("RayTest","startDaYuEvent"+FilePath);
        if (FileUtils.checkFile(FilePath)) {
            Log.i("RayTest","checkFile ok");
            Intent it = DaYuMediaPlayerActivity.createIntent(getActivity(), starVideoUrl, starlyricsUrl, userid,uploadUrl,extraInfo);
            act.startActivityForResult(it, CEWebKit.REQUEST_VIDEO_RECORD);
           /* Intent it = DaYuMediaPlayerActivity.createIntent(getActivity(), starVideoUrl, starlyricsUrl, userid, uploadUrl);
            act.startActivityForResult(it, KaraStar.REQUEST_VIDEO_RECORD);*/
            //getActivity().finish();

        }else{
            Log.i("RayTest","checkFile null");
            Intent it = DaYu2MediaRecorderActivity.createIntent(getActivity(), starVideoUrl, starlyricsUrl, userid,uploadUrl,extraInfo);
            act.startActivityForResult(it, CEWebKit.REQUEST_VIDEO_RECORD);
            //getActivity().finish();
        }
        /*Log.i("RayTest", "path3 " + FilePath);
        if (FileUtils.checkFile(FilePath)) {
            Intent it = MediaPlayerActivity.createIntent(getActivity(), FilePath, name, starVideoUrl, starId);
            act.startActivityForResult(it, KaraStar.REQUEST_VIDEO_RECORD);
            getActivity().finish();
        } else {
            Log.i("RayTest","DaYuMediaRecorderActivity");
            Intent it = DaYuMediaRecorderActivity.createIntent(getActivity(), name, starVideoUrl, starId);
            act.startActivityForResult(it, KaraStar.REQUEST_VIDEO_RECORD);
            getActivity().finish();
        }*/

    }

    public void CheckEventSwitch(final int ID, final EventCheckCallback callback) {
        //CheckApi();

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
                                Log.i("RayTest","Id:"+eventItem.getId()+" status:"+eventItem.getStatus());
                                Log.i("RayTest",""+eventItem.toString());
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

/*    private void CheckApi() {
        RequestQueue rq = Volley.newRequestQueue(getActivity());
        String url = "https://api3.inlive.tw/api3/get_event_settings";
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("RayTest","ok: "+response);
                Gson gson = new Gson();
                EventActivity eventitem = gson.fromJson(response, EventActivity.class);
                for(EventActivity.EventItem event: eventitem.getEvents()){
                    Log.i("RayTest","===========");
                    Log.i("RayTest","id:"+event.getId());
                    Log.i("RayTest","name:"+event.getName());
                    Log.i("RayTest","Status:"+event.getStatus());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("RayTest","Error: "+error.toString());
            }
        });
        rq.add(stringRequest);
    }*/

    protected void addSubscription(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }
    public void unsubscribeTasks() {
        mCompositeSubscription.unsubscribe();

    }

    @Override
    public void onOKDialogcheck(Bundle bundle) {
        int type = bundle.getInt("type");
        switch (type){
            case CreateViewDialogFragment.TYPE_FINISH_MEDIA_RECORD:

                break;
            case CreateViewDialogFragment.TYPE_PAUSE_MEDIA_RECORD:

                dialogFragment.dismiss();
                break;


            default:
                break;
        }
    }

    @Override
    public void onCancelDialogcheck(Bundle mArgs) {

    }

    public interface EventCheckCallback{

        void eventSW(boolean sw);
    }

    public void startKsEvent(final Boolean needFinish) {
        Log.i("RayTest","startKsEvent");
       /* if(!CheckPermissions())
            return;*/
        KaraStar.getInstance().open(getActivity(), LocalDataManager.getInstance().getLoginInfo().getUserId(), new KaraStar.ViewHandler() {
            @Override
            public void switchToRecordView(KSWebActivity act, int starId, String starVideoUrl, String name) {
                Log.i("RayTest","switchToRecordView2");
                String FilePath = VCamera.getVideoCachePath() + "K_star_rec_" + starId + "_" + LocalDataManager.getInstance().getLoginInfo().getUserId() + "/0.mp4";
                if (FileUtils.checkFile(FilePath)) {
                    Intent it = MediaPlayerActivity.createIntent(getActivity(), FilePath, name, starVideoUrl, starId);
                    act.startActivityForResult(it, KaraStar.REQUEST_VIDEO_RECORD);
                    if(needFinish)
                        getActivity().finish();
                } else {
                    Intent it = MediaRecorderActivity.createIntent(getActivity(), name, starVideoUrl, starId, false);
                    act.startActivityForResult(it, KaraStar.REQUEST_VIDEO_RECORD);
                    if(needFinish)
                        getActivity().finish();

                }

            }

            @Override
            public void switchToUserView(KSWebActivity act, String userId) {

                act.startActivity(OtherUserActivity.createIntent(getActivity(),
                        Integer.valueOf(userId), false));
            }

        });

    }
  /*  private static final int REQUEST_CODE = 0; // 请求码
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE

    };


    private boolean CheckPermissions() {
        PermissionsChecker mPermissionsChecker = new PermissionsChecker(getContext().getApplicationContext());
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
            dialogFragment.showMsgDialog(getActivity().getSupportFragmentManager(),"提示",getString(R.string.permissions_error),CreateViewDialogFragment.TYPE_MV_DOWNLOAD_ERROR,false);
            return false;
        }else{
            return true;
        }
    }
    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(getActivity(), REQUEST_CODE, PERMISSIONS);
    }*/

/*    private boolean CheckSpaceAvailable() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        stat.restat(Environment.getDataDirectory().getPath());
        long bytesAvailable ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytesAvailable = stat.getBlockSize()*stat.getAvailableBlocksLong();
        }else
            bytesAvailable = (long)stat.getBlockSize()*(long)stat.getAvailableBlocks();
        long megAvailable = bytesAvailable / 1048576;
        tw.chiae.inlive.presentation.ui.main.mergefilm.Log.i("RayTest"," 空間剩餘約："+megAvailable);
        if(megAvailable<=getResources().getInteger(R.integer.max_ks_space)){
            return false;
        }
        return true;
    }*/

/*    public void startKsEvent2(Boolean needFinish) {
        KaraStar.getInstance().open(getActivity(), LocalDataManager.getInstance().getLoginInfo().getUserId(), new KaraStar.ViewHandler() {
            @Override
            public void switchToRecordView(KSWebActivity act, int starId, String starVideoUrl, String name) {
                String FilePath = VCamera.getVideoCachePath() + "K_star_rec_" + starId + "_" + LocalDataManager.getInstance().getLoginInfo().getUserId() + "/0.mp4";
                Log.i("RayTest", "path3 " + FilePath);
                if (FileUtils.checkFile(FilePath)) {
                    Intent it = MediaPlayerActivity.createIntent(getActivity(), FilePath, name, starVideoUrl, starId);
                    act.startActivityForResult(it, KaraStar.REQUEST_VIDEO_RECORD);
                    getActivity().finish();
                } else {
                    Log.i("RayTest", "DaYuMediaRecorderActivity");
                    Intent it = MediaRecorderActivity.createIntent(getActivity(), name, starVideoUrl, starId);
                    act.startActivityForResult(it, KaraStar.REQUEST_VIDEO_RECORD);
                    getActivity().finish();
                }
            }

            @Override
            public void switchToUserView(KSWebActivity act, String userId) {
                Log.i("RayTest", "switchToUserView: " + userId);
                act.startActivity(OtherUserActivity.createIntent(getActivity(),
                        Integer.valueOf(userId), false));
            }

        });

    }*/
}
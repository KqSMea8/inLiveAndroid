package tw.chiae.inlive.presentation.ui.login.splash;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/*import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;*/
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import rx.Observer;
import tw.chiae.inlive.BeautyLiveApplication;
import tw.chiae.inlive.data.bean.Banner;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.HotAnchorPageBean;
import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.repository.ParamsRemoteResponse;
import tw.chiae.inlive.data.repository.ServerEventResponse;
import tw.chiae.inlive.data.sharedpreference.PrefsHelper;
import tw.chiae.inlive.domain.BlackList;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.domain.LoginManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;
import tw.chiae.inlive.presentation.ui.login.LoginSelectActivity;
import tw.chiae.inlive.presentation.ui.room.RoomInfoTmp;
import tw.chiae.inlive.util.CETracking;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.FrescoUtil;
import tw.chiae.inlive.util.Jpush.TagAliasBean;
import tw.chiae.inlive.util.Jpush.TagAliasOperatorHelper;
import tw.chiae.inlive.util.L;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static tw.chiae.inlive.util.Jpush.TagAliasOperatorHelper.ACTION_SET;
import static tw.chiae.inlive.util.Jpush.TagAliasOperatorHelper.sequence;


/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class SplashPresenter extends BasePresenter<SplashUiInterface> {

    private final Context context;
    //private final Context context;
    private LoginManager loginManager;
    private Object mainAccountInfo;
    private BlackListManager blackManager;

    public SplashPresenter(Context context, SplashUiInterface uiInterface) {
        super(uiInterface);

        this.context = (Context) uiInterface;

    }
    public void ModifyMode() {
        initParams();
    }

    public void initParams() {
        loginManager = new LoginManager();
        blackManager = new BlackListManager();
        //getMainAccountInfo();
    }

    public void autoLogin() {

        LoginInfo info = LocalDataManager.getInstance().getLoginInfo();
        if (info == null || TextUtils.isEmpty(info.getToken())) {

            L.w("SplashPresenter", "Attempt auto login but login token is null! info=%s", info);
            getUiInterface().failLogin();
            return;
        }else

        getUiInterface().showLoadingText("正在登入...");
        Subscription subscription = loginManager.autoLogin(info.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<LoginInfo>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<LoginInfo> response) {
                        Log.i("RayTest", "onSuccess: " + response.getMsg());
                        getUiInterface().startMainActivity();
                        /*JPushInterface.setAlias(context, response.getData().getUserId(), new TagAliasCallback() {
                            @Override
                            public void gotResult(int i, String s, Set<String> set) {
                                Log.i("RayTest","gotResult: "+s);
                                String RegistrationID = JPushInterface.getRegistrationID(context);
                                Log.i("RayTest","RegistrationID:"+RegistrationID);

                            }
                        });*/
                        //JPushInterface.setAlias(context,0,response.getData().getUserId());
                        Log.i("RayTest","TagAliasBean:"+response.getData().getUserId());
                        TagAliasBean tagAliasBean = new TagAliasBean(response.getData().getUserId());
                        TagAliasOperatorHelper.getInstance().handleAction(context,sequence,tagAliasBean);
                    }

                    @Override
                    protected void onDataFailure(BaseResponse<LoginInfo> response) {
                        //静默请求
//                        super.onDataFailure(response);
                        L.w(LOG_TAG, "auto login failure!");

                        getUiInterface().startLoginSelectActivity();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        //静默请求
//                        super.onError(throwable);

                        getUiInterface().startLoginSelectActivity();
                    }
                });
        addSubscription(subscription);
    }

    public void performFirstStartNavigation(SplashActivity activity) {
        PrefsHelper.setIsFirstRun(false);
        activity.startActivity(LoginSelectActivity.createIntent(activity));
        activity.finish();
    }

    public boolean isFirstRun() {
        return PrefsHelper.getIsFirstRun();
    }

    public void stopCETracking() {
    }

    public void checkServerStat() {

        if(PrefsHelper.getIsFirstRun()){
            getUiInterface().showLoadingText("");
        }else{
            getUiInterface().showLoadingText("檢查伺服器...");
        }

        Subscription subscription = loginManager.checkServerStat()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<ServerEventResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(ServerEventResponse<String> response) {

                        getUiInterface().onResponseServerEvent(response);

                    }
                });
        addSubscription(subscription);
    }

    public void downloadImg() {
        downloadBanner();


    }

    private void downloadBanner() {
        getUiInterface().showLoadingText("下載Banner...");
        Subscription subscription = loginManager.loadHotAnchors(LocalDataManager.getInstance().getLoginInfo().getToken(), "", "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<HotAnchorPageBean>>(getUiInterface()) {

                    @Override
                    public void onSuccess(BaseResponse<HotAnchorPageBean> response) {
                        List<Banner> banners = response.getData().getBanner();

                        LocalDataManager.getInstance().saveBanners(banners);
                        CacheAllImage(banners);
                        //getUiInterface().storeBannerImg(banners);
                    }
                });
        addSubscription(subscription);

    }

    private void CacheAllImage(List<Banner> banners) {
        final int iSize = banners.size();
        final List<String> paths = new ArrayList<>();
        for (Banner banner : banners) {
            FrescoUtil.CacheImgToDisk(banner.getImageUrl(), new FrescoUtil.CacheCallbacek() {
                @Override
                public void cachePath(String path) {
                    paths.add(path);
                    Log.i("RayTest", "path:" + path);
                    if (paths.size() == iSize) {

                        getUiInterface().CompleteDownloadBanner(paths);
                    }
                }
            }, true);
        }

    }

    public void getOfficialList() {
        getUiInterface().showLoadingText("下載官方清單...");
        List<String> officiallist = Arrays.asList(Const.OfficialAccountListID);
        for (String userID : officiallist) {
            getUserInfo(userID, officiallist);
        }

    }

    private void getUserInfo(String userID, final List<String> officiallist) {
        //getUiInterface().showLoadingText("下載官方清單..." + userID);
        Subscription subscription = loginManager.getUserInfo(Integer.parseInt(userID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<UserInfo>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<UserInfo> response) {
                        LocalDataManager.getInstance().saveOfficialListInfo(response.getData());
                        //FrescoUtil.CacheImgToDisk(Const.MAIN_HOST_URL+response.getData().getAvatar());
                        Log.i("RayTest",""+officiallist.size()+" : "+LocalDataManager.getInstance().getOfficialList().size());
                        if (officiallist.size() == LocalDataManager.getInstance().getOfficialList().size())
                            getUiInterface().CompleteOfficialList();
                    }
                });
        addSubscription(subscription);
    }

    /*public void getModifyParams() {
        final int PAGER_JSON = 1;
        getUiInterface().showLoadingText("下載遠端清單...");
        RequestQueue requestQueueAppcation = NoHttp.newRequestQueue();
        final Request<JSONObject> request = NoHttp.createJsonObjectRequest("https://script.google.com/macros/s/AKfycbz-h1b-37CXz9fgBaQZ8spcZccfPm3ygGgNeSdp9YJiNavZhg/exec", RequestMethod.GET);
        requestQueueAppcation.add(PAGER_JSON, request, new OnResponseListener<JSONObject>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<JSONObject> response) {
                if (what == PAGER_JSON) {// 判断what是否是刚才指定的请求
                    JSONObject result = response.get();// 响应结果
                    Gson gson = new Gson();
                    ParamsRemoteResponse paramsResponse = gson.fromJson(result.toString(), ParamsRemoteResponse.class);

                    LoginInfo userInfo = LocalDataManager.getInstance().getLoginInfo();
                    if (userInfo != null) {

                        if (userInfo.getUserId().equals(paramsResponse.getAuth())) {
                            if (paramsResponse.getEnv() == 1) {
                                Const.setEnvironment(true);

                            } else {
                                Const.setEnvironment(false);

                            }
                            if (paramsResponse.getGash() == 0) {
                                Const.IsPayMode=false ;
                            } else {
                                Const.IsPayMode=true ;
                            }
                            Const.MainOfficialAccount=paramsResponse.getMainOfficial();
                            String[] OfficialAccountArray = new String[paramsResponse.getOfficiList().size()];
                            Const.OfficialAccountListID=paramsResponse.getOfficiList().toArray(OfficialAccountArray) ;
                            Log.i("RayTest", "MainOfficialAuccont:" + Const.MainOfficialAccount + "env:" + paramsResponse.getEnv());
                            if (Const.getToast() == 1 && paramsResponse.getEnv() == 0) {
                                getUiInterface().showMsg("已登入正式伺服器");
                            }
                            if (paramsResponse.getToast() == 1 && paramsResponse.getEnv() == 1) {
                                getUiInterface().showMsg("已登入測試伺服器");
                            }
                        }
                        Log.i("RayTest", "修改完畢");
                        if(loginManager==null)
                            loginManager = new LoginManager();

                    }
                }

                getUiInterface().isModifyParams();

            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                Log.i("RayTest", "onFailed:");
                if(loginManager==null)
                    loginManager = new LoginManager();
                getUiInterface().isModifyParams();
            }

            @Override
            public void onFinish(int what) {

            }
        });
    }*/


    public void getMainAccountInfo() {
        Log.i("RayTest","下載主客服清單");
        Subscription subscription = loginManager.getUserInfo(Integer.parseInt(Const.MainOfficialAccount))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<UserInfo>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<UserInfo> response) {
                        LocalDataManager.getInstance().addUserInfo(response.getData());
                        //FrescoUtil.CacheImgToDisk(Const.MAIN_HOST_URL+response.getData().getAvatar());
                        getUiInterface().CompleteMainAccountList();
                    }
                });
        addSubscription(subscription);
    }

    public void getblacklist() {
        Log.i("RayTest","下載黑名單3");
        getUiInterface().showLoadingText("下載黑名單...");
        String uid = LocalDataManager.getInstance().getLoginInfo().getUserId();
        String token = LocalDataManager.getInstance().getLoginInfo().getToken();
        Subscription subscription = blackManager.getblacklist(uid,1,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<BlackList>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUiInterface().CompleteBlackList();
                    }

                    @Override
                    public void onNext(List<BlackList> blackLists) {
                        LocalDataManager.getInstance().saveBlackList(blackLists);
                        getUiInterface().CompleteBlackList();
                    }
                });

        addSubscription(subscription);
    }
}

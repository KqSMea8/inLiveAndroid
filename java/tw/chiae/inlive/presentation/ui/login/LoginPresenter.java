package tw.chiae.inlive.presentation.ui.login;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import rx.Observer;
import tw.chiae.inlive.data.bean.Banner;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.HotAnchorPageBean;
import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.repository.ServerEventResponse;
import tw.chiae.inlive.domain.BlackList;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.domain.LoginManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tw.chiae.inlive.presentation.ui.login.splash.BlackListManager;
import tw.chiae.inlive.util.CETracking;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.FrescoUtil;
import tw.chiae.inlive.util.Jpush.TagAliasBean;
import tw.chiae.inlive.util.Jpush.TagAliasOperatorHelper;

import static tw.chiae.inlive.util.Jpush.TagAliasOperatorHelper.sequence;


/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class LoginPresenter extends BasePresenter<LoginUiInterface> {

    private final BlackListManager blackManager;
    private LoginManager loginManager;
    private Context context;
    public LoginPresenter(LoginUiInterface uiInterface) {
        super(uiInterface);
        loginManager = new LoginManager();
        blackManager = new BlackListManager();
        this.context= (Context) uiInterface;
    }

    public void login(String name, String type){
        Log.i("RayTest","login1");
        Subscription subscription = loginManager.login(name, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<LoginInfo>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<LoginInfo> response) {
                        LocalDataManager.getInstance().saveLoginInfo(response.getData());
                        getUiInterface().startActivityAndFinishOthers();
                        CETracking.getInstance().onUserLogin((Activity) context, response.getData().getUserId());
                        /*JPushInterface.setAlias(context, response.getData().getUserId(), new TagAliasCallback() {
                            @Override
                            public void gotResult(int i, String s, Set<String> set) {
                                Log.i("RayTest","s3:"+s+" result:"+i);
                                String RegistrationID = JPushInterface.getRegistrationID(context);
                                Log.i("RayTest","RegistrationID:"+RegistrationID);
                            }
                        });*/
                        Log.i("RayTest","TagAliasBean:"+response.getData().getUserId());
                        TagAliasBean tagAliasBean = new TagAliasBean(response.getData().getUserId());
                        TagAliasOperatorHelper.getInstance().handleAction(context,sequence,tagAliasBean);
                    }
                });
        addSubscription(subscription);
    }

    public void loginByCaptcha(final String phone, String captcha){
        Log.i("RayTest","login2");
        Subscription subscription = loginManager.loginByCaptcha(phone, captcha)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<LoginInfo>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<LoginInfo> response) {
//                        MobclickAgent.onEvent(context,"phone",phone);
                        LocalDataManager.getInstance().saveLoginInfo(response.getData());
                        getUiInterface().startActivityAndFinishOthers();
                        CETracking.getInstance().onUserLogin((Activity) context, response.getData().getUserId());
                        /*JPushInterface.setAlias(context, response.getData().getUserId(), new TagAliasCallback() {
                            @Override
                            public void gotResult(int i, String s, Set<String> set) {
                                Log.i("RayTest","s2:"+s+" result:"+i);
                                String RegistrationID = JPushInterface.getRegistrationID(context);
                                Log.i("RayTest","RegistrationID:"+RegistrationID);
                            }
                        });*/
                        Log.i("RayTest","TagAliasBean:"+response.getData().getUserId());
                        TagAliasBean tagAliasBean = new TagAliasBean(response.getData().getUserId());
                        TagAliasOperatorHelper.getInstance().handleAction(context,sequence,tagAliasBean);
                    }
                });
        addSubscription(subscription);
    }

    public void sendCaptcha(String phoneNum){
        Subscription subscription = loginManager.sendCaptcha(phoneNum)
                .compose(this.<BaseResponse<String>>applyAsySchedulers())
                .subscribe(new BaseObserver<BaseResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        //Empty
                        getUiInterface().smsSendsSccess(response.getData());
                    }
                });
        addSubscription(subscription);
    }

    public void checkServerStat() {

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
        Subscription subscription = loginManager.loadHotAnchors(LocalDataManager.getInstance().getLoginInfo().getToken(), "", "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<HotAnchorPageBean>>(getUiInterface()) {

                    @Override
                    public void onSuccess(BaseResponse<HotAnchorPageBean> response) {
                        List<Banner> banners = response.getData().getBanner();
                        Log.i("RayTest","checkServerStat onSuccess: "+response.toString());
                        //getUiInterface().storeBannerImg(banners);
                        LocalDataManager.getInstance().saveBanners(banners);
                        CacheAllImage(banners);
                    }
                });
        addSubscription(subscription);

    }

    private void CacheAllImage(List<Banner> banners) {
        final int iSize = banners.size();
        final List<String> paths = new ArrayList<>();
        for(Banner banner :banners){
            FrescoUtil.CacheImgToDisk(banner.getImageUrl(),new FrescoUtil.CacheCallbacek(){
                @Override
                public void cachePath(String path) {
                    paths.add(path);
                    Log.i("RayTest","path:"+path);
                    if(paths.size()==iSize){
                        getUiInterface().CompleteDownloadBanner(paths);
                    }
                }
            },true);
        }

    }
    public void getOfficialList() {

        List<String> officiallist = Arrays.asList(Const.OfficialAccountListID);
        for(String userID : officiallist){
            getUserInfo(userID,officiallist);
        }

    }

    private void getUserInfo(String userID, final List<String> officiallist) {

        Subscription subscription = loginManager.getUserInfo(Integer.parseInt(userID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<UserInfo>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<UserInfo> response) {
                        LocalDataManager.getInstance().saveOfficialListInfo(response.getData());
                        if(officiallist.size() == LocalDataManager.getInstance().getOfficialList().size()) {
                            getblacklist();
                            //getUiInterface().CompleteOfficialList();
                        }
                    }
                });
        addSubscription(subscription);
    }

    public void getblacklist() {
        Log.i("RayTest","下載黑名單2");
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
                        getUiInterface().CompleteOfficialList();
                    }

                    @Override
                    public void onNext(List<BlackList> blackLists) {
                        LocalDataManager.getInstance().saveBlackList(blackLists);
                        getUiInterface().CompleteOfficialList();
                    }
                });

        addSubscription(subscription);
    }
}

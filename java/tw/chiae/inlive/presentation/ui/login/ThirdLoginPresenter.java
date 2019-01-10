package tw.chiae.inlive.presentation.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tw.chiae.inlive.data.bean.Banner;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.HotAnchorPageBean;
import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.data.bean.ThirdLoginPlatform;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.repository.ServerEventResponse;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.domain.LoginManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.google.GooglePlus;

import cn.sharesdk.line.Line;

import rx.Subscription;
import tw.chiae.inlive.util.CETracking;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.FrescoUtil;
import tw.chiae.inlive.util.Jpush.TagAliasBean;
import tw.chiae.inlive.util.Jpush.TagAliasOperatorHelper;


import static com.tencent.bugly.crashreport.inner.InnerAPI.context;
import static tw.chiae.inlive.util.Jpush.TagAliasOperatorHelper.sequence;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class ThirdLoginPresenter extends BasePresenter<LoginUiInterface> {

    private final Context context;
    private LoginManager loginManager;

    public ThirdLoginPresenter(LoginUiInterface uiInterface) {
        super(uiInterface);
        loginManager = new LoginManager();
        this.context= (Context) uiInterface;
    }

    public void thirdLogin(Platform platform, final HashMap<String, Object> res){
        Log.i("RayTest","thirdLogin: "+platform.getName());
        String openId = platform.getDb().getUserId();
        String platformName = formatPlatformName(platform);
        if (platformName.equals("wechat")) {
            res.put("unionid",platform.getDb().get("unionid"));
        }
        Subscription subscription = loginManager.thirdLogin(openId, platformName, res)
                .compose(this.<BaseResponse<LoginInfo>>applyAsySchedulers())
                .subscribe(new BaseObserver<BaseResponse<LoginInfo>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<LoginInfo> response) {

                        LocalDataManager.getInstance().saveLoginInfo(response.getData());
                        getUiInterface().startActivityAndFinishOthers();
                        CETracking.getInstance().onUserLogin((Activity) context, response.getData().getUserId());
                        /*JPushInterface.setAlias(context, response.getData().getUserId(), new TagAliasCallback() {
                            @Override
                            public void gotResult(int i, String s, Set<String> set) {

                                String RegistrationID = JPushInterface.getRegistrationID(context);
                                Log.i("RayTest","RegistrationID:"+RegistrationID);
                            }
                        });*/
                        Log.i("RayTest","TagAliasBean:"+response.getData().getUserId());
                        TagAliasBean tagAliasBean = new TagAliasBean(response.getData().getUserId());
                        TagAliasOperatorHelper.getInstance().handleAction(context,sequence,tagAliasBean);
                    }
                })
        ;
        addSubscription(subscription);
    }

    public void thirdLoginByLine(String openId, HashMap<String, Object> res){
        Subscription subscription = loginManager.thirdLogin(openId, "line", res)
                .compose(this.<BaseResponse<LoginInfo>>applyAsySchedulers())
                .subscribe(new BaseObserver<BaseResponse<LoginInfo>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<LoginInfo> response) {
                        CETracking.getInstance().onUserLogin((Activity) context, response.getData().getUserId());
                        LocalDataManager.getInstance().saveLoginInfo(response.getData());
                        getUiInterface().startActivityAndFinishOthers();
                    }
                })
                ;
        addSubscription(subscription);
    }

    @ThirdLoginPlatform
    private String formatPlatformName(Platform platform){
        String platformName;
        /*if (platform instanceof Wechat || platform instanceof WechatMoments){
            platformName = ThirdLoginPlatform.PLATFORM_WECHAT;
        }
        else*/ if (platform instanceof Facebook ){
            platformName = ThirdLoginPlatform.PLATFORM_FACEBOOK;
        }
        else if (platform instanceof Line){
            platformName = ThirdLoginPlatform.PLATFORM_LINE;
        }
       /* else if (platform instanceof Instagram){
            platformName = ThirdLoginPlatform.PLATFORM_INSTAGRAM;
        }*/else if(platform instanceof GooglePlus){
            platformName=ThirdLoginPlatform.PLATFORM_GOOGLE;
        }
        else {
            throw new IllegalArgumentException(String.format("Unsupported platform %s!", platform));
        }
        return platformName;
    }


    public void downloadData() {
        downloadBanner();
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
                        if(officiallist.size() == LocalDataManager.getInstance().getOfficialList().size())
                            getUiInterface().CompleteOfficialList();
                    }
                });
        addSubscription(subscription);
    }
}

package tw.chiae.inlive.domain;

import android.util.Log;

import com.google.gson.Gson;

import rx.Subscription;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.HotAnchorPageBean;
import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.data.bean.gift.Gift;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.repository.ParamsRemoteResponse;
import tw.chiae.inlive.data.repository.ServerEventResponse;
import tw.chiae.inlive.data.repository.SourceFactory;

import java.util.HashMap;
import java.util.List;

import rx.Observable;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class LoginManager {

    public Observable<BaseResponse<LoginInfo>> register(String username, String password){
        Log.i("RayTestUser","register username: "+username+" pass: "+password);
        return SourceFactory.create().register(username, password);
    }

    public Observable<BaseResponse<LoginInfo>> autoLogin(String token){
        return SourceFactory.create().autoLogin(token);
    }

    public Observable<BaseResponse<LoginInfo>> login(String username, String password){
        Log.i("RayTestUser","login username: "+username+" pass: "+password);
        return SourceFactory.create().login(username, password);
    }

    public Observable<BaseResponse<LoginInfo>> loginByCaptcha(String username, String captcha){
        return SourceFactory.create().loginByCaptcha(username, captcha);
    }

    public Observable<BaseResponse<LoginInfo>> thirdLogin(String openId, String platform,
                                                          HashMap<String, Object> map){
        Gson gson = new Gson();
        String result = gson.toJson(map);
        Log.i("mrl",openId+"   "+platform+"dasdasdsadsa"+result);
        return SourceFactory.create().thirdLogin(openId, platform, result);
    }

    //mob以外的三方
    public Observable<BaseResponse<LoginInfo>> thirdLogin(String openId, String platform, String map){
        return SourceFactory.create().thirdLogin(openId, platform, map);
    }

    public Observable<BaseResponse<String>> sendCaptcha(String phone){
        return SourceFactory.create().sendCaptcha(phone);
    }

    public Observable<ServerEventResponse<String>> checkServerStat() {
        return SourceFactory.createApi3Json().checkServerStat();
    }

    public Observable<BaseResponse<HotAnchorPageBean>> loadHotAnchors(String token, String city, String sex) {
        return SourceFactory.create().loadHotAnchors(token, city, sex);
    }

    public Observable<BaseResponse<UserInfo>> getUserInfo(Integer uid) {
        return SourceFactory.create().getUserInfo(uid);
    }


}

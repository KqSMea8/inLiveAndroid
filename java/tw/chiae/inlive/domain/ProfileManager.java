package tw.chiae.inlive.domain;

import android.text.TextUtils;

import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.UpDataBean;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.repository.SourceFactory;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;

/**
 * 个人信息相关资料管理类。
 * @author Muyangmin
 * @since 1.0.0
 */
public class ProfileManager {

    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_INTRODUCTION = "intro";
    private static final String KEY_GENDER = "sex";

    public Observable<BaseResponse<String>> editNickname(String nickname){
        if (TextUtils.isEmpty(nickname)){
            throw new IllegalArgumentException("Cannot edit nickname to empty.");
        }
        return SourceFactory.create().editProfile(constructJson(KEY_NICKNAME, nickname));
    }

    public Observable<BaseResponse<String>> editIntroduction(String nickname){
        if (TextUtils.isEmpty(nickname)){
            throw new IllegalArgumentException("Cannot edit introduction to empty.");
        }
        return SourceFactory.create().editProfile(constructJson(KEY_INTRODUCTION, nickname));
    }

    public Observable<BaseResponse<String>> editEmotion(String token, int emotion){
        return SourceFactory.create().getEmotion(token,emotion);
    }

    public Observable<BaseResponse<String>> setBirthday(String token, String birthday){
        return SourceFactory.create().setBirthday(token,birthday);
    }

    public Observable<BaseResponse<String>> getProvince(String token, String province,String city){
        return SourceFactory.create().getProvince(token,province,city);
    }

    public Observable<BaseResponse<String>> editJob(String token, String professional){
        return SourceFactory.create().editJob(token,professional);
    }

    private String constructJson(String key, String value){
        JSONObject json = new JSONObject();
        try {
            json.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public Observable<BaseResponse<String>> fixProfile(String nickname, int gender){
        JSONObject json = new JSONObject();
        try {

            json.put(KEY_NICKNAME, nickname);
            json.put(KEY_GENDER, String.valueOf(gender));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("RayTest","json: ");
        Log.i("RayTest","fixProfile: "+json.toString());
        return SourceFactory.create().editProfile(json.toString());
    }

    public Observable<BaseResponse<String>> uploadAvatar(String absPath){
        return SourceFactory.create().uploadAvatar(absPath);
    }

    public Observable<BaseResponse<String>> upLoadMyAddress(String roomid){
        return SourceFactory.create().upLoadMyAddress(roomid);
    }

    public Observable<BaseResponse<String>> upLoadMyRecommen(String uid){
        return SourceFactory.create().upLoadMyRecommen(uid);
    }

    public Observable<BaseResponse<UpDataBean>> upNewAppVersion(String system){
        return SourceFactory.create().upNewAppVersion(system);
    }
}

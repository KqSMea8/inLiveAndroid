package tw.chiae.inlive.presentation.ui.login;

import android.support.annotation.NonNull;

import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.domain.MeFragmentManager;
import tw.chiae.inlive.domain.ProfileManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class UserInfoWritePresenter extends BasePresenter<UserInfoWriteInterface> {

    private MeFragmentManager mManager;
    private ProfileManager pm;

    public UserInfoWritePresenter(UserInfoWriteInterface uiInterface) {
        super(uiInterface);
        pm = new ProfileManager();
        mManager = new MeFragmentManager();
    }

    public void loadUserInfo(Integer uid) {
        Subscription subscription = mManager.getUserInfo(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<UserInfo>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<UserInfo> response) {
                        Log.i("RayTest","get snap:"+ response.getData().getSnap());
                        getUiInterface().onProfileWriteSuccess();
                    }
                });
        addSubscription(subscription);
    }

    public void fixProfile(String nickname, int gender , final int uid){
        Log.i("RayTest","fixProfile:"+uid);
        pm.fixProfile(nickname, gender)
                .compose(this.<BaseResponse<String>>applyAsySchedulers())
                .subscribe(new BaseObserver<BaseResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        //getUiInterface().onProfileWriteSuccess();
                        Log.i("RayTest","fixProfile "+response.getData().getBytes());
                        if(uid!=0)
                            loadUserInfo(uid);
                        else
                            getUiInterface().onProfileWriteSuccess();
                    }
                });
    }
    public void updateEmotion(@NonNull String token, final int emotion){
        pm.editEmotion(token,emotion)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        getUiInterface().onProfileChangeSuccess();
                    }
                });
    }
    public void updateBirthday(@NonNull String token, final String birthday){
        pm.setBirthday(token,birthday)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        getUiInterface().onProfileChangeSuccess();
                    }
                });
    }

    public void updateProvince(@NonNull String token, final String province,String city){
        pm.getProvince(token,province,city)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        getUiInterface().onProfileChangeSuccess();
                    }
                });
    }

    public void saveNickName(String nickname, int gender) {
        pm.fixProfile(nickname, gender)
                .compose(this.<BaseResponse<String>>applyAsySchedulers())
                .subscribe(new BaseObserver<BaseResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        getUiInterface().saveNickNameSuccess();
                    }
                });
    }
}

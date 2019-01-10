package tw.chiae.inlive.presentation.ui.main.me.profile;

import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;

import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.domain.ProfileManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tw.chiae.inlive.util.UnicodeUtil;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class ProfilePresenter extends BasePresenter<ProfileEditInterface> {

    private ProfileManager pm;

    public ProfilePresenter(ProfileEditInterface uiInterface) {
        super(uiInterface);
        pm = new ProfileManager();
    }

    public void updateNickname(@NonNull final String nickname){
        Subscription subscription = pm.editNickname(nickname)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {

                        String name = String.valueOf(Html.fromHtml(nickname));
                        Log.i("RayTest",name+" onSuccess"+ UnicodeUtil.StringUtfDecode(name));
                        getUiInterface().showProfileUpdated(UnicodeUtil.StringUtfDecode(name), response.getData());
                    }
                });
        addSubscription(subscription);
    }

    public void updateIntroduction(@NonNull final String introduction){
        Subscription subscription = pm.editIntroduction(introduction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        getUiInterface().showProfileUpdated(introduction, response.getData());
                    }
                });
        addSubscription(subscription);
    }
    public void updateJob(@NonNull final String token, final String professional){
        Subscription subscription = pm.editJob(token,professional)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        getUiInterface().showProfileUpdated(professional, response.getData());
                    }
                });
        addSubscription(subscription);
    }

}

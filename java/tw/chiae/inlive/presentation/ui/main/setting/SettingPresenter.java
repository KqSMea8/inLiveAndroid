package tw.chiae.inlive.presentation.ui.main.setting;

import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.UpDataBean;
import tw.chiae.inlive.data.bean.room.HitList;
import tw.chiae.inlive.domain.ProfileManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/12/1 0001.
 */

public class SettingPresenter extends BasePresenter<SettingInterface>{
    ProfileManager profileManager;

    protected SettingPresenter(SettingInterface uiInterface) {
        super(uiInterface);
        profileManager=new ProfileManager();
    }

    public void loadMyAddress(String roomid) {
        Subscription subscription = profileManager.upLoadMyAddress(roomid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        getUiInterface().getMyAddress(response.getData());
                    }
                });
        addSubscription(subscription);
    }

    public void upLoadMyRecommen(String uid) {
        Subscription subscription = profileManager.upLoadMyRecommen(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        getUiInterface().upLoadMyRecommen(response.getCode());
                    }
                });
        addSubscription(subscription);
    }

    public void upNewAppVersion(String system) {
        Subscription subscription = profileManager.upNewAppVersion(system)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<UpDataBean>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<UpDataBean> response) {
                        getUiInterface().getNewAppVersion(response.getData());
                    }
                });
        addSubscription(subscription);
    }

}

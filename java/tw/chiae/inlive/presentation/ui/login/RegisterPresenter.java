package tw.chiae.inlive.presentation.ui.login;

import android.support.annotation.NonNull;

import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.domain.LoginManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class RegisterPresenter extends BasePresenter<RegisterUiInterface> {

    private LoginManager manager;

    public RegisterPresenter(RegisterUiInterface uiInterface) {
        super(uiInterface);
        manager = new LoginManager();
    }

    public void performLogin(@NonNull String username, @NonNull String password){
        Subscription subscription = manager.register(username, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<LoginInfo>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<LoginInfo> response) {
                        LocalDataManager.getInstance().saveLoginInfo(response.getData());
                        getUiInterface().gotoMain();
                    }
                });
        addSubscription(subscription);
    }
}

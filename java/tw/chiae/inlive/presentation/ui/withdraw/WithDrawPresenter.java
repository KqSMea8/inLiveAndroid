package tw.chiae.inlive.presentation.ui.withdraw;

import android.support.annotation.NonNull;

import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.transaction.WithDrawRespose;
import tw.chiae.inlive.domain.WithDrawManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by huanzhang on 2016/5/13.
 */
public class WithDrawPresenter extends BasePresenter<IwithDrawNum>{
    private WithDrawManager mManager;
    protected WithDrawPresenter(IwithDrawNum uiInterface) {
        super(uiInterface);
        mManager = new WithDrawManager();
    }
    public void withDraw(@NonNull String num, String account){
        Subscription subscription = mManager.withDraw(num, account)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<WithDrawRespose>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<WithDrawRespose> response) {
                       getUiInterface().commitSuccess(response.getData());
                    }
                });
        addSubscription(subscription);
    }
}

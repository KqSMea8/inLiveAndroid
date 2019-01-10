package tw.chiae.inlive.presentation.ui.main;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.domain.AnchorManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;
import tw.chiae.inlive.presentation.ui.base.page.PageRecorder;
import tw.chiae.inlive.presentation.ui.main.index.HotAnchorInterface;
import tw.chiae.inlive.presentation.ui.main.mergefilm.EventActivity;

/**
 * Created by rayyeh on 2017/7/19.
 */

public class EventPresenter  extends BasePresenter<EventInterface> {


    private final EventManager eventManager;

    public EventPresenter(EventInterface uiInterface) {
        super(uiInterface);
        eventManager = new EventManager();
    }

    public void checkActivateEvent() {
        Subscription subscription = eventManager.checkActivateEvent()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EventActivity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(EventActivity eventActivity) {
                        getUiInterface().UpdateActivateEvent(eventActivity);
                    }
                });
        addSubscription(subscription);
    }

    public void loadOfficialInfo(String accountList) {
        Subscription subscription = eventManager.getUserInfo(Integer.parseInt(accountList))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<UserInfo>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<UserInfo> response) {
                        getUiInterface().showUserInfo(response.getData());
                    }
                });
        addSubscription(subscription);
    }
}

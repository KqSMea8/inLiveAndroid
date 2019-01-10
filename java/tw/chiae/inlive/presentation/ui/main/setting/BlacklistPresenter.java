package tw.chiae.inlive.presentation.ui.main.setting;

import android.support.annotation.NonNull;
import android.util.Log;

import rx.Observer;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.room.HitList;
import tw.chiae.inlive.domain.BlackList;
import tw.chiae.inlive.domain.HitListManager;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.domain.ProfileManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;
import tw.chiae.inlive.presentation.ui.login.splash.BlackListManager;
import tw.chiae.inlive.presentation.ui.main.me.profile.ProfileEditInterface;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class BlacklistPresenter extends BasePresenter<BlacklistInterface> {

    private final BlackListManager mBlackManager;
    private final BlacklistInterface blackInterface;
    private HitListManager pm;

    public BlacklistPresenter(BlacklistInterface uiInterface) {
        super(uiInterface);
        blackInterface = uiInterface;
        pm = new HitListManager();
        mBlackManager = new BlackListManager();
    }

    public void updateInform(String token) {
        Subscription subscription = pm.hitList(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<List<HitList>>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<List<HitList>> response) {
                        getUiInterface().showResult(response.getData());
                    }
                });
        addSubscription(subscription);
    }

    public void updateblackList() {
        Log.i("RayTest","下載黑名單1");
        String uid = LocalDataManager.getInstance().getLoginInfo().getUserId();
        String token = LocalDataManager.getInstance().getLoginInfo().getToken();
        Subscription subscription = mBlackManager.getblacklist(uid,1,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<BlackList>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        blackInterface.CompleteBlackList();
                    }

                    @Override
                    public void onNext(List<BlackList> blackLists) {
                        LocalDataManager.getInstance().saveBlackList(blackLists);
                        blackInterface.CompleteBlackList();
                    }
                });

        addSubscription(subscription);
    }

    public void delBlackList(final String blackUid) {
        Log.i("RayTest","刪除黑名單:"+blackUid);
        String uid = LocalDataManager.getInstance().getLoginInfo().getUserId();
        String token = LocalDataManager.getInstance().getLoginInfo().getToken();
        String id = getBlackUserId(blackUid);
        Subscription subscription = mBlackManager.delblacklist(uid,token,id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<BlackList>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        blackInterface.FailDelBlackList();
                    }

                    @Override
                    public void onNext(List<BlackList> blackLists) {
                        LocalDataManager.getInstance().saveBlackList(blackLists);
                        blackInterface.CompleteDelBlackList(blackLists);
                    }
                });

        addSubscription(subscription);
    }

    private String getBlackUserId(String blackUid) {
        List<BlackList> blackLists = LocalDataManager.getInstance().getmBlackList();
        for(BlackList blackList : blackLists){
            if(blackList.getBlack_id().equals(blackUid))
                return blackList.getId();
        }
        return "0";
    }
}
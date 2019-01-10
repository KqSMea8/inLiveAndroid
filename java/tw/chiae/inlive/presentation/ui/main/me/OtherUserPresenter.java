package tw.chiae.inlive.presentation.ui.main.me;

import android.util.Log;

import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.me.PlayBackInfo;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.domain.MeFragmentManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/7/20 0020.
 */
public class OtherUserPresenter extends BasePresenter {
    private MeFragmentManager mManager;
    private IMe mIme;
    protected OtherUserPresenter(IMe uiInterface) {
        super(uiInterface);
        mIme = uiInterface;
        mManager = new MeFragmentManager();
    }

    public void getPlayList(String token,String roomID) {
        Subscription subscription = mManager.getPlayBack(token,roomID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<List<PlayBackInfo>>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<List<PlayBackInfo>> response) {
                        mIme.getPlayLists(response.getData());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        mIme.getPlayLists(null);
                    }
                });
        addSubscription(subscription);
    }

    public void getPlayBackUrl(String roomID,String start,String end) {
        Subscription subscription = mManager.getPlayBackListUrl(roomID,start, end)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        mIme.getPlayUrl(response.getData());
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        mIme.getPlayLists(null);
                    }
                });
        addSubscription(subscription);
    }


    /**
     * 获取回播私密
     * @param uid
     */
    public void loadBackPrivateLimit(String uid,String urlstart) {
        Subscription subscription = mManager.loadBackPrivateLimit(uid,urlstart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<PrivateLimitBean>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<PrivateLimitBean> response) {
                        mIme.showPrivateLimit(response.getData());
                    }
                });
        addSubscription(subscription);
    }

    /**
     *
     * @param plid  私密限制id
     * @param prerequisite 用户输入的密码
     * @param uid 我的id
     * @param aid 主播id
     */
    public void checkPrivatePass(String type,int plid,String prerequisite,String uid,String aid){
        Subscription subscription = mManager.checkPrivatePass(type,plid,prerequisite,uid,aid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<Object>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<Object> response) {
                        mIme.startGoPlayFragment();
                    }
                });
        addSubscription(subscription);
    }
}

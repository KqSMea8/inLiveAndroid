package tw.chiae.inlive.presentation.ui.main.me;

import android.util.Log;

import rx.Observer;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.HotAnchorPageBean;
import tw.chiae.inlive.data.bean.me.PlayBackInfo;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.data.bean.room.RoomAdminInfo;
import tw.chiae.inlive.domain.BlackList;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.domain.MeFragmentManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;
import tw.chiae.inlive.presentation.ui.login.splash.BlackListManager;
import tw.chiae.inlive.util.L;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by huanzhang on 2016/4/15.
 */
public class MePresenter extends BasePresenter {
    private BlackListManager mBlackManager;
    private MeFragmentManager mManager;
    private IMe mIme;

    protected MePresenter(IMe uiInterface) {
        super(uiInterface);
        mIme = uiInterface;
        mManager = new MeFragmentManager();
        mBlackManager = new BlackListManager();
    }

    public void loadUserInfo(Integer uid) {
        Subscription subscription = mManager.getUserInfo(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<UserInfo>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<UserInfo> response) {
                        mIme.showInfo(response.getData());
                    }
                });
        addSubscription(subscription);
    }



    public void starUser(String token, String userId, String roomid, final int code) {
        Subscription subscription = mManager.starUsr(token, userId, roomid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
//                        mIUser.showUserInfo(response.getData());

                        if(response.getCode()==0)
                            mIme.getStartCode(code);
                        else
                            mIme.getStartCode(response.getCode());
                    }
                });
        addSubscription(subscription);
    }

    public void unStarUser(String token, String userId, String roomid, final int code) {
        Subscription subscription = mManager.unStarUsr(token, userId, roomid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
//                        mIUser.showUserInfo(response.getData());
                        if(response.getCode()==0)
                            mIme.getRemoveStartCode(code);
                        else
                            mIme.getRemoveStartCode(response.getCode());
                    }
                });
        addSubscription(subscription);
    }

    public void getPlayList(String Token, String roomID) {
        Subscription subscription = mManager.getPlayBack(Token, roomID)
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

    public void getPlayBackUrl(String roomID, String start, String end) {
        Subscription subscription = mManager.getPlayBackListUrl(roomID, start, end)
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

    public void setHit(String token, String hitid) {
        Subscription subscription = mManager.setHit(token, hitid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<Object>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<Object> response) {
                        mIme.getHitCode(response.getCode());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                    }
                });
        addSubscription(subscription);
    }

    public void removeHit(String token, String hitid) {
        Subscription subscription = mManager.removeHit(token, hitid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<Object>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<Object> response) {
                        mIme.getRemoveHitCode(response.getCode());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                    }
                });
        addSubscription(subscription);
    }

    //    获取私密
    public void loadPrivateLimit(String uid) {
        Subscription subscription = mManager.loadPrivateLimit(uid)
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
     * @param plid         私密限制id
     * @param prerequisite 用户输入的密码
     * @param uid          我的id
     * @param aid          主播id
     */
    public void checkPrivatePass(String type, int plid, String prerequisite, String uid, String aid) {
        Subscription subscription = mManager.checkPrivatePass(type, plid, prerequisite, uid, aid)
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

    /**
     * 获取回播私密
     *
     * @param uid
     */
    public void loadBackPrivateLimit(String uid, String urlstart) {
        Subscription subscription = mManager.loadBackPrivateLimit(uid, urlstart)
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

    public void addBlackList(final String blackUserId) {
        String uid = LocalDataManager.getInstance().getLoginInfo().getUserId();
        String token = LocalDataManager.getInstance().getLoginInfo().getToken();
        String roomid = LocalDataManager.getInstance().getLoginInfo().getCurrentRoomNum();
        Subscription subscription = mBlackManager.addblacklist(uid,"0",blackUserId,1,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<BlackList>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<BlackList> blackLists) {
                        LocalDataManager.getInstance().saveBlackList(blackLists);
                        mIme.CompleteAddBlackList(blackLists);
                    }
                });

        addSubscription(subscription);
    }

    public void delBlackList(final String blackUid, final int code) {
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

                    }

                    @Override
                    public void onNext(List<BlackList> blackLists) {
                        LocalDataManager.getInstance().saveBlackList(blackLists);
                        mIme.CompleteDelBlackList(blackLists,code);
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

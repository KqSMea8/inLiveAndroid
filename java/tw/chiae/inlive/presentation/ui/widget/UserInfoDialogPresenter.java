package tw.chiae.inlive.presentation.ui.widget;

import android.util.Log;

import rx.Observer;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.bean.room.RoomAdminInfo;
import tw.chiae.inlive.domain.BlackList;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.domain.MeFragmentManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tw.chiae.inlive.presentation.ui.login.splash.BlackListManager;

/**
 * Created by huanzhang on 2016/4/16.
 */
public class UserInfoDialogPresenter extends BasePresenter {
    private final BlackListManager mBlackManager;
    private IUserInfoDialog mIUser;
    private MeFragmentManager mManager;

    public UserInfoDialogPresenter(IUserInfoDialog uiInterface) {
        super(uiInterface);
        mIUser = uiInterface;
        mManager = new MeFragmentManager();
        mBlackManager = new BlackListManager();
    }

    public void loadUserInfo(String userId){
        Subscription subscription = mManager.getUserInfo(Integer.parseInt(userId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<UserInfo>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<UserInfo> response) {
                        mIUser.showUserInfo(response.getData());
                    }
                });
        addSubscription(subscription);
    }
    public void starUser(String token, final String uid, String roomid){
        Subscription subscription = mManager.starUsr(token,uid,roomid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
//                        mIUser.showUserInfo(response.getData());
//                        mIUser.getRemoveHitCode(response.getCode());
                        mIUser.getStartCode(response.getCode(),uid);
                    }
                });
        addSubscription(subscription);
    }

    public void unStarUser(String token, final String uid, String roomid){
        Subscription subscription = mManager.unStarUsr(token,uid,roomid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
//                        mIUser.showUserInfo(response.getData());
                        mIUser.getRemoveStartCode(response.getCode(),uid);
                    }
                });
        addSubscription(subscription);
    }

    public void getAdminList(String token,String uid) {
        Subscription subscription = mManager.getAdmin(token, uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<List<RoomAdminInfo>>>(getUiInterface()) {

                    @Override
                    public void onSuccess(BaseResponse<List<RoomAdminInfo>> response) {
                        Log.i("mrl","這尼瑪1"+response);
                        mIUser.getAdminLists(response.getData());
                        mIUser.adminnullgoinit();
                    }

                    @Override
                    protected void onDataFailure(BaseResponse<List<RoomAdminInfo>> response) {
                        Log.i("aRayTest","onDataFailure");
                        if(response.getCode()==1){
                            Log.i("aRayTest","onDataFailure1"+response.getCode());
                            mIUser.getAdminLists(new ArrayList<RoomAdminInfo>());
                            mIUser.adminnullgoinit();
                        }else {
                            super.onDataFailure(response);
                        }
                    }
                });
        addSubscription(subscription);
    }

    public void setHit(String token, final String hitid){
        Subscription subscription = mManager.setHit(token,hitid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<Object>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<Object> response) {
                        mIUser.getHitCode(response.getCode(),hitid);
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                    }
                });
        addSubscription(subscription);
    }

    public void removeHit(String token, final String hitid){
        Subscription subscription = mManager.removeHit(token,hitid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<Object>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<Object> response) {
                        mIUser.getRemoveHitCode(response.getCode(),hitid);
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
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
                        mIUser.CompleteAddBlackList(blackLists,blackUserId);
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
                        mIUser.CompleteDelBlackList(blackLists,code,blackUid);
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

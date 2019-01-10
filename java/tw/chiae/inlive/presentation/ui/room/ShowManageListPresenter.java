package tw.chiae.inlive.presentation.ui.room;

import android.util.Log;

import tw.chiae.inlive.data.bean.AnchorSummary;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.PageBean;
import tw.chiae.inlive.data.bean.room.RoomAdminInfo;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/7/14 0014.
 */
public class ShowManageListPresenter extends BasePresenter<ShowManageInterface>{

    ShowAdminManager adminManager;
    protected ShowManageListPresenter(ShowManageInterface uiInterface) {
        super(uiInterface);
        adminManager=new ShowAdminManager();
    }

    public void loadAdminList(String token,String roomuid){
        Subscription subscription = adminManager.loadAdminList(token,roomuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<List<RoomAdminInfo>>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<List<RoomAdminInfo>> response) {
                        List<RoomAdminInfo> list = response.getData();
                        if (list==null || list.isEmpty()){
                            getUiInterface().showEmptyResult(null);
                        }
                        else {
                            getUiInterface().showEmptyResult(list);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        getUiInterface().requestOver();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        getUiInterface().requestOver();
                    }
                });
        addSubscription(subscription);
    }

    public void removeAdmin(String token,String roomuid,String adminid){
        Subscription subscription = adminManager.removeAdmin(token,roomuid,adminid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<Object>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<Object> response) {
                        getUiInterface().successAdmin();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        getUiInterface().requestOver();
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        getUiInterface().requestOver();
                    }
                });
        addSubscription(subscription);
    }

}

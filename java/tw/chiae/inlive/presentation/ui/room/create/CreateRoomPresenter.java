package tw.chiae.inlive.presentation.ui.room.create;

import android.hardware.Camera;
import android.util.Log;
import android.util.Size;

import com.google.gson.Gson;

import java.util.List;
import java.util.Vector;

import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.bean.room.ConferenceRoom;
import tw.chiae.inlive.data.bean.PushStreamInfo;
import tw.chiae.inlive.data.bean.ThemBean;
import tw.chiae.inlive.data.bean.room.CreateRoomBean;
import tw.chiae.inlive.domain.AnchorManager;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class CreateRoomPresenter extends BasePresenter<CreateRoomInterface> {

    private AnchorManager anchorManager;

    public CreateRoomPresenter(CreateRoomInterface uiInterface) {
        super(uiInterface);
        anchorManager = new AnchorManager();
    }

    public void generatePushStreaming(){
        String roomId = LocalDataManager.getInstance().getLoginInfo().getCurrentRoomNum();
        Subscription subscription = anchorManager.generatePushStreaming(roomId)
                .compose(this.<BaseResponse<String>>applyAsySchedulers())
                .subscribe(new BaseObserver<BaseResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        String origin = new Gson().toJson(response.getData());
                        getUiInterface().onPushStreamReady(response.getData());
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                    }
                });
        addSubscription(subscription);
    }

//    得到话题Bean
    public void getThemBean(){
        Subscription subscription = anchorManager.getThemBean()
                .compose(this.<BaseResponse<ThemBean>>applyAsySchedulers())
                .subscribe(new BaseObserver<BaseResponse<ThemBean>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<ThemBean> response) {
                        getUiInterface().onThemBean(response.getData());
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        getUiInterface().onThemBean(new ThemBean());
                    }
                });
        addSubscription(subscription);
    }

    //    创建房间传递标题
    public void postCreatRoom(String token, final String title, String roomid, String city, String province, char orientation, String privateString, int privatetype, String approveid ){
        Subscription subscription = anchorManager.postCreatRoom(token,title,roomid,city,province,orientation,privateString,privatetype,approveid)
                .compose(this.<BaseResponse<CreateRoomBean>>applyAsySchedulers())
                .subscribe(new BaseObserver<BaseResponse<CreateRoomBean>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<CreateRoomBean> response) {
                        getUiInterface().onCreateRoom(response.getData(),title);
//                        deletConferenceRoom();
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                    }
                });
        addSubscription(subscription);
    }


    public void createConferenceRoom(){
        Subscription subscription = anchorManager.createConferenceRoom(LocalDataManager.getInstance().getLoginInfo().getCurrentRoomNum(),LocalDataManager.getInstance().getLoginInfo().getCurrentRoomNum())
                .compose(this.<BaseResponse<ConferenceRoom>>applyAsySchedulers())
                .subscribe(new BaseObserver<BaseResponse<ConferenceRoom>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<ConferenceRoom> response) {
                        //这里我真的不想再弄一个对象了。。。
                        getUiInterface().onCreateConferenceRoom(response.getData().getRoom_name());
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                    }
                });
        addSubscription(subscription);
    }

    public void deletConferenceRoom(){
        Subscription subscription = anchorManager.deletConferenceRoom(LocalDataManager.getInstance().getLoginInfo().getCurrentRoomNum())
                .compose(this.<BaseResponse<Object>>applyAsySchedulers())
                .subscribe(new BaseObserver<BaseResponse<Object>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<Object> response) {
                        if (response.getCode()==0){
                            createConferenceRoom();
                        }
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                    }
                });
        addSubscription(subscription);
    }
    public void loadUserInfo(Integer uid) {
        Subscription subscription = anchorManager.getUserInfo(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<UserInfo>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<UserInfo> response) {
                        getUiInterface().showInfo(response.getData());
                    }
                });
        addSubscription(subscription);
    }


}

package tw.chiae.inlive.presentation.ui.room.player;

import com.google.gson.Gson;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.gift.Gift;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.domain.AnchorManager;
import tw.chiae.inlive.domain.GiftManager;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class PlayerPresenter extends BasePresenter<PlayerUiInterface> {

    private GiftManager manager;
    private AnchorManager anchorManager;

    public PlayerPresenter(PlayerUiInterface uiInterface) {
        super(uiInterface);
        manager = new GiftManager();
        anchorManager = new AnchorManager();
    }

    public void loadGiftList(){
        Subscription subscription = manager.getAvailableGifts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<List<Gift>>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<List<Gift>> response) {
                        getUiInterface().showGiftList(response.getData());
                    }
                });
        addSubscription(subscription);
    }

    public void sendGift(String toUserId, String giftId, int count){
        Subscription subscription = manager.sendGift(toUserId, giftId, count)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<Object>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<Object> response) {
                        //Empty
                    }
                });
        addSubscription(subscription);
    }

    //    发送红包
    public void sendHongBaoGift(String token, String roomuid, String giftid){
        Subscription subscription = manager.sendHongBaoGift(token, roomuid, giftid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<Object>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<Object> response) {
                    }
                });
        addSubscription(subscription);
    }



    public void loadPlaybackUrl(String roomId){
        Log.i("RayTest","loadPlaybackUrl:"+roomId);
        Subscription subscription = anchorManager.getPlaybackUrl(roomId)
                .compose(this.<BaseResponse<String>>applyAsySchedulers())
                .subscribe(new BaseObserver<BaseResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        getUiInterface().onPlaybackReady(response.getData());
                    }
                });
        addSubscription(subscription);
    }

    public void starUser(String token,String uid,String roomid){
        Subscription subscription = anchorManager.starUsr(token,uid,roomid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
//                        mIUser.showUserInfo(response.getData());
//                        mIUser.getRemoveHitCode(response.getCode());
                        getUiInterface().getStartCode(response.getCode());
                    }
                });
        addSubscription(subscription);
    }

    public void unStarUser(String token,String uid,String roomid){
        Subscription subscription = anchorManager.unStarUsr(token,uid,roomid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
//                        mIUser.showUserInfo(response.getData());
                        getUiInterface().getRemoveStartCode(response.getCode());
                    }
                });
        addSubscription(subscription);
    }

    //    获取一下用户信息= = 就是获取那个是否直播
    public void loadUserInfo(String userId){
        Subscription subscription = anchorManager.getUserInfo(Integer.parseInt(userId))
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


    public void generatePushStreaming(){
        String roomId = LocalDataManager.getInstance().getLoginInfo().getCurrentRoomNum();
        Subscription subscription = anchorManager.generatePushStreaming(roomId)
                .compose(this.<BaseResponse<String>>applyAsySchedulers())
                .subscribe(new BaseObserver<BaseResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        String origin = new Gson().toJson(response.getData());
                        getUiInterface().onMyPushReady(origin);
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                    }
                });
        addSubscription(subscription);
    }

    public void sendFlyDanMuMsg(String roomuid, String content){
        Log.i("RayTest","sendFlyDanMuMsg");
        Subscription subscription = anchorManager.sendDanmuMsg(roomuid,content)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<Object>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<Object> response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response.getData().toString());
                            getUiInterface().upDataLoginBalance( jsonObject.getString("coinbalance"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        addSubscription(subscription);
    }
}

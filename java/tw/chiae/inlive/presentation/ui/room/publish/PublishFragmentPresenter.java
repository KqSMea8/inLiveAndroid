package tw.chiae.inlive.presentation.ui.room.publish;

import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import retrofit2.Call;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import tw.chiae.inlive.data.bean.AnchoBean;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.CameraSize;
import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.data.repository.HotPointResponse;
import tw.chiae.inlive.data.repository.RecordRoomResponse;
import tw.chiae.inlive.data.repository.ServerEventResponse;
import tw.chiae.inlive.domain.AnchorManager;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.domain.PublishTimeInfo;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;
import tw.chiae.inlive.presentation.ui.chatting.utils.FileHelper;
import tw.chiae.inlive.presentation.ui.chatting.utils.SharePreferenceManager;
import tw.chiae.inlive.presentation.ui.room.HotPointInfo;
import tw.chiae.inlive.presentation.ui.room.RoomActivity;
import tw.chiae.inlive.presentation.ui.widget.CustomToast;
import tw.chiae.inlive.util.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class PublishFragmentPresenter extends BasePresenter<PublishFragmentUiInterface>
        implements CameraSizePicker {

    private AnchorManager anchorManager;
    private PublishFragmentCallback mCallback;

    public PublishFragmentPresenter(PublishFragmentUiInterface uiInterface) {
        super(uiInterface);
        anchorManager = new AnchorManager();
    }

    public void startLive(){
        Subscription subscription = anchorManager.startLive(LocalDataManager.getInstance().getLoginInfo().getToken())
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

    public void stopLive(){

        // token會遺失  須防呆檢查  閃退已知問題
        Log.i("RayTest","stop live  ");
        String token;
        /*if(LocalDataManager.getInstance().getLoginInfo()==null){
            mCallback.Logout();
            return;
        }*/
   /*     if(LocalDataManager.getInstance().getLoginInfo()==null) {
            token = LocalDataManager.getInstance().getTokenTmp();
            Log.i("RayTest","tmp token : "+token);
        }else*/
            token = LocalDataManager.getInstance().getLoginInfo().getToken();
/*
        if(LocalDataManager.getInstance().getLoginInfo()==null) {
            token = LocalDataManager.getInstance().getTokenTmp();
            Log.i("RayTest","tmp token : "+token);
        }*/

        Subscription subscription = anchorManager.stopLive(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<Object>>(getUiInterface()) {
                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        Log.i("RayTest","onError");
                    }

                    @Override
                    public void onSuccess(BaseResponse<Object> response) {
                        //Empty

                    }

                    @Override
                    protected void onDataFailure(BaseResponse<Object> response) {
                        super.onDataFailure(response);
                        Log.i("RayTest","onDataFailure"+response);
                    }
                });
        addSubscription(subscription);
    }



    public void saveFrontCameraSize(@NonNull CameraSize front) {
        LocalDataManager.getInstance().saveCameraSize(front, true);
    }

    public void saveBackCameraSize(@NonNull CameraSize back) {
        LocalDataManager.getInstance().saveCameraSize(back, false);
    }

    @Nullable
    public CameraSize getBackCameraSize() {
        return LocalDataManager.getInstance().getCameraSize(false);
    }

    @Nullable
    public CameraSize getFrontCameraSize() {
        return LocalDataManager.getInstance().getCameraSize(true);
    }

    @NonNull
    @Override
    public CameraSize selectBestSize(List<Camera.Size> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("Null or empty list not allowed!");
        }
        LinkedList<Camera.Size> candidateSizes = new LinkedList<>();
        LinkedList<CameraSize> whiteListItems = new LinkedList<>();
        for (Camera.Size size : list) {
            if (candidateSizes.isEmpty()) {
                candidateSizes.add(size);
                continue;
            }

            //add white list adjustment
            for (CameraSize item : WHITE_LIST) {
                if (item.height == size.height && item.width == size.width) {
                    whiteListItems.add(item);
                }
            }

            int widthDiff = Math.abs(size.width - STD_WIDTH);
            int widthDiffInCandidates = Math.abs(candidateSizes.get(0).width - STD_WIDTH);
            if (widthDiff < widthDiffInCandidates) {
                candidateSizes.clear();
                candidateSizes.add(size);
            } else if (widthDiff == widthDiffInCandidates) {
                candidateSizes.add(size);
            }
            //else : do nothing
        }

        //优先选择白名单中的分辨率
        if (!whiteListItems.isEmpty()){
            L.i("PickSize", "Using white list item!");
            //按照与宽度的差值排序
            Collections.sort(whiteListItems, new Comparator<CameraSize>() {
                @Override
                public int compare(CameraSize lhs, CameraSize rhs) {
                    int lDiff = Math.abs(STD_WIDTH-lhs.width);
                    int rDiff = Math.abs(STD_WIDTH-rhs.width);
                    return Integer.valueOf(lDiff).compareTo(rDiff);
                }
            });
            return whiteListItems.get(0);
        }
        L.d("PickSize", "candidates:%s", candidateSizes);

        Camera.Size finalSize = null;
        for (Camera.Size size : candidateSizes) {
            if (finalSize == null) {
                finalSize = size;
                continue;
            }
            int heightDiff = Math.abs(size.width - STD_HEIGHT);
            int heightDiffFinal = Math.abs(finalSize.width - STD_HEIGHT);
            //Here == is impossible
            if (heightDiff < heightDiffFinal) {
                finalSize = size;
            }
        }
        //Here finalSize won't be null.
        if (finalSize == null) {
            throw new IllegalStateException("Final size shouldn't be null!");
        }
        CameraSize wrapper = new CameraSize(finalSize.width, finalSize.height);
        L.i("PickSize", "Finally use size %s", wrapper);
        return wrapper;
    }

    //提醒微直播切换横竖屏咯
    public void onRoomOrientationChange(String roomid,String orientation) {
        Subscription subscription = anchorManager.onRoomOrientationChange(roomid,orientation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        //是否发送切换恒生活成功
                    }
                });
        addSubscription(subscription);
    }


    public void deletConferenceRoom(String roomName){
        Subscription subscription = anchorManager.deletConferenceRoom(roomName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<Object>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<Object> response) {
                        //是否发送切换恒生活成功
                    }
                });
        addSubscription(subscription);
    }

    public void sendFlyDanMuMsg(String roomuid, String content){

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

    public void setPublishCallback(PublishFragmentCallback callback){
        this.mCallback = callback;
    };

    public void setupCreateRequest(String wsUserId, String wsRoomId, String publishRoomTitle) {

        Subscription subscription = anchorManager.RecordRoomNote2(wsUserId,wsRoomId,publishRoomTitle)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        getUiInterface().upDataRoomStreamID(s);
                    }
                });
        addSubscription(subscription);

    }

    public void setupEndRoomRequest(String creatRoomUid, String creatRoomroomid, String creatRoomStreamID) {
        Log.i("RayTest","setupEndRoomRequest");
        Log.i("RayTest","creatRoomUid: "+creatRoomUid);
        Log.i("RayTest","creatRoomroomid: "+creatRoomroomid);
        Log.i("RayTest","creatRoomStreamID: "+creatRoomStreamID);
        if(anchorManager==null){
            Log.i("RayTest","anchorManager null ");
        }else
            Log.i("RayTest","anchorManager OK ");
        Subscription subscription = anchorManager.RecordRoomNoteEnd(creatRoomUid,creatRoomroomid,creatRoomStreamID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("RayTest","setupEndRoomRequest onError"+ e.getMessage());
                    }

                    @Override
                    public void onNext(String s) {
                        //getUiInterface().upDataEndRoom(s);
                        Log.i("RayTest","setupEndRoomRequest onCompleted");
                    }
                });
        addSubscription(subscription);
    }

    public void getHotPointRequest(String mAnchorId) {
        Log.i("RayTest","getHotPointRequest");
        Subscription subscription = anchorManager.getHotPoint(mAnchorId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<HotPointResponse<HotPointInfo>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getUiInterface().EndHotPoint(0);
                    }

                    @Override
                    public void onNext(List<HotPointResponse<HotPointInfo>> hotPointResponses) {
                        Log.i("RayTest","getHotPointRequest2: "+hotPointResponses.get(0).toString());

                        getUiInterface().EndHotPoint(hotPointResponses.get(0).getCoin());
                    }
                });
        addSubscription(subscription);
    }

    public void getStartPublishTime(String wsUserId) {
        Subscription subscription = anchorManager.getStartPulishTime(wsUserId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<PublishTimeInfo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<PublishTimeInfo> publishTimeInfos) {
                        Log.i("RayTest","PublishTimeInfo:"+publishTimeInfos.get(0).getStarttime());
                        getUiInterface().setupStartPublishTime(publishTimeInfos.get(0).getStarttime());
                    }
                });
        addSubscription(subscription);
    }

    public void getViewPagerJson(String user_id, String user_token) {

        Subscription subscription = anchorManager.getViewPagerJson(user_id,user_token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<String>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {

                        getUiInterface().setBalamceValue(response.getData());
                    }
                });
        addSubscription(subscription);
    }

    public void sendLiveCheck() {
        LoginInfo userinfo = LocalDataManager.getInstance().getLoginInfo();
        String uid = userinfo.getUserId();
        String token = userinfo.getToken();
        Subscription subscription = anchorManager.sendLiveChek(uid,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        getUiInterface().sendLiveMsg("開播中");

                    }
                });
        addSubscription(subscription);
    }

    public interface PublishFragmentCallback {

        void Logout();
    }
}

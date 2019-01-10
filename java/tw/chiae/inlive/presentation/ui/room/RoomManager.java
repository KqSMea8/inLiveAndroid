package tw.chiae.inlive.presentation.ui.room;


import rx.Completable;
import rx.Observable;
import tw.chiae.inlive.data.bean.BaseResponse;

import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.presentation.ui.main.mergefilm.EventActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;

/**
 * Created by rayyeh on 2017/7/11.
 */

public class RoomManager {
    public Observable<EventActivity> checkActivateEvent() {
        return SourceFactory.createApi3Json().checkActivateEvent();
    }

    public Observable<String> sendSpeedReport(String uid, String uip, int type ,String rip, String remark) {
        return SourceFactory.createApi3String().sendSpeedReport(uid,uip,type,rip,remark);
    }

    public Observable<BaseResponse<UserInfo>> getUserInfo(Integer uid){
        Log.i("RayTest","getUserInfo : "+uid);
        return SourceFactory.create().getUserInfo(uid);
    }
}

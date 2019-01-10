package tw.chiae.inlive.presentation.ui.main;

import rx.Completable;
import rx.Observable;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.presentation.ui.main.mergefilm.EventActivity;

/**
 * Created by rayyeh on 2017/7/19.
 */

public class EventManager {

    public Observable<EventActivity> checkActivateEvent() {
        return SourceFactory.createApi3Json().checkActivateEvent();
    }

    public Observable<BaseResponse<UserInfo>> getUserInfo(Integer uid){
        return SourceFactory.create().getUserInfo(uid);
    }

}

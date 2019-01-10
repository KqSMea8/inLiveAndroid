package tw.chiae.inlive.data.repository;

import rx.Observable;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.presentation.ui.main.mergefilm.EventActivity;

/**
 * Created by rayyeh on 2017/7/11.
 */

public interface ISourceApi2 {

    Observable<EventActivity> checkActivateEvent();


}

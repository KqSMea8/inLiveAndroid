package tw.chiae.inlive.data.repository;

import java.util.List;

import retrofit2.Call;
import rx.Observable;
import rx.Subscription;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.domain.BlackList;
import tw.chiae.inlive.domain.PublishTimeInfo;
import tw.chiae.inlive.presentation.ui.main.mergefilm.EventActivity;
import tw.chiae.inlive.presentation.ui.main.setting.BlacklistPresenter;
import tw.chiae.inlive.presentation.ui.room.HotPointInfo;

/**
 * Created by rayyeh on 2017/7/6.
 */

public interface ISourceApi3 {
    /**
     * @return
     */
    Observable<ServerEventResponse<String>> checkServerStat();

    /**
     * 紀錄房間訊息
     * @param wsUserId
     * @param wsRoomId
     * @param publishRoomTitle
     * @return
     */
    Observable<RecordRoomResponse<String>> RecordRoomNote(String wsUserId, String wsRoomId, String publishRoomTitle);

    Observable<String> RecordRoomNote2(String wsUserId, String wsRoomId, String publishRoomTitle);

    Observable<String> RecordRoomNoteEnd(String wsUserId, String wsRoomId, String wsRoomStreamID);

    Observable<List<HotPointResponse<HotPointInfo>>> getHotPoint(String mAnchorId);

    Observable<List<PublishTimeInfo>> getStartPulishTime(String wsUserId);

    Observable<String> sendSpeedReport(String uid, String uip, int type, String rip, String remark);

    Observable<EventActivity> checkActivateEvent();

    Observable<List<BlackList>> getblacklist(String uid, int type, String token);

    Observable<List<BlackList>> addblacklist(String uid, String roomid , String blackid ,int type, String token);

    Observable<List<BlackList>> delblacklist(String uid, String token, String id);

    Observable<String> sendLiveChek(String uid, String token);

    Observable<List<HotPointResponse<HotPointInfo>>> getHotPoint_Test(String mAnchorId);
}

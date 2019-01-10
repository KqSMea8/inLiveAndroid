package tw.chiae.inlive.data.repository;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.domain.BlackList;
import tw.chiae.inlive.domain.PublishTimeInfo;
import tw.chiae.inlive.presentation.ui.main.mergefilm.EventActivity;
import tw.chiae.inlive.presentation.ui.room.HotPointInfo;

/**
 * Created by rayyeh on 2017/7/6.
 */

interface RetrofitApi3 {

    @GET("api3/get_server_status")
    Observable<ServerEventResponse<String>> checkServerStat();

    @GET("RoomManager/create_room")
    Observable<RecordRoomResponse<String>> RecordRoomNote(@Query("uid") String wsUserId,
                                                           @Query("roomid") String wsRoomId,
                                                           @Query("title") String publishRoomTitle);

    @GET("RoomManager/create_room")
    Observable<String> RecordRoomNote2(@Query("uid") String wsUserId,
                                 @Query("roomid") String wsRoomId,
                                 @Query("title") String publishRoomTitle);

    @GET("RoomManager/close_room")
    Observable<String> RecordRoomNoteEnd(@Query("uid") String wsUserId,
                                       @Query("roomid") String wsRoomId,
                                       @Query("streamid") String streamID);

    @GET("hotpoint/getdaimond/{mAnchorId}")
    Observable<List<HotPointResponse<HotPointInfo>>> getHotPoint(@Path("mAnchorId")String mAnchorId);

    @GET("RoomManager/get_strarttime")
    Observable<List<PublishTimeInfo>> getStartPulishTime(@Query("uid") String wsUserId);

    @POST("Userlog/addlog")
    Observable<String> sendSpeedReport(@Query("uid")String uid,
                                       @Query("uip")String uip,
                                       @Query("type")int type,
                                       @Query("rip")String rip,
                                       @Query(value = "remark", encoded=true) String remark);

/*    @GET("api3/get_event_settings")
     Observable<EventActivity> checkActivateEvent();*/

    @GET("api3/get_event_settings")
    Observable<EventActivity> checkActivateEvent();

    @GET("https://testapi3.inlive.tw/api3/get_event_settings")
    Observable<EventActivity> checkActivateEvent_Test();

    @GET("BlacklistManager/get_my_blacklist")
    Observable<List<BlackList>> getblacklist(@Query("uid")String uid,
                                             @Query("token")String token,
                                             @Query("type")int type);

    @GET("BlacklistManager/add_user_to_blacklist")
    Observable<List<BlackList>> addblacklist(@Query("uid")String uid,
                                    @Query("roomid")String roomid,
                                    @Query("blackid")String blackid,
                                    @Query("type")int type,
                                    @Query("token")String token);

    @GET("BlacklistManager/delete_user_from_blacklist")
    Observable<List<BlackList>> delblacklist(@Query("uid")String uid,
                                    @Query("token")String token,
                                    @Query("id")String id);

    @GET("https://testapi3.inlive.tw/BlacklistManager/get_my_blacklist")
    Observable<List<BlackList>> getblacklist_test(@Query("uid")String uid,
                                                  @Query("token")String token,
                                                  @Query("type")int type);

    @GET("https://testapi3.inlive.tw/BlacklistManager/add_user_to_blacklist")
    Observable<List<BlackList>> addblacklist_test(@Query("uid")String uid,
                                         @Query("roomid")String roomid,
                                         @Query("blackid")String blackid,
                                         @Query("type")int type, @Query("token")String token);

    @GET("https://testapi3.inlive.tw/BlacklistManager/delete_user_from_blacklist")
    Observable<List<BlackList>> delblacklist_test(@Query("uid")String uid,
                                         @Query("token")String token,
                                         @Query("id")String id);

    @GET("CDN/livecheck")
    Observable<String> sendLiveChek(@Query("token")String token,@Query("uid")String uid);

    @GET("https://testapi3.inlive.tw/hotpoint/getdaimond/{mAnchorId}")
    Observable<List<HotPointResponse<HotPointInfo>>> getHotPointTest(@Path("mAnchorId")String mAnchorId);
}

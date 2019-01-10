package tw.chiae.inlive.data.repository;

import tw.chiae.inlive.data.bean.AnchoBean;
import tw.chiae.inlive.data.bean.AnchorSummary;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.room.ConferenceRoom;
import tw.chiae.inlive.data.bean.CurrencyRankItem;
import tw.chiae.inlive.data.bean.GetFriendBean;
import tw.chiae.inlive.data.bean.HotAnchorPageBean;
import tw.chiae.inlive.data.bean.HotAnchorSummary;
import tw.chiae.inlive.data.bean.IncomeBean;
import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.data.bean.PageBean;
import tw.chiae.inlive.data.bean.PushStreamInfo;
import tw.chiae.inlive.data.bean.ThemBean;
import tw.chiae.inlive.data.bean.UpDataBean;
import tw.chiae.inlive.data.bean.gift.Gift;
import tw.chiae.inlive.data.bean.me.PlayBackInfo;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.bean.room.CreateRoomBean;
import tw.chiae.inlive.data.bean.room.HitList;
import tw.chiae.inlive.data.bean.room.LiveRoomEndInfo;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.data.bean.room.PrvMsg;
import tw.chiae.inlive.data.bean.room.RoomAdminInfo;
import tw.chiae.inlive.data.bean.transaction.PresentRecordItem;
import tw.chiae.inlive.data.bean.transaction.RechargeInfo;
import tw.chiae.inlive.data.bean.transaction.WithDrawRespose;
import tw.chiae.inlive.data.bean.websocket.getLikeList;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 定义Retrofit请求的method/param等。
 * Created by huanzhang on 2016/4/11.
 */
interface RetrofitApi {

    @POST("user/register")
    Observable<BaseResponse<LoginInfo>> register(@Query("username") String username,
                                                 @Query("password") String password);

    @POST("user/autoLogin")
    Observable<BaseResponse<LoginInfo>> autoLogin(@Query("token") String token);

    @POST("user/login")
    Observable<BaseResponse<LoginInfo>> login(@Query("username") String username,
                                              @Query("password") String password);

    @GET("anchor/search_detail")
    Observable<BaseResponse<PageBean<AnchorSummary>>> mqueryAnchors
            (@Query("query") String condition,
             @Query("type") String type,
             @Query(QueryConstants.PAGE_NUM) int pageNum);

    @GET("anchor/onlineFriends")
    Observable<BaseResponse<List<HotAnchorSummary>>> loadFollowedLives(@Query(QueryConstants.PAGE_NUM) int pageNum);

    //全部關注人
    @GET("User/followees")
    Observable<BaseResponse<getLikeList>> loadFollowedList(@Query(QueryConstants.PAGE_NUM) int pageNum);

    @GET("Topic/getTopicUser")
    Observable<BaseResponse<List<HotAnchorSummary>>> loadTopicLives(@Query("topicId") int topicID);

    @GET("anchor/hot")
    Observable<BaseResponse<HotAnchorPageBean>> loadHotAnchors(@Query(QueryConstants.PAGE_NUM) int pageNum);

    @GET("newIdeas/recommend")
    Observable<BaseResponse<HotAnchorPageBean>> loadHotAnchors(@Query("token") String token,
                                                               @Query("province") String province,
                                                               @Query("sex") String sex);


    @GET("anchor/recommend")
    Observable<BaseResponse<PageBean<AnchorSummary>>> loadRecommendAnchors(@Query("token") String token,
                                                                           @Query("order") String time,
                                                                           @Query("size") String size,
                                                                           @Query("page") String page
                                                                            );

    @GET("anchor/city")
    Observable<BaseResponse<PageBean<AnchorSummary>>> loadCommendAnchors(@Query("token") String token,
                                                                         @Query("city") String city);

    @GET("user/follow")
    Observable<BaseResponse<Object>> followAnchor(@Query("uid") String uid);

    @GET("user/unfollow")
    Observable<BaseResponse<Object>> unfollowAnchor(@Query("uid") String uid);

    @GET("user/profile")
    Observable<BaseResponse<UserInfo>> getUserInfo(@Query("uid") Integer uid);

    @GET("User/setEmotion")
    Observable<BaseResponse<String>> getEmotion(@Query("token") String token,
                                                @Query("emotion") int emotion);

    @GET("User/setBirthday")
    Observable<BaseResponse<String>> setBirthday(@Query("token") String token,
                                                 @Query("birthday") String birthday);

    @GET("User/setProvince")
    Observable<BaseResponse<String>> getProvince(@Query("token") String token,
                                                 @Query("province") String province,
                                                 @Query("city") String city);

    @GET("Anchor/getAnchorBean")
    Observable<BaseResponse<AnchoBean>> getAnchoBean(@Query("user_id") String user_id);

    @GET("user/follow")
    Observable<BaseResponse<String>> starUser(@Query("token") String token,
                                              @Query("uid") String uid,
                                              @Query("roomid") String roomid);

    @GET("user/unfollow")
    Observable<BaseResponse<String>> unStarUser(@Query("token") String token,
                                                @Query("uid") String uid,
                                                @Query("roomid") String roomid);

    @GET("user/followees")
    Observable<BaseResponse<PageBean<AnchorSummary>>> getUserStars(@Query("uid") String uid,
                                                                   @Query(QueryConstants.PAGE_NUM) int pageNum);

    @GET("user/followers")
    Observable<BaseResponse<PageBean<AnchorSummary>>> getUserFans(@Query("uid") String uid,
                                                                  @Query(QueryConstants.PAGE_NUM) int pageNum);

    @GET("gift/collection")
    Observable<BaseResponse<List<Gift>>> getAvailableGifts();

    @POST("gift/send")
    Observable<BaseResponse<Object>> sendGift(@Query("to_uid") String toUserId,
                                              @Query("gift_id") String giftId,
                                              @Query("count") int count);

    @POST("Gift/sendredgift")
    Observable<BaseResponse<Object>> sendHongBaoGift(@Query("token") String token,
                                                     @Query("to_uid") String roomuid,
                                                     @Query("gift_id") String giftid);

    @POST("user/edit")
    Observable<BaseResponse<String>> editProfile(@Query("profile") String profileJson);

    @POST("User/setProfessional")
    Observable<BaseResponse<String>> editJob(@Query("token") String token,
                                             @Query("professional") String professional);

    @POST("anchor/live")
    Observable<BaseResponse<Object>> setLiveStatus(@Query("token") String token,
                                                   @Query("status") String status);

    @GET("room/entryOfflineroom")
    Observable<BaseResponse<LiveRoomEndInfo>> getLiveRoomInfo(@Query("roomnum") String roomId);

    @GET("user/getchargeoption")
    Observable<BaseResponse<RechargeInfo>> getRechargeInfo();

    @POST("user/location")
    Observable<BaseResponse<Object>> reportLocation(@Query("lat") String lat, @Query("lng") String
            lng);

    @GET("user/contributeList")
    Observable<BaseResponse<PageBean<CurrencyRankItem>>> getCurrencyRankList(@Query("user_id") String uid,
                                                                             @Query(QueryConstants.PAGE_NUM) int pageNum);

    @POST("user/uploadavatar")
    @Multipart
    Observable<BaseResponse<String>> uploadAvatar(@Part MultipartBody.Part file);

    @GET("user/income")
    Observable<BaseResponse<IncomeBean>> getIncomeBean();

    @POST("user/incometocash")
    Observable<BaseResponse<WithDrawRespose>> withDraw(@Query("num") String num,
                                                       @Query("account") String account);

    @GET("User/cashhistory/")
    Observable<BaseResponse<List<PresentRecordItem>>> getPresentRecord();

    @POST("auth/login")
    Observable<BaseResponse<LoginInfo>> thirdLogin(@Query("openid") String openId,
                                                   @Query("type") String platform,
                                                   @Query("payload") String extras);

    @POST("payment/aliPay")
    Observable<BaseResponse<String>> generateRechargeOrder(@Query("num") String account);

    @POST("payment/appWeixin")
    Observable<BaseResponse<String>> generateRechargeWechat(@Query("num") String account);

    @GET("CDN/getpushaddr")
    Observable<BaseResponse<String>> generatePushStreaming(@Query("roomID") String roomId);

    @GET("CDN/getpulladdr")
    Observable<BaseResponse<String>> getPlaybackUrl(@Query("roomID") String roomId);

    @POST("SMS/sendSMS")
    Observable<BaseResponse<String>> sendCaptcha(@Query("phone") String phone);

    @POST("SMS/verify")
    @FormUrlEncoded
    Observable<BaseResponse<LoginInfo>> loginByCaptcha(@Field("phone") String phone,
                                                       @Field("captcha") String captcha);

    @POST("Room/getAdmin")
    Observable<BaseResponse<List<RoomAdminInfo>>> getAdmin(@Query("token") String token,
                                                           @Query("uid") String uid);

    @POST("room/delAdmin")
    Observable<BaseResponse<Object>> removeAdmin(@Query("token") String token,
                                                 @Query("uid") String uid,
                                                 @Query("adminuid") String adminid);

    @GET("Room/getRoomBack")
    Observable<BaseResponse<List<PlayBackInfo>>> getPlayBack(@Query("token") String token,
                                                             @Query("roomid") String roomid);

    @GET("Qiniu/getPlayback")
    Observable<BaseResponse<String>> getPlayBackListUrl(@Query("roomID") String roomID,
                                                        @Query("startTime") String start,
                                                        @Query("endTime") String end);

    @POST("Topic/getTopic")
    Observable<BaseResponse<ThemBean>> getThemBean();

    @POST("Topic/getTopic")
    Observable<BaseResponse<ThemBean>> getThemBean(@Query("title") String title,
                                                   @Query("count") String number);

    @GET("room/createRoom")
    Observable<BaseResponse<CreateRoomBean>> postCreateRoom(@Query("token") String token,
                                                            @Query("title") String title,
                                                            @Query("roomid") String roomid,
                                                            @Query("address") String address,
                                                            @Query("province") String province,
                                                            @Query("orientation") char orientation,
                                                            @Query("prerequisite") String prerequisite,
                                                            @Query("ptid") int ptid,
                                                            @Query("approveid") String approveid
                                                            );

    @GET("Message/sendMsg")
    Observable<BaseResponse<PrvMsg>> chat(@Query("token") String token,
                                          @Query("to_uid") String to_uid,
                                          @Query("content") String content);

    @GET("User/getHitlist")
    Observable<BaseResponse<List<HitList>>> hitList(@Query("token") String token);

    @GET("User/setHit")
    Observable<BaseResponse<Object>> setHit(@Query("token") String token,
                                            @Query("hituid") String title);

    @GET("User/removeHit")
    Observable<BaseResponse<Object>> removeHit(@Query("token") String token,
                                               @Query("hituid") String title);

    @GET("Friend/getfriend")
    Observable<BaseResponse<List<GetFriendBean>>> getFriendList();

    @GET("room/onRoomOrientationChange")
    Observable<BaseResponse<String>> onRoomOrientationChange(@Query("roomId") String roomID,
                                                             @Query("orientation") String start);

    @GET("Qiniu/createRoom")
    Observable<BaseResponse<ConferenceRoom>> createConferenceRoom(@Query("roomId") String roomID,
                                                                  @Query("roomName") String roomName);

    @GET("Qiniu/roomToken")
    Observable<BaseResponse<String>> getRoomToken(@Query("roomName") String roomID,
                                                  @Query("userId") String roomName,
                                                  @Query("perm") String perm,
                                                  @Query("expireAt") Long expireAt);

    @GET("Qiniu/deleteRoom")
    Observable<BaseResponse<Object>> deletConferenceRoom(@Query("roomName") String roomID);

    @POST("Qiniu/sendMessage")
    Observable<BaseResponse<Object>> sendConferenceMsg(@Query("userId") String roomID,
                                                       @Query("msg") String msg);

    @GET("qiniu/getPullAddress")
    Observable<BaseResponse<String>> upLoadMyAddress(@Query("roomID") String roomID);

    @GET("user/setRecommenUser")
    Observable<BaseResponse<String>> upLoadMyRecommen(@Query("uid") String uid);

    @GET("Config/getAppVersion")
    Observable<BaseResponse<UpDataBean>> upNewAppVersion(@Query("system") String system);

    @POST("Private/privateRecovery")
    Observable<BaseResponse<Object>> publishRecoveryPrivate(@Query("plid") int plid);

    @GET("Private/getNowPrivateLimit")
    Observable<BaseResponse<PrivateLimitBean>> loadPrivateLimit(@Query("uid") String uid);

    @POST("Private/checkPrivatePass")
    Observable<BaseResponse<Object>> loadPrivatePwd(@Query("plid") int plid,
                                                            @Query("prerequisite") String prerequisite,
                                                            @Query("uid") String uid,
                                                            @Query("aid") String aid);

    @POST("Private/checkPrivateLeve")
    Observable<BaseResponse<Object>> loadPrivateLevel(@Query("plid") int plid,
                                                    @Query("uid") String uid,
                                                    @Query("aid") String aid);

    @POST("Private/checkPrivateCharge")
    Observable<BaseResponse<Object>> loadPrivateTicket(@Query("plid") int plid,
                                                    @Query("uid") String uid,
                                                    @Query("aid") String aid);

    @GET("Private/getPrivateLimit")
    Observable<BaseResponse<PrivateLimitBean>> loadBackPrivateLimit(@Query("uid") String uid,
                                                                    @Query("starttime") String urlstart);

    @POST("Gift/sendBarrage")
    Observable<BaseResponse<Object>> sendDanmuMsg(@Query("roomid") String roomid,
                                                       @Query("content") String content);

    @GET("Anchor/getAnchorBean")
    Observable<BaseResponse<String>> getViewPagerJson(@Query("user_id")String user_id,
                                                         @Query("token")String user_token);
}

package tw.chiae.inlive.domain;

import retrofit2.Call;
import rx.Subscription;
import tw.chiae.inlive.data.bean.AnchoBean;
import tw.chiae.inlive.data.bean.AnchorSummary;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.room.ConferenceRoom;
import tw.chiae.inlive.data.bean.GetFriendBean;
import tw.chiae.inlive.data.bean.HotAnchorPageBean;
import tw.chiae.inlive.data.bean.HotAnchorSummary;
import tw.chiae.inlive.data.bean.PageBean;
import tw.chiae.inlive.data.bean.PushStreamInfo;
import tw.chiae.inlive.data.bean.ThemBean;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.bean.room.CreateRoomBean;
import tw.chiae.inlive.data.bean.room.LiveRoomEndInfo;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.data.bean.websocket.getLikeList;
import tw.chiae.inlive.data.repository.HotPointResponse;
import tw.chiae.inlive.data.repository.RecordRoomResponse;
import tw.chiae.inlive.data.repository.SourceFactory;

import java.util.List;

import rx.Observable;
import tw.chiae.inlive.presentation.ui.main.mergefilm.EventActivity;
import tw.chiae.inlive.presentation.ui.room.HotPointInfo;
import tw.chiae.inlive.util.Const;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class AnchorManager {

    private static final String LIVE_STATUS_ON = "on";
    private static final String LIVE_STATUS_OFF = "off";

    public Observable<BaseResponse<PageBean<AnchorSummary>>> mqueryAnchors(String condition,
                                                                           String type,
                                                                           int pageNum) {
        return SourceFactory.create().mqueryAnchors(condition, type,pageNum);
    }

    public Observable<BaseResponse<PageBean<AnchorSummary>>> loadRecommendAnchors(String token, String time ,String size , String page) {
        return SourceFactory.create().loadRecommendAnchors(token, time , size , page);
    }

    public Observable<BaseResponse<PageBean<AnchorSummary>>> loadCommendAnchors(String token, String city) {
        return SourceFactory.create().loadCommendAnchors(token, city);
    }

    //  关注
    public Observable<BaseResponse<List<HotAnchorSummary>>> loadFollowedLives(int pageNum) {
        return SourceFactory.create().loadFollowedLives(pageNum);
    }
    //全部關注
    public Observable<BaseResponse<getLikeList>> loadFollowedList(int pageNum) {
        return SourceFactory.create().loadFollowedList(pageNum);
    }

    //  关于话题的列表哦
    public Observable<BaseResponse<List<HotAnchorSummary>>> loadTopicLives(int topicid) {
        return SourceFactory.create().loadTopicLives(topicid);
    }

    /**
     * 查询首页推荐的热门主播列表，附带Banner图片。
     */
    public Observable<BaseResponse<HotAnchorPageBean>> loadHotAnchors(int pageNum) {
        return SourceFactory.create().loadHotAnchors(pageNum);
    }

    /**
     * 查询首页推荐的热门主播列表，附带Banner图片。
     */
    public Observable<BaseResponse<HotAnchorPageBean>> loadHotAnchors(String token, String city, String sex) {
        return SourceFactory.create().loadHotAnchors(token, city, sex);
    }


    public Observable<BaseResponse<Object>> followAnchor(String anchorUid) {
        return SourceFactory.create().followAnchor(anchorUid);
    }

    public Observable<BaseResponse<Object>> unfollowAnchor(String uid) {
        return SourceFactory.create().unfollowAnchor(uid);
    }

    public Observable<BaseResponse<PageBean<AnchorSummary>>> getAnchorFollowings
            (String uid, int pageNum) {
        return SourceFactory.create().getUserStars(uid, pageNum);
    }

    public Observable<BaseResponse<PageBean<AnchorSummary>>> getAnchorFollowees
            (String uid, int pageNum) {
        return SourceFactory.create().getUserFans(uid, pageNum);
    }

    public Observable<BaseResponse<Object>> startLive(String token) {
        return SourceFactory.create().setLiveStatus(token, LIVE_STATUS_ON);
    }

    public Observable<BaseResponse<Object>> stopLive(String token) {
        return SourceFactory.create().setLiveStatus(token, LIVE_STATUS_OFF);
    }

    public Observable<BaseResponse<LiveRoomEndInfo>> getLiveRoomEndInfo(String roomId) {
        return SourceFactory.create().getLiveRoomInfo(roomId);
    }

    public Observable<BaseResponse<Object>> reportLocation(String lat, String lng) {
        return SourceFactory.create().reportLocation(lat, lng);
    }

    public Observable<BaseResponse<String>> generatePushStreaming(String roomId) {
        return SourceFactory.create().generatePushStreaming(roomId);
    }

    public Observable<BaseResponse<String>> getPlaybackUrl(String roomId) {
        return SourceFactory.create().getPlaybackUrl(roomId);
    }

    public Observable<BaseResponse<AnchoBean>> getAnchoBean(String user_id) {
        return SourceFactory.create().getAnchoBean(user_id);
    }

    public Observable<BaseResponse<ThemBean>> getThemBean() {
        return SourceFactory.create().getThemBean();
    }

    public Observable<BaseResponse<ThemBean>> getThemBean(String title, String number) {
        return SourceFactory.create().getThemBean(title, number);
    }

    public Observable<BaseResponse<CreateRoomBean>> postCreatRoom(String token, String title, String roomid, String city, String province, char orientation, String privateString, int privatetype, String approveid) {
        return SourceFactory.create().postCreatRoom(token, title, roomid, city, province, orientation, privateString, privatetype,approveid);
    }

    public Observable<BaseResponse<String>> starUsr(String token, String uid, String roomid) {
        return SourceFactory.create().starUser(token, uid, roomid);
    }

    public Observable<BaseResponse<String>> unStarUsr(String token, String uid, String roomid) {
        return SourceFactory.create().unStarUser(token, uid, roomid);
    }

    public Observable<BaseResponse<UserInfo>> getUserInfo(Integer uid) {
        return SourceFactory.create().getUserInfo(uid);
    }

    public Observable<BaseResponse<List<GetFriendBean>>> getFriendList() {
        return SourceFactory.create().getFriendList();
    }

    public Observable<BaseResponse<String>> onRoomOrientationChange(String roomId, String orientation) {
        return SourceFactory.create().onRoomOrientationChange(roomId, orientation);
    }

    public Observable<BaseResponse<ConferenceRoom>> createConferenceRoom(String roomId, String roomName) {
        return SourceFactory.create().createConferenceRoom(roomId, roomName);
    }

    public Observable<BaseResponse<String>> getRoomToken(String roomName, String userId, String perm, long expireAt) {
        return SourceFactory.create().getRoomToken(roomName, userId, perm, expireAt);
    }

    public Observable<BaseResponse<Object>> deletConferenceRoom(String roomName) {
        return SourceFactory.create().deletConferenceRoom(roomName);
    }

    public Observable<BaseResponse<Object>> sendConferenceMsg(String userId, String msg) {
        return SourceFactory.create().sendConferenceMsg(userId, msg);
    }

    public Observable<BaseResponse<Object>> publishRecoveryPrivate(int plid) {
        return SourceFactory.create().publishRecoveryPrivate(plid);
    }

    public Observable<BaseResponse<PrivateLimitBean>> loadPrivateLimit(String uid) {
        return SourceFactory.create().loadPrivateLimit(uid);
    }

    public Observable<BaseResponse<Object>> checkPrivatePass(String type,int plid,String prerequisite,String uid,String aid) {
        return SourceFactory.create().checkPrivatePass(type,plid,prerequisite,uid,aid);
    }

    public Observable<BaseResponse<Object>> sendDanmuMsg(String roomuid, String content) {
        return SourceFactory.create().sendDanmuMsg(roomuid,content);
    }


    public Observable<RecordRoomResponse<String>> RecordRoomNote(String wsUserId, String wsRoomId, String publishRoomTitle) {
        return SourceFactory.createApi3String().RecordRoomNote(wsUserId,wsRoomId,publishRoomTitle);
    }

    public Observable<String> RecordRoomNote2(String wsUserId, String wsRoomId, String publishRoomTitle) {
        return SourceFactory.createApi3String().RecordRoomNote2(wsUserId,wsRoomId,publishRoomTitle);
    }

    public Observable<String> RecordRoomNoteEnd(String creatRoomUid, String creatRoomroomid, String creatRoomStreamID) {
        return SourceFactory.createApi3String().RecordRoomNoteEnd(creatRoomUid,creatRoomroomid,creatRoomStreamID);
    }

    public Observable<List<HotPointResponse<HotPointInfo>>> getHotPoint(String mAnchorId) {
        if(Const.TEST_ENVIROMENT_SW)
            return SourceFactory.createApi3Json().getHotPoint_Test(mAnchorId);
        else
            return SourceFactory.createApi3Json().getHotPoint(mAnchorId);

    }

    public Observable<List<PublishTimeInfo>> getStartPulishTime(String wsUserId) {
        return SourceFactory.createApi3Json().getStartPulishTime(wsUserId);
    }

    public Observable getViewPagerJson(String user_id, String user_token) {
        return SourceFactory.create().getViewPagerJson(user_id,user_token);
    }

    public Observable<EventActivity> checkActivateEvent() {
        return SourceFactory.createApi3Json().checkActivateEvent();
    }

    public Observable<String> sendLiveChek(String uid, String token) {
        return SourceFactory.createApi3String().sendLiveChek(uid,token);
    }
}

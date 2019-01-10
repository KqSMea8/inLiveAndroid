package tw.chiae.inlive.domain;

import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.me.PlayBackInfo;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.data.bean.room.RoomAdminInfo;
import tw.chiae.inlive.data.repository.SourceFactory;

import java.util.List;

import rx.Observable;

/**
 * Created by huanzhang on 2016/4/15.
 */
public class MeFragmentManager {


    public Observable<BaseResponse<UserInfo>> getUserInfo(Integer uid){
        return SourceFactory.create().getUserInfo(uid);
    }

    public Observable<BaseResponse<String>> starUsr(String token,String uid,String roomid){
        return SourceFactory.create().starUser(token,uid,roomid);
    }

    public Observable<BaseResponse<String>> unStarUsr(String token,String uid,String roomid){
        return SourceFactory.create().unStarUser(token,uid,roomid);
    }
    //    mrl 管理员的list集合
    public Observable<BaseResponse<List<RoomAdminInfo>>> getAdmin(String token, String user_id) {
        return SourceFactory.create().getAdmin(token,user_id);
    }

    //    mrl 回播list集合
    public Observable<BaseResponse<List<PlayBackInfo>>> getPlayBack(String token,String roomID) {
        return SourceFactory.create().getPlayBack(token,roomID);
    }

    //    mrl 回播的url地址
    public Observable<BaseResponse<String>> getPlayBackListUrl(String roomID,String start, String end) {
        return SourceFactory.create().getPlayBackListUrl(roomID,start,end);
    }

    //    拉黑
    public Observable<BaseResponse<Object>> setHit(String token,String hitid) {
        return SourceFactory.create().setHit(token,hitid);
    }

    //    取消拉黑黑
    public Observable<BaseResponse<Object>> removeHit(String token,String hitid) {
        return SourceFactory.create().removeHit(token,hitid);
    }

    public Observable<BaseResponse<PrivateLimitBean>> loadPrivateLimit(String uid) {
        return SourceFactory.create().loadPrivateLimit(uid);
    }

    public Observable<BaseResponse<Object>> checkPrivatePass(String type,int plid,String prerequisite,String uid,String aid) {
        return SourceFactory.create().checkPrivatePass(type,plid,prerequisite,uid,aid);
    }

    public Observable<BaseResponse<PrivateLimitBean>> loadBackPrivateLimit(String uid,String urlstart) {
        return SourceFactory.create().loadBackPrivateLimit(uid,urlstart);
    }
}

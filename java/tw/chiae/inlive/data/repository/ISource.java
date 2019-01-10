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
import tw.chiae.inlive.data.bean.ThirdLoginPlatform;
import tw.chiae.inlive.data.bean.UpDataBean;
import tw.chiae.inlive.data.bean.gift.Gift;
import tw.chiae.inlive.data.bean.me.PlayBackInfo;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.bean.room.CreateRoomBean;
import tw.chiae.inlive.data.bean.room.HitList;
import tw.chiae.inlive.data.bean.room.LiveRoomEndInfo;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.data.bean.room.RoomAdminInfo;
import tw.chiae.inlive.data.bean.transaction.PresentRecordItem;
import tw.chiae.inlive.data.bean.transaction.RechargeInfo;
import tw.chiae.inlive.data.bean.transaction.WithDrawRespose;
import tw.chiae.inlive.data.bean.websocket.getLikeList;

import java.util.List;

import rx.Observable;

/**
 * 定义网络请求相关方法。
 * Created by huanzhang on 2016/4/11.
 *
 * @since 1.0.0
 */
public interface ISource {

    /**
     * 注册新用户，成功时返回新用户的登录信息。
     *
     * @param username 用户名
     * @param password 密码
     */
    Observable<BaseResponse<LoginInfo>> register(String username, String password);

    /**
     * 使用token执行自动登录。
     *
     * @param token 存储在本地的token。
     */
    Observable<BaseResponse<LoginInfo>> autoLogin(String token);

    /**
     * 使用账户名和密码登录。
     *
     * @param username 用户名，明文
     * @param password 密码，明文
     */
    Observable<BaseResponse<LoginInfo>> login(String username, String password);

    /**
     * 使用账户名和密码登录。
     *
     * @param username 用户名，明文
     * @param captcha  验证码，明文
     */
    Observable<BaseResponse<LoginInfo>> loginByCaptcha(String username, String captcha);

    /**
     * 查询首页推荐的热门主播列表，附带Banner图片。
     */
    Observable<BaseResponse<List<HotAnchorSummary>>> loadFollowedLives(int pageNum);
    //全部關注人
    Observable<BaseResponse<getLikeList>> loadFollowedList(int pageNum);

    /**
     * 查询首页推荐的话题
     */
    Observable<BaseResponse<List<HotAnchorSummary>>> loadTopicLives(int topicID);

    /**
     * 查询首页推荐的热门主播列表，附带Banner图片。
     */
    Observable<BaseResponse<HotAnchorPageBean>> loadHotAnchors(int pageNum);

    /**
     * 查询首页推荐的热门主播列表，附带Banner图片。
     */
    Observable<BaseResponse<HotAnchorPageBean>> loadHotAnchors(String token, String city, String sex);

    /**
     * 查询首页推荐的热门主播列表，附带Banner图片。
     */
    Observable<BaseResponse<PageBean<AnchorSummary>>> loadRecommendAnchors(String token, String time ,String size, String page);

    /**
     * 同城
     */
    Observable<BaseResponse<PageBean<AnchorSummary>>> loadCommendAnchors(String token, String city);

    /**
     * 根据指定的条件查询主播列表。
     *
     * @param condition 查询条件
     * @param pageNum   页号
     */
    Observable<BaseResponse<PageBean<AnchorSummary>>> mqueryAnchors(String condition,String type, int pageNum);

    /**
     * 根据指定的AnchorId来执行关注操作。
     * 该操作为异步静默操作，即点击关注后就会实施UI变更，所以返回值为Object，意为忽略。
     */
    Observable<BaseResponse<Object>> followAnchor(String uid);

    /**
     * 取消对指定用户的关注。
     *
     * @see #followAnchor(String)
     */
    Observable<BaseResponse<Object>> unfollowAnchor(String uid);

    Observable<BaseResponse<UserInfo>> getUserInfo(Integer uid);

    Observable<BaseResponse<String>> getEmotion(String token, int emotion);

    Observable<BaseResponse<String>> setBirthday(String token, String birthday);

    Observable<BaseResponse<String>> getProvince(String token, String province, String city);

    Observable<BaseResponse<AnchoBean>> getAnchoBean(String user_id);

    /**
     * 关注用户
     *
     * @param uid
     * @return
     */
    Observable<BaseResponse<String>> starUser(String token, String uid, String roomid);

    /**
     * 取消关注用户
     *
     * @param uid
     * @return
     */
    Observable<BaseResponse<String>> unStarUser(String token, String uid, String roomid);

    /**
     * 获取用户关注列表
     *
     * @param uid
     * @return
     */
    Observable<BaseResponse<PageBean<AnchorSummary>>> getUserStars(String uid, int pageNum);

    /**
     * 获取用户粉丝列表
     *
     * @param uid
     * @getStars
     */
    Observable<BaseResponse<PageBean<AnchorSummary>>> getUserFans(String uid, int pageNum);

    /**
     * 获取可用的礼物列表。
     */
    Observable<BaseResponse<List<Gift>>> getAvailableGifts();

    /**
     * 送礼物。
     *
     * @param toUserId 接收礼物的人
     * @param giftId   礼物的ID
     * @param count    数量，至少为1
     */
    Observable<BaseResponse<Object>> sendGift(String toUserId, String giftId, int count);

    //    送红包
    Observable<BaseResponse<Object>> sendHongBaoGift(String token, String roomuid, String giftid);

    /**
     * 修改个人资料
     *
     * @param profileJson 待修改的属性组成的JSON字符串。
     */
    Observable<BaseResponse<String>> editProfile(String profileJson);

    Observable<BaseResponse<String>> editJob(String token, String professional);

    /**
     * @param status 新状态
     */
    Observable<BaseResponse<Object>> setLiveStatus(String token, String status);

    /**
     * 获取已结束的直播间信息，包含观看人数和秀币总收入。
     */
    Observable<BaseResponse<LiveRoomEndInfo>> getLiveRoomInfo(String roomId);

    /**
     * 获取充值信息。
     */
    Observable<BaseResponse<RechargeInfo>> getRechargeMap();

    /**
     * 上报用户地理位置
     *
     * @param lat 纬度
     * @param lng 经度
     */
    Observable<BaseResponse<Object>> reportLocation(String lat, String lng);

    /**
     * 获取秀币排行榜。
     */
    Observable<BaseResponse<PageBean<CurrencyRankItem>>> getCurrencyRankList(String uid, int page);

    /**
     * 上传头像。
     *
     * @param path 头像的存储文件地址
     */
    Observable<BaseResponse<String>> uploadAvatar(String path);

    /**
     * 获取收益及提现信息。
     */
    Observable<BaseResponse<IncomeBean>> getIncomeBean();

    /**
     * 提现
     *
     * @param num 金额
     * @return
     */
    Observable<BaseResponse<WithDrawRespose>> withDraw(String num, String account);

    /**
     * 获取提现记录
     *
     * @return
     */
    Observable<BaseResponse<List<PresentRecordItem>>> getPresentRecord();

    /**
     * 第三方登录信息
     *
     * @param openId   第三方平台OpenId
     * @param platform 第三方平台标记
     * @param extras   第三方登录返回的用户资料
     */
    Observable<BaseResponse<LoginInfo>> thirdLogin(String openId, @ThirdLoginPlatform String
            platform, String extras);

    /**
     * 根据指定的金额生成订单信息。
     *
     * @param amount 金额
     * @return 生成的订单号，包含验签等信息。
     */
    Observable<BaseResponse<String>> generateRechargeOrder(String amount);

    Observable<BaseResponse<String>> generateRechargeWechat(String amount);

    /**
     * 生成推流地址。
     *
     * @param roomId 房间号
     * @return 生成的推流地址
     */
    Observable<BaseResponse<String>> generatePushStreaming(String roomId);

    /**
     * 生成拉流地址。
     *
     * @param roomId 房间号
     */
    Observable<BaseResponse<String>> getPlaybackUrl(String roomId);

    /**
     * 给指定电话号码发送短信验证码。
     */
    Observable<BaseResponse<String>> sendCaptcha(String phone);

    //    管理员列表
    Observable<BaseResponse<List<RoomAdminInfo>>> getAdmin(String token, String anchorid);

    //    管理员列表
    Observable<BaseResponse<Object>> removeAdmin(String token, String anchorid, String adminid);

    //    回播列表
    Observable<BaseResponse<List<PlayBackInfo>>> getPlayBack(String token, String roomID);

    //    回播url
    Observable<BaseResponse<String>> getPlayBackListUrl(String roomID, String start, String end);

    //     话题列表
    Observable<BaseResponse<ThemBean>> getThemBean();

    //  创建房间传递话题
    Observable<BaseResponse<CreateRoomBean>> postCreatRoom(String token, String title, String roomid, String city, String province, char orientation, String privateString, int privatetype,String approveidString);

    //黑名单
    Observable<BaseResponse<List<HitList>>> getHitList(String token);

    //     话题列表
    Observable<BaseResponse<ThemBean>> getThemBean(String title, String number);

    //    拉黑
    Observable<BaseResponse<Object>> setHit(String token, String hitid);

    //    解除拉黑
    Observable<BaseResponse<Object>> removeHit(String token, String hitid);

    //    小伙伴
    Observable<BaseResponse<List<GetFriendBean>>> getFriendList();

    //横竖屏切换通知pc端
    Observable<BaseResponse<String>> onRoomOrientationChange(String roomid, String orientation);

    //获取自己的推流地址
    Observable<BaseResponse<String>> upLoadMyAddress(String roomid);

    /**
     * 生成连麦房间。
     *
     * @param roomId 房间号
     * @return 生成的
     */
    Observable<BaseResponse<ConferenceRoom>> createConferenceRoom(String roomId, String roomName);

    //更新自己的推荐用户
    Observable<BaseResponse<String>> upLoadMyRecommen(String uid);

    //更新自己的推荐用户
    Observable<BaseResponse<UpDataBean>> upNewAppVersion(String system);

    Observable<BaseResponse<String>> getRoomToken(String roomName, String userId, String perm, long expireAt);

    Observable<BaseResponse<Object>> deletConferenceRoom(String roomName);

    Observable<BaseResponse<Object>> sendConferenceMsg(String userId, String msg);

    /**
     * 主播恢复私密
     * @param plid
     * @return
     */
    Observable<BaseResponse<Object>> publishRecoveryPrivate(int plid);

    Observable<BaseResponse<PrivateLimitBean>> loadPrivateLimit(String uid);

    /**
     * 这里我同用了一个 但是不确定好不好 请后面的人帮我思考一下，联系qq 357847847
     * @param plid
     * @param prerequisite
     * @param uid
     * @param aid
     * @return
     */
    Observable<BaseResponse<Object>> checkPrivatePass(String type,int plid,String prerequisite,String uid,String aid);

    /**
     * 获取回播私密
     * @param uid
     * @return
     */
    Observable<BaseResponse<PrivateLimitBean>> loadBackPrivateLimit(String uid,String urlstart);

    /**
     * 發送彈幕
     * @param roomuid
     * @param content
     * @return
     */
    Observable<BaseResponse<Object>> sendDanmuMsg(String roomuid, String content);

    /**
     *
     * @param user_id
     * @param user_token
     * @return
     */
    Observable<BaseResponse<String>> getViewPagerJson(String user_id, String user_token);


}

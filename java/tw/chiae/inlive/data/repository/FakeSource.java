package tw.chiae.inlive.data.repository;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
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
import tw.chiae.inlive.data.bean.transaction.RechargeMapItem;
import tw.chiae.inlive.data.bean.transaction.WithDrawRespose;
import tw.chiae.inlive.data.bean.websocket.getLikeList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;

/**
 * 模拟数据。
 */
public class FakeSource implements ISource {

    public static final int FAKE_ONE_PAGE_ITEM_COUNT = 20;
    private static final int FAKE_NETWORK_DELAY = 1000;


    private <T> Observable<BaseResponse<T>> delayedFakeFromGson(Class<T> clz, String data){
        return delayedObservable(createSuccessObservable(new Gson().fromJson(data, clz)));
    }

    /**
     * 为每个请求方法模拟网络延迟。
     *
     * @param observable 将要被延迟发送的Observable对象。
     */
    private <T> Observable<T> delayedObservable(Observable<T> observable) {
        return observable.delay(FAKE_NETWORK_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * 根据指定数据创建表示成功的请求结果。
     */
    private <T> Observable<BaseResponse<T>> createSuccessObservable(@NonNull final T data) {
        return Observable.create(new Observable.OnSubscribe<BaseResponse<T>>() {
            @Override
            public void call(Subscriber<? super BaseResponse<T>> subscriber) {
                BaseResponse<T> baseResponse = new BaseResponse<>();
                baseResponse.setCode(BaseResponse.RESULT_CODE_SUCCESS);
                baseResponse.setMsg("请求成功");
                baseResponse.setData(data);
                subscriber.onNext(baseResponse);
                subscriber.onCompleted();
            }
        });
    }

    private <T> Observable<BaseResponse<T>> createFailedObservable(){
        return Observable.create(new Observable.OnSubscribe<BaseResponse<T>>() {
            @Override
            public void call(Subscriber<? super BaseResponse<T>> subscriber) {
                BaseResponse<T> response = new BaseResponse<>();
                response.setCode(BaseResponse.RESULT_CODE_SUCCESS+1);
                response.setMsg("Fake Source暂不支持这个API哦");
                response.setData(null);
                subscriber.onNext(response);
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<BaseResponse<LoginInfo>> register(String username, String password) {
        //注册和登录返回的数据是一样的。
        return login(username, password);
    }

    @Override
    public Observable<BaseResponse<LoginInfo>> autoLogin(String token) {
        return login(token, token);
    }

    @Override
    public Observable<BaseResponse<LoginInfo>> login(String username, String password) {
        LoginInfo info = new LoginInfo();
        info.setAvatar("");
        info.setNickname("FakeLogin");
        info.setCurrentRoomNum("1475430026");
        info.setToken("fb50388230091951355b5d0e2c67d421");
        info.setTotalBalance(8888);
        info.setUserId("804");
        return delayedObservable(createSuccessObservable(info));
    }

    @Override
    public Observable<BaseResponse<HotAnchorPageBean>> loadHotAnchors(int pageNum) {
        return delayedFakeFromGson(HotAnchorPageBean.class, "{\"total_cnt\": 5," +
                        "\"page\": 1,\"size\": 1,\"page_cnt\": 5,\"list\": [{\"id\": \"535\"," +
                        "\"nickname\": \"bendq01\",\"curroomnum\": \"7777\",\"snap\": " +
                        "\"/style/images/default.gif\",\"city\": \"长沙市\",\"online\": 40," +
                        "\"avatar\": \"/passport/avatar.php?uid=540&size=middle\"}],\"banner\": " +
                        "[{\"img_url\": \"\",\"target_url\": \"\"}]}"
                )
        ;
    }

    @Override
    public Observable<BaseResponse<HotAnchorPageBean>> loadHotAnchors(String token,String city,String sex) {
        return delayedFakeFromGson(HotAnchorPageBean.class, "{\"total_cnt\": 5," +
                "\"page\": 1,\"size\": 1,\"page_cnt\": 5,\"list\": [{\"id\": \"535\"," +
                "\"nickname\": \"bendq01\",\"curroomnum\": \"7777\",\"snap\": " +
                "\"/style/images/default.gif\",\"city\": \"长沙市\",\"online\": 40," +
                "\"avatar\": \"/passport/avatar.php?uid=540&size=middle\"}],\"banner\": " +
                "[{\"img_url\": \"\",\"target_url\": \"\"}]}"
        )
                ;
    }

    @Override
    public Observable<BaseResponse<List<HotAnchorSummary>>> loadFollowedLives(int pageNum) {
        List<HotAnchorSummary> list = new ArrayList<>();
        return delayedObservable(createSuccessObservable(list));
    }

    @Override
    public Observable<BaseResponse<getLikeList>> loadFollowedList(int pageNum) {
        return null;
    }

    @Override
    public Observable<BaseResponse<List<HotAnchorSummary>>> loadTopicLives(int topicID) {
        List<HotAnchorSummary> list = new ArrayList<>();
        return delayedObservable(createSuccessObservable(list));
    }

    @Override
    public Observable<BaseResponse<Object>> followAnchor(String uid) {
        return delayedObservable(createFailedObservable());
    }

    @Override
    public Observable<BaseResponse<Object>> unfollowAnchor(String uid) {
        return delayedObservable(createFailedObservable());
    }

    @Override
    public Observable<BaseResponse<PageBean<AnchorSummary>>> mqueryAnchors(String condition,String type, int
            pageNum) {
        return delayedObservable(this.<PageBean<AnchorSummary>>createFailedObservable());
    }

    @Override
    public Observable<BaseResponse<UserInfo>> getUserInfo(Integer uid) {
        return Observable.create(new Observable.OnSubscribe<BaseResponse<UserInfo>>() {
            @Override
            public void call(Subscriber<? super BaseResponse<UserInfo>> subscriber) {
                BaseResponse<UserInfo> response = new BaseResponse<>();
                response.setCode(BaseResponse.RESULT_CODE_SUCCESS);
                UserInfo result = new UserInfo();
                result.setNickname("哈哈哈");
                result.setId("8065");
                result.setAvatar("passport/avatar.php?uid=750&size=middle");
                result.setFolloweesCount("0");
                result.setFollowersCount("0");
                result.setIntro("不喜欢");
                result.setTotalContribution(1312435);
                result.setLevel("1");
                result.setCoinBalance(0.00);
                result.setSex(0);
                result.setVip("0");
                response.setData(result);
                subscriber.onNext(response);
                subscriber.onCompleted();
            }
        }).delay(FAKE_NETWORK_DELAY, TimeUnit.MILLISECONDS);
    }

    @Override
    public Observable<BaseResponse<String>> getEmotion(String token, int emotion) {
        return delayedObservable(this.<String>createFailedObservable());
    }

    @Override
    public Observable<BaseResponse<String>> setBirthday(String token, String birthday) {
        return delayedObservable(this.<String>createFailedObservable());
    }

    @Override
    public Observable<BaseResponse<String>> getProvince(String token, String province, String city) {
        return delayedObservable(this.<String>createFailedObservable());
    }

    @Override
    public Observable<BaseResponse<AnchoBean>> getAnchoBean(String user_id) {
        return delayedObservable(this.<AnchoBean>createFailedObservable());
    }

    @Override
    public Observable<BaseResponse<String>> starUser(String token,String uid,String roomid) {
        return delayedObservable(this.<String>createFailedObservable());
    }

    @Override
    public Observable<BaseResponse<String>> unStarUser(String token,String uid,String roomid) {
        return delayedObservable(this.<String>createFailedObservable());
    }

    @Override
    public Observable<BaseResponse<PageBean<AnchorSummary>>> getUserStars(String uid, int pageNum) {
        return delayedObservable(this.<PageBean<AnchorSummary>>createFailedObservable());
    }

    @Override
    public Observable<BaseResponse<PageBean<AnchorSummary>>> getUserFans(String uid, int pageNum) {
        return delayedObservable(this.<PageBean<AnchorSummary>>createFailedObservable());
    }

    @Override
    public Observable<BaseResponse<PageBean<AnchorSummary>>> loadRecommendAnchors(String token,String time,String size ,String page) {
        return delayedObservable(this.<PageBean<AnchorSummary>>createFailedObservable());
    }

    @Override
    public Observable<BaseResponse<PageBean<AnchorSummary>>> loadCommendAnchors(String token, String city) {
        return delayedObservable(this.<PageBean<AnchorSummary>>createFailedObservable());
    }

    @Override
    public Observable<BaseResponse<List<Gift>>> getAvailableGifts() {
        List<Gift> list = new ArrayList<>();
        Gift gift = new Gift();
        gift.setDisplayName("巧克力盒");
        gift.setPrice(500);
        gift.setImageUrl("/style/images/gift/50/35.png");
        gift.setTypeId("1");
        list.add(gift);
        return delayedObservable(createSuccessObservable(list));
    }

    @Override
    public Observable<BaseResponse<Object>> sendGift(String toUserId, String giftId, int count) {
        Object obj = "Success";
        return delayedObservable(createSuccessObservable(obj));
    }

    @Override
    public Observable<BaseResponse<Object>> sendHongBaoGift(String token, String roomuid, String giftid) {
        Object obj = "Success";
        return delayedObservable(createSuccessObservable(obj));
    }

    @Override
    public Observable<BaseResponse<String>> editProfile(String profileJson) {
        return delayedObservable(createSuccessObservable("修改成功"));
    }

    @Override
    public Observable<BaseResponse<String>> editJob(String token, String professional) {
        return delayedObservable(createSuccessObservable("修改成功"));
    }

    @Override
    public Observable<BaseResponse<Object>> setLiveStatus(String token,String status) {
        Object obj = "修改成功";
        return delayedObservable(createSuccessObservable(obj));
    }

    @Override
    public Observable<BaseResponse<LiveRoomEndInfo>> getLiveRoomInfo(String roomId) {
        LiveRoomEndInfo info = new LiveRoomEndInfo();
        info.setAudienceCount("300");
        info.setCoinIncome("889746");
        return delayedObservable(createSuccessObservable(info));
    }

    @Override
    public Observable<BaseResponse<RechargeInfo>> getRechargeMap() {
        List<RechargeMapItem> list = new ArrayList<>();
        for (int i=1; i< 10001; i*=10){
            RechargeMapItem item = new RechargeMapItem();
            item.setCurrencyAmount(String.valueOf(i*10));
            item.setRmbAmount(String.valueOf(i));
            if (1==i){
                item.setMsg("新人礼包，仅一次机会");
            } else if (99 < i){
                item.setMsg("赠送"+(0.2*i)+"P豆");
            }
            list.add(item);
        }
        RechargeInfo info = new RechargeInfo();
        info.setCoinBalance(300);
        return delayedObservable(createSuccessObservable(info));
    }

    @Override
    public Observable<BaseResponse<Object>> reportLocation(String lat, String lng) {
        return delayedObservable(createSuccessObservable((Object)"ok"));
    }

    @Override
    public Observable<BaseResponse<PageBean<CurrencyRankItem>>> getCurrencyRankList(String uid, int page) {
        return null;
    }

    @Override
    public Observable<BaseResponse<String>> uploadAvatar(String path) {
        return delayedObservable(createSuccessObservable("ok"));
    }

    @Override
    public Observable<BaseResponse<IncomeBean>> getIncomeBean() {
        return delayedObservable(createSuccessObservable(new IncomeBean()));
    }

    @Override
    public Observable<BaseResponse<WithDrawRespose>> withDraw(String num, String account) {
        return null;
    }

    @Override
    public Observable<BaseResponse<List<PresentRecordItem>>> getPresentRecord() {
        return null;
    }

    @Override
    public Observable<BaseResponse<LoginInfo>> thirdLogin(String openId, @ThirdLoginPlatform
    String platform, String extras) {
        return login("stub", "stub");
    }

    @Override
    public Observable<BaseResponse<String>> generateRechargeOrder(String amount) {
        return delayedObservable(createSuccessObservable(""));
    }

    @Override
    public Observable<BaseResponse<String>> generateRechargeWechat(String amount) {
        return delayedObservable(createSuccessObservable(""));
    }

    @Override
    public Observable<BaseResponse<String>> generatePushStreaming(String roomId) {
        return null;
    }

    @Override
    public Observable<BaseResponse<String>> getPlaybackUrl(String roomId) {
        return delayedObservable(createSuccessObservable("rtmp://pili-live-rtmp.xingketv.com/xingketv/Test12345610"));
    }

    @Override
    public Observable<BaseResponse<String>> sendCaptcha(String phone) {
        return null;
    }

    @Override
    public Observable<BaseResponse<List<RoomAdminInfo>>> getAdmin(String token, String anchorid) {
        List<RoomAdminInfo> list = new ArrayList<>();
        return delayedObservable(createSuccessObservable(list));
    }

    @Override
    public Observable<BaseResponse<Object>> removeAdmin(String token, String anchorid,String adminid) {
        return null;
    }

    @Override
    public Observable<BaseResponse<List<PlayBackInfo>>> getPlayBack(String token,String roomID) {
        List<PlayBackInfo> list = new ArrayList<>();
        return delayedObservable(createSuccessObservable(list));
    }

    @Override
    public Observable<BaseResponse<String>> getPlayBackListUrl(String roomID,String start, String end) {
        String url=new String();
        return delayedObservable(createSuccessObservable(url));
    }

    @Override
    public Observable<BaseResponse<ThemBean>> getThemBean() {
        return delayedObservable(createSuccessObservable(new ThemBean()));
    }

    @Override
    public Observable<BaseResponse<CreateRoomBean>> postCreatRoom(String token, String title, String roomid, String city, String province, char orientation, String privateString, int privatetyp ,String approveid) {
        return null;
    }

    @Override
    public Observable<BaseResponse<List<HitList>>> getHitList(String token) {
        List<HitList> list=new ArrayList<>();
        return delayedObservable(createSuccessObservable(list));
    }

    @Override
    public Observable<BaseResponse<ThemBean>> getThemBean(String title,String number) {
        return delayedObservable(createSuccessObservable(new ThemBean()));
    }

    @Override
    public Observable<BaseResponse<Object>> setHit(String token, String hitid) {
        return delayedObservable(createSuccessObservable((Object)"1"));
    }

    @Override
    public Observable<BaseResponse<Object>> removeHit(String token, String hitid) {
        return delayedObservable(createSuccessObservable((Object)"1"));
    }

    @Override
    public Observable<BaseResponse<List<GetFriendBean>>> getFriendList() {
        return null;
    }

    @Override
    public Observable<BaseResponse<LoginInfo>> loginByCaptcha(String username, String captcha) {
        return null;
    }

    @Override
    public Observable<BaseResponse<String>> onRoomOrientationChange(String roomid, String orientation) {
        return delayedObservable(createSuccessObservable("ok"));
    }

    @Override
    public Observable<BaseResponse<String>> upLoadMyAddress(String roomid) {
        return null;
    }

    @Override
    public Observable<BaseResponse<String>> upLoadMyRecommen(String userid) {
        return null;
    }

    @Override
    public Observable<BaseResponse<UpDataBean>> upNewAppVersion(String system) {
        return null;
    }

    @Override
    public Observable<BaseResponse<ConferenceRoom>> createConferenceRoom(String roomId, String roomName) {
        return null;
    }

    @Override
    public Observable<BaseResponse<String>> getRoomToken(String roomName, String userId, String perm, long expireAt) {
        return null;
    }

    @Override
    public Observable<BaseResponse<Object>> deletConferenceRoom(String roomName) {
        return null;
    }

    @Override
    public Observable<BaseResponse<Object>> sendConferenceMsg(String userId, String invitation) {
        return null;
    }

    @Override
    public Observable<BaseResponse<Object>> publishRecoveryPrivate(int plid) {
        return null;
    }

    @Override
    public Observable<BaseResponse<PrivateLimitBean>> loadPrivateLimit(String uid) {
        return null;
    }

    @Override
    public Observable<BaseResponse<Object>> checkPrivatePass(String type,int plid, String prerequisite, String uid, String aid) {
        return null;
    }

    @Override
    public Observable<BaseResponse<PrivateLimitBean>> loadBackPrivateLimit(String uid, String urlstart) {
        return null;
    }

    @Override
    public Observable<BaseResponse<Object>> sendDanmuMsg(String roomuid, String content) {
        return null;
    }

    @Override
    public Observable<BaseResponse<String>> getViewPagerJson(String user_id, String user_token) {
        return null;
    }

}

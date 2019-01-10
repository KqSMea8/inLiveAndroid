package tw.chiae.inlive.data.repository;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import okio.BufferedSink;
import tw.chiae.inlive.BuildConfig;
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
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.room.create.PrivateSetStringDialog;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.L;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * 使用Retrofit实现的网络请求。
 * Created by huanzhang on 2016/4/11.
 */
public class RetrofitSource implements ISource {
    private static final int CONNECT_TIME_OUT = 5;
    private static final int WRITE_TIME_OUT = 15;
    private static final int READ_TIME_OUT = 15;

    private RetrofitApi api;

    public RetrofitSource() {
        OkHttpClient okHttpClient = new OkHttpClient();
        OkHttpClient.Builder clientBuilder = okHttpClient.newBuilder()
                //添加通用请求信息, see http://stackoverflow.com/a/33667739
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        HttpUrl.Builder builder = request.url().newBuilder()
                                .addQueryParameter("device", Build.MODEL);
                        //存在可能取不到Token的情况，所以只在有信息的时候调用getToken，否则传空。
                        LoginInfo loginInfo = LocalDataManager.getInstance().getLoginInfo();
                        builder.addQueryParameter("token", loginInfo != null ? loginInfo.getToken() : "");
                        request = request.newBuilder().url(builder.build()).build();
                        return chain.proceed(request);
                    }
                })
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(L.INSTANCE);
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addNetworkInterceptor(logging);
        }

        okHttpClient = clientBuilder.build();

        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(Const.WEB_BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = mRetrofit.create(RetrofitApi.class);
    }

    @Override
    public Observable<BaseResponse<LoginInfo>> register(String username, String password) {
        return api.register(username, password);
    }

    @Override
    public Observable<BaseResponse<LoginInfo>> autoLogin(String token) {
        return api.autoLogin(token);
    }

    @Override
    public Observable<BaseResponse<LoginInfo>> login(String username, String password) {
        return api.login(username, password);
    }

    @Override
    public Observable<BaseResponse<PageBean<AnchorSummary>>> mqueryAnchors(String condition, String type, int
            pageNum) {
        return api.mqueryAnchors(condition, type, pageNum);
    }


    @Override
    public Observable<BaseResponse<List<HotAnchorSummary>>> loadFollowedLives(int pageNum) {
        return api.loadFollowedLives(pageNum);
    }

    //全部關注人
    @Override
    public Observable<BaseResponse<getLikeList>> loadFollowedList(int pageNum) {
        return api.loadFollowedList(pageNum);
    }

    @Override
    public Observable<BaseResponse<List<HotAnchorSummary>>> loadTopicLives(int topicID) {
        return api.loadTopicLives(topicID);
    }

    @Override
    public Observable<BaseResponse<HotAnchorPageBean>> loadHotAnchors(int pageNum) {
        return api.loadHotAnchors(pageNum);
    }

    @Override
    public Observable<BaseResponse<HotAnchorPageBean>> loadHotAnchors(String token, String city, String sex) {
        city="";
        sex="";
        return api.loadHotAnchors(token, city, sex);
    }

    @Override
    public Observable<BaseResponse<Object>> followAnchor(String uid) {
        return api.followAnchor(uid);
    }

    @Override
    public Observable<BaseResponse<Object>> unfollowAnchor(String uid) {
        return api.unfollowAnchor(uid);
    }

    @Override
    public Observable<BaseResponse<UserInfo>> getUserInfo(Integer uid) {
        return api.getUserInfo(uid);
    }

    @Override
    public Observable<BaseResponse<String>> getEmotion(String token, int emotion) {
        return api.getEmotion(token, emotion);
    }

    @Override
    public Observable<BaseResponse<String>> setBirthday(String token, String birthday) {
        return api.setBirthday(token, birthday);
    }

    @Override
    public Observable<BaseResponse<String>> getProvince(String token, String province, String city) {
        return api.getProvince(token, province, city);
    }


    @Override
    public Observable<BaseResponse<AnchoBean>> getAnchoBean(String user_id) {
        return api.getAnchoBean(user_id);
    }

    @Override
    public Observable<BaseResponse<String>> starUser(String token, String uid, String roomid) {
        return api.starUser(token, uid, roomid);
    }

    @Override
    public Observable<BaseResponse<String>> unStarUser(String token, String uid, String roomid) {
        return api.unStarUser(token, uid, roomid);
    }

    @Override
    public Observable<BaseResponse<PageBean<AnchorSummary>>> getUserStars(String uid, int pageNum) {
        return api.getUserStars(uid, pageNum);
    }

    @Override
    public Observable<BaseResponse<PageBean<AnchorSummary>>> getUserFans(String uid, int pageNum) {
        return api.getUserFans(uid, pageNum);
    }

    @Override
    public Observable<BaseResponse<PageBean<AnchorSummary>>> loadRecommendAnchors(String token, String time ,String size ,String page) {
        return api.loadRecommendAnchors(token, time ,size, page);
    }

    @Override
    public Observable<BaseResponse<PageBean<AnchorSummary>>> loadCommendAnchors(String token, String city) {
        return api.loadCommendAnchors(token, city);
    }

    @Override
    public Observable<BaseResponse<List<Gift>>> getAvailableGifts() {
        return api.getAvailableGifts();
    }

    @Override
    public Observable<BaseResponse<Object>> sendGift(String toUserId, String giftId, int count) {
        return api.sendGift(toUserId, giftId, count);
    }

    @Override
    public Observable<BaseResponse<Object>> sendHongBaoGift(String token, String roomuid, String giftid) {
        return api.sendHongBaoGift(token, roomuid, giftid);
    }


    @Override
    public Observable<BaseResponse<String>> editProfile(String profileJson) {
        return api.editProfile(profileJson);
    }

    @Override
    public Observable<BaseResponse<String>> editJob(String token, String professional) {
        return api.editJob(token, professional);
    }

    @Override
    public Observable<BaseResponse<Object>> setLiveStatus(String token, String status) {
        return api.setLiveStatus(token, status);
    }

    @Override
    public Observable<BaseResponse<LiveRoomEndInfo>> getLiveRoomInfo(String roomId) {
        return api.getLiveRoomInfo(roomId);
    }

    @Override
    public Observable<BaseResponse<RechargeInfo>> getRechargeMap() {
        return api.getRechargeInfo();
    }

    @Override
    public Observable<BaseResponse<Object>> reportLocation(String lat, String lng) {
        return api.reportLocation(lat, lng);
    }

    @Override
    public Observable<BaseResponse<PageBean<CurrencyRankItem>>> getCurrencyRankList(String uid, int page) {
        return api.getCurrencyRankList(uid, page);
    }

    @Override
    public Observable<BaseResponse<String>> uploadAvatar(String path) {
        File file = new File(path);
        Log.i("RayTest","uploadAvatar: "+path);
        ProgressRequestBody fileBody = new ProgressRequestBody(file, new UploadCallbacks() {
            @Override
            public void onProgressUpdate(int i) {

            }

            @Override
            public void detailProgress(long mUploaded, long mTotal) {
                int progress = (int)(100 * mUploaded / mTotal);
                //Log.i("RayTest","["+progress + " %]"+" mUploaded:"+mUploaded+" total:"+mTotal);
            }
        });
        //RequestBody photoRequestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        MultipartBody.Part photoPart = MultipartBody.Part.createFormData("name", file.getName(),
                fileBody);
        return api.uploadAvatar(photoPart);
//        return api.editProfile(path);
    }

    @Override
    public Observable<BaseResponse<IncomeBean>> getIncomeBean() {
        return api.getIncomeBean();
    }

    @Override
    public Observable<BaseResponse<WithDrawRespose>> withDraw(String num, String account) {
        return api.withDraw(num, account);
    }

    @Override
    public Observable<BaseResponse<List<PresentRecordItem>>> getPresentRecord() {
        return api.getPresentRecord();
    }

    @Override
    public Observable<BaseResponse<LoginInfo>> thirdLogin(String openId, @ThirdLoginPlatform String platform, String extras) {
        return api.thirdLogin(openId, platform, extras);
    }

    @Override
    public Observable<BaseResponse<String>> generateRechargeOrder(String amount) {
        return api.generateRechargeOrder(amount);
    }

    @Override
    public Observable<BaseResponse<String>> generateRechargeWechat(String amount) {
        return api.generateRechargeWechat(amount);
    }

    @Override
    public Observable<BaseResponse<String>> generatePushStreaming(String roomId) {
        return api.generatePushStreaming(roomId);
    }

    @Override
    public Observable<BaseResponse<String>> getPlaybackUrl(String roomId) {
        return api.getPlaybackUrl(roomId);
    }

    @Override
    public Observable<BaseResponse<String>> sendCaptcha(String phone) {
        return api.sendCaptcha(phone);
    }

    @Override
    public Observable<BaseResponse<List<RoomAdminInfo>>> getAdmin(String token, String anchorid) {
        return api.getAdmin(token, anchorid);
    }

    @Override
    public Observable<BaseResponse<Object>> removeAdmin(String token, String anchorid, String adminid) {
        return api.removeAdmin(token, anchorid, adminid);
    }

    @Override
    public Observable<BaseResponse<List<PlayBackInfo>>> getPlayBack(String token, String roomID) {
        return api.getPlayBack(token, roomID);
    }

    @Override
    public Observable<BaseResponse<String>> getPlayBackListUrl(String roomID, String start, String end) {
        return api.getPlayBackListUrl(roomID, start, end);
    }

    //    得到话题对象
    @Override
    public Observable<BaseResponse<ThemBean>> getThemBean() {
        return api.getThemBean();
    }

    //    传递房间号
    @Override
    public Observable<BaseResponse<CreateRoomBean>> postCreatRoom(String token, String title, String roomid, String city, String province, char orientation, String privateString, int privatetype,String approveidString) {
        return api.postCreateRoom(token, title, roomid, city, province, orientation, privateString, privatetype,approveidString);
    }

    @Override
    public Observable<BaseResponse<List<HitList>>> getHitList(String token) {
        return api.hitList(token);
    }

    //    得到话题对象
    @Override
    public Observable<BaseResponse<ThemBean>> getThemBean(String title, String number) {
        return api.getThemBean(title, number);
    }

    @Override
    public Observable<BaseResponse<Object>> setHit(String token, String hitid) {
        return api.setHit(token, hitid);
    }

    @Override
    public Observable<BaseResponse<Object>> removeHit(String token, String hitid) {
        return api.removeHit(token, hitid);
    }

    @Override
    public Observable<BaseResponse<List<GetFriendBean>>> getFriendList() {
        return api.getFriendList();
    }

    @Override
    public Observable<BaseResponse<LoginInfo>> loginByCaptcha(String username, String captcha) {
        return api.loginByCaptcha(username, captcha);
    }

    @Override
    public Observable<BaseResponse<String>> onRoomOrientationChange(String roomid, String orientation) {
        return api.onRoomOrientationChange(roomid, orientation);
    }

    @Override
    public Observable<BaseResponse<String>> upLoadMyAddress(String roomid) {
        return api.upLoadMyAddress(roomid);
    }

    @Override
    public Observable<BaseResponse<String>> upLoadMyRecommen(String uid) {
        return api.upLoadMyRecommen(uid);
    }

    @Override
    public Observable<BaseResponse<UpDataBean>> upNewAppVersion(String system) {
        return api.upNewAppVersion(system);
    }

    @Override
    public Observable<BaseResponse<ConferenceRoom>> createConferenceRoom(String roomId, String roomName) {
        return api.createConferenceRoom(roomId, roomName);
    }

    @Override
    public Observable<BaseResponse<String>> getRoomToken(String roomName, String userId, String perm, long expireAt) {
        return api.getRoomToken(roomName, userId, perm, expireAt);
    }

    @Override
    public Observable<BaseResponse<Object>> deletConferenceRoom(String roomName) {
        return api.deletConferenceRoom(roomName);
    }

    @Override
    public Observable<BaseResponse<Object>> sendConferenceMsg(String userId, String msg) {
        return api.sendConferenceMsg(userId, msg);
    }

    @Override
    public Observable<BaseResponse<Object>> publishRecoveryPrivate(int plid) {
        return api.publishRecoveryPrivate(plid);
    }

    @Override
    public Observable<BaseResponse<PrivateLimitBean>> loadPrivateLimit(String uid) {
        return api.loadPrivateLimit(uid);
    }


    @Override
    public Observable<BaseResponse<Object>> checkPrivatePass(String type, int plid, String prerequisite, String uid, String aid) {
        if (type.equals(PrivateSetStringDialog.PRIVTE_ROOM_PWD))
            return api.loadPrivatePwd(plid, prerequisite, uid, aid);
        else if (type.equals(PrivateSetStringDialog.PRIVTE_ROOM_TICKET))
            return api.loadPrivateTicket(plid, uid, aid);
        else if (type.equals(PrivateSetStringDialog.PRIVTE_ROOM_LEVEL))
            return api.loadPrivateLevel(plid, uid, aid);
        else
            return api.loadPrivatePwd(plid, prerequisite, uid, aid);
    }

    @Override
    public Observable<BaseResponse<PrivateLimitBean>> loadBackPrivateLimit(String uid, String urlstart) {
        return api.loadBackPrivateLimit(uid, urlstart);
    }

    @Override
    public Observable<BaseResponse<Object>> sendDanmuMsg(String roomuid, String content) {
        return api.sendDanmuMsg(roomuid, content);
    }

    @Override
    public Observable<BaseResponse<String>> getViewPagerJson(String user_id, String user_token) {
        return api.getViewPagerJson(user_id, user_token);
    }


    static Interceptor interceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            Log.i("mrl", "请求体" + request.url());
            if (request.url().toString().contains("dasdasdsadasdas")){
                Log.i("mrl","攔截了"+response.body().string());
            }
            return response;
        }
    };




    private class ProgressRequestBody  extends RequestBody{

        private final File mFile;
        private final UploadCallbacks mListener;
        private static final int DEFAULT_BUFFER_SIZE = 2048;

        public ProgressRequestBody(final File file, final  UploadCallbacks listener) {
            mFile = file;
            mListener = listener;
        }

        @Override
        public MediaType contentType() {

            return MediaType.parse("image/jpg");
        }

        @Override
        public long contentLength() throws IOException {
            return mFile.length();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            long fileLength = mFile.length();
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            FileInputStream in = new FileInputStream(mFile);
            long uploaded = 0;

            try {
                int read;
                Handler handler = new Handler(Looper.getMainLooper());
                while ((read = in.read(buffer)) != -1) {

                    // update progress on UI thread
                    handler.post(new ProgressUpdater(uploaded, fileLength));

                    uploaded += read;
                    sink.write(buffer, 0, read);
                }
            } finally {
                in.close();
            }
        }
        private class ProgressUpdater implements Runnable {
            private long mUploaded;
            private long mTotal;
            public ProgressUpdater(long uploaded, long total) {
                mUploaded = uploaded;
                mTotal = total;
            }

            @Override
            public void run() {
                int progress = (int)(100 * mUploaded / mTotal);
                Log.i("RayTest","["+progress + " %]"+" mUploaded:"+mUploaded+" total:"+mTotal);
                mListener.detailProgress(mUploaded , mTotal);
                mListener.onProgressUpdate((int)(100 * mUploaded / mTotal));
            }
        }

    }

    private interface UploadCallbacks {
        void onProgressUpdate(int i);

        void detailProgress(long mUploaded, long mTotal);
    }


}

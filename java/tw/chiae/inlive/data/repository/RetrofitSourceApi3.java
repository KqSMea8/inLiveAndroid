package tw.chiae.inlive.data.repository;

import android.os.Build;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import tw.chiae.inlive.BuildConfig;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.domain.BlackList;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.domain.PublishTimeInfo;
import tw.chiae.inlive.presentation.ui.main.mergefilm.EventActivity;
import tw.chiae.inlive.presentation.ui.room.HotPointInfo;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.L;

/**
 * Created by rayyeh on 2017/7/6.
 */

public class RetrofitSourceApi3 implements ISourceApi3{

    private static final int CONNECT_TIME_OUT = 5;
    private static final int WRITE_TIME_OUT = 15;
    private static final int READ_TIME_OUT = 15;
    private OkHttpClient okHttpClient ;

    private RetrofitApi3 api;

    public RetrofitSourceApi3(boolean isJsonResponse) {
        if(okHttpClient==null)
            okHttpClient = new OkHttpClient();
        OkHttpClient.Builder clientBuilder = okHttpClient.newBuilder()
                //添加通用请求信息, see http://stackoverflow.com/a/33667739
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Log.i("RayTest","interceptor1");

                        Request request = chain.request();
                        Log.i("RayTest", "请求体api3 interceptor1:" + request.url());
                        HttpUrl.Builder builder = request.url().newBuilder();

                        /*        .addQueryParameter("device", Build.MODEL);
                        //存在可能取不到Token的情况，所以只在有信息的时候调用getToken，否则传空。
                        LoginInfo loginInfo = LocalDataManager.getInstance().getLoginInfo();
                        builder.addQueryParameter("token", loginInfo != null ? loginInfo.getToken() : "");*/
                        request = request.newBuilder().url(builder.build()).build();
                        return chain.proceed(request);
                    }
                })
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);
        Log.i("RayTest","newBuilder");
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(L.INSTANCE);
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addNetworkInterceptor(logging);
        }

        okHttpClient = clientBuilder.build();


        Retrofit mRetrofit = null;
        if(isJsonResponse) {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(Const.Server_API3_HOST)
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }else {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(Const.Server_API3_HOST)
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(new ToStringConverterFactory())
                    .build();
        }


        api = mRetrofit.create(RetrofitApi3.class);
    }


    @Override
    public Observable<ServerEventResponse<String>> checkServerStat() {
        return api.checkServerStat();
    }

    @Override
    public Observable<RecordRoomResponse<String>> RecordRoomNote(String wsUserId, String wsRoomId, String publishRoomTitle) {
        return api.RecordRoomNote(wsUserId,wsRoomId,publishRoomTitle);
    }

    @Override
    public Observable<String> RecordRoomNote2(String wsUserId, String wsRoomId, String publishRoomTitle) {
        return api.RecordRoomNote2(wsUserId,wsRoomId,publishRoomTitle);
    }

    @Override
    public Observable<String> RecordRoomNoteEnd(String wsUserId, String wsRoomId, String wsStreamID) {
        return api.RecordRoomNoteEnd(wsUserId,wsRoomId,wsStreamID);
    }

    @Override
    public Observable<List<HotPointResponse<HotPointInfo>>> getHotPoint(String mAnchorId) {
        return api.getHotPoint(mAnchorId);
    }

    @Override
    public Observable<List<PublishTimeInfo>> getStartPulishTime(String wsUserId) {
        return api.getStartPulishTime(wsUserId);
    }

    @Override
    public Observable<String> sendSpeedReport(String uid, String uip, int type, String rip, String remark) {
        return api.sendSpeedReport(uid,uip,type,rip,remark);
    }

    @Override
    public Observable<EventActivity> checkActivateEvent() {
        if(Const.TEST_ENVIROMENT_SW)
            return api.checkActivateEvent_Test();
        else
           // return api.checkActivateEvent();
        return api.checkActivateEvent();
    }

    @Override
    public Observable<List<BlackList>> getblacklist(String uid, int type, String token) {
        if(Const.TEST_ENVIROMENT_SW) {
            Log.i("RayTest","getblacklist:"+uid+" token:"+ token);
            return api.getblacklist_test(uid, token, type);
        }else
            return api.getblacklist(uid,token, type);
    }

    @Override
    public Observable<List<BlackList>> addblacklist(String uid, String roomid, String blackid, int type,String token) {
        if(Const.TEST_ENVIROMENT_SW)
            return api.addblacklist_test(uid,roomid,blackid,type,token);
        else
            return api.addblacklist(uid,roomid,blackid,type,token);
    }

    @Override
    public Observable<List<BlackList>> delblacklist(String uid, String token, String id) {
        if(Const.TEST_ENVIROMENT_SW)
            return api.delblacklist_test(uid,token,id);
        else
            return api.delblacklist(uid,token,id);
    }

    @Override
    public Observable<String> sendLiveChek(String uid, String token) {
        return api.sendLiveChek(uid,token);
    }

    @Override
    public Observable<List<HotPointResponse<HotPointInfo>>> getHotPoint_Test(String mAnchorId) {
        return api.getHotPointTest(mAnchorId);
    }


    static Interceptor interceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain)  {
            Log.i("RayTest","interceptor2");
            Request request = chain.request();
            Response response = null;
            try {
                response = chain.proceed(request);
                Log.i("RayTest", "请求体" + request.url());
                if (request.url().toString().contains("dasdasdsadasdas")){
                    Log.i("RayTest","攔截了"+response.body().string());
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("RayTest", "IOException e: " + e.getMessage());
            }

            return response;
        }
    };


    public static String toUtf8String(String s) {
        String deCodeString="";
       /* try {
            String prefix ="[@interceptor@]";
            int start = s.indexOf(prefix);
            String newString = s.substring(start+prefix.length(),s.length());
            String interceptorString = URLDecoder.decode(newString, "utf-8");
            if(start!=0)
                deCodeString = s.substring(0,start)+interceptorString;
        } catch (UnsupportedEncodingException e) {

        }*/
        try {
            String interceptorString = URLDecoder.decode(s, "utf-8");
            deCodeString = interceptorString;
        } catch (UnsupportedEncodingException e) {


        }
        return deCodeString;
    }

}

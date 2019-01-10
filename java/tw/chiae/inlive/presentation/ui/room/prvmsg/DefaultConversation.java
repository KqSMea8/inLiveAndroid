package tw.chiae.inlive.presentation.ui.room.prvmsg;

import android.view.View;

import com.bumptech.glide.Glide;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.exceptions.JMFileSizeExceedException;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import tw.chiae.inlive.BeautyLiveApplication;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;
import tw.chiae.inlive.util.Const;

/**
 * Created by rayyeh on 2017/7/31.
 */

public class DefaultConversation extends Conversation {

    private static final int MES = 9;
    private String defaultAccount;
    private String mName;
    private String mLev;
    private String mSex;
    private AccountInfoCallback mCallback;

    public int getmApproveid() {
        return mApproveid;
    }

    private int mApproveid;

    public String getmName() {
        return mName;
    }

    public String getmLev() {
        return mLev;
    }

    public String getmSex() {
        return mSex;
    }

    public String getmAvt() {
        return mAvt;
    }

    private String mAvt;

    @Override
    public String getTargetAppKey() {
        return null;
    }

    @Override
    public boolean setUnReadMessageCnt(int i) {
        return false;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public Message getLatestMessage() {
        return null;
    }

    @Override
    public Object getTargetInfo() {
        return null;
    }

    @Override
    public boolean resetUnreadCount() {
        return false;
    }

    @Override
    public Message getMessage(int i) {
        return null;
    }

    @Override
    public Message getMessage(long l) {
        return null;
    }

    @Override
    public List<Message> getAllMessage() {
        return null;
    }

    @Override
    public List<Message> getMessagesFromOldest(int i, int i1) {
        return null;
    }

    @Override
    public List<Message> getMessagesFromNewest(int i, int i1) {
        return null;
    }

    @Override
    public boolean deleteMessage(int i) {
        return false;
    }

    @Override
    public boolean deleteAllMessage() {
        return false;
    }

    @Override
    public boolean updateConversationExtra(String s) {
        return false;
    }

    @Override
    public boolean updateMessageExtra(Message message, String s, String s1) {
        return false;
    }

    @Override
    public boolean updateMessageExtra(Message message, String s, Number number) {
        return false;
    }

    @Override
    public boolean updateMessageExtra(Message message, String s, Boolean aBoolean) {
        return false;
    }

    @Override
    public boolean updateMessageExtras(Message message, Map<String, String> map) {
        return false;
    }

    @Override
    public Message createSendMessage(MessageContent messageContent) {
        return null;
    }

    @Override
    public Message createSendMessage(MessageContent messageContent, String s) {
        return null;
    }

    @Override
    public Message createSendMessage(MessageContent messageContent, List<UserInfo> list, String s) {
        return null;
    }

    @Override
    public Message createSendMessageAtAllMember(MessageContent messageContent, String s) {
        return null;
    }

    @Override
    public Message createSendTextMessage(String s) {
        return null;
    }

    @Override
    public Message createSendTextMessage(String s, String s1) {
        return null;
    }

    @Override
    public Message createSendImageMessage(File file) throws FileNotFoundException {
        return null;
    }

    @Override
    public Message createSendImageMessage(File file, String s) throws FileNotFoundException {
        return null;
    }

    @Override
    public Message createSendVoiceMessage(File file, int i) throws FileNotFoundException {
        return null;
    }

    @Override
    public Message createSendVoiceMessage(File file, int i, String s) throws FileNotFoundException {
        return null;
    }

    @Override
    public Message createSendCustomMessage(Map<? extends String, ? extends String> map) {
        return null;
    }

    @Override
    public Message createSendCustomMessage(Map<? extends String, ? extends String> map, String s) {
        return null;
    }

    @Override
    public Message createSendFileMessage(File file, String s, String s1) throws FileNotFoundException, JMFileSizeExceedException {
        return null;
    }

    @Override
    public Message createSendFileMessage(File file, String s) throws FileNotFoundException, JMFileSizeExceedException {
        return null;
    }

    @Override
    public Message createLocationMessage(double v, double v1, int i, String s) {
        return null;
    }

    @Override
    public void retractMessage(Message message, BasicCallback basicCallback) {

    }

    public interface AccountInfoCallback{
        void onSucceed();
    }
    public void setAccountInfoCallback(AccountInfoCallback callback){
        this.mCallback = callback;
    }
    public void setDefaultAccount(String account){
        this.defaultAccount = account;
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(Const.WEB_BASE_URL + "user/profile", RequestMethod.GET);
        request.add("uid", defaultAccount);
        request.add("token", LocalDataManager.getInstance().getLoginInfo().getToken());
        Log.i("RayTest","setDefaultAccount:"+account);
        BeautyLiveApplication.getRequestQueue().add(MES, request, new OnResponseListener<JSONObject>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<JSONObject> response) {
                JSONObject result = response.get();// 响应结果
                try {
                    JSONObject data = result.getJSONObject("data");
                    mName=data.getString("nickname");
                    mLev=data.getString("emceelevel");
                    mSex=data.getString("sex");
                    mAvt=data.getString("avatar");
                    mApproveid=getApproveidType(data.getString("approveid"));
                    Log.i("RayTest","onSucceed:"+mName);
                    mCallback.onSucceed();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

            }

            @Override
            public void onFinish(int what) {

            }
        });
    }


    private int getApproveidType(String approveid) {
        String[] accountLists = Const.OfficialAccountListID;
        List<String> newDefaultlist = Arrays.asList(accountLists);
        if(newDefaultlist.contains(LocalDataManager.getInstance().getLoginInfo().getUserId()))
            return 1;
        else
            return 0;
    }
    public String getDefaultAccount() {
        return defaultAccount;
    }

}

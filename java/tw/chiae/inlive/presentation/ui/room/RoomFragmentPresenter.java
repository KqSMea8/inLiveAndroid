package tw.chiae.inlive.presentation.ui.room;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.domain.BlackList;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;
import tw.chiae.inlive.presentation.ui.login.splash.BlackListManager;
import tw.chiae.inlive.presentation.ui.main.mergefilm.EventActivity;

/**
 * Created by rayyeh on 2017/7/11.
 */

public class RoomFragmentPresenter extends BasePresenter<RoomFragmentInterface>{
    private final RoomManager roommanager;
    private final BlackListManager mBlackManager;
    List<PriConversation> userInfoList ;


    public RoomFragmentPresenter(RoomFragmentInterface uiInterface) {
        super(uiInterface);
        roommanager = new RoomManager();
        mBlackManager= new BlackListManager();
    }

    public void checkActivateEvent() {
        Subscription subscription = roommanager.checkActivateEvent()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EventActivity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(EventActivity eventActivity) {
                        getUiInterface().UpdateActivateEvent(eventActivity);
                    }
                });
        addSubscription(subscription);
    }

    public void sendSpeedReport(String uid, String uip, String rip, String remark) {
        Log.i("RayTest","sendSpeedReport ");
        Subscription subscription = roommanager.sendSpeedReport(uid,uip,1,rip,remark)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        Log.i("RayTest","sendSpeedReport: "+s);
                        getUiInterface().SendReportComplete(s);
                    }
                });
        addSubscription(subscription);
    }

    public void getUserInfoList(List<Conversation> mDatas, Map<String, String> draftMap) {

        userInfoList = new ArrayList<>();
        if(draftMap==null)
            draftMap = new HashMap<>();
        if(mDatas.size()==0){
            SetupOfficialAccunt(userInfoList, draftMap);
        }
        for(Conversation conversation : mDatas){
            String some= ((UserInfo) conversation.getTargetInfo()).getUserName();
            some=some.replace("user","");
            tw.chiae.inlive.data.bean.me.UserInfo info = LocalDataManager.getInstance().getUserInfo(some);
            if(info==null){
                getInfo(some,conversation,mDatas.size(),draftMap);
            }else{
                PriConversation Conversation = new PriConversation();
                Conversation.setUserID(some);
                Conversation.setLastMsg(conversation.getLatestMessage());
                Conversation.setConversationID(conversation.getId());
                Conversation.setTime(conversation.getLastMsgDate());
                Conversation.setAvt(info.getAvatar());
                Conversation.setApproveid(info.getApproveid());
                Conversation.setNickName(info.getNickname());
                userInfoList.add(Conversation);
                if(userInfoList.size()==mDatas.size()) {
                    SetupOfficialAccunt(userInfoList,draftMap);
                }
            }

        }
    }

    private void getInfo(final String userId, final Conversation con, final int Size, final Map<String, String> draftMap)  {
        Log.i("RayTest","getInfo1"+userId);

        Subscription subscription = roommanager.getUserInfo(Integer.parseInt(userId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<tw.chiae.inlive.data.bean.me.UserInfo>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<tw.chiae.inlive.data.bean.me.UserInfo> response) {
                        tw.chiae.inlive.data.bean.me.UserInfo info = response.getData();
                        PriConversation Conversation = new PriConversation();
                        Conversation.setUserID(userId);
                        Conversation.setLastMsg(con.getLatestMessage());
                        Conversation.setConversationID(con.getId());
                        Conversation.setTime(con.getLastMsgDate());
                        Conversation.setToken(con.getTargetAppKey());
                        Conversation.setAvt(info.getAvatar());
                        Conversation.setApproveid(info.getApproveid());
                        Conversation.setNickName(info.getNickname());
                        userInfoList.add(Conversation);

                        LocalDataManager.getInstance().addUserInfo(response.getData());
                        if(userInfoList.size()==Size) {
                            SetupOfficialAccunt(userInfoList, draftMap);
                        }
                    }
                });
        addSubscription(subscription);
    }

    private void SetupOfficialAccunt(List<PriConversation> userInfoList, Map<String, String> draftMap) {
        List<tw.chiae.inlive.data.bean.me.UserInfo> officialList = LocalDataManager.getInstance().getOfficialList();
        for(tw.chiae.inlive.data.bean.me.UserInfo officialInfo : officialList){
            PriConversation Conversation = new PriConversation();
            Conversation.setUserID(officialInfo.getId());

            String draftString = draftMap.get(officialInfo.getId());
            if(draftString==null)
                draftString="";
            if(userInfoList.contains(Conversation)){
            }else{
                Conversation.setLastMsg(null);
                Conversation.setConversationID("");
                Conversation.setTime(0);
                Conversation.setAvt(officialInfo.getAvatar());
                Conversation.setToken("");
                Conversation.setApproveid(officialInfo.getApproveid());
                Conversation.setNickName(officialInfo.getNickname());
                userInfoList.add(Conversation);
            }

        }
        Log.i("Raytest","userInfoList size:"+userInfoList.size());
        getUiInterface().UpdateConversationUserInfo(userInfoList);

    }

    public void seToTop(final Conversation conv) {
        String userId= ((cn.jpush.im.android.api.model.UserInfo) conv.getTargetInfo()).getUserName();
        userId=userId.replace("user","");
        Log.i("RayTest","getInfo "+userId);
        Subscription subscription = roommanager.getUserInfo(Integer.parseInt(userId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<tw.chiae.inlive.data.bean.me.UserInfo>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<tw.chiae.inlive.data.bean.me.UserInfo> response) {
                        tw.chiae.inlive.data.bean.me.UserInfo info = response.getData();
                         LocalDataManager.getInstance().addUserInfo(response.getData());
                            //getUiInterface().setToTop(conv);\
                            getUiInterface().setToTop(null);
                    }
                });
        addSubscription(subscription);
    }

    public void getUserInfo(Conversation mConv, final RoomCallback callback) {
        String userId= ((cn.jpush.im.android.api.model.UserInfo) mConv.getTargetInfo()).getUserName();
        userId=userId.replace("user","");
        Log.i("RayTest","getInfo "+userId);
        Subscription subscription = roommanager.getUserInfo(Integer.parseInt(userId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<tw.chiae.inlive.data.bean.me.UserInfo>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<tw.chiae.inlive.data.bean.me.UserInfo> response) {
                        tw.chiae.inlive.data.bean.me.UserInfo info = response.getData();
                        LocalDataManager.getInstance().addUserInfo(response.getData());
                        //getUiInterface().setToTop(conv);\
                        callback.onSuccessInfo(response.getData());
                    }
                });
        addSubscription(subscription);
    }

    public void addBlackList(final String blackUserId) {
        String uid = LocalDataManager.getInstance().getLoginInfo().getUserId();
        String token = LocalDataManager.getInstance().getLoginInfo().getToken();
        String roomid = LocalDataManager.getInstance().getLoginInfo().getCurrentRoomNum();
        Subscription subscription = mBlackManager.addblacklist(uid,"0",blackUserId,1,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<BlackList>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<BlackList> blackLists) {
                        LocalDataManager.getInstance().saveBlackList(blackLists);
                        getUiInterface().CompleteAddBlackList(blackLists,blackUserId);
                    }
                });

        addSubscription(subscription);
    }

    public void delBlackList(final String blackUid, final int code) {
        String uid = LocalDataManager.getInstance().getLoginInfo().getUserId();
        String token = LocalDataManager.getInstance().getLoginInfo().getToken();
        String id = getBlackUserId(blackUid);
        Subscription subscription = mBlackManager.delblacklist(uid,token,id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<BlackList>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<BlackList> blackLists) {
                        LocalDataManager.getInstance().saveBlackList(blackLists);
                        getUiInterface().CompleteDelBlackList(blackLists,code,blackUid);
                    }
                });

        addSubscription(subscription);
    }

    private String getBlackUserId(String blackUid) {
        List<BlackList> blackLists = LocalDataManager.getInstance().getmBlackList();
        for(BlackList blackList : blackLists){
            if(blackList.getBlack_id().equals(blackUid))
                return blackList.getId();
        }
        return "0";
    }



/*    public void DownLoadUsereInfo(String some) {
        Subscription subscription = roommanager.getUserInfo(Integer.parseInt(some))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<tw.chiae.inlive.data.bean.me.UserInfo>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<tw.chiae.inlive.data.bean.me.UserInfo> response) {
                        //getUiInterface().showUserInfo(response.getData());
                        LocalDataManager.getInstance().addUserInfo(response.getData());
                        getUiInterface().notifChange(response.getData());
                    }
                });
        addSubscription(subscription);
    }*/

    public interface RoomCallback{
        void onSuccessInfo(tw.chiae.inlive.data.bean.me.UserInfo data);
    }
}

package tw.chiae.inlive.domain;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.List;

import tw.chiae.inlive.data.bean.Banner;
import tw.chiae.inlive.data.bean.CameraSize;
import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.data.bean.local.PayChannel;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.sharedpreference.PrefsHelper;
import tw.chiae.inlive.data.websocket.WsObjectPool;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;

/**
 * 管理本地数据的存储，抽象解耦非简单配置项的本地数据存储位置（File、DB、SP），例如用户信息。
 * @author Muyangmin
 * @since 1.0.0
 */
public class LocalDataManager {

    private static LocalDataManager instance;
    private String saveToken;
    private List<UserInfo> userInfoList = new ArrayList<>();


    private List<Banner> banners;



    private List<UserInfo> OfficialList = new ArrayList<UserInfo>() ;
    private boolean officialAccount;



    private List<BlackList> mBlackList;

    private LocalDataManager() {

    }

    public static LocalDataManager getInstance() {
        if (instance == null) {
            synchronized (LocalDataManager.class) {
                if (instance == null) {
                    instance = new LocalDataManager();
                }
            }
        }
        return instance;
    }

    private LoginInfo mLoginInfo;

    public void saveLoginInfo(@NonNull LoginInfo loginInfo) {
        //Update cached object!

        mLoginInfo = loginInfo;
        PrefsHelper.setLoginInfo(loginInfo);

        WsObjectPool.init(loginInfo);

        CrashReport.setUserId(loginInfo.getUserId());
    }

    public void clearLoginInfo(){
        mLoginInfo = null;
        saveToken = null;
        PrefsHelper.removeLoginInfo();
        WsObjectPool.release();
    }

    public LoginInfo getLoginInfo() {
        if (mLoginInfo == null) {
            mLoginInfo = PrefsHelper.getLoginInfo();
        }
        return mLoginInfo;
    }

    public void saveCameraSize(@NonNull CameraSize size, boolean isFrontCamera){
        PrefsHelper.saveCameraSize(size, isFrontCamera);
    }

    @Nullable
    public CameraSize getCameraSize(boolean isFrontCamera){
        return PrefsHelper.getCameraSize(isFrontCamera);
    }

    @PayChannel
    public int getPreferredPayChannel(int defValue){
        return PrefsHelper.getPreferredChannel(defValue);
    }

    public void savePreferredPayChannel(@PayChannel int channel){
        PrefsHelper.savePreferredPayChannel(channel);
    }

    public void saveToken(String token) {
        this.saveToken = token;
    }

    public String getTokenTmp() {
        return saveToken;
    }

    public void saveBanners(List<Banner> bannerList) {
        Log.i("RayTest","儲存banner");
        this.banners = bannerList;
    }

    public List<Banner> getBanners() {
        if(banners==null)
            banners = new ArrayList<>();
        return banners;
    }

    public void saveOfficialListInfo(UserInfo data) {
        if(OfficialList==null)
            OfficialList = new ArrayList<UserInfo>();

        for(UserInfo info : OfficialList) {
            if(info != null && info.getId().equals(data.getId())) {
                OfficialList.remove(info);
                OfficialList.add(data);
                return ;
            }
        }

        OfficialList.add(data);
    }

    public List<UserInfo> getOfficialList() {
        return OfficialList;
    }

    public boolean isOfficialAccount(String uid) {
        Log.i("RayTest","比對："+uid+" OfficialList size:"+OfficialList.size());
        for(UserInfo official : OfficialList){
            Log.i("RayTest","official："+official.getId());
            if(official.getId().equals(uid)) {

                return true;
            }
        }
        return false;
    }

    public UserInfo getUserInfo(String uid) {
        for(UserInfo info : userInfoList){
            if(info.getId().equals(uid)){
                return info;
            }
        }
        return null;
    }

    public UserInfo getOfficialUserInfo(String uid) {
        for(UserInfo info : OfficialList){
            if(info.getId().equals(uid)){
                return info;
            }
        }
        return null;
    }

    public void addUserInfo(UserInfo item){
        Log.i("RayTest","add id: "+item.getId()+" size: "+userInfoList.size());
        if(userInfoList.contains(item.getId()))
        {
            userInfoList.remove(item);
            userInfoList.add(item);
        }else{
            userInfoList.add(item);
        }

        for(UserInfo info : userInfoList){
           Log.i("RayTest","id: "+info.getId());
        }
    }

    public void saveBlackList(List<BlackList> blackLists) {
        ArrayList<BlackList> mNewList = new ArrayList<>();
        for(BlackList list :blackLists){
            Log.i("RayTest","saveBlackList: "+list.getBlack_id()+" id:"+list.getId());
            if(!list.getBlack_id().equals("0"))
                mNewList.add(list);
        }
        this.mBlackList = mNewList;
    }

    public List<BlackList> getmBlackList() {
        if(mBlackList==null)
            mBlackList = new ArrayList<>();
        return mBlackList;
    }

    public boolean getIsHit(String ChekUid) {
        for(BlackList list : getmBlackList() ){
            if(list.getBlack_id().equals(ChekUid))

                return true;
        }
        return false;
    }
}

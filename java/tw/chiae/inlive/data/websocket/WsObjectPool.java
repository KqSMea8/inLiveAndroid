package tw.chiae.inlive.data.websocket;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.bean.websocket.WsLightHeartRequest;
import tw.chiae.inlive.data.bean.websocket.WsLoginRequest;
import tw.chiae.inlive.data.bean.websocket.WsLogoutRequest;
import tw.chiae.inlive.data.bean.websocket.WsPongRequest;
import tw.chiae.inlive.data.bean.websocket.WsPrvMsgRequest;
import tw.chiae.inlive.data.bean.websocket.WsPublicMsgRequest;
import tw.chiae.inlive.data.bean.websocket.WsRequest;
import tw.chiae.inlive.data.bean.websocket.WsRoomManageRequest;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.main.me.IMe;
import tw.chiae.inlive.presentation.ui.main.me.MeFragment;
import tw.chiae.inlive.util.L;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class WsObjectPool{

    private static final String LOG_TAG = WsObjectPool.class.getSimpleName();

    private static SparseArray<WsRequest> requestArray;

    private static final int REQ_LOGIN = 1;
    private static final int REQ_LOGOUT = 2;
    private static final int REQ_SEND_PUB_MSG = 3;
    private static final int REQ_ROOM_MANAGE = 4;
    private static final int REQ_SEND_PRV_MSG = 5;

    private static String nickname;
    private static String userId;
    private static String token;
    private static String ucuid;

    //Use singleton to avoid duplicated allocation
    private static WsPongRequest PONG_INSTANCE;

    static {
        if (PONG_INSTANCE == null) {
            synchronized (WsObjectPool.class) {
                if (PONG_INSTANCE == null) {
                    PONG_INSTANCE = new WsPongRequest();
                    PONG_INSTANCE.setMethod(SocketConstants.EVENT_PONG);
                    PONG_INSTANCE.setDevice(SocketConstants.DEVICE_ANDROID);
                }
            }
        }
    }

    /**
     * 释放所有资源，清空数据。
     */
    public static void release(){
        nickname = null;
        token = null;
        userId = null;
        if (requestArray != null) {
            requestArray.clear();
            requestArray = null;
        }
    }

    /**
     * 为用户初始化对象池。
     */
    public static void init(LoginInfo loginInfo) {
        String username = loginInfo.getNickname();
        String userId = loginInfo.getUserId();
        String token = loginInfo.getToken();
        String ucuid = loginInfo.getUcuid();

        WsObjectPool.token = token;
        WsObjectPool.userId = userId;
        WsObjectPool.nickname = username;
        WsObjectPool.ucuid = ucuid;

        requestArray = new SparseArray<>();
        WsLoginRequest loginRequest = new WsLoginRequest();
        loginRequest.setMethod(SocketConstants.EVENT_LOGIN);
        loginRequest.setUserName(username);
        loginRequest.setToken(token);
        loginRequest.setUserId(userId);
        loginRequest.setApproveid(LocalDataManager.getInstance().getLoginInfo().getApproveid());
        if (MeFragment.mrlinfo!=null&&MeFragment.mrlinfo.getLevel()!=null){
            loginRequest.setLevelId(MeFragment.mrlinfo.getLevel());
        }else {
            loginRequest.setLevelId("1");
        }
        loginRequest.setUcuid(ucuid);
        requestArray.put(REQ_LOGIN, loginRequest);

        WsLogoutRequest logoutRequest = new WsLogoutRequest();
        logoutRequest.setMethod(SocketConstants.EVENT_LOGOUT);
        logoutRequest.setUserName(username);
        logoutRequest.setToken(token);
        logoutRequest.setUserId(userId);
        requestArray.put(REQ_LOGOUT, logoutRequest);

        WsPublicMsgRequest pubMsgRequest = new WsPublicMsgRequest();
        pubMsgRequest.setMethod(SocketConstants.EVENT_PUB_MSG);
        pubMsgRequest.setChecksum("");
        requestArray.put(REQ_SEND_PUB_MSG, pubMsgRequest);

        WsRoomManageRequest manageRequest = new WsRoomManageRequest();
        manageRequest.setMethod(SocketConstants.EVENT_MANAGE);
        requestArray.put(REQ_ROOM_MANAGE, manageRequest);

        WsPrvMsgRequest prvMsgRequest = new WsPrvMsgRequest();
        prvMsgRequest.set_method_(SocketConstants.EVENT_PRV_MSG);
        requestArray.put(REQ_SEND_PRV_MSG, prvMsgRequest);
    }

    private static void checkInitOrThrow() {
        if (TextUtils.isEmpty(nickname) || TextUtils.isEmpty(userId) || TextUtils.isEmpty(token)
                || TextUtils.isEmpty(ucuid)
                || requestArray.size() == 0) {
            L.e(LOG_TAG, "un=%s, userId=%s, token=%s, array=%s", nickname, userId, token,
                    requestArray);
            if (!tryRestorePoolFromLocal()) {
                //throw new IllegalStateException("Pool not initialized correctly and cannot be " +"restored!");
            }
        }
    }

    /**
     * 在检测到对象池未初始化时执行的最后的恢复操作，如果能从本地恢复则可以避免抛出异常。
     *
     * @return 如果成功从本地存储的登录信息中恢复则返回true，否则返回false。
            */
    private static boolean tryRestorePoolFromLocal() {
        LoginInfo loginInfo = LocalDataManager.getInstance().getLoginInfo();
        L.d(LOG_TAG, "Trying to restore ws object pool: login info=%s.", loginInfo);
        if (loginInfo != null) {
            init(loginInfo);
            L.i(LOG_TAG, "Ws object pool has been restored successfully.");
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    //Assume type safe
    private static <T extends WsRequest> T get(int index) {
        if (requestArray==null)
            return null;
        return (T) requestArray.get(index);
    }

    public static WsLoginRequest newLoginRequest(String roomId,String approveid) {

        checkInitOrThrow();
        WsLoginRequest request = get(REQ_LOGIN);
        request.setRoomId(roomId);
        request.setApproveid(approveid);
        return request;
    }

    public static WsLogoutRequest newLogoutRequest(String roomId) {

        checkInitOrThrow();
        WsLogoutRequest request = get(REQ_LOGOUT);
        request.setRoomId(roomId);
        return request;
    }

    public static WsPublicMsgRequest newPublicMsgRequest(String content,String approveid) {
        checkInitOrThrow();

        WsPublicMsgRequest request = get(REQ_SEND_PUB_MSG);
        request.setContent(content);
        request.setApproveid(approveid);
        return request;
    }
    public static WsPrvMsgRequest newPrvMsgRequest(String from_client_id,
                                                   String from_user_id,
                                                   String from_client_name,
                                                   int vip,
                                                   String levelid,
                                                   String to_client_id,
                                                   String to_client_name,
                                                   String to_user_id,
                                                   String pub,
                                                   String conference_invitation,
                                                   String conference_type,
                                                   int conference_invitation_return,
                                                   String time,
                                                   String avatar) {
        checkInitOrThrow();
        WsPrvMsgRequest request = get(REQ_SEND_PRV_MSG);
        request.setFrom_client_id(from_client_id);
        request.setFrom_user_id(from_user_id);
        request.setFrom_client_name(from_client_name);
        request.setVip(vip);
        request.setLevelid(levelid);
        request.setTo_client_id(to_client_id);
        request.setTo_client_name(to_client_name);
        request.setTo_user_id(to_user_id);
        request.setPub(pub);
        WsPrvMsgRequest.ContentMsg contentMsg=request.new ContentMsg();
        contentMsg.setConference_invitation(conference_invitation);
        contentMsg.setConference_type(conference_type);
        contentMsg.setConference_invitation_return(conference_invitation_return);
        request.setContent(contentMsg);
        request.setTime(time);
        request.setAvatar(avatar);
        return request;
    }

    public static WsPongRequest newPongRequest() {
        return PONG_INSTANCE;
    }

    public static WsLightHeartRequest newLightHeartRequest(int colorIndex,String approveid){
        //暂不缓存
        WsLightHeartRequest request = new WsLightHeartRequest();
        request.setMethod(SocketConstants.EVENT_LIGHT_HEART);
        request.setColorIndex(colorIndex);
        request.setApproveid(approveid);
        return request;
    }
    public static WsRoomManageRequest newRoomManageRequest(String type,
                                                           String targetUserId,
                                                           String targetUsername){
        checkInitOrThrow();
        WsRoomManageRequest request = get(REQ_ROOM_MANAGE);
        request.setType(type);
        request.setTargetUserId(targetUserId);
        request.setTargetUsername(targetUsername);
        return request;
    }

//    设置为管理员
    public static WsRoomManageRequest newRoomManageRequest(String method,String targetUserId,String targetUsername,String type){
        WsRoomManageRequest request = get(REQ_ROOM_MANAGE);
        request.setMethod(method);
        request.setType(type);
        request.setTargetUserId(targetUserId);
        request.setTargetUsername(targetUsername);
        return request;
    }


}

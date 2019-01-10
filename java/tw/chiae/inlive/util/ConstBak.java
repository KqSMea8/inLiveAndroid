package tw.chiae.inlive.util;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class ConstBak {

    // 測試環境開關
    // false :  正式環境  true :測試環境
    public static final boolean TEST_ENVIROMENT_SW = true;
    public static final int VIEW_THROTTLE_TIME = 500;

    /**
     * 直播间心心防抖动时间。
     * 1s <= 50次点击。
     */
    public static final int LIVE_ROOM_HEART_THROTTLE = 200;
    public static String HOST_DNS = getInstance();
    public static String WS_HOST  = getSocketHost();


    /**
     * WS服务器地址。
     */
//    139.129.19.190InLive的
//    public static final String WS_HOST_FOR_PING = "114.55.150.132";
    public static final String WS_HOST_FOR_PING = "chat1.inlive.tw";
    public static final String WS_HOST_FOR_TESTPING = "chat.inlive.tw";
//    public static final String WS_HOST_FOR_PING = "chatroom.cxtv.kaduoxq.com";

    /**
     * Web Socket 服务器地址。
     */
    public static final String SOCKET_URL = "ws://" + WS_HOST + ":7272";

    /**
     * 主站域名。
     */
    private static final String MAIN_HOST_DEV = "192.168.2.104:80";
    //    private static final String MAIN_HOST_TEST = "meilibo.cxtv.kaduoxq.com";
//    demo.meilibo.net
//    public static final String MAIN_HOST_TEST = "114.55.36.241";
//    www.51pride.com
//    net.cn
//  test.meilibo.net 俊儿
    public static final String MAIN_HOST_TEST = "api.inlive.tw";
    public static final String MAIN_HOST_RELEASE = "api1.inlive.tw";
    public static final String SEC_HOST_TEST = "http://api2.inlive.tw";
    public static final String PLAYER_BASE_URL = "rtmp://daniulive.com:1935/hls/stream";
    public static final String PUBLISH_BASE_URL = PLAYER_BASE_URL;

    /**
     * API主机域名
     */

    private static String getSocketHost() {
        if(TEST_ENVIROMENT_SW)
            return WS_HOST_FOR_TESTPING;
        else
            return WS_HOST_FOR_PING;
    }

    private static String getInstance() {
        if(TEST_ENVIROMENT_SW)
            return MAIN_HOST_TEST;
        else
            return MAIN_HOST_RELEASE;
    }

    public static final String MAIN_HOST_FOR_PING = HOST_DNS;

    public static final String MAIN_HOST_URL = "http://" + MAIN_HOST_FOR_PING;

    public static final String WEB_BASE_URL = MAIN_HOST_URL + "/OpenAPI/v1/";

    public static final String LIVE_FINISH_BROADCAST_ACTION = "tw.chiae.inlive.ACTION_FINISH";
//    http://demo.meilibo.net/OpenAPI/v1/

    /**
     * 微信开放平台appId
     */
    public static final String WX_APPID = "wxd87b8fe22a008417";
    public static final String PAY_RESULT_STATUS_SUCCESS = "200";
    public static final String PAY_RESULT_STATUS_FAIL = "500";
    public static final String PAY_TYPE_WEIXIN = "1";

    public static final String SNAP_DEFAULT_NAME = "/style/images/default.gif";
    public static boolean isDebugMode = false;
    public static final String Server_API3_HOST = "https://api3.inlive.tw/";
    public static final String Server_Stat_URL = "https://api3.inlive.tw/api3/get_server_status";

    // true : Gash Mode
    public static boolean IsPayMode =false;

    //K 歌
    public static final String HostApi2Url = "http://api2.inlive.tw/";
    public static final String checkServerUrl = "http://api2.inlive.tw/api2/get_event_settings";
    public static final String contributeList = WEB_BASE_URL+"user/contributeList";
    public static boolean NewMode = false;
    //public static final String HotPointAPI = "https://api3.inlive.tw/hotpoint/getdaimond/";

    //更新熱度
    //public static final String checkHotValue = "http://apitest2.inlive.tw/hotpoint/getht";
    //官方帳號清單
    public static final String[] OfficialAccountListID = {"1170799","1000787","1000516","1000518"};
}

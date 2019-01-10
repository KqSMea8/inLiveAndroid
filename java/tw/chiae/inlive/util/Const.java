package tw.chiae.inlive.util;

import java.util.Arrays;

import tw.chiae.inlive.presentation.ui.chatting.utils.ConfigSharePreference;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class Const {

    // 測試環境開關
    // false :  正式環境  true :測試環境
    public static boolean TEST_ENVIROMENT_SW ;
    //遠端動態修改設定值開關
    public static boolean ModifyMode = false;
    //新ＵＩ開關
    public static boolean isNewUIMode = true;
    // true : Gash Mode
    public static boolean IsPayMode =true;
    public static final int VIEW_THROTTLE_TIME = 500;



    /**
     * 直播间心心防抖动时间。
     * 1s <= 50次点击。
     */
    public static final int LIVE_ROOM_HEART_THROTTLE = 200;
    public static String HOST_DNS = getInstance(ConfigSharePreference.readEnviroment());
    public static String WS_HOST  = getSocketHost(ConfigSharePreference.readEnviroment());


    /**
     * WS服务器地址。
     * *///    139.129.19.190InLive的
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
//    net.cnㄉ
//  test.meilibo.net 俊儿 c  ff
    public static final String MAIN_HOST_TEST = "api.inlive.tw";
    public static final String MAIN_HOST_RELEASE = "api1.inlive.tw";
    public static final String SEC_HOST_TEST = "http://api2.inlive.tw";
    public static final String PLAYER_BASE_URL = "rtmp://daniulive.com:1935/hls/stream";
    public static final String PUBLISH_BASE_URL = PLAYER_BASE_URL;
    private static boolean environment;


    private static int toast;
    //彈幕開關 記錄
    public static int dammuSW;
    //貴賓進場特效
    public static int VipEnterSW;
    //等級進場特效
    public static int LevelEnterSW;
    public static int MaxLevel = 128;
    public static String RankPageUrl = "https://api2.inlive.tw/events/reward?uid=";

    /**
     * API主机域名
     */

    private static String getSocketHost(boolean state) {
        if(state)
            return WS_HOST_FOR_TESTPING;
        else
            return WS_HOST_FOR_PING;
    }

    private static String getInstance(boolean state) {
        if(state)
            return MAIN_HOST_TEST;
        else
            return MAIN_HOST_RELEASE;
    }

    public static String MAIN_HOST_FOR_PING = HOST_DNS;

    public static String MAIN_HOST_URL = "http://" + MAIN_HOST_FOR_PING;

    public static String WEB_BASE_URL = MAIN_HOST_URL + "/OpenAPI/v1/";

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


    //K 歌
    public static final String HostApi2Url = "http://api2.inlive.tw/";
    public static final String checkServerUrl = "http://api2.inlive.tw/api2/get_event_settings";
    public static final String contributeList = WEB_BASE_URL+"user/contributeList";
    public static boolean NewMode = false;
    //public static final String HotPointAPI = "https://api3.inlive.tw/hotpoint/getdaimond/";

    //更新熱度
    //public static final String checkHotValue = "http://apitest2.inlive.tw/hotpoint/getht";
    //官方帳號清單
    //public static final String[] OfficialAccountListID = {"1170799","1000787","1000516","1000518"};
    //public static String[] OfficialAccountListID = {"1155914","1170799"};
    public static String[] OfficialAccountListID = getOfficical();
    public static String MainOfficialAccount = getMainOfficial();



    public static void setEnvironment(boolean environment) {
        Const.environment = environment;
        TEST_ENVIROMENT_SW = environment;
        HOST_DNS = getInstance(environment);
        WS_HOST  = getSocketHost(environment);
        MAIN_HOST_FOR_PING = HOST_DNS;
        MAIN_HOST_URL = "http://"   + MAIN_HOST_FOR_PING;
        WEB_BASE_URL = MAIN_HOST_URL + "/OpenAPI/v1/";
        if(environment)
            RankPageUrl = "https://testapi3.inlive.tw/events/reward?uid=";
        else
            RankPageUrl = "https://api2.inlive.tw/events/reward?uid=";
        OfficialAccountListID = getOfficical();
        MainOfficialAccount = getMainOfficial();
    }

    public static boolean getEnvironment() {
        return environment;
    }

    public static int getToast() {
        return toast;
    }

    public static void setToast(int toast) {
        Const.toast = toast;
    }

    public static String[] getOfficical() {

        if(TEST_ENVIROMENT_SW) {
            String[] list = {"1155914", "1170799"};
            return list;
        }
        else{
            String[] list = {"1155914"};
            return list;
        }
    }

    private static String getMainOfficial() {
        if(TEST_ENVIROMENT_SW) {
            return "1170799";
        }
        else{
            return "1155914";
        }
    }
}

package tw.chiae.inlive.data.bean.websocket;

import tw.chiae.inlive.presentation.ui.chatting.utils.TimeFormat;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;
import tw.chiae.inlive.util.Const;

/**
 * Created by Administrator on 2016/6/18 0018.
 */
public class SystemWelcome implements RoomPublicMsg{
    private String client_name;
    private String type;
    private int levelid;
    private String approveid ;
    private String time;
    private int flyshow;
    private int flyType;
    private long hotpoint = 0 ;
    private int vip;
    private String avatar;
    private int visitant;
    private String user_id;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }



    public int getVisitant() {
        return visitant;
    }

    public void setVisitant(int visitant) {
        this.visitant = visitant;
    }



    public long getHotpoint() {
        if(hotpoint<=0)
            hotpoint=0;
        return hotpoint;
    }

    public void setHotpoint(long hotpoint) {
        this.hotpoint = hotpoint;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getApproveid() {
        return approveid;
    }

    public void setApproveid(String approveid) {
        this.approveid = approveid;
    }

    public int getLevelid() {
        return levelid;
    }

    public void setLevelid(int levelid) {
        this.levelid = levelid;
    }

    public String getClient_name() {
        String Prefix = "fb_";
        if(client_name.contains(Prefix)){
            client_name = client_name.substring(Prefix.length(),client_name.length());
        }
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getTime() {

        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getFlyshow() {
        if(flyshow<0)
            flyshow=0;
        return flyshow;
    }

    public void setFlyshow(int flyshow) {
        this.flyshow = flyshow;
    }

    public int getflyType() {
        //
        Log.i("RayTest","LevelEnterSW: "+Const.LevelEnterSW +"  VipEnterSW:"+Const.VipEnterSW+ "  visitant " +visitant);
        if(Const.LevelEnterSW==0)
            return 0;
        if(Const.VipEnterSW==0)
            visitant = 0;
        if(levelid>60){
            if(visitant==1)
                return 3;
            return 1;
        }else{
            if(visitant==1)
                return 2;
            return 0;
        }
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    //    "adminer_list" =     (
//            );
//    "client_id" = 0a0b20df08fc00000004;
//    "client_list" =     (
//            );
//    "client_name" = "\U6d4b\U8bd5";
//    levelid = 1;
//    time = "18:27";
//    type = login;
//    ucuid = 1190;
//    "user_id" = 1245;
//    vip = 0;

//{
// "type":"login",
// "client_id":"7f00000108fe00000017",
// "client_name":"\u514b\u7f85\u7c73",
// "user_id":"1000787",
// "vip":0,
// "levelid":"36",
// "time":"10:12",
// "approveid":"\u65e0",
// "avatar":"\/style\/avatar\/760\/1000787_middle.jpg?t=1502935962",
// "message_time":1502935962,
// "flyshow":1,
// "visitant":0,
// "guard":0}
}

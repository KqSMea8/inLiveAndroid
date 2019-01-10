package tw.chiae.inlive.presentation.ui.room;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tw.chiae.inlive.presentation.ui.main.mergefilm.EventActivity;

/**
 * Created by rayyeh on 2017/6/2.
 */


public class HotPointInfo implements Serializable{
    //[{"uid":"1000626","roomid":"21581491637270","starttime":"1496366338","coin":"4","hotpoint":"26","cdate":"2017-06-02"}]

    //[{"uid":"1000601","coin":0,"day":"2017-06-27"}]
    private long uid;
    private long roomid;
    private long starttime;
    private long coin;
    private long hotpoint;
    private String day;


    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getRoomid() {
        return roomid;
    }

    public void setRoomid(long roomid) {
        this.roomid = roomid;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getCoin() {
        return coin;
    }

    public void setCoin(long coin) {
        this.coin = coin;
    }

    public long  getHotpoint() {
        return hotpoint;
    }

    public void setHotpoint(long hotpoint) {
        this.hotpoint = hotpoint;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String cdate) {
        this.day = cdate;
    }

}

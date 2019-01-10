package tw.chiae.inlive.data.repository;

import tw.chiae.inlive.data.bean.BaseResponse;

/**
 * Created by rayyeh on 2017/7/10.
 */

public class HotPointResponse<T> extends BaseResponse {
    private long uid;
    private long coin;
    private String day;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getCoin() {
        return coin;
    }

    public void setCoin(long coin) {
        this.coin = coin;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String toString() {
        return "uid:"+uid+"  Coin="+coin+" day:"+day;
    }
}

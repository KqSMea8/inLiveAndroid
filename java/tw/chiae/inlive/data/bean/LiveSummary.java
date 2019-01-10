package tw.chiae.inlive.data.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016/12/17 0017.
 */

public class LiveSummary implements Parcelable {
    protected String id;
    /**
     * 房间号
     */
    protected String curroomnum;
    /**
     * 观看人数
     */
    protected int online;
    /**
     * 头像小中图
     */
    protected String avatar;
    /**
     * 原图 做直播壁纸等
     */
    protected String snap;

    protected String city;

    protected String nickname;

    protected int is_attention;

/*    public String getApproveid() {
        return approveid;
    }

    protected String approveid;*/

    public String getId() {
        return id;
    }

    public String getCurroomnum() {
        return curroomnum;
    }

    public int getOnline() {
        return online;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getSnap() {
        return snap;
    }

    public String getCity() {
        return city;
    }

    public String getNickname() {
        return nickname;
    }

    public int getIs_attention() {
        return is_attention;
    }

    @Override
    public String toString() {
        return "LiveSummary{" +
                "id='" + id + '\'' +
                ", curroomnum='" + curroomnum + '\'' +
                ", online=" + online +
                ", avatar='" + avatar + '\'' +
                ", snap='" + snap + '\'' +
                ", city='" + city + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public LiveSummary() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.nickname);
        dest.writeString(this.curroomnum);
        dest.writeString(this.snap);
        dest.writeString(this.city);
        dest.writeInt(this.online);
        dest.writeString(this.avatar);
    }

    protected LiveSummary(Parcel in) {
        this.id = in.readString();
        this.nickname = in.readString();
        this.curroomnum = in.readString();
        this.snap = in.readString();
        this.city = in.readString();
        this.online = in.readInt();
        this.avatar = in.readString();
    }

    public static final Parcelable.Creator<LiveSummary> CREATOR = new Parcelable.Creator<LiveSummary>() {
        @Override
        public LiveSummary createFromParcel(Parcel source) {
            return new LiveSummary(source);
        }

        @Override
        public LiveSummary[] newArray(int size) {
            return new LiveSummary[size];
        }
    };
}

package tw.chiae.inlive.data.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 主播简介，用于热门主播，推荐主播,etc。
 * @author Muyangmin
 * @since 1.0.0
 */
public class HotAnchorSummary extends LiveSummary implements Parcelable  {

    /**
     *  "id": "3542",
     "curroomnum": 1979404553,
     "online": 0,
     "roomTitle": null,
     "roomTopic": null,
     "avatar": "/style/images/default.gif",
     "snap": "/style/images/default.gif",
     "province": "江苏",
     "city": "南京市",
     "nickname": "152****9830",
     "sid": "0",
     "is_attention": 0,
     "starttime": "1481786346",
     "bsid": "8246",
     "private": 0,
     "channel_id": "0"
     */

    private String roomTitle;
    private List<RoomTopic> roomTopic;
    private String starttime;
    private String broadcasting;
    private String hotpoint ;
    private String approveid;


    /*@SerializedName("approveid")


    public String getHotpoint() {
        return hotpoint;
    }

    public void setHotpoint(String hotpoint) {
        this.hotpoint = hotpoint;
    }



*/
    public String getApproveid() {
        return approveid;
    }

    public void setApproveid(String approveid) {
        this.approveid = approveid;
    }

    public String getHotpoint() {
        return hotpoint;
    }

    public void setHotpoint(String hotpoint) {
        this.hotpoint = hotpoint;
    }

    public String getBroadcasting() {
        return broadcasting;
    }

    public void setBroadcasting(String broadcasting) {
        this.broadcasting = broadcasting;
    }

    public String getStarttime() {
        return starttime;
    }

    public String getRoomTitle() {
        return roomTitle;
    }

    public List<RoomTopic> getTopic() {
        return roomTopic;
    }

    public void setTopic(List<RoomTopic> topic) {
        this.roomTopic = topic;
    }

    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCurrentRoomNum() {
        return curroomnum;
    }

    public void setCurrentRoomNum(String currentRoomNum) {
        this.curroomnum = currentRoomNum;
    }

    public String getSnap() {
        return snap;
    }

    public void setSnap(String snap) {
        this.snap = snap;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getOnlineCount() {
        return online;
    }

    public void setOnlineCount(int onlineCount) {
        this.online = onlineCount;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }



    @Override
    public int describeContents() {
        return 0;
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
        dest.writeString(this.roomTitle);
        dest.writeInt(this.is_attention);
        dest.writeString(this.starttime);
    }

    public HotAnchorSummary(){

    }

    protected HotAnchorSummary(Parcel in) {
        this.id = in.readString();
        this.nickname = in.readString();
        this.curroomnum = in.readString();
        this.snap = in.readString();
        this.city = in.readString();
        this.online = in.readInt();
        this.avatar = in.readString();
        this.roomTitle=in.readString();
        this.is_attention=in.readInt();
        this.starttime=in.readString();
    }

    public static final Parcelable.Creator<HotAnchorSummary> CREATOR = new Parcelable.Creator<HotAnchorSummary>() {
        @Override
        public HotAnchorSummary createFromParcel(Parcel source) {
            return new HotAnchorSummary(source);
        }

        @Override
        public HotAnchorSummary[] newArray(int size) {
            return new HotAnchorSummary[size];
        }
    };



    public class RoomTopic implements Parcelable{
        String id;
        String title;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeString(id);
        }

        protected RoomTopic(Parcel in) {
            this.id = in.readString();
            this.title=in.readString();
        }

        public final Parcelable.Creator<RoomTopic> CREATOR = new Parcelable.Creator<RoomTopic>() {
            public  RoomTopic createFromParcel(Parcel in) {
                return new RoomTopic(in);
            }

            public RoomTopic[] newArray(int size) {
                return new RoomTopic[size];
            }
        };
    }
}

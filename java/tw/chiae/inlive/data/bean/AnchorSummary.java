package tw.chiae.inlive.data.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 搜索和推荐页的实体。
 * @author Muyangmin
 * @since 1.0.0
 */
public class AnchorSummary extends LiveSummary implements Parcelable {

    public static final int IS_ATTENTION = 1;



    /**
     * id : 451
     * nickname : 晓甜要坚持
     * curroomnum : 1704013154
     * ucuid : 453
     * bigpic : /Public/bigpic/2015-07/55a1e2e56856b.jpg
     * broadcasting : n
     * offlinevideo : http://b.51miao.com.cn/5522.flv
     * sex : 0
     * intro :
     * avatar : /passport/avatar.php?uid=453&size=middle
     * emceelevel : 0
     */
    private String approveid;
    private String hotpoint;

    @SerializedName("bigpic")
    private String bigPic;
    private String broadcasting;
    @SerializedName("offlinevideo")
    private String offlineVideo;
    private int sex;
    private boolean following;
    private String intro;
    @SerializedName("emceelevel")
    private int emceeLevel;



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



    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSnap() {
        return snap;
    }

    public void setSnap(String snap) {
        this.snap = snap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        String Prefix="fb_";
        if(nickname.contains(Prefix)){
            nickname = nickname.substring(Prefix.length(),nickname.length());
        }
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

    public String getBigPic() {
        return bigPic;
    }

    public void setBigPic(String bigPic) {
        this.bigPic = bigPic;
    }

    public String getBroadcasting() {
        return broadcasting;
    }

    public void setBroadcasting(String broadcasting) {
        this.broadcasting = broadcasting;
    }

    public String getOfflineVideo() {
        return offlineVideo;
    }

    public void setOfflineVideo(String offlineVideo) {
        this.offlineVideo = offlineVideo;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getEmceeLevel() {
        return emceeLevel;
    }

    public void setEmceeLevel(int emceeLevel) {
        this.emceeLevel = emceeLevel;
    }


    @Override
    public String toString() {
        return "AnchorSummary{" +
                "id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", currentRoomNum='" + curroomnum + '\'' +
                ", bigPic='" + bigPic + '\'' +
                ", broadcasting='" + broadcasting + '\'' +
                ", offlineVideo='" + offlineVideo + '\'' +
                ", sex=" + sex +
                ", following=" + following +
                ", intro='" + intro + '\'' +
                ", avatar='" + avatar + '\'' +
                ", emceeLevel=" + emceeLevel +
                ", snap='" + snap + '\'' +
                ", city='" + city + '\'' +
                '}';
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
        dest.writeString(this.bigPic);
        dest.writeString(this.broadcasting);
        dest.writeString(this.offlineVideo);
        dest.writeInt(this.sex);
        dest.writeInt(this.emceeLevel);
    }

    protected AnchorSummary(Parcel in) {
        this.id = in.readString();
        this.nickname = in.readString();
        this.curroomnum = in.readString();
        this.snap = in.readString();
        this.city = in.readString();
        this.online = in.readInt();
        this.avatar = in.readString();
        this.bigPic=in.readString();
        this.broadcasting=in.readString();
        this.offlineVideo=in.readString();
        this.sex=in.readInt();
        this.emceeLevel=in.readInt();
    }

    public static final Parcelable.Creator<AnchorSummary> CREATOR = new Parcelable.Creator<AnchorSummary>() {
        @Override
        public AnchorSummary createFromParcel(Parcel source) {
            return new AnchorSummary(source);
        }

        @Override
        public AnchorSummary[] newArray(int size) {
            return new AnchorSummary[size];
        }
    };
}

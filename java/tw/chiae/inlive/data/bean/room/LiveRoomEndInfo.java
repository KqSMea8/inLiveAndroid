package tw.chiae.inlive.data.bean.room;

import com.google.gson.annotations.SerializedName;

/**
 * 直播结束后看到的数据，目前包含观看人数和秀币总收入。
 *
 * @author Muyangmin
 * @since 1.0.0
 */
public class LiveRoomEndInfo {

    @SerializedName("client")
    private String audienceCount;
    @SerializedName("coin")
    private String coinIncome;
    private String avatar;
    private String nickname;
    private String coinStartcome;

    public String getAudienceCount() {
        return audienceCount;
    }

    public void setAudienceCount(String audienceCount) {
        this.audienceCount = audienceCount;
    }

    public String getCoinIncome() {
        return coinIncome;
    }

    public void setCoinIncome(String coinIncome) {
        this.coinIncome = coinIncome;
    }

    public String getCoinStartcome() {
        return coinStartcome;
    }

    public void setCoinStartcome(String coinStart) {
        this.coinStartcome = coinStart;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "LiveRoomEndInfo{" +
                "audienceCount='" + audienceCount + '\'' +
                ", coinIncome='" + coinIncome + '\'' +
                ", avatar='" + avatar + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}

package tw.chiae.inlive.data.bean.websocket;

import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class SendGiftMsg implements RoomPublicMsg {

    /**
     * giftIcon : /style/images/gift/50/5.png
     * giftCount : 1
     * userId : 829
     * giftName : 玫瑰
     * from_client_name : testapp
     * from_client_avatar : /passport/avatar.php?uid=752&size=middle
     * type : sendGift
     */

    private String giftIcon;
    private int giftCount;
    private long anchorBalance;
    @SerializedName("userId")
    private String fromUserId;
    private String giftName;
    @SerializedName("from_client_name")
    private String fromUserName;
    @SerializedName("from_client_avatar")
    private String fromUserAvatar;
    @SerializedName("levelid")
    private int level;
    private String isred;
    private String amount;
    private String imageUrl;
    private int comboHit;
    private double hotpoint = 0;

    private String approveid ;
    public String getApproveid() {
        return approveid;
    }

    public void setApproveid(String approveid) {
        this.approveid = approveid;
    }
    public int getComboHit() {
        return comboHit;
    }

    public void setComboHit(int comboHit) {
        this.comboHit = comboHit;
    }

    public String getRedId() {
        return redId;
    }

    public void setRedId(String redId) {
        this.redId = redId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private String redId;

    public String getRed_Id() {
        return redId;
    }

    public void setRed_Id(String red_Id) {
        this.redId = red_Id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getIsred() {
        return isred;
    }

    public void setIsred(String isred) {
        this.isred = isred;
    }

    public String getGiftIcon() {
        return giftIcon;
    }

    public void setGiftIcon(String giftIcon) {
        this.giftIcon = giftIcon;
    }

    public int getGiftCount() {
        return giftCount;
    }

    public void setGiftCount(int giftCount) {
        this.giftCount = giftCount;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public String getFromUserName() {
        String Prefix = "fb_";
        if(fromUserName.contains(Prefix)){
            fromUserName = fromUserName.substring(Prefix.length(),fromUserName.length());
        }
        return fromUserName;
    }

    public double getHotpoint() {
        if(hotpoint<=0)
            hotpoint=0;
        return Math.round(hotpoint);
    }

    public void setHotpoint(double hotpoint) {
        this.hotpoint = Math.round(hotpoint);
    }


    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getFromUserAvatar() {
        return fromUserAvatar;
    }

    public void setFromUserAvatar(String fromUserAvatar) {
        this.fromUserAvatar = fromUserAvatar;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getAnchorBalance() {
        return anchorBalance;
    }

    public void setAnchorBalance(long anchorBalance) {
        this.anchorBalance = anchorBalance;
    }

    @Override
    public String toString() {
        return "SendGiftMsg{" +
                "giftIcon='" + giftIcon + '\'' +
                ", giftCount=" + giftCount +
                ", anchorBalance=" + anchorBalance +
                ", fromUserId='" + fromUserId + '\'' +
                ", giftName='" + giftName + '\'' +
                ", fromUserName='" + fromUserName + '\'' +
                ", fromUserAvatar='" + fromUserAvatar + '\'' +
                ", level=" + level +
                ", isred='" + isred + '\'' +
                ", amount='" + amount + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", comboHit=" + comboHit +
                ", redId='" + redId + '\'' +
                '}';
    }
}
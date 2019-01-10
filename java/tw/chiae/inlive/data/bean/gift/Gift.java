package tw.chiae.inlive.data.bean.gift;

import com.google.gson.annotations.SerializedName;

/**
 * 礼物实体类，标记礼物的内容、图片等。
 */
public class Gift {
    private String id;
    private String typeId;
    @SerializedName("giftname")
    private String displayName;
    @SerializedName("gifticon")
    private String imageUrl;
    @SerializedName("needcoin")
    private int price;   //use int instead of double
    private int exp;
    private String isred;
    private String redId;

    public String getRedId() {
        return redId;
    }

    public void setRedId(String redId) {
        this.redId = redId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public String getIsred() {
        return isred;
    }

    public void setIsred(String isred) {
        this.isred = isred;
    }
}
package tw.chiae.inlive.data.bean;

import com.google.gson.annotations.SerializedName;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class Banner {

    /**
     * img_url : 图片地址
     * target_url : 点击跳转的目标地址
     */

    @SerializedName("img_url")
    private String imageUrl;
    @SerializedName("target_url")
    private String targetUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Banner){
            Banner banner = (Banner) obj;
            return this.imageUrl.equals(banner.getImageUrl());
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "Banner{" +
                "imageUrl='" + imageUrl + '\'' +
                ", targetUrl='" + targetUrl + '\'' +
                '}';
    }
}

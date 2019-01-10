package tw.chiae.inlive.data.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class HotAnchorPageBean {


    /**
     * total_cnt : 5
     * page : 1
     * size : 1
     * page_cnt : 5
     * list : [{"id":"535","nickname":"bendq01","curroomnum":"7777","snap":"/style/images/default
     * .gif","city":"长沙市","online":40,"avatar":"/passport/avatar.php?uid=540&size=middle"}]
     * banner : [{"img_url":"","target_url":""}]
     */

    @SerializedName("total_cnt")
    private int totalCount;

    private List<HotAnchorSummary> recommend_first;

    private List<Banner> banner;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<HotAnchorSummary> getList() {
        return recommend_first;
    }

    public void setList(List<HotAnchorSummary> list) {
        this.recommend_first = list;
    }

    public List<Banner> getBanner() {
        return banner;
    }

    public void setBanner(List<Banner> banner) {
        this.banner = banner;
    }

    @Override
    public String toString() {
        return "HotAnchorPageBean{" +
                "totalCount=" + totalCount +
                ", list=" + recommend_first +
                ", banner=" + banner +
                '}';
    }
}

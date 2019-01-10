package tw.chiae.inlive.data.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class PageBean<T> {

    @SerializedName("total_cnt")
    private Long totalNum;
    @SerializedName("sum_coin")
    private Long sum_coin;
    @SerializedName("page")
    private String page_index;
    private List<T> list;

    public String getPage_index() {
        return page_index;
    }

    public void setPage_index(String page_index) {
        this.page_index = page_index;
    }

    public Long getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Long totalNum) {
        this.totalNum = totalNum;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Long getSum_coin() {
        return sum_coin;
    }

    public void setSum_coin(Long sum_coin) {
        this.sum_coin = sum_coin;
    }
}

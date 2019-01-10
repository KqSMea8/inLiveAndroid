package tw.chiae.inlive.data.bean.websocket;

import tw.chiae.inlive.data.bean.HotAnchorSummary;

import java.util.List;

/**
 * Created by 繁华丶落尽 on 2017/2/7.
 */
public class getLikeList {
    List<HotAnchorSummary> list;

    public List<HotAnchorSummary> getList() {
        return list;
    }

    public void setList(List<HotAnchorSummary> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "getLikeList{" +
                "list=" + list +
                '}';
    }
}

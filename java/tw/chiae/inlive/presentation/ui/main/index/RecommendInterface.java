package tw.chiae.inlive.presentation.ui.main.index;

import java.util.List;

import tw.chiae.inlive.data.bean.AnchorSummary;
import tw.chiae.inlive.data.bean.HotAnchorSummary;
import tw.chiae.inlive.data.bean.ThemBean;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.presentation.ui.base.page.PagedUiInterface;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public interface RecommendInterface extends PagedUiInterface<AnchorSummary> {
    void showEmptyResult();

    //    得到热门话题
    void onThemBean(ThemBean themBean);

    /**
     * 得到私密类型
     */
    void showPrivateLimit(PrivateLimitBean bean);

    /**
     * 得到许可进入直播间
     */
    void startGoPlayFragment();

    void saveAnchorsInfoData(List<HotAnchorSummary> list);
}

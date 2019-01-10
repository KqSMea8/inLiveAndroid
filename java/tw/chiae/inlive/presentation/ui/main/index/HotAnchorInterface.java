package tw.chiae.inlive.presentation.ui.main.index;

import tw.chiae.inlive.data.bean.HotAnchorSummary;
import tw.chiae.inlive.data.bean.Banner;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.presentation.ui.base.page.PagedUiInterface;
import tw.chiae.inlive.presentation.ui.main.mergefilm.EventActivity;

import java.util.List;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public interface HotAnchorInterface extends PagedUiInterface<HotAnchorSummary> {

    void displayBanners(List<Banner> banners);

    /**
     * 得到私密类型
     */
    void showPrivateLimit(PrivateLimitBean bean);

    /**
     * 得到许可进入直播间
     */
    void startGoPlayFragment();

    void UpdateActivateEvent(EventActivity eventActivity);

    void CompleteDownloadBanner(List<Banner> paths);
}

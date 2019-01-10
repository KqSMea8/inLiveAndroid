package tw.chiae.inlive.presentation.ui.main.index;

import tw.chiae.inlive.data.bean.AnchorSummary;
import tw.chiae.inlive.data.bean.ThemBean;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.presentation.ui.base.page.PagedUiInterface;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public interface CommendInterface extends PagedUiInterface<AnchorSummary> {
    void showEmptyResult();

    /**
     * 得到私密类型
     */
    void showPrivateLimit(PrivateLimitBean bean);

    /**
     * 得到许可进入直播间
     */
    void startGoPlayFragment();
}

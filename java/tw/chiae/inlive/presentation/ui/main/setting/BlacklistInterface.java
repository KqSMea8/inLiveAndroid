package tw.chiae.inlive.presentation.ui.main.setting;

import tw.chiae.inlive.data.bean.room.HitList;
import tw.chiae.inlive.domain.BlackList;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;
import tw.chiae.inlive.presentation.ui.base.page.PagedUiInterface;

import java.util.List;

/**
 * @author lww
 * @since 1.0.0
 */
public interface BlacklistInterface extends BaseUiInterface {
    void showResult(List<HitList> list);
    void CompleteBlackList();

    void CompleteDelBlackList(List<BlackList> blackLists);

    void FailDelBlackList();
}

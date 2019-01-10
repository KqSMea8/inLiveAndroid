package tw.chiae.inlive.presentation.ui.main.me;

import tw.chiae.inlive.data.bean.AnchorSummary;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;
import tw.chiae.inlive.presentation.ui.base.page.PagedUiInterface;

import java.util.List;

/**
 * Created by huanzhang on 2016/4/20.
 */
public interface IUserList extends PagedUiInterface<AnchorSummary> {

    void showEmptyResult();
}

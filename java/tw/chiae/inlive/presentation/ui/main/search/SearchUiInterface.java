package tw.chiae.inlive.presentation.ui.main.search;

import tw.chiae.inlive.data.bean.AnchorSummary;
import tw.chiae.inlive.presentation.ui.base.page.PagedUiInterface;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public interface SearchUiInterface extends PagedUiInterface<AnchorSummary> {
    void showEmptyResult();
}

package tw.chiae.inlive.presentation.ui.main;

import tw.chiae.inlive.data.bean.EventSummary;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.presentation.ui.base.page.PagedUiInterface;
import tw.chiae.inlive.presentation.ui.main.mergefilm.EventActivity;

/**
 * Created by rayyeh on 2017/7/19.
 */

public interface EventInterface extends PagedUiInterface<EventSummary> {
    void UpdateActivateEvent(EventActivity eventActivity);

    void showUserInfo(UserInfo data);
}

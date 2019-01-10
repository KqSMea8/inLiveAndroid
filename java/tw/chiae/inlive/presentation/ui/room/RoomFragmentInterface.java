package tw.chiae.inlive.presentation.ui.room;

import java.util.List;

import cn.jpush.im.android.api.model.Conversation;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.domain.BlackList;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;
import tw.chiae.inlive.presentation.ui.main.mergefilm.EventActivity;

/**
 * Created by rayyeh on 2017/7/11.
 */

interface RoomFragmentInterface extends BaseUiInterface {
    void UpdateActivateEvent(EventActivity eventActivity);

    void SendReportComplete(String s);

    void UpdateConversationUserInfo(List<PriConversation> userInfoList);

    void notifChange(UserInfo data);

    void setToTop(Conversation conversation);

    void CompleteDelBlackList(List<BlackList> blackLists, int code, String blackUid);

    void CompleteAddBlackList(List<BlackList> blackLists, String blackUserId);
}

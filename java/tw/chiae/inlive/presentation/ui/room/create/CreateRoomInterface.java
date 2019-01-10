package tw.chiae.inlive.presentation.ui.room.create;

import tw.chiae.inlive.data.bean.ThemBean;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.bean.room.CreateRoomBean;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public interface CreateRoomInterface extends BaseUiInterface {
    void showInfo(UserInfo info);
    void onPushStreamReady(String address);

    void onThemBean(ThemBean themBean);

    void onCreateConferenceRoom(String roomName);

    void onCreateRoom(CreateRoomBean createRoomBean, String title);
}

package tw.chiae.inlive.presentation.ui.widget;

import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.bean.room.RoomAdminInfo;
import tw.chiae.inlive.domain.BlackList;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;

import java.util.List;

/**
 * Created by huanzhang on 2016/4/16.
 */
public interface IUserInfoDialog extends BaseUiInterface {
    void showUserInfo(UserInfo info);
    //    得到播放人数
    void getAdminLists(List<RoomAdminInfo> adminList);
//    如果该房间没有管理员
    void adminnullgoinit();
    //    啦黑
    void getHitCode(int code, String hitid);
    //      解除拉黑
    void getRemoveHitCode(int code, String hitid);
    //    关注
    void getStartCode(int code, String uid);
    //    解除关注
    void getRemoveStartCode(int code, String uid);

    void CompleteDelBlackList(List<BlackList> blackLists, int code,String hituid);

    void CompleteAddBlackList(List<BlackList> blackLists,String hituid);
}

package tw.chiae.inlive.presentation.ui.room;

import tw.chiae.inlive.data.bean.room.RoomAdminInfo;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;

import java.util.List;

/**
 * Created by Administrator on 2016/7/14 0014.
 */
public interface ShowManageInterface  extends BaseUiInterface {
//    下拉刷新天假的数据
    void showEmptyResult(List<RoomAdminInfo> list);

    void successAdmin();
    void requestOver();
}

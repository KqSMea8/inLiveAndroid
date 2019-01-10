package tw.chiae.inlive.presentation.ui.main.me;

import tw.chiae.inlive.data.bean.me.PlayBackInfo;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.domain.BlackList;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;

import java.util.List;

/**
 * Created by huanzhang on 2016/4/15.
 */
public interface IMe extends BaseUiInterface {
    void showInfo(UserInfo info);

    //    得到回播列表
    void getPlayLists(List<PlayBackInfo> playBackList);

    //    得到回播的url
    void getPlayUrl(String url);

    //    啦黑
    void getHitCode(int code);
    // 解除拉黑
    void getRemoveHitCode(int code);
//    关注
    void getStartCode(int code);
//    解除关注
    void getRemoveStartCode(int code);

    /**
     * 得到私密类型
     */
    void showPrivateLimit(PrivateLimitBean bean);

    /**
     * 得到许可进入直播间
     */
    void startGoPlayFragment();

    void FailDelBlackList(String blackUid);

    void CompleteDelBlackList(List<BlackList> blackUid,int code);

    void CompleteAddBlackList(List<BlackList> blackUserId);

    void FailAddBlackList(String blackUserId);
}

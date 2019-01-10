package tw.chiae.inlive.presentation.ui.room.player;

import tw.chiae.inlive.data.bean.gift.Gift;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;

import java.util.List;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public interface PlayerUiInterface extends BaseUiInterface {
    void showGiftList(List<Gift> giftList);

    /**
     * @param playbackUrl 播放地址
     */
    void onPlaybackReady(String playbackUrl);

    //    关注
    void getStartCode(int code);
    //    解除关注
    void getRemoveStartCode(int code);

    void showUserInfo(UserInfo userInfo);


    void onMyPushReady(String address);

    /**
     * 刷新餘額
     * @param coinbalance
     */
    void upDataLoginBalance(String coinbalance);

}

package tw.chiae.inlive.presentation.ui.room.gift;

import android.support.annotation.NonNull;

import tw.chiae.inlive.data.bean.gift.SendGiftAction;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public interface IAnimController {
    /**
     * 提交新的送礼动画。
     */
    void enqueue(@NonNull SendGiftAction action);

    /**
     * 停止当前正在播放的动画并移除所有的动画。
     */
    void removeAll();

    /**
     * 动画播放完成后调用该方法。
     */
    void onPlayerAvailable();
}

package tw.chiae.inlive.presentation.ui.room.create;

/**
 * Created by Administrator on 2016/12/13 0013.
 */

public interface PrivateTypeCommit {

    /**
     * 恢复公开----取消私密
     */
    void recoveryCommit();

    /**
     * 拿到数据
     */
    void privateStringSet(String str,int type);
}

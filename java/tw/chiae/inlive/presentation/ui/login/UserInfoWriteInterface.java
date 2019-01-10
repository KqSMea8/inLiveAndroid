package tw.chiae.inlive.presentation.ui.login;

import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public interface UserInfoWriteInterface extends BaseUiInterface {
    void onProfileWriteSuccess();
    void onProfileChangeSuccess();
    void saveNickNameSuccess();
}

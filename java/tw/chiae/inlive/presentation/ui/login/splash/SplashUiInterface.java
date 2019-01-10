package tw.chiae.inlive.presentation.ui.login.splash;

import java.util.List;

import tw.chiae.inlive.data.bean.Banner;
import tw.chiae.inlive.data.repository.ParamsRemoteResponse;
import tw.chiae.inlive.data.repository.ServerEventResponse;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public interface SplashUiInterface extends BaseUiInterface {

    /**
     * 自动登录失效时会调用。
     */
    void startLoginSelectActivity();

    /**
     * 自动登录成功时会调用。
     */
    void startMainActivity();

    void onResponseServerEvent(ServerEventResponse<String> response);

    void storeBannerImg(List<Banner> banners);

    void CompleteOfficialList();

    void showLoadingText(String s);

    void CompleteDownloadBanner(List<String> paths);

    void isModifyParams();

    void showMsg(String str);

    void initPresenterComplete();

    void CompleteMainAccountList();

    void failLogin();

    void CompleteBlackList();
}

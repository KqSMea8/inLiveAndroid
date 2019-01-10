package tw.chiae.inlive.presentation.ui.login;

import java.util.List;

import tw.chiae.inlive.data.bean.Banner;
import tw.chiae.inlive.data.repository.ServerEventResponse;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public interface LoginUiInterface extends BaseUiInterface {
    void startActivityAndFinishOthers();

    void smsSendsSccess(String s);

    void onResponseServerEvent(ServerEventResponse<String> response);

    void storeBannerImg(List<Banner> banners);

    void CompleteOfficialList();

    void CompleteDownloadBanner(List<String> paths);
}

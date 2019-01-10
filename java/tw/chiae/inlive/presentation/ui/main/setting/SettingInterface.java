package tw.chiae.inlive.presentation.ui.main.setting;

import tw.chiae.inlive.data.bean.UpDataBean;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;

/**
 * Created by Administrator on 2016/12/1 0001.
 */

public interface SettingInterface extends BaseUiInterface{
    void getMyAddress(String address);

    void upLoadMyRecommen(int code);

    void getNewAppVersion(UpDataBean upData);
}

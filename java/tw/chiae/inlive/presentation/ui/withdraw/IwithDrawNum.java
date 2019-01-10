package tw.chiae.inlive.presentation.ui.withdraw;

import tw.chiae.inlive.data.bean.transaction.WithDrawRespose;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;

/**
 * Created by huanzhang on 2016/5/13.
 */
public interface IwithDrawNum extends BaseUiInterface{
    void commitSuccess(WithDrawRespose respose);
}

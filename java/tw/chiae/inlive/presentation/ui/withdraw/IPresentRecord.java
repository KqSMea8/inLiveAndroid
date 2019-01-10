package tw.chiae.inlive.presentation.ui.withdraw;

import tw.chiae.inlive.data.bean.transaction.PresentRecordItem;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;

import java.util.List;

/**
 * Created by huanzhang on 2016/5/13.
 */
public interface IPresentRecord extends BaseUiInterface{
    void showList(List<PresentRecordItem> list);
}

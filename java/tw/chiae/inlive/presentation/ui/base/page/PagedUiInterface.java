package tw.chiae.inlive.presentation.ui.base.page;

import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;

import java.util.List;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public interface PagedUiInterface<E> extends BaseUiInterface {
    /**
     * 显示指定的数据列表。
     */
    void showData(List<E> list);

    /**
     * 追加数据到现有列表。
     */
    void appendData(List<E> list);

}

package tw.chiae.inlive.presentation.ui.base;

import android.app.Dialog;

import java.util.List;

import tw.chiae.inlive.domain.BlackList;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public interface BaseUiInterface {
    /**
     * 数据请求发生网络异常时调用。
     */
    void showNetworkException();

    /**
     * 发生Error但又不是网络异常时调用。
     */
    void showUnknownException();

    /**
     * 数据成功返回但不是预期值时调用。
     */
    void showDataException(String msg);

    /**
     * 显示加载完成的UI(e.g. 复位 Ultra-Ptr头部或尾部)
     */
    void showLoadingComplete();

    /**
     * 显示进度条对话框。
     */
    Dialog showLoadingDialog();

    /**
     * 关闭进度条对话框。
     */
    void dismissLoadingDialog();

    void setCoinData(int currencyItemSum);




 /*   void FailDelBlackList(String blackUid);

    void CompleteDelBlackList(List<BlackList> blackUid);

    void CompleteAddBlackList(List<BlackList> blackUserId);

    void FailAddBlackList(String blackUserId);*/
}
package tw.chiae.inlive.presentation.ui.main.me.transaction;

import tw.chiae.inlive.data.bean.transaction.RechargeMapItem;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;

import java.util.List;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public interface RechargeInterface extends BaseUiInterface {

    void showBalance(double balance);

    void showRechargeList(List<RechargeMapItem> list);

    /**
     * 充值成功
     */
    void showRechargeSuccess();

    /**
     * 充值订单为处理中的状态，即未成功也未失败，需要稍后以服务器状态为准
     */
    void showRechargeProcessing();

    /**
     * 充值失败
     */
    void showRechargeFailed(String status, String msg);

    /**
     * 用户取消支付
     */
    void showPayCancelled();
}

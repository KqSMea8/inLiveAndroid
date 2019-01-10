package tw.chiae.inlive.presentation.ui.main.currency;

import tw.chiae.inlive.data.bean.CurrencyRankItem;
import tw.chiae.inlive.data.bean.PageBean;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;

import java.util.List;

/**
 * Created by huanzhang on 2016/5/9.
 */
public interface ICurrency extends BaseUiInterface {
   void showData(PageBean<CurrencyRankItem> data);
   void showRefreshData(PageBean<CurrencyRankItem> data, int currentPage);

   void refreshEnd();

   void showFirstData(PageBean<CurrencyRankItem> currencyItem);

   void showRefreshPageData(PageBean<CurrencyRankItem> currencyItem, int pageIndex);
}

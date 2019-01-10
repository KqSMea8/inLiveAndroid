package tw.chiae.inlive.presentation.ui.main.currency;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.CurrencyRankItem;
import tw.chiae.inlive.data.bean.PageBean;
import tw.chiae.inlive.domain.CurrencyManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by huanzhang on 2016/5/9.
 */
public class CurrencyPresenter extends BasePresenter{
    private ICurrency iCurrency;
    private CurrencyManager mManager;
    private int page = 0;
    private int currentPage = 1;
    private int CurrencyItemCount = 0;
    private int CurrencyItemSum = 0 ;
    private Long TotalDataItemsSize = 0l;
    private String Uid;
    ArrayList<PageBean<CurrencyRankItem>> TotalList;
    private int totalpage = 1;


    protected CurrencyPresenter(ICurrency uiInterface) {
        super(uiInterface);
        iCurrency = uiInterface;
        mManager = new CurrencyManager();
    }

    public void getFirstData(final String uid){
        currentPage = 1;
        final Subscription subscription = mManager.getCurrentByUser(uid,currentPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<PageBean<CurrencyRankItem>>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<PageBean<CurrencyRankItem>> response) {
                        PageBean<CurrencyRankItem> CurrencyItem = response.getData();
                        iCurrency.showFirstData(CurrencyItem);
                    }
                });
        addSubscription(subscription);
    }

    int Coin = 0;
    int MaxPage = 0;
    public void getCoinTotal(final String uid, final int Page) {
        if(Page == 1) {
            Coin = 0; //初始
            MaxPage = 0;
        }
        final Subscription subscription = mManager.getCurrentByUser(uid,Page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<PageBean<CurrencyRankItem>>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<PageBean<CurrencyRankItem>> response) {
                        PageBean<CurrencyRankItem> CurrencyItem = response.getData();
                        if(CurrencyItem!=null && CurrencyItem.getList().size()==0){
                            iCurrency.setCoinData(Coin);
                            Log.i("RayTest","MaxPage:"+MaxPage);
                        }else{
                            Coin+=CurrencyItem.getSum_coin();
                            getCoinTotal(uid,Page+1);
                            MaxPage++;
                        }
                    }
                });
        addSubscription(subscription);
    }

    public void LoadMorePage(final String uid){
        if(currentPage<MaxPage) {
            currentPage++;
            Log.i("RayTest","Load Page "+currentPage);
            final Subscription subscription = mManager.getCurrentByUser(uid,currentPage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseObserver<BaseResponse<PageBean<CurrencyRankItem>>>(getUiInterface()) {
                        @Override
                        public void onSuccess(BaseResponse<PageBean<CurrencyRankItem>> response) {
                            PageBean<CurrencyRankItem> CurrencyItem = response.getData();
                            iCurrency.showData(CurrencyItem);
                        }
                    });
            addSubscription(subscription);
        }else{
            Log.i("RayTest","refreshEnd "+currentPage);
            iCurrency.refreshEnd();

        }


    }

    public void getRefreshData(String uid) {
        final Subscription subscription = mManager.getCurrentByUser(uid,1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<PageBean<CurrencyRankItem>>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<PageBean<CurrencyRankItem>> response) {
                        PageBean<CurrencyRankItem> CurrencyItem = response.getData();
                        iCurrency.showRefreshData(CurrencyItem,1);
                    }
                });
        addSubscription(subscription);
    }

    public void getRefreshPage(final String uid, final int PageIndex) {
        if(PageIndex<=currentPage) {
            Log.i("RayTest","Load PageIndex "+PageIndex);
            final Subscription subscription = mManager.getCurrentByUser(uid,PageIndex)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseObserver<BaseResponse<PageBean<CurrencyRankItem>>>(getUiInterface()) {
                        @Override
                        public void onSuccess(BaseResponse<PageBean<CurrencyRankItem>> response) {
                            PageBean<CurrencyRankItem> CurrencyItem = response.getData();
                            iCurrency.showRefreshPageData(CurrencyItem,PageIndex);

                        }
                    });
            addSubscription(subscription);
        }else{
            Log.i("RayTest","PageIndex "+PageIndex + " currentPage :"+currentPage);
            iCurrency.refreshEnd();

        }
    }
}

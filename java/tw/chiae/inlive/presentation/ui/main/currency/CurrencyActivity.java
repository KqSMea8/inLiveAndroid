package tw.chiae.inlive.presentation.ui.main.currency;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.magiepooh.recycleritemdecoration.ItemDecorations;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.CurrencyRankItem;
import tw.chiae.inlive.data.bean.PageBean;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.base.ptr.BasePtr;

import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class CurrencyActivity extends BaseActivity implements ICurrency{
    private PtrFrameLayout ptrFrameLayout;
    private CurrencyPresenter mPresenter;
    public static String KEY_UID = "uid",KEY_COIN="coin";
//    整整的个数
    private String uid ,coin;
//    个数显示
    private TextView mCoinTv;
    public static Intent createIntent(Context context,String uid) {
        Intent intent = new Intent(context, CurrencyActivity.class);
        intent.putExtra(KEY_UID,uid);
        return intent;
    }

    private RecyclerView mList;
    private CurrencyListAdapter mAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        //ptrFrameLayout.autoRefresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_currency;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        mList = $(R.id.currency_list);
        mCoinTv = $(R.id.item_xb_txt_total_coin);
        ptrFrameLayout = $(R.id.curency_list_ptr);
    }

    @Override
    protected void init() {
        uid = getIntent().getStringExtra(KEY_UID);
        coin = getIntent().getStringExtra(KEY_COIN);
//        这里默认设置成.0  穿过来的那个coin没***用呢目前
        mCoinTv.setText(getString(R.string.room_contributors_contribution_all,0+"",
                " IN幣"));
        TextView tvTitle = $(R.id.tv_toolbar_title);
        tvTitle.setText(getString(R.string.coin_rank," "));
        mPresenter = new CurrencyPresenter(this);

        BasePtr.setPagedPtrStyle(ptrFrameLayout);
        mAdapter = new CurrencyListAdapter();
        mList.setAdapter(mAdapter);
        //mPresenter.getCoinTotal(uid);
        //mPresenter.getRefreshData(uid);
        //mPresenter.getData(uid);
        mPresenter.getFirstData(uid);
        mList.setLayoutManager(new LinearLayoutManager(this));
        mList.addItemDecoration(ItemDecorations.vertical(this)
                .type(CurrencyListAdapter.TYPE_NO_ONE, R.drawable.divider_decoration_transparent_h1)
                .create());
        mList.addItemDecoration(ItemDecorations.vertical(this)
                .type(CurrencyListAdapter.TYPE_NO_TWO, R.drawable.divider_decoration_transparent_h1)
                .create());
        mList.addItemDecoration(ItemDecorations.vertical(this)
                .type(CurrencyListAdapter.TYPE_NORMAL, R.drawable.divider_decoration_transparent_h1)
                .create());
        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler2() {

            @Override
            public boolean checkCanDoLoadMore(PtrFrameLayout frame, View content, View footer) {
                return super.checkCanDoLoadMore(frame, mList, footer);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return super.checkCanDoRefresh(frame, mList, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                Log.i("RayTest","onRefreshBegin");
                //mPresenter.getData(uid);
                //mPresenter.getCoinTotal(uid);
                mPresenter.getRefreshData(uid);
            }

            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                Log.i("RayTest","onLoadMoreBegin");
                mPresenter.LoadMorePage(uid);
                //mPresenter.getCoinTotal(uid);
                //mPresenter.LoadMoreData(uid);
            }
        });


    }

    @Override
    public void showLoadingComplete() {
        super.showLoadingComplete();
        ptrFrameLayout.refreshComplete();
    }

    @Override
    public void setCoinData(int currencyItemSum) {
        mCoinTv.setText(getString(R.string.room_contributors_contribution_all,currencyItemSum+"",
                " IN幣"));
    }

    @Override
    public void showData(PageBean<CurrencyRankItem> data) {
      if(data.getList()!=null&&data.getList().size()!=0){
          mAdapter.appendData(data.getList());
      }
    }

    @Override
    public void showRefreshData(PageBean<CurrencyRankItem> data, int currentPage) {
        if(data.getList()!=null&&data.getList().size()!=0){
            mAdapter.update(data.getList());
            mPresenter.getCoinTotal(uid,1);
            mPresenter.getRefreshPage(uid,currentPage+1);
        }
    }


    @Override
    public void refreshEnd() {
        showLoadingComplete();
    }

    @Override
    public void showFirstData(PageBean<CurrencyRankItem> data) {
        if(data.getList()!=null&&data.getList().size()!=0){
            mAdapter.update(data.getList());
            mPresenter.getCoinTotal(uid,1);
        }
    }

    @Override
    public void showRefreshPageData(PageBean<CurrencyRankItem> data, int pageIndex) {
        if(data.getList()!=null&&data.getList().size()!=0){
            mAdapter.appendData(data.getList());
            mPresenter.getRefreshPage(uid,pageIndex+1);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribeTasks();
    }
}

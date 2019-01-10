package tw.chiae.inlive.presentation.ui.withdraw;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.magiepooh.recycleritemdecoration.ItemDecorations;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.transaction.PresentRecordItem;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.base.ptr.BasePtr;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerAdapter;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerHolder;

import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class PresentRecordActivity extends BaseActivity implements IPresentRecord{
    private PtrFrameLayout ptrFrameLayout;
    private RecyclerView recyclerView;
    private View viewEmpty;
    private PresentRecordPresenter mPresenter;
    private RecordAdapter adapter;

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, PresentRecordActivity.class);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_present_record;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        recyclerView = $(R.id.present_record_recycler);
        ptrFrameLayout = $(R.id.present_record_ptr);
        viewEmpty = $(R.id.present_record_tv_empty);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ptrFrameLayout.autoRefresh();
    }

    @Override
    protected void init() {
        mPresenter = new PresentRecordPresenter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(ItemDecorations.vertical(this)
                .type(0, R.drawable.divider_decoration_transparent_h1)
                .create());

//        BasePtr.setLoadMoreOnlyStyle(ptrFrameLayout);
        BasePtr.setRefreshOnlyStyle(ptrFrameLayout);
        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return checkContentCanBePulledDown(frame, recyclerView, header);
            }
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPresenter.getPresentRecord();
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

    }

    @Override
    public void showList(List<PresentRecordItem> list) {
        if(list == null || list.size()==0){
            viewEmpty.setVisibility(View.VISIBLE);
            return;
        }
        viewEmpty.setVisibility(View.INVISIBLE);
        if (adapter == null) {
            adapter = new RecordAdapter(list);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setDataList(list);
        }
    }
    private class RecordAdapter extends SimpleRecyclerAdapter<PresentRecordItem,
            SearchResultHolder> {
        public RecordAdapter(List<PresentRecordItem> anchorSummaries) {
            super(anchorSummaries);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_present_record;
        }

        @NonNull
        @Override
        protected SearchResultHolder createHolder(View view) {
            return new SearchResultHolder(view);
        }
    }
    private class SearchResultHolder extends SimpleRecyclerHolder<PresentRecordItem> {
        private TextView mCash,mTime,mStatus;
        public SearchResultHolder(View itemView) {
            super(itemView);
            mCash = $(itemView,R.id.item_present_record_cash);
            mTime = $(itemView,R.id.item_present_record_time);
            mStatus = $(itemView,R.id.item_present_record_status);
        }

        @Override
        public void displayData(PresentRecordItem data) {
            mCash.setText(data.getCash());
            mTime.setText(data.getTime());
            int status = Integer.parseInt(data.getConfirmed());
            mStatus.setText(status==1?getString(R.string.income_complete):getString(R.string.income_dispose));
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribeTasks();
    }
}

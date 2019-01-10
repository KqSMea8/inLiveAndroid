package tw.chiae.inlive.presentation.ui.main.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.magiepooh.recycleritemdecoration.ItemDecorations;

import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.room.HitList;
import tw.chiae.inlive.domain.BlackList;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.base.ptr.BasePtr;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerAdapter;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerHolder;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;

import java.util.ArrayList;
import java.util.List;

public class BlacklistActivity extends BaseActivity implements BlacklistInterface, CreateViewDialogFragment.dialogCallback {

    private RecyclerView recyclerHitList;
    private BlacklistAdapter adapter;
    private BlacklistPresenter presenter;
    private PtrFrameLayout ptrFrameLayout;
    private ImageView container;
    private CreateViewDialogFragment dialogFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_blacklist;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {

        presenter=new BlacklistPresenter(this);
        dialogFragment = CreateViewDialogFragment.newInstance();
        dialogFragment.setDialogCallback(this);

        container = $(R.id.blacklist_container);
        recyclerHitList = $(R.id.hitlist_recycler);
        recyclerHitList.setLayoutManager(new LinearLayoutManager(this));
        recyclerHitList.addItemDecoration(ItemDecorations.vertical(this)
                .type(0, R.drawable.divider_decoration_transparent_h1)
                .create());

        ptrFrameLayout = $(R.id.ptr_blacklist);

        BasePtr.setRefreshOnlyStyle(ptrFrameLayout);

        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler2() {

            @Override
            public boolean checkCanDoLoadMore(PtrFrameLayout frame, View content, View footer) {
                return super.checkCanDoLoadMore(frame, recyclerHitList, footer);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return super.checkCanDoRefresh(frame, recyclerHitList, header);
            }

            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                //presenter.queryNextPage(mUid, mKey);
                Log.i("RayTest","onLoadMoreBegin");
                presenter.updateblackList();
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                //presenter.queryFirstPage(mUid, mKey);
                Log.i("RayTest","onRefreshBegin");
                presenter.updateblackList();
            }
        });


    }

    @Override
    protected void init() {
        //presenter.updateInform(LocalDataManager.getInstance().getLoginInfo().getToken());
        presenter.updateblackList();
    }

    @Override
    public void showResult(List<HitList> list) {

    }

    @Override
    public void CompleteBlackList() {
        if(ptrFrameLayout!=null && ptrFrameLayout.isRefreshing())
            ptrFrameLayout.refreshComplete();
        List<BlackList> blacklists = LocalDataManager.getInstance().getmBlackList();
        //blacklists = getTestList(blacklists);
        if (adapter == null) {
            adapter = new BlacklistAdapter(blacklists);
            recyclerHitList.setAdapter(adapter);
        } else {
            adapter.setDataList(blacklists);
        }
    }

    private List<BlackList> getTestList(List<BlackList> blacklists) {
        List<BlackList> newList = new ArrayList<>();
        int index = 0;
        for(BlackList old: blacklists){
            old.setBlack_id(old.getBlack_id()+"_"+index);
            newList.add(old);
            index++;
        }
        return newList;
    }

    @Override
    public void CompleteDelBlackList(List<BlackList> blackLists) {
        if(dialogFragment.getDialog()!=null){
            if(dialogFragment.getDialog().isShowing())
                dialogFragment.dismiss();
        }
        CompleteBlackList();
    }

    @Override
    public void FailDelBlackList() {

    }


    @Override
    public void setCoinData(int currencyItemSum) {

    }

    public static Intent createIntent(Context context, String id) {
        Intent i = new Intent(context, BlacklistActivity.class);
        return  i;
    }

    @Override
    public void onOKDialogcheck(Bundle bundle) {
        String mBlackUserID = bundle.getString("blackUserId");
        presenter.delBlackList(mBlackUserID);
    }

    @Override
    public void onCancelDialogcheck(Bundle mArgs) {

    }

    private class BlacklistAdapter extends SimpleRecyclerAdapter<BlackList,
            BlacklistHolder> {
        public BlacklistAdapter(List<BlackList> hitLists) {
            super(hitLists);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_hitlist_result;
        }

        @NonNull
        @Override
        protected BlacklistHolder createHolder(View view) {
            return new BlacklistHolder(view);
        }
    }

    private class BlacklistHolder extends SimpleRecyclerHolder<BlackList> {

        private Button btnDelBlackList;
        private TextView tvNickname,tvUsername,tvId,tvCurroomnum;
        private ImageView imgGender, imgLevel;
        private ImageButton imgbtnChange;

        public BlacklistHolder(View itemView) {
            super(itemView);
            tvNickname = (TextView) itemView.findViewById(R.id.item_hitlist_nickname);
            //tvUsername = (TextView) itemView.findViewById(R.id.item_hitlist_username);
            tvId = (TextView) itemView.findViewById(R.id.item_hitlist_id);
            tvCurroomnum = (TextView) itemView.findViewById(R.id.item_hitlist_curroomnum);
            //imgGender = (ImageView) itemView.findViewById(R.id.item_hitlist_gender);
            //imgLevel = (ImageView) itemView.findViewById(R.id.item_hitlist_img_level);
            imgbtnChange = (ImageButton) itemView.findViewById(R.id.item_hitlist_change);
            btnDelBlackList = (Button) itemView.findViewById(R.id.bt_del_blacklist_user);
        }

        @Override
        public void displayData(BlackList data) {
            tvId.setText("inLive ID");
            tvCurroomnum.setText(data.getBlack_id());
            tvNickname.setText(data.getNickname());
            btnDelBlackList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   dialogFragment.showCheckDelDialog(getSupportFragmentManager(),tvCurroomnum.getText().toString(), CreateViewDialogFragment.TYPE_CANCEL_BLACKLIST);

                }
            });

            //tvUsername.setText(data.getBlack_id());

//            imgLevel.setImageResource(PicUtil.getLevelImageId(itemView.getContext(), Integer.parseInt(data.getEmceelecel())));
        }
    }


}

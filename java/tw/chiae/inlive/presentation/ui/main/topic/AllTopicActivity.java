package tw.chiae.inlive.presentation.ui.main.topic;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.AnchorSummary;
import tw.chiae.inlive.data.bean.HotAnchorSummary;
import tw.chiae.inlive.data.bean.ThemBean;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerAdapter;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerHolder;
import tw.chiae.inlive.presentation.ui.main.index.RecommendInterface;
import tw.chiae.inlive.presentation.ui.main.index.RecommendPresenter;
import tw.chiae.inlive.util.Const;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.srain.cube.views.ptr.PtrFrameLayout;
import rx.functions.Action1;

public class AllTopicActivity extends BaseActivity implements RecommendInterface {

    private RecyclerView recyclerView;
    private RecommendPresenter presenter;
    private RelativeLayout rlEmptyLive;
    private String topic,topictext;
    private TextView topicTitle;
    //    返回
    private ImageButton back;
    //    标题
    private EditText edtLiveTitle;
    //    话题列表
    private List<ThemBean.Topic> topicsList;
//    话题列表适配器
    private ThemAdapter adapter;
    @SuppressWarnings("unused")
    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, AllTopicActivity.class);
        return intent;
    }

    @Override
    protected void parseIntentData(Intent intent, boolean isFromNewIntent) {
        super.parseIntentData(intent, isFromNewIntent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_all_topic;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        topicsList=new ArrayList<>();
        presenter=new RecommendPresenter(this);
        recyclerView= (RecyclerView) findViewById(R.id.hot_topic_all_recycler);
        rlEmptyLive= (RelativeLayout) findViewById(R.id.followed_topic_rl_no_live_all);
        back= (ImageButton) findViewById(R.id.topic_index_imgbtn_back_all);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ThemAdapter(topicsList);
        recyclerView.setAdapter(adapter);
        presenter.getThemBean("");
    }

    @Override
    protected void init() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllTopicActivity.this.finish();
            }
        });
    }

    @Override
    public void showEmptyResult() {

    }

    @Override
    public void onThemBean(ThemBean themBean) {
        topicsList = themBean.getTopic();
        adapter.setDataList(topicsList);
    }

    @Override
    public void showData(List<AnchorSummary> list) {

    }

    @Override
    public void appendData(List<AnchorSummary> list) {

    }

    @Override
    public void showPrivateLimit(PrivateLimitBean bean) {

    }

    @Override
    public void startGoPlayFragment() {

    }

    @Override
    public void saveAnchorsInfoData(List<HotAnchorSummary> list) {

    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }


    // 话题列表适配器
    private class ThemAdapter extends SimpleRecyclerAdapter<ThemBean.Topic, RecommendHolder> {
        public ThemAdapter(List<ThemBean.Topic> anchorSummaries) {
            super(anchorSummaries);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.start_them_list_item;
        }

        @NonNull
        @Override
        protected RecommendHolder createHolder(View view) {
            return new RecommendHolder(view);
        }
    }

    private class RecommendHolder extends SimpleRecyclerHolder<ThemBean.Topic> {

        private TextView mChatTitile, mChartNumber;

        public RecommendHolder(View itemView) {
            super(itemView);
            mChatTitile = (TextView) itemView.findViewById(R.id.item_them_tv);
            mChartNumber = (TextView) itemView.findViewById(R.id.item_them_number);
        }

        @Override
        public void displayData(final ThemBean.Topic data) {
            mChatTitile.setText(data.getTopic_title());
            mChartNumber.setText(data.getTopic_num() + getString(R.string.unit_live));
            RxView.clicks(itemView)
                    .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            startActivity(NewsTopicActivity.createIntent(AllTopicActivity.this,data.getTopic_id(),data.getTopic_title()));
                        }
                    });
        }
    }
}

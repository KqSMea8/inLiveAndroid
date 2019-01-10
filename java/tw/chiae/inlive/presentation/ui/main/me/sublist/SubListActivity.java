package tw.chiae.inlive.presentation.ui.main.me.sublist;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import tw.chiae.inlive.R;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.main.me.MeStarListFragment;
import tw.chiae.inlive.presentation.ui.main.search.SearchActivity;

//个人中心关注列表页面
public class SubListActivity extends BaseActivity {
    public static int KEY_STAR = 1;
    public static int KEY_FANS = 2;
    private static final String ARG_UID = "uid";
    private static final String ARG_KEY = "key";
    private TextView mTitle;
    private ImageButton mAdd;
    private int type=1;

    public static Intent createIntent(Context context,String uid, int key) {
        Intent intent = new Intent(context, SubListActivity.class);
        intent.putExtra(ARG_UID,uid);
        intent.putExtra(ARG_KEY,key);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sub_list;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        mTitle = $(R.id.tv_toolbar_title);
        mAdd = $(R.id.sub_list_title_add);
    }

    @Override
    protected void init() {
       if(this.getIntent().getIntExtra(ARG_KEY,1) ==2){
           mTitle.setText(getResources().getString(R.string.me_fans));
           type=2;
       }else{
           mAdd.setVisibility(View.GONE);
           mAdd.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent i = SearchActivity.createIntent(SubListActivity.this);
                   startActivity(i);
               }
           });
       }

        MeStarListFragment fragment = MeStarListFragment.newInstance(getIntent().getStringExtra(ARG_UID),this.getIntent().getIntExtra(ARG_KEY,1),true);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.sub_list_content,fragment)
                .commit();
    }

    @Override
    protected void onResume() {
        if (type==2){
//            MobclickAgent.onPageStart("粉丝列表");
        }else {
//            MobclickAgent.onPageStart("关注列表");
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (type==2) {
//            MobclickAgent.onPageEnd("粉丝列表");
        }else {
//            MobclickAgent.onPageEnd("关注列表");
        }
        super.onPause();
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }
}

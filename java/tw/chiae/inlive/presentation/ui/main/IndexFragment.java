package tw.chiae.inlive.presentation.ui.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import rx.functions.Action1;
import tw.chiae.inlive.R;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseFragment;
import tw.chiae.inlive.presentation.ui.chatting.DemoActivity;
import tw.chiae.inlive.presentation.ui.main.index.CommendAnchorFragment;
import tw.chiae.inlive.presentation.ui.main.index.FollowedAnchorLiveFragment;
import tw.chiae.inlive.presentation.ui.main.index.HotAnchorFragment;
import tw.chiae.inlive.presentation.ui.main.index.RecommendAnchorFragment;
import tw.chiae.inlive.presentation.ui.main.search.SearchActivity;
import tw.chiae.inlive.util.Const;

/**
 * 首页。
 *
 * @author Muyangmin
 * @since 1.0.0
 */
public class IndexFragment extends BaseFragment {

    private ViewPager viewPager;
    private AnchorTypeAdapter adapter;
    private TabLayout tabLayout;
    private String[] pageTitles;
    private TextView indexTop;
    private FollowedAnchorLiveFragment followFragment;
    private HotAnchorFragment hotFragment;
    private RecommendAnchorFragment recommendFragment;
    private CommendAnchorFragment commendFragment;
    //    private NearbyFragment nearbyFragment;
    //private final UIHandler mUIHandler = new UIHandler(this);
    //private static final int UNREAD = 0x99999;
    //private static final int READ = 0x99998;
    private TextView newMsg;
    private List<Conversation> mDatas = new ArrayList<Conversation>();

    public static IndexFragment newInstance() {
        return new IndexFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_main_index;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mDatas = JMessageClient.getConversationList();
            for (int i = 0; i < mDatas.size(); i++) {
                Conversation conv = mDatas.get(i);
                if (conv.getUnReadMsgCnt() > 0) {
                    newMsg.setVisibility(View.GONE);
                    break;
                } else
                    newMsg.setVisibility(View.GONE);
            }
        } catch (NullPointerException e) {
        }

        if(adapter!=null) {
           adapter.getItem(viewPager.getCurrentItem()).onResume();
        }


/*        BaseActivity mActivity = (BaseActivity) getActivity();
        getView().setFitsSystemWindows(true);
        mActivity.setTaskBarColored(R.color.indexToolbar);*/
    }

    @Override
    public void onPause() {
        super.onPause();
        if(adapter!=null) {
            adapter.getItem(viewPager.getCurrentItem()).onPause();
        }
    }

    @Override
    protected void initViews(View view) {

        newMsg = $(view, R.id.new_msg);


        RxView.clicks($(view, R.id.main_index_imgbtn_search)).throttleFirst(Const
                .VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        startActivity(SearchActivity.createIntent(getActivity()));
                        getActivity().overridePendingTransition(R.anim.fragment_slide_right_in, R
                                .anim.fragment_slide_right_out);
                    }
                });

        RxView.clicks($(view, R.id.main_index_imgbtn_chat)).throttleFirst(Const
                .VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        startActivity(DemoActivity.createIntent(getActivity()));
                        getActivity().overridePendingTransition(R.anim.fragment_slide_left_in, R
                                .anim.fragment_slide_left_out);
                    }
                });

        tabLayout = $(view, R.id.main_index_tab_layout);
        viewPager = $(view, R.id.main_index_view_pager);
        indexTop = $(view, R.id.index_top_tx);

        recommendFragment = RecommendAnchorFragment.newInstance();
        recommendFragment.setRecommendShowHot(new RecommendAnchorFragment.RecommendShowHot() {
            @Override
            public void showHot() {
                viewPager.setCurrentItem(1);
            }
        });
        followFragment = FollowedAnchorLiveFragment.newInstance();
        followFragment.setFollwedShowHot(new FollowedAnchorLiveFragment.FollwedShowHot() {
            @Override
            public void showHot() {
                viewPager.setCurrentItem(1);
                Log.i("followFragment", "執行這裡沒");
            }
        });
        hotFragment = HotAnchorFragment.newInstance();
        recommendFragment = RecommendAnchorFragment.newInstance();
        recommendFragment.setRecommendShowHot(new RecommendAnchorFragment.RecommendShowHot() {
            @Override
            public void showHot() {
                viewPager.setCurrentItem(1);
            }
        });
        commendFragment = CommendAnchorFragment.newInstance();
        commendFragment.setCommendShowHot(new CommendAnchorFragment.CommendShowHot() {
            @Override
            public void showHot() {
                viewPager.setCurrentItem(1);
            }
        });
//        nearbyFragment = NearbyFragment.newInstance();
        Fragment[] fragments = new Fragment[]{followFragment,
                hotFragment,
                recommendFragment,
        };//,commendFragment
        pageTitles = new String[]{getString(R.string.index_tab_followed),
                getString(R.string.tindex_tab_hot),
                getString(R.string.index_tab_recommend),
        };//,getString(R.string.index_tab_commend)

        viewPager.removeAllViews();
        adapter = new AnchorTypeAdapter(getChildFragmentManager(), fragments, pageTitles);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(fragments.length);
        tabLayout.setupWithViewPager(viewPager);
        //默认显示热门主播
        viewPager.setCurrentItem(1);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i("RayTest","onTabSelected:"+tab.getPosition());
                changeTabSelect(tab);
                Fragment fragment = adapter.getItem(tab.getPosition());
                fragment.onResume();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.i("RayTest","onTabUnselected:"+tab.getPosition());
                changeTabNormal(tab);
                BaseFragment fragment = (BaseFragment) adapter.getItem(tab.getPosition());
                fragment.onPause();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.i("RayTest","onTabReselected:"+tab.getPosition());
                View view = tab.getCustomView();
                TextView txt_title = (TextView) view.findViewById(R.id.index_table_item_tv);
                if (txt_title.getText().toString().equals(pageTitles[1])) {
                    //startActivityForResult(IndexHotListActivity.createIntent(getActivity()), 1);
////                    hotFragment.GoTop();
                }
            }
        });
        tabLayout.setOnClickListener(mTabOnClickListener);
        setupTabIcons();
    }

    //   更新切换到的
    private void changeTabSelect(TabLayout.Tab tab) {
        View view = tab.getCustomView();
//        ImageView img_title = (ImageView) view.findViewById(R.id.index_table_item_img);
        TextView txt_title = (TextView) view.findViewById(R.id.index_table_item_tv);
        ImageView img_title = (ImageView) view.findViewById(R.id.index_table_item_img);
        txt_title.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
        txt_title.setAlpha(1.0f);
        txt_title.setTextSize(17);
        if (txt_title.getText().toString().equals(pageTitles[0])) {
//            img_title.setImageResource(R.drawable.tab_home_passed);
            viewPager.setCurrentItem(0);

        } else if (txt_title.getText().toString().equals(pageTitles[1])) {
//            img_title.setImageResource(R.drawable.tab_mine_passed);
            viewPager.setCurrentItem(1);
            img_title.setImageResource(R.drawable.index_hot_jt_press);
//        } else if (txt_title.getText().toString().equals(pageTitles[2])){
////            img_title.setImageResource(R.drawable.tab_info_passed);
//            viewPager.setCurrentItem(2);
        } else {
            viewPager.setCurrentItem(2);
        }
    }

    //    还原上一个
    private void changeTabNormal(TabLayout.Tab tab) {
        View view = tab.getCustomView();
//        ImageView img_title = (ImageView) view.findViewById(R.id.index_table_item_img);
        TextView txt_title = (TextView) view.findViewById(R.id.index_table_item_tv);
        ImageView img_title = (ImageView) view.findViewById(R.id.index_table_item_img);
        txt_title.setTextColor(getActivity().getResources().getColor(R.color.colorIcons));
        txt_title.setAlpha(0.8f);
        txt_title.setTextSize(14);
        if (txt_title.getText().toString().equals(pageTitles[1])) {
            img_title.setImageResource(R.drawable.index_hot_jt_nopress);
        }
//        if (txt_title.getText().toString().equals("One")) {
//            img_title.setImageResource(R.drawable.tab_home_normal);
//        } else if (txt_title.getText().toString().equals("Two")) {
//            img_title.setImageResource(R.drawable.tab_mine_normal);
//        } else {
//            img_title.setImageResource(R.drawable.tab_info_normal);
//        }
    }

    //  设置自定义的
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setCustomView(getTabView(0));
        tabLayout.getTabAt(1).setCustomView(getTabView(1));
        tabLayout.getTabAt(2).setCustomView(getTabView(2));
//        tabLayout.getTabAt(3).setCustomView(getTabView(3));

    }


    //    设置字和那个啥哦
    public View getTabView(int position) {
        int selectedViewPos = tabLayout.getSelectedTabPosition();

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.index_table_item_layout, null);
        TextView txt_title = (TextView) view.findViewById(R.id.index_table_item_tv);
        ImageView img_title = (ImageView) view.findViewById(R.id.index_table_item_img);
        txt_title.setText(pageTitles[position]);

        txt_title.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
        if (position == selectedViewPos) {
            //img_title.setVisibility(View.GONE);
            txt_title.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            txt_title.setTextSize(17);
            txt_title.setAlpha(1.0f);
        } else {
            txt_title.setTextColor(getActivity().getResources().getColor(R.color.colorIcons));
            txt_title.setTextSize(14);
            txt_title.setAlpha(0.8f);
        }
//        img_title.setImageResource(tabIcons[position]);

//        if (position == 0) {
//            txt_title.setTextColor(Color.YELLOW);
//            img_title.setImageResource(tabIconsPressed[position]);
//        } else {
//            txt_title.setTextColor(Color.WHITE);
//            img_title.setImageResource(tabIcons[position]);
//        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case 1:
//                data.getStringExtra("ctiy");
//                data.getStringExtra("sex");
//                toastShort(data.getStringExtra("ctiy")+data.getStringExtra("sex"));
                Log.i("RayTest","getCurrentItem: "+viewPager.getCurrentItem());
                if (hotFragment != null ) {
                    hotFragment.setSex(data.getStringExtra("sex"));
                    hotFragment.setCity(data.getStringExtra("ctiy"));
                    hotFragment.getPresenter().loadFirstPage(LocalDataManager.getInstance().getLoginInfo().getToken(), data.getStringExtra("ctiy"), data.getStringExtra("sex"));
                }
                break;
            default:
                break;
        }
    }

    private View.OnClickListener mTabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };

    public void switchToHotList() {
        viewPager.setCurrentItem(1);
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    @Override
    public void onCancelDialogcheck(Bundle mArgs) {


    }

    private static class AnchorTypeAdapter extends FragmentPagerAdapter {
        private Fragment[] fragments;
        private String[] pageTitles;

        public AnchorTypeAdapter(FragmentManager fm, Fragment[] fragments, String[] pageTitles) {
            super(fm);
            this.fragments = fragments;
            this.pageTitles = pageTitles;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitles[position];
        }
    }


    //  话题
    private PopupWindow mPopupWindow;
    private View popuView;
    private TabLayout popuTab;
    private ViewPager popuViewPager;
    //    屏幕高度 状态栏高度
    private int stateh, xunih;
    private String[] popuTabText;
    private Fragment[] popuFragments;

    private void startHostPopupWindow() {
        Log.i("RayTest","startHostPopupWindow");
        if (mPopupWindow == null) {
            getXuNiDpi();
//            getWindowKeyBrodH();
            LayoutInflater relativeLayout = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            popuView = relativeLayout.inflate(R.layout.index_pop_layout, null);
            popuTab = (TabLayout) popuView.findViewById(R.id.popu_tab_layout);
            popuViewPager = (ViewPager) popuView.findViewById(R.id.popu_view_pager);
//            初始化
            iniHostPager(popuTab, popuViewPager);
            mPopupWindow = new PopupWindow(popuView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            // 使其聚集
            mPopupWindow.setFocusable(true);
            // 设置允许在外点击消失
            mPopupWindow.setOutsideTouchable(true);
            // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.setAnimationStyle(R.style.popwin_anim_style);
            if (xunih == 0) {
                mPopupWindow.showAsDropDown(indexTop, 0, -stateh);
            } else {
                mPopupWindow.showAsDropDown(indexTop, 0, -(xunih - stateh));
            }
        } else {
            if (xunih == 0) {
                mPopupWindow.showAsDropDown(indexTop, 0, -stateh);
            } else {
                mPopupWindow.showAsDropDown(indexTop, 0, -(xunih - stateh));
            }
        }
    }

    private void iniHostPager(TabLayout popuTab, ViewPager popuViewPager) {
        popuTabText = new String[]{getString(R.string.index_popu_all)};
        popuFragments = new Fragment[]{};
        AnchorTypeAdapter adapter = new AnchorTypeAdapter(this.getChildFragmentManager(), popuFragments, popuTabText);
        popuViewPager.removeAllViews();
        popuViewPager.setAdapter(adapter);
        popuViewPager.setOffscreenPageLimit(popuFragments.length);
        popuTab.setupWithViewPager(popuViewPager);
//        //默认显示热门主播
//        popuViewPager.setCurrentItem(1);
    }

    private void getXuNiDpi() {
        int dpi = 0;
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            dpi = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        xunih = dpi - getActivity().getWindowManager().getDefaultDisplay().getHeight();
        Rect rect = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        stateh = rect.top;

        Display disp = getActivity().getWindowManager().getDefaultDisplay();
        Point outP = new Point();
        disp.getSize(outP);

    }


    /**
     * 在会话列表中接收消息
     *
     * @param event 消息事件
     */
    public void onEvent(MessageEvent event) {
        Message msg = event.getMessage();
        ConversationType convType = msg.getTargetType();
        if (convType == ConversationType.single) {
            final UserInfo userInfo = (UserInfo) msg.getTargetInfo();
            final String targetID = userInfo.getUserName();
            final Conversation conv = JMessageClient.getSingleConversation(targetID, userInfo.getAppKey());
            if (conv != null && conv.getUnReadMsgCnt() > 0) {
                //mUIHandler.sendEmptyMessage(UNREAD);
            } else if (conv != null && conv.getUnReadMsgCnt() == 0) {
                //mUIHandler.sendEmptyMessage(READ);
            }
        }
    }

    /*private static class UIHandler extends Handler {
        private final WeakReference<IndexFragment> mActivity;

        public UIHandler(IndexFragment activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            IndexFragment activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case UNREAD:
                        //activity.newMsg.setVisibility(View.VISIBLE);
                        break;
                    case READ:
                        activity.newMsg.setVisibility(View.GONE);
                        break;
                }
            }
        }
    }*/

}

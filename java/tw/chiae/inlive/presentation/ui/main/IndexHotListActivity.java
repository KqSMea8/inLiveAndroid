package tw.chiae.inlive.presentation.ui.main;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import tw.chiae.inlive.R;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.util.L;

public class IndexHotListActivity extends BaseActivity {

    private TabLayout popuTab;
    private ViewPager popuViewPager;
    private String[] popuTabText;
    private Fragment[]  popuFragments;
    private TextView mCompletTv;
    private String mCtiy,mSex;
    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, IndexHotListActivity.class);
        return intent;
    }
    @Override
    protected void parseIntentData(Intent intent, boolean isFromNewIntent) {
        super.parseIntentData(intent, isFromNewIntent);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_index_hot_list;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        popuTab = (TabLayout) findViewById(R.id.popu_tab_layout);
        popuViewPager= (ViewPager) findViewById(R.id.popu_view_pager);
        popuViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mCompletTv= (TextView) findViewById(R.id.index_hot_list_complete);
//            初始化
        iniHostPager(popuTab,popuViewPager);
    }

    @Override
    protected void init() {
        mCompletTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCtiy==null){
                    toastShort(getString(R.string.inde_popu_select_region));
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("ctiy",mCtiy);
                intent.putExtra("sex",mSex);
                setResult(1,intent);
                finish();
            }
        });
    }



    private void iniHostPager(TabLayout popuTab,ViewPager popuViewPager) {
        popuTabText=new String[]{getString(R.string.index_popu_all),getString(R.string.index_popu_wuman),getString(R.string.index_popu_man)};
        BlankFragment blankFragmentAll=new BlankFragment();
        blankFragmentAll.setSex("");
        blankFragmentAll.setmBackCtiy(new BlankFragment.BackCtiy() {
            @Override
            public void back(String ctiy, String sex) {
                mCtiy=ctiy;
                mSex=sex;
            }
        });
        BlankFragment blankFragmentWuman=new BlankFragment();
        blankFragmentWuman.setSex("1");
        blankFragmentWuman.setmBackCtiy(new BlankFragment.BackCtiy() {
            @Override
            public void back(String ctiy, String sex) {
                mCtiy=ctiy;
                mSex=sex;
            }
        });
        BlankFragment blankFragmentMan=new BlankFragment();
        blankFragmentMan.setSex("0");
        blankFragmentMan.setmBackCtiy(new BlankFragment.BackCtiy() {
            @Override
            public void back(String ctiy, String sex) {
                mCtiy=ctiy;
                mSex=sex;
            }
        });
        popuFragments=new Fragment[]{blankFragmentAll,blankFragmentWuman,blankFragmentMan};
        AnchorTypeAdapter adapter =new AnchorTypeAdapter(getSupportFragmentManager(),popuFragments,popuTabText);
        popuViewPager.removeAllViews();
        popuViewPager.setAdapter(adapter);
        popuViewPager.setOffscreenPageLimit(popuFragments.length);
        popuTab.setupWithViewPager(popuViewPager);
//        //默认显示热门主播
//        popuViewPager.setCurrentItem(1);
        popuTab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                IndexHotListActivity.this.popuViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                mCtiy=null;
                mSex=null;
                BlankFragment blankFragments= (BlankFragment) popuFragments[tab.getPosition()];
                blankFragments .finshiRecyView();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void setCoinData(int currencyItemSum) {

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
//
//
//    private Hotviewpager hotviewpager;
//    interface Hotviewpager{
//        void finsh();
//    }
//
//    public void setHotviewpager(Hotviewpager hotviewpager) {
//        this.hotviewpager = hotviewpager;
//    }
}

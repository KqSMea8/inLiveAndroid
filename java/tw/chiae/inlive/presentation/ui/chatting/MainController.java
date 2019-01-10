package tw.chiae.inlive.presentation.ui.chatting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import tw.chiae.inlive.BeautyLiveApplication;
import tw.chiae.inlive.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

public class MainController implements OnClickListener, OnPageChangeListener {

    private final static String TAG = "MainController";

    private ConversationListFragment mConvListFragment;
    private MainView mMainView;
    private DemoActivity mContext;
    private ProgressDialog mDialog;
    // 裁剪后图片的宽(X)和高(Y), 720 X 720的正方形。
    private static int OUTPUT_X = 720;
    private static int OUTPUT_Y = 720;

    public MainController(MainView mMainView, DemoActivity context) {
        this.mMainView = mMainView;
        this.mContext = context;
        setViewPager();
    }

    private void setViewPager() {
        List<Fragment> fragments = new ArrayList<Fragment>();
        // init Fragment
        mConvListFragment = new ConversationListFragment();
        fragments.add(mConvListFragment);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(mContext.getSupportFragmentManger(),
                fragments);
        mMainView.setViewPagerAdapter(viewPagerAdapter);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
    }

    /**
     * 裁剪图片
     */
    public void cropRawPhoto(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // 设置裁剪
        intent.putExtra("crop", "true");

        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", OUTPUT_X);
        intent.putExtra("outputY", OUTPUT_Y);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        mContext.startActivityForResult(intent, BeautyLiveApplication.REQUEST_CODE_CROP_PICTURE);
    }

    @Override
    public void onPageSelected(int index) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    public void sortConvList() {
        mConvListFragment.sortConvList();
    }

}

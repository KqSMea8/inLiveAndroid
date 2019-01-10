package tw.chiae.inlive.presentation.ui.main.mergefilm;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import tw.chiae.inlive.R;

/**
 * Created by Administrator on 2017/2/24 0024.
 */

public class LaodingDilog extends DialogFragment {

    /**
     * 等待layout
     */
    private RelativeLayout mLoadingLayout;
    /**
     * 動畫
     */
    private ImageView mLoadinganim;
    /**
     * 進度
     */
    private TextView mLoadingText;
    private View mLoadingView;
    private AnimationDrawable mLoadinganimDra;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Window window = getDialog().getWindow();
        mLoadingView = inflater.inflate(R.layout.record_loading_layout, null, false);//需要用android.R.id.content这个view
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//注意此处
        initView(mLoadingView);
        return mLoadingView;
    }

    private void initView(View view) {
        mLoadingLayout = (RelativeLayout) view.findViewById(R.id.loading_layout);
        mLoadinganim = (ImageView) view.findViewById(R.id.loading_anim);
        mLoadingText = (TextView) view.findViewById(R.id.loading_progress);
        mLoadinganim.setImageResource(R.drawable.recordloading_gif);
        mLoadinganimDra = (AnimationDrawable) mLoadinganim.getDrawable();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mLoadinganimDra != null)
            mLoadinganimDra.start();
    }

    public void stopLoadinganim() {
        if (mLoadinganimDra != null && mLoadinganimDra.isRunning())
            mLoadinganimDra.stop();
        dismissAllowingStateLoss();
    }

    public void setLoadingProgress(String s){
        if (mLoadingText!=null){
            if (s.trim().length()<1)
                mLoadingText.setVisibility(View.GONE);
            else {
                mLoadingText.setText(s);
                mLoadingText.setVisibility(View.VISIBLE);
            }
        }
    }
}

package tw.chiae.inlive.presentation.ui.base.ProgressHUD;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import tw.chiae.inlive.R;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;

/**
 * Created by rayyeh on 2017/3/23.
 */

public class ProgressHUDDialog extends Dialog {
    private View mView;

    private FrameLayout mCustomViewContainer;
    private FrameLayout mCustomViewContainer_Msg;
    private int mWidth, mHeight;
    private TextView tv_Msg;
    private float mCornerRadius = 10;
    private float mDimAmount;
    private View mainView;
    private LoadingDialog mBackground;

    public ProgressHUDDialog(Context context) {
        super(context);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mainView = LayoutInflater.from(getContext()).inflate(R.layout.kprogresshud_hud,null);
        setContentView(mainView);
        Window window = getWindow();
        window.setBackgroundDrawable(new ColorDrawable(0));
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.dimAmount = mDimAmount;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        setCanceledOnTouchOutside(false);
        initViews();
    }

    private void initViews() {
        mBackground = new LoadingDialog(getContext());
        mBackground.setContentView(mainView);
        //mBackgroundLayout = (LoadingDialog) findViewById(R.id.background);
        mBackground.setBaseColor(Color.GRAY);
        mBackground.setCornerRadius(mCornerRadius);
        if (mWidth != 0) {
            updateBackgroundSize();
        }

        mCustomViewContainer = (FrameLayout) mBackground.getContainer();
        mCustomViewContainer_Msg = (FrameLayout) mBackground.getContainerText();
        addViewToFrame(mView,tv_Msg);
    }

    private void addViewToFrame(View ...view) {
        if (view == null) return;
        int wrapParam = ViewGroup.LayoutParams.WRAP_CONTENT;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(wrapParam, wrapParam);
        mCustomViewContainer.addView(view[0], params);
        if(view.length>1)
            mCustomViewContainer_Msg.addView(view[1]);
    }

    private void updateBackgroundSize() {
        ViewGroup.LayoutParams params = mBackground.getMainLayout().getLayoutParams();
        params.width = DpUtilsHelper.dpToPixel(mWidth, getContext());
        params.height = DpUtilsHelper.dpToPixel(mHeight, getContext());
        mBackground.getMainLayout().setLayoutParams(params);
    }

    public void setView(View view) {
        if (view != null) {
            mView = view;
            if (isShowing()) {
                mCustomViewContainer.removeAllViews();
                addViewToFrame(view);
            }
        }
    }

    public void setMsg(String str) {
        tv_Msg = new TextView(getContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv_Msg.setTextColor(getContext().getResources().getColor(R.color.white,null));
        }else
            tv_Msg.setTextColor(getContext().getResources().getColor(R.color.white));
        int wrapParam = ViewGroup.LayoutParams.WRAP_CONTENT;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(wrapParam, wrapParam);
        tv_Msg.setLayoutParams(params);
        tv_Msg.setText(str);
    }

    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
        if (mBackground != null) {
            updateBackgroundSize();
        }
    }
}

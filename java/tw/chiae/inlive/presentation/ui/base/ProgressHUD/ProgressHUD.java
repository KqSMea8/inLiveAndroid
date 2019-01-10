package tw.chiae.inlive.presentation.ui.base.ProgressHUD;

import android.app.Dialog;
import android.app.ProgressDialog;
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

import org.w3c.dom.Text;

import tw.chiae.inlive.R;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;

/**
 * Created by rayyeh on 2017/3/22.
 */

public class ProgressHUD {


    private ProgressHUDDialog mProgressHUDDialog;
    private float mDimAmount;
    private int mWindowColor;
    private float mCornerRadius;//矩形的圆角程度
    private Context mContext;

    public ProgressHUD(Context context) {
        mContext = context;
        mProgressHUDDialog = new ProgressHUDDialog(mContext);
        mCornerRadius = 10;
        ColorDrawable dialogColor = new ColorDrawable(Color.GRAY);
        dialogColor.setAlpha(100);
        mProgressHUDDialog.getWindow().setBackgroundDrawable(dialogColor);
        View view = new LoadingCirView(mContext);
        mProgressHUDDialog.setView(view);

    }

    //    设置HUD的大小
    public ProgressHUD setSize(int width, int height) {
        mProgressHUDDialog.setSize(width, height);
        return this;
    }


    //   设置矩形的圆角程度
    public ProgressHUD setCornerRadius(float radius) {
        mCornerRadius = radius;
        return this;
    }

    //展示
    public ProgressHUD show() {
        if (!isShowing() ) {

            mProgressHUDDialog.show();
        }
        return this;
    }

    public Dialog getDialogView() {
        if(mProgressHUDDialog==null)
            return ProgressDialog.show(mContext, null, mContext.getString(R.string.loading_dialog_text), true, false);;
         return mProgressHUDDialog;
    }

    public boolean isShowing() {
        return mProgressHUDDialog != null && mProgressHUDDialog.isShowing();
    }
    //隐藏
    public void dismiss() {
        if (mProgressHUDDialog != null && mProgressHUDDialog.isShowing()) {
            mProgressHUDDialog.dismiss();
            mProgressHUDDialog.cancel();
        }

    }

    public void setText(int loading_dialog_text) {
        mProgressHUDDialog.setMsg(mContext.getResources().getString(loading_dialog_text));
    }



}

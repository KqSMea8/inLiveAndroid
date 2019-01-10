package tw.chiae.inlive.presentation.ui.login.splash;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ms_square.etsyblur.BlurConfig;
import com.ms_square.etsyblur.BlurDialogFragment;

import tw.chiae.inlive.R;
import tw.chiae.inlive.presentation.ui.main.setting.SmartAsyncPolicyHolder;

import static tw.chiae.inlive.R.string.manager;
import static tw.chiae.inlive.presentation.ui.main.setting.CreateViewDialogFragment.TYPE_ADD_FAVORITE;
import static tw.chiae.inlive.presentation.ui.main.setting.CreateViewDialogFragment.TYPE_ADD_FAVORITE_ALREADY_BLACK;

/**
 * Created by rayyeh on 2017/11/17.
 */

public class ModifyDialogFragment extends BlurDialogFragment implements View.OnClickListener {

    public static final int TYPE_DEBUG_NORMAL = 1;
    
    private static ModifyDialogFragment fragment;
    private DebugDialogCallback mCallback;
    private Bundle mBundle;
    private boolean isShow;
    private Button btn_ok;
    private Button btn_cancel;
    private TextView tv_title;
    private EditText etModifyView;
    private BlurConfig mBlurConfig;

    public static ModifyDialogFragment newInstance() {
        fragment = new ModifyDialogFragment();
        //fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.EtsyBlurDialogTheme);
        return fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle mArgs = RestoreArguments();
        if (mArgs == null)
            return null;
        int itype = mArgs.getInt("type");
        Log.i("RayTest", "onCreateDialog " + itype);
        AlertDialog dialog = null;
        switch (itype) {
            default:
                dialog = initDefaultView(mArgs);
                break;
        }


        return dialog;
    }

    public void setBlurConfig(BlurConfig config){
        Log.i("RayTetst","setBlurConfig");
        this.mBlurConfig = config;
    }
    
    @NonNull
    protected BlurConfig blurConfig() {
        Log.i("mBlurConfig","getBlurConfig2");
        if(mBlurConfig==null) {
            Log.i("mBlurConfig","getBlurConfig3");
            mBlurConfig = getBlurConfig();
        }
        return mBlurConfig;
    }
    
    public BlurConfig getBlurConfig (){
        if(mBlurConfig==null) {
            mBlurConfig = new BlurConfig.Builder().overlayColor(Color.argb(136, 255, 255, 255))  // semi-transparent white color
                    .asyncPolicy(SmartAsyncPolicyHolder.INSTANCE.smartAsyncPolicy())
                    .allowFallback(false)
                    .debug(true)
                    .build();
        }
        return mBlurConfig;
    }
    private AlertDialog initDefaultView(Bundle mArgs) {
        Log.i("RayTest","initDefaultView");
        String title = mArgs.getString("title");
        String content = mArgs.getString("content");
        int itype = mArgs.getInt("type");


        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_debug_check_window,null);
        btn_ok = (Button) v.findViewById(R.id.btn_check_del_ok);
        btn_cancel = (Button) v.findViewById(R.id.btn_check_del_cancel);
        tv_title = (TextView)v.findViewById(R.id.tv_dialog_del_check_title);
        etModifyView = (EditText)v.findViewById(R.id.et_debug_text);
        TextView tv_content = (TextView)v.findViewById(R.id.tv_dialog_del_check_content);
        tv_title.setText(title);
        tv_content.setText(content);
     /*   if(itype == TYPE_PAUSE_MEDIA_RECORD ||itype == TYPE_SPACE_NOT_ENOUGH ){
            btn_cancel.setVisibility(View.GONE);
        }*/
      
        if(title.equals(""))
            tv_title.setVisibility(View.GONE);
        else
            tv_title.setVisibility(View.VISIBLE);
        
        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
       
        AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.EtsyBlurAlertDialogTheme)
                .setView(v)
                .create();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
        dialog.getWindow().setLayout(dm.widthPixels, dm.heightPixels);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        BlurConfig mBlurConfig = new BlurConfig.Builder()
                .overlayColor(Color.argb(0, 0, 0, 0))  // semi-transparent white color
                .asyncPolicy(SmartAsyncPolicyHolder.INSTANCE.smartAsyncPolicy())
                .debug(true)
                .allowFallback(false)
                .build();
        setBlurConfig(mBlurConfig);
        return dialog;
    }

    @Override
    public void onClick(View v) {
        if(mCallback==null)
            return;
        switch (v.getId()){
            case R.id.btn_check_del_ok:
                if(etModifyView.getText().toString().isEmpty())
                    mCallback.onCancel();
                else
                    mCallback.onClickOK(etModifyView.getText().toString());
                break;
            
            case R.id.btn_check_del_cancel:
                mCallback.onCancel();
                dismiss();
                break;
            
            
        }
    }

    private Bundle RestoreArguments() {

            return mBundle;
    }

    public void setDebugCallback(DebugDialogCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        isShow = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isShow = false;
    }

    public void showDebugMsg(FragmentManager supportFragmentManager, String title, String sContent, int iType) {
        if(!isShow) {
            isShow = true;
            Bundle args = new Bundle();

            args.putString("title", title);
            args.putCharSequence("content", sContent);
            args.putInt("type", iType);
            SaveArguments(args);
            Log.i("RayTest","show type: "+iType);
            //dialogFragment.getDialog().setCanceledOnTouchOutside(false);
            show(supportFragmentManager, "dialog1");

        }
    }
    private void SaveArguments(Bundle args) {
        this.mBundle = args;
    }

    public interface DebugDialogCallback{

        void onClickOK(String s);

        void onCancel();
    }
}

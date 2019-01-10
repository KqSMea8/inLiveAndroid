package tw.chiae.inlive.presentation.ui.main.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ms_square.etsyblur.AsyncPolicy;
import com.ms_square.etsyblur.BlurConfig;
import com.ms_square.etsyblur.BlurDialogFragment;
import com.ms_square.etsyblur.SmartAsyncPolicy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import tw.chiae.inlive.R;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;
import tw.chiae.inlive.presentation.ui.main.webview.SimpleWebViewActivity;
import tw.chiae.inlive.presentation.ui.room.RoomInfoTmp;

/**
 * CreateViewDialogFragment.java
 *
 * @author Manabu-GT on 6/12/14.
 */
public class CreateViewDialogFragment extends BlurDialogFragment implements View.OnClickListener {


    private static CreateViewDialogFragment fragment;


    private String mBlackUserID
            ;
    private Button btn_ok;
    private Button btn_cancel;
    private dialogCallback mCallback;
    private View v;
    //private Context mContext;
    public static final int TYPE_ADD_FAVORITE = 0 ;
    public static final int TYPE_CANCEL_FAVORITE =1;
    public static final int TYPE_ADD_BLACKLIST=2;
    public static final int TYPE_CANCEL_BLACKLIST=3;
    public static final int TYPE_ADD_FAVORITE_ALREADY_BLACK =4;
    public static final int TYPE_FINISH_MEDIA_PALYER =10;
    public static final int TYPE_FINISH_MEDIA_RECORD =11;
    public static final int TYPE_ERROR_NETWORK = 12;
    public static final int TYPE_PAUSE_MEDIA_RECORD =13;
    public static final int TYPE_SPACE_NOT_ENOUGH = 14;
    public static final int TYPE_CHECK_WIFI = 15;
    public static final int TYPE_MV_DOWNLOAD_ERROR = 16;
    public static final int TYPE_SHOW_ERROR = 17;
    public static final int TYPE_RECORD_MEDIA = 18;
    public static final int TYPE_DEL_RECORD = 19;
    public static final int TYPE_HELPER_WEB =20;
    public static final int TYPE_CHECK_SEND = 21;

    public static final int TYPE_SHOW_UPDATE =22;
    public static final int TYPE_NEED_FOUCSE_UPDATE =23;
    private BlurConfig mBlurConfig;
    private Bundle mBundle;
    //private boolean isShow = false;
    private FragmentManager mManager = null;
    private TextView tv_title;
    private WebView mWebView;

    public static CreateViewDialogFragment newInstance() {
        fragment = new CreateViewDialogFragment();
        //fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.EtsyBlurDialogTheme);
        return fragment;
    }

    public void setBlurConfig(BlurConfig config){
        Log.i("RayTetst","setBlurConfig");
        this.mBlurConfig = config;
    }

    public BlurConfig getBlurConfig (){
        if(mBlurConfig==null) {
            mBlurConfig = new BlurConfig.Builder().overlayColor(Color.argb(136, 255, 255, 255))  // semi-transparent white color
                    .asyncPolicy(SmartAsyncPolicyHolder.INSTANCE.smartAsyncPolicy())
                    .debug(true)
                    .build();
        }
        return mBlurConfig;
    }
    // implement either onCreateView or onCreateDialog

   /* public void setContext(Context context){
        this.mContext = context;
    }*/

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle mArgs = RestoreArguments();
        if(mArgs==null)
            return null;
        int itype = mArgs.getInt("type");
        Log.i("RayTest","onCreateDialog "+itype);
        AlertDialog dialog = null;
        switch (itype){
            case TYPE_ADD_FAVORITE:
            case TYPE_CANCEL_FAVORITE:
            case TYPE_ADD_BLACKLIST:
            case TYPE_CANCEL_BLACKLIST:
            case TYPE_ADD_FAVORITE_ALREADY_BLACK:
            case TYPE_FINISH_MEDIA_PALYER :
            case TYPE_FINISH_MEDIA_RECORD :
            case TYPE_ERROR_NETWORK :
            case TYPE_SPACE_NOT_ENOUGH:
            case TYPE_PAUSE_MEDIA_RECORD :
            case TYPE_CHECK_WIFI:
            case TYPE_MV_DOWNLOAD_ERROR:
            case TYPE_SHOW_ERROR:
            case TYPE_RECORD_MEDIA:
            case TYPE_CHECK_SEND:
            case TYPE_DEL_RECORD:
            case TYPE_SHOW_UPDATE:
            case TYPE_NEED_FOUCSE_UPDATE:
                dialog = initDefaultView(mArgs);
                break;

            case TYPE_HELPER_WEB:
                dialog = initWebView(mArgs);
                break;
            default:
                dialog = initDefaultView(mArgs);
                break;
        }


        return dialog;

        /*}else
            return super.onCreateDialog(savedInstanceState);*/
    }

    private AlertDialog initWebView(Bundle mArgs) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_web_window,null);
        String mUrl = mArgs.getString("Url");
        ImageView iv_close = (ImageView) v.findViewById(R.id.iv_web_close);
        mWebView = (WebView) v.findViewById(R.id.simple_webview);
        AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.EtsyBlurAlertDialogTheme)
                .setView(v)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoomInfoTmp.isDialogShow=false;
                dismiss();
            }
        });
        if (!TextUtils.isEmpty(mUrl)) {
            mWebView.setWebChromeClient(new SimpleWebChromeClient());

            mWebView.setWebViewClient(new SimpleWebViewClient(mUrl,dialog));
            mWebView.setDownloadListener(new MyWebViewDownLoadListener());
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setLoadWithOverviewMode(true);
            mWebView.getSettings().setUseWideViewPort(true);

            if(mUrl.contains("http")||mUrl.contains("https")){

            }else{
                mUrl = SourceFactory.wrapPath(mUrl);
            }
            //
            Log.i("RayTest","Load Url CreateView :"+mUrl+"  "+mWebView.getScaleX());
            mWebView.loadUrl(mUrl);
        }
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }

    private AlertDialog initDefaultView(Bundle mArgs) {
        Log.i("RayTest","initDefaultView");
        String title = mArgs.getString("title");
        String content = mArgs.getString("content");
        int itype = mArgs.getInt("type");
        boolean needPositiveButton = mArgs.getBoolean("needPositiveButton");

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_del_check_window,null);
        btn_ok = (Button) v.findViewById(R.id.btn_check_del_ok);
        btn_cancel = (Button) v.findViewById(R.id.btn_check_del_cancel);
        tv_title = (TextView)v.findViewById(R.id.tv_dialog_del_check_title);
        TextView tv_content = (TextView)v.findViewById(R.id.tv_dialog_del_check_content);
        tv_title.setText(title);
        tv_content.setText(content);
     /*   if(itype == TYPE_PAUSE_MEDIA_RECORD ||itype == TYPE_SPACE_NOT_ENOUGH ){
            btn_cancel.setVisibility(View.GONE);
        }*/
        if(!needPositiveButton)
            btn_cancel.setVisibility(View.GONE);
        else
            btn_cancel.setVisibility(View.VISIBLE);
        if(title.equals(""))
            tv_title.setVisibility(View.GONE);
        else
            tv_title.setVisibility(View.VISIBLE);
        if(itype<=TYPE_ADD_FAVORITE_ALREADY_BLACK){
            String myValue = mArgs.getString("blackUserId");
        }
        if(itype==TYPE_NEED_FOUCSE_UPDATE ||itype==TYPE_SHOW_UPDATE){
            setTextGravity(Gravity.LEFT);
            btn_ok.setText("更新");
            btn_cancel.setText("下次再說");
        }
        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        Log.i("RayTest","dialog build");
        AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.EtsyBlurAlertDialogTheme)
                .setView(v)
                .create();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
        dialog.getWindow().setLayout(dm.widthPixels, dm.heightPixels);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private Bundle RestoreArguments() {
        return mBundle;
    }
/*    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.dialog_del_check_window, container, false);
        Log.i("RayTest","onCreateView dialog!"+mBlackUserID);
        btn_ok = (Button) v.findViewById(R.id.btn_check_del_ok);
        btn_cancel = (Button) v.findViewById(R.id.btn_check_del_cancel);
        btn_cancel.setOnClickListener(this);
        *//*ListView listView = (ListView) v.findViewById(R.id.dialog_content);
        listView.setAdapter(new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                new String[]{
                        "Enable History",
                        "Clear History",
                        "Search History",
                        "Select Currency",
                        "About"
                }
        ));*//*
        return v;
    }*/

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.i("RayTest","onDismiss");
        RoomInfoTmp.isDialogShow = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("RayTest","onDetach");
        RoomInfoTmp.isDialogShow = false;
    }

    @NonNull
    protected BlurConfig blurConfig() {
        Log.i("RayTetst","blurConfig");
        if(mBlurConfig==null)
            mBlurConfig = getBlurConfig();
        return mBlurConfig;
    }


    public void setBlackUserID(String sBlackUserID) {
        this.mBlackUserID = sBlackUserID;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_check_del_ok:
                Log.i("RayTest","btn_check_del_ok");
                callbackOK();
                dismiss();
                break;

            case R.id.btn_check_del_cancel:
                Log.i("RayTest","btn_check_del_cancel");
                callbackCancel();
                dismiss();

                break;
        }
    }

    private void callbackCancel() {
        Bundle mArgs = RestoreArguments();
        if(mArgs.getInt("type")<=TYPE_ADD_FAVORITE_ALREADY_BLACK){
            String myValue = mArgs.getString("blackUserId");
            Log.i("RayTest","myValue2 "+myValue);

            if(mBlackUserID!=null && !mBlackUserID.equals("")&&mCallback!=null){

                mCallback.onCancelDialogcheck(mArgs);
            }
        }else{
            mCallback.onCancelDialogcheck(mArgs);
        }
    }

    private void callbackOK() {


        Bundle mArgs = RestoreArguments();
        if(mArgs.getInt("type")<=TYPE_ADD_FAVORITE_ALREADY_BLACK){
            String myValue = mArgs.getString("blackUserId");
            Log.i("RayTest","myValue2 "+myValue);

            if(mBlackUserID!=null && !mBlackUserID.equals("")&&mCallback!=null){

                mCallback.onOKDialogcheck(mArgs);
            }
        }else{
            mCallback.onOKDialogcheck(mArgs);
        }

    }

    public CreateViewDialogFragment setDialogCallback(dialogCallback callback){
        this.mCallback = callback;
        return fragment;
    }

    //iType :
    //1: 加入最愛
    public void showMsgDialog(FragmentManager manager,String title,  CharSequence msg, int iType ,boolean needPositiveButton ) {
        Log.i("RayTest","show : "+RoomInfoTmp.isDialogShow);
        if(!RoomInfoTmp.isDialogShow) {
            RoomInfoTmp.isDialogShow = true;
            Bundle args = new Bundle();

      /*      if (iType == TYPE_FINISH_MEDIA_PALYER ||
                    iType == TYPE_FINISH_MEDIA_RECORD ||
                    iType ==TYPE_ERROR_NETWORK ||
                    iType == TYPE_PAUSE_MEDIA_RECORD||
                    iType == TYPE_SPACE_NOT_ENOUGH) {
                args.putString("title", title);
                args.putString("content", msg);
            }*/
            args.putString("title", title);
            args.putCharSequence("content", msg);
            args.putInt("type", iType);
            args.putBoolean("needPositiveButton", needPositiveButton);
            SaveArguments(args);
            Log.i("RayTest","show type: "+iType);
            //dialogFragment.getDialog().setCanceledOnTouchOutside(false);
            show(manager, "dialog1");

        }

    }
    public void showCheckDelDialog(FragmentManager manager, String sBlackUserID, int iType) {
        if(!RoomInfoTmp.isDialogShow) {
            RoomInfoTmp.isDialogShow = true;
            setBlackUserID(sBlackUserID);
            Bundle args = new Bundle();
            args.putString("blackUserId", sBlackUserID);


            if (iType == TYPE_ADD_BLACKLIST) {
                args.putString("title", "確認將他/她設為黑名單? ");
                args.putString("content", "若設為黑名單則雙方將無法看見彼此，\n也會同時取消最愛．");
            }

            if (iType == TYPE_CANCEL_BLACKLIST) {
                args.putString("title", "確認將他/她移出黑名單? ");
                args.putString("content", "雙方將能在inLive中再次看到對方的資訊。\n" +
                        "（若您將黑名單用戶加入最愛,會自動移出黑名單）");
            }

            if (iType == TYPE_ADD_FAVORITE) {
                args.putString("title", "確認將他/她加入最愛? ");
                args.putString("content", "");
            }

            if (iType == TYPE_CANCEL_FAVORITE) {
                args.putString("title", "確認將他/她取消最愛? ");
                args.putString("content", "");
            }

            if (iType == TYPE_ADD_FAVORITE_ALREADY_BLACK) {
                args.putString("title", "");
                args.putString("content", "您已將他/她加入黑名單,\n無法加入最愛,請先解除黑名單。");
            }
            args.putInt("type", iType);
            SaveArguments(args);
            //dialogFragment.getDialog().setCanceledOnTouchOutside(false);
            Log.i("RayTest", "isCancelable " + isCancelable());
            show(manager, "dialog1");

        }

    }

    private void SaveArguments(Bundle args) {
        this.mBundle = args;
    }

    public void setTextGravity(int gravity){
        //tv_title.setGravity(Gravity.LEFT);

    }

    public void showHelpView(FragmentManager manager , String Url, int iType) {
        if(!RoomInfoTmp.isDialogShow) {
            Log.i("RayTest","showHelpView url:"+Url);
            RoomInfoTmp.isDialogShow = true;
            Bundle args = new Bundle();
            args.putString("Url", Url);
            args.putInt("type", iType);
            SaveArguments(args);
            this.mManager = manager;
            show(manager, "dialog1");

        }
    }

    public interface dialogCallback{
        void onOKDialogcheck(Bundle bundle);

        void onCancelDialogcheck(Bundle mArgs);
    }

    private class SimpleWebChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {



        }
    }

    private class SimpleWebViewClient extends WebViewClient {
        private final String mUrl;

        public SimpleWebViewClient(String url, AlertDialog dialog) {
            this.mUrl = url;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals(mUrl)) {
                // This is my web site, so do not override; let my WebView load the page
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            startActivity(intent);

            return false;
        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            Log.i("RayTest","onPageFinished");
            super.onPageFinished(view, url);



        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            //super.onReceivedSslError(view, handler, error);
        }

    }

    private class MyWebViewDownLoadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

        }
    }



}
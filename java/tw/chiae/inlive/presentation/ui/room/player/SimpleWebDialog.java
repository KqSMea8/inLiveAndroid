package tw.chiae.inlive.presentation.ui.room.player;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ms_square.etsyblur.BlurConfig;
import com.ms_square.etsyblur.BlurDialogFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import tw.chiae.inlive.R;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;
import tw.chiae.inlive.presentation.ui.main.setting.CreateViewDialogFragment;
import tw.chiae.inlive.presentation.ui.main.setting.SmartAsyncPolicyHolder;
import tw.chiae.inlive.presentation.ui.main.webview.SimpleWebViewActivity;
import tw.inlive.cewebkit.CEWebActivity;

import static tw.chiae.inlive.R.string.manager;

/**
 * Created by rayyeh on 2017/11/3.
 */

public class SimpleWebDialog extends BlurDialogFragment implements View.OnClickListener  {

    public static final int TYPE_WEB_NORMAL = 1;
    public static final int TYPE_WEB_EVENT = 2;
    private BlurConfig mBlurConfig;
    private Bundle mBundle;
    private boolean isShow = false;
    private ImageButton mBack;
    private TextView tvTitle;
    private RelativeLayout rl_toolbar;
    private dialogListener mlistener;
    private WebView mWebView;
    private RelativeLayout mToolbar;
    private RelativeLayout mainContent;
    private RelativeLayout dialogProgressMainView;
    private ValueAnimator valueAnimatorFadeIn,valueAnimatorFadeOut;

    public static SimpleWebDialog newInstance() {
        SimpleWebDialog fragment = new SimpleWebDialog();
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.EtsyBlurDialogTheme);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgbtn_toolbar_back:
                this.dismiss();
                break;
            default:
                break;
        }
    }

    public void setBlurConfig(BlurConfig config){
        Log.i("mBlurConfig","setBlurConfig");
        this.mBlurConfig = config;
    }

    public BlurConfig getBlurConfig (){
        if(mBlurConfig==null) {
            Log.i("mBlurConfig","getBlurConfig1");
            mBlurConfig = new BlurConfig.Builder().overlayColor(Color.argb(0, 0, 0, 0))  // semi-transparent white color
                    .asyncPolicy(SmartAsyncPolicyHolder.INSTANCE.smartAsyncPolicy())
                    .debug(true)
                    .allowFallback(false)
                    .build();
        }
        return mBlurConfig;
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i("RayTest","onCreateDialog");
        Bundle mArgs = RestoreArguments();
        if(mArgs==null)
            return null;
        AlertDialog dialog = initWebView(mArgs);
        return dialog;

        /*}else
            return super.onCreateDialog(savedInstanceState);*/
    }
    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private AlertDialog initWebView(Bundle mArgs) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_simple_web,null);
        int iViewType = mArgs.getInt("type");
        initAnimView();
        String mUrl = mArgs.getString("url");
        AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.EtsyBlurDialogTheme)
                .setView(v)
                .create();

        mWebView = (WebView) v.findViewById(R.id.simple_webview);
        mainContent = (RelativeLayout)v.findViewById(R.id.rl_simple_main_view);
        dialogProgressMainView = (RelativeLayout) v.findViewById(R.id.rl_pb_view);


        mToolbar = (RelativeLayout) v.findViewById(R.id.rl_web_dialog_toolbar);
        if(iViewType==TYPE_WEB_EVENT){
            mToolbar.setVisibility(View.GONE);
            mWebView.setAlpha(0);
        }else{
            mToolbar.setVisibility(View.VISIBLE);
            mWebView.setAlpha(1);
        }

        //設置dialog 大小


        //設置webveiw 大小
        //RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(dm.widthPixels, WindowManager.LayoutParams.MATCH_PARENT);
        //mWebView.setLayoutParams(rlp);

        dialog.setCanceledOnTouchOutside(false);

        if (!TextUtils.isEmpty(mUrl)) {
            mWebView.setWebChromeClient(new SimpleWebChromeClient());
            mWebView.setWebViewClient(new SimpleWebViewClient(mUrl,dialog));
            mWebView.setDownloadListener(new MyWebViewDownLoadListener());
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setLoadWithOverviewMode(true);
            mWebView.getSettings().setUseWideViewPort(true);
            mWebView.clearCache(true);
            mWebView.addJavascriptInterface(this, "CEWebViewAPI");
            //mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
            if(mUrl.contains("http")||mUrl.contains("https")){

            }else{
                mUrl = SourceFactory.wrapPath(mUrl);
            }
            //
            Log.i("RayTest","Load Url SimpleWeb:"+mUrl+"  "+mWebView.getScaleX());
            rl_toolbar = (RelativeLayout)v.findViewById(R.id.view_toolbar_height_buffer);
            mBack = (ImageButton) v.findViewById(R.id.imgbtn_toolbar_back);
            mBack.setOnClickListener(this);
            tvTitle = (TextView)v.findViewById(R.id.tv_toolbar_title);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                params.height = getStatusBarHeight();
                rl_toolbar.setLayoutParams(params);
            }

            mWebView.loadUrl(mUrl);
        }
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

    private void showMainProgress() {
        //mainContent.setAlpha(0);
        //dialogProgressMainView.setAlpha(1);

        //valueAnimatorFadeIn.addUpdateListener(new ViewAnimatorUpdateListener(dialogProgressMainView,1,valueAnimatorFadeIn));
        //valueAnimatorFadeOut.addUpdateListener(new ViewAnimatorUpdateListener(mainContent,0,valueAnimatorFadeOut));

        //valueAnimatorFadeIn.start();
        //valueAnimatorFadeOut.start();

    }
    private void initAnimView(){
         valueAnimatorFadeIn = ValueAnimator.ofFloat(0,1);
         valueAnimatorFadeOut = ValueAnimator.ofFloat(1,0);
        valueAnimatorFadeIn.setDuration(1000);
        valueAnimatorFadeOut.setDuration(1000);
    }
    private void HideMainProgress() {

        valueAnimatorFadeIn.addUpdateListener(new ViewAnimatorUpdateListener(mWebView,1,valueAnimatorFadeIn));
        //valueAnimatorFadeOut.addUpdateListener(new ViewAnimatorUpdateListener(dialogProgressMainView,0,valueAnimatorFadeOut));

        valueAnimatorFadeIn.start();

        //valueAnimatorFadeOut.start();
    }



    private int getStatusBarHeight() {

        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        Log.i("RayTest","status bar height:"+result);
        return result;
    }

    private Bundle RestoreArguments() {
        return mBundle;
    }
    private void SaveArguments(Bundle args) {
        this.mBundle = args;
    }

    public void showWebContent(FragmentManager supportFragmentManager, String url, int type) {
        if(!isShow) {
            isShow = true;
            Bundle args = new Bundle();
            args.putString("url", url);
            args.putInt("type", type);
            SaveArguments(args);
            show(supportFragmentManager, "dialog1");
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.i("RayTest","onDismiss");
        isShow = false;
        if(mlistener!=null)
            mlistener.WebDialogDismiss();
    }
    @Override
    public void onDetach() {
        super.onDetach();
        isShow = false;
    }

    private class SimpleWebChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            Log.i("RayTest","onReceivedTitle:"+title);
            if(!title.contains("www")&&!title.contains("http")&&!title.contains("https")){
                super.onReceivedTitle(view, title);
                if (tvTitle != null && !(TextUtils.isEmpty(title))) {
                    tvTitle.setText(title);
                }
            }else{
                if (tvTitle != null && !(TextUtils.isEmpty(title))) {
                    tvTitle.setText(title);
                }else
                    super.onReceivedTitle(view, title);
            }



        }
    }


    private class MyWebViewDownLoadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

        }
    }

    private class SimpleWebViewClient extends WebViewClient {
        private final String mUrl;
        private String returnUrl;

        public SimpleWebViewClient(String url, AlertDialog dialog) {
            this.mUrl = url;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals(mUrl)) {
                return false;
            }

            return false;
        }
        @Override
        public void onPageStarted(WebView view, String urlx, Bitmap favicon) {
            // TODO Auto-generated method stub
            //showMainProgress();
            if(!urlx.contains("facebook")) {
                this.returnUrl = urlx;
            }

            super.onPageStarted(view, urlx, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String urlx) {
            // TODO Auto-generated method stub
            Log.i("RayTest","onPageFinished");
            //super.onPageFinished(view, url);
            dialogProgressMainView.setAlpha(0);
            dialogProgressMainView.setVisibility(View.GONE);
            if(mWebView.getAlpha()==0)
                HideMainProgress();
            if(urlx.contains("facebook") && urlx.contains("dialog/oauth")) {
                if(this.returnUrl == null) {
                    this.returnUrl = mUrl;
                }

                mWebView.clearHistory();
                view.loadUrl("javascript:setTimeout(function(){window.location = \'" + this.returnUrl + "\';},1000);");

            }
            Log.i("RayWeb","onPageFinished");
            //view.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            super.onPageFinished(view, urlx);


        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            //super.onReceivedSslError(view, handler, error);
        }
    }
    @JavascriptInterface
    public void closeView() {
        Log.i("RayTest","closeView");
        this.dismiss();
    }

    public void setDialogListener(dialogListener listener){
        this.mlistener = listener;
    }
    public interface dialogListener {

        void WebDialogDismiss();
    }


    @JavascriptInterface
    public void onViewLoaded() {
        Log.i("RayTest","onViewLoaded");
    }


    private class ViewAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        private final float mfinalVal;
        private final ValueAnimator mValueAnimator;
        private View mView;

        public ViewAnimatorUpdateListener(View view , float maxValue , ValueAnimator valueAnimator) {
            this.mView = view;
            this.mfinalVal = maxValue;
            this.mValueAnimator = valueAnimator;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float val = (float) animation.getAnimatedValue();
            Log.i("RayTest","val:"+val);
            mView.setAlpha(val);
            if(val==mfinalVal){
                mValueAnimator.removeUpdateListener(this);
            }
        }
    }

    class MyJavaScriptInterface
    {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processHTML(String html)
        {
            Log.i("RayWeb","html:"+html);
            // process the html as needed by the app
            final File path = Environment.getExternalStoragePublicDirectory(
                    //Environment.DIRECTORY_PICTURES
                    Environment.DIRECTORY_DCIM + "/EventHtml/");
            Log.i("RayTest","processHTML path:"+path.getAbsolutePath());
            // Make sure the path directory exists.
            if(!path.exists())
            {
                // Make it, if it doesn't exit
                path.mkdirs();
            }

            final File file = new File(path, "config_"+System.currentTimeMillis()+".txt");

            // Save your stream, don't forget to flush() it before closing it.

            try
            {
                file.createNewFile();
                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append(html);

                myOutWriter.close();

                fOut.flush();
                fOut.close();
            }
            catch (IOException e)
            {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }
    }
}

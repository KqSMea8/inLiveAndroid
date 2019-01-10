package tw.chiae.inlive.presentation.ui.main.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ClientCertRequest;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import tw.chiae.inlive.R;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.main.MainActivity;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.L;

/**
 * @author Muyangmin
 * @since 1.0.0
 * 我的等级
 */
public class SimpleWebViewActivity extends BaseActivity {

    private static final String EXTRA_URL = "url";
    private static final String EXTRA_TITLE = "title";

    private String mUrl;

    private TextView tvTitle;
    private WebView mWebView;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri[]> mUploadMessage;
    private boolean mDefaultTitle = false;

    private String mTitle;
    private RelativeLayout rl_toolbar;

    public static Intent createIntent(Context context, String url, String title) {
        Log.i("RayTest","Load: " +url);
        Intent intent = new Intent(context, SimpleWebViewActivity.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_TITLE, title);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_simple_webview;
    }
    @Override
    protected void parseIntentData(Intent intent, boolean isFromNewIntent) {
        super.parseIntentData(intent, isFromNewIntent);
        mUrl = intent.getStringExtra(EXTRA_URL);
        mTitle = intent.getStringExtra(EXTRA_TITLE);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void findViews(Bundle savedInstanceState) {
        mWebView = $(R.id.simple_webview);

        WebSettings settings = mWebView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        rl_toolbar = $(R.id.rl_toolbar_container);
        rl_toolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.indexToolbar));
        tvTitle = $(R.id.tv_toolbar_title);
        if (TextUtils.isEmpty(mUrl)) {
            toastShort(getString(R.string.web_posturl_error));
            finish();
        } else {
            mWebView.setWebChromeClient(new SimpleWebChromeClient());
            mWebView.setWebViewClient(new SimpleWebViewClient());
            mWebView.setDownloadListener(new MyWebViewDownLoadListener());
            mWebView.getSettings().setJavaScriptEnabled(true);
            String absPath = SourceFactory.wrapPath(mUrl);
            //absPath = absPath+"&time="+getSystemTime();
            mWebView.loadUrl(absPath);
        }
    }

    private long getSystemTime() {
        return System.currentTimeMillis();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return;
            Uri[] a=new Uri[1];
            Uri result = intent == null || resultCode != RESULT_OK ? null
                    : intent.getData();
            a[0]=result;
            if (result!=null) {
                mUploadMessage.onReceiveValue(a);
            }
            mUploadMessage = null;
        }
    }

    @Override
    protected void init() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            try {
                mWebView.stopLoading();
                mWebView.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    private class SimpleWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("RayTest","shouldOverrideUrlLoading");
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
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Log.i("RayTest","shouldOverrideUrlLoading");
            return super.shouldOverrideUrlLoading(view, request);

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            Log.i("RayTest","onPageStarted");
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            Log.i("RayTest","onPageFinished");
            super.onPageFinished(view, url);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            Log.i("RayTest","onLoadResource");
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            Log.i("RayTest","onPageCommitVisible");
            super.onPageCommitVisible(view, url);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            Log.i("RayTest","shouldInterceptRequest");
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            Log.i("RayTest","shouldInterceptRequest");
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
            Log.i("RayTest","onTooManyRedirects");
            super.onTooManyRedirects(view, cancelMsg, continueMsg);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.i("RayTest","onReceivedError");
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            Log.i("RayTest","onReceivedError");
            super.onReceivedError(view, request, error);
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            Log.i("RayTest","onReceivedHttpError");
            super.onReceivedHttpError(view, request, errorResponse);
        }

        @Override
        public void onFormResubmission(WebView view, Message dontResend, Message resend) {
            Log.i("RayTest","onFormResubmission");
            super.onFormResubmission(view, dontResend, resend);
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            Log.i("RayTest","doUpdateVisitedHistory");
            super.doUpdateVisitedHistory(view, url, isReload);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            Log.i("RayTest","onReceivedSslError");
            //super.onReceivedSslError(view, handler, error);
        }

        @Override
        public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
            Log.i("RayTest","onReceivedClientCertRequest");
            super.onReceivedClientCertRequest(view, request);
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            Log.i("RayTest","onReceivedHttpAuthRequest");
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            Log.i("RayTest","shouldOverrideKeyEvent");
            return super.shouldOverrideKeyEvent(view, event);
        }

        @Override
        public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
            Log.i("RayTest","onUnhandledKeyEvent");
            super.onUnhandledKeyEvent(view, event);
        }

        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            Log.i("RayTest","onScaleChanged");
            super.onScaleChanged(view, oldScale, newScale);
        }

        @Override
        public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
            Log.i("RayTest","onReceivedLoginRequest");
            super.onReceivedLoginRequest(view, realm, account, args);
        }

    }

    public void setDefaultTitle(String sTitle){
        if (tvTitle != null && !(TextUtils.isEmpty(sTitle))) {
            tvTitle.setText(sTitle);
            this.mDefaultTitle = true;
        }
    }

    private class SimpleWebChromeClient extends WebChromeClient {
        @Override

        public void onReceivedTitle(WebView view, String title) {
            Log.i("RayTest","onReceivedTitle:"+title);
            boolean defaultTitle = false;
            if(mTitle.equals("INLIVE Gash APK 下載")){
                defaultTitle = true;
            }
            if(!title.contains("www")&&!title.contains("http")&&!title.contains("https") && !defaultTitle ){
                super.onReceivedTitle(view, title);
                if (tvTitle != null && !(TextUtils.isEmpty(title))) {
                    tvTitle.setText(title);
                }
            }else{
                if (tvTitle != null && !(TextUtils.isEmpty(title))) {
                    tvTitle.setText(mTitle);
                }else
                    super.onReceivedTitle(view, title);
            }


        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Log.i("tag", "url="+url);
            Log.i("tag", "userAgent="+userAgent);
            Log.i("tag", "contentDisposition="+contentDisposition);
            Log.i("tag", "mimetype="+mimetype);
            Log.i("tag", "contentLength="+contentLength);
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    private void getUrlType(String fName){
        String type="";
      /* 取得扩展名 */
        String end=fName.substring(fName.lastIndexOf(".")+1,fName.length()).toLowerCase();

      /* 依扩展名的类型决定MimeType */
        if(end.equals("pdf")){
            type = "application/pdf";//
        }
        else if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||
                end.equals("xmf")||end.equals("ogg")||end.equals("wav")){
            type = "audio/*";
        }
        else if(end.equals("3gp")||end.equals("mp4")){
            type = "video/*";
        }
        else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||
                end.equals("jpeg")||end.equals("bmp")){
            type = "image/*";
        }
        else if(end.equals("apk")){
        /* android.permission.INSTALL_PACKAGES */
            Uri uri = Uri.parse(fName);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }
}

package tw.chiae.inlive.presentation.ui.main.mergefilm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.PermissionChecker;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.SslErrorHandler;
import android.net.http.SslError;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import tw.chiae.inlive.presentation.ui.main.mergefilm.model.FileUtils;
import tw.chiae.inlive.presentation.ui.widget.CustomToast;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.R.attr.handle;
import static android.R.attr.targetSdkVersion;

// K歌大明星活動頁面
public class KSWebActivity extends FragmentActivity
{
	private static final int REQUEST_EXTERNAL_STORAGE = 1045;
	private WebView			webView;
	private ProgressDialog	dialog;
	private String			userId;

	private float			uploadProgress;
	private boolean isThread2Alive = true;
	private KSMultipartUtility multipart = null;
	private String returnUrl;
	private int ApptargetSdkVersion;
	//private ProgressDialog uploadProgressView;

	@SuppressLint("SetJavaScriptEnabled")	// 不發出 setJavascriptEnabled 警告
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// 取得參數
		Intent	intent = getIntent();

		userId = intent.getStringExtra("userId");

		// 設定關閉主視窗 title, 讓 WebView 顯示成全螢幕
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		// 建立 WebView 物件
		webView = new WebView(this);

		// 無法防止遊戲內關閉 Cookie, 改用其他方式記錄登入狀態
		CookieManager.getInstance().setAcceptCookie(true);

		// Android API Level 21 才支援 setAcceptThirdPartyCookies
		//CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

		// 開啟 Javascript 支援
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);

		// 設定不使用 Cache
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

		// 串接 Javascript 介面
		webView.addJavascriptInterface(this, "karaStarAPI");

		// 設定點選網頁連結不另開瀏覽器
		webView.setWebViewClient(new WebViewClient()
		{
			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
			{
				// 設定 ssl 支援
				handler.cancel();
			}
		});

		// 設定 WebView 為 Activity 顯示內容
		setContentView(webView);

		dialog = null;
		/*webView.setWebViewClient(new WebViewClient()
		{
			// This method will be triggered when the Page Started Loading
			public void onPageStarted(WebView view, String url, Bitmap favicon)
			{
				if(dialog != null)
					dialog.dismiss();

				dialog = ProgressDialog.show(KSWebActivity.this, null, "載入中請稍候...");
				dialog.setCancelable(true);
				super.onPageStarted(view, url, favicon);
			}

			// This method will be triggered when the Page loading is completed
			public void onPageFinished(WebView view, String url)
			{
				dialog.dismiss();
				super.onPageFinished(view, url);
				if(url.contains("facebook.com") && (url.contains("&refsrc") || url.contains("&domain")))
				{
					Uri uri = Uri.parse(url);
					String callbackUrl = uri.getQueryParameter("refsrc");
					if(callbackUrl==null)
						callbackUrl = "http://" + uri.getQueryParameter("domain");
					view.loadUrl(callbackUrl); // callback URL
				}

			}
		});*/
		returnUrl = null;
		webView.setWebViewClient(new WebViewClient()
		{
			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
			{
				// 設定 ssl 支援
				handler.cancel();
			}

			// This method will be triggered when the Page Started Loading
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon)
			{
				if(dialog != null)
					dialog.dismiss();

				if(!url.contains("facebook.com"))
					returnUrl = url;

				dialog = ProgressDialog.show(KSWebActivity.this, null, "載入中請稍候...");
				dialog.setCancelable(true);
				super.onPageStarted(view, url, favicon);
			}

			// This method will be triggered when the Page loading is completed
			@Override
			public void onPageFinished(WebView view, String url)
			{
				dialog.dismiss();
				super.onPageFinished(view, url);

				if(url.contains("facebook.com") && url.contains("share/submit"))
				{
					if(returnUrl == null)
						returnUrl = "/events/karastar";

					view.loadUrl("javascript:setTimeout(function(){window.location = '" + returnUrl + "';},3000);");
				}
			}
		});

		// 進入活動頁面

		String url = "http://api2.inlive.tw/events/karastar?uid=" + userId + "&platform=android"+"&systime="+getSystemTime();
		try
		{
			webView.loadUrl(url);
		}
		catch(Exception e)
		{
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		if(dialog != null) {
			dialog.dismiss();
		}
		if(multipart!=null)
			multipart=null;
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("RayTest","KSWEBACtivity onResume!");
	}

	private long getSystemTime(){
		return System.currentTimeMillis();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
	}

	// 處理使用者按下返回鍵
	@Override
	public void onBackPressed()
	{
		if(webView.canGoBack())
		{
			webView.goBack();
		}
		else
		{
			super.onBackPressed();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == KaraStar.REQUEST_VIDEO_RECORD)
		{
			// 錄影返回
			if(resultCode == RESULT_OK)
			{
				String  path = data.getStringExtra("filePath");
				int     starId = data.getIntExtra("starId", 0);
				float   timeOffset = data.getFloatExtra("timeOffset", 0);
                float   audioAdjust = data.getFloatExtra("audioAdjust", 0);
				//webView.loadUrl("https://api2.inlive.tw/events/karastar/upload?f=" + path + "&starid=" + starId + "&timeoffset=" + timeOffset);
                webView.loadUrl("https://api2.inlive.tw/events/karastar/upload?f=" + path + "&starid=" + starId + "&timeoffset=" + timeOffset + "&audioadjust=" + audioAdjust);
			}
		}
	}

	// -----------------------------------------------------------
	// Javascript 交換資料用 function

	// 開啟錄影頁面
	@JavascriptInterface
	public void startRecord(int starId, String videoUrl, String videoName)
	{

		URL url = null;
		int file_size = 0;
		try {
			String path = videoUrl+videoName+".mp4";
			url = new URL(path);
			URLConnection urlConnection = url.openConnection();
			urlConnection.connect();
			file_size = urlConnection.getContentLength();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.i("RayTest","id:"+ starId + " video:"+videoUrl +" videoName:"+videoName+" size:"+file_size/(1024*1024));

		//boolean auth = CheckAuthority();

		KaraStar.getInstance().viewHandler.switchToRecordView(this, starId, videoUrl, videoName);
	}

	private boolean CheckAuthority() {

		int permission_camera = ActivityCompat.checkSelfPermission(this,
				CAMERA);
		int permission_Record = ActivityCompat.checkSelfPermission(this,
				RECORD_AUDIO);


/*		//相機權限
		if(permission_camera != PackageManager.PERMISSION_GRANTED){
			Log.i("RayTest","相機 未取得權限，向使用者要求允許權限");
			ActivityCompat.requestPermissions(this,
					new String[] {CAMERA},
					REQUEST_EXTERNAL_STORAGE
			);

		}else{
			Log.i("RayTest","已有權限，可進行檔案存取");
		}


		//錄音權限
		if(permission_Record != PackageManager.PERMISSION_GRANTED){
			Log.i("RayTest","相機 未取得權限，向使用者要求允許權限");
			ActivityCompat.requestPermissions(this,
					new String[] {RECORD_AUDIO},
					REQUEST_EXTERNAL_STORAGE
			);

		}else{
			Log.i("RayTest","已有權限，可進行檔案存取");
		}*/
		boolean result = true;
		try {
			final PackageInfo info = getApplicationContext().getPackageManager().getPackageInfo(
					getApplicationContext().getPackageName(), 0);
			ApptargetSdkVersion = info.applicationInfo.targetSdkVersion;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		Log.i("RayTest","Build.VERSION.SDK_INT: "+Build.VERSION.SDK_INT+ " Build.VERSION_CODES.M :"+Build.VERSION_CODES.M);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

			if (targetSdkVersion >= Build.VERSION_CODES.M) {
				// targetSdkVersion >= Android M, we can
				// use Context#checkSelfPermission
				Log.i("RayTest","targetSdkVersion >= Build.VERSION_CODES.M");
				result = getApplicationContext().checkSelfPermission(CAMERA)
						== PackageManager.PERMISSION_GRANTED;
			} else {
				// targetSdkVersion < Android M, we have to use PermissionChecker
				Log.i("RayTestnji","targetSdkVersion < Build.VERSION_CODES.M");
				result = PermissionChecker.checkSelfPermission(getApplicationContext(), CAMERA)
						== PermissionChecker.PERMISSION_GRANTED;
			}
		}
		Log.i("RayTest","相機 未取得權限，向使用者要求允許權限 "+ result);
		return result;

	}

	// 上傳影片檔案
	// 上傳影片檔案
	@JavascriptInterface
	public void videoUpload(final int starId, final String videoFile)
	{
		/*uploadProgress = 0;
		if(uploadProgressView==null)
			uploadProgressView = new ProgressDialog(this);
		uploadProgressView.setTitle("上傳進度條");
		uploadProgressView.setTitle("正在處理	請稍候...");
		uploadProgressView.setProgress(0);
		uploadProgressView.setMax(100);
		uploadProgressView.show();*/
		//uploadProgressView.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		try
		{
			String requestURL = "http://api2.inlive.tw/events/karastar/upload_video";
			multipart = new KSMultipartUtility(requestURL, "utf-8");

			multipart.addFormField("userId", userId);
			multipart.addFormField("starId", Integer.toString(starId));

			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					while(multipart.uploadLength == 0 || multipart.uploadBytes < multipart.uploadLength)
					{

						uploadProgress = (float)multipart.uploadBytes / multipart.uploadLength;
						//Log.i("RayTest","videoUpload:"+uploadProgress);
						//uploadProgressView.incrementProgressBy((int) Math.floor(uploadProgress*100));
						try
						{
							Thread.sleep(100);
						}
						catch (InterruptedException e)
						{
						}
					}
					Log.i("RayTest","videoUpload ok!");

				}
			}).start();
			new Thread(new Runnable() {
				@Override
				public void run()
				{
					try {
						isThread2Alive = true;
						multipart.addFilePart("videoFile", new File(videoFile));

						List<String> response = multipart.finish();
						showCompleteUpLoad(videoFile);
						setLoadUrl(1);
					}
					catch (IOException e)
					{
						Log.d("KaraStar", e.getLocalizedMessage());
						Log.i("RayTest", "set false: "+e.getLocalizedMessage());
						isThread2Alive = false;
						toastShort("影片上傳失 敗，請重新再上傳作品，謝謝。");
						setLoadUrl(0);
					}
					//showCompleteUpLoad(videoFile);
					//setLoadUrl(1);
				}
			}).start();


		}
		catch (IOException e)
		{
			Log.d("KaraStar", e.getLocalizedMessage());
			toastShort("影片上傳失敗，請重新再上傳作品，謝謝。");
			setLoadUrl(0);
		}


	}

	private void showCompleteUpLoad(String videoFile) {
		if (FileUtils.checkFile(videoFile)) {
			File f = new File(videoFile);
			if (f != null) {
				if (f.exists()) {
					//已经存在，删除
					if (f.isDirectory())
						FileUtils.deleteDir(f);
					else
						FileUtils.deleteFile(f);
				}
			}
		}
		/*if(uploadProgressView!=null && uploadProgressView.isShowing())
			uploadProgressView.dismiss();*/
		toastShort("恭喜您影片上傳成功!審核過後將會顯示在排行榜中。您可至您的參賽頁查看審核進度");
	}

	private void setLoadUrl(int i) {
		final String str ;
		switch (i){

			case 1:
				str ="javascript:uploadResult(1,'');";
				break;

			default:
				str ="javascript:uploadResult(0,'');";
				break;
		}
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.i("RayTest","javascript1");
				webView.loadUrl(str);
				Log.i("RayTest","javascript2");
			}
		});
	}

	// 取得目前的上載進度
	@JavascriptInterface
	public float getUploadProgress()
	{
		Log.i("RayPro","uploadProgress:"+uploadProgress);
		return uploadProgress;
	}

	protected void toastShort(@NonNull final String msg){
		new Thread(){
			public void run() {

				Looper.prepare();

				CustomToast.makeCustomText(KSWebActivity.this, msg, Toast.LENGTH_SHORT).show();

				Looper.loop();// 进入loop中的循环，查看消息队列

			};

		}.start();

	}

	// 開啟個人頁面
	@JavascriptInterface
	public void openUserPage(String userId)
	{
		KaraStar.getInstance().viewHandler.switchToUserView(this, userId);
	}

	@JavascriptInterface
	public void onClose()
	{
		finish();
	}


}

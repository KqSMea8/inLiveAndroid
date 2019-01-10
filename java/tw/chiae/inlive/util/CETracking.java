package tw.chiae.inlive.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CETracking
{
	// -----------------------------------------------------------
	// 追蹤事件定義
	final public static String	EVENT_APP_START		= "CE_AppStart";		// 啟動 App

	final public static String	EVENT_USER_LOGIN	= "CE_UserLogin";		// 使用者登入
	final public static String	EVENT_USER_LOGOUT	= "CE_UserLogout";		// 使用者登出
	final public static String	EVENT_USER_ALIVE	= "CE_UserAlive";		// 使用者持續上線中

	final public static String	EVENT_USER_PURCHASE	= "CE_UserPurchase";	// 使用者付費
	private static final String CETRACK_TAG = "CE_REQUEST_TAG" ;

	// -----------------------------------------------------------
	private static CETracking	instance = null;

	private Context applicationContext = null;
	private Activity currentActivity = null;
	private String	appId = "";
	private String	apiUrl = "";

	private long	responseTime = 0;	// 每次追蹤訊息返回時計算, 下次發送訊息時傳給 Server
	private String	sessionId = "";		// 由 Server 產生, App 啟動後的所有訊息都屬於同一個 Session
	private StringRequest jsonRequest;

	private CETracking()
	{
	}

	// 取得唯一的 CESocialKit API 物件
	public static CETracking getInstance()
	{
		if (instance == null)
			instance = new CETracking();

		return instance;
	}

	// -----------------------------------------------------------
	private interface PostCompletion
	{
		public void onResult(String response, Exception e);
	}

	// 網路通訊底層
	private RequestQueue requestQueue = null;

	// 取得 RequestQueue 物件
	private RequestQueue getRequestQueue()
	{
		if (requestQueue == null)
		{
			if (applicationContext == null)
				return null;

			requestQueue = Volley.newRequestQueue(applicationContext);
		}

		return requestQueue;
	}

	private void sendPost(final String url, final Map<String, String> postData, final PostCompletion postCompletion)
	{
		if(getRequestQueue() == null)
		{
			if(postCompletion != null)
				postCompletion.onResult(null, null);
			return;
		}

		jsonRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
		{
			@Override
			public void onResponse(String response)
			{
				if(postCompletion != null)
					postCompletion.onResult(response, null);
			}
		}, new Response.ErrorListener()
		{
			@Override
			public void onErrorResponse(VolleyError error)
			{
				if(postCompletion != null)
					postCompletion.onResult(null, error);
			}
		})
		{
			@Override
			protected Map<String, String> getParams() throws AuthFailureError
			{
				return postData;
			}
		};
		jsonRequest.setTag(CETRACK_TAG);
		getRequestQueue().add(jsonRequest);
	}

	// 送出追蹤事件
	private void trackEvent(String eventName, String data)
	{
		final long	sendTime = SystemClock.uptimeMillis();

		Map<String, String> postData = new HashMap<>();
		postData.put("appId", appId);
		postData.put("eventName", eventName);
		postData.put("responseTime", Long.toString(responseTime));
		postData.put("sessionId", sessionId);
		postData.put("data", data);

		sendPost(apiUrl + "app_track", postData, new PostCompletion()
		{
			@Override
			public void onResult(String response, Exception e)
			{
				try
				{
					if(response==null)
						return;
					JSONObject json = new JSONObject(response);

					if(json.has("sessionId"))
					{
						sessionId = json.getString("sessionId");
					}
				}
				catch(JSONException jsonException)
				{
					return;
				}

				// 計算本次訊息返回花費時間
				responseTime = SystemClock.uptimeMillis() - sendTime;
			}
		});
	}

	// -----------------------------------------------------------
	private AppEventsLogger getFacebookEventLogger()
	{
		return AppEventsLogger.newLogger(currentActivity);
	}

	// -----------------------------------------------------------
	// 追蹤事件

	// App 啟動
	public void onAppStart(Application app, String _appId, String _apiUrl)
	{
		appId	= _appId;
		apiUrl	= _apiUrl;

		// 啟動 Facebook SDK
		AppEventsLogger.activateApp(app);

		applicationContext = app.getApplicationContext();

		try
		{
			JSONObject	json = new JSONObject();

			json.put("UUID", CEInstallation.id(applicationContext));

			trackEvent(EVENT_APP_START, json.toString());
		}
		catch (JSONException e)
		{
		}
	}

	// -----------------------------------------------------------
	private Handler		alivehandler = new Handler();
	private Runnable	aliveRunnable = null;
	private String		userId = "";

	// 使用者登入
	public void onUserLogin(Activity activity, String _userId)
	{
		currentActivity	= activity;
		userId			= _userId;

		AppEventsLogger	logger = getFacebookEventLogger();

		Bundle parameters = new Bundle();
		parameters.putString("userId", userId);
		logger.logEvent(EVENT_USER_LOGIN, parameters);

		try
		{
			JSONObject	json = new JSONObject();

			json.put("userId", userId);

			trackEvent(EVENT_USER_LOGIN, json.toString());
		}
		catch (JSONException e)
		{
		}

		// 登入後定時更新登入持續時間
		aliveRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					JSONObject	json = new JSONObject();

					json.put("userId", userId);

					trackEvent(EVENT_USER_ALIVE, json.toString());
				}
				catch (JSONException e)
				{
				}

				// 首次更新之後改每三分鐘更新
				alivehandler.postDelayed(this, 1000 * 60 * 3);
			}
		};

		// 一分鐘後第一次更新
		alivehandler.postDelayed(aliveRunnable, 1000 * 60);
	}

	// 使用者登出
	public void onUserLogout(Activity activity)
	{
		if(userId == "" || aliveRunnable == null)
			return;

		currentActivity	= activity;

		AppEventsLogger	logger = getFacebookEventLogger();

		Bundle parameters = new Bundle();
		parameters.putString("userId", userId);
		logger.logEvent(EVENT_USER_LOGOUT, parameters);

		try
		{
			JSONObject	json = new JSONObject();

			json.put("userId", userId);

			trackEvent(EVENT_USER_LOGOUT, json.toString());
		}
		catch (JSONException e)
		{
		}

		// 關閉登入持續更新
		alivehandler.removeCallbacks(aliveRunnable);
		aliveRunnable = null;
		userId = "";
		getRequestQueue().cancelAll(CETRACK_TAG);
	}

	// -----------------------------------------------------------
	// 使用者付費
	public void onUserPurchase(Activity activity, float money, String currency)
	{
		if(userId == "")
			return;

		currentActivity	= activity;

		AppEventsLogger	logger = getFacebookEventLogger();

		Bundle parameters = new Bundle();
		parameters.putString("userId", userId);
		parameters.putFloat("money", money);
		parameters.putString("currency", currency);
		logger.logEvent(EVENT_USER_PURCHASE, parameters);

		try
		{
			JSONObject	json = new JSONObject();

			json.put("userId", userId);
			json.put("money", money);
			json.put("currency", currency);

			trackEvent(EVENT_USER_PURCHASE, json.toString());
		}
		catch (JSONException e)
		{
		}
	}
}

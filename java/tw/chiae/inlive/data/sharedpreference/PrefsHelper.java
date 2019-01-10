package tw.chiae.inlive.data.sharedpreference;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import tw.chiae.inlive.data.bean.CameraSize;
import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.data.bean.local.PayChannel;
import tw.chiae.inlive.util.L;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class PrefsHelper {

    private static final String LOG_TAG = "PrefsHelper";

    private static final String PREFERENCE_FILE_NAME = "BeautyLivePrefs";

    private static final String KEY_IS_FIRST_RUN = "isFirstRun";

    private static final String KEY_LOGIN_INFO = "loginInfo";

    private static final String KEY_FRONT_CAMERA_WIDTH = "frontCamWidth";
    private static final String KEY_FRONT_CAMERA_HEIGHT = "frontCamHeight";
    private static final String KEY_BACK_CAMERA_WIDTH = "backCamWidth";
    private static final String KEY_BACK_CAMERA_HEIGHT = "backCamHeight";


    private static final String KEY_PAY_CHANNEL = "payChannel";

    public static void init(Context context) {
        Prefs.init(context.getApplicationContext(), PREFERENCE_FILE_NAME);
    }

    public static boolean getIsFirstRun() {
        //首次启动这个方法比较特别，默认返回true，而不是一般Boolean的false
        return Prefs.get(KEY_IS_FIRST_RUN, true);
    }

    public static void setIsFirstRun(boolean isFirstRun) {
        Prefs.set(KEY_IS_FIRST_RUN, isFirstRun);
    }

    /**
     * 保存用户的登录数据。
     */
    public static void setLoginInfo(@NonNull LoginInfo info) {
        String infoToSave = new Gson().toJson(info);
        L.d(LOG_TAG, "LoginInfoToSp:%s", infoToSave);
        Log.i("RayTest","setLoginInfo LoginInfoToSp:"+infoToSave);
        Prefs.set(KEY_LOGIN_INFO, infoToSave);
    }

    /**
     * 清除登录数据。
     */
    public static void removeLoginInfo() {
        L.d(LOG_TAG, "Removing login info.");
        Log.i("RayTest","Removing login inf:");
        Prefs.remove(KEY_LOGIN_INFO);
    }

    /**
     * 查询存储的用户登录数据，如果不存在则返回null。
     */
    @Nullable
    public static LoginInfo getLoginInfo() {
        String savedInfo = Prefs.getString(KEY_LOGIN_INFO);
        L.i(LOG_TAG, "LoginInfoFromSp:%s", savedInfo);
        Log.i("RayTestLoginInfo", "getLoginInfo LoginInfoFromSp: "+ savedInfo);
        if (TextUtils.isEmpty(savedInfo)) {
            return null;
        }
        return new Gson().fromJson(savedInfo, LoginInfo.class);
    }

    public static void saveCameraSize(@NonNull CameraSize size, boolean isFrontCamera) {
        Prefs.set(isFrontCamera ? KEY_FRONT_CAMERA_WIDTH : KEY_BACK_CAMERA_WIDTH, size.width);
        Prefs.set(isFrontCamera ? KEY_FRONT_CAMERA_HEIGHT : KEY_BACK_CAMERA_HEIGHT, size.height);
    }

    @Nullable
    public static CameraSize getCameraSize(boolean isFrontCamera) {
        int width = Prefs.getInt(isFrontCamera ? KEY_FRONT_CAMERA_WIDTH : KEY_BACK_CAMERA_WIDTH);
        int height = Prefs.getInt(isFrontCamera ? KEY_FRONT_CAMERA_HEIGHT : KEY_BACK_CAMERA_HEIGHT);
        if (width <= 0 || height <= 0) {
            return null;
        } else {
            return new CameraSize(width, height);
        }
    }

    @PayChannel
    public static int getPreferredChannel(@PayChannel int defValue){
        return Prefs.get(KEY_PAY_CHANNEL, defValue);
    }

    public static void savePreferredPayChannel(@PayChannel int channel){
        Prefs.set(KEY_PAY_CHANNEL, channel);
    }

}

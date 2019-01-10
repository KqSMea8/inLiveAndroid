package tw.chiae.inlive.util;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

/**
 * 封装Android应用（Package）相关的方法。
 *
 * @author Muyangmin
 * @since 1.0.0
 */
public final class Packages {
    /**
     * 获得当前应用的版本名称。
     *
     * @return 返回应用版本号；如果未获取到，返回null。
     */
    public static String getVersionName(Context context) {
        return getVersionName(context, context.getPackageName());
    }

    /**
     * 获得指定应用的版本名称。
     *
     * @param packageName 应用包名。
     * @return 返回应用版本名称；如果未获取到，返回null。
     */
    public static String getVersionName(Context context, String packageName) {
        String version = null;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(packageName, 0);
            version = info.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 获得当前应用的版本号。
     *
     * @return 返回应用版本号。
     */
    public static int getVersionCode(Context context) {
        return getVersionCode(context, context.getPackageName());
    }

    /**
     * 获得指定应用的版本号。
     *
     * @param packageName 应用包名。
     * @return 返回应用版本号；如果未获取到，返回-1。
     */
    public static int getVersionCode(Context context, String packageName) {
        int version = -1;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(packageName, 0);
            version = info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 安装指定的APK文件。会发送一个Intent供用户选择用来执行的程序。
     *
     * @param activity 要执行的上下文。
     * @param path     要安装的文件的绝对路径。如果参数为空，则不执行操作。
     */
    public static void installPackage(Activity activity, String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        installPackage(activity, new File(path));
    }

    /**
     * 安装指定的APK文件。会发送一个Intent供用户选择用来执行的程序。
     *
     * @param context 要执行的上下文。
     * @param file    要安装的文件。如果参数不是文件或文件不存在，不会执行任何操作。
     */
    public static void installPackage(Context context, File file) {
        if (file.exists() && file.isFile()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * 读取AndroidManifest.xml文件下的MetaData标签并取出指定键的值。
     *
     * @param key 要读取的键
     * @return 如果成功获取到则返回值，否则返回null。
     */
    public static String getMetaDataValue(Context context, String key) {
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            // 使用get方法，避免取到的数据为纯数字等非String类型
            Object value = appInfo.metaData.get(key);
            if (value != null) {
                return value.toString();
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询指定的Action是否能被Activity响应。
     *
     * @param context 上下文信息
     * @param intent  目标intent
     * @return 返回解析的Activity的数目，可能为0。
     * @see PackageManager#queryIntentActivities(Intent, int)
     */
    public static int queryIntentActivities(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size();
    }

    /**
     * 启动应用市场，如果失败则抛出一个RuntimeException。
     */
    public static void launchMarketOrThrow(Context context) {
        launchMarket(context, new OnMarketNotFoundListener() {

            @Override
            public void onMarketNotFound() {
                throw new RuntimeException();
            }
        });
    }

    /**
     * 启动应用市场并跳转到相关产品页面。
     *
     * @param context  上下文信息，不能为null。
     * @param listener 如果设备商不存在应用市场则回调，建议不要使用null。
     */
    public static void launchMarket(Context context,
                                    OnMarketNotFoundListener listener) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        int targetActivities = queryIntentActivities(context, intent);
        if (targetActivities <= 0) {
            if (listener != null) {
                listener.onMarketNotFound();
            }
        } else {
            //在某些机型和ROM版本上前面的query方法可能失效
            //sample: OPPO R819T，meizu MX5
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                listener.onMarketNotFound();
            }
        }
    }

    public interface OnMarketNotFoundListener {
        void onMarketNotFound();
    }
}

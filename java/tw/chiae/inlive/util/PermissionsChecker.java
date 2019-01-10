package tw.chiae.inlive.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by rayyeh on 2017/11/7.
 */

public class PermissionsChecker {
    private final Context mContext;
    private int targetSdkVersion;


    public PermissionsChecker(Context context) {
        mContext = context.getApplicationContext();
        String PackName = mContext.getApplicationContext().getPackageName();
        final PackageInfo info;
        try {
            info = mContext.getApplicationContext().getPackageManager().getPackageInfo(PackName, 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
            Log.i("RayTest","targetSdkVersion: "+targetSdkVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    // 判断权限集合
    public boolean lacksPermissions(String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
                return true;
            }
        }

        return false;
    }

    // 判断是否缺少权限
    public boolean lacksPermission(String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = mContext.checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(mContext, permission)
                        == PermissionChecker.PERMISSION_GRANTED;

            }
        }else
            result = true;
        Log.i("RayTest","permission:"+permission+" result:"+result);
        return result;



        /*return ContextCompat.checkSelfPermission(mContext, permission) ==
                PackageManager.PERMISSION_DENIED;*/
    }

    public List<String> getLacksPermissions(String[] permissions) {
        List<String> leaksPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
            }else{
                leaksPermissionList.add(permission);
            }
        }
        return leaksPermissionList;
    }

}

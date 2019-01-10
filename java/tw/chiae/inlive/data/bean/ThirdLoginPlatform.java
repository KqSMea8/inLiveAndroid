package tw.chiae.inlive.data.bean;

import android.support.annotation.StringDef;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public @StringDef @interface ThirdLoginPlatform {
    String PLATFORM_SINA = "sina";
    String PLATFORM_QQ = "qq";
    String PLATFORM_WECHAT = "wechat";
    String PLATFORM_FACEBOOK="facebook";
    String PLATFORM_TWTTER="twitter";
    String PLATFORM_LINE="line";
    String PLATFORM_INSTAGRAM="instagram";
    String PLATFORM_GOOGLE="googleplus";
}

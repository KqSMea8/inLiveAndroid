package tw.chiae.inlive.util.share;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import tw.chiae.inlive.R;
import tw.chiae.inlive.presentation.ui.widget.CustomToast;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.L;

import java.util.HashMap;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.google.GooglePlus;

import cn.sharesdk.line.Line;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class ShareHelper {

    private static final String LOG_TAG = "ShareHelper";

    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    /**
     * 由调用者决定何时分享（同步方法，调用立即开始分享）
     */
    public <T extends Platform.ShareParams> void invokeShare(final String platformName,
                                                             final PlatformActionListener listener,
                                                             final Func1<Void, T> shareParamBuilder) {
        Platform platform = ShareSDK.getPlatform(platformName);
        platform.setPlatformActionListener(listener);
        platform.share(shareParamBuilder.call(null));
    }

    protected void toastShort(Context context, @NonNull String msg) {
        CustomToast.makeCustomText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public final void unsubscribeAll() {
        mSubscriptions.unsubscribe();
    }

    public interface ShareParamProvider {
        /**
         * 分享的URL，必填。
         */
        @NonNull
        String getShareTitleUrl();

        /**
         * 分享标题，必填。
         */
        @NonNull
        String getShareTitle();

        /**
         * 分享内容，必填。
         */
        @NonNull
        String getShareText();

        @NonNull
        String getImgUrl();
        /**
         * 仅微信和朋友圈需要提供这个选项。
         */
        int getWechatShareType();
    }

    public static final class RoomShareParam implements ShareParamProvider {

        private String shareTitle;
        private String shareTitleUrl;
        private String shareText;
        private String shartImg;
        public RoomShareParam(Context context, String roomId, String anchorName,String imgUrl) {
            shareTitle = context.getString(R.string.share_titles,anchorName);
            shareTitleUrl = context.getString(R.string.share_room_url, "http://"+Const.MAIN_HOST_TEST, roomId);
            shareText = context.getString(R.string.share_room_text, anchorName);
            shartImg="http://"+Const.MAIN_HOST_TEST+imgUrl;
            L.i(LOG_TAG, "Share url=%s", shareTitleUrl);
        }

        @NonNull
        @Override
        public String getShareTitleUrl() {
            return shareTitleUrl;
        }

        @NonNull
        @Override
        public String getShareTitle() {
            return shareTitle;
        }

        @NonNull
        @Override
        public String getShareText() {
            return shareText;
        }

        @NonNull
        @Override
        public String getImgUrl() {
            return shartImg;
        }

        public void setShartImg(String shartImg) {
            this.shartImg = shartImg;
        }

        @Override
        public int getWechatShareType() {
            return Platform.SHARE_WEBPAGE;
        }
    }

    public static class DefaultShareListener implements PlatformActionListener {

        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            L.i(LOG_TAG, "onComplete, platform=%s, i=%s, map=%s", platform.getName(), i, hashMap);
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            L.e(LOG_TAG, "onError, platform=%s, i=%s, throwable=%s", platform.getName(), i,
                    throwable);
        }

        @Override
        public void onCancel(Platform platform, int i) {
            L.i(LOG_TAG, "onCancel, platform=%s, i=%s", platform.getName(), i);
        }

    }

    public static final class FacebookParamBuilder implements Func1<Void, Facebook.ShareParams> {

        private ShareParamProvider provider;

        public FacebookParamBuilder(ShareParamProvider provider) {
            this.provider = provider;
        }

        @Override
        public Facebook.ShareParams call(Void aVoid) {
            Facebook.ShareParams params = new Facebook.ShareParams();
            params.setImageUrl(provider.getImgUrl());
            params.setTitleUrl(provider.getShareTitleUrl());
            params.setTitle(provider.getShareTitle());
            params.setText(provider.getShareText());
            params.setShareType(provider.getWechatShareType());
            params.setUrl(provider.getShareTitleUrl());
            return params;
        }
    }

    public static final class GooglePlusParamBuilder implements Func1<Void, GooglePlus.ShareParams> {

        private ShareParamProvider provider;

        public GooglePlusParamBuilder(ShareParamProvider provider) {
            this.provider = provider;
        }

        @Override
        public GooglePlus.ShareParams call(Void aVoid) {
            GooglePlus.ShareParams params = new GooglePlus.ShareParams();
            params.setImageUrl(provider.getImgUrl());
            params.setTitleUrl(provider.getShareTitleUrl());
            params.setTitle(provider.getShareTitle());
            params.setText(provider.getShareText());
            params.setShareType(provider.getWechatShareType());
            params.setUrl(provider.getShareTitleUrl());
            return params;
        }
    }

    public static final class LineParamBuilder implements Func1<Void, Line.ShareParams> {

        private ShareParamProvider provider;

        public LineParamBuilder(ShareParamProvider provider) {
            this.provider = provider;
        }

        @Override
        public Line.ShareParams call(Void aVoid) {
            Line.ShareParams params = new Line.ShareParams();
            params.setImageUrl(provider.getImgUrl());
            params.setTitleUrl(provider.getShareTitleUrl());
            params.setTitle(provider.getShareTitle());
            params.setText(provider.getShareText());
            params.setShareType(provider.getWechatShareType());
            params.setUrl(provider.getShareTitleUrl());
            return params;
        }
    }

/*    public static final class InstagramParamBuilder implements Func1<Void, Instagram.ShareParams> {

        private ShareParamProvider provider;

        public InstagramParamBuilder(ShareParamProvider provider) {
            this.provider = provider;
        }

        @Override
        public Instagram.ShareParams call(Void aVoid) {
            Instagram.ShareParams params = new Instagram.ShareParams();
            params.setImageUrl(provider.getImgUrl());
            params.setTitleUrl(provider.getShareTitleUrl());
            params.setTitle(provider.getShareTitle());
            params.setText(provider.getShareText());
            params.setShareType(provider.getWechatShareType());
            params.setUrl(provider.getShareTitleUrl());
            return params;
        }
    }*/
}

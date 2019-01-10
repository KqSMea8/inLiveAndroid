package tw.chiae.inlive.presentation.ui.room.player;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding.view.RxView;
import tw.chiae.inlive.R;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.login.LoginSelectActivity;
import tw.chiae.inlive.presentation.ui.main.setting.SettingActivity;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.L;
import tw.chiae.inlive.util.share.ShareHelper;

import java.util.concurrent.TimeUnit;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.google.GooglePlus;

import cn.sharesdk.line.Line;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class RoomShareHelper extends ShareHelper
        /* implements PlatformActionListener*/
{

    private static final String LOG_TAG = "RoomShareHelper";

//    private String roomId;
    private View shareLayout;

    private String shareTitle;
    private String shareTitleUrl;
    private String shareText;
    private Context context;
    private String userPlayUrl;

    private ShareParamProvider shareParamProvider;
    private PlatformActionListener actionListener;
    private Action1<Void> onShareBegin;

    private LinearLayout mFacebook, mGoogle, mLine, mInstagram,mCopy;

    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    public RoomShareHelper(@NonNull final View shareLayout, String roomId, String anchorName,String imgUrl) {
//       this.roomId = roomId;
        this.shareLayout = shareLayout;
        context = shareLayout.getContext();
        shareTitle = context.getString(R.string.app_name);
        shareTitleUrl = context.getString(R.string.share_room_url, Const.MAIN_HOST_URL, roomId);
        L.i(LOG_TAG, "Share url=%s", shareTitleUrl);
        shareText = context.getString(R.string.share_room_text, anchorName);

        mFacebook = (LinearLayout)shareLayout.findViewById(R.id.player_share_ll_facebook);
        mGoogle = (LinearLayout)shareLayout.findViewById(R.id.player_share_ll_google);
        mLine = (LinearLayout)shareLayout.findViewById(R.id.player_share_ll_line);
        mInstagram = (LinearLayout)shareLayout.findViewById(R.id.player_share_ll_instagram);
        mCopy= (LinearLayout) shareLayout.findViewById(R.id.player_share_ll_copy);

        shareParamProvider = new RoomShareParam(shareLayout.getContext(), roomId, anchorName,imgUrl);
        actionListener = new DefaultShareListener();
        onShareBegin = new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                        //开始分享后自动隐藏
                        hideShareLayout();
            }
        };
        subscribeShareClick(mFacebook, Facebook.NAME, new FacebookParamBuilder(shareParamProvider));
        subscribeShareClick(mGoogle, GooglePlus.NAME, new GooglePlusParamBuilder(shareParamProvider));
        subscribeShareClick(mLine, Line.NAME, new LineParamBuilder(shareParamProvider));
        /*subscribeShareClick(mInstagram, Instagram.NAME, new InstagramParamBuilder(shareParamProvider));*/

        RxView.clicks(mCopy)
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        hideShareLayout();
                        if (userPlayUrl==null) {
                            toastShort(context,"暫無獲取到主播的直播地址請重試");
                            return;
                        }
                        android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboardManager.setText(userPlayUrl);
                        toastShort(context,context.getString(R.string.my_publish_path_copy));
                    }
                });
//        subscribeShareClick(mQQ, QQ.NAME, new Func1<Void, Platform.ShareParams>() {
//            @Override
//            public Platform.ShareParams call(Void aVoid) {
//                QQ.ShareParams params = new QQ.ShareParams();
//                params.setTitleUrl(shareTitleUrl);
//                params.setTitle(shareTitle);
//                params.setText(shareText);
//                return params;
//            }
//        });
//
//        subscribeShareClick(mQzone, QZone.NAME, new Func1<Void, Platform.ShareParams>() {
//            @Override
//            public Platform.ShareParams call(Void aVoid) {
//                QZone.ShareParams params = new QZone.ShareParams();
//                params.setTitleUrl(shareTitleUrl);
//                params.setTitle(shareTitle);
//                params.setText(shareText);
//                return params;
//            }
//        });
//
//        subscribeShareClick(mWeibo, SinaWeibo.NAME, new Func1<Void, Platform.ShareParams>() {
//            @Override
//            public Platform.ShareParams call(Void aVoid) {
//                SinaWeibo.ShareParams params = new SinaWeibo.ShareParams();
//                params.setTitleUrl(shareTitleUrl);
//                params.setTitle(shareTitle);
//                params.setText(shareText);
//                return params;
//            }
//        });
//
//        subscribeShareClick(mWechat, Wechat.NAME, new Func1<Void, Platform.ShareParams>() {
//            @Override
//            public Platform.ShareParams call(Void aVoid) {
//                Wechat.ShareParams params = new Wechat.ShareParams();
//                params.setTitleUrl(shareTitleUrl);
//                params.setTitle(shareTitle);
//                params.setShareType(Wechat.SHARE_WEBPAGE);
//                params.setUrl(shareTitleUrl);
//                params.setText(shareText);
//                return params;
//            }
//        });
//
//        subscribeShareClick(mWechatCircle, WechatMoments.NAME, new Func1<Void, Platform.ShareParams>() {
//            @Override
//            public Platform.ShareParams call(Void aVoid) {
//                WechatMoments.ShareParams params = new WechatMoments.ShareParams();
//                params.setTitleUrl(shareTitleUrl);
//                params.setTitle(shareTitle);
//                params.setText(shareText);
//                params.setShareType(Wechat.SHARE_WEBPAGE);
//                params.setUrl(shareTitleUrl);
//                return params;
//            }
//        });

    }

    public void setUserPlayUrl(String userPlayUrl) {
        this.userPlayUrl = userPlayUrl;
    }

    //    @Override
//    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//        L.i(LOG_TAG, "onComplete, platform=%s, i=%s, map=%s", platform.getName(), i, hashMap);
//    }
//
//    @Override
//    public void onError(Platform platform, int i, Throwable throwable) {
//        L.e(LOG_TAG, "onError, platform=%s, i=%s, throwable=%s", platform.getName(), i, throwable);
//    }
//
//    @Override
//    public void onCancel(Platform platform, int i) {
//        L.i(LOG_TAG, "onCancel, platform=%s, i=%s", platform.getName(), i);
//    }

    protected void showShareLayout(Context context){
        shareLayout.setVisibility(View.VISIBLE);
        Animation headerAnim= AnimationUtils.loadAnimation(context, R.anim.room_buttom_in);
        shareLayout.startAnimation(headerAnim);
    }

    protected void hideShareLayout(){
        shareLayout.setVisibility(View.INVISIBLE);
    }

    private <T extends Platform.ShareParams> void subscribeShareClick(final View view,
                                                                      final String platformName,
                                                                      final Func1<Void, T> shareParamBuilder){
        Subscription subscription = RxView.clicks(view)
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .map(shareParamBuilder)
                .subscribe(new Action1<T>() {
                    @Override
                    public void call(T t) {
                        toastShort(view.getContext(),context.getString(R.string.share_open_text));
                        //开始分享后自动隐藏
                        hideShareLayout();
                        invokeShare(platformName, actionListener, shareParamBuilder);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        toastShort(view.getContext(),context.getString(R.string.share_error_text));
                    }
                });
        mSubscriptions.add(subscription);
    }
//
//    public void unsubscribeAll(){
//        mSubscriptions.unsubscribe();
//    }
//
//    protected void toastShort(Context context, @NonNull String msg){
//        CustomToast.makeCustomText(context, msg, Toast.LENGTH_SHORT).show();
//    }


}

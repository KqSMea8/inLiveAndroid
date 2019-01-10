package tw.chiae.inlive.presentation.ui.base;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.presentation.ui.login.LoginActivity;
import tw.chiae.inlive.presentation.ui.login.LoginSelectActivity;
import tw.chiae.inlive.presentation.ui.login.splash.SplashActivity;
import tw.chiae.inlive.util.L;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observer;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public abstract class BaseObserver<E extends BaseResponse> implements Observer<E> {

    protected final String LOG_TAG = getClass().getSimpleName();

    private final BaseUiInterface mUiInterface;

    public BaseObserver(BaseUiInterface baseUiInterface) {
        mUiInterface = baseUiInterface;
    }



    @Override
    public void onCompleted() {
        mUiInterface.showLoadingComplete();
    }

    @Override
    public void onError(Throwable throwable) {
        L.e("BaseObserver", "Request Error!", throwable);
        handleError(throwable, mUiInterface, LOG_TAG);
    }

    /**
     * 按照通用规则解析和处理数据请求时发生的错误。这个方法在执行支付等非标准的REST请求时很有用。
     */
    public static void handleError(Throwable throwable, BaseUiInterface mUiInterface, String LOG_TAG){
        Log.i("mrl","這尼瑪哦"+throwable);
        mUiInterface.showLoadingComplete();
        if (throwable == null) {
            mUiInterface.showUnknownException();
            return;
        }
        //分为以下几类问题：网络连接，数据解析，客户端出错【空指针等】，服务器内部错误

        if (throwable instanceof SocketTimeoutException || throwable
                instanceof ConnectException || throwable instanceof UnknownHostException) {
            mUiInterface.showNetworkException();
        } else if ((throwable instanceof JsonSyntaxException) || (throwable instanceof
                NumberFormatException) || (throwable instanceof MalformedJsonException)) {
            //mUiInterface.showDataException("數據解析出錯");
        } else if ((throwable instanceof HttpException)) {
            mUiInterface.showDataException("服務器錯誤(" + ((HttpException) throwable).code()+")");
            //自动上报这个异常
            L.e(true, LOG_TAG, "Error while performing response!", throwable);
        } else if (throwable instanceof NullPointerException) {
            mUiInterface.showDataException("攻城獅正在修復中...");
            //自动上报这个异常
            L.e(true, LOG_TAG, "Error while performing response!", throwable);
        } else {
            mUiInterface.showUnknownException();
        }
    }

    @Override
    public void onNext(E response) {
        Log.i("mrl","服务器大概返回"+response);
        Log.i("RayCode","getCode"+response);
        switch (response.getCode()) {
            case BaseResponse.RESULT_CODE_SUCCESS:
                onSuccess(response);
                break;
            case BaseResponse.RESULT_CODE_TOKEN_EXPIRED:
                if (mUiInterface instanceof LoginActivity || mUiInterface instanceof SplashActivity){
                    onDataFailure(response);
                }
                else if (mUiInterface instanceof BaseActivity || mUiInterface instanceof BaseFragment){
                    final BaseActivity activity;
                    if (mUiInterface instanceof BaseFragment){
                        activity = (BaseActivity) ((BaseFragment)mUiInterface).getActivity();
                    }
                    else{
                        activity = (BaseActivity)mUiInterface;
                    }
                    AlertDialog alertDialog = new AlertDialog.Builder(activity)
                            .setMessage("你的賬號在別處登錄，你的請重新登錄")
                            .setPositiveButton("好", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    activity.startActivity(LoginSelectActivity.createIntent(activity));
                                    activity.sendFinishBroadcast(LoginSelectActivity.class.getSimpleName());
                                }
                            })
                            .create();
                    alertDialog.show();
                }
                else{
                    onDataFailure(response);
                }
                break;
            default:
                onDataFailure(response);
        }
    }

    public abstract void onSuccess(E response);

    protected void onDataFailure(E response) {
        String msg = response.getMsg();
        L.w(LOG_TAG, "request data but get failure:" + msg);
        if (!TextUtils.isEmpty(msg)) {
            mUiInterface.showDataException(response.getMsg());
        }
        else{
            mUiInterface.showUnknownException();
        }
    }

    /**
     * Create a new silence, non-leak observer.
     */
    public static <T> Observer<T> silenceObserver(){
        return new Observer<T>() {
            @Override
            public void onCompleted() {
                //Empty
            }

            @Override
            public void onError(Throwable e) {
                //Empty
            }

            @Override
            public void onNext(T t) {
                //Empty
            }
        };
    }

}
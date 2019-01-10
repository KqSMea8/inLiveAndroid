package tw.chiae.inlive.presentation.ui.base;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class BasePresenter<UiType extends BaseUiInterface> {

    private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    private UiType mUiInterface;

    protected BasePresenter(UiType uiInterface) {
        this.mUiInterface = uiInterface;
    }

    public void unsubscribeTasks() {
        mCompositeSubscription.unsubscribe();

    }

    /**
     * 每次发起时加入CompositeSubscription,这个方法应该内部调用，所以使用protected.
     */
    protected void addSubscription(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    protected UiType getUiInterface() {
        if (mUiInterface == null) {
            throw new IllegalStateException("UiInterface is not initialized correctly.");
        }
        return mUiInterface;
    }

    /**
     * Usage:
     * Observable.compose(applySchedulers)
     */
    protected final <T> Observable.Transformer<T, T> applyAsySchedulers() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}

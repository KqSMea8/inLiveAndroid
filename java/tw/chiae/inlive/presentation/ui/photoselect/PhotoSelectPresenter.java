package tw.chiae.inlive.presentation.ui.photoselect;

import android.content.Context;

import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.LoginInfo;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;

import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ymlong on Nov 18, 2015.
 */
class PhotoSelectPresenter extends BasePresenter {
    private final IPhotoSelect mUiInterface;
    private final PublishPhotoManager mPublishPhotoManager;

    public PhotoSelectPresenter(Context context, IPhotoSelect mUiInterface) {
        super(mUiInterface);
        this.mUiInterface = mUiInterface;
        mPublishPhotoManager = new PublishPhotoManager(context);
    }

    public void showPhotos() {
        Subscription subscription = mPublishPhotoManager.getPhotos()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ImageItem>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        mUiInterface.showUnknownException();
                    }

                    @Override
                    public void onNext(List<ImageItem> imageItems) {
                        mUiInterface.showPhotos(imageItems);
                    }
                });
        addSubscription(subscription);
    }

}

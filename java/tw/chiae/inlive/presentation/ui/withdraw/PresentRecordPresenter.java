package tw.chiae.inlive.presentation.ui.withdraw;

import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.PageBean;
import tw.chiae.inlive.data.bean.transaction.PresentRecordItem;
import tw.chiae.inlive.data.bean.transaction.WithDrawRespose;
import tw.chiae.inlive.domain.WithDrawManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;
import tw.chiae.inlive.presentation.ui.base.BaseUiInterface;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by huanzhang on 2016/5/13.
 */
public class PresentRecordPresenter extends BasePresenter<IPresentRecord>{
    private WithDrawManager mManager;
    protected PresentRecordPresenter(IPresentRecord uiInterface) {
        super(uiInterface);
        mManager = new WithDrawManager();
    }

    public void getPresentRecord(){
        Subscription subscription = mManager.getPresentRecord()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<List<PresentRecordItem>>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<List<PresentRecordItem>> response) {
                        getUiInterface().showList(response.getData());
                    }
                });
        addSubscription(subscription);
    }
}

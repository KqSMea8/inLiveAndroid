package tw.chiae.inlive.presentation.ui.base.page;

import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.PageBean;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public abstract class PagedPresenter<DataType, SubUiType extends PagedUiInterface<DataType>,
        ManagerType>
        extends BasePresenter<SubUiType> {
    private PageRecorder pageRecorder;
    private ManagerType manager;

    public PagedPresenter(SubUiType uiInterface) {
        super(uiInterface);
        manager = createManager();
        pageRecorder = new PageRecorder();
    }

    public void loadFirstPage() {
        Subscription subscription = callManager(manager, pageRecorder.getFirstPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<PageBean<DataType>>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<PageBean<DataType>> response) {
                        pageRecorder.moveToFirstPage();
                        getUiInterface().showData(response.getData().getList());
                    }
                });
        addSubscription(subscription);
    }

    public void loadNextPage() {
        Subscription subscription = callManager(manager, pageRecorder.getNextPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<PageBean<DataType>>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<PageBean<DataType>> response) {
                        pageRecorder.moveToNextPage();
                        getUiInterface().appendData(response.getData().getList());
                    }
                });
        addSubscription(subscription);
    }

    protected abstract ManagerType createManager();

    protected abstract Observable<BaseResponse<PageBean<DataType>>> callManager(ManagerType
                                                                                        manager,
                                                                                int pageNum);
}

package tw.chiae.inlive.presentation.ui.main.search;

import tw.chiae.inlive.data.bean.AnchorSummary;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.PageBean;
import tw.chiae.inlive.domain.AnchorManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;
import tw.chiae.inlive.presentation.ui.base.page.PageRecorder;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class SearchPresenter extends BasePresenter<SearchUiInterface>{

    private PageRecorder pageRecorder;
    private AnchorManager anchorManager;
    private String lastCondition;

    public SearchPresenter(SearchUiInterface uiInterface) {
        super(uiInterface);
        pageRecorder = new PageRecorder();
        anchorManager = new AnchorManager();
    }

    public void queryAnchors(final String condition,String type){
        Subscription subscription = anchorManager.mqueryAnchors(condition,type, pageRecorder.getFirstPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<PageBean<AnchorSummary>>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<PageBean<AnchorSummary>> response) {
                        lastCondition = condition;
                        List<AnchorSummary> list = response.getData().getList();
                        if (list==null || list.isEmpty()){
                            getUiInterface().showEmptyResult();
                        }
                        else {
                            getUiInterface().showData(list);
                        }
                    }
                });
        addSubscription(subscription);
    }

    public void queryNextPage(String type){
        Subscription subscription = anchorManager.mqueryAnchors(type,lastCondition,
                pageRecorder.getNextPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<PageBean<AnchorSummary>>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<PageBean<AnchorSummary>> response) {
                        List<AnchorSummary> list = response.getData().getList();
                        if (list==null || list.isEmpty()){
                            return ;
                        }
                        pageRecorder.moveToNextPage();
                        getUiInterface().appendData(list);
                    }
                });
        addSubscription(subscription);
    }

    public void followAnchor(String uid){
        //静默操作
        Subscription subscription = anchorManager.followAnchor(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<Object>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<Object> response) {
                        //Do nothing
                    }

                    @Override
                    protected void onDataFailure(BaseResponse<Object> response) {
                        super.onDataFailure(response);
                    }
                });
        addSubscription(subscription);
    }

    public void unfollowAnchor(String uid){
        //静默操作
        Subscription subscription = anchorManager.unfollowAnchor(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<Object>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<Object> response) {
                        //Do nothing
                    }

                    @Override
                    protected void onDataFailure(BaseResponse<Object> response) {
                        super.onDataFailure(response);
                    }
                });
        addSubscription(subscription);
    }
}

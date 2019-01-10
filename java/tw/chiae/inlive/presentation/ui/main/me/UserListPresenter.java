package tw.chiae.inlive.presentation.ui.main.me;

import tw.chiae.inlive.data.bean.AnchorSummary;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.PageBean;
import tw.chiae.inlive.domain.AnchorManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;
import tw.chiae.inlive.presentation.ui.base.page.PageRecorder;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by huanzhang on 2016/4/20.
 */
public class UserListPresenter extends BasePresenter<IUserList> {

    private PageRecorder pageRecorder;
    private AnchorManager mManager;

    protected UserListPresenter(IUserList uiInterface) {
        super(uiInterface);
        pageRecorder = new PageRecorder();
        mManager = new AnchorManager();
    }

    private Observable<BaseResponse<PageBean<AnchorSummary>>> queryByKey(int key, String uid, int
            pageNum) {
        if (key == UserListFragment.KEY_STAR) {
            return mManager.getAnchorFollowings(uid, pageNum);
        } else if (key == UserListFragment.KEY_FANS) {
            return mManager.getAnchorFollowees(uid, pageNum);
        } else {
            throw new UnsupportedOperationException("Unsupported type:" + key);
        }
    }

    public void queryFirstPage(String uid, int key) {
        Subscription subscription = queryByKey(key, uid, pageRecorder.getFirstPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<PageBean<AnchorSummary>>>(getUiInterface
                        ()) {
                    @Override
                    public void onSuccess(BaseResponse<PageBean<AnchorSummary>> response) {

                        List<AnchorSummary> list = response.getData().getList();
                        if (list == null || list.isEmpty()) {
                            getUiInterface().showEmptyResult();
                        } else {
                            getUiInterface().showData(list);
                        }
                    }
                });
        addSubscription(subscription);
    }

    public void queryNextPage(String uid, int key) {
        Subscription subscription = queryByKey(key, uid, pageRecorder.getNextPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<PageBean<AnchorSummary>>>(getUiInterface
                        ()) {
                    @Override
                    public void onSuccess(BaseResponse<PageBean<AnchorSummary>> response) {
                        List<AnchorSummary> list = response.getData().getList();
                        if (list == null || list.isEmpty()) {
                            return;
                        }
                        pageRecorder.moveToNextPage();
                        getUiInterface().appendData(list);
                    }
                });
        addSubscription(subscription);
    }

    public void starUser(String userId) {
        Subscription subscription = mManager.followAnchor(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<Object>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<Object> response) {
//                        mIUser.showUserInfo(response.getData());
                    }
                });
        addSubscription(subscription);
    }

    public void unStarUser(String userId) {
        Subscription subscription = mManager.unfollowAnchor(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<Object>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<Object> response) {
//                        mIUser.showUserInfo(response.getData());
                    }
                });
        addSubscription(subscription);
    }
}

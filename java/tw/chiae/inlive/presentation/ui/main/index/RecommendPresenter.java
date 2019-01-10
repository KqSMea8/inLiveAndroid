package tw.chiae.inlive.presentation.ui.main.index;

import tw.chiae.inlive.data.bean.AnchorSummary;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.HotAnchorPageBean;
import tw.chiae.inlive.data.bean.PageBean;
import tw.chiae.inlive.data.bean.ThemBean;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.domain.AnchorManager;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;
import tw.chiae.inlive.presentation.ui.base.page.PageRecorder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tw.chiae.inlive.presentation.ui.main.mergefilm.Log;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class RecommendPresenter extends BasePresenter<RecommendInterface> {

    private AnchorManager anchorManager;

    public RecommendPresenter(RecommendInterface uiInterface) {
        super(uiInterface);
        anchorManager = new AnchorManager();
    }

    public void loadRecommendAnchors(String token){
        Log.i("RayTest","loadRecommendAnchors!!");
        Subscription subscription = anchorManager.loadRecommendAnchors(token,"time", "20", "1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<PageBean<AnchorSummary>>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<PageBean<AnchorSummary>> response) {
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

    public void followAnchor(String userId) {
        Subscription subscription = anchorManager.followAnchor(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<Object>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<Object> response) {
                        //empty
                    }
                });
        addSubscription(subscription);
    }

    public void unfollowAnchor(String userId) {
        Subscription subscription = anchorManager.unfollowAnchor(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<Object>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<Object> response) {
                        //empty
                    }
                });
        addSubscription(subscription);
    }

    //    得到话题Bean
    public void getThemBean(String number){
        Subscription subscription = anchorManager.getThemBean(null,number)
                .compose(this.<BaseResponse<ThemBean>>applyAsySchedulers())
                .subscribe(new BaseObserver<BaseResponse<ThemBean>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<ThemBean> response) {
                        getUiInterface().onThemBean(response.getData());
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        getUiInterface().onThemBean(new ThemBean());
                    }
                });
        addSubscription(subscription);
    }

    //    获取私密
    public void loadPrivateLimit(String uid) {
        Subscription subscription = anchorManager.loadPrivateLimit(uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<PrivateLimitBean>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<PrivateLimitBean> response) {
                        getUiInterface().showPrivateLimit(response.getData());
                    }
                });
        addSubscription(subscription);
    }

    /**
     *
     * @param plid  私密限制id
     * @param prerequisite 用户输入的密码
     * @param uid 我的id
     * @param aid 主播id
     */
    public void checkPrivatePass(String type,int plid,String prerequisite,String uid,String aid){
        Subscription subscription = anchorManager.checkPrivatePass(type,plid,prerequisite,uid,aid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<Object>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<Object> response) {
                        getUiInterface().startGoPlayFragment();
                        try {
                            JSONObject jsonObject=new JSONObject(response.getData().toString());
                            LocalDataManager.getInstance().getLoginInfo().setTotalBalance(jsonObject.getLong("coinbalance"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        addSubscription(subscription);
    }

    public void loadAnchorsInfo(String token, String city, String sex) {
        Subscription subscription = anchorManager.loadHotAnchors(token, city, sex)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<HotAnchorPageBean>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<HotAnchorPageBean> response) {
                        getUiInterface().saveAnchorsInfoData(response.getData().getList());
                        AnchorsTmp.newInstance().setAnchorsInfo(response.getData().getList()); ;
                    }
                });
        addSubscription(subscription);
    }
}

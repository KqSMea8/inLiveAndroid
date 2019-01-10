package tw.chiae.inlive.presentation.ui.main.index;

import tw.chiae.inlive.data.bean.AnchorSummary;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.PageBean;
import tw.chiae.inlive.data.bean.ThemBean;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.domain.AnchorManager;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class CommendPresenter extends BasePresenter<CommendInterface> {

    private AnchorManager anchorManager;

    public CommendPresenter(CommendInterface uiInterface) {
        super(uiInterface);
        anchorManager = new AnchorManager();
    }

    public void loadCommendAnchors(String token,String city){
        Subscription subscription = anchorManager.loadCommendAnchors(token,city)
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
}

package tw.chiae.inlive.presentation.ui.main.topic;

import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.HotAnchorSummary;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.domain.AnchorManager;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;
import tw.chiae.inlive.presentation.ui.base.page.PageRecorder;
import tw.chiae.inlive.presentation.ui.main.index.FollowedAnchorInterface;
import tw.chiae.inlive.util.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/7/26 0026.
 */
public class NewsTopicPresenter extends BasePresenter<FollowedAnchorInterface> {
    private AnchorManager anchorManager;
    private PageRecorder pageRecorder;

    protected NewsTopicPresenter(FollowedAnchorInterface uiInterface) {
        super(uiInterface);
        anchorManager = new AnchorManager();
        pageRecorder = new PageRecorder();
    }

    public void loadFirstTopic(int topicID) {
        Subscription subscription = anchorManager.loadTopicLives(topicID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<List<HotAnchorSummary>>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<List<HotAnchorSummary>> response) {
                        pageRecorder.moveToFirstPage();
                        L.i("Presenter", response.toString());
                        getUiInterface().showData(response.getData());
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

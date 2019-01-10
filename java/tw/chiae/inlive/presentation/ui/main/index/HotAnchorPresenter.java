package tw.chiae.inlive.presentation.ui.main.index;

import android.net.Uri;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;

import rx.Observer;
import tw.chiae.inlive.data.bean.Banner;
import tw.chiae.inlive.data.bean.BaseResponse;
import tw.chiae.inlive.data.bean.HotAnchorPageBean;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.domain.AnchorManager;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.base.BaseObserver;
import tw.chiae.inlive.presentation.ui.base.BasePresenter;
import tw.chiae.inlive.presentation.ui.base.page.PageRecorder;
import tw.chiae.inlive.presentation.ui.base.page.PagedPresenter;
import tw.chiae.inlive.presentation.ui.main.mergefilm.EventActivity;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.FrescoUtil;
import tw.chiae.inlive.util.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author Muyangmin
 * @since 1.0.0
 * @see PagedPresenter
 */
public class HotAnchorPresenter extends BasePresenter<HotAnchorInterface> {

    private AnchorManager anchorManager;
    private PageRecorder pageRecorder;

    public HotAnchorPresenter(HotAnchorInterface uiInterface) {
        super(uiInterface);
        anchorManager = new AnchorManager();
        pageRecorder = new PageRecorder();
    }

    public void onBannerClicked(BaseActivity activity){

    }

//    第一张
    public void loadFirstPage() {
        Subscription subscription = anchorManager.loadHotAnchors(pageRecorder.getFirstPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<HotAnchorPageBean>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<HotAnchorPageBean> response) {

                        pageRecorder.moveToFirstPage();
                        getUiInterface().displayBanners(response.getData().getBanner());
                        L.i("Presenter", response.toString());
                        getUiInterface().showData(response.getData().getList());
                        AnchorsTmp.newInstance().setAnchorsInfo(response.getData().getList()); ;
                    }
                });
        addSubscription(subscription);
    }

    //    第一张
    public void loadFirstPage(String token,String city,String sex) {
        Subscription subscription = anchorManager.loadHotAnchors(token, city, sex)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<HotAnchorPageBean>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<HotAnchorPageBean> response) {
                        pageRecorder.moveToFirstPage();
                        List<Banner> banners = response.getData().getBanner();
                        getUiInterface().displayBanners(banners);
                        L.i("Presenter", response.toString());
                        getUiInterface().showData(response.getData().getList());
                        AnchorsTmp.newInstance().setAnchorsInfo(response.getData().getList()); ;
                    }
                });
        addSubscription(subscription);
    }

//    第二张
    public void loadNextPage() {
        Subscription subscription = anchorManager.loadHotAnchors(pageRecorder.getNextPage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<BaseResponse<HotAnchorPageBean>>(getUiInterface()) {
                    @Override
                    public void onSuccess(BaseResponse<HotAnchorPageBean> response) {
                        pageRecorder.moveToNextPage();
                        if(response.getData()!=null) {
                            getUiInterface().displayBanners(response.getData().getBanner());
                            getUiInterface().appendData(response.getData().getList());
                        }
                    }
                });
        addSubscription(subscription);
    }

    /**
     * 获取私密
     * @param uid
     */
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

    public void checkActivateEvent() {
        Log.i("RayTest","檢查活動事件");
        Subscription subscription = anchorManager.checkActivateEvent()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EventActivity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(EventActivity eventActivity) {
                        getUiInterface().UpdateActivateEvent(eventActivity);
                    }
                });
        addSubscription(subscription);
    }

    public void CacheAllImage(final List<Banner> banners) {
        Log.i("RayTest","CacheAllImage....");
        final int iSize = banners.size();
        final List<String> paths = new ArrayList<>();
        //removeOldBannerCache();
        for(Banner banner :banners){
            FrescoUtil.CacheImgToDisk(banner.getImageUrl(),new FrescoUtil.CacheCallbacek(){
                @Override
                public void cachePath(String path) {
                    paths.add(path);
                    Log.i("RayTest","[========path========]:"+path);
                    if(paths.size()==iSize){
                        LocalDataManager.getInstance().saveBanners(banners);
                        getUiInterface().CompleteDownloadBanner(banners);
                    }
                }


            },true);
        }

    }

    public void removeOldBannerCache() {

      for(Banner banner:LocalDataManager.getInstance().getBanners() )  {
          String imgurl = Const.MAIN_HOST_URL+banner.getImageUrl();
          FrescoUtil.removeDiskCache(Uri.parse(imgurl));
      }
    }
}

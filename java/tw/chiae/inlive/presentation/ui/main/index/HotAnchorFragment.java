package tw.chiae.inlive.presentation.ui.main.index;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nineoldandroids.animation.ValueAnimator;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import me.grantland.widget.AutofitTextView;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.Banner;
import tw.chiae.inlive.data.bean.HotAnchorSummary;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseFragment;
import tw.chiae.inlive.presentation.ui.base.ptr.BasePtr;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerHolder;
import tw.chiae.inlive.presentation.ui.main.index.privateutil.GoPrivateRoom;
import tw.chiae.inlive.presentation.ui.main.index.privateutil.GoPrivateRoomInterface;
import tw.chiae.inlive.presentation.ui.main.me.OtherUserActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.EventActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.KSWebActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.KaraStar;
import tw.chiae.inlive.presentation.ui.main.mergefilm.MediaPlayerActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.MediaRecorderActivity;
import tw.chiae.inlive.presentation.ui.main.mergefilm.VCamera;
import tw.chiae.inlive.presentation.ui.main.mergefilm.model.FileUtils;
import tw.chiae.inlive.presentation.ui.main.webview.SimpleWebViewActivity;
import tw.chiae.inlive.presentation.ui.room.RoomActivity;
import tw.chiae.inlive.presentation.ui.room.create.PrivateSetStringDialog;
import tw.chiae.inlive.presentation.ui.room.player.PlayerFragment;
import tw.chiae.inlive.presentation.ui.widget.AdaptiveTextView;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.FrescoUtil;
import tw.chiae.inlive.util.L;
import tw.chiae.inlive.util.Spans;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class HotAnchorFragment extends BaseFragment implements HotAnchorInterface, GoPrivateRoomInterface {

    private static final long AUTO_REFRESH_TIME = 10;
    private static final String CHECK_HOTPOINT = "HOTPOINT_REQUEST" ;
    private static final String CHECK_EVENT = "EVENT_REQUEST" ;
    private PtrFrameLayout ptrFrameLayout;
    private RecyclerView recyclerView;
    private HotAnchorPresenter presenter;
    private HotAnchorAdapter adapter;
    private ScheduledExecutorService autoRefreshService;
    //    地区标题
    private String city;
    //    性别
    private String sex;
    // 私密房间的dialog
    private GoPrivateRoom goPrivateRoom;
    // 当前选中的用户
    private HotAnchorSummary hotAnchorSummary;
    private PrivateLimitBean mbean;
    //private RequestQueue requestqueue;
    //private List<HotAnchorSummary> finalList;
    /*private long startTime;
    private Handler timehandler = new Handler();
    private StringRequest HotPointRequest;
    private HotPointRequestListener HotPointResponse;
    private HotPointRequestErrorListener HotPointErrorResponse;*/
    //private EventRequestListener EventResponse;
    //private EventRequestErrorListener EventErrorResponse;
    //private StringRequest EventRequest;
    private View BannerView;
    private HotAnchorHeaderHolder HotAnchorHeaderHolderView;
    private int currentIndex = 0;
    private ScheduledFuture<?> scheduledFuture;
    private EventActivity eventsInfo;
    private boolean onRefreshData = false;
    private boolean isOnResume = false;
    private ValueAnimator valueAnimator;
//    private MyTimerTask task;
//    private Timer timer;


/*    private static class HotPointRequestListener implements com.android.volley.Response.Listener<String> {

        private final HotAnchorFragment mfragment;
        private final HotAnchorAdapter adapter;
        private List<HotAnchorSummary> list;

        public HotPointRequestListener(HotAnchorFragment fragment, HotAnchorAdapter ada) {
            this.mfragment = fragment;
            //this.list = mfragment.finalList;
            this.adapter = ada;
            if(list==null)
                list = new ArrayList<>();
        }
        @Override
        public void onResponse(String sResponse) {
            try {
                JSONArray jsonArray = new JSONArray(sResponse);
                for (int index = 0; index < jsonArray.length(); index++) {
                    String hotItem = jsonArray.getJSONObject(index).toString();
                    HotPointInfo hotpointInfo = new Gson().fromJson(hotItem, HotPointInfo.class);
                    mfragment.updateHotPointView(hotpointInfo.getUid(), hotpointInfo.getHotpoint());
                }
               // mfragment.adapter.notifyDataSetChanged();
                mfragment.adapter.setDataList(list);
            } catch (JSONException e) {
                Log.i("RayTest", "JSONException " + e.toString());

            } catch (NumberFormatException timee) {
                Log.i("RayTest", "NumberFormatException " + timee.toString());
            }
        }
    }

    private static class HotPointRequestErrorListener implements com.android.volley.Response.ErrorListener{


        @Override
        public void onErrorResponse(VolleyError volleyError) {

        }
    }*/

/*
    private static class EventRequestListener implements com.android.volley.Response.Listener<String> {

        private final HotAnchorFragment mfragment;
        private final int startEventId;

        public EventRequestListener(HotAnchorFragment fragment, int EventId) {
            this.mfragment = fragment;
            this.startEventId = EventId;
        }
        @Override
        public void onResponse(String response) {
            Gson gson = new Gson();
            Log.i("RayTest","response:"+response);
            EventActivity eventList = gson.fromJson(response, EventActivity.class);
            for (EventActivity.EventItem event : eventList.getEvents()) {
                //處理活動事件
                Log.i("RayTest","need start event id :"+startEventId);

                if ((event.getId() == 1 && event.isViewable() == 1) && startEventId ==0) {
                    mfragment.startKsEvent();
                } else if((event.getId() == startEventId && event.isViewable() == 1)){
                    Log.i("RayTest","start event id :"+startEventId);
                    mfragment.starActivityEvent(startEventId, LocalDataManager.getInstance().getLoginInfo().getUserId());
                }

            }
        }
    }*/

   /* private static class EventRequestErrorListener implements com.android.volley.Response.ErrorListener{


        @Override
        public void onErrorResponse(VolleyError volleyError) {

        }
    }
*/

    public static HotAnchorFragment newInstance() {
        return new HotAnchorFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_hot_anchor_list;
    }

    @Override
    protected void initViews(View view) {
        city = getString(R.string.index_tab_hot);
//        版本更新
        presenter = new HotAnchorPresenter(this);
        ptrFrameLayout = $(view, R.id.hot_anchor_ptr);
        ptrFrameLayout.disableWhenHorizontalMove(true);
        //设置为不可上拉加载的样式，只需要禁用掉上拉Feature即可，不需要改动逻辑代码。
//        BasePtr.setPagedPtrStyle(ptrFrameLayout);
        BasePtr.setRefreshOnlyStyle(ptrFrameLayout);
        recyclerView = $(view, R.id.hot_anchor_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        presenter.checkActivateEvent();

        if (adapter == null) {
            adapter = new HotAnchorAdapter(new ArrayList<HotAnchorSummary>());
            recyclerView.setAdapter(adapter);
        }
        if(LocalDataManager.getInstance().getBanners()==null)
            Log.i("RayTest","getBanners:null");
        adapter.setBannerList(LocalDataManager.getInstance().getBanners());

    }

    private void setupPtrFrameLayout() {
        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler2() {

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return super.checkCanDoRefresh(frame, recyclerView, header);
            }

            @Override
            public boolean checkCanDoLoadMore(PtrFrameLayout frame, View content, View footer) {
                return super.checkCanDoLoadMore(frame, recyclerView, footer);
            }

            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
//                加载第二章
                presenter.loadNextPage();
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
//                加载啊第一张
                Log.i("RayTest", "onRefreshBegin");
                onRefreshData = true;
                presenter.loadFirstPage(LocalDataManager.getInstance().getLoginInfo().getToken(), city, sex);
            }
        });
        //ptrFrameLayout.autoRefresh();
    }

 /*   private void SetupRequest(int StartEventId) {
        if(adapter==null){
            Log.i("RayTest","adapter null");
            return;
        }

        EventResponse = new EventRequestListener(this,StartEventId);
        EventErrorResponse = new EventRequestErrorListener();

        EventRequest = new StringRequest(Const.checkServerUrl, EventResponse,EventErrorResponse);
        EventRequest.setTag(CHECK_EVENT);
    }*/



    @Override
    public void onResume() {
        super.onResume();
        isOnResume = true;
        RefreshData();
        if(HotAnchorHeaderHolderView!=null)

            {
                HotAnchorHeaderHolderView.StartBannerAnim();
            }

        if(valueAnimator!=null && !valueAnimator.isRunning())
            valueAnimator.start();
      /*  task = new MyTimerTask(this);
        if(timer==null)
            timer = new Timer();

        timer.schedule(task, 0, 10000);*/
        }

    private void RefreshData() {
        if (presenter != null ) {
            presenter.loadFirstPage(LocalDataManager.getInstance().getLoginInfo().getToken(), getString(R.string.index_tab_hot), "");
        }
    }


    ;

    /* private static class MyTimerTask extends TimerTask {
         private HotAnchorFragment mFragment;
         private String mRoomUserUid;

         public MyTimerTask(HotAnchorFragment fragment  ) {
             this.mFragment = fragment;
         }

         @Override
         public void run() {
             if(mFragment==null)
                 return;
             mFragment.updateHotPoint();
             if (mFragment.presenter != null) {
                 mFragment.presenter.loadFirstPage(LocalDataManager.getInstance().getLoginInfo().getToken(), mFragment.getString(R.string.index_tab_hot), "");
             }
         }
     }*/
    @Override
    public void onPause() {
//        MobclickAgent.onPageStart("主页-热门");
        super.onPause();
        isOnResume = false;
        if(valueAnimator!=null && valueAnimator.isRunning())
            valueAnimator.cancel();
       /* scheduledFuture.cancel(true);
        autoRefreshService.shutdownNow();
        autoRefreshService = null;
        scheduledFuture = null;*/
        //stopLoopTimer();
        if(HotAnchorHeaderHolderView!=null){
            HotAnchorHeaderHolderView.cancelBannerAnim();
        }
        L.i(LOG_TAG, "Auto refreshing service has been shutdown.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isOnResume = false;
        presenter.unsubscribeTasks();
        L.i("RayTest", "onDestroy");
        //HotPointRequest = null;
       /* requestqueue.cancelAll(CHECK_HOTPOINT);
        requestqueue.cancelAll(CHECK_EVENT);*/
        //stopLoopTimer();
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public void showLoadingComplete() {
        ptrFrameLayout.refreshComplete();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("RayTest", "HotAnchorFagment onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.i("RayTest", "onAttachFragment");
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    //    banners
    @Override
    public void displayBanners(final List<Banner> banners) {
       //adapter.setBannerList(banners);
        if(CheckBanner(banners))
            presenter.CacheAllImage(banners);
    }

    private boolean CheckBanner(List<Banner> banners) {
        List<Banner> oldBanner = adapter.getBannerList();
        if((oldBanner.size()!=banners.size())|| onRefreshData){
            onRefreshData = false;
            return true;
        }

        return false;
    }

    @Override
    public void showData(List<HotAnchorSummary> list) {
        if (adapter == null) {
            adapter = new HotAnchorAdapter(list);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setDataList(list);
        }
    }

    @Override
    public void appendData(List<HotAnchorSummary> list) {
        Log.i("RayTest","====================");
        for(HotAnchorSummary hotAnchorSummary : list){
            Log.i("RayTest","snap: "+hotAnchorSummary.getSnap()+"    av:"+hotAnchorSummary.getAvatar());
        }
        Log.i("RayTest","====================");
        adapter.appendData(list);
    }

    @Override
    public void showPrivateLimit(PrivateLimitBean bean) {
        mbean = bean;
        dismissLoadingDialog();
        if (bean.getCome() == 0 && bean.getPtid() != 0) {
            if (bean.getPtid() == Integer.valueOf(PrivateSetStringDialog.PRIVTE_ROOM_LEVEL)) {
                //如果等级够了，直接调用验证接口
                if (Integer.valueOf(bean.getPrerequisite()) <= Integer.valueOf(LocalDataManager.getInstance().getLoginInfo().getLevel())) {
                    presenter.checkPrivatePass(PrivateSetStringDialog.PRIVTE_ROOM_LEVEL, bean.getId(), "", LocalDataManager.getInstance().getLoginInfo().getUserId(), hotAnchorSummary.getId());
                    return;
                }
            }
            /*if (goPrivateRoom == null)
                goPrivateRoom = new GoPrivateRoom();
            goPrivateRoom.setGoPrivateRoomInterface(HotAnchorFragment.this);
            Bundle bundle = new Bundle();
            bundle.putInt(GoPrivateRoom.GO_PLID, bean.getId());
            bundle.putString(GoPrivateRoom.GO_PRIVATE_TYPE, String.valueOf(bean.getPtid()));
            bundle.putString(GoPrivateRoom.GO_PRIVATE_CONTE, bean.getPrerequisite());
            bundle.putString(GoPrivateRoom.GO_NAME, hotAnchorSummary.getNickname());
            bundle.putString(GoPrivateRoom.GO_PHOTO, hotAnchorSummary.getAvatar());
            bundle.putString(GoPrivateRoom.GO_USER_ID, hotAnchorSummary.getId());
            bundle.putString(GoPrivateRoom.GO_LAYOU_BG, hotAnchorSummary.getSnap());
            goPrivateRoom.setArguments(bundle);
            goPrivateRoom.show(getActivity().getFragmentManager(), "dasdas");*/
        } else
            startPlayFragment();
    }

    /**
     * 进入直播间的总操作
     */
    @Override
    public void startGoPlayFragment() {
        startPlayFragment();
    }

    @Override
    public void UpdateActivateEvent(EventActivity eventActivity) {
        this.eventsInfo = eventActivity;
        setupPtrFrameLayout();
    }

    @Override
    public void CompleteDownloadBanner(List<Banner> banners) {
        Log.i("RayTest","banner 下載完成");
        if(banners!=null )
            adapter.setBannerList(banners);
    }

    @Override
    public void questGoPrivateRoom(String type, int msg, String userid, String pwd) {
        presenter.checkPrivatePass(type, msg, pwd, LocalDataManager.getInstance().getLoginInfo().getUserId(), userid);
    }

    @Override
    public void onCancelDialogcheck(Bundle mArgs) {

    }

    private class BannerHolder implements Holder<Banner> {

        private SimpleDraweeView drawee;

        @Override
        public View createView(Context context) {
            drawee = new SimpleDraweeView(context);
            drawee.setScaleType(ImageView.ScaleType.FIT_CENTER);
            return drawee;
        }

        @Override
        public void UpdateUI(Context context, int position, Banner data) {
            if (TextUtils.isEmpty(data.getImageUrl())) {
                drawee.setImageURI(Uri.EMPTY);
            } else {
                Uri imgUri = FrescoUtil.getCache(Const.MAIN_HOST_URL+data.getImageUrl());
                if(imgUri==null) {
                    //Log.i("RayTest","使用內存："+data.getImageUrl());
                    drawee.setImageURI(SourceFactory.wrapPathToUri(data.getImageUrl()));
                } else{
                    //Log.i("RayTest","使用Disk緩存："+data.getImageUrl());
                    drawee.setImageURI(imgUri);}
            }
        }
    }

    private class HotAnchorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_HEADER = 1;
        private static final int TYPE_CONTENT = 2;

        private List<HotAnchorSummary> dataList;
        private List<Banner> bannerList;

        public HotAnchorAdapter(List<HotAnchorSummary> dataList) {
            this.dataList = dataList;
        }

        public final void setDataList(List<HotAnchorSummary> dataList) {
            this.dataList = dataList;
            notifyDataSetChanged();
        }

        public final void appendData(List<HotAnchorSummary> appends) {
            this.dataList.addAll(appends);

            notifyDataSetChanged();
        }

        public void setBannerList(List<Banner> banners) {

            if(banners.size()==0)
                this.bannerList = new ArrayList<>();
            else
                this.bannerList = banners;
            Log.i("RayTest","banners size:"+bannerList.size());

            notifyDataSetChanged();
        }

        public List<Banner> getBannerList(){
            return bannerList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view;
            if (viewType == TYPE_CONTENT) {
                view = inflater.inflate(R.layout.item_hot_anchor, parent, false);
                return new HotAnchorHolder(view);
            } else {
                if(BannerView==null) {
                    BannerView = inflater.inflate(R.layout.header_hot_anchor, parent, false);
                }
                if(HotAnchorHeaderHolderView ==null)
                    HotAnchorHeaderHolderView= new HotAnchorHeaderHolder(BannerView);
                return HotAnchorHeaderHolderView;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == TYPE_CONTENT) {
                ((HotAnchorHolder) holder).displayData(dataList.get(position - 1));
            } else {
                ((HotAnchorHeaderHolder) holder).displayBanner(bannerList);
                ((HotAnchorHeaderHolder) holder).setCurrentBanner(currentIndex);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_HEADER;
            }
            return TYPE_CONTENT;
        }

        @Override
        public int getItemCount() {
            if (dataList == null)
                return 1;
            /*return dataList.size() + 1;*/
            return dataList.size() + 1;
        }

        public void setBannerListTmp(List<Banner> banners) {
            /*boolean isDataChange = false;
            for(Banner banner : banners) {
                if (bannerList.contains(banner.getImageUrl()))
                    isDataChange = true;
            }
            if(!isDataChange)
                setBannerList(banners);*/
        }
    }

    float moveX, moveY;
    static class BannerHandler extends Handler {
        private WeakReference<HotAnchorFragment> mFragment;


        BannerHandler(HotAnchorFragment fragment) {
            mFragment = new WeakReference<HotAnchorFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            HotAnchorFragment fragment = mFragment.get();
            if (fragment != null && fragment.isOnResume) {
                fragment.RefreshData();
            }
        }
    }

    private Handler mHandler = new BannerHandler(this);
    private class HotAnchorHeaderHolder extends RecyclerView.ViewHolder {

        private ConvenientBanner<Banner> cvBanner;
        private CBViewHolderCreator cbViewHolder;

        public HotAnchorHeaderHolder(View itemView) {
            super(itemView);
            Log.i("RayTest","HotAnchorHeaderHolder");
            cvBanner = $(itemView, R.id.hot_anchor_banner);
            cvBanner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    if(position==cvBanner.getChildCount()-1){
                        mHandler.sendEmptyMessage(1);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }
        public void setCurrentBanner(int pos){
            if( pos <=0)
                pos=0;
            cvBanner.setcurrentitem(pos);
        }

        public void displayBanner(final List<Banner> banners) {
            if(cbViewHolder==null) {
                cbViewHolder = getCBViewHolder(banners);
            }

            cvBanner.setPages(cbViewHolder, banners)
                    .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R
                            .drawable.ic_page_indicator_focused})
                    .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                    .setManualPageable(true);
        }
        public void cancelBannerAnim(){
            if(cvBanner!=null){
                cvBanner.stopTurning();
            }
        }

        public void StartBannerAnim(){
            if(cvBanner!=null){
                cvBanner.startTurning(5000);
            }
        }


        private CBViewHolderCreator getCBViewHolder(final List<Banner> banners) {
            CBViewHolderCreator cv = new CBViewHolderCreator() {


                public BannerHolder viewholder;

                @Override
                public Object createHolder() {
                    cvBanner.startTurning(5000);
                    //showBanner(banners);
                    cvBanner.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
//                            banana显示网页
                            if(banners==null ||banners.size()<=0)
                                return;
                            String TargetUrl = banners.get(position).getTargetUrl();
                            String name = banners.get(position).getImageUrl();
                            Log.i("RayTest","BannerHolder onClick"+ position+"  Target:"+TargetUrl+" name:"+name);
                            if(TargetUrl.contains("https://")||TargetUrl.contains("http://")){
                                startActivity(SimpleWebViewActivity.createIntent(getActivity(),
                                        SourceFactory.wrapPath(banners.get(position).getTargetUrl()),""));
                            }else{
                                startActivateEvent(Integer.parseInt(TargetUrl));
                            }

                            /*if (position == 0) {
                                checkActivateEvent(1, banners.get(position).getTargetUrl());
                            } else {
                                startActivity(SimpleWebViewActivity.createIntent(getActivity(),
                                        SourceFactory.wrapPath(banners.get(position).getTargetUrl())));
                            }*/

                        }
                    });

                    if(viewholder==null) {
                        viewholder = new BannerHolder();
                    }
                    return viewholder;
                }

                private void showBanner(List<Banner> banners) {
                    for(Banner banner : banners){
                        Log.i("RayTest","=========="+banner.getImageUrl()+" target:"+banner.getTargetUrl());
                    }
                }
            };
            return cv;
        }

    }

   /* private void checkEvent(final List<Banner> banners, final int position) {
        Log.i("Raytest", "checkEvent");

        StringRequest sr = new StringRequest(Const.checkServerUrl, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("RayTestEvent", response);
                Gson gson = new Gson();
                EventActivity eventList = gson.fromJson(response, EventActivity.class);
                for (EventActivity.EventItem event : eventList.getEvents()) {
                    //處理活動事件
                    //Log.i("RayTestEvent", "isActive:" + event.isActive()+event.getId());
                    Log.i("RayTestEvent", "id:" + event.getId() + " name:" + event.getName() + "  url:" + event.getEventUrl());
                    switch (event.getId()) {
                        case 3:
                            Log.i("RayTestEvent", "isViewable:" + event.isViewable());
                            if (event.isViewable() == 1)
                                //CEWebKit.getInstance().open(getActivity(),6);
                                break;
                        case 4:
                            if (event.isViewable() == 1)
                                // CEWebKit.getInstance().open(getActivity(),4);
                                break;
                        default:

                            break;
                    }
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                tw.chiae.inlive.presentation.ui.main.mergefilm.Log.i("RayTest", "onErrorResponse:" + volleyError.toString());
            }
        });
        requestqueue.add(sr);
    }*/


    private class HotAnchorHolder extends SimpleRecyclerHolder<HotAnchorSummary> {

        private AutofitTextView hot_point_value;
        private SimpleDraweeView drawSnap;
        private TextView tvNickname;
        private TextView tvLocation;
        private TextView tvOnlineCount;
        private TextView note;
        private SimpleDraweeView drawAvatar;
        private TextView tvTitle;
        private TextView tvTopic;
        private TextView live_type;
        private ImageView hot_point;
        private ImageView approveid_value;
        DecimalFormat mDecimalFormat = new DecimalFormat("#,###");
        private int[] types = {R.drawable.tag_star, R.drawable.tag_gold, R.drawable.tag_office, R.drawable.tag_sp};
        private int[] res = {R.drawable.fireicon_01, R.drawable.fireicon_02, R.drawable.fireicon_03, R.drawable.fireicon_04, R.drawable.fireicon_05, R.drawable.fireicon_06, R.drawable.fireicon_07, R.drawable.fireicon_08, R.drawable.fireicon_09, R.drawable.fireicon_10};

        @SuppressWarnings("unused")
        public HotAnchorHolder(View itemView) {
            super(itemView);
            drawAvatar = (SimpleDraweeView) itemView.findViewById(R.id.img_user_avatar);
            drawSnap = (SimpleDraweeView) itemView.findViewById(R.id
                    .item_hot_anchor_img_front_cover);
            tvNickname = (TextView) itemView.findViewById(R.id.item_hot_anchor_tv_nickname);
            tvLocation = (TextView) itemView.findViewById(R.id.item_hot_anchor_tv_location);
            tvOnlineCount = (TextView) itemView.findViewById(R.id.item_hot_anchor_tv_online_count);
            tvTitle = (TextView) itemView.findViewById(R.id.item_hot_anchor_title);
            tvTopic = (TextView) itemView.findViewById(R.id.item_hot_anchor_topic);
            live_type = (TextView) itemView.findViewById(R.id.live_type);
            hot_point = (ImageView) itemView.findViewById(R.id.iv_hot_point);
            hot_point_value = (AutofitTextView) itemView.findViewById(R.id.tv_hot_point);
            approveid_value = (ImageView) itemView.findViewById(R.id.iv_hot_approveid_type);
        }

        @Override
        public void displayData(final HotAnchorSummary data) {
            if (data.getBroadcasting() != null) {
                if (data.getBroadcasting().equals("y")) {
                    live_type.setBackgroundResource(R.drawable.live_type_on);
                } else {
                    live_type.setBackgroundResource(R.drawable.live_type_off);
                    //data.setHotpoint("0");
                }
            }
//                if (data.getOnline()==0) {
//                    live_type.setText("休息中");
//                } else {
//                    live_type.setText("直播中");
//                }

            if (data.getRoomTitle() == null) {
                tvTitle.setVisibility(View.INVISIBLE);
            } else {
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText(data.getRoomTitle());
            }
            if (data.getTopic() == null) {
                tvTopic.setVisibility(View.GONE);
            } else {
                tvTopic.setVisibility(View.VISIBLE);
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < data.getTopic().size(); i++) {
                    stringBuffer.append("#" + data.getTopic().get(i).getTitle() + "#");
                }
                tvTopic.setText(stringBuffer.toString());
            }
            //drawAvatar.setImageURI(SourceFactory.wrapPathToUri(data.getAvatar()));
            drawSnap.setImageURI(SourceFactory.wrapPathToUri(data.getSnap()));
            tvNickname.setText(data.getNickname());
            tvLocation.setText(data.getCity());
            tvOnlineCount.setText(Spans.createSpan("", String.valueOf(data.getOnlineCount()), "", new
                            ForegroundColorSpan
                            (ContextCompat.getColor(getContext(), R.color.yunkacolor)),
                    new RelativeSizeSpan(1.6F)));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hotAnchorSummary = data;
                    showLoadingDialog();
                    presenter.loadPrivateLimit(data.getId());

                }
            });
            int pprovetype = getTypeValue(data.getApproveid());
            if (pprovetype == 0)
                approveid_value.setVisibility(View.GONE);
            switch (pprovetype) {
                case 1:
                    approveid_value.setImageResource(types[0]);
                    break;
                case 2:
                    approveid_value.setImageResource(types[1]);
                    break;
                case 3:
                    approveid_value.setImageResource(types[2]);
                    break;
                case 4:
                    approveid_value.setImageResource(types[3]);
                    break;
                default:
                    approveid_value.setVisibility(View.GONE);
                    break;
            }
            final double value = Double.parseDouble(data.getHotpoint());
            mDecimalFormat.format((double) value);
            setTextAnim(value);
            //hot_point_value.setText(mDecimalFormat.format((double) value) + "");
            /*hot_point_value.setText(data.getHotpoint());*/
       /*     valueAnimator = ValueAnimator.ofInt(0, 9);
            valueAnimator.setDuration(550);
            valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    hot_point.setImageResource(res[value]);
                }
            });
            valueAnimator.start();*/
            //TODO note
        }

        private int getTypeValue(String approveid) {
            if (approveid.equals("星級藝人"))
                return 1;
            if (approveid.equals("金牌藝人"))
                return 2;
            if (approveid.contains("官方"))
                return 3;
            if (approveid.contains("特約"))
                return 4;
            return 0;
        }

        private void setTextAnim(double value) {
/*            int starValue;
            int endValue = (int) value;
            String strStart = (String) hot_point_value.getText();*/
            hot_point_value.setText(mDecimalFormat.format( value) + "");
           /* if(strStart.equals("0"))
                hot_point_value.setText(mDecimalFormat.format( value) + "");
            else{
                try {
                    starValue = mDecimalFormat.parse(strStart).intValue();
                    ValueAnimator anim = ValueAnimator.ofInt(starValue, endValue);
                    anim.setDuration(500) ;
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int val = (int) animation.getAnimatedValue();
                            mDecimalFormat.format((double) val);
                            hot_point_value.setText(mDecimalFormat.format((double) val) + "");
                        }
                    });
                    anim.start();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }*/
        }
    }


  /*  private void updateHotPoint() {
        if(EventRequest==null)
            SetupRequest();

    }*/

/*    private void updateHotPointView(long uid, long hotpoint) {

        if(finalList==null){
            finalList = new ArrayList<>();
        }
        for (HotAnchorSummary list : finalList) {
            if (list.getId().equals(Long.toString(uid))) {
                Log.i("RayTest","Name:"+list.getNickname()+ list.getHotpoint()+"===>"+"val:"+hotpoint );
                list.setHotpoint(Long.toString(hotpoint));
            }
        }
    }*/


/*
    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);
                if (rest == PackageManager.PERMISSION_GRANTED) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (Exception e) {
                result = false;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }
*/


    //    ==========================更新
    private int PAGER_JSON = 1;





    //  判断是否更新



    public HotAnchorPresenter getPresenter() {
        return presenter;
    }

    public void GoTop() {
        if (adapter != null) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            linearLayoutManager.scrollToPosition(0);
        }
    }

    public void startPlayFragment() {
        if (hotAnchorSummary.getBroadcasting().equals("n")) {
            startActivity(OtherUserActivity.createIntent(getActivity(),
                    Integer.valueOf(hotAnchorSummary.getId()),
                    false));
            getActivity().overridePendingTransition(R.anim.fragment_slide_left_in, R.anim
                    .fragment_slide_left_out);
//            Intent myintent = new Intent(getActivity(), OtherUserActivity.class);
//            startActivity(myintent);
        } else {
            startActivity(RoomActivity.createIntent(getActivity(),
                    RoomActivity.TYPE_VIEW_LIVE,
                    hotAnchorSummary.getCurrentRoomNum(),
                    hotAnchorSummary.getId(),
                    PlayerFragment.createArgs(hotAnchorSummary)));
            getActivity().overridePendingTransition(R.anim.fragment_slide_left_in, R.anim
                    .fragment_slide_left_out);

        }
    }

    public void startActivateEvent(final int startEventId) {
        switch (startEventId){
            case 1:
                startKsEvent(false);
                break;
            default:
                starActivityEvent(startEventId, LocalDataManager.getInstance().getLoginInfo().getUserId());
                break;
        }

    }

    /*public void startKsEvent() {
        Log.i("RayTest","startKsEvent");
        KaraStar.getInstance().open(getActivity(), LocalDataManager.getInstance().getLoginInfo().getUserId(), new KaraStar.ViewHandler() {
            @Override
            public void switchToRecordView(KSWebActivity act, int starId, String starVideoUrl, String name) {
                String FilePath = VCamera.getVideoCachePath() + "K_star_rec_" + starId + "_" + LocalDataManager.getInstance().getLoginInfo().getUserId() + "/0.mp4";

                if (FileUtils.checkFile(FilePath)) {
                    Intent it = MediaPlayerActivity.createIntent(getActivity(), FilePath, name, starVideoUrl, starId);
                    act.startActivityForResult(it, KaraStar.REQUEST_VIDEO_RECORD);
                } else {
                    Intent it = MediaRecorderActivity.createIntent(getActivity(), name, starVideoUrl, starId);
                    act.startActivityForResult(it, KaraStar.REQUEST_VIDEO_RECORD);
                }
            }

            @Override
            public void switchToUserView(KSWebActivity act, String userId) {

                act.startActivity(OtherUserActivity.createIntent(getActivity(),
                        Integer.valueOf(userId), false));
            }

        });

    }*/

    private void showErrorMsgDiaLog(Activity currentActivity, String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity)
                .setTitle("公告")
                .setMessage(str)
                .setPositiveButton("關閉", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }


}

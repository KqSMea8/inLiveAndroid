package tw.chiae.inlive.presentation.ui.main.index;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.magiepooh.recycleritemdecoration.ItemDecorations;
import com.jakewharton.rxbinding.view.RxView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import rx.functions.Action1;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.AnchorSummary;
import tw.chiae.inlive.data.bean.HotAnchorSummary;
import tw.chiae.inlive.data.bean.ThemBean;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseFragment;
import tw.chiae.inlive.presentation.ui.base.ptr.BasePtr;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerAdapter;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerHolder;
import tw.chiae.inlive.presentation.ui.main.index.privateutil.GoPrivateRoom;
import tw.chiae.inlive.presentation.ui.main.index.privateutil.GoPrivateRoomInterface;
import tw.chiae.inlive.presentation.ui.main.me.OtherUserActivity;
import tw.chiae.inlive.presentation.ui.main.topic.AllTopicActivity;
import tw.chiae.inlive.presentation.ui.main.topic.NewsTopicActivity;
import tw.chiae.inlive.presentation.ui.room.RoomActivity;
import tw.chiae.inlive.presentation.ui.room.create.PrivateSetStringDialog;
import tw.chiae.inlive.presentation.ui.room.player.PlayerFragment;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.FrescoUtil;
import tw.chiae.inlive.util.PicUtil;

/**
 * 最新
 *
 * @author Muyangmin
 * @since 1.0.0
 */
public class RecommendAnchorFragment extends BaseFragment implements RecommendInterface, GoPrivateRoomInterface {

    private RecommendPresenter presenter;

    //    话题的那个
    public static final int TYPE_HEADER = 0;
    //    普通的type
    public static final int TYPE_NORMAL = 1;
    private PtrFrameLayout ptrFrameLayout;
    private RecyclerView recyclerView;
    private View viewEmpty;
    private RecommendAdapter adapter;
    private View mThemlayout;
    private TagFlowLayout mFlowLayout;
    //    话题的对象集合
    private List<ThemBean.Topic> list;
    //    话题的字符串集合
    private List<String> tipoclist;
    //    跳转到热门
    private TextView goHot;
    //    跳转到人呢的回调
    private RecommendShowHot recommendShowHot;
    // 私密房间的dialog
    private GoPrivateRoom goPrivateRoom;
    // 当前选中的用户
    private AnchorSummary hotAnchorSummary;
    private DisplayMetrics dm;

    public static RecommendAnchorFragment newInstance() {
        return new RecommendAnchorFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_recommend_anchor;
    }

    @Override
    protected void initViews(View view) {
        dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        mThemlayout = getActivity().getLayoutInflater().inflate(R.layout.recommend_them_tagflow_layout, null);
        mFlowLayout = (TagFlowLayout) mThemlayout.findViewById(R.id.id_flowlayout);

        presenter = new RecommendPresenter(this);
        ptrFrameLayout = $(view, R.id.recommend_anchor_ptr);
        viewEmpty = $(view, R.id.recommend_tv_empty);
        BasePtr.setRefreshOnlyStyle(ptrFrameLayout);
        goHot = $(view, R.id.followed_anchor_tv_view_hot);
        goHot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recommendShowHot.showHot();
            }
        });
        recyclerView = $(view, R.id.recommend_anchor_recycler);

        GridLayoutManager gridlayoutManager = new GridLayoutManager(view.getContext(), 2,GridLayoutManager.VERTICAL,false);

        recyclerView.addItemDecoration(new MDGridRvDividerDecoration());
        recyclerView.setLayoutManager(gridlayoutManager);
        recyclerView.addItemDecoration(ItemDecorations.vertical(view.getContext())
                .type(0, R.drawable.divider_decoration_transparent_h1)
                .create());

        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return super.checkCanDoRefresh(frame, recyclerView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                //presenter.loadAnchorsInfo(LocalDataManager.getInstance().getLoginInfo().getToken(),"","");
                presenter.loadRecommendAnchors(LocalDataManager.getInstance().getLoginInfo().getToken());
                //presenter.getThemBean("8");
            }
        });
        //presenter.getThemBean("8");
        //ptrFrameLayout.autoRefresh();
        presenter.loadRecommendAnchors(LocalDataManager.getInstance().getLoginInfo().getToken());
    }

    @Override
    public void questGoPrivateRoom(String type, int msg, String userid, String pwd) {
        presenter.checkPrivatePass(type, msg, pwd, LocalDataManager.getInstance().getLoginInfo().getUserId(), userid);
    }

    @Override
    public void onCancelDialogcheck(Bundle mArgs) {

    }

    public interface RecommendShowHot {
        void showHot();
    }

    public void setRecommendShowHot(RecommendShowHot recommendShowHot) {
        this.recommendShowHot = recommendShowHot;
    }

    @Override
    public void showData(List<AnchorSummary> list) {
        viewEmpty.setVisibility(View.INVISIBLE);
        Log.i("RayTest","size: "+list.size());
        if (adapter == null) {
            adapter = new RecommendAdapter(list);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setDataList(list);
        }
    }

    @Override
    public void onResume() {
//        MobclickAgent.onPageStart("主页-最新");
        /*if(ptrFrameLayout!=null&&!ptrFrameLayout.isAutoRefresh())
            ptrFrameLayout.autoRefresh(true);*/
        presenter.loadRecommendAnchors(LocalDataManager.getInstance().getLoginInfo().getToken());
        super.onResume();
    }

    @Override
    public void onPause() {
//        MobclickAgent.onPageStart("主页-最新");
/*        if(ptrFrameLayout!=null&&ptrFrameLayout.isAutoRefresh())
            ptrFrameLayout.autoRefresh(false);*/
        super.onPause();
    }

    @Override
    public void appendData(List<AnchorSummary> list) {
        adapter.appendData(list);

    }

    @Override
    public void showPrivateLimit(PrivateLimitBean bean) {
        dismissLoadingDialog();
        if (bean.getCome() == 0 && bean.getPtid() != 0) {
            if (bean.getPtid() == Integer.valueOf(PrivateSetStringDialog.PRIVTE_ROOM_LEVEL)) {
                //如果等级够了，直接调用验证接口
                if (Integer.valueOf(bean.getPrerequisite()) <= Integer.valueOf(LocalDataManager.getInstance().getLoginInfo().getLevel())) {
                    presenter.checkPrivatePass(PrivateSetStringDialog.PRIVTE_ROOM_LEVEL, bean.getId(), "", LocalDataManager.getInstance().getLoginInfo().getUserId(), hotAnchorSummary.getId());
                    return;
                }
            }
           /* if (goPrivateRoom == null)
                goPrivateRoom = new GoPrivateRoom();
            goPrivateRoom.setGoPrivateRoomInterface(RecommendAnchorFragment.this);
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

    @Override
    public void startGoPlayFragment() {
        startPlayFragment();
    }

    @Override
    public void saveAnchorsInfoData(List<HotAnchorSummary> list) {
        //presenter.loadRecommendAnchors(LocalDataManager.getInstance().getLoginInfo().getToken());
    }

    @Override
    public void showEmptyResult() {
        //Clear data
        if (adapter != null) {
            adapter.setDataList(new ArrayList<AnchorSummary>());
        }
        viewEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    public void onThemBean(ThemBean themBean) {
        viewEmpty.setVisibility(View.INVISIBLE);
        if (themBean != null) {
            tipoclist = new ArrayList<>();
            list = themBean.getTopic();
        }
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                tipoclist.add(list.get(i).getTopic_title());
            }
            tipoclist.add(getString(R.string.recommend_host_topic));

            mFlowLayout.setAdapter(new TagAdapter(tipoclist) {
                @Override
                public View getView(FlowLayout parent, int position, Object o) {
                    TextView tv = (TextView) getActivity().getLayoutInflater().inflate(R.layout.recommend_them_item,
                            mFlowLayout, false);
                    tv.setText("# " + tipoclist.get(position) + " #");
                    if (position == tipoclist.size() - 1) {
                        tv.setText(tipoclist.get(position));
                        tv.setTextColor(0xffffffff);
                        tv.setBackgroundResource(R.drawable.recommend_them_flow_hot_bg);
                    }
                    return tv;
                }
            });
            mFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
                @Override
                public boolean onTagClick(View view, int position, FlowLayout parent) {
                    if (tipoclist.get(position).equals(getString(R.string.recommend_host_topic))) {
//                        如果是热门话题则跳转到所有热门
                        startActivity(AllTopicActivity.createIntent(getActivity()));
                        return true;
                    }
                    startActivity(NewsTopicActivity.createIntent(getActivity(), list.get(position).getTopic_id(), list.get(position).getTopic_title()));
                    return true;
                }
            });
        }
    }

    @Override
    public void showLoadingComplete() {
        super.showLoadingComplete();
        ptrFrameLayout.refreshComplete();
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recyclerView.setAdapter(null);
       /* if(ptrFrameLayout.isAutoRefresh())
            ptrFrameLayout.autoRefresh(false);*/
        presenter.unsubscribeTasks();

    }

    private class RecommendAdapter extends SimpleRecyclerAdapter<AnchorSummary,
            RecommendHolder> {


        public RecommendAdapter(List<AnchorSummary> anchorSummaries) {
            super(anchorSummaries);

        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_recommend_user_result;
        }

        @NonNull
        @Override
        protected RecommendHolder createHolder(View view) {
            return new RecommendHolder(view);
        }


        @Override
        public void onBindViewHolder(RecommendHolder holder, int position) {
            if (getItemViewType(position) == TYPE_HEADER) return;
            super.onBindViewHolder(holder, getRealPosition(holder));
        }

        public int getRealPosition(RecyclerView.ViewHolder holder) {
            int position = holder.getLayoutPosition();
            if (mThemlayout != null) {
                return position - 1;
            }
            return position;
        }

        //    总的item个数
        @Override
        public int getItemCount() {
            if (mThemlayout != null) {
                return getDataList().size() + 1;
            }
            return getDataList().size();
        }

        //    返回不用的viewtype 的id
        @Override
        public int getItemViewType(int position) {
            if (mThemlayout != null && position == 0) {
                return TYPE_HEADER;
            } else {
                return TYPE_NORMAL;
            }
        }

        //  创建viewholder
        @Override
        public RecommendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //        如果头头不为空，并且样式是头头，那么就返回一个头头
            if (mThemlayout != null && viewType == TYPE_HEADER) {
                return new RecommendHolder(mThemlayout);
            }
            return super.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return getItemViewType(position) == TYPE_HEADER
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
        }
    }

    private class RecommendHolder extends SimpleRecyclerHolder<AnchorSummary> {

        private ViewGroup.LayoutParams lp;
        private TextView tvName;
        private SimpleDraweeView draweeAvatar;
        private ImageView imgLevel, imgStar;
       // private TextView tvHotPoint ;
        private ImageView ivApproveid;
        private TextView tvliveType;
        private int[] types = {R.drawable.tag_star, R.drawable.tag_gold, R.drawable.tag_office, R.drawable.tag_sp};

        public RecommendHolder(View itemView) {
            super(itemView);
            if (itemView == mThemlayout) {
                return;
            }
            tvName = (TextView) itemView.findViewById(R.id.item_search_anchor_tv_name);
//            tvIntro = (TextView) itemView.findViewById(R.id.item_search_anchor_tv_intro);
            draweeAvatar = (SimpleDraweeView) itemView.findViewById(R.id.img_user_avatar);
//            imgGender = (ImageView) itemView.findViewById(R.id.item_search_anchor_img_gender);
            imgLevel = (ImageView) itemView.findViewById(R.id.item_search_anchor_img_level);
            imgStar = (ImageView) itemView.findViewById(R.id.img_user_star_type);
           // tvHotPoint = (TextView) itemView.findViewById(R.id.tv_hot_point);
            //ivHotPoint = (ImageView) itemView.findViewById(R.id.iv_hot_point);
            ivApproveid =(ImageView) itemView.findViewById(R.id.iv_hot_approveid_type);
            tvliveType = (TextView)itemView.findViewById(R.id.recommend_live_type);
//            imgbtnFollow = (ImageButton) itemView.findViewById(R.id.item_search_anchor_imgbtn_follow);
            lp = draweeAvatar.getLayoutParams();
            lp.height = getScreenWidth()/2;
            lp.width = getScreenWidth()/2;
            draweeAvatar.setLayoutParams(lp);
        }

        public void dusplayHotPointUI(boolean d){
            int display ;
            if(d)
                display = View.VISIBLE;
            else
                display = View.INVISIBLE;
            //tvHotPoint.setVisibility(display);
            //ivHotPoint.setVisibility(display);
            ivApproveid.setVisibility(display);
        }

        @Override
        public void displayData(final AnchorSummary data) {
//            imgbtnFollow.setVisibility(View.GONE);

            tvName.setText(data.getNickname());
//            tvIntro.setText(data.getIntro());
            if (!TextUtils.isEmpty(data.getAvatar())) {

                FrescoUtil.frescoResize(SourceFactory.wrapPathToUri(data.getAvatar()),lp.width,lp.height,draweeAvatar);
               // draweeAvatar.setImageURI(SourceFactory.wrapPathToUri(data.getAvatar()));
            }


            if (data.getBroadcasting() != null) {
                if (data.getBroadcasting().equals("y")) {
                    tvliveType.setBackgroundResource(R.drawable.live_type_on);
                } else {
                    tvliveType.setBackgroundResource(R.drawable.live_type_off);
                    //data.setHotpoint("0");
                }
            }

           /* if(data.getApproveid()!=null){

            }else{

            }
*/
            int pprovetype = getTypeValue(data.getApproveid());
            if (pprovetype == 0)
                ivApproveid.setVisibility(View.GONE);
            switch (pprovetype) {
                case 1:
                    ivApproveid.setImageResource(types[0]);
                    break;
                case 2:
                    ivApproveid.setImageResource(types[1]);
                    break;
                case 3:
                    ivApproveid.setImageResource(types[2]);
                    break;
                case 4:
                    ivApproveid.setImageResource(types[3]);
                    break;
                default:
                    ivApproveid.setVisibility(View.GONE);
                    break;
            }
            if(data.getHotpoint()==null ){
                dusplayHotPointUI(false);
            }else {
                dusplayHotPointUI(true);
                //tvHotPoint.setText("" + data.getHotpoint());
            }
//            imgGender.setImageResource(SourceFactory.isMale(data.getSex()) ?
//                    R.drawable.ic_global_male : R.drawable.ic_global_female);
            /*imgLevel.setImageResource(PicUtil.getLevelImageId(itemView.getContext(), data.getEmceeLevel()));*/
//            RxView.clicks(imgbtnFollow)
//                    .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
//                    .subscribe(ㄜ
//                            new Action1<Void>() {
//                        @Override
//                        public void call(Void aVoid) {
//                            if (data.isFollowing()) {
//                                data.setFollowing(false);
//                                imgbtnFollow.setImageResource(R.drawable.ic_follow);
//                                presenter.unfollowAnchor(data.getId());
//                            } else {
//                                data.setFollowing(true);
//                                imgbtnFollow.setImageResource(R.drawable.ic_followed);
//                                presenter.followAnchor(data.getId());
//                            }
//                        }
//                    });
            RxView.clicks(itemView)
                    .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
//                            startActivity(OtherUserActivity.createIntent(getActivity(),
//                                    Integer.parseInt(data.getId()),true));
                            hotAnchorSummary = data;
                            showLoadingDialog();
                            presenter.loadPrivateLimit(data.getId());
                        }
                    });

//            imgStar.setImageResource(R.drawable.global_star_1);
        }

        private int getTypeValue(String approveid) {
            if(approveid==null)
                return 0;
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

        public int getScreenWidth() {

            return dm.widthPixels;
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

    public class MDGridRvDividerDecoration extends RecyclerView.ItemDecoration {


        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildLayoutPosition(view)-1;
            int decoration = 3;
            if(position%2==0){
                if(position/2==0)
                    outRect.set(decoration, 0, decoration, 0);  //左 第一排
                else
                    outRect.set(decoration, 0, decoration, decoration);  //左 其他
            }else{
                if(position/2==0)
                    outRect.set(decoration, 0, decoration, 0);  //左 第一排
                else
                    outRect.set(decoration, 0, decoration, decoration);  //左 其他
            }


          /*  if(position%4==0)
                outRect.set(0, 0, 0, 0);
            if(position%4==1)
                outRect.set(0, 0, 0, 0);
            if(position%4==2)
                outRect.set(0, 0, 0, 0);
            if(position%4==3)
                outRect.set(0, 0, 0, 0);*/


        }
    }
}

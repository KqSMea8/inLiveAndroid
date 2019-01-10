package tw.chiae.inlive.presentation.ui.main.me;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.magiepooh.recycleritemdecoration.ItemDecorations;
import com.jakewharton.rxbinding.view.RxView;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.AnchorSummary;
import tw.chiae.inlive.data.bean.room.PrivateLimitBean;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseFragment;
import tw.chiae.inlive.presentation.ui.base.ptr.BasePtr;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerAdapter;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerHolder;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.FrescoUtil;
import tw.chiae.inlive.util.PicUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by huanzhang on 2016/6/2.
 */
public class MeStarListFragment extends BaseFragment implements IUserList {

    public static int KEY_STAR = 1;
    public static int KEY_FANS = 2;


    private static final String ARG_UID = "uid";
    private static final String ARG_KEY = "key";
    private static final String ARG_SHOW_ONLINE = "showOnline";

    private boolean showOnline = false;

    private String mUid;
    private int mKey;

    private PtrFrameLayout ptrFrameLayout;
    private RecyclerView recyclerView;
    private UserListPresenter presenter;
    private StarUserListAdapter adapter;
    private TextView viewEmpty;

    public static MeStarListFragment newInstance(String uid, int key, boolean showOnline) {
        MeStarListFragment fragment = new MeStarListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_UID, uid);
        bundle.putInt(ARG_KEY, key);
        bundle.putBoolean(ARG_SHOW_ONLINE, showOnline);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_list;
    }

    @Override
    protected void initViews(View view) {
        presenter = new UserListPresenter(this);
        mKey = getArguments().getInt(ARG_KEY);
        mUid = getArguments().getString(ARG_UID);
        showOnline = getArguments().getBoolean(ARG_SHOW_ONLINE);
        recyclerView = $(view, R.id.user_list_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.addItemDecoration(ItemDecorations.vertical(this.getActivity())
                .type(0, R.drawable.divider_decoration_transparent_h1)
                .create());

        ptrFrameLayout = $(view, R.id.user_list_ptr);
        viewEmpty = $(view, R.id.user_list_tv_empty);

        String uid = LocalDataManager.getInstance().getLoginInfo().getUserId();
        boolean isShowSelf = (mUid.equals(uid));
        String subject = getString(isShowSelf ? R.string.user_list_subject_me : R.string
                .user_list_subject_other);

        if (mKey == KEY_FANS) {
            viewEmpty.setText(getString(R.string.user_list_no_followee, subject));
        } else {
            viewEmpty.setText(getString(R.string.user_list_no_following, subject));
        }

//        BasePtr.setLoadMoreOnlyStyle(ptrFrameLayout);
        BasePtr.setPagedPtrStyle(ptrFrameLayout);
        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler2() {

            @Override
            public boolean checkCanDoLoadMore(PtrFrameLayout frame, View content, View footer) {
                return super.checkCanDoLoadMore(frame, recyclerView, footer);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return super.checkCanDoRefresh(frame, recyclerView, header);
            }

            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                presenter.queryNextPage(mUid, mKey);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                presenter.queryFirstPage(mUid, mKey);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.queryFirstPage(mUid, mKey);
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
    public void showData(List<AnchorSummary> list) {
        viewEmpty.setVisibility(View.GONE);
        if (adapter == null) {
            adapter = new StarUserListAdapter(list, showOnline);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setDataList(list);
        }
    }

    @Override
    public void appendData(List<AnchorSummary> list) {
        adapter.appendData(list);
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
    public void onDestroy() {
        super.onDestroy();
        presenter.unsubscribeTasks();
    }

    @Override
    public void onCancelDialogcheck(Bundle mArgs) {

    }


    public class StarUserListAdapter extends SimpleRecyclerAdapter<AnchorSummary,
            StarUserListHolder> {
        private boolean showOnline;

        public StarUserListAdapter(List<AnchorSummary> anchorSummaries, boolean showOnline) {
            super(anchorSummaries);
            this.showOnline = showOnline;
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_search_result;
        }

        @NonNull
        @Override
        protected StarUserListHolder createHolder(View view) {
            return new StarUserListHolder(view, showOnline);
        }
    }

    public class StarUserListHolder extends SimpleRecyclerHolder<AnchorSummary> {

        private TextView tvNickname, tvIntro;
        private SimpleDraweeView draweeAvatar;
        private ImageView imgGender, imgLevel, imgStar;
        private ImageButton imgbtnFollow;
        private Boolean showOnline;

        public StarUserListHolder(View itemView, boolean showOnline) {
            super(itemView);
            this.showOnline = showOnline;
            tvNickname = (TextView) itemView.findViewById(R.id.item_search_anchor_tv_nickname);
            tvIntro = (TextView) itemView.findViewById(R.id.item_search_anchor_tv_intro);
            draweeAvatar = (SimpleDraweeView) itemView.findViewById(R.id.img_user_avatar);
            imgGender = (ImageView) itemView.findViewById(R.id.item_search_anchor_img_gender);
            imgLevel = (ImageView) itemView.findViewById(R.id.item_search_anchor_img_level);
            imgStar = (ImageView) itemView.findViewById(R.id.img_user_star_type);
            imgbtnFollow = (ImageButton) itemView.findViewById(R.id.item_search_anchor_imgbtn_follow);
        }

        @Override
        public void displayData(final AnchorSummary data) {
            tvNickname.setText(data.getNickname());
            if(data.getIntro()==null || TextUtils.isEmpty(data.getIntro().trim()))
            {
                tvIntro.setText("我就是個謎，什麼都不告訴你 !");
            }else
            {
                tvIntro.setText(data.getIntro());
            }

            if (!TextUtils.isEmpty(data.getAvatar())) {
                FrescoUtil.frescoResize(SourceFactory.wrapPathToUri(data.getAvatar()),
                        (int) getResources().getDimension(R.dimen.avatar_size_default),
                        (int) getResources().getDimension(R.dimen.avatar_size_default),
                        draweeAvatar
                );
            }
            imgGender.setImageResource(SourceFactory.isMale(data.getSex()) ?
                    R.drawable.ic_global_male : R.drawable.ic_global_female);
            imgLevel.setImageResource(PicUtil.getLevelImageId(itemView.getContext(), data
                    .getEmceeLevel()));
            //关注的
            if (data.getIs_attention() == AnchorSummary.IS_ATTENTION) {
                data.setFollowing(true);
                imgbtnFollow.setImageResource(R.drawable.ic_followed);
            } else {
                data.setFollowing(false);
                imgbtnFollow.setImageResource(R.drawable.ic_follow);
            }
            RxView.clicks(imgbtnFollow)
                    .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            if (data.isFollowing()) {
                                data.setFollowing(false);
                                imgbtnFollow.setImageResource(R.drawable.ic_follow);
                                presenter.unStarUser(data.getId());
                            } else {
                                data.setFollowing(true);
                                imgbtnFollow.setImageResource(R.drawable.ic_followed);
                                presenter.starUser(data.getId());
                            }
                        }
                    });
            RxView.clicks(itemView)
                    .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                    .filter(new Func1<Void, Boolean>() {
                        @Override
                        public Boolean call(Void aVoid) {
                            boolean isValid = !TextUtils.isEmpty(data.getId());
                            if (!isValid) {
                                toastShort(getString(R.string.load_user_profile_error));
                            }
                            //有些Uid数据不合发
                            return isValid;
                        }
                    })
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            startActivity(OtherUserActivity.createIntent(getActivity(),
                                    Integer.parseInt(data.getId()), showOnline));
                        }
                    });
//            if (imgStar!=null) {
//                imgStar.setImageResource(R.drawable.global_star_1);
//            }
        }

    }
}
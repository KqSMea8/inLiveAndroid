package tw.chiae.inlive.presentation.ui.main.search;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.magiepooh.recycleritemdecoration.ItemDecorations;
import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import rx.functions.Action1;
import rx.functions.Func1;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.AnchorSummary;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.base.ptr.BasePtr;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerAdapter;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerHolder;
import tw.chiae.inlive.presentation.ui.main.me.OtherUserActivity;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.FrescoUtil;
import tw.chiae.inlive.util.PicUtil;

/**
 * 搜索
 *
 * @author Muyangmin
 * @since 1.0.0
 */
public class SearchActivity extends BaseActivity implements SearchUiInterface {

    private View viewEmpty;
    private PtrFrameLayout ptrFrameLayout;
    private RecyclerView recyclerView;
    private EditText edtContent;
    private Button search_btn_ID, search_btn_topic, search_btn_name, search_btn;
    private ImageButton imgbtnClearInput;
    private SearchPresenter presenter;
    private SearchResultAdapter adapter;
    private boolean isclick = false;


    public static Intent createIntent(Context context) {
        return new Intent(context, SearchActivity.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_user;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        presenter = new SearchPresenter(this);
        viewEmpty = $(R.id.search_tv_empty);
        ptrFrameLayout = $(R.id.search_ptr);
        recyclerView = $(R.id.search_recycler);
        edtContent = $(R.id.search_edit_content);
        search_btn_ID = $(R.id.search_btn_ID);
        search_btn_topic = $(R.id.search_btn_topic);
        search_btn_name = $(R.id.search_btn_name);
        imgbtnClearInput = $(R.id.search_imgbtn_clear_input);
        search_btn = (Button) findViewById(R.id.search_btn);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(ItemDecorations.vertical(this)
                .type(0, R.drawable.divider_decoration_transparent_h1)
                .create());

        //不允许刷新；仅当有内容时允许加载更多
        BasePtr.setLoadMoreOnlyStyle(ptrFrameLayout);
        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler2() {
            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
//                presenter.queryNextPage();
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                //Not supported
            }
        });


        edtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    imgbtnClearInput.setVisibility(View.VISIBLE);
                } else {
                    imgbtnClearInput.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        RxView.clicks(search_btn_ID)
                .map(new Func1<Void, CharSequence>() {
                    @Override
                    public CharSequence call(Void aVoid) {

                        showEmptyResult();
                        edtContent.getText().clear();
                        if (!isclick) {
                            search_btn_ID.setBackground(new ColorDrawable(0xc8ff59a5));
                            search_btn_name.setBackground(new ColorDrawable(0x4e9891));
                            search_btn_topic.setBackground(new ColorDrawable(0x4e9891));
                        }
                        SpannableString s = new SpannableString("請輸入你要搜索的ID");
                        edtContent.setInputType(EditorInfo.TYPE_CLASS_PHONE);
                        edtContent.setHint(s);
                        return edtContent.getText();

                    }
                })
                .filter(new Func1<CharSequence, Boolean>() {
                    @Override
                    public Boolean call(final CharSequence charSequence) {
                        //为空时不处理
                        search_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (edtContent.getText().length() == 0) {
                                    toastShort("请输入ID");
                                } else {
                                    presenter.queryAnchors(charSequence.toString(), "id");
                                }
                            }
                        });
                        return !TextUtils.isEmpty(charSequence);
                    }
                })
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MICROSECONDS)
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(final CharSequence charSequence) {

                    }
                });
        search_btn_ID.performClick();
        RxView.clicks(search_btn_name)
                .map(new Func1<Void, CharSequence>() {
                    @Override
                    public CharSequence call(Void aVoid) {
                        showEmptyResult();
                        edtContent.getText().clear();
                        if (!isclick) {
                            search_btn_name.setBackground(new ColorDrawable(0xc8ff59a5));
                            search_btn_ID.setBackground(new ColorDrawable(0x4e9891));
                            search_btn_topic.setBackground(new ColorDrawable(0x4e9891));
                        }
                        SpannableString s = new SpannableString("請輸入你要搜索的暱稱");
                        edtContent.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                        edtContent.setHint(s);
                        return edtContent.getText();
                    }
                })
                .filter(new Func1<CharSequence, Boolean>() {
                    @Override
                    public Boolean call(final CharSequence charSequence) {
                        //为空时不处理
                        search_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (edtContent.getText().length() == 0) {
                                    toastShort("請輸入暱稱");
                                } else {
                                    presenter.queryAnchors(charSequence.toString(), "nickname");
                                }
                            }
                        });
                        return !TextUtils.isEmpty(charSequence);
                    }
                })
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MICROSECONDS)
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(final CharSequence charSequence) {


                    }
                });
        RxView.clicks(search_btn_topic)
                .map(new Func1<Void, CharSequence>() {
                    @Override
                    public CharSequence call(Void aVoid) {
                        showEmptyResult();
                        edtContent.getText().clear();
                        if (!isclick) {
                            search_btn_topic.setBackground(new ColorDrawable(0xc8ff59a5));
                            search_btn_name.setBackground(new ColorDrawable(0x4e9891));
                            search_btn_ID.setBackground(new ColorDrawable(0x4e9891));
                        }
                        SpannableString s = new SpannableString("请输入你要搜索的话题");
                        edtContent.setHint(s);
                        return edtContent.getText();
                    }
                })
                .filter(new Func1<CharSequence, Boolean>() {
                    @Override
                    public Boolean call(final CharSequence charSequence) {
                        //为空时不处理
                        search_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (edtContent.getText().length() == 0) {
                                    toastShort("请输入话题");
                                } else {
                                    presenter.queryAnchors(charSequence.toString(), "topic");
                                }
                            }
                        });
                        return !TextUtils.isEmpty(charSequence);
                    }
                })
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MICROSECONDS)
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(final CharSequence charSequence) {


                    }
                });
        RxView.clicks(imgbtnClearInput)
                .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        edtContent.getText().clear();
                        showEmptyResult();
                    }
                });

    }

    @Override
    protected void init() {
        setSwipeBackEnable(false);
    }

    @Override
    public void showData(List<AnchorSummary> list) {
        viewEmpty.setVisibility(View.INVISIBLE);
        if (adapter == null) {
            adapter = new SearchResultAdapter(list);
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
    protected void onDestroy() {
        super.onDestroy();
        presenter.unsubscribeTasks();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fragment_slide_left_in, R.anim.fragment_slide_left_out);
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    private class SearchResultAdapter extends SimpleRecyclerAdapter<AnchorSummary,
            SearchResultHolder> {
        public SearchResultAdapter(List<AnchorSummary> anchorSummaries) {
            super(anchorSummaries);
        }

        @Override
        protected int getItemLayoutId(int viewType) {
            return R.layout.item_search_result;
        }

        @NonNull
        @Override
        protected SearchResultHolder createHolder(View view) {
            return new SearchResultHolder(view);
        }
    }

    private class SearchResultHolder extends SimpleRecyclerHolder<AnchorSummary> {

        private TextView tvNickname, tvIntro;
        private SimpleDraweeView draweeAvatar;
        private ImageView imgGender, imgLevel, imgStar;
        private ImageButton imgbtnFollow;

        public SearchResultHolder(View itemView) {
            super(itemView);
            tvNickname = (TextView) itemView.findViewById(R.id.item_search_anchor_tv_nickname);
            tvIntro = (TextView) itemView.findViewById(R.id.item_search_anchor_tv_intro);
            draweeAvatar = (SimpleDraweeView) itemView.findViewById(R.id.img_user_avatar);
            imgGender = (ImageView) itemView.findViewById(R.id.item_search_anchor_img_gender);
            imgLevel = (ImageView) itemView.findViewById(R.id.item_search_anchor_img_level);
            imgStar = (ImageView) itemView.findViewById(R.id.img_user_star_type);
            imgbtnFollow = (ImageButton) itemView.findViewById(R.id
                    .item_search_anchor_imgbtn_follow);
        }

        @Override
        public void displayData(final AnchorSummary data) {
            tvNickname.setText(data.getNickname());
            tvIntro.setText(data.getIntro());
            if (!TextUtils.isEmpty(data.getAvatar())) {
                FrescoUtil.frescoResize(SourceFactory.wrapPathToUri(data.getAvatar()),
                        (int) getResources().getDimension(R.dimen.avatar_size_default),
                        (int) getResources().getDimension(R.dimen.avatar_size_default),
                        draweeAvatar
                );
            }
            imgGender.setImageResource(SourceFactory.isMale(data.getSex()) ?
                    R.drawable.ic_global_male : R.drawable.ic_global_female);
            imgLevel.setImageResource(PicUtil.getLevelImageId(itemView.getContext(), data.getEmceeLevel()));

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
                                presenter.unfollowAnchor(data.getId());
                            } else {
                                data.setFollowing(true);
                                imgbtnFollow.setImageResource(R.drawable.ic_followed);
                                presenter.followAnchor(data.getId());
                            }
                        }
                    });
            RxView.clicks(itemView)
                    .throttleFirst(Const.VIEW_THROTTLE_TIME, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            startActivity(OtherUserActivity.createIntent(SearchActivity.this,
                                    Integer.parseInt(data.getId()), true));
                        }
                    });
//            imgStar.setImageResource(R.drawable.global_star_1);
        }
    }
}

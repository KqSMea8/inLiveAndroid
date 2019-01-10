package tw.chiae.inlive.presentation.ui.widget.giftView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.gift.Gift;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by huanzhang on 10/21/15.
 */
public class GiftLayoutView extends RelativeLayout implements AdapterView.OnItemClickListener {
    private static final int DEFUALT_ROW = 2;
    private static final int DEFUALT_COLUMN = 4;
    private static final int DEFUALT_SRC_SELECT = R.drawable.ic_page_indicator_focused;
    private static final int DEFUALT_SRC_NORMOL = R.drawable.ic_page_indicator;
    private static final int DEFUALT_POINT_MARGIN = 5;
    private static final int DEFUALT_ITEM_PADDING = 0;
    private Context mContext;
    private ViewPager mViewPager;
    private LinearLayout mPointLayout;
    private ArrayList<View> mPageViews;
    private ArrayList<GiftGridViewAdapter> mAdapters;
    private List<Gift> mGiftAllKeys;
    private List<List<Gift>> mGiftPagekeys;
    private ArrayList<ImageView> mPointViews;
    private GiftClickListener mGiftClickListener;

    private int mRow = DEFUALT_ROW, mColumn = DEFUALT_COLUMN;
    private int mPointMargin = DEFUALT_POINT_MARGIN, mItemPadding = DEFUALT_ITEM_PADDING;
    private int mPointNorSrc = DEFUALT_SRC_NORMOL, mPointSelectSrc = DEFUALT_SRC_SELECT;
    private int mPages = 0, mPageTotalEmotion, mCurrentPage;
    private LayoutParams mViewpagerLayoutParams, mPointLayoutParams;

    private int mSelectPage = -1,mSelectPosition = -1;

    //切换页面只保存了postion并不是保存的那个礼物的对象，所以会送错
    Gift selectGift;
    public GiftLayoutView(Context context) {
        super(context);
        this.mContext = context;
    }

    public GiftLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initAttr(context, attrs);
    }

    public GiftLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        this.mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.face_style);

        this.mRow = typedArray.getInteger(R.styleable.face_style_face_row, DEFUALT_ROW);

        this.mColumn = typedArray.getInteger(R.styleable.face_style_face_column, DEFUALT_COLUMN);
        this.mPointSelectSrc = typedArray.getResourceId(R.styleable.face_style_face_point_src_select, DEFUALT_SRC_SELECT);
        this.mPointNorSrc = typedArray.getResourceId(R.styleable.face_style_face_point_src_normol, DEFUALT_SRC_NORMOL);
        this.mPointMargin = (int) typedArray.getDimension(R.styleable.face_style_face_point_margin, DEFUALT_POINT_MARGIN);
        this.mItemPadding = (int) typedArray.getDimension(R.styleable.face_style_face_item_padding, DEFUALT_ITEM_PADDING);
        typedArray.recycle();
    }

    public void setGiftDatas(List<Gift> facestrs) {
        if (facestrs != null) {
//            this.mGiftAllKeys = facestrs;
//            进行排序
            this.mGiftAllKeys =AscendingList(facestrs);
            removeAllViews();
            initView();
        }

    }

    private List<Gift> AscendingList(List<Gift> facestrs) {
        for (int i=0;i<facestrs.size();i++){
            for (int j=0;j<facestrs.size()-1;j++){
                if (facestrs.get(j).getPrice()>facestrs.get(j+1).getPrice()){
                    Gift gift=facestrs.get(j);
                    facestrs.set(j,facestrs.get(j+1));
                    facestrs.set(j+1,gift);
                }
            }
        }
        return facestrs;
    }

    public void setGiftSelectChangeListener(GiftClickListener listener) {
        mGiftClickListener = listener;
    }

    //获取勾选的礼物
    public Gift getSelectedGift(){
        if(mSelectPage == -1){
            return  null;
        }else{
            return selectGift;
        }
    }
    private void initView() {

        mViewPager = new ViewPager(mContext);
        mPointLayout = new LinearLayout(mContext);
        //noinspection ResourceType
        mViewPager.setId(1);
        //noinspection ResourceType
        mPointLayout.setId(2);
//
        mViewpagerLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPointLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mViewpagerLayoutParams.addRule(RelativeLayout.ABOVE, mPointLayout.getId());

        mPointLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//        mPointLayoutParams.setMargins(0, 0, 0, 12);//左上右下

        addView(mPointLayout, mPointLayoutParams);
        addView(mViewPager, mViewpagerLayoutParams);
//
        mPointLayout.setOrientation(LinearLayout.HORIZONTAL);
        mPointLayout.setGravity(Gravity.CENTER);

        initData();
        initViewPager();
        initPointLayout();
        setData();
    }

    private void initViewPager() {
        mPageViews = new ArrayList<View>();
        // 中间添加表情页

        mAdapters = new ArrayList<>();
        for (int i = 0; i < mGiftPagekeys.size(); i++) {
            GridView view = new GridView(mContext);
            GiftGridViewAdapter adapter = new GiftGridViewAdapter(mContext, mGiftPagekeys.get(i), mItemPadding);
            view.setAdapter(adapter);
            mAdapters.add(adapter);
            view.setOnItemClickListener(this);
            view.setNumColumns(mColumn);
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setHorizontalSpacing(1);
            view.setVerticalSpacing(1);
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            view.setCacheColorHint(0);
            view.setSelector(new ColorDrawable(Color.TRANSPARENT));
            view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
            view.setGravity(Gravity.CENTER);
            mPageViews.add(view);
        }
    }

    private void initPointLayout() {
        if (mGiftAllKeys == null || mGiftAllKeys.size() == 0) {
            return;
        }

        mPointViews = new ArrayList<ImageView>();
        for (int i = 0; i < mPages; i++) {
            ImageView imageView = new ImageView(mContext);
            imageView.setImageResource(mPointNorSrc);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT));
            layoutParams.setMargins(mPointMargin, mPointMargin, mPointMargin, mPointMargin);

            mPointViews.add(imageView);
            mPointLayout.addView(imageView, layoutParams);
        }
    }

    private void initData() {
        if (mGiftAllKeys == null || mGiftAllKeys.size() == 0) {
            return;
        }
        mGiftPagekeys = new ArrayList<>();
        mPageTotalEmotion = mRow * mColumn;
        mPages = (int) Math.ceil(mGiftAllKeys.size() / (mPageTotalEmotion * 1.00));

        for (int count =0, totalCount= mGiftAllKeys.size(); count < totalCount; /*add in loop, empty here*/) {
            ArrayList<Gift> page = new ArrayList<>();
            if (totalCount - count > mPageTotalEmotion) {// 0~ (n-1) page
                page.addAll(mGiftAllKeys.subList(count, count + mPageTotalEmotion));
                count += mPageTotalEmotion;
            } else {
                page.addAll(mGiftAllKeys.subList(count, totalCount));
                count = totalCount;
            }
            mGiftPagekeys.add(page);
        }
    }

    /**
     * 填充数据
     */
    private void setData() {
        if (mGiftAllKeys == null || mGiftAllKeys.size() == 0) {
            return;
        }

        mViewPager.setAdapter(new GiftViewPagerAdapter(mPageViews));
        mCurrentPage = 0;
        mViewPager.setCurrentItem(0);
        mPointViews.get(mCurrentPage).setImageResource(mPointSelectSrc);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mPointViews.get(mCurrentPage).setImageResource(mPointNorSrc);
                mPointViews.get(i).setImageResource(mPointSelectSrc);
                mCurrentPage = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if((mSelectPage != -1) &&(mSelectPage==mCurrentPage)) {

            if(position == mSelectPosition){

                mAdapters.get(mSelectPage).updateAdapter(-1,mSelectPosition);
                mSelectPage = -1;
                mSelectPosition = -1;
                selectGift=null;
            }else {

                mAdapters.get(mSelectPage).updateAdapter(position, mSelectPosition);
                mSelectPage = mCurrentPage;
                mSelectPosition = position;
                selectGift=mGiftPagekeys.get(mCurrentPage).get(mSelectPosition);
            }


        }else{
            if(mSelectPage!= -1) {

                mAdapters.get(mSelectPage).updateAdapter(-1, mSelectPosition);
            }
            Log.i("RayTest","onItem click 4");
            mAdapters.get(mCurrentPage).updateAdapter(position,-1);
            mSelectPage = mCurrentPage;
            mSelectPosition = position;
            //记录选中的礼物对象
            selectGift=mGiftPagekeys.get(mCurrentPage).get(mSelectPosition);

        }

        if (mGiftClickListener != null) {
            if(mSelectPage!=-1) {

                mGiftClickListener.onEmotionSelected(true);
                Log.i("zh", "click item");
            }else{
                mGiftClickListener.onEmotionSelected(false);
            }
        }

    }
}

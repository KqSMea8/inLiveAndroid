package tw.chiae.inlive.presentation.ui.photoselect;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import tw.chiae.inlive.R;
import tw.chiae.inlive.util.FrescoUtil;
import tw.chiae.inlive.util.PixelUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huanzhang on Nov 17, 2015.
 */
public class PhotoSelectAdapter extends RecyclerView.Adapter<PhotoSelectAdapter.PhotoViewHolder> {
    public static final int PADDING_OUTSIDE = 8;
    public static final int PADDIND_INSIDE = 4;
    public static final int BLANK_COUNT_OUTSIDE = 2;
    public static final int BLANK_COUNT_INSIDE = 3;
    public static final int ITEM_COUNT_IN_ONE_ROW = 4;
    private final Context mContext;
    private final ArrayList<String> mSelectedDataList;
    private final IPhotoSelect mIPublishPhoto;
    private final int mPaddingSum;
    private final int mPhotoLimit;
    private List<ImageItem> mDataList;
    private int mItemSize;
    private PhotoViewHolder mTag;

    public PhotoSelectAdapter(Context context, IPhotoSelect iPublishPhoto, int photoLimit) {
        mContext = context;
        mIPublishPhoto = iPublishPhoto;
        mPhotoLimit = photoLimit;
        mSelectedDataList = new ArrayList<>();
        mPaddingSum = PixelUtil.dp2px(mContext, PADDING_OUTSIDE * BLANK_COUNT_OUTSIDE
                + PADDIND_INSIDE * BLANK_COUNT_INSIDE);
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo_publish_select, parent, false);
        setHeight(parent, v);
        return new PhotoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        if (mDataList != null) {
            holder.initData(position);
        }
    }

    @Override
    public int getItemCount() {
        if (mDataList == null) {
            return 0;
        } else {
            return mDataList.size();
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    private ImageItem getItem(int position) {
        if (mDataList == null) {
            return null;
        } else {
            return mDataList.get(position);
        }
    }

    private void setHeight(ViewGroup parent, View v) {
        int mWidthPixels = parent.getWidth();
        mItemSize = (mWidthPixels - mPaddingSum) / ITEM_COUNT_IN_ONE_ROW;

        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        layoutParams.height = mItemSize;
        layoutParams.width = mItemSize;
        v.setLayoutParams(layoutParams);
    }

    public void addPhotos(List<ImageItem> dataList) {
        if (mDataList == null) {
            mDataList = dataList;
        } else {
            mDataList.addAll(dataList);
        }
        notifyDataSetChanged();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final SimpleDraweeView mSimpleDraweeView;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            mSimpleDraweeView =
                    (SimpleDraweeView) itemView.findViewById(R.id.sdv_photo_publish_select_item);
            mSimpleDraweeView.setAspectRatio(1.0f);
            itemView.setOnClickListener(this);
        }

        public void initData(int position) {
            FrescoUtil
                    .frescoResize(Uri.parse("file://" + mDataList.get(position).getImagePath()),
                                  mItemSize, mItemSize, mSimpleDraweeView);
            ImageItem imageItem = getItem(position);
        }
        @Override
        public void onClick(View v) {
            ImageItem imageItem = getItem(getAdapterPosition());
            if (imageItem == null) {
                return;
            }
            mIPublishPhoto.onSelected(imageItem);
        }
    }
}

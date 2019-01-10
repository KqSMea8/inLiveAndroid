package tw.chiae.inlive.presentation.ui.main.currency;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.CurrencyRankItem;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.util.FrescoUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huanzhang on 2016/5/7.
 */
public class CurrencyListAdapter extends RecyclerView.Adapter<CurrencyListAdapter
        .ViewHolder> {
    public static final int TYPE_NORMAL = 4;
    public static final int TYPE_NO_ONE = 1;
    public static final int TYPE_NO_TWO = 2;
    public static final int TYPE_NO_THREE = 3;

    private List<CurrencyRankItem> mDatas = new ArrayList<>();

    public void update(List<CurrencyRankItem> datas){
        if(datas!= null){
            mDatas = datas;
        }
        notifyDataSetChanged();
    }

    public void appendData(List<CurrencyRankItem> datas){
        if(mDatas ==null)
            mDatas = new ArrayList<>();
        if(datas!= null){
            mDatas.addAll(datas);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_NO_ONE;
        }
        if (position == 1) {
            return TYPE_NO_TWO;
        }
        if (position == 2) {
            return TYPE_NO_THREE;
        }
        return TYPE_NORMAL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        switch (viewType) {
            case TYPE_NO_ONE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_contributor_header_item, parent, false);
                break;
            case TYPE_NO_TWO:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_contributor_header_item2, parent, false);
                break;
            case TYPE_NO_THREE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_contributor_header_item3, parent, false);
                break;
            case TYPE_NORMAL:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_contributor_normal, parent, false);
                break;
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.showData(mDatas.get(position),position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void clearData() {
        if(mDatas!=null){
            mDatas.clear();
            notifyDataSetChanged();
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mRankTv, mNameTv, mCoinCountTv;
        private SimpleDraweeView mUserPortrait, mUserType;
        private ImageView mSexImg, mLevelImg;

        public ViewHolder(View itemView) {
            super(itemView);
            findView(itemView);
        }
        private void findView(View itemView){
            mRankTv = (TextView)itemView.findViewById(R.id.item_xb_txt_rank);
            mNameTv = (TextView)itemView.findViewById(R.id.item_xb_txt_username);
            mCoinCountTv = (TextView)itemView.findViewById(R.id.item_xb_txt_coin_count);
            mUserPortrait = (SimpleDraweeView)itemView.findViewById(R.id.item_xb_user_portrait);
            mUserType = (SimpleDraweeView)itemView.findViewById(R.id.item_xb_img_user_type);
            mSexImg = (ImageView)itemView.findViewById(R.id.item_xb_img_gender);
            mLevelImg = (ImageView)itemView.findViewById(R.id.item_xb_img_level);
        }
        public void showData(CurrencyRankItem data,int position){
            mRankTv.setText(itemView.getContext().getString(R.string.room_contributor_rank,position+1));
            mNameTv.setText(data.getUsername()+"");
            mCoinCountTv.setText(itemView.getContext().getString(R.string
                    .room_contributor_contribution,data.getCoin()+"",
                    " IN幣"));
            if(!TextUtils.isEmpty(data.getAvatar())){
                if(position ==0) {
                    FrescoUtil.frescoResize(SourceFactory.wrapPathToUri(data.getAvatar()),
                            (int) itemView.getContext().getResources().getDimension(R.dimen.currency_one_size),
                            (int) itemView.getContext().getResources().getDimension(R.dimen.currency_one_size),
                            mUserPortrait);
                }else{
                    FrescoUtil.frescoResize(SourceFactory.wrapPathToUri(data.getAvatar()),
                            (int) itemView.getContext().getResources().getDimension(R.dimen.currency_two_size),
                            (int) itemView.getContext().getResources().getDimension(R.dimen.currency_two_size),
                            mUserPortrait);
                }
            }

            mSexImg.setImageResource(data.getSex() == 0 ? R.drawable.ic_male : R.drawable.ic_female);

            int resId =itemView.getContext().getResources().getIdentifier("ic_level_" + data.getLevelid(), "drawable",
                    itemView.getContext().getPackageName());
            mLevelImg.setImageResource(resId);
        }
    }
}

package tw.chiae.inlive.presentation.ui.widget.giftView;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.gift.Gift;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.util.FrescoUtil;

import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huanzhang on 10/21/15.
 */
public class GiftGridViewAdapter extends BaseAdapter {

    private List<Gift> mGiftKeys;
    private ArrayList<Boolean> mselect;
    private Context mContext;
    private int mItemPadding = 0;

    public GiftGridViewAdapter(Context context, List<Gift> datas, int padding) {
        this.mGiftKeys = datas;
        this.mContext = context;
        this.mItemPadding = padding;
        if (datas == null) {
            this.mGiftKeys = new ArrayList<>();

        } else {
            this.mselect = new ArrayList<>();
            for (int i = 0; i < mGiftKeys.size(); i++) {
                mselect.add(false);
            }
        }
    }

    public GiftGridViewAdapter(Context context) {
        this.mGiftKeys = new ArrayList<>();
        this.mselect = new ArrayList<>();
        this.mContext = context;
    }

    public void updateAdapter(ArrayList<Gift> datas) {
        this.mGiftKeys = datas;
        this.mselect = new ArrayList<>();
        this.notifyDataSetChanged();
    }

    public void updateAdapter(int selectPosition, int unSelectPosition) {
        if (selectPosition != -1) {
            mselect.set(selectPosition, Boolean.TRUE);
        }
        if (unSelectPosition != -1) {
            mselect.set(unSelectPosition, Boolean.FALSE);
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mGiftKeys.size();
    }

    @Override
    public Object getItem(int position) {
        return mGiftKeys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        Log.d("mark", "getView() is invoked!" + "position = " + position + ","
//                + "convertView = " + convertView + "," + "parent = " + parent);

        ViewHolder vHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_gift, null);
            vHolder = new ViewHolder(convertView);
            convertView.setTag(vHolder);
        } else {
            vHolder = (ViewHolder) convertView.getTag();
        }
        vHolder.showData(mGiftKeys.get(position), mselect.get(position));
        return convertView;

//        EmotionTextView tv = new EmotionTextView(mContext);
//        Log.i("zh", "emotion:" + mGiftKeys.get(position));
//        tv.setEmotionText(mGiftKeys.get(position));
//        tv.setPadding(mItemPadding, mItemPadding, mItemPadding, mItemPadding);
//        tv.setTag(mGiftKeys.get(position));
//        return tv;
    }

    class ViewHolder {
        private LinearLayout ll_itemgift_main;
        private SimpleDraweeView mGiftImg;
        private ImageView mContinue;
        private TextView mMoney;

//        private TextView mExperience;

        private int size = 0;

        public ViewHolder(View view) {
            mGiftImg = (SimpleDraweeView) view.findViewById(R.id.item_gift_icon);
            mMoney = (TextView) view.findViewById(R.id.item_gift_money);
//            mExperience = (TextView) view.findViewById(R.id.item_gift_experience);
            mContinue = (ImageView) view.findViewById(R.id.item_gift_continue);
            ll_itemgift_main = (LinearLayout)view.findViewById(R.id.ll_itemgift_main);
            size = (int) mContext.getResources().getDimension(R.dimen.item_gift_icon_size);
            mGiftImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        public void showData(Gift gift, boolean isSelect) {
            if (isSelect) {
                ll_itemgift_main.setBackgroundResource(R.drawable.item_gift_selected);
                mContinue.setImageResource(R.drawable.gifticon_on);
                if (Integer.parseInt(gift.getIsred())>0){
                    mContinue.setVisibility(View.VISIBLE);
                }
            } else {
                if (Integer.parseInt(gift.getIsred())>0){
                    ll_itemgift_main.setBackgroundResource(0);
                    mContinue.setVisibility(View.GONE);
                }else {
                    //ll_main_layout.setBackgroundResource(R.drawable.item_gift_selected);
                    //ll_main_layout.setBackgroundResource(0);
                    mContinue.setImageResource(0);
                    ll_itemgift_main.setBackgroundResource(0);
                }
            }

            mMoney.setText(String.valueOf(gift.getPrice()));
//            mExperience.setText(gift.getExp() + "经验值");
            if (!TextUtils.isEmpty(gift.getImageUrl())) {

                int Gift_id = Integer.parseInt(gift.getId());
                if(Gift_id==171){
                    ViewGroup.LayoutParams lp = mGiftImg.getLayoutParams();
                    lp.width = size+60;
                    mGiftImg.setLayoutParams(lp);
                }
                    FrescoUtil.frescoResize(SourceFactory.wrapPathToUri(gift.getImageUrl()), size,
                            size, mGiftImg);

            }

        }
    }
}

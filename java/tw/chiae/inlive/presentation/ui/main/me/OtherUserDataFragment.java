package tw.chiae.inlive.presentation.ui.main.me;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.data.repository.SourceFactory;
import tw.chiae.inlive.domain.BlackList;
import tw.chiae.inlive.presentation.ui.base.BaseFragment;
import tw.chiae.inlive.presentation.ui.main.currency.CurrencyActivity;
import tw.chiae.inlive.presentation.ui.main.webview.SimpleWebViewActivity;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.EventUtil;
import tw.chiae.inlive.util.FrescoUtil;

import java.util.ArrayList;
import java.util.List;


public class OtherUserDataFragment extends BaseFragment implements View.OnClickListener {

    private static final String ARG_UID = "user";
    private UserInfo mUserInfo;
    private View mAge,mLove,mMajor,mID,mExplain,mHome;
    private TextView mAgeTip,mLoveTip,mMajorTip,mIDTip,mExplainTip,mHomeTip;
    private TextView mAgeValue,mLoveValue,mHomeValue,mMajorValue,mIDValue,mExplainValue;

    //使用列表存放，方便遍历
    private List<SimpleDraweeView> topContributeDrawees;
    private LinearLayout mRankLayout;
    private boolean RandMode = false;

    public static OtherUserDataFragment newInstance(UserInfo mUserInfo) {
        OtherUserDataFragment fragment = new OtherUserDataFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_UID, mUserInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        CheckEventSwitch(EventUtil.RankID, new EventCheckCallback() {
            @Override
            public void eventSW(boolean sw) {
                if(sw){
                    RandMode = true;
                }else{
                    RandMode = false;
                }
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_other_user_data;
    }

    @Override
    protected void initViews(View view) {
        mUserInfo = getArguments().getParcelable(ARG_UID);
        mAge=$(view,R.id.other_user_age);
        mLove=$(view,R.id.other_user_love);
        mHome=$(view,R.id.other_user_home);
        mMajor=$(view,R.id.other_user_major);
        mID=$(view,R.id.other_user_id);
        mExplain=$(view,R.id.other_user_explain);

        mAgeTip=$(mAge,R.id.other_user_item_left_tv);
        mLoveTip=$(mLove,R.id.other_user_item_left_tv);
        mHomeTip=$(mHome,R.id.other_user_item_left_tv);
        mMajorTip=$(mMajor,R.id.other_user_item_left_tv);
        mIDTip=$(mID,R.id.other_user_item_left_tv);
        mExplainTip=$(mExplain,R.id.other_user_item_left_tv);

        mAgeTip.setText(getActivity().getResources().getString(R.string.other_data_age));
        mLoveTip.setText(getActivity().getResources().getString(R.string.other_data_love));
        mHomeTip.setText(getActivity().getResources().getString(R.string.other_data_home));
        mMajorTip.setText(getActivity().getResources().getString(R.string.other_data_major));
        mIDTip.setText(getActivity().getResources().getString(R.string.other_data_id));
        mExplainTip.setText(getActivity().getResources().getString(R.string.other_data_explain));

        mAgeValue=$(mAge,R.id.other_user_item_right_tv);
        mLoveValue=$(mLove,R.id.other_user_item_right_tv);
        mHomeValue=$(mHome,R.id.other_user_item_right_tv);
        mMajorValue=$(mMajor,R.id.other_user_item_right_tv);
        mIDValue=$(mID,R.id.other_user_item_right_tv);
        mExplainValue=$(mExplain,R.id.other_user_item_right_tv);
        mRankLayout = $(view,R.id.ll_rank_layout);
        if (mUserInfo.getAge()!=null) {
            mAgeValue.setText(mUserInfo.getAge());
        }
//        0保密,1单身,2热恋中,3已婚,4同性
        if (mUserInfo.getLove()!=null){
            switch (Integer.valueOf(mUserInfo.getLove())){
                case 0:
                    mLoveValue.setText(getString(R.string.popup_feel_secret));
                    break;
                case 1:
                    mLoveValue.setText(getString(R.string.popup_feel_lonely));
                    break;
                case 2:
                    mLoveValue.setText(getString(R.string.popup_feel_love));
                    break;
                case 3:
                    mLoveValue.setText(getString(R.string.popup_feel_married));
                    break;
                case 4:
                    mLoveValue.setText(getString(R.string.popup_feel_gay));
                    break;
            }
        }
        if (mUserInfo.getHome()!=null){
            mHomeValue.setText(mUserInfo.getHome());
        }
        if (mUserInfo.getMajor()!=null && mUserInfo.getMajor().length()!=0){
            mMajorValue.setText(mUserInfo.getMajor());
        } else
            mMajorValue.setText(getString(R.string.popup_major_default));

        if (mUserInfo.getId()!=null){
            mIDValue.setText(mUserInfo.getId());
        }

        if (mUserInfo.getIntro()!=null && mUserInfo.getIntro().length()!=0){
            mExplainValue.setText(mUserInfo.getIntro());
        } else
            mExplainValue.setText(getString(R.string.popup_Explain_default));

        //贡献榜前三的头像
        List<String> topAvatars = mUserInfo.getTopContributeUsers();
        topContributeDrawees=new ArrayList<>();
        SimpleDraweeView draweeTop1 = $(view,R.id.me_coin_top_one);
        SimpleDraweeView draweeTop2 = $(view,R.id.me_coin_top_two);
        SimpleDraweeView draweeTop3 = $(view,R.id.me_coin_top_three);
        topContributeDrawees.add(draweeTop1);
        topContributeDrawees.add(draweeTop2);
        topContributeDrawees.add(draweeTop3);
        if (topAvatars!=null){
            int avatarSize = (int) getResources().getDimension(R.dimen.avatar_size_default);
            //取可显示的头像个数和返回的头像个数的较小值执行遍历
            for (int i=0; i< Math.min(topContributeDrawees.size(), topAvatars.size()); i++){
                FrescoUtil.frescoResize(SourceFactory.wrapPathToUri(topAvatars.get(i)),
                        avatarSize, avatarSize, topContributeDrawees.get(i));
            }
        }
        mRankLayout.setOnClickListener(this);
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    @Override
    public void onClick(View v) {
        Log.i("RayTest","onClick:"+v.getId());
        switch (v.getId()){
            case R.id.ll_rank_layout:
                openCurrencyActivity();
                break;
            default:
                break;
        }
    }

    private void openCurrencyActivity() {
        if(RandMode){
            startActivity(SimpleWebViewActivity.createIntent(getActivity(), Const.RankPageUrl+mUserInfo.getId(),""));
        }else{
            startActivity(CurrencyActivity.createIntent(getActivity(), mUserInfo.getId()));
        }
    }


}

package tw.chiae.inlive.presentation.ui.main.index;

import android.os.Bundle;
import android.view.View;

import tw.chiae.inlive.R;
import tw.chiae.inlive.presentation.ui.base.BaseFragment;

/**
 * Created by 繁华丶落尽 on 2017/1/13.
 */
public class NearbyFragment extends BaseFragment {

    public static NearbyFragment newInstance() {
        return new NearbyFragment();
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_nearby;
    }

    @Override
    protected void initViews(View view) {

    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    @Override
    public void onCancelDialogcheck(Bundle mArgs) {

    }
}

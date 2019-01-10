package tw.chiae.inlive.presentation.ui.login.country;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.gjiazhe.wavesidebar.WaveSideBar;
import tw.chiae.inlive.R;
import tw.chiae.inlive.presentation.ui.base.BaseActivity;
import tw.chiae.inlive.presentation.ui.login.LoginActivity;

import java.util.ArrayList;

public class CountryActivity extends BaseActivity {
    private RecyclerView rvContacts;
    private WaveSideBar sideBar;
    private ContactsAdapter adapter;
    private ArrayList<Contact> contacts = new ArrayList<>();

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, CountryActivity.class);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_country;
    }

    @Override
    protected void findViews(Bundle savedInstanceState) {
        rvContacts = (RecyclerView) findViewById(R.id.rv_contacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        adapter=new ContactsAdapter(contacts, R.layout.item_country);
        rvContacts.setAdapter(adapter);
        sideBar = (WaveSideBar) findViewById(R.id.side_bar);
    }

    @Override
    protected void init() {
        initData();
        adapter.setItemClick(new ContactsAdapter.ItemClick() {
            @Override
            public void onItemClick(int postion) {
                resultBack(contacts.get(postion));
            }
        });

        sideBar.setOnSelectIndexItemListener(new WaveSideBar.OnSelectIndexItemListener() {
            @Override
            public void onSelectIndexItem(String index) {
                for (int i=0; i<contacts.size(); i++) {
                    if (contacts.get(i).getIndex().equals(index)) {
                        ((LinearLayoutManager) rvContacts.getLayoutManager()).scrollToPositionWithOffset(i, 0);
                        return;
                    }
                }
            }
        });
    }


    private void initData() {
        contacts.addAll(Contact.getCountryCode());
    }

    public void resultBack(Contact country){
        Intent intent=new Intent();
        intent.putExtra(LoginActivity.COUNTRY_CODE,country.getCode());
        intent.putExtra(LoginActivity.COUNTRY_NAME,country.getName());
        setResult(LoginActivity.COUNTRY_CODE_REQUEST,intent);
        finish();
    }

    @Override
    public void setCoinData(int currencyItemSum) {

    }
}
